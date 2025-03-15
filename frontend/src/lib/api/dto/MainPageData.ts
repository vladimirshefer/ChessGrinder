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
    globalScore?: number
    emailHash: string
    eloPoints?: number;
}

export interface ListDto<T extends any> {
    count?: number
    values: T[]
}

export type TournamentStatus = "FINISHED" | "ACTIVE" | "PLANNED"

export type PairingStrategy = "SWISS" | "ROUND_ROBIN"

export let DEFAULT_DATETIME_FORMAT = "YYYY-MM-DDTHH:mm"

export interface TournamentDto {
    id: string
    name: string
    date: string
    status?: TournamentStatus
    locationName?: string
    locationUrl?: string
    roundsNumber: number
    pairingStrategy: PairingStrategy
}

export interface TournamentListDto {
    tournaments: TournamentDto[]
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
