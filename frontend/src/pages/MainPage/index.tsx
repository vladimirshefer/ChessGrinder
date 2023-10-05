import React from "react";
import {MemberList} from "./MemberList";
import {TournamentsList} from "./TournamentsList";
import {useQuery} from "@tanstack/react-query";
import {ListDto, MemberDto, TournamentDto, TournamentListDto} from "lib/api/dto/MainPageData";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import userRepository from "lib/api/repository/UserRepository";
import loc from "strings/loc";
import {Link} from "react-router-dom";

function MainPage() {

    let {
        data: {
            values: users = [] as MemberDto[],
        } = {} as ListDto<MemberDto>,
        refetch: refetchUsers
    } = useQuery({
        queryKey: ["members"],
        queryFn: () => userRepository.getUsers(),
    })

    let {
        data: {
            tournaments = [] as TournamentDto[],
        } = {} as TournamentListDto,
        refetch: refetchTournaments
    } = useQuery({
        queryKey: ["tournaments"],
        queryFn: () => tournamentRepository.getTournaments()
    })

    async function createTournament() {
        await tournamentRepository.postTournament()
        await refetchTournaments()
    }

    return <>
        <div className={"p-3"}>
            <MemberList members={users!!}/>
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
