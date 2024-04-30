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

    let usersQuery = useQuery({
        queryKey: ["members"],
        queryFn: async () => {
            if (searchParams.size !== 0 && (startSeason === null || endSeason === null)) {
                alert(loc("Wrong set of request parameters"));
                return null;
            }
            try {
                return await userRepository.getUsersWithSeasonDates(startSeason, endSeason);
            }
            catch (error: any) {
                alert(loc(error.response.data.message));
            }
        },
    })

    let users = usersQuery.data?.values;
    if (!users) {
        return <>Loading...</>
    }
    return <div className={"p-2"}>
        <MemberList users={users}/>
    </div>
}
