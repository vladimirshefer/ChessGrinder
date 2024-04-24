import {UserDto} from "lib/api/dto/MainPageData";
import React from "react";
import {useLoc} from "strings/loc";
import {UserPane} from "pages/MainPage/UserPane";

export function MemberList(
    {
        users,
        startSeasonDate = null,
        endSeasonDate = null,
    }: {
        users: UserDto[],
        startSeasonDate?: Date | null,
        endSeasonDate?: Date | null,
    }
) {
    let loc = useLoc()

// new Date('2024-04-01'), new Date('2024-04-23')
//TODO теперь передавать в MemberList
    return <div>
        <h2 className={"text-xl my-2 uppercase text-left font-semibold"}>{loc("Members")}</h2>
        <div className={"w-full grid grid-cols-12"}>
            {users.map(user => <UserPane user={user}
                                startSeasonDate={startSeasonDate}
                                endSeasonDate={endSeasonDate}/>)}
        </div>
    </div>;
}

export default MemberList
