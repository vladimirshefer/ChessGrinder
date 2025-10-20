import {Link, useNavigate, useParams} from "react-router-dom";
import participantRepository from "lib/api/repository/ParticipantRepository";
import {useQuery} from "@tanstack/react-query";
import userRepository from "lib/api/repository/UserRepository";
import {Conditional, usePermissionGranted} from "components/Conditional";
import {AiOutlineArrowLeft, AiOutlineDelete, AiOutlineEdit} from "react-icons/ai";
import React, {useMemo} from "react";
import {useLoc} from "strings/loc";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import UserPane from "pages/MainPage/UserPane";
import MatchesTable from "pages/TournamentPage/MatchesTable";
import tournamentPageRepository from "lib/api/repository/TournamentPageRepository";
import {TournamentPageData} from "lib/api/dto/TournamentPageData";
import {usePageTitle} from "lib/react/hooks/usePageTitle";
import useDropdownControls from "lib/react/hooks/useDropdownControls";
import {BsToggleOff, BsToggleOn} from "react-icons/bs";
import {MdOutlineMoreVert} from "react-icons/md";
import {useAuthenticatedUser} from "contexts/AuthenticatedUserContext";

export default function ParticipantPage() {
    let navigate = useNavigate()
    let loc = useLoc();

    let [showDropdown, setShowDropdown, dropdownRef] = useDropdownControls()
    let [authenticatedUser] = useAuthenticatedUser()

    let {
        tournamentId,
        participantId,
    } = useParams()

    let participantQuery = useQuery({
        queryKey: ["participant", participantId],
        queryFn: async () => {
            if (!participantId) return null;
            return await participantRepository.getParticipant(tournamentId!, participantId)
        }
    })
    usePageTitle(` ${participantQuery.data?.name || loc("Nickname")} - ${participantQuery.data?.userFullName || loc("Guest")} - ${loc("Participant")} - ChessGrinder`, [participantQuery.data, loc])

    let userQuery = useQuery({
        queryKey: ["user", participantQuery.data?.userId],
        queryFn: async () => {
            if (!participantId || !participantQuery.isSuccess || !participantQuery.data?.userId) return null
            return await userRepository.getUser(participantQuery.data.userId)
        },
    })

    let tournamentQuery = useQuery({
        queryKey: ["tournament", tournamentId],
        queryFn: async () => {
            if (!tournamentId) return null
            return await tournamentRepository.getTournament(tournamentId)
        }
    })

    let tournamentPageQuery = useQuery({
        queryKey: ["tournamentPageData", tournamentId],
        queryFn: () => tournamentId ? tournamentPageRepository.getData(tournamentId) : Promise.reject<TournamentPageData>(new Error())
    })

    let isMeModerator = usePermissionGranted(tournamentId || "", "TournamentEntity", "MODERATOR");
    let isMeOwner = usePermissionGranted(tournamentId || "", "TournamentEntity", "OWNER");
    let isMeParticipant = !!userQuery?.data?.id && userQuery?.data?.id === authenticatedUser?.id;
    let matches = useMemo(
        () => {
            if (!tournamentPageQuery.isSuccess) {
                return []
            }
            return tournamentPageQuery.data!.rounds
                .flatMap(it => it.matches)
                .filter(it => it.white?.id === participantId || it.black?.id === participantId)
        }, [tournamentPageQuery, participantId]
    )

    if (tournamentQuery.isLoading) {
        return <>Loading tournament</>
    }

    if (!tournamentId && tournamentQuery.isError) {
        return <>
            No tournament
            <Link to={`/`} className={"btn-light"}>Home</Link>
        </>
    }

    if (!participantId || participantQuery.isError || userQuery.isError) {
        return <>
            No such user
            <Link to={`/tournament/${tournamentId}`}>
                <button className={"p-2 bg-gray-200 rounded-md mx-1"}>
                    Back
                </button>
            </Link>
        </>
    }

    async function changeNickname() {
        let currentName = participantQuery.data?.name || "";
        let nextName = window.prompt("Enter new nickname", currentName);
        if (nextName === null) {
            return;
        }
        nextName = nextName.trim();
        if (!nextName || nextName === currentName) {
            return;
        }
        await participantRepository.updateParticipant(tournamentId!, {
            id: participantQuery.data?.id || "",
            name: nextName,
            userId: "",
            score: 0,
            userFullName: "",
            buchholz: 0,
            isMissing: false,
            place: -1
        })
            .catch((e) => alert("Could not change nickname! " + e?.response?.data?.message))
        await participantQuery.refetch()
    }

    if (!participantQuery.data) {
        return <>No participant</>
    }
    let participant = participantQuery.data;

    let missing = participant.isMissing || false;
    let moderator = participant.isModerator || false;

    async function deleteParticipant() {
        let participantFriendlyName = userQuery.data?.name || participantQuery.data?.name || participantId;
        let tournamentFriendlyName = tournamentQuery.data?.name || tournamentId;
        if (!window.confirm(`Do you want to delete user ${participantFriendlyName} from tournament ${tournamentFriendlyName}?`)) {
            return;
        }
        if (tournamentQuery.data?.status !== "PLANNED") {
            if (!window.confirm("Tournament already started!\n" +
                "Deleting the participant could break the pairing engine!\n" +
                "Consider \"Missing\" instead.\n" +
                "Are you sure?")) {
                return
            }
        }
        await participantRepository.deleteParticipant(tournamentId!, participantId!)
        navigate(`/tournament/${tournamentId}`)
    }

    async function toggleMissing() {
        if (missing) {
            await participantRepository.unmissParticipant(tournamentId!, participantId!)
        } else {
            await participantRepository.missParticipant(tournamentId!, participantId!)
        }
        await participantQuery.refetch()
    }

    async function toggleIsModerator() {
        await participantRepository.updateParticipant(tournamentId!, {
            id: participantId,
            isModerator: !moderator
        })
        await participantQuery.refetch()
    }

    return <div className={"p-3 text-left"}>
        <div className={"py-3 grid gap-3"}>
            <Conditional on={participantQuery.isSuccess}>
                <Link to={"/tournament/" + tournamentId} className={"text-xl"} title={loc("Back")} replace={true}>
                    <span className={"flex items-center gap-2"}>
                        <AiOutlineArrowLeft/>
                        {"Back to tournament"}
                    </span>
                </Link>
                <div className={"text-xl flex gap-2 items-center"}>
                    <h1 className={"grow font-bold text-ellipsis line-clamp-5 overflow-hidden break-all"}
                        title={participantQuery.data?.name}>
                        {participantQuery.data?.name}
                    </h1>
                    <div className={"relative"}>
                        <button onClick={() => setShowDropdown(!showDropdown)} className={"flex items-center"}>
                            <MdOutlineMoreVert/>
                        </button>
                        {showDropdown ? (
                            <ul ref={dropdownRef}
                                className={"absolute grid right-0 top-full bg-white border border-gray-300 shadow-lg z-20 min-w-max text-sm place-items-stretch"}
                            >
                                <Conditional on={isMeModerator || (isMeParticipant)}>
                                    <li>
                                        <button
                                            className={"flex gap-2 items-center px-3 py-2 hover:bg-gray-100 w-full"}
                                            type={"button"}
                                            onClick={changeNickname}
                                            disabled={!participantQuery.data}
                                        >
                                            <AiOutlineEdit/>
                                            {loc("Change nickname")}
                                        </button>
                                    </li>
                                </Conditional>
                                <Conditional on={isMeModerator}>
                                    <li>
                                        <button
                                            className={"flex gap-2 items-center px-3 py-2 hover:bg-gray-100 w-full"}
                                            type={"button"}
                                            onClick={toggleMissing}
                                            title={"Set missing"}
                                            disabled={!participantQuery.data}
                                        >
                                            {missing ? <BsToggleOn/> : <BsToggleOff/>}
                                            {loc("Missing")}
                                        </button>
                                    </li>
                                    <Conditional on={isMeOwner}>
                                        <li>
                                            <button
                                                className={"flex gap-2 items-center px-3 py-2 hover:bg-gray-100 w-full"}
                                                type={"button"}
                                                onClick={toggleIsModerator}
                                                title={"Set moderator"}
                                                disabled={!participantQuery.data}
                                            >
                                                {moderator ? <BsToggleOn/> : <BsToggleOff/>}
                                                {loc("Moderator")}
                                            </button>
                                        </li>
                                    </Conditional>
                                    <li>
                                        <button
                                            className={"flex gap-2 items-center px-3 py-2 bg-danger-100 w-full hover:bg-danger-300 text-danger-800"}
                                            title={loc("Delete")}
                                            onClick={deleteParticipant}
                                        >
                                            <AiOutlineDelete/>
                                            {loc("Delete participant")}
                                        </button>
                                    </li>
                                </Conditional>

                            </ul>
                        ) : <></>}
                    </div>
                </div>
            </Conditional>

            <Conditional on={userQuery.isSuccess && userQuery.data}>
                <div className={"py-3"}>
                    <UserPane user={userQuery.data!}/>
                </div>
            </Conditional>
            {userQuery.isLoading ? <>Loading user data</> : null}
            {userQuery.isError ? <>Error loading user data</> : null}

            {!!matches && matches.length > 0 &&
                <div>
                    <MatchesTable
                        matches={matches}
                        canEditResults={false}
                        submitMatchResult={() => {
                        }}
                    />
                </div>
            }
        </div>
    </div>
}
