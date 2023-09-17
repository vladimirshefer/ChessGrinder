import {MatchDto, MatchResult, RoundDto} from "lib/api/dto/TournamentPageData";
import RunningRoundTable from "./RunningRoundTable";
import React from "react";

export default function RoundTab(
    {
        round,
        submitMatchResult,
        submitRoundFinished,
        deleteRound,
        reopenRound,
        drawRound,
    }: {
        round: RoundDto,
        submitMatchResult: (match: MatchDto, result: MatchResult | null) => void,
        submitRoundFinished: () => void,
        deleteRound: () => void,
        reopenRound: () => void,
        drawRound: () => void,
    }
) {
    return <div>
        { !round.isFinished ?
            <RunningRoundTable matches={round.matches || []}
                               submitMatchResult={(match, result) => {
                                   submitMatchResult(match, result!!);
                               }}
            /> : <div>Round is finished</div>
        }
        <div className={"mt-2 px-2 w-full flex justify-end"}>
            <button className={"bg-red-200 p-1 rounded-md mx-1 px-1"}
                    onClick={() => {
                        if (window.confirm("Delete round?")) {
                            deleteRound();
                        }
                    }}
            >Delete
            </button>
            <button className={"bg-orange-200 p-1 rounded-md mx-1 px-1"}
                    onClick={() => drawRound()}
            >Draw
            </button>
            {
                !round.isFinished ?
                    <button className={"bg-blue-200 p-1 rounded-md mx-1"}
                            onClick={() => submitRoundFinished()}
                    >Finish
                    </button> :
                    <button
                        className={"bg-blue-200 p-1 rounded-md mx-1"}
                        onClick={() => reopenRound()}
                    >Reopen</button>
            }
        </div>
    </div>
}
