export interface BadgeDto {
    title: string
    description: string
    imageUrl: string
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

export interface TournamentDto {
    id: string
    name: string
    date: string
    status?: TournamentStatus
}

export interface TournamentListDto {
    tournaments: TournamentDto[]
}

export interface MainPageData {
    members: MemberDto[]
    tournaments: TournamentDto[]
}
