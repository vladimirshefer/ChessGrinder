import localStorageUtil from "lib/util/LocalStorageUtil";
import {BadgeDto, ListDto, UserBadgeDto, UserDto, UserReputationHistoryRecordDto} from "lib/api/dto/MainPageData";
import {qualifiedService} from "lib/api/repository/apiSettings";
import restApiClient from "lib/api/RestApiClient";
import authService from "lib/auth/AuthService";
import {ParticipantDto, TournamentPageData} from "../dto/TournamentPageData";

export interface UserRepository {
    getUser(username: string): Promise<UserDto | null>

    getUsers(
        limit: number | undefined,
        globalScoreFromDate?: string | undefined,
        globalScoreToDate?: string | undefined
    ): Promise<ListDto<UserDto>>

    getMe(): Promise<UserDto | null>

    getParticipant(userId: string): Promise<ListDto<ParticipantDto>>

    assignReputation(data: UserReputationHistoryRecordDto): Promise<void>

    updateUser(userId: string, user: UserDto): Promise<void>

    deleteUser(userId: string): Promise<void>

    checkPermission(userId: string, targetId: string, targetType: string, permission: string): Promise<boolean>
}

class LocalStorageUserRepository implements UserRepository {
    private userKeyPrefix = "cgd.user";

    async getUser(username: string): Promise<UserDto | null> {
        let userStr = localStorage.getItem(`${this.userKeyPrefix}.${username}`)
        if (!userStr) {
            return null
        }
        let user: UserDto = JSON.parse(userStr);
        user.badges = localStorageUtil.getAllObjectsByPrefix<UserBadgeDto>(`cgd.user__badge.${user.id}.`)
            .map(it => localStorageUtil.getObject<BadgeDto>(`cgd.badge.${it.badgeId}`)!!)
        return user
    }

    async getUsers(): Promise<ListDto<UserDto>> {
        let users: UserDto[] = localStorageUtil.getAllObjectsByPrefix<UserDto>(`${this.userKeyPrefix}.`)
            .map(user => {
                user.badges = localStorageUtil.getAllObjectsByPrefix<UserBadgeDto>(`cgd.user__badge.${user.id}.`)
                    .map(it => localStorageUtil.getObject<BadgeDto>(`cgd.badge.${it.badgeId}`)!!)
                return user
            });
        return {
            values: users
        };
    }

    async getMe(): Promise<UserDto | null> {
        let username = authService.getAuthData()?.username;
        if (username) {
            return await this.getUser(username)
        }
        return null;
    }

    async getParticipant(userId: string): Promise<ListDto<ParticipantDto>> {
        let tournaments = localStorageUtil.getAllObjectsByPrefix<TournamentPageData>("cgd.tournament.");
        let result = tournaments
            .map(tournament => {
                let matchingParticipants: ParticipantDto[] = tournament.participants
                    .filter(participant => !!participant.userId)
                    .map(participant => {
                        let user = localStorageUtil.getObject<UserDto>(`cgd.user.${participant.userId!!}`);
                        return [participant, user] as [ParticipantDto, UserDto | null];
                    })
                    .filter(([, user]) => !!user)
                    .filter(([, user]) => {
                        return user!!.id === userId || user!!.username === userId;
                    })
                    .map(([participant]) => participant);
                let participant = matchingParticipants?.[0];
                return [tournament, participant] as [TournamentPageData, ParticipantDto | undefined];
            })
            .filter(([, participant]) => !!participant)
            .map(([tournament, participant]) => {
                participant!!.tournament = tournament.tournament
                return participant!!
            });
        return {
            count: result.length,
            values: result,
        };
    }

    async assignReputation(data: UserReputationHistoryRecordDto): Promise<void> {
        alert("Unsupported");
    }

    async updateUser(userId: string, user: UserDto): Promise<void> {
        let user1: UserDto | null = await this.getUser(userId);
        localStorageUtil.setObject(`${this.userKeyPrefix}.${userId}`, {...user1, ...user})
    }

    async deleteUser(userId: string): Promise<void> {
        localStorageUtil.removeObject(`${this.userKeyPrefix}.${userId}`);
    }

    async checkPermission(userId: string, targetId: string, targetType: string, permission: string): Promise<boolean> {
        return true;
    }
}

class RestApiUserRepository implements UserRepository {
    async getUser(username: string): Promise<UserDto | null> {
        return restApiClient.get(`/user/${username}`);
    }

    async getUsers(
        limit?: number | undefined,
        globalScoreFromDate?: string | undefined,
        globalScoreToDate?: string | undefined
    ): Promise<ListDto<UserDto>> {
        let queryParams: any = {}
        if (!!limit) queryParams.limit = limit
        if (!!globalScoreFromDate) queryParams.globalScoreFromDate = globalScoreFromDate
        if (!!globalScoreToDate) queryParams.globalScoreToDate = globalScoreToDate
        return restApiClient.get(`/user`, {...queryParams});
    }

    async getMe(): Promise<UserDto | null> {
        return restApiClient.get(`/user/me`);
    }

    async getParticipant(userId: string): Promise<ListDto<ParticipantDto>> {
        return restApiClient.get(`/user/${userId}/participant`);
    }

    async assignReputation(data: UserReputationHistoryRecordDto): Promise<void> {
        return restApiClient.post(`/user/${data.userId}/reputation`, data);
    }

    async updateUser(userId: string, user: UserDto): Promise<void> {
        return restApiClient.patch(`/user/${userId}`, user);
    }

    async deleteUser(userId: string): Promise<void> {
        await restApiClient.delete(`/user/${userId}`);
    }

    async checkPermission(userId: string, targetId: string, targetType: string, permission: string): Promise<boolean> {
        return restApiClient.get(`/user/${userId}/checkPermission`, {
            targetId, targetType, permission
        });
    }
}

let userRepository = qualifiedService({
    local: new LocalStorageUserRepository(),
    production: new RestApiUserRepository(),
})

export default userRepository;
