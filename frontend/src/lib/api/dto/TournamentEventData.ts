// First, let's create the DTOs for TournamentEvent and TournamentEventSchedule

import { TournamentDto } from "./MainPageData";
import { ParticipantDto } from "./TournamentPageData";

export type TournamentEventScheduleStatus = "ACTIVE" | "PAUSED" | "ARCHIVED";

export interface TournamentEventDto {
    id: string;
    name?: string;
    locationName?: string;
    locationUrl?: string;
    date: string;
    status: "FINISHED" | "ACTIVE" | "PLANNED";
    roundsNumber: number;
    registrationLimit?: number;
    tournaments?: TournamentDto[];
    participants?: ParticipantDto[];
}

export interface TournamentEventScheduleDto {
    id: string;
    name: string;
    dayOfWeek: number; // 1-7 representing Monday-Sunday
    time: string; // Format: "HH:mm"
    status: TournamentEventScheduleStatus;
    events?: TournamentEventDto[];
}

export interface TournamentEventListDto {
    events: TournamentEventDto[];
}

export interface TournamentEventScheduleListDto {
    schedules: TournamentEventScheduleDto[];
}