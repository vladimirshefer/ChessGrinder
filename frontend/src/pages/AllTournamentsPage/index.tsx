import React from "react";
import {useQuery} from "@tanstack/react-query";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import TournamentsList from "pages/MainPage/TournamentsList";
import {useNavigate, useSearchParams} from "react-router-dom";

export default function AllTournamentsPage() {
    let navigate = useNavigate()

    let [searchParams] = useSearchParams()

    let tournamentsQuery = useQuery({
        queryKey: ["tournaments"],
        queryFn: () => tournamentRepository.getTournaments()
    })

    let tournaments = tournamentsQuery.data?.values;
    if (!tournaments) {
        return <>Loading...</>
    }

    tournaments = tournaments
        .filter(it => searchParams.getAll("status").length === 0 || (searchParams.getAll("status").map(it => it.toLowerCase()).includes(it.status?.toLowerCase() || "")))
        .filter(it => searchParams.getAll("city").length === 0 || (searchParams.getAll("city").map(it => it.toLowerCase()).includes(it.city?.toLowerCase() || "")))
        .filter(it => searchParams.getAll("name").length === 0 || (searchParams.getAll("name").map(it => it.toLowerCase()).find(nameParam => it.name?.toLowerCase().includes(nameParam)) !== undefined))
        .filter(it => searchParams.get("finished") === null || ((searchParams.get("finished") !== "false" && searchParams.get("finished") !== "true") || (searchParams.get("finished") === "false" && it.status !== "FINISHED") || (searchParams.get("finished") === "true" && it.status === "FINISHED")))

    function createTournament() {
        navigate(`/tournament/create`)
    }

    return <div className={"p-2"}>
        <TournamentsList tournaments={tournaments} createTournament={createTournament}/>
    </div>
}
