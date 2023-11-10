import {ParticipantDto, TournamentPageData} from "lib/api/dto/TournamentPageData";
import {qualifiedService} from "./apiSettings";
import restApiClient from "lib/api/RestApiClient";
import localStorageUtil from "lib/util/LocalStorageUtil";
import {requirePresent} from "lib/util/common";
import {UserDto} from "lib/api/dto/MainPageData";

export interface ParticipantRepository {
    postParticipant(tournamentId: string, participant: ParticipantDto): Promise<void>
    deleteParticipant(tournamentId: string, participantId: string): Promise<void>
    getParticipant(tournamentId: string, participantId: string): Promise<ParticipantDto>
    updateParticipant(tournamentId: string, participant: ParticipantDto): Promise<void>
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

    async updateParticipant(tournamentId: string, participant: ParticipantDto): Promise<void> {
        throw new Error("Not supported: Update participant")
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

    async updateParticipant(tournamentId: string, participant: ParticipantDto): Promise<void> {
        throw await restApiClient.put(`/tournament/${tournamentId}/participant/${participant.id}`, participant)
    }
}

let participantRepository: ParticipantRepository = qualifiedService({
    local: new LocalStorageParticipantRepository(),
    production: new RestApiParticipantRepository()
})

export default participantRepository;
