import {MatchDto, MatchResult, RoundDto} from "lib/api/dto/TournamentPageData";
import MatchesTable from "./MatchesTable";
import React from "react";
import {Conditional, usePermissionGranted} from "components/Conditional";
import {useLoc} from "strings/loc";

export default function RoundTab(
    {
        tournamentId,
        round,
        submitMatchResult,
        submitRoundFinished,
        deleteRound,
        reopenRound,
        runPairing,
    }: {
        tournamentId: string,
        round: RoundDto,
        submitMatchResult: (match: MatchDto, result: MatchResult | null) => void,
        submitRoundFinished: () => void,
        deleteRound: () => void,
        reopenRound: () => void,
        runPairing: () => void,
    }
) {
    let loc = useLoc();
    let isMeModerator = usePermissionGranted(tournamentId, "TournamentEntity", "MODERATOR");

    if (!round) {
        return <>Error</>
    }

    return <div>
        <MatchesTable matches={round.matches || []}
                      canEditResults={!round.isFinished && isMeModerator}
                      submitMatchResult={(match, result) => {
                          submitMatchResult(match, result!!);
                      }}
        />
        <Conditional on={isMeModerator}>
            <div className={"mt-2 px-2 w-full flex justify-end gap-2"}>
                <Conditional on={!round.isFinished}>
                    <button className={"btn-dark p-1 px-1"}
                            onClick={() => runPairing()}
                    >{loc("Pairing")}
                    </button>
                </Conditional>
                {
                    !round.isFinished ?
                        <button className={"btn-dark p-1"}
                                onClick={() => submitRoundFinished()}
                        >{loc("Finish")}
                        </button> :
                        <button
                            className={"btn-dark p-1"}
                            onClick={() => reopenRound()}
                        >{loc("Reopen")}</button>
                }
                <button className={"btn-danger p-1 px-1"}
                        onClick={() => {
                            if (window.confirm("Delete round?")) {
                                deleteRound();
                            }
                        }}
                >{loc("Delete")}
                </button>
            </div>
        </Conditional>
    </div>
}
