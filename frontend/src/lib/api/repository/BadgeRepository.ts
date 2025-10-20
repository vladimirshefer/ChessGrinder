import {qualifiedService} from "lib/api/repository/apiSettings";
import {BadgeDto, ListDto, UserDto} from "lib/api/dto/MainPageData";
import localStorageUtil from "lib/util/LocalStorageUtil";
import restApiClient from "lib/api/RestApiClient";
import {requirePresent} from "lib/util/common";
import userRepository from "lib/api/repository/UserRepository";

export interface BadgeRepository {
    getBadges(): Promise<ListDto<BadgeDto>>;
    getBadge(badgeId: string): Promise<BadgeDto>;
    createBadge(badge: BadgeDto): Promise<void>;
    assignBadge(badgeId: string, userId: string): Promise<void>;
    deleteBadge(badgeId: string): Promise<void>;
    getUsers(badgeId: string): Promise<ListDto<UserDto>>;
}

class LocalStorageBadgeRepository implements BadgeRepository {
    async getBadges(): Promise<ListDto<BadgeDto>> {
        return {
            values: localStorageUtil.getAllObjectsByPrefix<BadgeDto>("cgd.badge.")
        };
    }

    async getBadge(badgeId: string): Promise<BadgeDto> {
        let badge = localStorageUtil.getObject<BadgeDto>(`cgd.badge.${badgeId}`);
        return requirePresent(badge, `No badge with id ${badgeId}`);
    }

    async createBadge(badge: BadgeDto): Promise<void> {
        localStorageUtil.setObject(`cgd.badge.${badge.id}`, badge);
    }

    async assignBadge(badgeId: string, userId: string): Promise<void> {
        localStorageUtil.setObject(`cgd.user__badge.${userId}.${badgeId}`, {
            userId: userId,
            badgeId: badgeId,
        })
    }

    async deleteBadge(badgeId: string): Promise<void> {
        localStorageUtil.forEach("cgd.user_badge.", (k, _v) => {
            if (k.includes(`.${badgeId}`)) {
                localStorageUtil.removeObject(`cgd.user_badge.${k}`);
            }
        })
        localStorageUtil.removeObject(`cgd.badge.${badgeId}`)
    }

    async getUsers(badgeId: string): Promise<ListDto<UserDto>> {
        let usersBadges = localStorageUtil.getAllObjectsByPrefix<{userId: string, badgeId: string}>(`cgd.user_badge`);

        let usersFound = usersBadges
            .filter(it => it.badgeId === badgeId)
            .map(it => it.userId)
            .map(async it => await userRepository.getUser(it));

        let usersResolved = (await Promise.all(usersFound))
            .filter(it => !!it)
            .map(it => it)

        return {values: usersResolved}
    }

}

class RestApiBadgeRepository implements BadgeRepository {
    async getBadges(): Promise<ListDto<BadgeDto>> {
        return restApiClient.get("/badge");
    }

    async getBadge(badgeId: string): Promise<BadgeDto> {
        return restApiClient.get(`/badge/${badgeId}`);
    }

    async createBadge(badge: BadgeDto): Promise<void> {
        return restApiClient.post("/badge", badge);
    }

    async assignBadge(badgeId: string, userId: string): Promise<void> {
        return restApiClient.post(`/user/${userId}/badge/${badgeId}`)
    }

    async deleteBadge(badgeId: string): Promise<void> {
        await restApiClient.delete(`/badge/${badgeId}`)
    }

    async getUsers(badgeId: string): Promise<ListDto<UserDto>> {
        return restApiClient.get(`/badge/${badgeId}/users`);
    }
}

let badgeRepository: BadgeRepository = qualifiedService({
    local: new LocalStorageBadgeRepository(),
    production: new RestApiBadgeRepository(),
})

export default badgeRepository
