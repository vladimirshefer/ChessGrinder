import {UserDto} from "lib/api/dto/MainPageData";
import {useLoc, useTransliterate} from "strings/loc";
import Gravatar, {GravatarType} from "components/Gravatar";
import {Link} from "react-router-dom";
import {AiOutlineTrophy} from "react-icons/ai";
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

    return <div key={user.id} className={"col-span-12 flex"}>
        <div className={"h-[3em] w-[3em] inline-block overflow-hidden mr-2"}>
            <Gravatar
                text={user.emailHash}
                type={GravatarType.Robohash}
                size={150}
                className={"rounded-full"}
                inputType={"MD5"}
            />
        </div>
        <div className={"grid w-full content-left items-left"}>
            <div className={"text-left"}>
                <Link to={`/user/${user.id}`}>
                    {transliterate(user.name)}
                </Link>
            </div>
            <div className={"h-[1em] text-xl flex gap-2 items-start"}>
                <div className={"flex items-start h-full gap-2 grow"}>
                    {(user.badges || []).map(badge =>
                        <Link to={`/badge/${badge.id}`}
                              className={"block h-full"}
                              key={badge.id}
                              title={`${badge.title}\n\n${badge.description}`}
                        >
                            <Gravatar
                                text={badge.title}
                                type={GravatarType.Identicon}
                                size={150}
                                className={"block rounded-full h-full"}
                            />
                        </Link>
                    )}
                </div>
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
