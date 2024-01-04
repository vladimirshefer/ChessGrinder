import React from "react";
import {MemberList} from "./MemberList";
import {TournamentsList} from "./TournamentsList";
import {useQuery} from "@tanstack/react-query";
import {ListDto, UserDto, TournamentDto, TournamentListDto} from "lib/api/dto/MainPageData";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import userRepository from "lib/api/repository/UserRepository";
import {useLoc} from "strings/loc";
import {Link, useNavigate} from "react-router-dom";

function MainPage() {
    let loc = useLoc()
    let navigate = useNavigate()

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
    return <>
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
        <TournamentsList tournaments={tournaments} createTournament={createTournament}/>
    </>

}

export default MainPage
