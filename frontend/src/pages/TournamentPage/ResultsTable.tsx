import React, {useMemo} from "react";
import {ParticipantDto} from "lib/api/dto/TournamentPageData";
import ToggleableSelectableTextInput from "components/ToggleableSelectableTextInput";
import {useQuery} from "@tanstack/react-query";
import userRepository from "lib/api/repository/UserRepository";

function ResultsTable(
    {
        participants,
        addParticipant
    }: {
        participants: ParticipantDto[],
        addParticipant: (name: string) => void,
    }
) {

    let {data: users} = useQuery({
        queryKey: ["users"],
        queryFn: async () => {
            return await userRepository.getUsers()
        }
    })

    let members = useMemo(() => users || [], [users])

    return <>
        <div className={"w-full grid grid-cols-12"}>
            <span className={"col-span-6"}>Name</span>
            <span className={"col-span-3"}>PTS</span>
            <span className={"col-span-3"}>BHZ</span>
            <div className={"col-span-12 px-2 my-1"}>
                <ToggleableSelectableTextInput
                    values={members}
                    selectKeyExtractor={user => user.id}
                    selectOptionNameExtractor={user => user.name}
                    buttonText={"Add participant"}
                    submitValue={addParticipant}
                />
            </div>
            {
                participants
                    .sort((a, b) => -(a.score !== b.score ? a.score - b.score : a.buchholz - b.buchholz))
                    .map(participant => {
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
