import React from "react";
import {MemberList} from "pages/MainPage/MemberList";
import {TournamentsList} from "./TournamentsList";
import {useQuery} from "@tanstack/react-query";
import {ListDto, UserDto, TournamentDto, TournamentListDto} from "lib/api/dto/MainPageData";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import userRepository from "lib/api/repository/UserRepository";
import {useLoc} from "strings/loc";
import {Link, useNavigate} from "react-router-dom";
import MyActiveTournamentPane from "pages/MainPage/MyActiveTournamentPane";

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

    let tournamentsVisible = tournaments.filter(
        it => it.status === "ACTIVE" || it.status === "PLANNED"
    );
    if (tournamentsVisible.length === 0) {
        const maxTourNumber: number = 2;
        //sorted on server's side
        tournamentsVisible = tournaments.slice(0, maxTourNumber);
    }

    const maxUsers: number = 8;
    return <>
        <MyActiveTournamentPane/>
        <div className={"p-3"}>
            <MemberList users={users.slice(0, maxUsers)}/>
            <div className={"grid py-2"}>
                <Link to={"/users"}>
                    <button className={"btn bg-primary-400 w-full"}>
                        {loc("All users")}
                    </button>
                </Link>
            </div>
        </div>
        <div className={"p-3"}>
            <TournamentsList tournaments={tournamentsVisible} createTournament={createTournament}/>
            <div className={"grid py-2"}>
                <Link to={"/tournaments"}>
                    <button className={"btn bg-primary-400 w-full"}>
                        {loc("All tournaments")}
                    </button>
                </Link>
            </div>
        </div>
        <div className={"w-full mt-5 text-sm"}>
            <Link className={"underline"} to={'/privacyPolicy'}>Privacy Policy</Link>
        </div>
    </>
}

export default MainPage
