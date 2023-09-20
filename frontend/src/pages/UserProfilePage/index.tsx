import {Link, useNavigate, useParams} from "react-router-dom";
import {useAuthData} from "lib/auth/AuthService";
import {useEffect, useMemo} from "react";
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

    if (!username) {
        let currentUserName = authData?.username;
        if (!currentUserName) {
            return <div>
                <div>You are not logged in.</div>
                <div>
                    <Link to={"/login"}>
                        <button>Login</button>
                    </Link>
                </div>
            </div>
        }

    }

    return <>
        User {userProfile?.name || userProfile?.id || "Unknown"}

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
