import { TournamentEventDto, TournamentEventListDto } from "../dto/TournamentEventData";
import localStorageUtil from "lib/util/LocalStorageUtil";
import restApiClient from "lib/api/RestApiClient";
import { qualifiedServiceProxy } from "./apiSettings";
import { requirePresent } from "lib/util/common";
import { AuthData } from "lib/auth/AuthService";

export interface TournamentEventRepository {
    getTournamentEvent(eventId: string): Promise<TournamentEventDto>;
    getTournamentEvents(): Promise<TournamentEventListDto>;
    getTournamentEventsByStatus(status: string): Promise<TournamentEventListDto>;
    createTournamentEvent(name: string, date: string, locationName?: string, locationUrl?: string, roundsNumber?: number, registrationLimit?: number): Promise<TournamentEventDto>;
    updateTournamentEvent(eventId: string, name: string, date: string, locationName?: string, locationUrl?: string, roundsNumber?: number, registrationLimit?: number): Promise<TournamentEventDto>;
    deleteTournamentEvent(eventId: string): Promise<void>;
    startTournamentEvent(eventId: string, numTournaments: number, ratingThreshold: number): Promise<void>;
    finishTournamentEvent(eventId: string): Promise<void>;
    registerParticipant(eventId: string, nickname?: string): Promise<void>;
}

class LocalStorageTournamentEventRepository implements TournamentEventRepository {
    async getTournamentEvent(eventId: string): Promise<TournamentEventDto> {
        const event = localStorageUtil.getObject<TournamentEventDto>(`cgd.event.${eventId}`);
        if (!event) throw new Error(`No such event with id ${eventId}`);
        return event;
    }

    async getTournamentEvents(): Promise<TournamentEventListDto> {
        const events = localStorageUtil.getAllObjectsByPrefix("cgd.event.") as TournamentEventDto[];
        return { events };
    }

    async getTournamentEventsByStatus(status: string): Promise<TournamentEventListDto> {
        const allEvents = await this.getTournamentEvents();
        const filteredEvents = allEvents.events.filter(event => event.status === status);
        return { events: filteredEvents };
    }

    async createTournamentEvent(name: string, date: string, locationName?: string, locationUrl?: string, roundsNumber: number = 5, registrationLimit?: number): Promise<TournamentEventDto> {
        const id = `${Math.trunc(Math.random() * 1000000) + 1000000}`;
        const event: TournamentEventDto = {
            id,
            name,
            date,
            locationName,
            locationUrl,
            status: "PLANNED",
            roundsNumber,
            registrationLimit,
            tournaments: [],
            participants: []
        };
        localStorageUtil.setObject(`cgd.event.${id}`, event);
        return event;
    }

    async updateTournamentEvent(eventId: string, name: string, date: string, locationName?: string, locationUrl?: string, roundsNumber?: number, registrationLimit?: number): Promise<TournamentEventDto> {
        const event = await this.getTournamentEvent(eventId);
        event.name = name;
        event.date = date;
        event.locationName = locationName;
        event.locationUrl = locationUrl;
        if (roundsNumber) event.roundsNumber = roundsNumber;
        event.registrationLimit = registrationLimit;
        localStorageUtil.setObject(`cgd.event.${eventId}`, event);
        return event;
    }

    async deleteTournamentEvent(eventId: string): Promise<void> {
        localStorageUtil.removeObject(`cgd.event.${eventId}`);
    }

    async startTournamentEvent(eventId: string, numTournaments: number, ratingThreshold: number): Promise<void> {
        const event = await this.getTournamentEvent(eventId);
        event.status = "ACTIVE";
        localStorageUtil.setObject(`cgd.event.${eventId}`, event);
    }

    async finishTournamentEvent(eventId: string): Promise<void> {
        const event = await this.getTournamentEvent(eventId);
        event.status = "FINISHED";
        localStorageUtil.setObject(`cgd.event.${eventId}`, event);
    }

    async registerParticipant(eventId: string, nickname?: string): Promise<void> {
        const event = await this.getTournamentEvent(eventId);
        const authData = requirePresent(localStorageUtil.getObject<AuthData>("cgd.auth"), "Not logged in");
        const username = authData?.username;
        if (!authData || !username) throw new Error("Not logged in");
        
        if (event.participants?.find(p => p.userId === username)) {
            throw new Error("You are already registered for this event");
        }

        if (!event.participants) {
            event.participants = [];
        }

        event.participants.push({
            id: username,
            userId: username,
            name: nickname || username,
            buchholz: 0,
            score: 0,
            isMissing: false,
            place: -1
        });

        localStorageUtil.setObject(`cgd.event.${eventId}`, event);
    }
}

class RestApiTournamentEventRepository implements TournamentEventRepository {
    async getTournamentEvent(eventId: string): Promise<TournamentEventDto> {
        return await restApiClient.get<TournamentEventDto>(`/tournament-event/${eventId}`);
    }

    async getTournamentEvents(): Promise<TournamentEventListDto> {
        return await restApiClient.get<TournamentEventListDto>("/tournament-event");
    }

    async getTournamentEventsByStatus(status: string): Promise<TournamentEventListDto> {
        return await restApiClient.get<TournamentEventListDto>(`/tournament-event/status/${status}`);
    }

    async createTournamentEvent(name: string, date: string, locationName?: string, locationUrl?: string, roundsNumber: number = 5, registrationLimit?: number): Promise<TournamentEventDto> {
        const params = new URLSearchParams();
        params.append("name", name);
        params.append("date", date);
        if (locationName) params.append("locationName", locationName);
        if (locationUrl) params.append("locationUrl", locationUrl);
        params.append("roundsNumber", roundsNumber.toString());
        if (registrationLimit) params.append("registrationLimit", registrationLimit.toString());
        
        return await restApiClient.post<TournamentEventDto>(`/tournament-event?${params.toString()}`);
    }

    async updateTournamentEvent(eventId: string, name: string, date: string, locationName?: string, locationUrl?: string, roundsNumber?: number, registrationLimit?: number): Promise<TournamentEventDto> {
        const params = new URLSearchParams();
        params.append("name", name);
        params.append("date", date);
        if (locationName) params.append("locationName", locationName);
        if (locationUrl) params.append("locationUrl", locationUrl);
        if (roundsNumber) params.append("roundsNumber", roundsNumber.toString());
        if (registrationLimit) params.append("registrationLimit", registrationLimit.toString());
        
        return await restApiClient.put<TournamentEventDto>(`/tournament-event/${eventId}?${params.toString()}`);
    }

    async deleteTournamentEvent(eventId: string): Promise<void> {
        await restApiClient.delete(`/tournament-event/${eventId}`);
    }

    async startTournamentEvent(eventId: string, numTournaments: number, ratingThreshold: number): Promise<void> {
        const params = new URLSearchParams();
        params.append("numTournaments", numTournaments.toString());
        params.append("ratingThreshold", ratingThreshold.toString());
        
        await restApiClient.post(`/tournament-event/${eventId}/action/start?${params.toString()}`);
    }

    async finishTournamentEvent(eventId: string): Promise<void> {
        await restApiClient.post(`/tournament-event/${eventId}/action/finish`);
    }

    async registerParticipant(eventId: string, nickname?: string): Promise<void> {
        const params = new URLSearchParams();
        if (nickname) params.append("nickname", nickname);
        
        await restApiClient.post(`/tournament-event/${eventId}/action/participate?${params.toString()}`);
    }
}

const tournamentEventRepository = qualifiedServiceProxy({
    local: new LocalStorageTournamentEventRepository(),
    production: new RestApiTournamentEventRepository(),
});

export default tournamentEventRepository;