import {useEffect} from "react";
import loginPageRepository from "lib/api/repository/LoginPageRepository";
import {useNavigate} from "react-router-dom";
import {Conditional, ConditionalOnMode} from "components/Conditional";
import {useAuthenticatedUser} from "contexts/AuthenticatedUserContext";
import {useMode} from "lib/api/repository/apiSettings";
import {useLoc} from "strings/loc";
import {useForm} from "react-hook-form";
import {UserSignUpRequest} from "lib/api/dto";
import config from "config";


const USERNAME_REGEX = /^[a-zA-Z][a-zA-Z0-9]+$/g

export default function LoginPage() {
    let navigate = useNavigate()
    let [authenticatedUser, authenticatedUserRefresh] = useAuthenticatedUser()
    let loc = useLoc();
    let [mode,] = useMode();
    let signInForm = useForm()
    let signUpForm = useForm()

    let ENABLE_LOGIN_USERNAME_PASSWORD = mode === "local" || config.features["auth.signupWithPasswordEnabled"]
    let ENABLE_REGISTRATION_USERNAME_PASSWORD = mode === "local" || config.features["auth.signupWithPasswordEnabled"]

    useEffect(() => {
        if (!!authenticatedUser) {
            navigate("/user")
        }
    }, [authenticatedUser, navigate])

    async function signIn(username: string, password: string) {
        await loginPageRepository.signIn(username, password)
        await authenticatedUserRefresh()
    }

    async function signUp(data: UserSignUpRequest) {
        await loginPageRepository.signUp(data)
            .catch(it => alert(it?.response?.data?.message || it?.message || "SignUp failed"))
        await authenticatedUserRefresh()
    }

    async function handleSignInSubmit(data: any) {
        await signIn(data["username"], data["password"])
    }

    async function handleSignUpSubmit(data: any) {
        if (data["password"] !== data["passwordConfirm"]) {
            alert("Password mismatch")
            return
        }

        if (!!data["username"] && !USERNAME_REGEX.test(data["username"])) {
            alert("Incorrect username. Must start with letter and contain no special chars.")
            return
        }

        let userSignupRequest = {
            username: data["username"],
            password: data["password"],
            fullName: data["fullName"],
        } as UserSignUpRequest;

        await signUp(userSignupRequest)
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
            <form className={"grid gap-1"} onSubmit={signInForm.handleSubmit(handleSignInSubmit)}>
                <h3 className={"font-semibold uppercase"}>{loc("Sign in")}</h3>
                <input className={"border-b-2 outline-none"} placeholder={loc("Username")}
                       {...signInForm.register("username")}
                />
                <input className={"border-b-2 outline-none"} placeholder={loc("Password")}
                       type={"password"}
                       {...signInForm.register("password")}
                />
                <button className={"btn-primary uppercase"} type={"submit"}>
                    {loc("Sign in")}
                </button>
            </form>
        </Conditional>
        <Conditional on={ENABLE_REGISTRATION_USERNAME_PASSWORD}>
            <form className={"grid gap-1"} onSubmit={signUpForm.handleSubmit(handleSignUpSubmit)}>
                <h3 className={"font-semibold uppercase"}>{loc("Sign up")}</h3>
                <input className={"border-b-2 outline-none"} placeholder={loc("Full name")}
                       {...signUpForm.register("fullName")}
                />
                <input className={"border-b-2 outline-none"} placeholder={loc("Username")}
                       {...signUpForm.register("username")}
                />
                <input className={"border-b-2 outline-none"} placeholder={loc("Password")}
                       {...signUpForm.register("password")}
                       type={"password"}
                />
                <input className={"border-b-2 outline-none"} placeholder={loc("Password confirm")}
                       type={"password"}
                       {...signUpForm.register("passwordConfirm")}
                />
                <button type={"submit"} className={"btn-primary uppercase"}>
                    {loc("Sign up")}
                </button>
            </form>
        </Conditional>
    </div>
}
