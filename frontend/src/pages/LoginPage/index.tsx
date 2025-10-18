import {useEffect, useRef} from "react";
import loginPageRepository from "lib/api/repository/LoginPageRepository";
import {useNavigate} from "react-router-dom";
import {Conditional, ConditionalOnMode} from "components/Conditional";
import {useAuthenticatedUser} from "contexts/AuthenticatedUserContext";
import {useMode} from "lib/api/repository/apiSettings";
import {useLoc} from "strings/loc";
import {useForm} from "react-hook-form";
import {UserSignUpRequest} from "lib/api/dto";
import GoogleLoginButton from "pages/LoginPage/GoogleLoginButton";
import useSearchParam from "lib/react/hooks/useSearchParam";
import {useConfigurationProperty, useConfigurationPropertyEnabled} from "contexts/ConfigurationContext";
import ReCAPTCHA from "react-google-recaptcha";

const USERNAME_REGEX = /^[a-zA-Z][a-zA-Z0-9]+$/g

export default function LoginPage() {
    let navigate = useNavigate()
    let [authenticatedUser, authenticatedUserRefresh] = useAuthenticatedUser()
    let loc = useLoc();
    let [mode,] = useMode();
    let signInForm = useForm()
    let instantSignInForm = useForm()
    let signUpForm = useForm()
    let [referer] = useSearchParam("referer", "")
    const captchaRef = useRef<ReCAPTCHA>(null);

    let [ENABLE_LOGIN_USERNAME_PASSWORD] = useConfigurationPropertyEnabled("auth.password", mode === "local")
    let [ENABLE_REGISTRATION_EMAIL_LINK] = useConfigurationPropertyEnabled("auth.instant", mode === "local")
    let [ENABLE_REGISTRATION_USERNAME_PASSWORD] = useConfigurationPropertyEnabled("auth.password", mode === "local")
    let [CAPTCHA_PUBLIC_KEY] = useConfigurationProperty("captcha.site", import.meta.env.VITE_CAPTCHA_PUBLIC_KEY || "placeholder")

    useEffect(() => {
        if (!!authenticatedUser) {
            navigate("/user")
        }
    }, [authenticatedUser, navigate])

    async function signIn(username: string, password: string) {
        await loginPageRepository.signIn(username, password)
        authenticatedUserRefresh()
        signInForm.reset()
        signUpForm.reset()
        instantSignInForm.reset()
    }

    async function instantSignIn(email: string) {
        let token = await captchaRef!!.current!!.executeAsync();
        if (!token) {
            alert("Captcha is required");
            navigate("/login")
            return
        }
        try {
            await loginPageRepository.authInstantInit(email, token)
        } catch (e: any) {
            alert("Login failed! " + (e?.response?.data?.message || ""))
            return
        }

        authenticatedUserRefresh()
        signInForm.reset()
        signUpForm.reset()
        instantSignInForm.reset()
    }

    async function signUp(data: UserSignUpRequest) {
        await loginPageRepository.signUp(data)
            .catch(it => alert(it?.response?.data?.message || it?.message || "SignUp failed"))
        authenticatedUserRefresh()
    }

    async function handleSignInSubmit(data: any) {
        await signIn(data["username"], data["password"])
    }

    async function handleInstantSignInSubmit(data: any) {
        await instantSignIn(data["email"])
    }

    async function handleSignUpSubmit(data: any) {
        let username = data["username"];
        let password = data["password"];

        if (password !== data["passwordConfirm"]) {
            alert("Password mismatch")
            return
        }

        let userSignupRequest = {
            username: username,
            password: password,
            fullName: data["fullName"],
        } as UserSignUpRequest;

        await signUp(userSignupRequest)
        signInForm.reset()
        signUpForm.reset()
        instantSignInForm.reset()
        alert("Success")
    }

    async function handleSignUpViolated(errors: object) {
        let message = ""
        for (const [field, error] of Object.entries(errors)) {
            message += (error?.message || field) + "\n";
        }
        alert(message);
        console.error(message);
    }

    return <div className={"grid p-2 gap-5 text-left"}>
        <div className={"grid gap-2"}>
            <ConditionalOnMode mode={"production"}>
                <h3 className={"font-semibold uppercase"}>Social login</h3>
                <div className={"flex justify-center w-full"}>
                    <a href={`/api/oauth2/authorization/google?referer=${referer}`}>
                        <GoogleLoginButton/>
                    </a>
                </div>
            </ConditionalOnMode>
        </div>

        <Conditional on={ENABLE_LOGIN_USERNAME_PASSWORD}>
            <form className={"grid gap-1"} onSubmit={instantSignInForm.handleSubmit(handleInstantSignInSubmit)}>
                <h3 className={"font-semibold uppercase"}>{loc("Instant Sign In")}</h3>
                <input className={"border-b-2 outline-hidden"} placeholder={loc("Email")}
                       {...instantSignInForm.register("email")}
                />
                <button className={"btn-primary uppercase"} type={"submit"}>
                    {loc("Sign in")}
                </button>
                <ReCAPTCHA
                    ref={captchaRef}
                    size="invisible"
                    sitekey={CAPTCHA_PUBLIC_KEY!!}
                />
            </form>
        </Conditional>
        <Conditional on={ENABLE_REGISTRATION_EMAIL_LINK}>
            <form className={"grid gap-1"} onSubmit={signInForm.handleSubmit(handleSignInSubmit)}>
                <h3 className={"font-semibold uppercase"}>{loc("Sign in")}</h3>
                <input className={"border-b-2 outline-hidden"} placeholder={loc("Username")}
                       {...signInForm.register("username")}
                />
                <input className={"border-b-2 outline-hidden"} placeholder={loc("Password")}
                       type={"password"}
                       {...signInForm.register("password")}
                />
                <button className={"btn-primary uppercase"} type={"submit"}>
                    {loc("Sign in")}
                </button>
            </form>
        </Conditional>
        <Conditional on={ENABLE_REGISTRATION_USERNAME_PASSWORD}>
            <form className={"grid gap-1"} onSubmit={signUpForm.handleSubmit(handleSignUpSubmit, handleSignUpViolated)}>
                <h3 className={"font-semibold uppercase"}>{loc("Sign up")}</h3>
                <input className={"border-b-2 outline-hidden"} placeholder={loc("Full name")}
                       {...signUpForm.register("fullName", {
                           required: {
                               value: true,
                               message: `${loc("Full name")} is required`
                           },
                           minLength: {
                               value: 4,
                               message: `${loc("Full name")} must be at least 4 symbols length`
                           },
                       })}
                />
                <input className={"border-b-2 outline-hidden"} placeholder={loc("Username")}
                       {...signUpForm.register("username", {
                           required: {
                               value: true,
                               message: `${loc("Username")} is required`
                           },
                           minLength: {
                               value: 4,
                               message: `${loc("Username")} must be at least 4 symbols length`
                           },
                           pattern: {
                               value: USERNAME_REGEX,
                               message: `Invalid ${loc("Username")}`
                           }
                       })}
                />
                <input className={"border-b-2 outline-hidden"} placeholder={loc("Password")}
                       {...signUpForm.register("password", {
                           required: {
                               value: true,
                               message: `${loc("Password")} is required`
                           },
                           minLength: {
                               value: 8,
                               message: `${loc("Password")} must be at least 8 symbols length`
                           },
                       })}
                       type={"password"}
                />
                <input className={"border-b-2 outline-hidden"} placeholder={loc("Password confirm")}
                       type={"password"}
                       {...signUpForm.register("passwordConfirm", {
                           required: {
                               value: true,
                               message: `${loc("Password confirm")} is required`
                           },
                           minLength: {
                               value: 8,
                               message: `${loc("Password confirm")} must be at least 8 symbols length`
                           },
                       })}
                />
                <button type={"submit"} className={"btn-primary uppercase"}>
                    {loc("Sign up")}
                </button>
            </form>
        </Conditional>
    </div>
}
