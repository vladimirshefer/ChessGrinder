import { TournamentEventDto, TournamentEventScheduleDto, TournamentEventScheduleListDto, TournamentEventScheduleStatus } from "../dto/TournamentEventData";
import localStorageUtil from "lib/util/LocalStorageUtil";
import restApiClient from "lib/api/RestApiClient";
import { qualifiedServiceProxy } from "./apiSettings";

export interface TournamentEventScheduleRepository {
    getSchedule(scheduleId: string): Promise<TournamentEventScheduleDto>;
    getAllSchedules(): Promise<TournamentEventScheduleListDto>;
    getSchedulesByStatus(status: TournamentEventScheduleStatus): Promise<TournamentEventScheduleListDto>;
    createSchedule(name: string, dayOfWeek: number, time: string): Promise<TournamentEventScheduleDto>;
    updateSchedule(scheduleId: string, name: string, dayOfWeek: number, time: string): Promise<TournamentEventScheduleDto>;
    updateScheduleStatus(scheduleId: string, status: TournamentEventScheduleStatus): Promise<TournamentEventScheduleDto>;
    createEventFromSchedule(scheduleId: string, date: string, locationName?: string, locationUrl?: string, roundsNumber?: number, registrationLimit?: number): Promise<TournamentEventDto>;
    deleteSchedule(scheduleId: string): Promise<void>;
}

class LocalStorageTournamentEventScheduleRepository implements TournamentEventScheduleRepository {
    async getSchedule(scheduleId: string): Promise<TournamentEventScheduleDto> {
        const schedule = localStorageUtil.getObject<TournamentEventScheduleDto>(`cgd.schedule.${scheduleId}`);
        if (!schedule) throw new Error(`No such schedule with id ${scheduleId}`);
        return schedule;
    }

    async getAllSchedules(): Promise<TournamentEventScheduleListDto> {
        const schedules = localStorageUtil.getAllObjectsByPrefix("cgd.schedule.") as TournamentEventScheduleDto[];
        return { schedules };
    }

    async getSchedulesByStatus(status: TournamentEventScheduleStatus): Promise<TournamentEventScheduleListDto> {
        const allSchedules = await this.getAllSchedules();
        const filteredSchedules = allSchedules.schedules.filter(schedule => schedule.status === status);
        return { schedules: filteredSchedules };
    }

    async createSchedule(name: string, dayOfWeek: number, time: string): Promise<TournamentEventScheduleDto> {
        const id = `${Math.trunc(Math.random() * 1000000) + 1000000}`;
        const schedule: TournamentEventScheduleDto = {
            id,
            name,
            dayOfWeek,
            time,
            status: "ACTIVE",
            events: []
        };
        localStorageUtil.setObject(`cgd.schedule.${id}`, schedule);
        return schedule;
    }

    async updateSchedule(scheduleId: string, name: string, dayOfWeek: number, time: string): Promise<TournamentEventScheduleDto> {
        const schedule = await this.getSchedule(scheduleId);
        schedule.name = name;
        schedule.dayOfWeek = dayOfWeek;
        schedule.time = time;
        localStorageUtil.setObject(`cgd.schedule.${scheduleId}`, schedule);
        return schedule;
    }

    async updateScheduleStatus(scheduleId: string, status: TournamentEventScheduleStatus): Promise<TournamentEventScheduleDto> {
        const schedule = await this.getSchedule(scheduleId);
        schedule.status = status;
        localStorageUtil.setObject(`cgd.schedule.${scheduleId}`, schedule);
        return schedule;
    }

    async createEventFromSchedule(scheduleId: string, date: string, locationName?: string, locationUrl?: string, roundsNumber: number = 5, registrationLimit?: number): Promise<TournamentEventDto> {
        const schedule = await this.getSchedule(scheduleId);
        const id = `${Math.trunc(Math.random() * 1000000) + 1000000}`;
        const event: TournamentEventDto = {
            id,
            name: schedule.name,
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
        
        if (!schedule.events) {
            schedule.events = [];
        }
        schedule.events.push(event);
        localStorageUtil.setObject(`cgd.schedule.${scheduleId}`, schedule);
        
        return event;
    }

    async deleteSchedule(scheduleId: string): Promise<void> {
        localStorageUtil.removeObject(`cgd.schedule.${scheduleId}`);
    }
}

class RestApiTournamentEventScheduleRepository implements TournamentEventScheduleRepository {
    async getSchedule(scheduleId: string): Promise<TournamentEventScheduleDto> {
        return await restApiClient.get<TournamentEventScheduleDto>(`/tournament-event-schedule/${scheduleId}`);
    }

    async getAllSchedules(): Promise<TournamentEventScheduleListDto> {
        return await restApiClient.get<TournamentEventScheduleListDto>("/tournament-event-schedule");
    }

    async getSchedulesByStatus(status: TournamentEventScheduleStatus): Promise<TournamentEventScheduleListDto> {
        return await restApiClient.get<TournamentEventScheduleListDto>(`/tournament-event-schedule/status/${status}`);
    }

    async createSchedule(name: string, dayOfWeek: number, time: string): Promise<TournamentEventScheduleDto> {
        const params = new URLSearchParams();
        params.append("name", name);
        params.append("dayOfWeek", dayOfWeek.toString());
        params.append("time", time);
        
        return await restApiClient.post<TournamentEventScheduleDto>(`/tournament-event-schedule?${params.toString()}`);
    }

    async updateSchedule(scheduleId: string, name: string, dayOfWeek: number, time: string): Promise<TournamentEventScheduleDto> {
        const params = new URLSearchParams();
        params.append("name", name);
        params.append("dayOfWeek", dayOfWeek.toString());
        params.append("time", time);
        
        return await restApiClient.put<TournamentEventScheduleDto>(`/tournament-event-schedule/${scheduleId}?${params.toString()}`);
    }

    async updateScheduleStatus(scheduleId: string, status: TournamentEventScheduleStatus): Promise<TournamentEventScheduleDto> {
        const params = new URLSearchParams();
        params.append("status", status);
        
        return await restApiClient.put<TournamentEventScheduleDto>(`/tournament-event-schedule/${scheduleId}/status?${params.toString()}`);
    }

    async createEventFromSchedule(scheduleId: string, date: string, locationName?: string, locationUrl?: string, roundsNumber: number = 5, registrationLimit?: number): Promise<TournamentEventDto> {
        const params = new URLSearchParams();
        params.append("date", date);
        if (locationName) params.append("locationName", locationName);
        if (locationUrl) params.append("locationUrl", locationUrl);
        params.append("roundsNumber", roundsNumber.toString());
        if (registrationLimit) params.append("registrationLimit", registrationLimit.toString());
        
        return await restApiClient.post<TournamentEventDto>(`/tournament-event-schedule/${scheduleId}/event?${params.toString()}`);
    }

    async deleteSchedule(scheduleId: string): Promise<void> {
        await restApiClient.delete(`/tournament-event-schedule/${scheduleId}`);
    }
}

const tournamentEventScheduleRepository = qualifiedServiceProxy({
    local: new LocalStorageTournamentEventScheduleRepository(),
    production: new RestApiTournamentEventScheduleRepository(),
});

export default tournamentEventScheduleRepository;