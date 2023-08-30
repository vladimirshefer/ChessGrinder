import {Link} from "react-router-dom";
import {TournamentDto} from "lib/api/dto/MainPageData";

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
        <h2>Tournaments</h2>
        <ul className={"grid grid-cols-12"}>
            <li className={"col-span-3 rounded-md text-blue-800 h-[20vh] bg-gray-100 m-2 py-2 flex justify-center content-center"}>
                <div>
                    <button className={"h-full w-full"}
                            onClick={createTournament}
                    >
                        + Tournament
                    </button>
                </div>
            </li>
            {tournaments.map(tournament => {
                return <li key={tournament.id}
                           className={"col-span-3 rounded-md h-[20vh] bg-gray-100 m-2 py-2 flex justify-center content-center"}>
                    <div>
                        <Link to={`/tournament/${tournament.id}`}>
                            {tournament.name || tournament.id}
                        </Link>
                        <br/>
                        <small>
                            {tournament.date}
                        </small>
                    </div>
                </li>
            })}
        </ul>
    </div>;
}

export default TournamentsList
