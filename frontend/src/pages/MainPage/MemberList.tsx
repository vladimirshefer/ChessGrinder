import {MemberDto} from "lib/api/dto/MainPageData";
import React, {Fragment} from "react";
import {Link} from "react-router-dom";
import Gravatar, {GravatarType} from "../../components/Gravatar";

export function MemberList(
    {
        members: users,
    }: {
        members: MemberDto[],
    }
) {

    return <div>
        <h2 className={"text-xl my-2"}>Members</h2>
        <div className={"w-full grid grid-cols-12"}>
            {users.map(user => {
                return <Fragment key={user.name}>
                    <div className={"col-span-6 flex content-center items-center"}>
                        <div className={"w-8 imline-block rounded-full overflow-hidden mr-2"}>
                            <Gravatar text={user.username || user.id} type={GravatarType.Robohash} size={50}/>
                        </div>
                        <Link to={`/user/${user.id}`}>
                            {user.name}
                        </Link>
                    </div>
                    <div className={"col-span-6"}>
                        {
                            (user.badges || []).map(badge => {
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
