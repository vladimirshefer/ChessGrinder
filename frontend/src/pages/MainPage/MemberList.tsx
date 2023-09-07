import {MemberDto} from "lib/api/dto/MainPageData";

export function MemberList(
    {
        members
    }: {
        members: MemberDto[]
    }
) {
    return <div>
        <h2 className={"text-xl my-2"}>Members</h2>
        <div className={"w-full grid grid-cols-12"}>
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
                                return <span key={badge.imageUrl} title={badge.description}>{badge.imageUrl}</span>
                            })
                        }
                    </div>
                </>
            })}
        </div>
    </div>;
}

export default MemberList
