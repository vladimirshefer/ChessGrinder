export interface BadgeDto {
    title: string
    description: string
    imageUrl: string
}

export interface MemberDto {
    id: string
    name: string
    badges: BadgeDto[]
}

export interface TournamentDto {
    id: string
    name: string
    date: string
}

export interface MainPageData {
    members: MemberDto[]
    tournaments: TournamentDto[]
}
