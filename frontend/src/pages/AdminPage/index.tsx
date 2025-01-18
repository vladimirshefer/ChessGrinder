import {useMode} from "lib/api/repository/apiSettings";
import loginPageRepository from "lib/api/repository/LoginPageRepository";
import {useAuthenticatedUser} from "contexts/AuthenticatedUserContext";
import restApiClient from "../../lib/api/RestApiClient";

export default function AdminPage() {
    let [mode, setMode] = useMode()
    let [, authenticatedUserReload] = useAuthenticatedUser()

    return <div className={"grid gap-2 text-left p-1"}>
        <div className={"grid gap-2 py-3 border-b-2"}>
            <h3 className={"text-lg font-semibold"}>Local mode</h3>
            <div className={"flex gap-3 items-center"}>
                <span>Mode</span>
                <select
                    className={"border border-gray-300"}
                    defaultValue={mode}
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

            <button className={"btn-dark"}
                    onClick={e => {
                        for (let localStorageKey in localStorage) {
                            if (localStorageKey.startsWith("cgd.")) {
                                localStorage.removeItem(localStorageKey);
                            }
                        }
                    }}
            >Clear all local data
            </button>
        </div>

        <div className={"grid gap-2 py-3 border-b-2"}>
        <h3 className={"text-lg font-semibold"}>Cheats</h3>
            <button className={"btn-primary"}
                    onClick={e => {
                        restApiClient.get("/cheat/getAdminRole")
                            .then((e: any) => alert("Got admin role " + e?.data))
                            .catch(e => alert("Could not get admin role " + e?.response?.data?.message))
                    }}
            >Get Admin Role
            </button>
        </div>

        <div className={"grid gap-2 py-3 border-b-2"}>
            <h3 className={"text-lg font-semibold"}>Ratings</h3>
            <DangerActionButton
                actionName={"Reset All Ratings"}
                className={"btn-danger"}
                action={() => {
                    restApiClient.get("/admin/ratings/reset")
                        .catch(e => alert("Could not reset ratings " + e?.response?.data?.message))
                }}
            />
            <DangerActionButton
                actionName={"Recalculate All Ratings"}
                className={"btn-danger"}
                action={() => {
                    restApiClient.get("/admin/ratings/recalculate")
                        .catch(e => alert("Could not recalculate ratings " + e?.response?.data?.message))
                }}
            />
        </div>

    </div>
}

function DangerActionButton(
    {
        actionName,
        action,
        className,
    }: {
        actionName: string,
        action: () => void,
        className?: string,
    }
) {
    return <button className={className}
                   onClick={e => {
                       let prompt1 = prompt(`Are you sure you want to ${actionName}? To do so, type ${actionName}`);
                       if (prompt1 !== actionName) return;
                       action();
                   }}
    >{actionName}
    </button>
}
