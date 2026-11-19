import {useParams} from "react-router-dom";
import {useQuery} from "@tanstack/react-query";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import {TournamentPageImpl} from "./index";

function EventPage() {
    let {city, date: dateStr} = useParams()
    let tournamentsQuery = useQuery({
        queryKey: ["tournaments", city, dateStr],
        queryFn: async () => {
            let allTournaments = await tournamentRepository.getTournaments();
            return allTournaments.values
                .filter(it => it.city === city)
                .filter(it => it.date.includes(dateStr || ""));
        },
    })

    if (!tournamentsQuery.isSuccess) return <>Loading...</>
    let tournaments = tournamentsQuery.data;
    return <div className={"grid gap-2"}>
        {tournaments.map(it => <div key={it.id}>
            <TournamentPageImpl tournament={it} roundId={null}/>
        </div>)}
    </div>
}

export default EventPage;