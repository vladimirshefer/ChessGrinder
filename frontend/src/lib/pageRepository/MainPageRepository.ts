import MainPageData, {MemberDto, TournamentDto} from "lib/api/dto/MainPageData";
import axios, {AxiosRequestConfig} from "axios";
import {PROFILE, REST_API_HOST} from "./apiSettings";

interface MainPageRepository {
    getData: () => Promise<MainPageData>
}

class LocalStorageMainPageRepository implements MainPageRepository {
    async getData(): Promise<MainPageData> {
        return {
            members: JSON.parse(localStorage.getItem("cgd.pages.main.members") || "[]") as MemberDto[],
            tournaments: JSON.parse(localStorage.getItem("cgd.pages.main.tournaments") || "[]") as TournamentDto[]
        };
    }

}

class ProductionMainPageRepository implements MainPageRepository {
    async getData(): Promise<MainPageData> {
        const {data} = await axios.get(
            REST_API_HOST + "/pages/main",
            {
                headers: {
                    "Authorization": "Basic dm92YTpzaGVmZXI="
                }
            } as AxiosRequestConfig
        );
        return data as MainPageData;
    }
}


let mainPageRepository = PROFILE === "local"
    ? new LocalStorageMainPageRepository()
    : new ProductionMainPageRepository();

export default mainPageRepository;
