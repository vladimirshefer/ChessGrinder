import React, {useMemo} from "react";
import {useQuery} from "@tanstack/react-query";
import {useLoc} from "strings/loc";
import useSearchParam from "lib/react/hooks/useSearchParam";
import userRepository from "lib/api/repository/UserRepository";
import MemberList from "pages/MainPage/MemberList";

export default function UsersPage() {
    let loc = useLoc();
    const [sort] = useSearchParam("sort");
    const [city] = useSearchParam("city");

    let usersQuery = useQuery({
        queryKey: ["members", city],
        queryFn: async () => {
            try {
                return await userRepository.getUsers(undefined, city ?? undefined);
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
