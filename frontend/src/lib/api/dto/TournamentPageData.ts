export interface MatchParticipantDto {
    userId: string,
    name: string
}

export interface MatchDto {
    result: "WHITE_WIN" | "BLACK_WIN" | "DRAW" | undefined
    white: MatchParticipantDto
    black: MatchParticipantDto
}

export interface RoundDto {
    state: "FINISHED" | "STARTED"
    matches: MatchDto[]
}

export interface ParticipantDto {
    name: string
    userId: string
    score: number
    buchholz: number
}

export interface TournamentPageData {
    id: string
    name: string | undefined | null
    participants: ParticipantDto[]
    rounds: RoundDto[]
}
