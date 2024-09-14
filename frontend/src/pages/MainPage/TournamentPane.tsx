import {DEFAULT_DATETIME_FORMAT, TournamentDto} from "lib/api/dto/MainPageData";
import {useLoc} from "strings/loc";
import {useQuery} from "@tanstack/react-query";
import tournamentPageRepository from "lib/api/repository/TournamentPageRepository";
import {ParticipantDto} from "lib/api/dto/TournamentPageData";
import {Link} from "react-router-dom";
import {BsBookmarkCheckFill, BsFillRecordFill} from "react-icons/bs";
import dayjs from "dayjs";
import {Conditional, ConditionalOnAuthorized} from "components/Conditional";
import {AiFillClockCircle} from "react-icons/ai";
import {IoLocationSharp} from "react-icons/io5";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import participantRepository from "lib/api/repository/ParticipantRepository";
import useLoginPageLink from "lib/react/hooks/useLoginPageLink";

export function TournamentPane(
    {
        tournament
    }: {
        tournament: TournamentDto
    }
) {
    let loc = useLoc();
    let loginPageLink = useLoginPageLink();

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

    let isMeParticipating = !!meParticipantQuery?.data?.id;

    return <div className={`grid justify-items-start w-full p-4 
                ${isPlanned ? "tournament-planned" : isFinished ? "tournament-finished" : "tournament-active"}`}>
        <div className={"grid w-full justify-items-start"}>
            <Link className={"flex w-full gap-2 text-lg text-left justify-between items-center"}
                  to={`/tournament/${tournament.id}`}
            >
                <div className="flex gap-2 hover:underline font-semibold">
                    <span className={"grow"}>
                        {tournament.name || loc("Unnamed Tournament")}
                    </span>
                    <Conditional on={tournament.status === "ACTIVE"}>
                        <span className={"text-red-500"}><BsFillRecordFill/></span>
                    </Conditional>
                </div>
                <Conditional on={isFinished && isMeParticipating}>
                    <BsBookmarkCheckFill title={loc("Participating")}/>
                </Conditional>
            </Link>
            <small className={"font-bold text-left"}>
                {dayjs(tournament.date, DEFAULT_DATETIME_FORMAT).format("DD.MM.YYYY")}
            </small>
        </div>
        <div className={"p-1"}></div>
        <Conditional on={isPlanned || isActive}>
            <div className={"py-3"}>
                <IconTag
                    icon={<AiFillClockCircle className={"text-primary-400"}/>}
                    text={dayjs(tournament.date, DEFAULT_DATETIME_FORMAT).format("HH:mm")}
                />
                {(!!tournament.locationName) && (
                    <IconTag
                        icon={<IoLocationSharp className={"text-primary-400"}/>}
                        text={tournament.locationName || ""}
                        link={tournament.locationUrl || undefined}
                    />
                )}
                <Conditional on={isMeParticipating}>
                    <IconTag
                        icon={<BsBookmarkCheckFill className={"text-primary-400"}/>}
                        text={loc("Participating")}
                    />
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
            <div className={"w-full flex"}>
                <ConditionalOnAuthorized>{() =>
                    !isMeParticipating ? (
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
                        <Link className={"btn-primary w-full uppercase"} to={`/tournament/${tournament.id}`}>
                            {loc("Open")}
                        </Link>
                    )
                }
                </ConditionalOnAuthorized>
                <ConditionalOnAuthorized authorized={false}>{() =>
                    <Link to={loginPageLink} className={"w-full"}>
                        <button className={"btn-primary w-full uppercase"}>
                            {loc("Participate")}
                        </button>
                    </Link>
                }
                </ConditionalOnAuthorized>
            </div>
        </Conditional>
        <Conditional on={isActive}>
            <Link className={"btn-primary w-full uppercase"}
                  to={`/tournament/${tournament.id}`}
            >
                {loc("Open")}
            </Link>
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


export function IconTag(
    {
        icon,
        text,
        link = undefined,
    }: {
        icon: any,
        text: any,
        link?: string | undefined,
    }
) {
    let textElement = <span className={"grow text-ellipsis overflow-hidden line-clamp-1"}>
        {text}
    </span>;

    return <span className={"flex items-center text-left gap-1"}>
        <span>{icon}</span>
        {(!!link) && (
            <Link to={link} target={"_blank"}>
                {textElement}
            </Link>
        )}
        {(!link) && (
            textElement
        )}
    </span>
}
