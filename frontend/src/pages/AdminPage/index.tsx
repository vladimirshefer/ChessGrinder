import {useMode} from "lib/api/repository/apiSettings";
import authService from "lib/auth/AuthService";

export default function AdminPage() {
    let [mode, setMode] = useMode()

    return <>
        <h2>Admin Page</h2>
        <div className={"m-2"}>
            <span>Mode</span>
            <select defaultValue={mode}
                    onChange={(e) => {
                        setMode(e.target.value);
                        authService.setAuthData(null);
                    }}
                    name={"Mode"}
            >
                <option>local</option>
                <option>production</option>
            </select>
        </div>
        <div className={"m-2"}>
            <span>Clear all local data</span>
            <button className={"btn bg-blue-200 rounded-md px-1 mx-2"}
                onClick={e => {
                    for (let localStorageKey in localStorage) {
                        if (localStorageKey.startsWith("cgd.")) {
                            localStorage.removeItem(localStorageKey);
                        }
                    }
                }}
            >Clear</button>
        </div>s
    </>
}
