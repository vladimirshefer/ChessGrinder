import localStorageUtil from "lib/util/LocalStorageUtil";
import {BadgeDto, ListDto, MemberDto, UserBadgeDto} from "lib/api/dto/MainPageData";
import {qualifiedService} from "lib/api/repository/apiSettings";
import restApiClient from "lib/api/RestApiClient";
import authService from "lib/auth/AuthService";

export interface UserRepository {
    getUser(username: string): Promise<MemberDto | null>

    getUsers(): Promise<ListDto<MemberDto>>

    getMe(): Promise<MemberDto | null>
}

class LocalStorageUserRepository implements UserRepository {
    private userKeyPrefix = "cgd.user";

    async getUser(username: string): Promise<MemberDto | null> {
        let userStr = localStorage.getItem(`${this.userKeyPrefix}.${username}`)
        if (!userStr) {
            return null
        }
        let user: MemberDto = JSON.parse(userStr);
        user.badges = localStorageUtil.getAllObjectsByPrefix<UserBadgeDto>(`cgd.user__badge.${user.id}.`)
            .map(it => localStorageUtil.getObject<BadgeDto>(`cgd.badge.${it.badgeId}`)!!)
        return user
    }

    async getUsers(): Promise<ListDto<MemberDto>> {
        let users: MemberDto[] = localStorageUtil.getAllObjectsByPrefix<MemberDto>(`${this.userKeyPrefix}.`)
            .map(user => {
                user.badges = localStorageUtil.getAllObjectsByPrefix<UserBadgeDto>(`cgd.user__badge.${user.id}.`)
                    .map(it => localStorageUtil.getObject<BadgeDto>(`cgd.badge.${it.badgeId}`)!!)
                return user
            });
        return {
            values: users

        };
    }

    async getMe(): Promise<MemberDto | null> {
        let username = authService.getAuthData()?.username;
        if (username) {
            return await this.getUser(username)
        }
        return null;
    }

}

class RestApiUserRepository implements UserRepository {
    async getUser(username: string): Promise<MemberDto | null> {
        return restApiClient.get(`/user/${username}`);
    }

    async getUsers(): Promise<ListDto<MemberDto>> {
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
