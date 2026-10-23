import {Link, useNavigate, useParams} from "react-router-dom";
import {useQueries, useQuery, useQueryClient} from "@tanstack/react-query";
import dayjs from "dayjs";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import tournamentPageRepository from "lib/api/repository/TournamentPageRepository";
import participantRepository from "lib/api/repository/ParticipantRepository";
import roundRepository from "lib/api/repository/RoundRepository";
import restApiClient from "lib/api/RestApiClient";
import {TournamentDto, UserRoles} from "lib/api/dto/MainPageData";
import {MatchDto, MatchResult, ParticipantDto, TournamentPageData} from "lib/api/dto/TournamentPageData";
import ConditionalOnUserRole, {Conditional} from "components/Conditional";
import {AiOutlineDelete, AiOutlineHome, AiOutlinePlus} from "react-icons/ai";
import {useLoc} from "strings/loc";
import {useAuthenticatedUser} from "contexts/AuthenticatedUserContext";
import useLoginPageLink from "lib/react/hooks/useLoginPageLink";
import {usePageTitle} from "lib/react/hooks/usePageTitle";
import ResultsTable from "pages/TournamentPage/ResultsTable";
import RoundTab from "pages/TournamentPage/RoundTab";
import RoundControlButtons from "pages/TournamentPage/RoundControlButtons";
import MyActiveTournamentPane from "pages/MainPage/MyActiveTournamentPane";
import NicknameContextPane from "./NicknameContextPane";

function EventPage() {
    let {city, date: dateStr, roundId: roundIdStr} = useParams()
    let roundId = roundIdStr ? parseInt(roundIdStr) : null
    let tournamentsQuery = useQuery({
        queryKey: ["tournaments", city, dateStr],
        queryFn: async () => {
            let allTournaments = await tournamentRepository.getTournaments();
            return allTournaments.values
                .filter(it => it.city === city)
                .filter(it => it.date.includes(dateStr || ""));
        },
    })

    usePageTitle(`${city} - ${dateStr} - Event - ChessGrinder`)

    if (!tournamentsQuery.isSuccess) return <>Loading...</>
    let tournaments = tournamentsQuery.data;
    return <div className={"flex flex-col gap-2 w-full"}>
        <div className={"flex mt-4 p-2 items-top content-center"}>
            <h2 className={"text-lg font-semibold text-left grow"}>
                {city}
            </h2>
            <div className={"px-2"}>
                <span className={"font-semibold"}>{dateStr && dayjs(dateStr).format("DD.MM.YY")}</span>
            </div>
        </div>
        <Conditional on={tournaments.length === 0}>
            <div className={"p-2"}>No tournaments found for this event.</div>
        </Conditional>
        <Conditional on={tournaments.length > 0}>
            <EventRoundSelector
                city={city || ""}
                dateStr={dateStr || ""}
                tournaments={tournaments}
                roundId={roundId}
            />
        </Conditional>
        {tournaments.map(it => roundId
            ? <EventRoundSection key={it.id} tournament={it} roundId={roundId}/>
            : <EventTournamentSection key={it.id} tournament={it}/>)}
        <Conditional on={!!roundId && tournaments.length > 0}>
            <ConditionalOnUserRole role={UserRoles.ADMIN}>
                <EventRoundControlButtons
                    tournaments={tournaments}
                    roundId={roundId!}
                    eventPath={`/event/${encodeURIComponent(city || "")}/${dateStr}`}
                />
            </ConditionalOnUserRole>
        </Conditional>
        <ConditionalOnUserRole role={UserRoles.ADMIN}>
            <EventControlButtons tournaments={tournaments} onChanged={() => tournamentsQuery.refetch()}/>
        </ConditionalOnUserRole>
    </div>
}

/**
 * Single round selector for the whole event. Round N of the event is round N of each tournament.
 */
function EventRoundSelector(
    {
        city,
        dateStr,
        tournaments,
        roundId,
    }: {
        city: string,
        dateStr: string,
        tournaments: TournamentDto[],
        roundId: number | null,
    }) {
    let loc = useLoc()
    let queryClient = useQueryClient()
    let pageQueries = useQueries({
        queries: tournaments.map(it => ({
            queryKey: ["tournamentPageData", it.id],
            queryFn: () => tournamentPageRepository.getData(it.id),
        })),
    })
    let maxRounds = Math.max(0, ...pageQueries.map(it => it.data?.rounds?.length || 0))
    let roundNumbers = Array.from({length: maxRounds}, (_, idx) => idx + 1)
    let eventPath = `/event/${encodeURIComponent(city)}/${dateStr}`
    let isMain = !roundId

    async function createRoundForAll() {
        let results = await Promise.allSettled(tournaments
            .filter(it => it.status !== "FINISHED")
            .map(it => roundRepository.postRound(it.id)));
        if (results.some(it => it.status === "rejected")) {
            alert("Could not create round for some of the tournaments.")
        }
        await queryClient.invalidateQueries({queryKey: ["tournamentPageData"]});
    }

    return <div className={"flex flex-wrap text-sm justify-start place-items-stretch w-full px-2 my-2"}>
        <Link to={eventPath} replace={true}>
            <button
                className={`w-full h-full py-1 px-3 border border-black uppercase ${isMain ? "bg-primary-400 text-white" : "hover:bg-gray-300 text-black"}`}
                title={loc("Tournament page")}
            >
                <AiOutlineHome/>
            </button>
        </Link>
        {roundNumbers.map(rid => <Link
            to={`${eventPath}/round/${rid}`} title={loc(`Open round`) + " " + rid}
            key={rid}
            replace={!!roundId}
        >
            <div className={`w-full py-1 px-3 border border-black ${rid === roundId ? "bg-primary-400 text-white" : "hover:bg-gray-300"}`}>
                {rid}
            </div>
        </Link>)}
        <ConditionalOnUserRole role={UserRoles.ADMIN}>
            <button className={`py-1 px-3`} onClick={createRoundForAll} title={loc("New round")}>
                <AiOutlinePlus/>
            </button>
        </ConditionalOnUserRole>
    </div>
}

/**
 * Round of a single tournament of the event (copy of the round view of the tournament page).
 */
function EventRoundSection(
    {
        tournament,
        roundId,
    }: {
        tournament: TournamentDto,
        roundId: number,
    }) {
    let loc = useLoc()
    let id = tournament.id;
    let tournamentQuery = useQuery({
        queryKey: ["tournamentPageData", id],
        queryFn: () => tournamentPageRepository.getData(id)
    })
    let round = tournamentQuery.data?.rounds?.[roundId - 1];

    async function submitMatchResult(match: MatchDto, result: MatchResult) {
        await roundRepository.postMatchResult(id, roundId, match.id, result)
            .catch(e => alert("Could not set match result. " + e?.response?.data?.message));
        await tournamentQuery.refetch()
    }

    return <div className={"flex flex-col w-full mb-4"}>
        <h3 className={"px-2 text-lg font-semibold text-left"}>
            <Link to={`/tournament/${id}/round/${roundId}`}>
                {tournament.name || loc(`Unnamed Tournament`)}
            </Link>
        </h3>
        <div className={"p-2"}>
            {tournamentQuery.isSuccess && !round
                ? <>No round {roundId} in this tournament.</>
                : <RoundTab
                    tournamentId={id}
                    round={round!}
                    submitMatchResult={(match, result) => submitMatchResult(match, result!)}
                    submitRoundFinished={() => {}}
                    deleteRound={() => {}}
                    runPairing={() => {}}
                    reopenRound={() => {}}
                    hideControls={true}
                />}
        </div>
    </div>
}

/**
 * Synthetic event-level round controls (same buttons as on the tournament page):
 * every action is broadcast as a separate API call to the same round of each tournament of the event.
 */
function EventRoundControlButtons(
    {
        tournaments,
        roundId,
        eventPath,
    }: {
        tournaments: TournamentDto[],
        roundId: number,
        eventPath: string,
    }) {
    let navigate = useNavigate()
    let queryClient = useQueryClient()
    let pageQueries = useQueries({
        queries: tournaments.map(it => ({
            queryKey: ["tournamentPageData", it.id],
            queryFn: () => tournamentPageRepository.getData(it.id),
        })),
    })
    let targets = tournaments
        .map((tournament, idx) => ({tournament, round: pageQueries[idx].data?.rounds?.[roundId - 1]}))
        .filter(it => !!it.round)
    let unfinished = targets.filter(it => !it.round!.isFinished).map(it => it.tournament)
    let finished = targets.filter(it => !!it.round!.isFinished).map(it => it.tournament)

    async function broadcast(
        actionName: string,
        tournamentsToChange: TournamentDto[],
        action: (tournament: TournamentDto) => Promise<unknown>,
    ) {
        let results = await Promise.allSettled(tournamentsToChange.map(it => action(it)));
        let failures = results
            .map((result, idx) => ({result, tournament: tournamentsToChange[idx]}))
            .filter(it => it.result.status === "rejected");
        if (failures.length > 0) {
            alert(`${actionName} failed for:\n` + failures.map(it => {
                let reason = (it.result as PromiseRejectedResult).reason;
                return `${it.tournament.name || it.tournament.id}: ${reason?.response?.data?.message || "Unknown error"}`;
            }).join("\n"));
        }
        await queryClient.invalidateQueries({queryKey: ["tournamentPageData"]});
    }

    return <div className={"px-2"}>
        <RoundControlButtons
            hasUnfinishedRounds={unfinished.length > 0}
            hasFinishedRounds={finished.length > 0}
            runPairing={() => {
                broadcast("Pairing", unfinished, it => roundRepository.runPairing(it.id, roundId)).catch(console.error)
            }}
            submitRoundFinished={() => {
                broadcast("Finish round", unfinished, it => roundRepository.finishRound(it.id, roundId)).catch(console.error)
            }}
            reopenRound={() => {
                broadcast("Reopen round", finished, it => roundRepository.reopenRound(it.id, roundId)).catch(console.error)
            }}
            deleteRound={() => {
                broadcast("Delete round", targets.map(it => it.tournament),
                    it => roundRepository.deleteRound(it.id, roundId))
                    .then(() => navigate(eventPath))
                    .catch(console.error)
            }}
        />
    </div>
}

/**
 * Main block of the tournament page (copy of it, without round tabs and control buttons) for a single tournament of the event.
 */
function EventTournamentSection({tournament}: { tournament: TournamentDto }) {
    let loc = useLoc()
    let id = tournament.id;
    let navigate = useNavigate()
    let loginPageLink = useLoginPageLink();
    let [authenticatedUser] = useAuthenticatedUser()
    let isAuthenticatedUser = !!authenticatedUser

    let tournamentQuery = useQuery({
        queryKey: ["tournamentPageData", id],
        queryFn: () => tournamentPageRepository.getData(id)
    })
    let meParticipantQuery = useQuery({
        queryKey: ["meParticipant", id],
        queryFn: async () => participantRepository.getMe(id).catch(() => null)
    })

    let tournamentData: TournamentPageData | null | undefined = tournamentQuery.data;
    let participants: ParticipantDto[] = tournamentData?.participants || [];

    let hideParticipateButton = !meParticipantQuery.isSuccess || !!meParticipantQuery.data
    let isTournamentFinished = tournament.status === "FINISHED"

    async function openParticipant(participant: ParticipantDto) {
        navigate(`/tournament/${id}/participant/${participant.id}`)
    }

    async function participate() {
        let nickname = window.prompt("Please enter your nickname");
        if (!nickname) {
            alert("Nickname is not provided. Registration is cancelled.")
        } else {
            await tournamentRepository.participate(id, nickname)
                .catch(() => alert("Could not participate in tournament"));
            await meParticipantQuery.refetch()
            await tournamentQuery.refetch()
        }
    }

    async function leaveTournament() {
        if (!window.confirm("You want to be removed from the tournament?")) {
            alert("You stay in the tournament.")
        } else {
            await participantRepository.missMe(id)
                .catch(() => alert(`Could not withdraw from tournament`));
            await meParticipantQuery.refetch()
            await tournamentQuery.refetch()
        }
    }

    return <div className={"flex flex-col w-full mb-4"}>
        {tournament.status === "ACTIVE" && !meParticipantQuery.data?.isMissing && (
            <MyActiveTournamentPane tournamentId={id}/>
        )}
        <div className={"p-2"}>
            <NicknameContextPane tournamentId={id}/>
        </div>
        {!hideParticipateButton && tournament.status === "PLANNED" && (
            <div className={"px-2"}>
                {isAuthenticatedUser ?
                    (
                        <button className={"btn-primary w-full uppercase"} onClick={participate}>
                            {loc("Participate")}
                        </button>
                    ) : (
                        <Link to={loginPageLink} className={"w-full"}>
                            <button className={"btn-primary w-full uppercase"}>
                                {loc("Participate")}
                            </button>
                        </Link>
                    )
                }
            </div>
        )}

        <ResultsTable participants={participants} openParticipant={openParticipant}/>

        <Conditional on={isAuthenticatedUser && !!meParticipantQuery.data && !meParticipantQuery.data?.isMissing}>
            <Conditional on={!isTournamentFinished}>
                <div className={"grid 2-full p-2"}>
                    <button
                        className={"btn-light uppercase w-full"}
                        onClick={leaveTournament}
                        title={loc("Leave the tournament")}
                    >
                        {loc("Leave the tournament")}
                    </button>
                </div>
            </Conditional>
        </Conditional>
    </div>
}

/**
 * Synthetic event-level controls: every action is broadcast as a separate API call to each tournament of the event.
 */
function EventControlButtons(
    {
        tournaments,
        onChanged,
    }: {
        tournaments: TournamentDto[],
        onChanged: () => unknown,
    }) {
    let loc = useLoc()
    let queryClient = useQueryClient()

    async function broadcast(
        actionName: string,
        targets: TournamentDto[],
        action: (tournament: TournamentDto) => Promise<unknown>,
    ) {
        let results = await Promise.allSettled(targets.map(it => action(it)));
        let failures = results
            .map((result, idx) => ({result, tournament: targets[idx]}))
            .filter(it => it.result.status === "rejected");
        if (failures.length > 0) {
            alert(`${actionName} failed for:\n` + failures.map(it => {
                let reason = (it.result as PromiseRejectedResult).reason;
                return `${it.tournament.name || it.tournament.id}: ${reason?.response?.data?.message || "Unknown error"}`;
            }).join("\n"));
        }
        await queryClient.invalidateQueries({queryKey: ["tournamentPageData"]});
        await onChanged();
    }

    async function startNicknameContest() {
        await broadcast("Run nickname contest", tournaments,
            it => restApiClient.post(`/nickname-contest/${it.id}`));
    }

    async function startAll() {
        await broadcast("Start", tournaments.filter(it => it.status !== "ACTIVE"),
            it => tournamentRepository.startTournament(it.id));
    }

    async function finishAll() {
        await broadcast("Finish", tournaments.filter(it => it.status === "ACTIVE"),
            it => tournamentRepository.finishTournament(it.id));
    }

    async function deleteAll() {
        let confirmation = window.prompt(`Are you sure?\nTo delete ALL ${tournaments.length} tournaments of the event enter \nDELETE`);
        if (confirmation !== "DELETE") {
            alert("You entered wrong id. Tournaments will not be deleted.");
            return;
        }
        await broadcast("Delete", tournaments, it => tournamentRepository.deleteTournament(it.id));
    }

    return <div className={"flex flex-col p-2 gap-2"}>
        <div className={"flex gap-2 items-top content-center"}>
            <div className="flex flex-col gap-2 justify-start grow">
                <button
                    onClick={startNicknameContest}
                    className={"btn-primary uppercase"}
                >
                    {"Run nickname contest!"}
                </button>
            </div>
            <div className={"flex gap-2 justify-end"}>
                <Conditional on={tournaments.some(it => it.status !== "ACTIVE")}>
                    <button className={"btn-primary uppercase"} onClick={startAll}>
                        {loc("Start")}
                    </button>
                </Conditional>
                <Conditional on={tournaments.some(it => it.status === "ACTIVE")}>
                    <button className={"btn-primary uppercase"} onClick={finishAll}>
                        {loc("Finish")}
                    </button>
                </Conditional>
                <button
                    className={"btn-danger uppercase"}
                    onClick={deleteAll}
                    title={loc("Delete")}
                >
                    <AiOutlineDelete/>
                </button>
            </div>
        </div>
    </div>
}

export default EventPage;
