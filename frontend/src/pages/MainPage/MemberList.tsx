import {MemberDto} from "lib/api/dto/MainPageData";
import React from "react";
import ToggleableSelectableTextInput from "components/ToggleableSelectableTextInput";

export function MemberList(
    {
        members
    }: {
        members: MemberDto[]
    }
) {
    function createMember(selectedParticipant: string) {
        members.push({
            name: selectedParticipant,
            id: selectedParticipant,
            badges: [],
        })
    }

    return <div>
        <h2 className={"text-xl my-2"}>Members</h2>
        <div className={"w-full grid grid-cols-12"}>
            <div className={"col-span-12"}>
                <ToggleableSelectableTextInput
                    values={members.map(it => it.name)}
                    buttonText={"Add member"}
                    submitValue={createMember}
                />
            </div>
            {members.map(member => {
                return <>
                    <div key={member.name}
                         className={"col-span-6"}
                    >{member.name}</div>
                    <div key={`${member.name}_badges`}
                         className={"col-span-6"}
                    >
                        {
                            (member.badges || []).map(badge => {
                                return <span
                                    key={badge.imageUrl}
                                    title={badge.description}
                                    className={"cursor-default"}
                                >{badge.imageUrl}</span>
                            })
                        }
                    </div>
                </>
            })}
        </div>
    </div>;
}

export default MemberList
