import {MemberDto} from "lib/api/dto/MainPageData";
import {qualifiedServiceProxy} from "lib/api/repository/apiSettings";
import restApiClient from "lib/api/RestApiClient";
import localStorageUtil from "lib/util/LocalStorageUtil";

interface MainPageRepository {
    createMember: (member: MemberDto) => Promise<void>
}

class LocalStorageMainPageRepository implements MainPageRepository {

    async createMember(member: MemberDto): Promise<void> {
        localStorageUtil.setObject(`cgd.user.${member.id}`, member)
    }

}

class ProductionMainPageRepository implements MainPageRepository {
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
