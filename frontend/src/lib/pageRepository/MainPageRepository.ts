import {MainPageData, MemberDto, TournamentDto} from "lib/api/dto/MainPageData";
import axios, {AxiosRequestConfig} from "axios";
import {GLOBAL_SETTINGS} from "./apiSettings";

interface MainPageRepository {
    getData: () => Promise<MainPageData>
    postTournament: () => Promise<void>
}

class LocalStorageMainPageRepository implements MainPageRepository {
    async getData(): Promise<MainPageData> {
        let members = JSON.parse(localStorage.getItem("cgd.pages.main.members") || "[]") as MemberDto[];
        let tournaments = LocalStorageMainPageRepository.getTournaments();
        return {
            members: members,
            tournaments: tournaments
        };
    }

    async postTournament() {
        let tournaments = LocalStorageMainPageRepository.getTournaments()
        let id = `${Math.trunc(Math.random() * 1000000) + 1000000}`;
        tournaments.push({
            id: id,
            name: id,
            date: LocalStorageMainPageRepository.getTodayDate(),
        })
        localStorage.setItem("cgd.pages.main.tournaments", JSON.stringify(tournaments))
    }

    private static getTodayDate(): string {
        const date = new Date();
        let day = date.getDate();
        let month = date.getMonth() + 1;
        let year = date.getFullYear();
        return `${year}.${month}.${day}`
    }

    private static getTournaments() {
        return JSON.parse(localStorage.getItem("cgd.pages.main.tournaments") || "[]") as TournamentDto[];
    }

}

class ProductionMainPageRepository implements MainPageRepository {
    async getData(): Promise<MainPageData> {
        const {data} = await axios.get(
            GLOBAL_SETTINGS.restApiHost + "/pages/main",
            {
                headers: {
                    "Authorization": "Basic dm92YTpzaGVmZXI="
                }
            } as AxiosRequestConfig
        );
        return data as MainPageData;
    }

    async postTournament() {
        await axios.post(
            GLOBAL_SETTINGS.restApiHost + "/tournament",
            {},
            {
                headers: {
                    "Authorization": "Basic dm92YTpzaGVmZXI="
                }
            } as AxiosRequestConfig
        );
    }
}


let mainPageRepository: MainPageRepository = GLOBAL_SETTINGS.getProfile() === "local"
    ? new LocalStorageMainPageRepository()
    : new ProductionMainPageRepository();

export default mainPageRepository;
