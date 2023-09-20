import localStorageUtil from "lib/util/LocalStorageUtil";
import {MemberDto} from "lib/api/dto/MainPageData";
import {qualifiedService} from "./apiSettings";
import restApiClient from "../RestApiClient";

export interface UserRepository {
    getUser(username: string): Promise<MemberDto | null>

    getUsers(): Promise<MemberDto[]>

    getMe(): Promise<MemberDto | null>
}

class LocalStorageUserRepository implements UserRepository {
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

    async getMe(): Promise<MemberDto | null> {
        return null;
    }

}

class RestApiUserRepository implements UserRepository {
    async getUser(username: string): Promise<MemberDto | null> {
        return restApiClient.get(`/user/${username}`);
    }

    async getUsers(): Promise<MemberDto[]> {
        return restApiClient.get(`/user`);
    }

    async getMe(): Promise<MemberDto | null> {
        return restApiClient.get(`/user/me`);
    }
}

let userRepository = qualifiedService({
    local: new LocalStorageUserRepository(),
    production: new RestApiUserRepository(),
})

export default userRepository;
