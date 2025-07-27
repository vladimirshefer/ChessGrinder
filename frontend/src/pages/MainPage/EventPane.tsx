import {DEFAULT_DATETIME_FORMAT} from "lib/api/dto/MainPageData";
import {TournamentEventDto} from "lib/api/dto/TournamentEventData";
import {useLoc} from "strings/loc";
import {Link} from "react-router-dom";
import {BsBookmarkCheckFill, BsFillRecordFill} from "react-icons/bs";
import dayjs from "dayjs";
import {Conditional, ConditionalOnAuthorized} from "components/Conditional";
import {AiFillClockCircle} from "react-icons/ai";
import {IoLocationSharp} from "react-icons/io5";
import tournamentEventRepository from "lib/api/repository/TournamentEventRepository";
import {useQuery} from "@tanstack/react-query";
import useLoginPageLink from "lib/react/hooks/useLoginPageLink";
import {IconTag} from "./TournamentPane";

export function EventPane(
    {
        event
    }: {
        event: TournamentEventDto
    }
) {
    let loc = useLoc();
    let loginPageLink = useLoginPageLink();

    let meParticipantQuery = useQuery({
        queryKey: ["meEventParticipant", event.id],
        queryFn: async () => {
            // Check if the current user is registered for this event
            // This is a placeholder - you might need to implement this in your repository
            try {
                const eventData = await tournamentEventRepository.getTournamentEvent(event.id);
                const currentUser = localStorage.getItem("cgd.auth") ? 
                    JSON.parse(localStorage.getItem("cgd.auth") || "{}").username : null;
                
                if (!currentUser || !eventData.participants) return null;
                
                return eventData.participants.find(p => p.userId === currentUser) || null;
            } catch (error) {
                return null;
            }
        }
    });

    let isPlanned = event.status === "PLANNED";
    let isFinished = event.status === "FINISHED";
    let isActive = event.status === "ACTIVE";
    
    let isMeParticipating = !!meParticipantQuery?.data?.id;

    return <div className={`grid justify-items-start w-full p-4 
                ${isPlanned ? "tournament-planned" : isFinished ? "tournament-finished" : "tournament-active"}`}>
        <div className={"grid w-full justify-items-start"}>
            <Link className={"flex w-full gap-2 text-lg text-left justify-between items-center"}
                  to={`/tournament-event/${event.id}`}
            >
                <div className="flex gap-2 hover:underline font-semibold">
                    <span className={"grow"}>
                        {event.name || loc("Unnamed Event")}
                    </span>
                    <Conditional on={event.status === "ACTIVE"}>
                        <span className={"text-red-500"}><BsFillRecordFill/></span>
                    </Conditional>
                </div>
                <Conditional on={isFinished && isMeParticipating}>
                    <BsBookmarkCheckFill title={loc("Participating")}/>
                </Conditional>
            </Link>
            <small className={"font-bold text-left"}>
                {dayjs(event.date, DEFAULT_DATETIME_FORMAT).format("DD.MM.YYYY")}
            </small>
        </div>
        <div className={"p-1"}></div>
        <Conditional on={isPlanned || isActive}>
            <div className={"py-3"}>
                <IconTag
                    icon={<AiFillClockCircle className={"text-primary-400"}/>}
                    text={dayjs(event.date, DEFAULT_DATETIME_FORMAT).format("HH:mm")}
                />
                {(!!event.locationName) && (
                    <IconTag
                        icon={<IoLocationSharp className={"text-primary-400"}/>}
                        text={event.locationName || ""}
                        link={event.locationUrl || undefined}
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
                                        await tournamentEventRepository.registerParticipant(event.id, nickname)
                                            .catch(() => alert("Could not register for event"));
                                        await meParticipantQuery.refetch()
                                    }
                                }}
                        >
                            {loc("Register")}
                        </button>
                    ) : (
                        <Link className={"btn-primary w-full uppercase"} to={`/tournament-event/${event.id}`}>
                            {loc("Open")}
                        </Link>
                    )
                }
                </ConditionalOnAuthorized>
                <ConditionalOnAuthorized authorized={false}>{() =>
                    <Link to={loginPageLink} className={"w-full"}>
                        <button className={"btn-primary w-full uppercase"}>
                            {loc("Register")}
                        </button>
                    </Link>
                }
                </ConditionalOnAuthorized>
            </div>
        </Conditional>
        <Conditional on={isActive}>
            <Link className={"btn-primary w-full uppercase"}
                  to={`/tournament-event/${event.id}`}
            >
                {loc("Open")}
            </Link>
        </Conditional>
        <Conditional on={isFinished}>
            <div>
                <Link to={`/tournament-event/${event.id}`} className={""}>
                    <button className={"btn-dark w-full text-sm !px-4"}>
                        {loc("More info")}
                    </button>
                </Link>
            </div>
        </Conditional>
    </div>;
}

export default EventPane;