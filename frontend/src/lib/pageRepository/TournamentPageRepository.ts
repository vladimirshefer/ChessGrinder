import {MatchDto, MatchResult, ParticipantDto, TournamentPageData} from "lib/api/dto/TournamentPageData";
import axios, {AxiosRequestConfig} from "axios";
import {GLOBAL_SETTINGS} from "./apiSettings";

export interface TournamentPageRepository {
    getData: (tournamentId: string) => Promise<TournamentPageData>

    postParticipant(tournamentId: string, participant: string): Promise<void>

    postRound(tournamentId: string): Promise<void>

    deleteRound(tournamentId: string, roundNumber: number): Promise<void>

    postMatchResult(tournamentId: string, roundId: number, matchId: string, result: string): Promise<void>
}

class LocalStorageTournamentPageRepository implements TournamentPageRepository {
    async getData(tournamentId: string): Promise<TournamentPageData> {
        return JSON.parse(localStorage.getItem(`cgd.pages.tournament.${tournamentId}`) || "null")
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
        let participants = tournament.participants || []
        participants = [...participants]
        this.shuffleArray(participants)
        let matches: MatchDto[] = []
        let matchesAmount = Math.trunc(participants.length / 2)
        for (let i = 0; i < matchesAmount; i++) {
            matches.push(this.createMatch(participants[i * 2], participants[i * 2 + 1]))
        }
        tournament.rounds.push({
            state: "STARTED",
            matches: matches
        })
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
            white: {name: white.name} as ParticipantDto,
            black: {name: black.name} as ParticipantDto,
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
}

class ProductionTournamentPageRepository implements TournamentPageRepository {
    async getData(tournamentId: string): Promise<TournamentPageData> {
        const {data} = await axios.get(
            GLOBAL_SETTINGS.restApiHost + `/pages/tournament/${tournamentId}`,
            {
                headers: {
                    "Authorization": "Basic dm92YTpzaGVmZXI="
                }
            } as AxiosRequestConfig,
        );
        return data as TournamentPageData;
    }

    async postParticipant(tournamentId: string, participant: string) {
        await axios.post(
            GLOBAL_SETTINGS.restApiHost + `/tournament/${tournamentId}/participant`,
            participant,
            {
                headers: {
                    "Authorization": "Basic dm92YTpzaGVmZXI="
                }
            } as AxiosRequestConfig,
        )
    }

    async postRound(tournamentId: string): Promise<void> {
        await axios.post(
            GLOBAL_SETTINGS.restApiHost + `/tournament/${tournamentId}/round`,
            {},
            {
                headers: {
                    "Authorization": "Basic dm92YTpzaGVmZXI="
                }
            } as AxiosRequestConfig,
        )
    }

    async postMatchResult(tournamentId: string, roundId: number, matchId: string, result: string): Promise<void> {
        await axios.post(
            GLOBAL_SETTINGS.restApiHost + `/tournament/${tournamentId}/round/${roundId}/match/${matchId}`,
            {},
            {
                headers: {
                    "Authorization": "Basic dm92YTpzaGVmZXI="
                }
            } as AxiosRequestConfig,
        )
    }

    async deleteRound(tournamentId: string, roundNumber: number): Promise<void> {
        await axios.delete(
            GLOBAL_SETTINGS.restApiHost + `/tournament/${tournamentId}/round/${roundNumber}`,
            {
                headers: {
                    "Authorization": "Basic dm92YTpzaGVmZXI="
                }
            } as AxiosRequestConfig,
        )
    }
}

let tournamentPageRepository: TournamentPageRepository = GLOBAL_SETTINGS.getProfile() === "local"
    ? new LocalStorageTournamentPageRepository()
    : new ProductionTournamentPageRepository();

export default tournamentPageRepository;
