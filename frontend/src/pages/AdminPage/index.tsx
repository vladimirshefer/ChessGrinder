import {useMode} from "lib/api/repository/apiSettings";
import loginPageRepository from "lib/api/repository/LoginPageRepository";
import {useAuthenticatedUser} from "contexts/AuthenticatedUserContext";

export default function AdminPage() {
    let [mode, setMode] = useMode()
    let [, authenticatedUserReload] = useAuthenticatedUser()

    return <>
        <h2>Admin Page</h2>
        <div className={"m-2"}>
            <span>Mode</span>
            <select defaultValue={mode}
                    onChange={async (e) => {
                        await loginPageRepository.signOut()
                            .catch((e: unknown) => console.error(e));
                        setMode(e.target.value);
                        authenticatedUserReload();
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
        </div>
    </>
}
