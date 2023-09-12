import {useEffect, useState} from "react";
import loginPageRepository from "../../lib/pageRepository/LoginPageRepository";
import {useAuthData} from "../../lib/auth/AuthService";
import {useNavigate} from "react-router-dom";

export default function LoginPage() {
    let [username, setUsername] = useState("");
    let [password, setPassword] = useState("");
    let navigate = useNavigate()
    let authData = useAuthData()

    useEffect(() => {
        if (!!authData) {
            navigate("/user")
        }
    }, [authData])

    async function login(username: string, password: string) {
        await loginPageRepository.login(username, password)
    }

    return <div className={"grid grid-cols-12"}>
        <span className={"col-span-6"}>Email:</span>
        <input className={"col-span-6 border-b-2 outline-none border-blue-b-300"}
               onChange={(e) => setUsername(e.target.value)}
        />
        <span className={"col-span-6"}>Password:</span>
        <input className={"col-span-6 border-b-2 outline-none border-blue-b-300"} type={"password"}
               onChange={(e) => setPassword(e.target.value)}
        />
        <button className={"col-span-12 bg-purple-200 rounded-full mt-2"}
                onClick={() => login(username, password)}
        >
            Login
        </button>
    </div>
}
