import {TournamentDto, TournamentListDto} from "lib/api/dto/MainPageData";
import {TournamentPageData} from "lib/api/dto/TournamentPageData";
import localStorageUtil from "lib/util/LocalStorageUtil";
import restApiClient from "lib/api/RestApiClient";
import {qualifiedServiceProxy} from "./apiSettings";
import {AuthData} from "lib/auth/AuthService";

export interface TournamentRepository {
    postTournament: () => Promise<void>
    startTournament: (tournamentId: string) => Promise<void>
    finishTournament: (tournamentId: string) => Promise<void>
    getTournaments: () => Promise<TournamentListDto>
    participate: (tournamentId: string) => Promise<void>
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
                name: id,
                date: LocalStorageTournamentRepository.getTodayDate(),
                status: "PLANNED",
            } as TournamentDto
        } as TournamentPageData;
        localStorageUtil.setObject(`cgd.tournament.${id}`, tournament)
    }

    async finishTournament(tournamentId: string): Promise<void> {
        let tournament = localStorageUtil.getObject<TournamentPageData>(`cgd.tournament.${tournamentId}`);
        if (!tournament) throw new Error(`No such tournament with id ${tournamentId}`)
        tournament.tournament.status = "FINISHED"
        localStorageUtil.setObject(`cgd.tournament.${tournamentId}`, tournament)
    }

    async getTournaments(): Promise<TournamentListDto> {
        let tournamentsLocal = localStorageUtil.getAllObjectsByPrefix("cgd.tournament.") as TournamentPageData[];
        let tournaments = tournamentsLocal.map(it => it.tournament);
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

    async participate(tournamentId: string): Promise<void> {
        let tournament = localStorageUtil.getObject<TournamentPageData>(`cgd.tournament.${tournamentId}`);
        if (!tournament) throw new Error(`No such tournament with id ${tournamentId}`)
        let participants = tournament.participants;
        let authData = localStorageUtil.getObject<AuthData>("cgd.auth");
        if (!authData || !authData?.username) throw new Error("Not logged in");
        if (!!participants.find(p => p.userId === authData?.username)) {
            throw new Error("You are already participating")
        }
        participants.push({
            userId: authData.username,
            name: authData.username,
            buchholz: 0,
            score: 0,
        })
        localStorageUtil.setObject(`cgd.tournament.${tournamentId}`, tournament)
    }

    public static getTodayDate(): string {
        const date = new Date();
        let day = date.getDate();
        let month = date.getMonth() + 1;
        let year = date.getFullYear();
        return `${year}-${month}-${day}`
    }
}

class RestApiTournamentRepository implements TournamentRepository {
    async postTournament() {
        await restApiClient.post("/tournament");
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

    async participate(tournamentId: string): Promise<void> {
        await restApiClient.post(`/tournament/${tournamentId}/action/participate`)
    }
}

let tournamentRepository = qualifiedServiceProxy({
    local: new LocalStorageTournamentRepository(),
    production: new RestApiTournamentRepository(),
})

export default tournamentRepository;

