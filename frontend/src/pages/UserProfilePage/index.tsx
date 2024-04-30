import {Link, useNavigate, useParams} from "react-router-dom";
import React, {useEffect, useMemo, useState} from "react";
import {useQuery} from "@tanstack/react-query";
import userRepository from "lib/api/repository/UserRepository";
import loginPageRepository from "lib/api/repository/LoginPageRepository";
import {
    BadgeDto,
    DEFAULT_DATETIME_FORMAT,
    UserDto,
    UserReputationHistoryRecordDto,
    UserRoles
} from "lib/api/dto/MainPageData";
import ConditionalOnUserRole, {Conditional} from "components/Conditional";
import {useLoc} from "strings/loc";
import badgeRepository from "lib/api/repository/BadgeRepository";
import Gravatar, {GravatarType} from "components/Gravatar";
import {AiOutlineTrophy} from "react-icons/ai";
import {BiSolidChess} from "react-icons/bi";
import {BsPencilFill} from "react-icons/bs";
import {FiLogOut} from "react-icons/fi";
import useSearchParam from "lib/react/hooks/useSearchParam";
import DropdownSelect from "components/DropdownSelect";
import {useAuthenticatedUser} from "contexts/AuthenticatedUserContext";
import {useForm} from "react-hook-form";
import {propagate} from "lib/util/misc";
import {FaRegHeart} from "react-icons/fa";
import dayjs from "dayjs";

function AssignAchievementPane(
    {
        assignAchievement,
    }: {
        assignAchievement: (badge: BadgeDto) => Promise<void>
    }
) {
    let loc = useLoc()
    let badgesQuery = useQuery({
        queryKey: ["badgesSelect"],
        queryFn: async () => {
            return await badgeRepository.getBadges();
        },
    })

    let [selectedBadge, setSelectedBadge] = useState<BadgeDto>()

    return <div className={"max-w-full bg-inherit"}>
        <div className={"flex gap-2 items-center text-left"}>
            <h3 className={"uppercase"}>{loc("Assign achievement")}</h3>
        </div>
        <DropdownSelect<BadgeDto>
            values={badgesQuery.data?.values || []}
            className={"border-b bg-inherit"}
            onSelect={badge => setSelectedBadge(badge)}
            keyExtractor={badge => badge.id}
            matchesSearch={(searchQuery, badge) =>
                badge.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
                badge.description.toLowerCase().includes(searchQuery.toLowerCase())
            }
            emptyPresenter={() =>
                <div className={"bg-inherit text-left py-2"}>Select...</div>
            }
            presenter={badge =>
                <div className={"flex gap-2 p-2 bg-inherit"} title={badge.description}>
                    <Gravatar
                        text={badge.title}
                        type={GravatarType.Identicon}
                        size={50}
                        className={"rounded-full min-w-[50px] max-h-[50px]"}
                    />
                    <div className={"text-left bg-inherit"}>
                        <span className={"uppercase font-semibold"}>{badge.title}</span>
                        <span className={"text-sm text-gray-600 line-clamp-2"}
                              title={badge.description}>{badge.description}</span>
                    </div>
                </div>
            }
        />
        <div>
            <Conditional on={!!selectedBadge}>
                <button className={"btn-dark w-full"}
                        onClick={async () => {
                            await assignAchievement(selectedBadge!!)
                                .then(() => alert("Badge assigned"))
                                .catch(() => alert("Could not assign badge!"))
                        }}
                >Assign
                </button>
            </Conditional>
        </div>
    </div>;
}

function AssignReputationPane(
    {
        assignReputation,
        userId,
    }: {
        assignReputation: (r: UserReputationHistoryRecordDto) => void,
        userId: string,
    }) {
    let loc = useLoc()
    const {register, handleSubmit} = useForm();

    async function saveTournament(data: { [key: string]: any }) {
        let amount = data["amount"];
        if (isNaN(amount)) {
            let message = `Amount should be a number, but was '${amount}'`;
            alert(message)
            throw new Error(message);
        }
        let comment = data["comment"];
        if (typeof comment !== "string") {
            let message = `Comment should be a non-empty string, but was '${comment}'`;
            alert(message)
            throw new Error(message);
        }
        assignReputation({
            amount: amount,
            comment: comment,
            userId: userId,
        })
    }

    return <div>
        <form className={"grid gap-1"} onSubmit={handleSubmit(saveTournament)}>
            <div className={"flex gap-2 items-center text-left"}>
                <h3 className={"uppercase"}>{loc("Add reputation")}</h3>
            </div>
            <input type={"number"} placeholder={"Reputation amount"} {...register("amount")}/>
            <input type={"text"} placeholder={"Comment"} {...register("comment")}/>
            <button className={"btn-light uppercase"}
                    type={"submit"}
            >Submit
            </button>
        </form>
    </div>;
}

export default function UserProfilePage() {
    let {username} = useParams()
    let navigate = useNavigate()
    let loc = useLoc()
    let [authenticatedUser, authenticatedUserReload] = useAuthenticatedUser()
    let [activeTab, setActiveTab] = useSearchParam("tab", "history")

    useEffect(() => {
        if (!username) {
            let currentUserId = authenticatedUser?.id;
            if (currentUserId) {
                navigate(`/user/${currentUserId}`)
            }
        }
    }, [username, authenticatedUser, navigate])

    let isMyProfile = useMemo(() => {
        return !!username && username === authenticatedUser?.id
    }, [username, authenticatedUser])

    let {data: userProfile, refetch} = useQuery({
        queryKey: ["profile", username],
        queryFn: () => {
            return username ? userRepository.getUser(username) : Promise.reject<UserDto>()
        },
    })

    let historyQuery = useQuery({
        queryKey: ["history", userProfile, activeTab],
        queryFn: async () => {
            if (!userProfile) return null;
            return userRepository.getHistory(userProfile.id)
        },
        // enabled: activeTab === "history"
    })

    async function logout() {
        await loginPageRepository.signOut()
        await authenticatedUserReload()
    }

    if (!userProfile) {
        return <>No such user</>
    }

    let totalPoints = userProfile.totalPoints;

    return <div className={"grid p-4"}>
        <div className={"flex gap-1 items-center"}>
            <span className={"text-left uppercase grow"}>
                {userProfile.roles?.includes(UserRoles.ADMIN) ? loc("Administrator") : loc("User")}
            </span>
            <Conditional on={isMyProfile}>
                <Link to={"/user/me/edit"} className={"p-2"} title={loc("Edit profile")}><BsPencilFill/></Link>
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
                <ConditionalOnUserRole role={UserRoles.ADMIN}>
                    <span className={"text-sm text-gray-500"}>
                         {userProfile.username}
                    </span>
                </ConditionalOnUserRole>
                <div className={"flex font-semibold gap-4 items-center"}>
                    <div className={"flex gap-1 items-center"} title={loc("Tournaments")}>
                        <BiSolidChess/>
                        <span>{historyQuery.data?.count || "?"}</span>
                    </div>
                    <div className={"flex gap-1 items-center"} title={loc("Points")}>
                        <AiOutlineTrophy/>
                        <span>{totalPoints}</span>
                    </div>
                    <div className={"flex gap-1 items-center"} title={loc("Reputation")}>
                        <FaRegHeart/>
                        <span>{userProfile.reputation || 0}</span>
                    </div>
                </div>
            </div>
        </div>
        <div className={"p-3"}></div>
        <div className={"flex justify-between border-b-2 border-gray-400 mb-3"}>
            <button
                className={`uppercase font-semibold px-2 py-1 ${activeTab === "history" ? "text-primary" : " "}`}
                onClick={() => setActiveTab("history")}>{loc("History")}
            </button>
            <button
                className={`uppercase font-semibold px-2 py-1 ${activeTab === "achievements" ? "text-primary" : " "}`}
                onClick={() => setActiveTab("achievements")}>{loc("Achievements")}
            </button>
            <ConditionalOnUserRole role={UserRoles.ADMIN}>
                <button
                    className={`uppercase font-semibold px-2 py-1 ${activeTab === "admin" ? "text-primary" : " "}`}
                    onClick={() => setActiveTab("admin")}>{loc("Admin")}
                </button>
            </ConditionalOnUserRole>
        </div>
        <Conditional on={activeTab === "history"}>
            <div className={"text-sm"}>
                <div className="p-1"></div>
                <div className={"grid grid-cols-12 text-left font-semibold border-b-2"}>
                    <span
                        className={"col-span-8"}>{loc("Tournaments") + (historyQuery?.isSuccess ? ` (${historyQuery.data?.count})` : "")}</span>
                    <span className={"col-span-2"}>{loc("Place")}</span>
                    <span className={"col-span-2"}>{loc("Points")}</span>
                </div>
                <div>
                    {historyQuery.isSuccess ? (
                        <div className="grid grid-cols-12">
                            {historyQuery.data?.values?.map(row =>
                                <div key={row.tournament.id}
                                     className={"col-span-12 grid grid-cols-12 text-left border-b py-2"}>
                                    <Link to={`/tournament/${row.tournament.id}`} className={"grid col-span-8"}>
                                        {row.tournament.name || row.tournament.id}
                                        <span className={"text-xs text-gray-800"}>
                                            {dayjs(row.tournament.date, DEFAULT_DATETIME_FORMAT).format("DD.MM.YYYY")}
                                        </span>
                                    </Link>
                                    <span className={"col-span-2"}>{row.participant.place}</span>
                                    <span className={"col-span-2"}>{row.participant.score}</span>
                                </div>
                            )}
                        </div>
                    ) : (
                        <>Loading history...</>
                    )}
                </div>
            </div>
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
                                    <span className={"text-sm text-gray-600 line-clamp-2"}
                                          title={badge.description}>{badge.description}</span>
                                </div>
                            </div>
                        </Link>
                    }) || <span>No achievements</span>
                }
            </div>
        </Conditional>
        <Conditional on={activeTab === "admin"}>
            <ConditionalOnUserRole role={UserRoles.ADMIN}>
                <div className={"bg-white p-2 grid gap-4"}>
                    <AssignAchievementPane assignAchievement={async (badge) => {
                        await badgeRepository.assignBadge(badge.id, userProfile!!.id)
                            .catch(propagate(() => alert("Could not assign badge!")));
                        await refetch()
                    }}/>
                    <AssignReputationPane userId={userProfile.id} assignReputation={async (data) => {
                        await userRepository.assignReputation(data)
                            .catch(propagate(() => alert("Could not assign reputation")));
                        await refetch();
                    }}/>
                </div>
            </ConditionalOnUserRole>
        </Conditional>
    </div>
}
