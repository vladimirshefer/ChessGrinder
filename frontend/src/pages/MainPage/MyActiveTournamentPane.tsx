import {ListDto, TournamentDto} from "lib/api/dto/MainPageData";
import {MatchDto, MatchResult, ParticipantDto, RoundDto} from "lib/api/dto/TournamentPageData";
import {useQuery} from "@tanstack/react-query";
import tournamentPageRepository from "lib/api/repository/TournamentPageRepository";
import {useMemo, useState} from "react";
import {isNotEmptyArray} from "lib/util/common";
import {useLoc} from "strings/loc";
import {FaChessKing, FaRegChessKing} from "react-icons/fa6";
import {Link} from "react-router-dom";
import {BiSolidChess} from "react-icons/bi";
import {IconTag} from "pages/MainPage/TournamentPane";
import userRepository from "lib/api/repository/UserRepository";
import {useAuthenticatedUser} from "contexts/AuthenticatedUserContext";
import config from "config";
import {useMode} from "lib/api/repository/apiSettings";
import roundRepository from "lib/api/repository/RoundRepository";

function MyActiveTournamentPane() {

    let [authenticatedUser] = useAuthenticatedUser()

    let meParticipantsQuery = useQuery({
        queryKey: ["meParticipants", authenticatedUser?.id],
        queryFn: async (): Promise<ListDto<ParticipantDto>> => {
            let emptyList = {values: []} as ListDto<ParticipantDto>;
            if (!authenticatedUser?.id) return emptyList
            return await userRepository.getParticipant(authenticatedUser.id) || emptyList;
        }
    })

    let meParticipant = useMemo(() => {
        if (meParticipantsQuery.isSuccess && !!meParticipantsQuery.data) {
            let participantDtos = meParticipantsQuery?.data?.values
                ?.filter(it => it.tournament?.status === "ACTIVE");
            if (participantDtos !== undefined && participantDtos.length !== 0) {
                return participantDtos[0]
            }
        }
        return undefined;
    }, [meParticipantsQuery.data, meParticipantsQuery.isSuccess])

    let tournamentQuery = useQuery({
        queryKey: ["tournamentPageData", meParticipant?.tournament?.id],
        queryFn: async () => {
            let tournamentId = meParticipant?.tournament?.id;
            if (!tournamentId) return null;
            let tournamentPageData = await tournamentPageRepository.getData(tournamentId);
            if (!tournamentPageData) return null;
            return tournamentPageData
        }
    })

    let isTournamentDataLoaded = tournamentQuery.isSuccess && tournamentQuery.data

    let rounds = useMemo(() => {
        if (!isTournamentDataLoaded) {
            return undefined
        }
        let tournamentPageData = tournamentQuery.data!!;
        let rounds = tournamentPageData.rounds;
        if (isNotEmptyArray(rounds)) {
            return rounds
        } else {
            return undefined
        }
    }, [isTournamentDataLoaded, tournamentQuery.data])

    let currentRoundIndex: number | undefined = useMemo(() => {
        if (!isNotEmptyArray(rounds)) {
            return undefined
        }
        let result: any = (rounds as any).findLastIndex((it: RoundDto) => isNotEmptyArray(it.matches));

        if (result < 0) {
            return undefined
        }

        return result
    }, [rounds])

    let currentRound: RoundDto | undefined = useMemo(() => {
        if ((!currentRoundIndex && currentRoundIndex !== 0) || !isNotEmptyArray(rounds)) {
            return undefined
        } else {
            return rounds!![currentRoundIndex];
        }
    }, [rounds, currentRoundIndex])

    let currentMatchIndex = useMemo(() => {
        if (!currentRound) {
            return undefined
        }

        let result = currentRound.matches.findIndex(it => it.black?.id === meParticipant?.id || it.white?.id === meParticipant?.id);

        if (result < 0) {
            return undefined
        }

        return result
    }, [currentRound, meParticipant?.id])

    let currentMatch = useMemo(() => {
        if ((!currentMatchIndex && currentMatchIndex !== 0) || !currentRound) {
            return undefined;
        }

        return currentRound.matches[currentMatchIndex]
    }, [currentMatchIndex, currentRound])

    let opponent: ParticipantDto | undefined = useMemo(() => {
        if (!currentMatch) {
            return undefined;
        }

        let meIsBlack = currentMatch?.black?.id === meParticipant?.id;

        let opponent = meIsBlack
            ? currentMatch?.white
            : currentMatch?.black;

        return opponent || undefined
    }, [currentMatch, meParticipant?.id])

    if (!meParticipant ||
        !meParticipant.tournament ||
        !currentMatch ||
        (!currentRoundIndex && currentRoundIndex !== 0) ||
        (!currentMatchIndex && currentMatchIndex !== 0)
    ) {
        console.log(meParticipant, meParticipant?.tournament, currentMatch, currentRoundIndex, currentMatchIndex)
        return <div className={"hidden"}>No current tournament</div>
    }

    return <div className={"p-3"}>
        <MyActiveTournamentPaneImpl
            tournament={meParticipant.tournament}
            match={currentMatch}
            boardNumber={currentMatchIndex + 1 || -1}
            roundNumber={currentRoundIndex + 1 || -1}
            opponent={opponent}
            meParticipant={meParticipant}
            refetchTournament={async () => {
                await tournamentQuery.refetch()
            }}
        />
    </div>
}

function MyActiveTournamentPaneImpl(
    {
        tournament,
        match,
        boardNumber,
        roundNumber,
        opponent,
        meParticipant,
        refetchTournament,
    }: {
        tournament: TournamentDto,
        match: MatchDto,
        boardNumber: number,
        roundNumber: number,
        opponent: ParticipantDto | undefined,
        meParticipant: ParticipantDto | undefined,
        refetchTournament: () => Promise<void>,
    }
) {
    let loc = useLoc();
    let [mode,] = useMode();
    let [resultSelectorActive, setResultSelectorActive] = useState(false)
    let ENABLE_PARTICIPANT_SUBMIT_RESULT = mode === "local" || config.features["tournament.submitResultByParticipantsEnabled"]

    function getResultStr(result: MatchResult | undefined, isMeWhite: boolean) {
        switch (result) {
            case "WHITE_WIN":
                if (isMeWhite) {
                    return loc("You won")
                } else {
                    return loc("You lost")
                }
            case "BLACK_WIN":
                if (isMeWhite) {
                    return loc("You lost")
                } else {
                    return loc("You won")
                }
            case "DRAW":
                return loc("Draw")
            case "BUY":
                return loc("Bye")
            case "MISS":
                return loc("Miss")
            default:
                return loc("Submit result")
        }
    }

    let isMeWhite = match.white?.id === meParticipant?.id;

    async function submitMatchResult(matchResult: MatchResult) {
        if (!match.result) {
            try {
                await roundRepository.postMatchResult(tournament.id, roundNumber, match.id, matchResult)
                await refetchTournament()
            } catch (e: any) {
                alert("Could not set match result " + (e?.response?.data?.message || e?.message))
            } finally {
                setResultSelectorActive(false)
            }
        }
    }

    return <div className={`grid justify-items-start w-full p-4 overflow-hidden tournament-active`}>
        <div className={"grid w-full justify-items-start"}>
            <Link className={"flex w-full gap-2 text-lg text-left justify-between items-center"}
                  to={`/tournament/${tournament.id}/round/${roundNumber}`}
            >
                <div className="flex gap-2 hover:underline font-semibold">
                    <span className={"grow"}>
                        {`Game ${roundNumber}`}
                    </span>
                </div>
            </Link>
        </div>
        <div className={"flex gap-1 items-center"}>
            {(isMeWhite && (
                <IconTag
                    icon={<FaChessKing className={"fill-primary-400"}/>}
                    text={"You"}
                />
            )) || (
                <IconTag
                    icon={<FaChessKing className={"fill-primary-400"}/>}
                    text={opponent?.name || opponent?.userFullName || "—"}
                />
            )}
            <span> - </span>
            {((!isMeWhite) && (
                <IconTag
                    icon={<FaRegChessKing className={"fill-primary-400"}/>}
                    text={"You"}
                />
            )) || (
                <IconTag
                    icon={<FaRegChessKing className={"fill-primary-400"}/>}
                    text={opponent?.name || opponent?.userFullName || "—"}
                />
            )}
        </div>
        <IconTag
            icon={<BiSolidChess className={"text-primary-400"}/>}
            text={match.result === "BUY" ? "—" : (loc("Board") + " " + boardNumber)}
        />
        <div className={"p-1"}></div>
        <div className={"btn-light text-sm"} onClick={() => {
            if (!match.result && match.result !== "BUY" && match.result !== "MISSING" && ENABLE_PARTICIPANT_SUBMIT_RESULT) {
                setResultSelectorActive(!resultSelectorActive)
            }
        }}>
            {loc(getResultStr(match.result || match.resultSubmittedByWhite || match.resultSubmittedByBlack, isMeWhite))}
        </div>
        {(resultSelectorActive && (<>
                <div className={"p-1"}></div>
                <div className={"flex gap-1"}>
                    <div className={"btn-light text-sm !bg-primary-400 grow"} onClick={async () => {
                        await submitMatchResult(isMeWhite ? "WHITE_WIN" : "BLACK_WIN");
                    }}>
                        {loc("Win")}
                    </div>
                    <div className={"btn-light text-sm !bg-primary-200 grow"} onClick={async () => {
                        await submitMatchResult("DRAW");
                    }}>
                        {loc("Draw")}
                    </div>
                    <div className={"btn-light text-sm !bg-gray-200 grow"} onClick={async () => {
                        await submitMatchResult(isMeWhite ? "BLACK_WIN" : "WHITE_WIN");
                    }}>
                        {loc("Loss")}
                    </div>
                </div>
            </>
        ))}

    </div>;
}

export default MyActiveTournamentPane
