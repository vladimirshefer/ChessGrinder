import {GLOBAL_SETTINGS} from "lib/pageRepository/apiSettings";

export default function AdminPage() {
    return <>
        <h2>Admin Page</h2>
        <div className={"m-2"}>
            <span>Mode</span>
            <select value={GLOBAL_SETTINGS.getProfile()}
                    onChange={(e) => GLOBAL_SETTINGS.setProfile(e.target.value)}
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
