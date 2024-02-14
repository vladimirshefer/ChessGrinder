import React from "react";
import {MemberList} from "./MemberList";
import {TournamentsList} from "./TournamentsList";
import {useQuery} from "@tanstack/react-query";
import {ListDto, UserDto, TournamentDto, TournamentListDto} from "lib/api/dto/MainPageData";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import userRepository from "lib/api/repository/UserRepository";
import {useLoc} from "strings/loc";
import {Link, useNavigate} from "react-router-dom";
import {compareBy} from "lib/util/Comparator";
import useSearchParam from "lib/react/hooks/useSearchParam";

function MainPage() {
    let loc = useLoc()
    let navigate = useNavigate()
    /**
     * allows to add ?activeTournamentsOnly=true to the main page url.
     * This will hide everything except for currently available tournaments.
     * This is used to create advertisement link for social media.
     */
    let activeTournamentsOnly = useSearchParam("activeTournamentsOnly", "false")[0] === "true"

    let {
        data: {
            values: users = [] as UserDto[],
        } = {} as ListDto<UserDto>
    } = useQuery({
        queryKey: ["members"],
        queryFn: () => userRepository.getUsers(),
    })

    let {
        data: {
            tournaments = [] as TournamentDto[],
        } = {} as TournamentListDto,
    } = useQuery({
        queryKey: ["tournaments"],
        queryFn: () => tournamentRepository.getTournaments()
    })

    async function createTournament() {
        let tournament = await tournamentRepository.postTournament();
        await navigate(`/tournament/${tournament.id}/edit`)
    }

    const maxUsers: number = 10;

    let tournamentsVisible = activeTournamentsOnly
        ? tournaments.filter(it => it.status === "ACTIVE" || it.status === "PLANNED")
        : tournaments;

    return <>
        {!activeTournamentsOnly && (
            <div className={"p-3"}>
                <MemberList members={users.slice(0, maxUsers)}/>
                <div className={"grid py-2"}>
                    <Link to={"/users"}>
                        <button className={"btn bg-primary w-full"}>
                            {loc("All users")}
                        </button>
                    </Link>
                </div>
            </div>
        )}
        <TournamentsList tournaments={tournamentsVisible} createTournament={createTournament}/>
        <div className={"w-full mt-5 text-sm"}>
            <Link className={"underline"} to={'/privacyPolicy'}>Privacy Policy</Link>
        </div>
    </>

}

export default MainPage
