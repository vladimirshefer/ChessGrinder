import {MemberDto} from "lib/api/dto/MainPageData";
import React, {Fragment} from "react";
import ToggleableSelectableTextInput from "components/ToggleableSelectableTextInput";
import {Link} from "react-router-dom";
import ConditionalOnUserRole from "components/Conditional";

export function MemberList(
    {
        members,
        createMember,
    }: {
        members: MemberDto[],
        createMember: (memberName: string) => void,
    }
) {

    return <div>
        <h2 className={"text-xl my-2"}>Members</h2>
        <div className={"w-full grid grid-cols-12"}>
            <div className={"col-span-12"}>
                <ConditionalOnUserRole role={"ROLE_ADMIN"}>
                    <ToggleableSelectableTextInput
                        values={members.map(it => it.name)}
                        buttonText={"Add guest"}
                        submitValue={createMember}
                    />
                </ConditionalOnUserRole>
            </div>
            {members.map(member => {
                return <Fragment key={member.name}>
                    <div className={"col-span-6"}>
                        <Link to={`/user/${member.id}`}>
                            {member.name}
                        </Link>
                    </div>
                    <div className={"col-span-6"}>
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
                </Fragment>
            })}
        </div>
    </div>;
}

export default MemberList
