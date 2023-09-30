import {ParticipantDto, TournamentPageData} from "lib/api/dto/TournamentPageData";
import {qualifiedService} from "./apiSettings";
import restApiClient from "lib/api/RestApiClient";
import localStorageUtil from "lib/util/LocalStorageUtil";

export interface ParticipantRepository {
    postParticipant(tournamentId: string, participant: ParticipantDto): Promise<void>
    deleteParticipant(tournamentId: string, userId: string): Promise<void>
}

class LocalStorageParticipantRepository implements ParticipantRepository {
    async postParticipant(tournamentId: string, participant: ParticipantDto) {
        let tournament =  localStorageUtil.getObject<TournamentPageData>(`cgd.tournament.${tournamentId}`) || null;
        if (!tournament) throw new Error(`No tournament with id ${tournamentId}`)
        tournament.participants = tournament.participants || []
        tournament.participants.push(participant)
        localStorage.setItem(`cgd.tournament.${tournamentId}`, JSON.stringify(tournament))
    }

    async deleteParticipant(tournamentId: string, userId: string) {
        let tournament =  localStorageUtil.getObject<TournamentPageData>(`cgd.tournament.${tournamentId}`) || null;
        if (!tournament) throw new Error(`No tournament with id ${tournamentId}`)
        tournament.participants = tournament.participants || []
        tournament.participants.filter(it => it.userId !== userId)
        localStorage.setItem(`cgd.tournament.${tournamentId}`, JSON.stringify(tournament))
    }
}

class RestApiParticipantRepository implements ParticipantRepository {
    async postParticipant(tournamentId: string, participant: ParticipantDto) {
        await restApiClient.post(`/tournament/${tournamentId}/participant`, participant,)
    }

    async deleteParticipant(tournamentId: string, userId: string) {
        await restApiClient.delete(`/tournament/${tournamentId}/participant/${userId}`)
    }

}

let participantRepository: ParticipantRepository = qualifiedService({
    local: new LocalStorageParticipantRepository(),
    production: new RestApiParticipantRepository()
})

export default participantRepository;
