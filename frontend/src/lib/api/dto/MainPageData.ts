export interface BadgeDto {
    id: string
    title: string
    description: string
    imageUrl: string
}

export interface UserBadgeDto {
    userId: string,
    badgeId: string,
}

export interface UserDto {
    id: string
    username: string
    name: string
    badges: BadgeDto[]
    roles?: string[]
    reputation?: number
    emailHash: string
    eloPoints?: number;
}

export interface ListDto<T> {
    count?: number
    values: T[]
}

export type TournamentStatus = "FINISHED" | "ACTIVE" | "PLANNED"

export type PairingStrategy = "DEFAULT" | "SWISS" | "ROUND_ROBIN" | "SIMPLE"

export type RepeatableType = "WEEKLY" | null

export let DEFAULT_DATETIME_FORMAT = "YYYY-MM-DDTHH:mm"

export interface TournamentDto {
    id: string
    name: string
    date: string
    status?: TournamentStatus
    locationName?: string
    locationUrl?: string
    city?: string
    roundsNumber: number
    registrationLimit?: number
    pairingStrategy: PairingStrategy
    repeatable?: RepeatableType
}

export interface TournamentListDto {
    values: TournamentDto[]
}

export enum UserRoles {
    ADMIN = "ROLE_ADMIN"
}

export interface UserReputationHistoryRecordDto{
    userId: string
    amount: number
    comment: string
}

export interface StatsAgainstUserDTO {
    wins: number
    losses: number
    draws: number
}
