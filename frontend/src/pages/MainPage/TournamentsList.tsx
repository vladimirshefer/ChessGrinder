import {Link} from "react-router-dom";
import {TournamentDto} from "lib/api/dto/MainPageData";

export function TournamentsList(
    {
        tournaments
    }: {
        tournaments: TournamentDto[]
    }
) {
    return <div>
        <h2>Tournaments</h2>
        <ul>
            {tournaments.map(tournament => {
                return <li key={tournament.id}>
                    <Link to={`/tournament/${tournament.id}`}>
                        {tournament.name || tournament.id}
                    </Link>
                    <br/>
                    <small>
                        {tournament.date}
                    </small>
                </li>
            })}
        </ul>
    </div>;
}

export default TournamentsList
