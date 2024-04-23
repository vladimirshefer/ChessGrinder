import {UserDto} from "lib/api/dto/MainPageData";
import {useLoc} from "strings/loc";
import {useQuery} from "@tanstack/react-query";
import userRepository from "lib/api/repository/UserRepository";
import Gravatar, {GravatarType} from "components/Gravatar";
import {Link} from "react-router-dom";
import {AiOutlineTrophy} from "react-icons/ai";
import {FaRegHeart} from "react-icons/fa";
import React from "react";

export function UserPane(
    {
        user
    }: {
        user: UserDto
    }
) {
    let loc = useLoc()
    //TODO мне кажется, что надо создать отдельный запрос и отвязаться от UserPane
    //(а именно так, как было сделано до введения сезонности)
    //Можно ли сделать так, чтобы передавать даты не в этот класс, а в другой (похожий)?
    let userHistoryQuery = useQuery({
        queryKey: ["userHistory", user.id],
        queryFn: async () => {
//             return await userRepository.getHistory(user.id);
            return await userRepository.getTotalPoints(user.id, new Date('1970-01-01'), new Date('2100-01-01'));
        }
    });

    //userHistory используется здесь только для сложения очков -
    //а можно ли сразу сумму из бэка передавать?
    //Придумать какой-нибудь метод userRepository.getTotalPoints(user.id); чтобы он возвращал только число
    //Сейчас getHistory по факту нужен только для страницы профиля пользователя
    //Можно ли в таком случае мой новый запрос совместить с этим?
    //Надо еще посмотреть, чтобы это никак не влияло на главную страницу
//     let userHistory = userHistoryQuery.data?.values || [];
//     let totalPoints = userHistory
//         .map(it => it.participant.score)
//         .reduce((a, b) => a + b, 0);
//     let totalPoints = 6;
    let totalPoints = userHistoryQuery.data;

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
                    {user.name}
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
                    <span className={""}>{totalPoints}</span>
                </div>
                <div className={"h-full leading-4 flex block align-bottom gap-1"} title={loc("Reputation")}>
                    <FaRegHeart className={"inline -mt-[1px] leading-4 align-bottom"}/>
                    <span className={""}>{user.reputation || 0}</span>
                </div>
            </div>
        </div>
    </div>;
}

export default UserPane;
