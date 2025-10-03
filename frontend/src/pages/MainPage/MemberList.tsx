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
        <div className={"w-full grid"}>
            {users.map(user =>
                <div className={"border-b py-1"}>
                    <UserPane key={user.id} user={user}/>
                </div>
            )}
        </div>
    </div>;
}

export default MemberList
