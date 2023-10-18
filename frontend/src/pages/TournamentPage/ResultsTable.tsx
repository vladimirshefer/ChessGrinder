import React from "react";
import {ParticipantDto} from "lib/api/dto/TournamentPageData";
import {useLoc} from "strings/loc";

function ResultsTable(
    {
        participants,
        openParticipant,
    }: {
        participants: ParticipantDto[],
        openParticipant: (participant: ParticipantDto) => void,
    }
) {
    let loc = useLoc();

    return <>
        <div className={"w-full grid grid-cols-12 text-left p-2"}>
            <div className={"col-span-12 grid grid-cols-12 border-b font-semibold"}>
                <span className={"col-span-1 uppercase"}>№</span>
                <span className={"col-span-7 uppercase"}>{loc("Name")}</span>
                <span className={"col-span-2 uppercase"}>PTS</span>
                <span className={"col-span-2 uppercase"}>BHZ</span>
            </div>
            {
                participants
                    .sort((a, b) => -(a.score !== b.score ? a.score - b.score : a.buchholz - b.buchholz))
                    .map((participant, idx) => {
                        return <div className={"col-span-12 grid grid-cols-12 p-2 border-b text-sm"} key={participant.name}>
                            <div className={"col-span-1 text-gray-600"}>{idx + 1}</div>
                            <div className={"col-span-7"}>
                                <button onClick={() => openParticipant(participant)}>
                                    <div className={"grid text-left"}>
                                        <span className={"font-semibold"}>{participant.name}</span>
                                        {participant.userFullName &&
                                            <span>{participant.userFullName}</span>
                                        }
                                    </div>
                                </button>
                            </div>
                            <div className={"col-span-2"}>{participant.score}</div>
                            <div className={"col-span-2"}>{participant.buchholz}</div>
                        </div>
                    })
            }
        </div>
    </>
}

export default ResultsTable
