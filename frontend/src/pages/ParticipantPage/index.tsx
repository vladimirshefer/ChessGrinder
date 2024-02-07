import {Link, useNavigate, useParams} from "react-router-dom";
import participantRepository from "lib/api/repository/ParticipantRepository";
import {useQuery} from "@tanstack/react-query";
import userRepository from "lib/api/repository/UserRepository";
import ConditionalOnUserRole, {Conditional} from "components/Conditional";
import {UserRoles} from "lib/api/dto/MainPageData";
import {useForm} from "react-hook-form";
import {propagate} from "lib/util/misc";

export default function ParticipantPage() {
    let navigate = useNavigate()
    let editForm = useForm()

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
        queryKey: ["participantUser", participantQuery.data],
        queryFn: async () => {
            if (!participantId || !participantQuery.isSuccess || !participantQuery.data?.userId) return null
            return await userRepository.getUser(participantQuery.data?.userId!!)
        },
    })

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

    async function changeNickname(data: { [key: string]: string}) {
        await participantRepository.updateParticipant(tournamentId!!, {
            id: participantQuery.data?.id || "",
            name: data["nickname"],
            userId: "",
            score: 0,
            userFullName: "",
            buchholz: 0,
            isMissing: false,
        })
            .catch(propagate(() => "Could not change nickname!"))
        await participantQuery.refetch()
    }

    return <div className={"p-3"}>
        <div className={"p-3"}>
            <Conditional on={participantQuery.isSuccess}>
                <h1 className={"text-xl font-bold"}>{participantQuery.data?.name}</h1>
            </Conditional>
            <Conditional on={userQuery.isSuccess}>
                <h2>{userQuery.data?.name}</h2>
            </Conditional>
            {
                userQuery.isLoading ? <>Loading user data</> : null
            }
        </div>

        <div></div>

        <ConditionalOnUserRole role={UserRoles.ADMIN}>
            <form onSubmit={editForm.handleSubmit(changeNickname)} className={"grid p-2 gap-2 border my-2"}>
                <h3 className={"uppercase text-start"}>
                    {"Change nickname"}
                </h3>
                <div className={"flex gap-2"}>
                    <input
                        className={"input-text grow"}
                        type={"text"}
                        defaultValue={participantQuery.data?.name || ""}
                        placeholder={"Nickname"}
                        {...editForm.register("nickname")}
                    />
                    <button className={"btn-light"} type={"submit"}>Save</button>
                </div>
            </form>
        </ConditionalOnUserRole>

        <div className={"flex gap-2 justify-end"}>
            <Link to={`/tournament/${tournamentId}`}>
                <button className={"btn-light"}>
                    Back
                </button>
            </Link>
            <ConditionalOnUserRole role={UserRoles.ADMIN}>
                <button className={"btn-danger"}
                        onClick={async () => {
                            if (window.confirm(`Do you want to delete user ${participantId} from tournament ${tournamentId}?`)) {
                                await participantRepository.deleteParticipant(tournamentId!!, participantId!!)
                                navigate(`/tournament/${tournamentId}`)
                            }
                        }}
                >Delete
                </button>
                <Conditional on={participantQuery.data && !participantQuery.data?.isMissing}>
                    <button className={"btn-light"}
                            onClick={async () => {
                                await participantRepository.missParticipant(tournamentId!!, participantId!!)
                                await participantQuery.refetch()
                            }}
                    >
                        Missing on tournament
                    </button>
                </Conditional>
                <Conditional on={participantQuery.data && participantQuery.data?.isMissing}>
                    <button className={"btn-light"}
                            onClick={async () => {
                                await participantRepository.unmissParticipant(tournamentId!!, participantId!!)
                                await participantQuery.refetch()
                            }}
                    >
                        Returned to tournament
                    </button>
                </Conditional>
            </ConditionalOnUserRole>
        </div>
    </div>
}
