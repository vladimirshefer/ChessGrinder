import {UserDto} from "lib/api/dto/MainPageData";
import {useLoc, useTransliterate} from "strings/loc";
import Gravatar, {GravatarType} from "components/Gravatar";
import {Link} from "react-router-dom";
import {AiOutlineTrophy} from "react-icons/ai";
import {FaRegHeart} from "react-icons/fa";
import { FaChartLine } from "react-icons/fa6";
import React from "react";


export function UserPane(
    {
        user
    }: {
        user: UserDto
    }
) {
    let loc = useLoc()
    let transliterate = useTransliterate()

    const eloServiceEnabled = process.env.REACT_APP_eloServiceEnabled === 'true';

    return <div key={user.id} className={"col-span-12 flex"}>
        <div className={"h-[3em] w-[3em] inline-block overflow-hidden mr-2"}>
            <Gravatar
                text={user.username || user.id}
                type={GravatarType.Robohash}
                size={150}
                className={"rounded-full"}
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
                <div className={"h-full leading-4 flex block align-bottom gap-1"} title={loc("Tournament points")}>
                    <AiOutlineTrophy className={"inline -mt-[1px] leading-4 align-bottom"}/>
                    <span className={""}>{user.globalScore || 0}</span>
                </div>
                <div className={"h-full leading-4 flex block align-bottom gap-1"} title={loc("Reputation")}>
                    <FaRegHeart className={"inline -mt-[1px] leading-4 align-bottom"}/>
                    <span className={""}>{user.reputation || 0}</span>
                </div>

            {eloServiceEnabled && (
                <div className={"h-full leading-4 flex block align-bottom gap-1"} title={loc("Elo Points")}>
                   <FaChartLine className={"inline -mt-[1px] leading-4 align-bottom"}/>
                   <span>{user.eloPoints || "Unrated"}</span>
                 </div>
            )}
            </div>
        </div>
    </div>;
}

export default UserPane;
