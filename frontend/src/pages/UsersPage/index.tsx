import React from "react";
import {useQuery} from "@tanstack/react-query";
import userRepository from "lib/api/repository/UserRepository";
import MemberList from "pages/MainPage/MemberList";
import useSearchParam from "lib/react/hooks/useSearchParam";

export default function UsersPage() {

    let startSeason = useSearchParam("startSeason", "false")[0];
    let endSeason = useSearchParam("endSeason", "false")[0];
    console.log(startSeason!! + "@@@");
    let usersQuery = useQuery({
        queryKey: ["members"],
        queryFn: async () => {
            return await userRepository.getUsers()
        },
    })


    let users = usersQuery.data?.values;
    if (!users) {
        return <>Loading...</>
    }



    return <div className={"p-2"}>
        <MemberList users={users} startSeasonDate={startSeason} endSeasonDate={endSeason}/>
    </div>
}
