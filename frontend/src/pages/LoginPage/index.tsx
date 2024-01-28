import {useEffect, useState} from "react";
import loginPageRepository from "lib/api/repository/LoginPageRepository";
import {useNavigate} from "react-router-dom";
import {Conditional, ConditionalOnMode} from "components/Conditional";
import {useAuthenticatedUser} from "contexts/AuthenticatedUserContext";
import {useMode} from "lib/api/repository/apiSettings";
import {useLoc} from "strings/loc";


export default function LoginPage() {
    let [username, setUsername] = useState("");
    let [password, setPassword] = useState("");
    let [passwordConfirm, setPasswordConfirm] = useState("");
    let navigate = useNavigate()
    let [authenticatedUser, authenticatedUserRefresh] = useAuthenticatedUser()
    let loc = useLoc();
    let [mode, setMode] = useMode();

    let ENABLE_LOGIN_USERNAME_PASSWORD = mode === "local" || true
    let ENABLE_REGISTRATION_USERNAME_PASSWORD = mode === "local" || true

    useEffect(() => {
        if (!!authenticatedUser) {
            navigate("/user")
        }
    }, [authenticatedUser, navigate])

    async function login(username: string, password: string) {
        await loginPageRepository.login(username, password)
        await authenticatedUserRefresh()
    }

    async function register(username: string, password: string) {
        await loginPageRepository.register(username, password)
        await authenticatedUserRefresh()
    }

    return <div className={"grid p-2 gap-5 text-left"}>
        <div className={"grid gap-2"}>
            <ConditionalOnMode mode={"production"}>
                <h3 className={"font-semibold uppercase"}>Social login</h3>
                <div className={"flex justify-center w-full"}>
                    <a href={"/api/oauth2/authorization/google"}>
                        <img className={"h-8 inline-block"} src={"/google_logo.png"}
                             alt={"Sign in with Google"}></img>
                    </a>
                </div>
            </ConditionalOnMode>
        </div>

        <Conditional on={ENABLE_LOGIN_USERNAME_PASSWORD}>
            <div className={"grid gap-1"}>
                <h3 className={"font-semibold uppercase"}>{loc("Log in")}</h3>
                <input className={"border-b-2 outline-none"} placeholder={loc("Username")}
                       onChange={(e) => setUsername(e.target.value)}
                />
                <input className={"border-b-2 outline-none"} placeholder={loc("Password")}
                       type={"password"}
                       onChange={(e) => setPassword(e.target.value)}
                />
                <button className={"btn-primary uppercase"}
                        onClick={() => login(username, password)}
                >
                    {loc("Log in")}
                </button>
            </div>
        </Conditional>
        <Conditional on={ENABLE_REGISTRATION_USERNAME_PASSWORD}>
            <div className={"grid gap-1"}>
                <h3 className={"font-semibold uppercase"}>{loc("Register")}</h3>
                <input className={"border-b-2 outline-none"} placeholder={loc("Username")}
                       onChange={(e) => setUsername(e.target.value)}
                />
                <input className={"border-b-2 outline-none"} placeholder={loc("Password")}
                       type={"password"}
                       onChange={(e) => setPassword(e.target.value)}
                />
                <input className={"border-b-2 outline-none"} placeholder={loc("Password confirm")}
                       type={"password"}
                       onChange={(e) => setPasswordConfirm(e.target.value)}
                />
                <button className={"btn-primary uppercase"}
                        onClick={() => {
                            if (password !== passwordConfirm) {
                                alert("Password mismatch") }
                            else {
                                register(username, password)
                            }
                }}
                >
                    {loc("Register")}
                </button>
            </div>
        </Conditional>
    </div>
}
