import localStorageUtil from "lib/util/LocalStorageUtil";
import {MemberDto} from "lib/api/dto/MainPageData";
import {qualifiedService} from "./apiSettings";

export interface UserRepository {
    getUser(username: string): Promise<MemberDto | null>

    getUsers(): Promise<MemberDto[]>
}

class LocalStorageUserProfileRepository implements UserRepository {
    private userKeyPrefix = "cgd.user";

    async getUser(username: string): Promise<MemberDto | null> {
        let userStr = localStorage.getItem(`${this.userKeyPrefix}.${username}`)
        if (!userStr) {
            return null
        }
        return JSON.parse(userStr)
    }

    async getUsers(): Promise<MemberDto[]> {
        return localStorageUtil.getAllObjectsByPrefix(`${this.userKeyPrefix}.`);
    }

}

let userRepository = qualifiedService({
    local: new LocalStorageUserProfileRepository(),
    production: new LocalStorageUserProfileRepository(),
})

export default userRepository;
