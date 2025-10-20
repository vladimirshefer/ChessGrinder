import {UserDto} from "lib/api/dto/MainPageData";
import React from "react";
import {UserPane} from "pages/MainPage/UserPane";

export function MemberList(
    {
        users,
    }: {
        users: UserDto[],
    }
) {
    return <div>
        <div className={"w-full grid"}>
            {users.map(user =>
                <div className={"border-b py-1"} key={user.id}>
                    <UserPane user={user}/>
                </div>
            )}
        </div>
    </div>;
}

export default MemberList
