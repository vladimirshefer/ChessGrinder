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

export interface MemberDto {
    id: string
    username: string
    name: string
    badges: BadgeDto[]
    roles?: string[]
}

export interface ListDto<T extends any> {
    values: T[]
}

export type TournamentStatus = "FINISHED" | "ACTIVE" | "PLANNED"

export let DEFAULT_DATETIME_FORMAT = "YYYY-MM-DDTHH:mm"

export interface TournamentDto {
    id: string
    name: string
    date: string
    status?: TournamentStatus
    locationName?: string
    locationUrl?: string
}

export interface TournamentListDto {
    tournaments: TournamentDto[]
}

export interface MainPageData {
    members: MemberDto[]
    tournaments: TournamentDto[]
}

export enum UserRoles {
    ADMIN = "ROLE_ADMIN"
}
