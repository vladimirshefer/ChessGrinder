import {Link} from "react-router-dom";
import {TournamentDto} from "lib/api/dto/MainPageData";
import ConditionalOnUserRole from "components/ConditionalOnUserRole";
import loc from "strings/loc";
import tournamentRepository from "lib/api/repository/TournamentRepository";

export function TournamentsList(
    {
        tournaments,
        createTournament,
    }: {
        tournaments: TournamentDto[]
        createTournament: () => void
    }
) {

    return <div>
        <h2 className={"text-xl my-2"}>{loc("Tournaments")}</h2>
        <ul className={"grid grid-cols-12"}>
            <ConditionalOnUserRole role={"ROLE_ADMIN"}>
                <li className={"col-span-12 sm:col-span-6 md:col-span-4 lg:col-span-3 rounded-md text-blue-800 h-[20vh] bg-gray-100 m-2 py-2 flex justify-center content-center"}>
                    <div>
                        <button className={"h-full w-full"}
                                onClick={createTournament}
                        >
                            + {loc("Tournament")}
                        </button>
                    </div>
                </li>
            </ConditionalOnUserRole>
            {tournaments.map(tournament => {
                return <li key={tournament.id}
                           className={"col-span-12 sm:col-span-6 md:col-span-4 lg:col-span-3 rounded-md h-[20vh] bg-gray-100 m-2 py-2 flex justify-center content-center"}>
                    <div className={"grig grid-cols-12"}>
                        <Link to={`/tournament/${tournament.id}`}>
                            {tournament.name || tournament.id}
                        </Link>
                        <br/>
                        <small>
                            {tournament.date}
                        </small>
                        <div className={"col-span-12 p-2"}>
                            <button
                                onClick={() => tournamentRepository.participate(tournament.id)}
                            >
                                {loc("Participate")}
                            </button>
                        </div>
                    </div>
                </li>
            })}
        </ul>
    </div>;
}

export default TournamentsList
