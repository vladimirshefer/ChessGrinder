import {MatchDto, ParticipantDto, TournamentPageData} from "lib/api/dto/TournamentPageData";
import axios, {AxiosRequestConfig} from "axios";
import {PROFILE, REST_API_HOST} from "./apiSettings";

export interface TournamentPageRepository {
    getData: (tournamentId: string) => Promise<TournamentPageData>

    postParticipant(tournamentId: string, participant: string): Promise<void>

    postRound(tournamentId: string): Promise<void>
}

class LocalStorageTournamentPageRepository implements TournamentPageRepository {
    async getData(tournamentId: string): Promise<TournamentPageData> {
        return JSON.parse(localStorage.getItem(`cgd.pages.tournament.${tournamentId}`) || "null")
    }

    async postParticipant(tournamentId: string, participant: string) {
        let tournament = await this.getData(tournamentId) || {
            id: tournamentId,
            name: tournamentId,
            participants: [],
            rounds: [],
        };
        tournament.participants = tournament.participants || []
        tournament.participants.push({
            name: participant,
            userId: participant,
            score: 0,
            buchholz: 0,
        })
        localStorage.setItem(`cgd.pages.tournament.${tournamentId}`, JSON.stringify(tournament))
    }

    async postRound(tournamentId: string): Promise<void> {
        let tournament = await this.getData(tournamentId) || {
            id: tournamentId,
            name: tournamentId,
            participants: [],
            rounds: [],
        };
        tournament.rounds = tournament.rounds || []
        tournament.participants = tournament.participants || [];
        let matches: MatchDto[] = []
        let matchesAmount = tournament.participants.length / 2;
        for (let i = 0; i < matchesAmount; i++) {
            matches.push({
                white: {name: tournament.participants[i*2].name} as ParticipantDto,
                black: {name: tournament.participants[i*2 + 1].name} as ParticipantDto,
                result: undefined
            })
        }
        tournament.rounds.push({
            state: "STARTED",
            matches: matches
        })
        localStorage.setItem(`cgd.pages.tournament.${tournamentId}`, JSON.stringify(tournament))
    }
}

class ProductionTournamentPageRepository implements TournamentPageRepository {
    async getData(tournamentId: string): Promise<TournamentPageData> {
        const {data} = await axios.get(
            REST_API_HOST + `/pages/tournament/${tournamentId}`,
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
            REST_API_HOST + `/tournament/${tournamentId}/participant`,
            participant,
            {
                headers: {
                    "Authorization": "Basic dm92YTpzaGVmZXI="
                }
            } as AxiosRequestConfig,
        )
    }

    async postRound(tournamentId: string): Promise<void> {
    }
}

let tournamentPageRepository: TournamentPageRepository = PROFILE === "local"
    ? new LocalStorageTournamentPageRepository()
    : new ProductionTournamentPageRepository();

export default tournamentPageRepository;
