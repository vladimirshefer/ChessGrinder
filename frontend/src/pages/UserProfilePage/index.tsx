import {Link, useNavigate, useParams} from "react-router-dom";
import authService, {useAuthData} from "lib/auth/AuthService";
import {useEffect, useMemo} from "react";

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
        User {username}

        {
            isMyProfile ? (
                <div>
                    <button className={"bg-blue-200 rounded-full px-3"}
                        onClick={() => authService.clearAuthData()}>
                        Logout
                    </button>
                </div>
            ) : null
        }
    </>
}