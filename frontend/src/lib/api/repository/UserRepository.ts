import localStorageUtil from "lib/util/LocalStorageUtil";
import {BadgeDto, ListDto, MemberDto, UserBadgeDto, UserHistoryRecordDto} from "lib/api/dto/MainPageData";
import {qualifiedService} from "lib/api/repository/apiSettings";
import restApiClient from "lib/api/RestApiClient";
import authService from "lib/auth/AuthService";
import {ParticipantDto, TournamentPageData} from "../dto/TournamentPageData";

export interface UserRepository {
    getUser(username: string): Promise<MemberDto | null>

    getUsers(): Promise<ListDto<MemberDto>>

    getMe(): Promise<MemberDto | null>

    getHistory(username: string): Promise<ListDto<UserHistoryRecordDto>>
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

    async getHistory(username: string): Promise<ListDto<UserHistoryRecordDto>> {
        let tournaments = localStorageUtil.getAllObjectsByPrefix<TournamentPageData>("cgd.tournament.");
        let result = tournaments
            .map(tournament => {
                let matchingParticipants: ParticipantDto[] = tournament.participants
                    .filter(participant => !!participant.userId)
                    .map(participant => {
                        let user = localStorageUtil.getObject<MemberDto>(`cgd.user.${participant.userId!!}`);
                        return [participant, user] as [ParticipantDto, MemberDto | null];
                    })
                    .filter(([, user]) => !!user)
                    .filter(([, user]) => {
                        return user!!.username === username || user!!.id === username;
                    })
                    .map(([participant]) => participant);
                let participant = matchingParticipants?.[0];
                return [tournament, participant] as [TournamentPageData, ParticipantDto | undefined];
            })
            .filter(([, participant]) => !!participant)
            .map(([tournament, participant]) => {
                let participantDtos = tournament.participants
                    .sort((a, b) => -(a.score !== b.score ? a.score - b.score : a.buchholz - b.buchholz))
                return {
                    tournament: tournament.tournament,
                    participant: participant,
                    place: participantDtos.indexOf(participant!!) + 1,
                } as UserHistoryRecordDto
            });
        return {
            count: result.length,
            values: result,
        };
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

    async getHistory(username: string): Promise<ListDto<UserHistoryRecordDto>> {
        return restApiClient.get(`/user/${username}/history`);
    }
}

let userRepository = qualifiedService({
    local: new LocalStorageUserRepository(),
    production: new RestApiUserRepository(),
})

export default userRepository;
