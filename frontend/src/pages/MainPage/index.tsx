import React from "react";
import {TournamentsList} from "./TournamentsList";
import {useQuery} from "@tanstack/react-query";
import {TournamentDto, TournamentListDto} from "lib/api/dto/MainPageData";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import {useLoc} from "strings/loc";
import {Link, useNavigate} from "react-router-dom";
import MyActiveTournamentPane from "pages/MainPage/MyActiveTournamentPane";

function MainPage() {
    let loc = useLoc()
    let navigate = useNavigate()

    let {
        data: {
            values: tournaments = [] as TournamentDto[],
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

    return <>
        <MyActiveTournamentPane/>
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
    </>
}

export default MainPage
