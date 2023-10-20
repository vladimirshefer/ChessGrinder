import {Link, useNavigate, useParams, useSearchParams} from "react-router-dom";
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
import useSearchParam from "lib/react/hooks/useSearchParam";

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

    return <div className={"max-w-full"}>
        <button className={"btn bg-gray-200"} onClick={() => {
            setSelectActive(!selectActive);
            setSelectedBadge(undefined)
        }}>
            {loc("Assign achievement")}
        </button>
        <div className={""}>
            <Conditional on={!!selectedBadge}>{() => <>
                <div className={"flex gap-2 p-2 text-left bg-gray-100"}>
                    <div className={"min-w-[50px]"}>
                        <Gravatar
                            text={selectedBadge!!.title}
                            type={GravatarType.Identicon}
                            size={50}
                            className={"rounded-full"}
                        />
                    </div>
                    <div className={"grid "}>
                        <span>{selectedBadge!!.title || "No title"}</span>
                        <span
                            className={"text-sm text-gray-600 line-clamp-3"}>{selectedBadge!!.description || "..."}</span>
                    </div>
                </div>
                <button className={"btn-dark"}
                        onClick={() => assignAchievement(selectedBadge!!)}
                >
                    Assign
                </button>
            </>}</Conditional>
        </div>
        <Conditional on={selectActive && badgesQuery.isSuccess}>
            <div className={"border"}>
                {
                    badgesQuery.data?.values?.map(badge => {
                        return <div className={"flex gap-2 p-2 bg-gray-100 m-1"}
                                    key={badge.id}
                                    onClick={() => {
                                        setSelectedBadge(badge)
                                        setSelectActive(false)
                                    }}
                        >
                            <div className={"min-w-[50px]"}>
                                <Gravatar
                                    text={badge!!.title}
                                    type={GravatarType.Identicon}
                                    size={50}
                                    className={"rounded-full"}
                                />
                            </div>
                            <div className={"grid text-left"}>
                                <span className={"uppercase"}>{badge!!.title || "No title"}</span>
                                <span
                                    className={"text-sm text-gray-600 line-clamp-3"}>{badge!!.description || "..."}</span>
                            </div>
                        </div>
                    }) || []
                }
            </div>
        </Conditional>
    </div>;
}

export default function UserProfilePage() {
    let {username} = useParams()
    let navigate = useNavigate()
    let loc = useLoc()
    let authData = useAuthData()
    let [activeTab, setActiveTab] = useSearchParam("tab")

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
        <div className={"p-3"}></div>
        <div className={"flex justify-between border-b-2 border-gray-400"}>
            <button
                className={`uppercase font-semibold px-2 py-1 ${activeTab === "history" ? "text-primary" : " "}`}
                onClick={() => setActiveTab("history")}>History
            </button>
            <button
                className={`uppercase font-semibold px-2 py-1 ${activeTab === "achievements" ? "text-primary" : " "}`}
                onClick={() => setActiveTab("achievements")}>Achievements
            </button>
            <button
                className={`uppercase font-semibold px-2 py-1 ${activeTab === "admin" ? "text-primary" : " "}`}
                onClick={() => setActiveTab("admin")}>Admin
            </button>
        </div>
        <Conditional on={activeTab === "history" || !activeTab}>
            <>No history</>
        </Conditional>
        <Conditional on={activeTab === "achievements"}>
            <div className={"grid gap-2 py-2"}>
                {
                    userProfile.badges.map(badge => {
                        return <Link to={`/badge/${badge.id}`} key={badge.id}>
                            <div className={"flex gap-2"} title={badge.description}>
                                <Gravatar
                                    text={badge.title}
                                    type={GravatarType.Identicon}
                                    size={50}
                                    className={"rounded-full min-w-[50px] max-h-[50px]"}
                                />
                                <div className={"text-left"}>
                                    <span className={"uppercase font-semibold"}>{badge.title}</span>
                                    <span className={"text-sm text-gray-600 line-clamp-2"} title={badge.description}>{badge.description}</span>
                                </div>
                            </div>
                        </Link>
                    }) || <span>No achievements</span>
                }
            </div>
        </Conditional>
        <Conditional on={activeTab === "admin"}>
            <ConditionalOnUserRole role={UserRoles.ADMIN}>
                <AssignAchievementPane assignAchievement={async (badge) => {
                    await badgeRepository.assignBadge(badge.id, userProfile!!.id);
                    await refetch()
                }}/>
            </ConditionalOnUserRole>
        </Conditional>
    </div>
}
