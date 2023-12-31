import {MatchDto, MatchResult, ParticipantDto, RoundDto, TournamentPageData} from "lib/api/dto/TournamentPageData";
import {qualifiedService} from "./apiSettings";
import restApiClient from "lib/api/RestApiClient";
import localStorageUtil from "lib/util/LocalStorageUtil";
import {requirePresent} from "lib/util/common";

export interface TournamentPageRepository {
    getData(tournamentId: string): Promise<TournamentPageData | null>

    postRound(tournamentId: string): Promise<void>

    deleteRound(tournamentId: string, roundNumber: number): Promise<void>

    drawRound(tournamentId: string, roundNumber: number): Promise<void>

    finishRound(tournamentId: string, roundNumber: number): Promise<void>

    postMatchResult(tournamentId: string, roundId: number, matchId: string, result: string): Promise<void>

    reopenRound(tournamentId: string, roundNumber: number): Promise<void>;
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

    async postRound(tournamentId: string): Promise<void> {
        let tournament = await this.getData(tournamentId)
        if (!tournament) {
            throw new Error(`No tournament with id ${tournamentId}`)
        }
        tournament.rounds.push({
            isFinished: false,
            matches: [],
        })
        this.saveTournament(tournamentId, tournament)
    }

    async drawRound(tournamentId: string, roundNumber: number): Promise<void> {
        let tournament = await this.getData(tournamentId)
        if (!tournament) {
            throw new Error(`No tournament with id ${tournamentId}`)
        }
        if (!tournament.rounds || tournament.rounds.length < roundNumber) {
            throw new Error(`No round ${roundNumber} in tournament ${tournamentId}`)
        }
        let round = tournament.rounds[roundNumber - 1];
        if (round.isFinished) {
            throw new Error(`Round ${roundNumber} in tournament ${tournamentId} is already finished.`)
        }
        let participants: ParticipantDto[] = tournament.participants || []
        participants = [...participants]
        this.shuffleArray(participants)
        let matches: MatchDto[] = []
        let matchesAmount = Math.trunc(participants.length / 2)
        for (let i = 0; i < matchesAmount; i++) {
            matches.push(this.createMatch(participants[i * 2], participants[i * 2 + 1]))
        }
        round.matches = matches
        this.saveTournament(tournamentId, tournament)
    }

    /**
     * Shuffles array in place.
     * @param {Array} array items An array containing the items.
     */
    private shuffleArray(array: any[]) {
        for (let i = array.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [array[i], array[j]] = [array[j], array[i]];
        }
        return array;
    }

    async postMatchResult(tournamentId: string, roundId: number, matchId: string, result: string): Promise<void> {
        let tournament = await this.getData(tournamentId);
        if (!tournament) {
            throw new Error(`No tournament with id ${tournamentId}`);
        }
        let round = tournament.rounds[roundId - 1];
        if (!round) {
            throw new Error(`Tournament ${tournamentId} has no round ${roundId}`)
        }
        let match = round.matches.find(it => it.id === matchId);
        if (!match) {
            throw new Error(`No match with id ${matchId} in tournament ${tournamentId}`)
        }
        match.result = result as MatchResult
        this.saveTournament(tournamentId, tournament)
    }

    private saveTournament(tournamentId: string, tournament: TournamentPageData) {
        localStorage.setItem(`cgd.tournament.${tournamentId}`, JSON.stringify(tournament))
    }

    private createMatch(white: ParticipantDto, black: ParticipantDto): MatchDto {
        return {
            id: `${white.name}_${black.name}`,
            white: white,
            black: black,
            result: undefined
        };
    }

    async deleteRound(tournamentId: string, roundNumber: number): Promise<void> {
        let tournament = await this.getData(tournamentId)
        if (!tournament) {
            throw new Error(`No tournament with id ${tournamentId}`)
        }
        tournament.rounds = tournament.rounds.filter((_, i) => i !== roundNumber - 1)
        this.saveTournament(tournamentId, tournament)
    }

    async finishRound(tournamentId: string, roundNumber: number): Promise<void> {
        let tournament = requirePresent(await this.getData(tournamentId), `No tournament with id ${tournamentId}`)
        if (!tournament.rounds || tournament.rounds.length < roundNumber) {
            throw new Error(`No round ${roundNumber} in tournament ${tournamentId}`)
        }
        let round = tournament.rounds[roundNumber - 1];
        if (round.isFinished) {
            throw new Error(`Round ${roundNumber} in tournament ${tournamentId} is already finished.`)
        }
        round.isFinished = true
        await this.saveTournament(tournamentId, tournament)
    }

    async reopenRound(tournamentId: string, roundNumber: number): Promise<void> {
        let tournament = requirePresent(await this.getData(tournamentId), `No tournament with id ${tournamentId}`)
        if (!tournament.rounds || tournament.rounds.length < roundNumber) {
            throw new Error(`No round ${roundNumber} in tournament ${tournamentId}`)
        }
        let round = tournament.rounds[roundNumber - 1];
        if (!round.isFinished) {
            throw new Error(`Round ${roundNumber} in tournament ${tournamentId} is running.`)
        }
        round.isFinished = false
        await this.saveTournament(tournamentId, tournament)
    }
}

class ProductionTournamentPageRepository implements TournamentPageRepository {
    async getData(tournamentId: string): Promise<TournamentPageData | null> {
        return await restApiClient.get<TournamentPageData>(`/pages/tournament/${tournamentId}`)
            .catch((e) => Promise.resolve(null));
    }

    async postRound(tournamentId: string): Promise<void> {
        await restApiClient.post(`/tournament/${tournamentId}/round`, {})
    }

    async postMatchResult(tournamentId: string, roundId: number, matchId: string, result: string): Promise<void> {
        await restApiClient.post(`/tournament/${tournamentId}/round/${roundId}/match/${matchId}`, {
            matchResult: result
        })
    }

    async deleteRound(tournamentId: string, roundNumber: number): Promise<void> {
        await restApiClient.delete(`/tournament/${tournamentId}/round/${roundNumber}`)
    }

    async drawRound(tournamentId: string, roundNumber: number): Promise<void> {
        await restApiClient.post(`/tournament/${tournamentId}/round/${roundNumber}/action/matchup`)
    }

    async finishRound(tournamentId: string, roundNumber: number): Promise<void> {
        await restApiClient.post(`/tournament/${tournamentId}/round/${roundNumber}/action/finish`);
    }

    async reopenRound(tournamentId: string, roundNumber: number): Promise<void> {
        await restApiClient.post(`/tournament/${tournamentId}/round/${roundNumber}/action/reopen`);
    }
}

let tournamentPageRepository: TournamentPageRepository = qualifiedService({
    local: new LocalStorageTournamentPageRepository(),
    production: new ProductionTournamentPageRepository()
})

export default tournamentPageRepository;
