import {ParticipantDto, RoundDto, TournamentPageData} from "lib/api/dto/TournamentPageData";
import {qualifiedService} from "lib/api/repository/apiSettings";
import restApiClient from "lib/api/RestApiClient";
import localStorageUtil from "lib/util/LocalStorageUtil";

export interface TournamentPageRepository {
    getData(tournamentId: string): Promise<TournamentPageData | null>
}

class LocalStorageTournamentPageRepository implements TournamentPageRepository {
    async getData(tournamentId: string): Promise<TournamentPageData | null> {
        let tournament = localStorageUtil.getObject<TournamentPageData>(`cgd.tournament.${tournamentId}`)
        if (tournament) {
            let {pointsMap, buchholzMap} = this.calculateResults(tournament)
            tournament.participants.forEach((participant: ParticipantDto) => {
                participant.score = pointsMap.get(participant.id!!) || 0
                participant.buchholz = buchholzMap.get(participant.id!!) || 0
            })
            // Save recalculated values back.
            localStorageUtil.setObject(`cgd.tournament.${tournamentId}`, tournament)
            return tournament
        }
        return tournament || null
    }

    calculateResults(tournamentData: TournamentPageData) {
        let pointsMap /*userId -> points*/ = new Map<string, number>()
        let buchholzMap/*userId -> buchholz points*/ = new Map<string, number>()
        let buchholzListMap/*userId -> arrayOf[enemy user points]*/ = new Map<string, number[]>()
        let enemiesMap/*userId -> arrayOf[enemy user points]*/ = new Map<string, Set<string>>()
        let allMatches = tournamentData.rounds
            .filter(round => round.isFinished)
            .flatMap((round: RoundDto) => round.matches);
        allMatches.forEach(match => {
            let whiteId = match.white?.id;
            let blackId = match.black?.id;
            if (match.result === "WHITE_WIN") {
                whiteId && this.computeIfAbsent(pointsMap, whiteId, 1, i => i + 1)
                blackId && this.computeIfAbsent(pointsMap, blackId, 0, i => i)
            } else
            if (match.result === "BLACK_WIN") {
                blackId && this.computeIfAbsent(pointsMap, blackId, 1, i => i + 1)
                whiteId && this.computeIfAbsent(pointsMap, whiteId, 0, i => i)
            } else
            if (match.result === "DRAW") {
                whiteId && this.computeIfAbsent(pointsMap, whiteId, 0.5, i => i + 0.5)
                blackId && this.computeIfAbsent(pointsMap, blackId, 0.5, i => i + 0.5)
            } else
            if (match.result === "BUY") {
                whiteId && this.computeIfAbsent(pointsMap, whiteId, 1, i => i + 1)
                blackId && this.computeIfAbsent(pointsMap, blackId, 1, i => i + 1)
            } else {
                whiteId && this.computeIfAbsent(pointsMap, whiteId, 0, i => i)
                blackId && this.computeIfAbsent(pointsMap, blackId, 0, i => i)
            }

            whiteId && blackId && this.computeIfAbsent(enemiesMap, whiteId, new Set<string>([blackId]), enemyPoints => enemyPoints.add(blackId!!))
            whiteId && blackId && this.computeIfAbsent(enemiesMap, blackId, new Set<string>([whiteId]), enemyPoints => enemyPoints.add(whiteId!!))
        })
        enemiesMap.forEach((enemyIds, userId) => {
            buchholzListMap.set(userId, Array.from(enemyIds).map(enemyId => {
                let points = pointsMap.get(enemyId)
                if (points === undefined) {
                    throw new Error(`User ${userId} has no points in map. This should not happen.`)
                }
                return points
            }))
        })
        buchholzListMap.forEach((enemyPoints, userId) => {
            buchholzMap.set(userId, enemyPoints.reduce((a, b) => a + b))
        })
        return {pointsMap, enemiesMap, buchholzMap, buchholzListMap}
    }

    private computeIfAbsent<T>(map: Map<string, T>, key: string, defaultValue: T, valueMapper: (v: T) => T) {
        let previousValue = map.get(key);
        if (previousValue === undefined) {
            map.set(key, defaultValue)
        } else {
            map.set(key, valueMapper(previousValue))
        }
    }

}

class ProductionTournamentPageRepository implements TournamentPageRepository {
    async getData(tournamentId: string): Promise<TournamentPageData | null> {
        return await restApiClient.get<TournamentPageData>(`/pages/tournament/${tournamentId}`)
            .catch((e) => Promise.resolve(null));
    }
}

let tournamentPageRepository: TournamentPageRepository = qualifiedService({
    local: new LocalStorageTournamentPageRepository(),
    production: new ProductionTournamentPageRepository()
})

export default tournamentPageRepository;
