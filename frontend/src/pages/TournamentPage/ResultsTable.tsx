import React from "react";
import {ParticipantDto} from "lib/api/dto/TournamentPageData";

function ResultsTable(
    {
        participants,
        openParticipant,
    }: {
        participants: ParticipantDto[],
        openParticipant: (participant: ParticipantDto) => void,
    }
) {

    return <>
        <div className={"w-full grid grid-cols-12"}>
            <div className={"col-span-12 grid grid-cols-12 border-b"}>
                <span className={"col-span-1 uppercase"}>#</span>
                <span className={"col-span-5 uppercase"}>Name</span>
                <span className={"col-span-3 uppercase"}>PTS</span>
                <span className={"col-span-3 uppercase"}>BHZ</span>
            </div>
            {
                participants
                    .sort((a, b) => -(a.score !== b.score ? a.score - b.score : a.buchholz - b.buchholz))
                    .map((participant, idx) => {
                        return <div className={"col-span-12 grid grid-cols-12"} key={participant.name}>
                            <div className={"col-span-1"}>{idx + 1}</div>
                            <div className={"col-span-5"}>
                                <button onClick={() => openParticipant(participant)}>
                                    {participant.name}
                                </button>
                            </div>
                            <div className={"col-span-3"}>{participant.score}</div>
                            <div className={"col-span-3"}>{participant.buchholz}</div>
                        </div>
                    })
            }
        </div>
    </>
}

export default ResultsTable
