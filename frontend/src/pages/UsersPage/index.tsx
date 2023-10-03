import React, {Fragment} from "react";
import {Link} from "react-router-dom";
import {useQuery} from "@tanstack/react-query";
import userRepository from "lib/api/repository/UserRepository";

export default function UsersPage() {

    let usersQuery = useQuery({
        queryKey: ["members"],
        queryFn: async () => {
            return await userRepository.getUsers()
        },
    })

    return <>
        Members page

        <ul>
            {usersQuery?.data?.values?.map(member => {
                return <li key={member.name}>
                    <div className={"col-span-6"}>
                        <Link to={`/user/${member.id}`}>
                            {member.name}
                        </Link>
                    </div>
                    <div className={"col-span-6"}>
                        {
                            (member.badges || []).map(badge => {
                                return <span
                                    key={badge.imageUrl}
                                    title={badge.description}
                                    className={"cursor-default"}
                                >{badge.imageUrl}</span>
                            })
                        }
                    </div>
                </li>
            })}
        </ul>
    </>
}
