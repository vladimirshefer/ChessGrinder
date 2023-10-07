import {useNavigate, useParams} from "react-router-dom";
import {useAuthData} from "lib/auth/AuthService";
import React, {useEffect, useMemo, useState} from "react";
import {useQuery} from "@tanstack/react-query";
import userRepository from "lib/api/repository/UserRepository";
import loginPageRepository from "lib/api/repository/LoginPageRepository";
import {BadgeDto, MemberDto, UserRoles} from "lib/api/dto/MainPageData";
import ConditionalOnUserRole, {Conditional} from "components/Conditional";
import loc from "strings/loc";
import badgeRepository from "lib/api/repository/BadgeRepository";
import Gravatar, {GravatarType} from "components/Gravatar";

function AssignAchievementPane(
    {
        userId
    }: {
        userId: string
    }
) {

    let [selectActive, setSelectActive] = useState(false)
    let badgesQuery = useQuery({
        queryKey: ["badgesSelect", selectActive],
        queryFn: async () => {
            return await badgeRepository.getBadges();
        },
    })

    let [selectedBadge, setSelectedBadge] = useState<BadgeDto>()

    return <div>
        <button className={"btn bg-gray-200"}
                onClick={() => setSelectActive(!selectActive)}
        >
            {loc("Assign achievement")}
        </button>
        <div className={"bg-green-100"}>
            <Conditional on={!!selectedBadge}>
                <p>{selectedBadge?.imageUrl}</p>
                <p>{selectedBadge?.title}</p>
                <p>{selectedBadge?.description}</p>
                <button className={"btn-dark"}
                        onClick={() => badgeRepository.assignBadge(selectedBadge!!.id, userId)}
                >
                    Assign
                </button>
            </Conditional>
        </div>
        <div className={"bg-cyan-100"}>
            <Conditional on={selectActive && badgesQuery.isSuccess}>
                {
                    badgesQuery.data?.values?.map(badge => {
                        return <div key={badge.id}
                                    onClick={() => {
                                        setSelectedBadge(badge)
                                        setSelectActive(false)
                                    }}
                        >
                            <p>{badge?.imageUrl}</p>
                            <p>{badge?.title}</p>
                            <p>{badge?.description}</p>
                        </div>
                    }) || []
                }
            </Conditional>
        </div>
    </div>;
}

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
            return username ? userRepository.getUser(username) : Promise.reject<MemberDto>()
        },
    })

    async function logout() {
        await loginPageRepository.logout()
    }

    if (!userProfile) {
        return <>No such user</>
    }

    return <div className={"grid"}>
        <h1 className={"text-xl font-bold"}>
            {userProfile.name || userProfile.username || userProfile.id || "Unknown"}
        </h1>
        <span className={"text-gray-500"}>
            @{userProfile.username}
        </span>
        <div>
            {
                userProfile.roles?.map(role => {
                    return <span key={role} className={"bg-red-300 rounded-full px-2 text-sm p-1"}>{role}</span>
                })
            }
        </div>
        <div className={"grid place-items-center"}>
            <Gravatar text={userProfile.username || userProfile.id} type={GravatarType.Robohash} size={300}/>
        </div>
        <div>
            <div>
                Badges
            </div>
            {
                userProfile.badges.map(badge => {
                    return <span
                        key={badge.id}
                        title={badge.description}
                        className={"cursor-default"}
                    >{badge.imageUrl}{badge.title}</span>
                }) || <span>No achievements</span>
            }
        </div>

        <div>
            {
                isMyProfile ? (
                    <div className={"p-5"}>
                        <button className={"bg-blue-200 rounded-full px-5 py-1"}
                                onClick={() => logout()}>
                            Logout
                        </button>
                    </div>
                ) : null
            }
        </div>

        <ConditionalOnUserRole role={UserRoles.ADMIN}>
            <AssignAchievementPane userId={userProfile.id}/>
        </ConditionalOnUserRole>

    </div>
}
