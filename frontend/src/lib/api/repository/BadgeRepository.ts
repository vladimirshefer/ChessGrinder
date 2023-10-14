import {qualifiedService} from "lib/api/repository/apiSettings";
import {BadgeDto, ListDto} from "lib/api/dto/MainPageData";
import localStorageUtil from "lib/util/LocalStorageUtil";
import restApiClient from "lib/api/RestApiClient";
import {requirePresent} from "lib/util/common";

export interface BadgeRepository {
    getBadges(): Promise<ListDto<BadgeDto>>;
    getBadge(badgeId: string): Promise<BadgeDto>;
    createBadge(badge: BadgeDto): Promise<void>;
    assignBadge(badgeId: string, userId: string): Promise<void>;
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
}

let badgeRepository: BadgeRepository = qualifiedService({
    local: new LocalStorageBadgeRepository(),
    production: new RestApiBadgeRepository(),
})

export default badgeRepository
