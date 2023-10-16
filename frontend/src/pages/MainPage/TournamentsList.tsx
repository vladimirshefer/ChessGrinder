import {Link} from "react-router-dom";
import {TournamentDto, UserRoles} from "lib/api/dto/MainPageData";
import ConditionalOnUserRole, {Conditional, ConditionalOnAuthorized} from "components/Conditional";
import {useLoc} from "strings/loc";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import {AiFillClockCircle} from "react-icons/ai";
import {IoLocationSharp} from "react-icons/io5";
import "./TournamentsList.css"
import {useQuery} from "@tanstack/react-query";
import tournamentPageRepository from "../../lib/api/repository/TournamentPageRepository";
import {compareBy} from "lib/util/Comparator";
import {ParticipantDto} from "lib/api/dto/TournamentPageData";

function TournamentPane(
    {
        tournament
    }: {
        tournament: TournamentDto
    }
) {
    let loc = useLoc();

    /**
     * TODO migrate to special endpoint instead of tournamentPage
     */
    let winnerQuery = useQuery({
        queryKey: ["tournamentWinner", tournament.id],
        queryFn: async () => {
            let tournamentData = await tournamentPageRepository.getData(tournament.id);
            let participantsSorted: ParticipantDto[] = tournamentData?.participants
                ?.sort(compareBy(it => -it.buchholz))
                ?.sort(compareBy(it => -it.score)) || [];
            console.log("winner", participantsSorted)
            if (participantsSorted.length === 0) {
                return null
            }
            return participantsSorted[0]
        }
    })

    let isPlanned = tournament.status === "PLANNED"
    let isFinished = tournament.status === "FINISHED"
    let isActive = tournament.status === "ACTIVE"
    let winnerName: string = loc("Loading") + "...";
    let winnerResult: string = "..."
    if (winnerQuery.isError) {
        winnerName = loc("Error") + "!";
        winnerResult = "?"
    }
    else if (winnerQuery.isSuccess) {
        let data = winnerQuery.data;
        if (!data) {
            winnerName = loc("No participants");
            winnerResult = "?"
        } else {
            winnerName = data.name
            winnerResult = data.score + "";
        }
    }

    return <div className={`grid justify-items-start w-full p-4 
                ${isPlanned ? "tournament-planned" : isFinished ? "tournament-finished" : "tournament-active"}`}>
        <div className={"grid justify-items-start"}>
            <Link className={"font-bold text-lg text-left"} to={`/tournament/${tournament.id}`}>
                {tournament.name || loc("Unnamed Tournament")}
            </Link>
            <small className={"font-bold text-left"}>
                {tournament.date}
            </small>
        </div>
        <div className={"p-1"}></div>
        <Conditional on={isPlanned || isActive}>
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
        </Conditional>
        <Conditional on={isFinished}>
            <div className={"py-3 flex gap-5 uppercase text-left"}>
                <div className={"grid justify-items-start content-start"}>
                    <span className={"text-sm"}>{loc("Winner")}</span>
                    <span className={"font-bold"}>{winnerName}</span>
                </div>
                <div className={"grid justify-items-start content-start"}>
                    <span className={"text-sm"}>{loc("Result")}</span>
                    <span className={"font-bold"}>{winnerResult}</span>
                </div>
            </div>
        </Conditional>
        <div className={"p-1"}></div>
        <Conditional on={isPlanned || isActive}>
            <div className={"w-full"}>
                <ConditionalOnAuthorized>
                    <button className={"btn-primary w-full uppercase"}
                            onClick={() => tournamentRepository.participate(tournament.id)}
                    >
                        {loc("Participate")}
                    </button>
                </ConditionalOnAuthorized>
                <ConditionalOnAuthorized authorized={false}>
                    <Link to={"/login"} className={"w-full"}>
                        <button className={"btn-primary w-full uppercase"}>
                            {loc("Participate")}
                        </button>
                    </Link>
                </ConditionalOnAuthorized>
            </div>
        </Conditional>
        <Conditional on={isFinished}>
            <div>
                <Link to={`/tournament/${tournament.id}`} className={""}>
                    <button className={"btn-dark w-full text-sm !px-4"}>
                        {loc("More info")}
                    </button>
                </Link>
            </div>
        </Conditional>
    </div>;
}

export function TournamentsList(
    {
        tournaments,
        createTournament,
    }: {
        tournaments: TournamentDto[]
        createTournament: () => void
    }
) {
    let loc = useLoc()

    return <div>
        <h2 className={"text-xl my-2 text-left px-3 uppercase font-semibold"}>{loc("Tournaments")}</h2>
        <ul className={"grid grid-cols-12 gap-2"}>
            <ConditionalOnUserRole role={UserRoles.ADMIN}>
                <li className={"col-span-12 md:col-span-6 lg:col-span-4 xl:col-span-3 " +
                    "bg-gray-200 flex justify-center content-center" }>
                    <button className={"h-full w-full text-xl text-anzac-600 p-3"}
                            onClick={createTournament}
                    >
                        + {loc("Tournament")}
                    </button>
                </li>
            </ConditionalOnUserRole>
            {tournaments.map(tournament => {
                return <li key={tournament.id}
                           className={`col-span-12 md:col-span-6 lg:col-span-4 xl:col-span-3 
                                       flex content-center`}>
                    <TournamentPane tournament={tournament}/>
                </li>
            })}
        </ul>
    </div>;
}

export default TournamentsList
