import {MainPageData, MemberDto, TournamentDto} from "lib/api/dto/MainPageData";
import {GLOBAL_SETTINGS, qualifiedServiceProxy} from "lib/api/repository/apiSettings";
import restApiClient from "lib/api/RestApiClient";
import localStorageUtil from "lib/util/LocalStorageUtil";

interface MainPageRepository {
    getData: () => Promise<MainPageData>
    postTournament: () => Promise<void>
    createMember: (member: MemberDto) => Promise<void>
}

class LocalStorageMainPageRepository implements MainPageRepository {
    async getData(): Promise<MainPageData> {
        let members = this.getMembers();
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
        localStorageUtil.setObject(`cgd.user.${member.id}`, member)
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

    private getMembers(): MemberDto[] {
        return localStorageUtil.getAllObjectsByPrefix("cgd.user.");
    }

}

class ProductionMainPageRepository implements MainPageRepository {
    async getData(): Promise<MainPageData> {
        return await restApiClient.get<MainPageData>("/pages/main");
    }

    async postTournament() {
        await restApiClient.post("/tournament");
    }

    async createMember(member: MemberDto): Promise<void> {
        await restApiClient.post("/members", member);
    }
}

let localStorageMainPageRepository = new LocalStorageMainPageRepository()
let productionMainPageRepository = new ProductionMainPageRepository()

let mainPageRepository = qualifiedServiceProxy({
    local: localStorageMainPageRepository,
    production: productionMainPageRepository,
})

export default mainPageRepository;
