import {DEFAULT_DATETIME_FORMAT, TournamentDto} from "lib/api/dto/MainPageData";
import {useLoc} from "strings/loc";
import {useQuery} from "@tanstack/react-query";
import tournamentPageRepository from "lib/api/repository/TournamentPageRepository";
import {ParticipantDto} from "lib/api/dto/TournamentPageData";
import {Link, useLocation} from "react-router-dom";
import {FiArrowUpRight} from "react-icons/fi";
import {BsFillRecordFill} from "react-icons/bs";
import dayjs from "dayjs";
import {Conditional, ConditionalOnAuthorized} from "components/Conditional";
import {AiFillClockCircle} from "react-icons/ai";
import {IoLocationSharp} from "react-icons/io5";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import participantRepository from "lib/api/repository/ParticipantRepository";

export function TournamentPane(
    {
        tournament
    }: {
        tournament: TournamentDto
    }
) {
    let loc = useLoc();
    let location = useLocation()

    /**
     * TODO migrate to special endpoint instead of tournamentPage
     */
    let winnerQuery = useQuery({
        queryKey: ["tournamentWinner", tournament.id],
        queryFn: async () => {
            let tournamentData = await tournamentPageRepository.getData(tournament.id);
            // sorted on server side
            let participantsSorted: ParticipantDto[] = tournamentData?.participants || [];
            if (participantsSorted.length === 0) {
                return null
            }
            return participantsSorted[0]
        }
    })

    let meParticipantQuery = useQuery({
        queryKey: ["meParticipant", tournament.id],
        queryFn: async () => {
            return await participantRepository.getMe(tournament.id)
                .catch(() => null);
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
    } else if (winnerQuery.isSuccess) {
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
            <Link className={"flex gap-2 hover:underline font-semibold text-lg text-left items-center"}
                  to={`/tournament/${tournament.id}`}
            >
                <span className={"grow"}>{tournament.name || loc("Unnamed Tournament")}
                    <FiArrowUpRight className={"inline-block ml-1 -mt-[1px]"}/>
                </span>
                {tournament.status === "ACTIVE" &&
                    <span className={"text-red-500"}><BsFillRecordFill/></span>
                }
            </Link>
            <small className={"font-bold text-left"}>
                {dayjs(tournament.date, DEFAULT_DATETIME_FORMAT).format("DD.MM.YYYY")}
            </small>
        </div>
        <div className={"p-1"}></div>
        <Conditional on={isPlanned || isActive}>
            <div className={"py-3"}>
                <span className={"flex items-center"}>
                    <AiFillClockCircle className={"text-primary mr-3"}/>
                    {dayjs(tournament.date, DEFAULT_DATETIME_FORMAT).format("HH:mm")}
                </span>
                <Conditional on={!!tournament.locationName}>
                    {tournament.locationUrl ?
                        <Link to={tournament.locationUrl} target={"_blank"} className={"flex items-center text-left"}>
                            <IoLocationSharp className={"text-primary mr-3"}/>
                            {tournament.locationName || "Seven roads"}
                            <FiArrowUpRight className={"ml-1"}/>
                        </Link>
                        :
                        <span className={"flex items-center text-left"}>
                            <IoLocationSharp className={"text-primary mr-3"}/>
                            {tournament.locationName || "Seven roads"}
                        </span>
                    }
                </Conditional>
            </div>
        </Conditional>
        <Conditional on={isFinished}>
            <div className={"py-3 flex gap-2 uppercase text-left w-full"}>
                <div className={"grid justify-items-start content-start"}>
                    <span className={"text-sm"}>{loc("Winner")}</span>
                    <span className={"font-bold"}>{winnerName}</span>
                </div>
                <div className={"grow"}></div>
                <div className={"grid justify-items-start content-start"}>
                    <span className={"text-sm"}>{loc("Result")}</span>
                    <span className={"font-bold"}>{winnerResult}</span>
                </div>
            </div>
        </Conditional>
        <div className={"p-1"}></div>
        <Conditional on={isPlanned}>
            <div className={"w-full"}>
                <ConditionalOnAuthorized>{
                    !meParticipantQuery?.data ? (
                        <button className={"btn-primary w-full uppercase"}
                                onClick={async () => {
                                    let nickname = prompt("Please enter your nickname");
                                    if (!nickname) {
                                        alert("Nickname is not provided. Registration is cancelled.")
                                    } else {
                                        await tournamentRepository.participate(tournament.id, nickname)
                                            .catch(() => alert("Could not participate in tournament"));
                                        await meParticipantQuery.refetch()
                                    }
                                }}
                        >
                            {loc("Participate")}
                        </button>
                    ) : (
                        <div className={"btn-light w-full uppercase"}>Participating</div>
                    )
                }
                </ConditionalOnAuthorized>
                <ConditionalOnAuthorized authorized={false}>
                    <Link to={"/login?referer=" + location.pathname} className={"w-full"}>
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
