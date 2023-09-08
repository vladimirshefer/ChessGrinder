import React, {useMemo, useState} from "react";
import {ParticipantDto} from "lib/api/dto/TournamentPageData";
import ToggleableSelectableTextInput from "../../components/ToggleableSelectableTextInput";

function ResultsTable(
    {
        participants,
        addParticipant
    }: {
        participants: ParticipantDto[],
        addParticipant: (name: string) => void,
    }
) {

    let members = useMemo(() => [
        "",
        "Vladimir Shefer",
        "Alexander Boldyrev",
        "Stanislav Malov",
        "Sergey Pavlov",
        "Aikhan Ibrahimov",
        "Mikhail Boba"
    ].sort(), [])

    return <>
        <div className={"w-full grid grid-cols-12"}>
            <span className={"col-span-6"}>Name</span>
            <span className={"col-span-3"}>PTS</span>
            <span className={"col-span-3"}>BHZ</span>
            <div className={"col-span-12 px-2 my-1"}>
                <ToggleableSelectableTextInput
                    values={members}
                    buttonText={"Add participant"}
                    submitValue={addParticipant}
                />
            </div>
            {
                participants.map(participant => {
                    return <div className={"col-span-12 grid grid-cols-12"} key={participant.name}>
                        <div className={"col-span-6"}>{participant.name}</div>
                        <div className={"col-span-3"}>{participant.score}</div>
                        <div className={"col-span-3"}>{participant.buchholz}</div>
                    </div>
                })
            }
        </div>
    </>
}

export default ResultsTable
