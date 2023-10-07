import {Link} from "react-router-dom";
import {TournamentDto, UserRoles} from "lib/api/dto/MainPageData";
import ConditionalOnUserRole, {ConditionalOnAuthorized} from "components/Conditional";
import loc from "strings/loc";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import {AiFillClockCircle} from "react-icons/ai";
import {IoLocationSharp} from "react-icons/io5";

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
            <ConditionalOnUserRole role={UserRoles.ADMIN}>
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
                           className={`col-span-12 sm:col-span-6 md:col-span-4 lg:col-span-3 bg-black text-white 
                                        m-2 flex content-center`}>
                    <div className={"grig grid-cols-12 w-full p-4"}>
                        <div className={"col-span-12 grid justify-items-start"}>
                            <Link className={"font-bold text-lg text-left"} to={`/tournament/${tournament.id}`}>
                                {tournament.name || tournament.id}
                            </Link>
                            <small className={"font-bold text-left"}>
                                {tournament.date}
                            </small>
                        </div>
                        <div className={"py-3"}>
                            <span className={"flex items-center"}>
                                <AiFillClockCircle className={"text-primary mr-3"}/>
                                20:00
                            </span>
                            <span className={"flex items-center"}>
                                <IoLocationSharp className={"text-primary mr-3"}/>
                                Seven roads
                            </span>
                        </div>
                        <div className={"col-span-12"}>
                            <ConditionalOnAuthorized>
                                <button className={"btn-primary w-full"}
                                        onClick={() => tournamentRepository.participate(tournament.id)}
                                >
                                    {loc("Participate")}
                                </button>
                            </ConditionalOnAuthorized>
                            <ConditionalOnAuthorized authorized={false}>
                                <Link to={"/login"}>
                                    <button className={"btn-primary w-full"}>
                                        {loc("Participate")}
                                    </button>
                                </Link>
                            </ConditionalOnAuthorized>
                        </div>
                    </div>
                </li>
            })}
        </ul>
    </div>;
}

export default TournamentsList
