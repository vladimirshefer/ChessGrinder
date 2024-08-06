import {MatchDto, MatchResult, ParticipantDto, TournamentPageData} from "lib/api/dto/TournamentPageData";
import {qualifiedService} from "./apiSettings";
import restApiClient from "lib/api/RestApiClient";
import {requirePresent} from "lib/util/common";
import tournamentPageRepository from "./TournamentPageRepository";

export interface RoundRepository {
    postRound(tournamentId: string): Promise<void>

    deleteRound(tournamentId: string, roundNumber: number): Promise<void>

    runPairing(tournamentId: string, roundNumber: number): Promise<void>

    finishRound(tournamentId: string, roundNumber: number): Promise<void>

    postMatchResult(tournamentId: string, roundId: number, matchId: string, result: string): Promise<void>

    reopenRound(tournamentId: string, roundNumber: number): Promise<void>;
}

class LocalStorageRoundRepository implements RoundRepository {

    async postRound(tournamentId: string): Promise<void> {
        let tournament = await tournamentPageRepository.getData(tournamentId)
        if (!tournament) {
            throw new Error(`No tournament with id ${tournamentId}`)
        }
        tournament.rounds.push({
            isFinished: false,
            matches: [],
        })
        this.saveTournament(tournamentId, tournament)
    }

    async runPairing(tournamentId: string, roundNumber: number): Promise<void> {
        let tournament = await tournamentPageRepository.getData(tournamentId)
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
        let participants: (ParticipantDto | null)[] = tournament.participants || []
        participants = [...participants]
        this.shuffleArray(participants)
        if (participants.length % 2 === 1) participants.push(null)
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
        let tournament = await tournamentPageRepository.getData(tournamentId);
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

    private createMatch(white: ParticipantDto | null, black: ParticipantDto | null): MatchDto {
        return {
            id: `${white?.name}_${black?.name}`,
            white: white,
            black: black,
            result: undefined
        };
    }

    async deleteRound(tournamentId: string, roundNumber: number): Promise<void> {
        let tournament = await tournamentPageRepository.getData(tournamentId)
        if (!tournament) {
            throw new Error(`No tournament with id ${tournamentId}`)
        }
        tournament.rounds = tournament.rounds.filter((_, i) => i !== roundNumber - 1)
        this.saveTournament(tournamentId, tournament)
    }

    async finishRound(tournamentId: string, roundNumber: number): Promise<void> {
        let tournament = requirePresent(await tournamentPageRepository.getData(tournamentId), `No tournament with id ${tournamentId}`)
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
        let tournament = requirePresent(await tournamentPageRepository.getData(tournamentId), `No tournament with id ${tournamentId}`)
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

class ProductionRoundRepository implements RoundRepository {
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

    async runPairing(tournamentId: string, roundNumber: number): Promise<void> {
        await restApiClient.post(`/tournament/${tournamentId}/round/${roundNumber}/action/matchup`)
    }

    async finishRound(tournamentId: string, roundNumber: number): Promise<void> {
        await restApiClient.post(`/tournament/${tournamentId}/round/${roundNumber}/action/finish`);
    }

    async reopenRound(tournamentId: string, roundNumber: number): Promise<void> {
        await restApiClient.post(`/tournament/${tournamentId}/round/${roundNumber}/action/reopen`);
    }
}

let roundRepository: RoundRepository = qualifiedService({
    local: new LocalStorageRoundRepository(),
    production: new ProductionRoundRepository()
})

export default roundRepository;
