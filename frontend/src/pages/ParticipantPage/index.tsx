import {Link, useNavigate, useParams} from "react-router-dom";
import participantRepository from "lib/api/repository/ParticipantRepository";
import {useQuery} from "@tanstack/react-query";
import userRepository from "lib/api/repository/UserRepository";

export default function ParticipantPage() {
    let navigate = useNavigate()

    let {
        tournamentId, userId
    } = useParams()

    let userQuery = useQuery({
        queryKey: ["participantUser", userId],
        queryFn: async () => {
            if (!userId) return null
            return await userRepository.getUser(userId!!)
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

    if (!userId || userQuery.isError) {
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
            {
                userQuery.isSuccess ? <>
                    {userQuery.data?.name}
                </> : null
            }
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
                        if (window.confirm(`Do you want to delete user ${userId} from tournament ${tournamentId}?`)) {
                            await participantRepository.deleteParticipant(tournamentId!!, userId!!)
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
