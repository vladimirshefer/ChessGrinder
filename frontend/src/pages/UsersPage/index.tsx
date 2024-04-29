import React from "react";
import {useQuery} from "@tanstack/react-query";
import { useLocation } from 'react-router-dom';
import {useLoc} from "strings/loc";
import userRepository from "lib/api/repository/UserRepository";
import MemberList from "pages/MainPage/MemberList";

export default function UsersPage() {
    let loc = useLoc();
    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const startSeason = searchParams.get('startSeason');
    const endSeason = searchParams.get('endSeason');

    if (searchParams.size !== 0 && (startSeason === null || endSeason === null)) {
        alert(loc("Wrong parameters count"));
    }

    let usersQuery = useQuery({
        queryKey: ["members"],
        queryFn: async () => {
            return await userRepository.getUsers() //TODO можно попытаться засунуть прямо сюда!
            //только с другим названием, например: getUsersWithSeasonDates
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
