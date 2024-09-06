import {TournamentDto} from "./MainPageData";

export interface MatchDto {
    id: string
    /**
     * Undefined if match is not played yet
     */
    result: MatchResult | undefined
    white: ParticipantDto | null
    /**
     * Null if the match is buy
     */
    black: ParticipantDto | null

    resultSubmittedByParticipant1?: MatchResult | undefined
    resultSubmittedByParticipant2?: MatchResult | undefined
}

export type MatchResult = "WHITE_WIN" | "BLACK_WIN" | "DRAW" | "BUY" | "MISS"

export interface RoundDto {
    number: number
    isFinished: boolean
    matches: MatchDto[]
}

export interface ParticipantDto {
    id: string
    name: string
    userId?: string | null | undefined
    userFullName?: string | null | undefined
    score: number
    buchholz: number
    isMissing: boolean
    isModerator?: boolean | null | undefined
    place: number
    tournament?: TournamentDto | null | undefined
}

export interface TournamentPageData {
    participants: ParticipantDto[]
    rounds: RoundDto[],
    tournament: TournamentDto,
}
