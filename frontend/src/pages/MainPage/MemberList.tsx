import {UserDto} from "lib/api/dto/MainPageData";
import React from "react";
import {useLoc} from "strings/loc";
import {useQuery} from "@tanstack/react-query";
import {UserPane} from "pages/MainPage/UserPane";
import userRepository from "lib/api/repository/UserRepository";

export function MemberList(
    {
        users,
        startSeasonDate = null,
        endSeasonDate = null,
    }: {
        users: UserDto[],
        startSeasonDate?: string | null,
        endSeasonDate?: string | null,
    }
) {
    let loc = useLoc()

// new Date('2024-04-01'), new Date('2024-04-23')
//TODO теперь передавать в MemberList
//ЕЩВЩ сделать юзквери в котором будет трай, фор, эвеит
//сделать мапу юзер айди к очкам
//С этой мапой юзерпейн
//TODO создать DTO singleValueResponse<T>TT
    let seasonPointsQuery = useQuery({
        queryKey: ["usersData", users],
        queryFn: async () => {
            let userId2SeasonPoints = {} as any;
            try {
                for (const user of users) {
                    let points = await userRepository.getTotalPoints(user.id, startSeasonDate, endSeasonDate);
                    userId2SeasonPoints[user.id] = points;
                }
                return userId2SeasonPoints;
            }
            catch (error: any) {
                alert(loc(error.response.data.message));
            }

        }
    });

    //TODO в узерпейн передать очки

    return <div>
        <h2 className={"text-xl my-2 uppercase text-left font-semibold"}>{loc("Members")}</h2>
        <div className={"w-full grid grid-cols-12"}>
            {users.map(user => {
                            const totalPoints = seasonPointsQuery.data? seasonPointsQuery.data[user.id] : null;
                            return (
                                    <UserPane
                                        key={user.id}
                                        user={user}
                                        totalPoints={totalPoints}
                                    />
                                    );
                            })}
        </div>
    </div>;
}

export default MemberList
