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

export interface TournamentDto {
    id: string
    name: string
    date: string
    status?: string
}

export interface TournamentListDto{
    tournaments: TournamentDto[]
}

export interface MainPageData {
    members: MemberDto[]
    tournaments: TournamentDto[]
}
