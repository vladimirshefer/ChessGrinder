import {UserDto} from "lib/api/dto/MainPageData";
import React from "react";
import {Link} from "react-router-dom";
import Gravatar, {GravatarType} from "components/Gravatar";
import {useLoc} from "strings/loc";
import {AiOutlineTrophy} from "react-icons/ai";

export function MemberList(
    {
        members: users,
    }: {
        members: UserDto[],
    }
) {
    let loc = useLoc()

    return <div>
        <h2 className={"text-xl my-2 uppercase text-left font-semibold"}>{loc("Members")}</h2>
        <div className={"w-full grid grid-cols-12"}>
            {users.map(user => {
                return <div key={user.name} className={"col-span-12 flex"}>
                    <div className={"h-[3em] w-[3em] inline-block overflow-hidden mr-2"}>
                        <Gravatar
                            text={user.username || user.id}
                            type={GravatarType.Robohash}
                            size={150}
                            className={"rounded-full"}
                        />
                    </div>
                    <div className={"grid w-full content-left items-left"}>
                        <div className={"text-left"}>
                            <Link to={`/user/${user.id}`}>
                                {user.name}
                            </Link>
                        </div>
                        <div className={"h-[1em] text-xl flex gap-2 items-start"}>
                            <div className={"flex items-start h-full gap-2 grow"}>
                                {(user.badges || []).map(badge =>
                                    <Link to={`/badge/${badge.id}`}
                                          className={"block h-full"}
                                          key={badge.id}
                                          title={`${badge.title}\n\n${badge.description}`}
                                    >
                                        <Gravatar
                                            text={badge.title}
                                            type={GravatarType.Identicon}
                                            size={150}
                                            className={"block rounded-full h-full"}
                                        />
                                    </Link>
                                )}
                            </div>
                            <div className={"h-full leading-4 flex block align-bottom"}>
                                <AiOutlineTrophy className={"inline -mt-[1px] leading-4  mr-1 align-bottom"}/>
                                <span className={""}>{user.reputation || 0}</span>
                            </div>
                        </div>
                    </div>
                </div>
            })}
        </div>
    </div>;
}

export default MemberList
