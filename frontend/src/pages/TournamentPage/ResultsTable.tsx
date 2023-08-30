import React, {useMemo, useState} from "react";
import {ParticipantDto} from "lib/api/dto/TournamentPageData";

function ResultsTable(
    {
        participants,
        addParticipant
    }: {
        participants: ParticipantDto[],
        addParticipant: (name: string) => void,
    }
) {

    let [isInputActive, setInputActive] = useState(false)
    let [selectedParticipant, setSelectedParticipant] = useState<string>("")

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
                {
                    isInputActive ?
                        <div className={"w-full grid grid-cols-12 p-1"}>
                            <div className={"col-span-9"}>
                                <input className={"border-b-2 border-b-blue-300 w-full px-2 outline-none"}
                                       autoFocus list="members" name="myBrowser"
                                       onChange={event => setSelectedParticipant(event.target.value)}
                                />
                                <datalist id="members">
                                    {members.map(memberName => <option key={memberName} value={memberName}/>)}
                                </datalist>
                            </div>
                            <div className={"col-span-3 px-2 grid grid-cols-12 gap-x-1"}>
                                <button className={"w-full bg-blue-300 rounded-full col-span-8"}
                                        onClick={() => {
                                            if (selectedParticipant) {
                                                addParticipant(selectedParticipant)
                                            }
                                            setInputActive(false)
                                        }}>
                                    Add
                                </button>
                                <button className={"w-full bg-red-300 rounded-full col-span-4"}
                                        onClick={() => {
                                            setInputActive(false)
                                        }}>
                                    X
                                </button>
                            </div>
                        </div>
                        : <button className={"w-full bg-blue-300 rounded-full p-1"}
                                  onClick={() => setInputActive(true)}> Add participant </button>
                }
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
