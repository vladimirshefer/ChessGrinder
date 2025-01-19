import {Link, useNavigate, useParams} from "react-router-dom";
import participantRepository from "lib/api/repository/ParticipantRepository";
import {useQuery} from "@tanstack/react-query";
import userRepository from "lib/api/repository/UserRepository";
import {Conditional, usePermissionGranted} from "components/Conditional";
import {useForm} from "react-hook-form";
import {propagate} from "lib/util/misc";
import {AiOutlineArrowLeft, AiOutlineDelete} from "react-icons/ai";
import React from "react";
import {useLoc} from "strings/loc";
import Toggle from "components/Toggle";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import UserPane from "pages/MainPage/UserPane";

export default function ParticipantPage() {
    let navigate = useNavigate()
    let editForm = useForm()
    let loc = useLoc();

    let {
        tournamentId,
        participantId,
    } = useParams()

    let participantQuery = useQuery({
        queryKey: ["participant", participantId],
        queryFn: async () => {
            if (!participantId) return null;
            return await participantRepository.getParticipant(tournamentId!!, participantId)
        }
    })

    let userQuery = useQuery({
        queryKey: ["user", participantQuery.data?.userId],
        queryFn: async () => {
            if (!participantId || !participantQuery.isSuccess || !participantQuery.data?.userId) return null
            return await userRepository.getUser(participantQuery.data?.userId!!)
        },
    })

    let tournamentQuery = useQuery({
        queryKey: ["tournament", tournamentId],
        queryFn: async () => {
            if (!tournamentId) return null
            return await tournamentRepository.getTournament(tournamentId)
        }
    })

    let isMeModerator = usePermissionGranted(tournamentId || "", "TournamentEntity", "MODERATOR");
    let isMeOwner = usePermissionGranted(tournamentId || "", "TournamentEntity", "OWNER");

    if (!tournamentId) {
        return <>
            No tournamentId or userId

            <Link to={`/`}>
                <button className={"p-2 bg-gray-200 rounded-md mx-1"}>
                    Home
                </button>
            </Link>
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

    async function changeNickname(data: { [key: string]: string }) {
        await participantRepository.updateParticipant(tournamentId!!, {
            id: participantQuery.data?.id || "",
            name: data["nickname"],
            userId: "",
            score: 0,
            userFullName: "",
            buchholz: 0,
            isMissing: false,
            place: -1
        })
            .catch(propagate(() => "Could not change nickname!"))
        await participantQuery.refetch()
    }

    let missing = (participantQuery.data && participantQuery.data?.isMissing) || false;
    let moderator = (participantQuery.data && participantQuery.data?.isModerator) || false;

    return <div className={"p-3 text-left"}>
        <div className={"py-3"}>
            <Conditional on={participantQuery.isSuccess}>
                <div className={"flex items-center gap-2"}>
                    <Link to={"/tournament/" + tournamentId} className={"text-xl"} title={loc("Back")}>
                        <AiOutlineArrowLeft/>
                    </Link>
                    <h1 className={"text-xl font-bold flex gap-2"}>
                        {participantQuery.data?.name}
                    </h1>
                </div>
            </Conditional>
            <Conditional on={userQuery.isSuccess && userQuery.data}>
                <div className={"py-3"}>
                    <UserPane user={userQuery.data!!}/>
                </div>
            </Conditional>
            {userQuery.isLoading ? <>Loading user data</> : null}
            {userQuery.isError ? <>Error loading user data</> : null}
        </div>
        <div></div>
        <Conditional on={isMeModerator}>
            <div className={"grid border-y-2 py-2 grid-cols-[auto_1fr] gap-3 items-center"}>
                <h3 className={"col-span-2 text-lg font-semibold"}>{loc("Admin")}</h3>
                <span>{"Change nickname"}</span>
                <form onSubmit={editForm.handleSubmit(changeNickname)}>
                    <input
                        className={"input-text w-full"}
                        type={"text"}
                        defaultValue={participantQuery.data?.name || ""}
                        placeholder={loc("Nickname")}
                        {...editForm.register("nickname")}
                    />
                    <input type="submit" hidden/>
                </form>

                <span>{loc("Missing")}</span>
                <Toggle
                    title={"Set missing"}
                    checked={missing}
                    setChecked={async (v) => {
                        if (missing) {
                            await participantRepository.unmissParticipant(tournamentId!!, participantId!!)
                        } else {
                            await participantRepository.missParticipant(tournamentId!!, participantId!!)
                        }
                        await participantQuery.refetch()
                    }}
                />
                <Conditional on={isMeOwner}>
                    <span className="">{loc("Moderator")}</span>
                    <Toggle
                        title={"Set moderator"}
                        checked={moderator}
                        setChecked={async (v) => {
                            await participantRepository.updateParticipant(tournamentId!!, {
                                id: participantId,
                                isModerator: !moderator
                            })
                            await participantQuery.refetch()
                        }}
                    />
                </Conditional>

                <span>{loc("Delete participant")}</span>
                <div>
                    <button className={"btn-danger flex gap-1 items-center"}
                            title={loc("Delete")}
                            onClick={async () => {
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
                                await participantRepository.deleteParticipant(tournamentId!!, participantId!!)
                                navigate(`/tournament/${tournamentId}`)
                            }}
                    >
                        <span><AiOutlineDelete/></span>
                        {loc("Delete")}
                    </button>
                </div>
            </div>
        </Conditional>

    </div>
}
