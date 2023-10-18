import {Link, useNavigate, useParams} from "react-router-dom";
import {useAuthData} from "lib/auth/AuthService";
import React, {useEffect, useMemo, useState} from "react";
import {useQuery} from "@tanstack/react-query";
import userRepository from "lib/api/repository/UserRepository";
import loginPageRepository from "lib/api/repository/LoginPageRepository";
import {BadgeDto, MemberDto, UserRoles} from "lib/api/dto/MainPageData";
import ConditionalOnUserRole, {Conditional} from "components/Conditional";
import {useLoc} from "strings/loc";
import badgeRepository from "lib/api/repository/BadgeRepository";
import Gravatar, {GravatarType} from "components/Gravatar";
import {AiOutlineTrophy} from "react-icons/ai";
import {BiSolidChess} from "react-icons/bi";
import {BsPencilFill} from "react-icons/bs";
import {FiLogOut} from "react-icons/fi";

function AssignAchievementPane(
    {
        assignAchievement,
    }: {
        assignAchievement: (badge: BadgeDto) => void
    }
) {
    let loc = useLoc()
    let [selectActive, setSelectActive] = useState(false)
    let badgesQuery = useQuery({
        queryKey: ["badgesSelect", selectActive],
        queryFn: async () => {
            return await badgeRepository.getBadges();
        },
    })

    let [selectedBadge, setSelectedBadge] = useState<BadgeDto>()

    return <div className={"pt-3"}>
        <button className={"btn bg-gray-200"}
                onClick={() => setSelectActive(!selectActive)}
        >
            {loc("Assign achievement")}
        </button>
        <div className={"bg-green-100"}>
            <Conditional on={!!selectedBadge}>{() => <>
                <div className={"flex gap-2 p-2"}>
                    <div>
                        <Gravatar
                            text={selectedBadge!!.title}
                            type={GravatarType.Identicon}
                            size={50}
                            className={"rounded-full"}
                        />
                    </div>
                    <div className={"grid"}>
                        <div>
                            <span>{selectedBadge!!.title || "No title"}</span>
                        </div>
                        <div>
                            <span>{selectedBadge!!.description || "..."}</span>
                        </div>
                    </div>
                </div>
                <button className={"btn-dark"}
                        onClick={() => assignAchievement(selectedBadge!!)}
                >
                    Assign
                </button>
            </>}</Conditional>
        </div>
        <div className={"bg-cyan-100"}>
            <Conditional on={selectActive && badgesQuery.isSuccess}>
                {
                    badgesQuery.data?.values?.map(badge => {
                        return <div className={"flex gap-2 p-2"}
                                    key={badge.id}
                                    onClick={() => {
                                        setSelectedBadge(badge)
                                        setSelectActive(false)
                                    }}
                        >
                            <div>
                                <Gravatar
                                    text={badge!!.title}
                                    type={GravatarType.Identicon}
                                    size={50}
                                    className={"rounded-full"}
                                />
                            </div>
                            <div className={"grid"}>
                                <div>
                                    <span>{badge!!.title || "No title"}</span>
                                </div>
                                <div>
                                    <span>{badge!!.description || "..."}</span>
                                </div>
                            </div>
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
    let loc = useLoc()
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

    let {data: userProfile, refetch} = useQuery({
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

    return <div className={"grid p-4"}>
        <div className={"flex gap-1 items-center"}>
            <span className={"text-left uppercase grow"}>
                {userProfile.roles?.includes(UserRoles.ADMIN) ? loc("Administrator") : loc("User")}
            </span>
            <Conditional on={isMyProfile}>
                <button className={"p-2"} title={"Edit profile"}><BsPencilFill/></button>
                <button className={"p-2"} title={"Logout"}
                        onClick={logout}><FiLogOut/></button>
            </Conditional>
        </div>
        <div className={"p-2"}/>
        <div className={"flex gap-2"}>
            <div>
                <Gravatar text={userProfile.username || userProfile.id} type={GravatarType.Robohash} size={100}
                          className={"rounded-full"}/>
            </div>
            <div className={"grid text-left"}>
                <h1 className={"font-semibold uppercase truncate"}
                    title={userProfile.name || userProfile.username || userProfile.id || "Unknown"}>
                    {userProfile.name || userProfile.username || userProfile.id || "Unknown"}
                </h1>
                <span className={"text-sm text-gray-500"}>
                     {userProfile.username}
                </span>
                <div className={"flex font-semibold gap-4 items-center"}>
                    <div className={"flex gap-1 items-center"}>
                        <AiOutlineTrophy/>
                        <span>356</span>
                    </div>
                    <div className={"flex gap-1 items-center"}>
                        <BiSolidChess/>
                        <span>97</span>
                    </div>
                </div>
            </div>
        </div>
        <div>
            <div>
                Badges
            </div>
            {
                userProfile.badges.map(badge => {
                    return <Link to={`/badge/${badge.id}`} key={badge.id}>
                        <div className={"flex gap-2"} title={badge.description}>
                            <Gravatar
                                text={badge.title}
                                type={GravatarType.Identicon}
                                size={25}
                                className={"rounded-full"}
                            />
                            <span>{badge.title}</span>
                        </div>
                    </Link>
                }) || <span>No achievements</span>
            }
        </div>
        <ConditionalOnUserRole role={UserRoles.ADMIN}>
            <AssignAchievementPane assignAchievement={async (badge) => {
                await badgeRepository.assignBadge(badge.id, userProfile!!.id);
                await refetch()
            }}/>
        </ConditionalOnUserRole>
    </div>
}
