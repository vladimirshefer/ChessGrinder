import React from "react";
import {ParticipantDto} from "lib/api/dto/TournamentPageData";
import {useLoc, useTransliterate} from "strings/loc";
import {RiAdminLine} from "react-icons/ri";

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
    let transliterate = useTransliterate();

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
                    .map((participant, idx) => {
                        return <div className={`col-span-12 grid grid-cols-12 p-2 border-b text-sm ${participant.isMissing ? "bg-gray-100" : ""}`} key={participant.name}>
                            <div className={"col-span-1 text-gray-600"}>{idx + 1}</div>
                            <div className={"col-span-7"}>
                                <button onClick={() => openParticipant(participant)}>
                                    <div className={"grid text-left"}>
                                        <span className={"font-semibold text-ellipsis overflow-hidden line-clamp-3"}>{transliterate(participant.name) || "No nickname"}</span>
                                        {participant.userFullName &&
                                            <div className={"flex items-center gap-1"}>
                                                {participant.isModerator === true && (
                                                    <RiAdminLine className={"inline"} title={"Moderator"}/>
                                                )}
                                                <span className={"text-ellipsis overflow-hidden line-clamp-2"}>{transliterate(participant.userFullName)}</span>
                                            </div>
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
