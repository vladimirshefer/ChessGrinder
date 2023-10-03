import {qualifiedService} from "lib/api/repository/apiSettings";
import {BadgeDto, ListDto} from "lib/api/dto/MainPageData";
import localStorageUtil from "lib/util/LocalStorageUtil";
import restApiClient from "lib/api/RestApiClient";
import {randomString} from "../../util/Random";

export interface BadgeRepository {
    getBadges(): Promise<ListDto<BadgeDto>>;

    createBadge(badge: BadgeDto): Promise<void>;
}

class LocalStorageBadgeRepository implements BadgeRepository {
    async getBadges(): Promise<ListDto<BadgeDto>> {
        return {
            values: localStorageUtil.getAllObjectsByPrefix<BadgeDto>("cgd.badge.")
        };
    }

    async createBadge(badge: BadgeDto): Promise<void> {
        let id = randomString(15);
        localStorageUtil.setObject(`cgd.badge.${id}`, badge);
    }
}

class RestApiBadgeRepository implements BadgeRepository {
    async getBadges(): Promise<ListDto<BadgeDto>> {
        return restApiClient.get("/badge");
    }

    async createBadge(badge: BadgeDto): Promise<void> {
        return restApiClient.post("/badge", badge);
    }
}

let badgeRepository: BadgeRepository = qualifiedService({
    local: new LocalStorageBadgeRepository(),
    production: new RestApiBadgeRepository(),
})

export default badgeRepository
