import React from "react";
import {TournamentsList} from "./TournamentsList";
import {useQuery} from "@tanstack/react-query";
import {TournamentDto, TournamentListDto} from "lib/api/dto/MainPageData";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import {useLoc} from "strings/loc";
import {Link, useNavigate} from "react-router-dom";
import MyActiveTournamentPane from "pages/MainPage/MyActiveTournamentPane";
import dayjs from "dayjs";

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
        navigate(`/tournament/${tournament.id}/edit`)
    }

    let tournamentsVisible = tournaments.filter(
        it => {
            const oneWeekAgo = dayjs().subtract(1, 'week');
            const tournamentDate = dayjs(it.date);
            return it.status === "ACTIVE" || it.status === "PLANNED" || tournamentDate.isAfter(oneWeekAgo) || tournamentDate.isAfter();
        }
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
