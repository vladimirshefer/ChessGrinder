import React from "react";
import {useQuery} from "@tanstack/react-query";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import TournamentsList from "../MainPage/TournamentsList";
import {useNavigate} from "react-router-dom";

export default function AllTournamentsPage() {
    let navigate = useNavigate()
    let tournamentsQuery = useQuery({
        queryKey: ["tournaments"],
        queryFn: () => tournamentRepository.getTournaments()
    })

    let tournaments = tournamentsQuery.data?.values;
    if (!tournaments) {
        return <>Loading...</>
    }
    async function createTournament() {
        let tournament = await tournamentRepository.postTournament();
        await navigate(`/tournament/${tournament.id}/edit`)
    }
    return <div className={"p-2"}>
        <TournamentsList tournaments={tournaments} createTournament={createTournament}/>
    </div>
}
