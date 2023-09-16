import {MatchDto, MatchResult, ParticipantDto, RoundDto, TournamentPageData} from "lib/api/dto/TournamentPageData";
import {qualifiedService} from "./apiSettings";
import restApiClient from "lib/api/RestApiClient";

export interface TournamentPageRepository {
    getData(tournamentId: string): Promise<TournamentPageData>

    postParticipant(tournamentId: string, participant: string): Promise<void>

    postRound(tournamentId: string): Promise<void>

    deleteRound(tournamentId: string, roundNumber: number): Promise<void>

    drawRound(tournamentId: string, roundNumber: number): Promise<void>

    finishRound(tournamentId: string, roundNumber: number): Promise<void>

    postMatchResult(tournamentId: string, roundId: number, matchId: string, result: string): Promise<void>

    reopenRound(tournamentId: string, roundNumber: number): Promise<void>;
}

class LocalStorageTournamentPageRepository implements TournamentPageRepository {
    async getData(tournamentId: string): Promise<TournamentPageData> {
        let tournament = JSON.parse(localStorage.getItem(`cgd.pages.tournament.${tournamentId}`) || "null");
        if (tournament) {
            let {pointsMap, buchholzMap} = this.calculateResults(tournament)
            tournament.participants.forEach((participant: ParticipantDto) => {
                participant.score = pointsMap.get(participant.userId) || 0
                participant.buchholz = buchholzMap.get(participant.userId) || 0
            })
        }
        return tournament
    }

    calculateResults(tournamentData: TournamentPageData) {
        let pointsMap /*userId -> points*/ = new Map<string, number>()
        let buchholzMap/*userId -> buchholz points*/ = new Map<string, number>()
        let buchholzListMap/*userId -> arrayOf[enemy user points]*/ = new Map<string, number[]>()
        let enemiesMap/*userId -> arrayOf[enemy user points]*/ = new Map<string, Set<string>>()
        let allMatches = tournamentData.rounds
            .filter(round => round.state === "FINISHED")
            .flatMap((round: RoundDto) => round.matches);
        allMatches.forEach(match => {
            let whiteUserId = match.white.userId;
            let blackUserId = match.black.userId;
            if (match.result === "WHITE_WIN") {
                this.computeIfAbsent(pointsMap, whiteUserId, 1, i => i + 1)
            }
            if (match.result === "BLACK_WIN") {
                this.computeIfAbsent(pointsMap, blackUserId, 1, i => i + 1)
            }
            if (match.result === "DRAW") {
                this.computeIfAbsent(pointsMap, whiteUserId, 0.5, i => i + 0.5)
                this.computeIfAbsent(pointsMap, blackUserId, 0.5, i => i + 0.5)
            }

            this.computeIfAbsent(enemiesMap, whiteUserId, new Set<string>([blackUserId]), enemyPoints => enemyPoints.add(blackUserId))
            this.computeIfAbsent(enemiesMap, blackUserId, new Set<string>([whiteUserId]), enemyPoints => enemyPoints.add(whiteUserId))
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

    async postParticipant(tournamentId: string, participant: string) {
        let tournament = await this.getData(tournamentId) || this.getEmptyTournament(tournamentId);
        tournament.participants = tournament.participants || []
        tournament.participants.push({
            name: participant,
            userId: participant,
            score: 0,
            buchholz: 0,
        })
        this.saveTournament(tournamentId, tournament)
    }

    async postRound(tournamentId: string): Promise<void> {
        let tournament = await this.getData(tournamentId)
        if (!tournament) {
            throw new Error(`No tournament with id ${tournamentId}`)
        }
        tournament.rounds.push({
            state: "STARTED",
            matches: []
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
        if (round.state !== "STARTED") {
            throw new Error(`Round ${roundNumber} in tournament ${tournamentId} is already finished.`)
        }
        let participants = tournament.participants || []
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
        let tournament: TournamentPageData = await this.getData(tournamentId);
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
        localStorage.setItem(`cgd.pages.tournament.${tournamentId}`, JSON.stringify(tournament))
    }

    private createMatch(white: ParticipantDto, black: ParticipantDto) {
        return {
            id: `${white.name}_${black.name}`,
            white: {userId: white.userId, name: white.name} as ParticipantDto,
            black: {userId: black.userId, name: black.name} as ParticipantDto,
            result: undefined
        };
    }

    private getEmptyTournament(tournamentId: string): TournamentPageData {
        return {
            id: tournamentId,
            name: tournamentId,
            participants: [],
            rounds: [],
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
        let tournament = await this.getData(tournamentId)
        if (!tournament) {
            throw new Error(`No tournament with id ${tournamentId}`)
        }
        if (!tournament.rounds || tournament.rounds.length < roundNumber) {
            throw new Error(`No round ${roundNumber} in tournament ${tournamentId}`)
        }
        let round = tournament.rounds[roundNumber - 1];
        if (round.state !== "STARTED") {
            throw new Error(`Round ${roundNumber} in tournament ${tournamentId} is already finished.`)
        }
        round.state = "FINISHED"
        await this.saveTournament(tournamentId, tournament)
    }

    async reopenRound(tournamentId: string, roundNumber: number): Promise<void> {
        let tournament = await this.getData(tournamentId)
        if (!tournament) {
            throw new Error(`No tournament with id ${tournamentId}`)
        }
        if (!tournament.rounds || tournament.rounds.length < roundNumber) {
            throw new Error(`No round ${roundNumber} in tournament ${tournamentId}`)
        }
        let round = tournament.rounds[roundNumber - 1];
        if (round.state !== "FINISHED") {
            throw new Error(`Round ${roundNumber} in tournament ${tournamentId} is running.`)
        }
        round.state = "STARTED"
        await this.saveTournament(tournamentId, tournament)
    }
}

class ProductionTournamentPageRepository implements TournamentPageRepository {
    async getData(tournamentId: string): Promise<TournamentPageData> {
        return await restApiClient.get<TournamentPageData>(`/pages/tournament/${tournamentId}`);
    }

    async postParticipant(tournamentId: string, participant: string) {
        await restApiClient.post(`/tournament/${tournamentId}/participant`, participant,)
    }

    async postRound(tournamentId: string): Promise<void> {
        await restApiClient.post(`/tournament/${tournamentId}/round`, {})
    }

    async postMatchResult(tournamentId: string, roundId: number, matchId: string, result: string): Promise<void> {
        await restApiClient.post(`/tournament/${tournamentId}/round/${roundId}/match/${matchId}`, {})
    }

    async deleteRound(tournamentId: string, roundNumber: number): Promise<void> {
        await restApiClient.delete(`/tournament/${tournamentId}/round/${roundNumber}`)
    }

    async drawRound(tournamentId: string, roundNumber: number): Promise<void> {
        await restApiClient.post(`/tournament/${tournamentId}/round/${roundNumber}/draw`)
    }

    async finishRound(tournamentId: string, roundNumber: number): Promise<void> {
        await restApiClient.post(`/tournament/${tournamentId}/round/${roundNumber}/finish`);
    }

    async reopenRound(tournamentId: string, roundNumber: number): Promise<void> {
        await restApiClient.post(`/tournament/${tournamentId}/round/${roundNumber}/reopen`);
    }
}

let tournamentPageRepository: TournamentPageRepository = qualifiedService({
    local: new LocalStorageTournamentPageRepository(),
    production: new ProductionTournamentPageRepository()
})

export default tournamentPageRepository;
