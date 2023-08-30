import {TournamentPageData} from "lib/api/dto/TournamentPageData";
import axios, {AxiosRequestConfig} from "axios";
import {PROFILE, REST_API_HOST} from "./apiSettings";

export interface TournamentPageRepository{
    getData: (tournamentId: string) => Promise<TournamentPageData>
    postParticipant(tournamentId: string, participant: string): Promise<void>
}

class LocalStorageTournamentPageRepository implements TournamentPageRepository {
    async getData(tournamentId: string): Promise<TournamentPageData> {
        return JSON.parse(localStorage.getItem(`cgd.pages.tournament.${tournamentId}`) || "null")
    }

    async postParticipant(tournamentId: string, participant: string) {
        let data = await this.getData(tournamentId) || {
            id: tournamentId,
            name: tournamentId,
            participants: [],
            round: [],
        };
        data.participants.push({
            name: participant,
            userId: participant,
            score: 0,
            buchholz: 0,
        })
        localStorage.setItem(`cgd.pages.tournament.${tournamentId}`, JSON.stringify(data))
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
        const {data} = await axios.post(
            REST_API_HOST + `/tournament/${tournamentId}/participant`,
            participant,
            {
                headers: {
                    "Authorization": "Basic dm92YTpzaGVmZXI="
                }
            } as AxiosRequestConfig,
        )
    }
}

let tournamentPageRepository: TournamentPageRepository = PROFILE === "local"
    ? new LocalStorageTournamentPageRepository()
    : new ProductionTournamentPageRepository();

export default tournamentPageRepository;
