import {ParticipantDto, TournamentPageData} from "lib/api/dto/TournamentPageData";
import {qualifiedService} from "./apiSettings";
import restApiClient from "lib/api/RestApiClient";
import localStorageUtil from "lib/util/LocalStorageUtil";
import {requirePresent} from "lib/util/common";
import {ListDto, UserDto} from "lib/api/dto/MainPageData";
import authService from "lib/auth/AuthService";

export interface ParticipantRepository {
    postParticipant(tournamentId: string, participant: ParticipantDto): Promise<void>
    deleteParticipant(tournamentId: string, participantId: string): Promise<void>
    getParticipant(tournamentId: string, participantId: string): Promise<ParticipantDto>
    getMe(tournamentId: string): Promise<ParticipantDto>
    updateParticipant(tournamentId: string, participant: Partial<ParticipantDto>): Promise<void>
    missParticipant(tournamentId: string, participantId: string): Promise<void>;
    unmissParticipant(tournamentId: string, participantId: string): Promise<void>;
    getWinner(tournamentId: string): Promise<ListDto<ParticipantDto>>;
}

class LocalStorageParticipantRepository implements ParticipantRepository {
    async postParticipant(tournamentId: string, participant: ParticipantDto) {
        let tournament =  localStorageUtil.getObject<TournamentPageData>(`cgd.tournament.${tournamentId}`) || null;
        if (!tournament) throw new Error(`No tournament with id ${tournamentId}`)
        tournament.participants = tournament.participants || []
        tournament.participants.push(participant)
        let user = localStorageUtil.getObject<UserDto>(`cgd.user.${participant.userId}`);
        participant.userFullName = user?.name
        localStorage.setItem(`cgd.tournament.${tournamentId}`, JSON.stringify(tournament))
    }

    async deleteParticipant(tournamentId: string, participantId: string) {
        let tournament = requirePresent(localStorageUtil.getObject<TournamentPageData>(`cgd.tournament.${tournamentId}`), `No tournament with id ${tournamentId}`)
        tournament.participants = tournament.participants || []
        tournament.participants = tournament.participants.filter(it => it.id !== participantId)
        localStorage.setItem(`cgd.tournament.${tournamentId}`, JSON.stringify(tournament))
    }

    async getParticipant(tournamentId: string, participantId: string): Promise<ParticipantDto> {
        let tournament = requirePresent(localStorageUtil.getObject<TournamentPageData>(`cgd.tournament.${tournamentId}`), `No tournament with id ${tournamentId}`)
        let participant = tournament.participants.find(it => it.id === participantId)
        if (!participant) throw new Error(`No participant with id ${participantId} in tournament ${tournamentId}`)
        return participant
    }

    async updateParticipant(tournamentId: string, participant: Partial<ParticipantDto>): Promise<void> {
        let participantId: string = participant.id!!;
        let oldParticipant = await this.getParticipant(tournamentId, participantId);
        let newParticipant: ParticipantDto = {...oldParticipant, ...participant}
        await this.deleteParticipant(tournamentId, participantId)
        await this.postParticipant(tournamentId, newParticipant)
    }

    async missParticipant(tournamentId: string, participantId: string): Promise<void> {
        let participantDto = await this.getParticipant(tournamentId, participantId);
        await this.deleteParticipant(tournamentId, participantId)
        participantDto.isMissing = true;
        await this.postParticipant(tournamentId, participantDto)
    }

    async unmissParticipant(tournamentId: string, participantId: string): Promise<void> {
        let participantDto = await this.getParticipant(tournamentId, participantId);
        await this.deleteParticipant(tournamentId, participantId)
        participantDto.isMissing = false;
        await this.postParticipant(tournamentId, participantDto)
    }

    async getMe(tournamentId: string): Promise<ParticipantDto> {
        let username = authService.getAuthData()?.username;
        let tournament = requirePresent(localStorageUtil.getObject<TournamentPageData>(`cgd.tournament.${tournamentId}`), `No tournament with id ${tournamentId}`)
        let participant = tournament.participants.find(it => it.userId === username)
        if (!participant) throw new Error(`No participant with userId ${username} in tournament ${tournamentId}`)
        return participant
    }

    async getWinner(tournamentId: string): Promise<ListDto<ParticipantDto>> {
        let tournament = requirePresent(localStorageUtil.getObject<TournamentPageData>(`cgd.tournament.${tournamentId}`), `No tournament with id ${tournamentId}`)
        let participant = tournament.participants[0]
        if (!participant) throw new Error(`No participant in tournament ${tournamentId}`)
        return {values: [participant]}
    }
}

class RestApiParticipantRepository implements ParticipantRepository {
    async postParticipant(tournamentId: string, participant: ParticipantDto) {
        await restApiClient.post(`/tournament/${tournamentId}/participant`, participant,)
    }

    async deleteParticipant(tournamentId: string, participantId: string) {
        await restApiClient.delete(`/tournament/${tournamentId}/participant/${participantId}`)
    }

    async getParticipant(tournamentId: string, participantId: string): Promise<ParticipantDto> {
        return await restApiClient.get<ParticipantDto>(`/tournament/${tournamentId}/participant/${participantId}`)
    }

    async getMe(tournamentId: string): Promise<ParticipantDto> {
        return await restApiClient.get<ParticipantDto>(`/tournament/${tournamentId}/participant/me`);
    }

    async updateParticipant(tournamentId: string, participant: ParticipantDto): Promise<void> {
        await restApiClient.put(`/tournament/${tournamentId}/participant/${participant.id}`, participant)
    }

    async missParticipant(tournamentId: string, participantId: string): Promise<void> {
        await restApiClient.post(`/tournament/${tournamentId}/participant/${participantId}/action/miss`)
    }

    async unmissParticipant(tournamentId: string, participantId: string): Promise<void> {
        await restApiClient.post(`/tournament/${tournamentId}/participant/${participantId}/action/unmiss`)
    }

    async getWinner(tournamentId: string): Promise<ListDto<ParticipantDto>> {
        return await restApiClient.get<ListDto<ParticipantDto>>(`/tournament/${tournamentId}/participant/winner`)
    }
}

let participantRepository: ParticipantRepository = qualifiedService({
    local: new LocalStorageParticipantRepository(),
    production: new RestApiParticipantRepository()
})

export default participantRepository;
