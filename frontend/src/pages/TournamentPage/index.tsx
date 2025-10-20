import {Link, useNavigate, useParams} from "react-router-dom";
import React, {useMemo} from "react";
import ResultsTable from "pages/TournamentPage/ResultsTable";
import {useQuery} from "@tanstack/react-query";
import tournamentPageRepository from "lib/api/repository/TournamentPageRepository";
import {MatchDto, MatchResult, ParticipantDto, TournamentPageData} from "lib/api/dto/TournamentPageData";
import RoundTab from "pages/TournamentPage/RoundTab";
import {Conditional, usePermissionGranted} from "components/Conditional";
import participantRepository from "lib/api/repository/ParticipantRepository";
import {AiOutlineDelete, AiOutlineEdit, AiOutlineHome, AiOutlinePlus} from "react-icons/ai";
import {useLoc} from "strings/loc";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import dayjs from "dayjs";
import roundRepository from "lib/api/repository/RoundRepository";
import AddParticipantTournamentPageSection from "pages/TournamentPage/AddParticipantTournamentPageSection";
import {useAuthenticatedUser} from "contexts/AuthenticatedUserContext";
import useLoginPageLink from "lib/react/hooks/useLoginPageLink";
import MyActiveTournamentPane from "pages/MainPage/MyActiveTournamentPane";
import restApiClient from "lib/api/RestApiClient";
import {usePageTitle} from "lib/react/hooks/usePageTitle";
import {DEFAULT_DATETIME_FORMAT, TournamentDto} from "lib/api/dto/MainPageData";
import NicknameContextPane from "./NicknameContextPane";
import ShareDropdownButton from "pages/TournamentPage/ShareDropdownButton";

function TournamentPage() {
    let {id, roundId: roundIdStr} = useParams();
    let roundId = useMemo(() => roundIdStr ? parseInt(roundIdStr) : null, [roundIdStr]);
    let tournamentQuery = useQuery({
        queryKey: ["tournamentPageData", id],
        queryFn: () => id ? tournamentPageRepository.getData(id) : Promise.reject<TournamentPageData>(new Error())
    })

    let tournament = tournamentQuery.data?.tournament;

    if (!id) return <>No tournament id!</>
    if (tournamentQuery.isLoading) return <>Loading...</>
    if (tournamentQuery.isError || !tournament) return <>Error! tid: {id}</>

    return <TournamentPageImpl
        tournament={tournament}
        roundId={roundId}
    />;
}

export function TournamentPageImpl(
    {
        roundId = null,
        tournament,
    }: {
        roundId?: number | null,
        tournament: TournamentDto,
    }) {
    let loc = useLoc()
    let id = tournament.id;
    let loginPageLink = useLoginPageLink();
    let tournamentQuery = useQuery({
        queryKey: ["tournamentPageData", id],
        queryFn: () => id ? tournamentPageRepository.getData(id) : Promise.reject<TournamentPageData>(new Error())
    })
    let isMain = !roundId

    usePageTitle(`${tournament.name || "Unnamed"} - ${dayjs(tournament.date, DEFAULT_DATETIME_FORMAT).format("DD.MM.YYYY")} - Tournament - ChessGrinder`, [tournamentQuery.data])

    let [authenticatedUser] = useAuthenticatedUser()
    let isAuthenticatedUser = !!authenticatedUser

    let meParticipantQuery = useQuery({
        queryKey: ["meParticipant", id],
        queryFn: async () => {
            if (id == null) {
                return null;
            }

            return await participantRepository.getMe(id)
                .catch(() => null);
        }
    })

    let {data: tournamentData} = tournamentQuery;

    let isMeModerator = usePermissionGranted(id || "", "TournamentEntity", "MODERATOR");

    let navigate = useNavigate()
    let roundNumbers = tournamentQuery.data?.rounds?.map((e, idx) => idx + 1) || [];
    let participants: ParticipantDto[] = tournamentQuery.data?.participants || [];

    async function addParticipant(participant: ParticipantDto) {
        await participantRepository.postParticipant(id, participant)
            .catch(e => alert("Could not add participant. " +
                e?.response?.data?.message))
        await tournamentQuery.refetch()
    }

    async function openParticipant(participant: ParticipantDto) {
        navigate(`/tournament/${id}/participant/${participant.id}`)
    }

    async function createRound() {
        try {
            await roundRepository.postRound(id)
            await tournamentQuery.refetch();
            let newRoundSubPath = tournamentData && tournamentData.rounds ? `/round/${tournamentData.rounds.length + 1}` : "";
            navigate(`/tournament/${id}${newRoundSubPath}`, {replace: true});
        } catch (e: any) {
            alert("Could not create round. " +
                e?.response?.data?.message);
        }
    }

    async function runPairingForRound() {
        await roundRepository.runPairing(id, roundId!)
            .catch(e => alert("Pairing failed! " +
                e?.response?.data?.message));
        await tournamentQuery.refetch()
    }

    async function submitMatchResult(match: MatchDto, result: MatchResult) {
        await roundRepository.postMatchResult(id, roundId!, match.id, result)
            .catch(e => alert("Could not set match result. " +
                e?.response?.data?.message));

        await tournamentQuery.refetch()
    }

    async function deleteRound() {
        await roundRepository.deleteRound(id, roundId!)
            .catch(e => alert(loc("Could not delete round. " +
                e?.response?.data?.message)))
        await tournamentQuery.refetch()
        navigate(`/tournament/${id}`)
    }

    async function finishRound() {
        await roundRepository.finishRound(id, roundId!)
            .catch(e => alert("Could not finish round. Please, check if all match results are submitted. " +
                e?.response?.data?.message))
        await tournamentQuery.refetch()
    }

    async function reopenRound() {
        await roundRepository.reopenRound(id, roundId!)
            .catch(e => alert("Could not reopen round. " +
                e?.response?.data?.message))
        await tournamentQuery.refetch();
    }

    async function startNicknameContest() {
        await restApiClient.post(`/nickname-contest/${tournament.id}`);
    }

    let hideParticipateButton = !meParticipantQuery.isSuccess || !!meParticipantQuery.data
    let isTournamentFinished = tournament.status === "FINISHED"

    async function startTournament() {
        await tournamentRepository.startTournament(tournament.id)
            .catch(e => alert("Could not start tournament. " +
                (e?.response?.data?.message || "Unknown error")));
        await tournamentQuery.refetch()
    }

    async function finishTournament() {
        await tournamentRepository.finishTournament(tournament.id)
            .catch(e => alert("Could not finish tournament. " +
                (e?.response?.data?.message || "Unknown error")));
        await tournamentQuery.refetch()
    }

    async function deleteTournament() {
        let expectedConfirmation = (tournament.name.substring(0, 10) || "DELETE");
        let confirmation = window.prompt(`Are you sure?\nTo delete tournament enter \n${expectedConfirmation}`);
        if (confirmation !== expectedConfirmation) {
            alert("You entered wrong id. Tournament will not be deleted.");
            return;
        }
        await tournamentRepository.deleteTournament(tournament?.id)
            .catch(e => alert("Could not delete tournament. " +
                (e?.response?.data?.message || "Unknown error")));
        navigate("/");
    }

    async function participate() {
        let nickname = window.prompt("Please enter your nickname");
        if (!nickname) {
            alert("Nickname is not provided. Registration is cancelled.")
        } else {
            await tournamentRepository.participate(tournament.id, nickname)
                .catch(() => alert("Could not participate in tournament"));
            await meParticipantQuery.refetch()
            await tournamentQuery.refetch()
        }
    }

    async function leaveTournament() {
        if (!window.confirm("You want to be removed from the tournament?")) {
            alert("You stay in the tournament.")
        } else {
            await participantRepository.missMe(tournament.id)
                .catch(() => alert(`Could not withdraw from tournament`));
            await meParticipantQuery.refetch()
            await tournamentQuery.refetch()
        }
    }

    return <>
        <div className={"flex mt-4 p-2 items-top content-center"}>
            <h2 className={"text-lg font-semibold text-left grow"}>
                {tournament.name || loc(`Unnamed Tournament`)}
            </h2>
            <div className={"px-2"}>
                <div>
                    <small className={"font-semibold text-gray-500"}>{tournament.status}</small>
                </div>
                <div>
                    <span className={"font-semibold"}>{tournament.date && dayjs(tournament.date).format("DD.MM.YY")}</span>
                </div>
            </div>
        </div>
        {tournament.status === "ACTIVE" && !meParticipantQuery.data?.isMissing && (
            <MyActiveTournamentPane tournamentId={tournament.id}/>
        )}
        <div className={"p-2"}>
            <NicknameContextPane tournamentId={tournament.id}/>
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

        <Conditional on={isMeModerator}>
            <Conditional on={tournament.status !== "FINISHED"}>
                <AddParticipantTournamentPageSection participants={participants} addParticipant={addParticipant}/>
            </Conditional>
        </Conditional>

        <div className={"flex flex-wrap text-sm justify-start place-items-stretch w-full px-2 my-4"}>
            <Link className={"lg:col-span-1"} to={`/tournament/${id}`} replace={true}>
                <button
                    className={`w-full h-full py-1 px-3 border border-black uppercase ${isMain ? "bg-primary-400 text-white" : "hover:bg-gray-300 text-black"}`}
                    title={loc("Tournament page")}
                >
                    <AiOutlineHome/>
                </button>
            </Link>
            {roundNumbers.map(rid => {
                return <Link
                    to={`/tournament/${id}/round/${rid}`} title={loc(`Open round`) + " " + rid}
                    key={rid}
                    replace={!!roundId} // Write history only if navigating from home page
                >
                    <div
                        className={`w-full py-1 px-3  border border-black
                                    ${rid === roundId ? "bg-primary-400 text-white" : "hover:bg-gray-300"}`}>
                        {rid}
                    </div>
                </Link>
            })}
            <Conditional on={!isTournamentFinished}>
                <Conditional on={isMeModerator}>
                    <button className={`py-1 px-3`}
                            onClick={createRound}
                            title={loc("New round")}
                    ><AiOutlinePlus/>
                    </button>
                </Conditional>
            </Conditional>
            <div className={"grow"}></div>
            <ShareDropdownButton tournament={tournament}/>
        </div>
        <>
            <Conditional on={isMain}>
                <ResultsTable participants={participants} openParticipant={openParticipant}/>
            </Conditional>
            <Conditional on={!!roundId}>
                <div className={"p-2"}>
                    <RoundTab
                        tournamentId={tournament.id}
                        round={tournamentData!.rounds[roundId! - 1]}
                        submitMatchResult={(match, result) => submitMatchResult(match, result!)}
                        submitRoundFinished={() => {finishRound().catch(console.error)}}
                        deleteRound={() => deleteRound()}
                        runPairing={() => runPairingForRound()}
                        reopenRound={() => reopenRound()}
                    />
                </div>
            </Conditional>
        </>
        <Conditional on={isMain}>
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
        </Conditional>
        <Conditional on={isMain && isMeModerator}>
            <ControlButtons
                startNicknameContest={startNicknameContest}
                tournament={tournament}
                startTournament={startTournament}
                finishTournament={finishTournament}
                deleteTournament={deleteTournament}
            />
        </Conditional>
    </>
}

function ControlButtons(props: {
    tournament: TournamentDto,
    startTournament: () => Promise<void>,
    finishTournament: () => Promise<void>,
    deleteTournament: () => Promise<void>,
    startNicknameContest: () => Promise<void>,
}) {
    let loc = useLoc()

    return <div className={"flex p-2 gap-2 items-top content-center"}>
        <div className="flex flex-col gap-2 justify-start grow">
            <button
                onClick={props.startNicknameContest}
                className={"btn-primary uppercase"}
            >
                {"Run nickname contest!"}
            </button>
        </div>

        <div className={"flex gap-2 justify-end"}>
            <Conditional on={props.tournament.status !== "ACTIVE"}>
                <button className={"btn-primary uppercase"}
                        onClick={props.startTournament}
                >{loc("Start")}
                </button>
            </Conditional>
            <Conditional on={props.tournament.status === "ACTIVE"}>
                <button className={"btn-primary uppercase"}
                        onClick={props.finishTournament}
                >{loc("Finish")}
                </button>
            </Conditional>
            <Link to={`/tournament/${props.tournament.id}/edit`}>
                <button className={"btn-light h-full"}>
                    <AiOutlineEdit/>
                </button>
            </Link>
            <button
                className={"btn-danger uppercase"}
                onClick={props.deleteTournament}
                title={loc("Delete")}
            >
                <AiOutlineDelete/>
            </button>
        </div>
    </div>;
}

export default TournamentPage;
