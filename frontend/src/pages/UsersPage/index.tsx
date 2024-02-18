import React from "react";
import {useQuery} from "@tanstack/react-query";
import userRepository from "lib/api/repository/UserRepository";
import MemberList from "pages/MainPage/MemberList";

export default function UsersPage() {

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
        <MemberList users={users}/>
    </div>
}
