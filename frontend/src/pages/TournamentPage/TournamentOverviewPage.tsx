import {Link, useNavigate, useParams} from "react-router-dom";
import {useQuery} from "@tanstack/react-query";
import dayjs from "dayjs";
import tournamentPageRepository from "lib/api/repository/TournamentPageRepository";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import participantRepository from "lib/api/repository/ParticipantRepository";
import {TournamentPageData} from "lib/api/dto/TournamentPageData";
import {DEFAULT_DATETIME_FORMAT} from "lib/api/dto/MainPageData";
import {Conditional} from "components/Conditional";
import {useAuthenticatedUser} from "contexts/AuthenticatedUserContext";
import useLoginPageLink from "lib/react/hooks/useLoginPageLink";
import {useLoc} from "strings/loc";
import {usePageTitle} from "lib/react/hooks/usePageTitle";
import TournamentLocationMap from "pages/TournamentPage/TournamentLocationMap";
import {IconTag} from "pages/MainPage/TournamentPane";
import {AiFillClockCircle} from "react-icons/ai";
import {IoLocationSharp} from "react-icons/io5";
import {FaUserCheck, FaUsers} from "react-icons/fa6";
import {BsFillRecordFill} from "react-icons/bs";
import {GiChessKnight} from "react-icons/gi";
import {MdEvent} from "react-icons/md";

export default function TournamentOverviewPage() {
    const {id} = useParams();
    const loc = useLoc();
    const navigate = useNavigate();
    const loginPageLink = useLoginPageLink();
    const [authenticatedUser] = useAuthenticatedUser();
    const isAuthenticatedUser = !!authenticatedUser;

    const tournamentQuery = useQuery({
        queryKey: ["tournamentPageData", id],
        queryFn: () => id ? tournamentPageRepository.getData(id) : Promise.reject<TournamentPageData>(new Error()),
    });

    const meParticipantQuery = useQuery({
        queryKey: ["meParticipant", id],
        queryFn: async () => id == null ? null : await participantRepository.getMe(id).catch(() => null),
    });

    const tournament = tournamentQuery.data?.tournament;

    usePageTitle(`${tournament?.name || "Unnamed"} - ${tournament?.date ? dayjs(tournament.date, DEFAULT_DATETIME_FORMAT).format("DD.MM.YYYY") : ""} - Tournament - ChessGrinder`, [tournamentQuery.data]);

    if (!id) return <>No tournament id!</>;
    if (tournamentQuery.isLoading) return <>Loading...</>;
    if (tournamentQuery.isError || !tournament) return <>Error! tid: {id}</>;

    const hideParticipateButton = !meParticipantQuery.isSuccess || !!meParticipantQuery.data;
    const isMeParticipating = !!meParticipantQuery?.data?.id && !meParticipantQuery?.data?.isMissing;

    async function participate() {
        const nickname = window.prompt("Please enter your nickname");
        if (!nickname) {
            alert("Nickname is not provided. Registration is cancelled.");
            return;
        }
        await tournamentRepository.participate(tournament!.id, nickname)
            .catch(() => alert("Could not participate in tournament"));
        await meParticipantQuery.refetch();
        await tournamentQuery.refetch();
    }

    async function leaveTournament() {
        if (!window.confirm("You want to be removed from the tournament?")) {
            alert("You stay in the tournament.");
            return;
        }
        await participantRepository.missMe(tournament!.id)
            .catch(() => alert("Could not withdraw from tournament"));
        await meParticipantQuery.refetch();
        await tournamentQuery.refetch();
    }

    const participantsCount = tournamentQuery.data?.participants?.length ?? null;

    return <>
        <div className={"flex mt-4 p-2 items-center content-center"}>
            <h2 className={"text-lg font-semibold text-left grow flex items-center gap-2"}>
                <span>{tournament.name || loc(`Unnamed Tournament`)}</span>
                <Conditional on={tournament.status === "ACTIVE"}>
                    <span className={"text-red-500"}><BsFillRecordFill/></span>
                </Conditional>
            </h2>
            <div className={"px-2 text-right"}>
                <div><small className={"font-semibold text-gray-500"}>{tournament.status}</small></div>
                <div><span className={"font-semibold"}>{tournament.date && dayjs(tournament.date).format("DD.MM.YY")}</span></div>
            </div>
        </div>

        <div className={"p-2 grid gap-2 text-left"}>
            <IconTag
                icon={<MdEvent className={"text-primary-400"}/>}
                text={tournament.date ? dayjs(tournament.date, DEFAULT_DATETIME_FORMAT).format("DD.MM.YYYY") : "—"}
            />
            <IconTag
                icon={<AiFillClockCircle className={"text-primary-400"}/>}
                text={tournament.date ? dayjs(tournament.date, DEFAULT_DATETIME_FORMAT).format("HH:mm") : "—"}
            />
            {(tournament.city || tournament.locationName) && (
                <IconTag
                    icon={<IoLocationSharp className={"text-primary-400"}/>}
                    text={[tournament.locationName, tournament.city].filter(Boolean).join(", ")}
                    link={tournament.locationUrl || undefined}
                />
            )}
            {!!tournament.roundsNumber && (
                <IconTag
                    icon={<GiChessKnight className={"text-primary-400"}/>}
                    text={`${tournament.roundsNumber} ${loc("rounds")}`}
                />
            )}
            <Conditional on={participantsCount != null}>
                <IconTag
                    icon={<FaUsers className={"text-primary-400"}/>}
                    text={<span>{participantsCount}{!!tournament.registrationLimit ?
                        <small>/{tournament.registrationLimit}</small> : null}</span>}
                />
            </Conditional>
            <Conditional on={isMeParticipating}>
                <IconTag
                    icon={<FaUserCheck className={"text-primary-400"}/>}
                    text={loc("Participating")}
                />
            </Conditional>
        </div>

        <div className={"px-2 grid gap-2"}>
            <Conditional on={!hideParticipateButton && tournament.status === "PLANNED"}>
                {isAuthenticatedUser
                    ? <button className={"btn-primary w-full uppercase"} onClick={participate}>{loc("Participate")}</button>
                    : <Link to={loginPageLink} className={"w-full"}>
                        <button className={"btn-primary w-full uppercase"}>{loc("Participate")}</button>
                    </Link>}
            </Conditional>
            <Conditional on={isAuthenticatedUser && isMeParticipating && tournament.status !== "FINISHED"}>
                <button className={"btn-light uppercase w-full"} onClick={leaveTournament}>
                    {loc("Leave the tournament")}
                </button>
            </Conditional>
            <button className={"btn-primary w-full uppercase"} onClick={() => navigate(`/tournament/${id}/pairings`)}>
                {loc("Pairings")}
            </button>
        </div>

        <div className={"p-2"}>
            <TournamentLocationMap city={tournament.city}/>
        </div>
    </>;
}
