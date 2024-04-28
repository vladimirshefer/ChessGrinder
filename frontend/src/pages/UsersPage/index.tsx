import React from "react";
import {useQuery} from "@tanstack/react-query";
import userRepository from "lib/api/repository/UserRepository";
import MemberList from "pages/MainPage/MemberList";
import useSearchParam from "lib/react/hooks/useSearchParam";

export default function UsersPage() {

    let startSeason = useSearchParam("startSeason")[0];
    let endSeason = useSearchParam("endSeason")[0];
    let usersQuery = useQuery({
        queryKey: ["members"],
        queryFn: async () => {
            return await userRepository.getUsers() //TODO можно попытаться засунуть прямо сюда!
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
