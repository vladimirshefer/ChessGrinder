import {useState} from "react";

export default function LoginPage() {
    let [username, setUsername] = useState("");
    let [password, setPassword] = useState("");

    function login(username: string, password: string) {
        alert(`${username} : ${password}`)
    }

    return <div className={"grid grid-cols-12"}>
        <span className={"col-span-6"}>Email:</span>
        <input className={"col-span-6"}
               onChange={(e) => setUsername(e.target.value)}
        />
        <span className={"col-span-6"}>Password:</span>
        <input className={"col-span-6"} type={"password"}
               onChange={(e) => setPassword(e.target.value)}
        />
        <button className={"col-span-12 bg-purple-200 rounded-full"}
                onClick={() => login(username, password)}
        >
            Login
        </button>
    </div>
}
