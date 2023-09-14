import localStorageUtil from "../util/LocalStorageUtil";
import {MemberDto} from "../api/dto/MainPageData";

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

let userRepository = new LocalStorageUserProfileRepository()

export default userRepository;
