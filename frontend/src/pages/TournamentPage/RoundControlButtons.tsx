import React from "react";
import {Conditional} from "components/Conditional";
import {useLoc} from "strings/loc";

/**
 * Pairing / Finish / Reopen / Delete buttons of a round.
 * Shared by the tournament page (one round) and the event page (the same round of every tournament of the event).
 */
export default function RoundControlButtons(
    {
        hasUnfinishedRounds,
        hasFinishedRounds,
        runPairing,
        submitRoundFinished,
        reopenRound,
        deleteRound,
    }: {
        hasUnfinishedRounds: boolean,
        hasFinishedRounds: boolean,
        runPairing: () => void,
        submitRoundFinished: () => void,
        reopenRound: () => void,
        deleteRound: () => void,
    }
) {
    let loc = useLoc();

    return <div className={"mt-2 w-full flex justify-end gap-2"}>
        <Conditional on={hasUnfinishedRounds}>
            <button className={"btn-dark p-1 px-1"}
                    onClick={() => runPairing()}
            >{loc("Pairing")}
            </button>
            <button className={"btn-dark p-1"}
                    onClick={() => submitRoundFinished()}
            >{loc("Finish")}
            </button>
        </Conditional>
        <Conditional on={hasFinishedRounds}>
            <button
                className={"btn-dark p-1"}
                onClick={() => reopenRound()}
            >{loc("Reopen")}</button>
        </Conditional>
        <button className={"btn-danger p-1 px-1"}
                onClick={() => {
                    if (window.confirm("Delete round?")) {
                        deleteRound();
                    }
                }}
        >{loc("Delete")}
        </button>
    </div>
}
