import {TournamentDto, TournamentListDto} from "lib/api/dto/MainPageData";
import {TournamentPageData} from "lib/api/dto/TournamentPageData";
import localStorageUtil from "lib/util/LocalStorageUtil";
import restApiClient from "lib/api/RestApiClient";
import {qualifiedServiceProxy} from "./apiSettings";
import {AuthData} from "lib/auth/AuthService";
import {compareBy, reverse} from "lib/util/Comparator";
import {requirePresent} from "lib/util/common";

export interface TournamentRepository {
    postTournament: () => Promise<TournamentDto>
    startTournament: (tournamentId: string) => Promise<void>
    finishTournament: (tournamentId: string) => Promise<void>
    getTournaments: () => Promise<TournamentListDto>
    participate: (tournamentId: string, nickname: string) => Promise<void>
    deleteTournament: (tournamentId: string) => Promise<void>
    updateTournament:(tournament: TournamentDto) => Promise<void>
}

class LocalStorageTournamentRepository implements TournamentRepository {
    async postTournament() {
        let id = `${Math.trunc(Math.random() * 1000000) + 1000000}`;
        let tournament = {
            date: LocalStorageTournamentRepository.getTodayDate(),
            participants: [],
            rounds: [],
            tournament: {
                id: id,
                date: LocalStorageTournamentRepository.getTodayDate(),
                status: "PLANNED",
            } as TournamentDto
        } as TournamentPageData;
        localStorageUtil.setObject(`cgd.tournament.${id}`, tournament)
        return tournament.tournament
    }

    async finishTournament(tournamentId: string): Promise<void> {
        let tournament = localStorageUtil.getObject<TournamentPageData>(`cgd.tournament.${tournamentId}`);
        if (!tournament) throw new Error(`No such tournament with id ${tournamentId}`)
        tournament.tournament.status = "FINISHED"
        localStorageUtil.setObject(`cgd.tournament.${tournamentId}`, tournament)
    }

    async getTournaments(): Promise<TournamentListDto> {
        let tournamentsLocal = localStorageUtil.getAllObjectsByPrefix("cgd.tournament.") as TournamentPageData[];
        let tournaments = tournamentsLocal.map(it => it.tournament).sort(reverse(compareBy(it => it.date)));
        return {
            tournaments: tournaments
        } as TournamentListDto
    }

    async startTournament(tournamentId: string): Promise<void> {
        let tournament = localStorageUtil.getObject<TournamentPageData>(`cgd.tournament.${tournamentId}`);
        if (!tournament) throw new Error(`No such tournament with id ${tournamentId}`)
        tournament.tournament.status = "ACTIVE"
        localStorageUtil.setObject(`cgd.tournament.${tournamentId}`, tournament)
    }

    async participate(tournamentId: string, nickname: string): Promise<void> {
        let tournament = requirePresent(localStorageUtil.getObject<TournamentPageData>(`cgd.tournament.${tournamentId}`), `No such tournament with id ${tournamentId}`);
        let participants = tournament.participants;
        let authData = requirePresent(localStorageUtil.getObject<AuthData>("cgd.auth"), "Not logged in");
        let username = authData?.username;
        if (!authData || !username) throw new Error("Not logged in");
        if (!!participants.find(p => p.userId === username)) {
            throw new Error("You are already participating")
        }
        participants.push({
            id: authData.username,
            userId: authData.username,
            name: nickname || authData.username,
            buchholz: 0,
            score: 0,
        })
        localStorageUtil.setObject(`cgd.tournament.${tournamentId}`, tournament)
    }

    async deleteTournament(tournamentId: string): Promise<void> {
        localStorageUtil.removeObject(`cgd.tournament.${tournamentId}`);
    }

    public static getTodayDate(): string {
        const date = new Date();
        let day = date.getDate();
        let month = date.getMonth() + 1;
        let year = date.getFullYear();
        return `${("00000" + year).slice(-4)}-${("000" + month).slice(-2)}-${("000" + day).slice(-2)}`
    }

    async updateTournament(tournament: TournamentDto): Promise<void> {
        let tournamentData = requirePresent(localStorageUtil.getObject<TournamentPageData>(`cgd.tournament.${tournament.id}`), `No such tournament with id ${tournament.id}`);
        tournamentData.tournament = tournament
        localStorageUtil.setObject(`cgd.tournament.${tournament.id}`, tournamentData)
    }
}

class RestApiTournamentRepository implements TournamentRepository {
    async postTournament() {
        return await restApiClient.post<TournamentDto>("/tournament");
    }

    async finishTournament(tournamentId: string): Promise<void> {
        await restApiClient.get(`/tournament/${tournamentId}/action/finish`);
    }

    async startTournament(tournamentId: string): Promise<void> {
        await restApiClient.get(`/tournament/${tournamentId}/action/start`);
    }

    async getTournaments(): Promise<TournamentListDto> {
        return await restApiClient.get<TournamentListDto>("/tournament");
    }

    async deleteTournament(tournamentId: string): Promise<void> {
        await restApiClient.delete(`/tournament/${tournamentId}`);
    }

    async participate(tournamentId: string, nickname: string): Promise<void> {
        await restApiClient.post(`/tournament/${tournamentId}/action/participate?nickname=${nickname}`)
    }

    async updateTournament(tournament: TournamentDto): Promise<void> {
        await restApiClient.put(`/tournament/${tournament.id}`, tournament)
    }
}

let tournamentRepository = qualifiedServiceProxy({
    local: new LocalStorageTournamentRepository(),
    production: new RestApiTournamentRepository(),
})

export default tournamentRepository;

