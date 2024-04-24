import localStorageUtil from "lib/util/LocalStorageUtil";
import {
    BadgeDto,
    ListDto,
    UserDto,
    UserBadgeDto,
    UserHistoryRecordDto,
    UserReputationHistoryRecordDto
} from "lib/api/dto/MainPageData";
import {qualifiedService} from "lib/api/repository/apiSettings";
import restApiClient from "lib/api/RestApiClient";
import authService from "lib/auth/AuthService";
import {ParticipantDto, TournamentPageData} from "../dto/TournamentPageData";

export interface UserRepository {
    getUser(username: string): Promise<UserDto | null>

    getUsers(): Promise<ListDto<UserDto>>

    getMe(): Promise<UserDto | null>

    getHistory(userId: string): Promise<ListDto<UserHistoryRecordDto>>

    getTotalPoints(userId: string, startSeason: Date, endSeason: Date): Promise<number>

    assignReputation(data: UserReputationHistoryRecordDto): Promise<void>

    updateUser(userId: string, user: UserDto): Promise<void>

    deleteUser(userId: string): Promise<void>
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

    async getHistory(userId: string): Promise<ListDto<UserHistoryRecordDto>> {
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
                return {
                    tournament: tournament.tournament,
                    participant: participant,
                } as UserHistoryRecordDto
            });
        return {
            count: result.length,
            values: result,
        };
    }

    async getTotalPoints(userId: string, startSeason: Date, endSeason: Date): Promise<number> {
        alert("Unsupported");
        return 0;
    }

    async assignReputation(data: UserReputationHistoryRecordDto): Promise<void> {
        alert("Unsupported");
    }

    async updateUser(userId: string, user: UserDto): Promise<void> {
        alert("Unsupported");
    }

    async deleteUser(userId: string): Promise<void> {
        localStorageUtil.removeObject(`${this.userKeyPrefix}.${userId}`);
    }
}

class RestApiUserRepository implements UserRepository {
    async getUser(username: string): Promise<UserDto | null> {
        return restApiClient.get(`/user/${username}`);
    }

    async getUsers(): Promise<ListDto<UserDto>> {
        return restApiClient.get(`/user`);
    }

    async getMe(): Promise<UserDto | null> {
        return restApiClient.get(`/user/me`);
    }

    async getHistory(userId: string): Promise<ListDto<UserHistoryRecordDto>> {
        return restApiClient.get(`/user/${userId}/history`);
    }

    async getTotalPoints(userId: string, startSeason: Date, endSeason: Date): Promise<number> {
        //TODO по возможности, сделать эту структуру пустой (чтобы не было ...=null)
        const queryParams: Record<string, string | null> = {
            startSeasonDate: startSeason ? startSeason.toISOString().substring(0, 10) : null,
            endSeasonDate: endSeason ? endSeason.toISOString().substring(0, 10) : null
        };
        console.log("!!!!");
        return restApiClient.get(`/user/${userId}/totalPoints`, queryParams as Record<string, string>);
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
}

let userRepository = qualifiedService({
    local: new LocalStorageUserRepository(),
    production: new RestApiUserRepository(),
})

export default userRepository;
