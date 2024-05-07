import {UserDto} from "lib/api/dto/MainPageData";
import React from "react";
import {useLoc} from "strings/loc";
import {UserPane} from "pages/MainPage/UserPane";

export function MemberList(
    {
        users,
    }: {
        users: UserDto[],
    }
) {
    let loc = useLoc()

    return <div>
        <h2 className={"text-xl my-2 uppercase text-left font-semibold"}>{loc("Members")}</h2>
        <div className={"w-full grid grid-cols-12"}>
            {users.map(user => <UserPane key={user.id} user={user}/>)}
        </div>
    </div>;
}

export default MemberList
