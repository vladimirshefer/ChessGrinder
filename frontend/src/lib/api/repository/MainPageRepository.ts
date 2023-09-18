import {MainPageData, MemberDto, TournamentDto} from "lib/api/dto/MainPageData";
import {qualifiedServiceProxy} from "lib/api/repository/apiSettings";
import restApiClient from "lib/api/RestApiClient";
import localStorageUtil from "lib/util/LocalStorageUtil";
import {TournamentPageData} from "../dto/TournamentPageData";

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
        let id = `${Math.trunc(Math.random() * 1000000) + 1000000}`;
        let tournament = {
            date: LocalStorageMainPageRepository.getTodayDate(),
            participants: [],
            rounds: [],
            tournament: {
                id: id,
                name: id,
                date: LocalStorageMainPageRepository.getTodayDate(),
                status: "",
            } as TournamentDto
        } as TournamentPageData;
        localStorageUtil.setObject(`cgd.tournament.${id}`, tournament)
    }

    async createMember(member: MemberDto): Promise<void> {
        localStorageUtil.setObject(`cgd.user.${member.id}`, member)
    }

    private static getTodayDate(): string {
        const date = new Date();
        let day = date.getDate();
        let month = date.getMonth() + 1;
        let year = date.getFullYear();
        return `${year}-${month}-${day}`
    }

    private getTournaments(): TournamentDto[] {
        return (localStorageUtil.getAllObjectsByPrefix("cgd.tournament.") as TournamentPageData[]).map(it => it.tournament);
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
