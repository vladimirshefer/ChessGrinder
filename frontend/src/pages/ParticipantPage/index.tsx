import {Link, useNavigate, useParams} from "react-router-dom";
import participantRepository from "lib/api/repository/ParticipantRepository";
import {useQuery} from "@tanstack/react-query";
import userRepository from "lib/api/repository/UserRepository";
import {Conditional} from "components/Conditional";

export default function ParticipantPage() {
    let navigate = useNavigate()

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

    return <>
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
        <div>
            <Link to={`/tournament/${tournamentId}`}>
                <button className={"p-2 bg-gray-200 rounded-md mx-1"}>
                    Back
                </button>
            </Link>
            <button className={"p-2 bg-red-200 rounded-md mx-1"}
                    onClick={async () => {
                        if (window.confirm(`Do you want to delete user ${participantId} from tournament ${tournamentId}?`)) {
                            await participantRepository.deleteParticipant(tournamentId!!, participantId!!)
                            navigate(`/tournament/${tournamentId}`)
                        }
                    }}
            >Delete
            </button>
            <button  className={"p-2 bg-gray-200 rounded-md mx-1"}
            >Missing (TODO)</button>
        </div>
    </>
}
