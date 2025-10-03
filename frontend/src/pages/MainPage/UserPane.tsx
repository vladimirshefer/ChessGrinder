import {UserDto} from "lib/api/dto/MainPageData";
import {useLoc, useTransliterate} from "strings/loc";
import {UserAvatarImg} from "components/Gravatar";
import {Link} from "react-router-dom";
import {FaRegHeart} from "react-icons/fa";
import {FaArrowTrendUp} from "react-icons/fa6";
import React from "react";
import {useConfigurationPropertyEnabled} from "contexts/ConfigurationContext";

export function UserPane(
    {
        user
    }: {
        user: UserDto
    }
) {
    let loc = useLoc()
    let transliterate = useTransliterate()

    const [eloServiceEnabled] = useConfigurationPropertyEnabled("chess.rating", false);

    return <div key={user.id} className={"flex"}>
        <div className={"h-[3em] w-[3em] inline-block overflow-hidden mr-2"}>
            <UserAvatarImg emailHash={user.emailHash} size={150} className={"rounded-full"}/>
        </div>
        <div className={"grid w-full content-left items-left"}>
            <div className={"text-left font-semibold"}>
                <Link to={`/user/${user.id}`}>
                    {transliterate(user.name)}
                </Link>
            </div>
            <div className={"h-[1em] text-sm flex gap-2 items-start"}>
                {eloServiceEnabled && !!user.eloPoints && (
                    <div className={"h-full flex items-center gap-1"} title={`${loc("Rating")} (${loc("Elo points")})`}>
                        <FaArrowTrendUp/>
                        <span>{user.eloPoints || "0"}</span>
                    </div>
                )}
                {!!user.reputation && (
                    <div className={"h-full flex items-center gap-1"} title={loc("Reputation")}>
                        <FaRegHeart/>
                        <span className={""}>{user.reputation || 0}</span>
                    </div>
                )}
            </div>
        </div>
    </div>;
}

export default UserPane;
