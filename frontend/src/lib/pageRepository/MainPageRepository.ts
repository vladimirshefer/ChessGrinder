import {MainPageData, MemberDto, TournamentDto} from "lib/api/dto/MainPageData";
import axios, {AxiosRequestConfig} from "axios";
import {GLOBAL_SETTINGS} from "./apiSettings";

interface MainPageRepository {
    getData: () => Promise<MainPageData>
    postTournament: () => Promise<void>
    createMember: (member: MemberDto) => Promise<void>
}

class LocalStorageMainPageRepository implements MainPageRepository {
    async getData(): Promise<MainPageData> {
        let members = this.getMembersAsdf();
        let tournaments = this.getTournaments();
        return {
            members: members,
            tournaments: tournaments
        };
    }

    async postTournament() {
        let tournaments = this.getTournaments()
        let id = `${Math.trunc(Math.random() * 1000000) + 1000000}`;
        tournaments.push({
            id: id,
            name: id,
            date: LocalStorageMainPageRepository.getTodayDate(),
        })
        localStorage.setItem("cgd.pages.main.tournaments", JSON.stringify(tournaments))
    }

    async createMember(member: MemberDto): Promise<void> {
        let members = this.getMembersAsdf();
        members.push(member)
        await this.saveMembers(members);
    }

    private static getTodayDate(): string {
        const date = new Date();
        let day = date.getDate();
        let month = date.getMonth() + 1;
        let year = date.getFullYear();
        return `${year}.${month}.${day}`
    }

    private getTournaments(): TournamentDto[] {
        return JSON.parse(localStorage.getItem("cgd.pages.main.tournaments") || "[]") as TournamentDto[];
    }

    private getMembersAsdf(): MemberDto[] {
        return JSON.parse(localStorage.getItem("cgd.pages.main.members") || "[]");
    }

    private saveMembers(members: MemberDto[]) {
        localStorage.setItem("cgd.pages.main.members", JSON.stringify(members))
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

    async createMember(member: MemberDto): Promise<void> {
        await axios.post(
            GLOBAL_SETTINGS.restApiHost + "/members",
            member,
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
