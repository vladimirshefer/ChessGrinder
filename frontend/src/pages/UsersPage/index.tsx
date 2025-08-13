import React, {useMemo} from "react";
import {useQuery} from "@tanstack/react-query";
import {useLoc} from "strings/loc";
import useSearchParam from "lib/react/hooks/useSearchParam";
import userRepository from "lib/api/repository/UserRepository";
import MemberList from "pages/MainPage/MemberList";

export default function UsersPage() {
    let loc = useLoc();
    const [globalScoreFromDate] = useSearchParam("startSeason");
    const [globalScoreToDate] = useSearchParam("endSeason");
    const [sort] = useSearchParam("sort");

    let usersQuery = useQuery({
        queryKey: ["members"],
        queryFn: async () => {
            try {
                if (!globalScoreFromDate && !globalScoreToDate) {
                    return await userRepository.getUsers();
                }
                return await userRepository.getUsers(0, globalScoreFromDate || undefined, globalScoreToDate || undefined);
            }
            catch (error: any) {
                alert(loc(error.response.data.message));
                return null;
            }
        },
    })

    let users = useMemo(() => {
        let list = usersQuery.data?.values;

        if (sort === "reputation") {
            list = list?.sort((a, b) => (b.reputation || 0) - (a.reputation || 0));
        }
        if (sort === "score") {
            list = list?.sort((a, b) => (b.globalScore || 0) - (a.globalScore || 0));
        }
        if (sort === "rating") {
            list = list?.sort((a, b) => (b.eloPoints || 0) - (a.eloPoints || 0));
        }
        return list;
    }, [usersQuery.data, sort]);

    if (!users) {
        return <>Loading...</>
    }
    return <div className={"p-2"}>
        <MemberList users={users}/>
    </div>
}
