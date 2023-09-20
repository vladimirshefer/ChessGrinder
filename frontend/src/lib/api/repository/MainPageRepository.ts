import {MainPageData, MemberDto, TournamentDto} from "lib/api/dto/MainPageData";
import {qualifiedServiceProxy} from "lib/api/repository/apiSettings";
import restApiClient from "lib/api/RestApiClient";
import localStorageUtil from "lib/util/LocalStorageUtil";
import {TournamentPageData} from "../dto/TournamentPageData";

interface MainPageRepository {
    getData: () => Promise<MainPageData>
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

    async createMember(member: MemberDto): Promise<void> {
        localStorageUtil.setObject(`cgd.user.${member.id}`, member)
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
