import {TournamentPageData} from "lib/api/dto/TournamentPageData";
import axios, {AxiosRequestConfig} from "axios";
import {PROFILE, REST_API_HOST} from "./apiSettings";

export interface TournamentPageRepository{
    getData: (tournamentId: string) => Promise<TournamentPageData>
}

class LocalStorageTournamentPageRepository implements TournamentPageRepository {
    async getData(tournamentId: string): Promise<TournamentPageData> {
        return JSON.parse(localStorage.getItem(`cgd.pages.tournament.${tournamentId}`) || "null")
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
            } as AxiosRequestConfig
        );
        return data as TournamentPageData;
    }
}

let tournamentPageRepository = PROFILE === "local"
    ? new LocalStorageTournamentPageRepository()
    : new ProductionTournamentPageRepository();

export default tournamentPageRepository;
