import {useUser} from "lib/auth/AuthService";
import Gravatar, {GravatarType} from "components/Gravatar";
import React, {useEffect} from "react";
import {useNavigate} from "react-router-dom";
import {useLoc} from "../../strings/loc";

export default function UserProfileEditPage() {
    let user = useUser();
    let navigate = useNavigate()
    let loc = useLoc()

    useEffect(() => {
        if (!user) {
            navigate("/login")
        }
    }, [user, navigate]);

    if (!user) {
        return <>You are not logged in</>
    }

    return <div className={"p-3"}>
        <div className="flex py-2">
            <h1 className={"font-semibold uppercase"}>{loc("Settings")}</h1>
        </div>
        <div className={"grid gap-2"}>
            <div>
                <Gravatar text={user.username || user.id} type={GravatarType.Robohash} size={100}
                          className={"rounded-full"}/>
            </div>

            <div className={"grid grow text-left gap-2"}>
                <input type={"text"} className={"font-semibold uppercase truncate border-b-2"}
                       defaultValue={user.name || user.username || user.id || "Unknown"}
                       title={loc("Full name")}
                       placeholder={loc("Full name")}
                />
                <input type={"text"} className={"text-sm text-gray-500 border-b-2"}
                       defaultValue={user.username}
                       title={loc("Username")}
                       placeholder={loc("Username")}
                />
                <div className="p-2"></div>
                <h3 className={"text-sm uppercase font-semibold"}>{loc("Change password")}</h3>
                <div className={"grid gap-2 p-2 border"}>
                    <input type={"password"} className={"font-semibold uppercase truncate border-b-2"}
                           defaultValue={"*********"}
                           title={loc("Old password")}
                           placeholder={loc("Old password")}
                    />
                    <input type={"password"} className={"font-semibold uppercase truncate border-b-2"}
                           title={loc("New password")}
                           placeholder={loc("New password")}
                    />
                    <input type={"password"} className={"font-semibold uppercase truncate border-b-2"}
                           title={loc("Confirm new password")}
                           placeholder={loc("Confirm new password")}
                    />
                </div>
            </div>
            <div className={"grid gap-2"}>
                <div className="flex gap-2 justify-end">
                    <button className="btn-primary uppercase">{loc("Save")}</button>
                    <button className="btn-light uppercase">{loc("Cancel")}</button>
                </div>
                <div className={"flex justify-end"}>
                    <button className="btn-danger uppercase">{loc("Delete profile")}</button>
                </div>
            </div>
        </div>
    </div>
}
