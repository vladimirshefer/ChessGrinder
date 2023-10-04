import {TournamentDto} from "./MainPageData";

export interface MatchParticipantDto {
    userId: string,
    name: string
}

export interface MatchDto {
    id: string
    result: MatchResult | undefined
    white: ParticipantDto
    black: ParticipantDto
}

export type MatchResult = "WHITE_WIN" | "BLACK_WIN" | "DRAW"

export interface RoundDto {
    isFinished: boolean
    matches: MatchDto[]
}

export interface ParticipantDto {
    id: string
    name: string
    userId?: string | null | undefined
    score: number
    buchholz: number
}

export interface TournamentPageData {
    participants: ParticipantDto[]
    rounds: RoundDto[],
    tournament: TournamentDto,
}
