import React from "react";
import {useQuery} from "@tanstack/react-query";
import {useLoc} from "strings/loc";
import useSearchParam from "lib/react/hooks/useSearchParam";
import userRepository from "lib/api/repository/UserRepository";
import MemberList from "pages/MainPage/MemberList";

export default function UsersPage() {
    let loc = useLoc();
    const [startSeason] = useSearchParam("startSeason");
    const [endSeason] = useSearchParam("endSeason");

    let usersQuery = useQuery({
        queryKey: ["members"],
        queryFn: async () => {
            try {
                return await userRepository.getUsersWithSeasonDates(startSeason, endSeason);
            }
            catch (error: any) {
                alert(loc(error.response.data.message));
                return null;
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
