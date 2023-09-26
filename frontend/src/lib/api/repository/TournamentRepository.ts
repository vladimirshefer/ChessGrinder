import {TournamentDto, TournamentListDto} from "lib/api/dto/MainPageData";
import {TournamentPageData} from "lib/api/dto/TournamentPageData";
import localStorageUtil from "lib/util/LocalStorageUtil";
import restApiClient from "lib/api/RestApiClient";
import {qualifiedServiceProxy} from "./apiSettings";

export interface TournamentRepository {
    postTournament: () => Promise<void>
    startTournament: (tournamentId: string) => Promise<void>
    finishTournament: (tournamentId: string) => Promise<void>
    getTournaments: () => Promise<TournamentListDto>
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
}

let tournamentRepository = qualifiedServiceProxy({
    local: new LocalStorageTournamentRepository(),
    production: new RestApiTournamentRepository(),
})

export default tournamentRepository;

