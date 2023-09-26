import React, {useMemo} from "react";
import {ParticipantDto} from "lib/api/dto/TournamentPageData";
import ToggleableSelectableTextInput from "components/ToggleableSelectableTextInput";
import {useQuery} from "@tanstack/react-query";
import userRepository from "lib/api/repository/UserRepository";
import {ListDto, MemberDto} from "lib/api/dto/MainPageData";
import ConditionalOnUserRole from "components/ConditionalOnUserRole";

function ResultsTable(
    {
        participants,
        addParticipant
    }: {
        participants: ParticipantDto[],
        addParticipant: (participant: ParticipantDto) => void,
    }
) {

    let {
        data: {
            values: users = [] as MemberDto[]
        } = {} as ListDto<MemberDto>
    } = useQuery({
        queryKey: ["users"],
        queryFn: async () => await userRepository.getUsers()
    })

    let members = useMemo(() => users || [], [users])

    return <>
        <div className={"w-full grid grid-cols-12"}>
            <span className={"col-span-1"}></span>
            <span className={"col-span-5"}>Name</span>
            <span className={"col-span-3"}>PTS</span>
            <span className={"col-span-3"}>BHZ</span>
            <ConditionalOnUserRole role={"ADMIN"}>
                <div className={"col-span-12 px-2 my-1"}>
                    <ToggleableSelectableTextInput
                        values={members}
                        selectKeyExtractor={user => user.id}
                        selectOptionNameExtractor={user => user.name}
                        buttonText={"Add participant"}
                        submitValue={(userId) => addParticipant({
                            userId: userId,
                            name: members.find(it => it.id === userId)?.name || userId,
                            score: 0,
                            buchholz: 0,
                        })}
                    />
                </div>
            </ConditionalOnUserRole>
            {
                participants
                    .sort((a, b) => -(a.score !== b.score ? a.score - b.score : a.buchholz - b.buchholz))
                    .map((participant, idx) => {
                        return <div className={"col-span-12 grid grid-cols-12"} key={participant.name}>
                            <div className={"col-span-1"}>{idx + 1}</div>
                            <div className={"col-span-5"}>{participant.name}</div>
                            <div className={"col-span-3"}>{participant.score}</div>
                            <div className={"col-span-3"}>{participant.buchholz}</div>
                        </div>
                    })
            }
        </div>
    </>
}

export default ResultsTable
