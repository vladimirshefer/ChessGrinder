import {useNavigate, useParams} from "react-router-dom";
import {useAuthData} from "lib/auth/AuthService";
import React, {useEffect, useMemo} from "react";
import {useQuery} from "@tanstack/react-query";
import userRepository from "lib/api/repository/UserRepository";
import loginPageRepository from "lib/api/repository/LoginPageRepository";

export default function UserProfilePage() {
    let {username} = useParams()
    let navigate = useNavigate()
    let authData = useAuthData()

    useEffect(() => {
        if (!username) {
            let currentUserName = authData?.username;
            if (currentUserName) {
                navigate(`/user/${currentUserName}`)
            }
        }
    }, [username, authData, navigate])

    let isMyProfile = useMemo(() => {
        return !!username && username === authData?.username
    }, [username, authData])

    let {data: userProfile} = useQuery({
        queryKey: ["profile", username],
        queryFn: () => {
            return userRepository.getUser(username!!)
        },
    })

    async function logout() {
        await loginPageRepository.logout()
    }

    if (!userProfile) {
        return <>No such user</>
    }

    return <>
        <h1>
            {userProfile.name || userProfile.username || userProfile.id || "Unknown"}
        </h1>
        <span>
            @{userProfile.username}
        </span>

        <div>
            Roles:
            {
                userProfile.roles?.map(role => {
                    return <span key={role}>{role}</span>
                })
            }
        </div>
        <div>
            Badges:
            {
                userProfile.badges.map(badge => {
                    return <span
                        key={badge.imageUrl}
                        title={badge.description}
                        className={"cursor-default"}
                    >{badge.imageUrl}</span>
                }) || <span>No achievements</span>
            }
        </div>

        {
            isMyProfile ? (
                <div>
                    <button className={"bg-blue-200 rounded-full px-3"}
                            onClick={() => logout()}>
                        Logout
                    </button>
                </div>
            ) : null
        }
    </>
}
