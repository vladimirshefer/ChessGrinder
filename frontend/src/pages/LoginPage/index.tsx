import {useEffect, useState} from "react";
import loginPageRepository from "lib/api/repository/LoginPageRepository";
import {useNavigate} from "react-router-dom";
import {ConditionalOnMode} from "components/Conditional";
import {useAuthenticatedUser} from "contexts/AuthenticatedUserContext";

export default function LoginPage() {
    let [username, setUsername] = useState("");
    let [password, setPassword] = useState("");
    let navigate = useNavigate()
    let [authenticatedUser] = useAuthenticatedUser()

    useEffect(() => {
        if (!!authenticatedUser) {
            navigate("/user")
        }
    }, [authenticatedUser, navigate])

    async function login(username: string, password: string) {
        await loginPageRepository.login(username, password)
    }

    async function register(username: string, password: string) {
        alert("Registration is not yet supported")
    }

    return <div className={"grid grid-cols-12"}>
        <div className={"col-span-12 grid grid-cols-12 p-4"}>
            <ConditionalOnMode mode={"production"}>
                <div className={"col-span-12 font-bold"}>
                    <h3>Social login</h3>
                </div>
                <div className={"col-span-12"}>
                    <div className={"p-5"}>
                        <a href={"/api/oauth2/authorization/google"}>
                            <img className={"h-8 inline-block"} src={"/google_logo.png"}
                                 alt={"Sign in with Google"}></img>
                        </a>
                    </div>
                </div>
            </ConditionalOnMode>
        </div>

        <ConditionalOnMode mode={"local"}>
            <div className={"col-span-12 md:col-span-6 grid grid-cols-12 p-5 bg-gray-50 rounded-md mx-5 mt-5"}>
                <h3 className={"col-span-12 font-bold"}>Login</h3>
                <span className={"col-span-12 lg:col-span-4"}>Username:</span>
                <input className={"col-span-12 lg:col-span-8 border-b-2 outline-none border-blue-b-300"}
                       onChange={(e) => setUsername(e.target.value)}
                />
                <span className={"col-span-12 lg:col-span-4"}>Password:</span>
                <input className={"col-span-12 lg:col-span-8 border-b-2 outline-none border-blue-b-300"}
                       type={"password"}
                       onChange={(e) => setPassword(e.target.value)}
                />
                <button className={"col-span-12 bg-purple-200 rounded-full mt-2"}
                        onClick={() => login(username, password)}
                >
                    Login
                </button>
            </div>
            <div className={"col-span-12 md:col-span-6 grid grid-cols-12 p-5 bg-gray-50 rounded-md mx-5 mt-5"}>
                <h3 className={"col-span-12 font-bold"}>Register</h3>
                <span className={"col-span-12 lg:col-span-4"}>Username:</span>
                <input className={"col-span-12 lg:col-span-8 border-b-2 outline-none border-blue-b-300"}
                       onChange={(e) => setUsername(e.target.value)}
                />
                <span className={"col-span-12 lg:col-span-4"}>Password:</span>
                <input className={"col-span-12 lg:col-span-8 border-b-2 outline-none border-blue-b-300"}
                       type={"password"}
                       onChange={(e) => setPassword(e.target.value)}
                />
                <span className={"col-span-12 lg:col-span-4"}>Password confirm:</span>
                <input className={"col-span-12 lg:col-span-8 border-b-2 outline-none border-blue-b-300"}
                       type={"password"}
                       onChange={(e) => setPassword(e.target.value)}
                />
                <button className={"col-span-12 bg-purple-200 rounded-full mt-2"}
                        onClick={() => register(username, password)}
                >
                    Register
                </button>
            </div>
        </ConditionalOnMode>
    </div>
}
