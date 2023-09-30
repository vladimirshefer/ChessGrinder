import {Link, useNavigate, useParams} from "react-router-dom";
import React, {useMemo} from "react";
import ResultsTable from "pages/TournamentPage/ResultsTable";
import {useQuery} from "@tanstack/react-query";
import tournamentPageRepository from "lib/api/repository/TournamentPageRepository";
import {MatchDto, MatchResult, ParticipantDto, TournamentPageData} from "lib/api/dto/TournamentPageData";
import RoundTab from "pages/TournamentPage/RoundTab";
import ConditionalOnUserRole from "components/ConditionalOnUserRole";
import participantRepository from "lib/api/repository/ParticipantRepository";

function TournamentPage() {
    let {id, roundId: roundIdStr} = useParams();
    let roundId = useMemo(() => roundIdStr ? parseInt(roundIdStr) : null, [roundIdStr]);
    let {data: tournamentData, refetch, isSuccess: isDataReady} = useQuery({
        queryKey: ["tournamentPageData", id],
        queryFn: () => id ? tournamentPageRepository.getData(id) : Promise.reject<TournamentPageData>()
    });
    let navigate = useNavigate()
    let roundNumbers = tournamentData?.rounds?.map((e, idx) => idx + 1) || [];
    let participants: ParticipantDto[] = useMemo(() => tournamentData?.participants || [], [tournamentData])

    async function addParticipant(participant: ParticipantDto) {
        await participantRepository.postParticipant(id!!, participant)
        await refetch()
    }

    async function openParticipant(participant: ParticipantDto) {
        navigate(`/tournament/${id}/participant/${participant.userId}`)
    }

    async function createRound() {
        await tournamentPageRepository.postRound(id!!)
        await refetch()
        navigate(`/tournament/${id}` + (tournamentData && tournamentData.rounds ? `/round/${tournamentData.rounds.length + 1}` : ""))
    }

    async function drawRound() {
        await tournamentPageRepository.drawRound(id!!, roundId!!)
        await refetch()
    }

    async function submitMatchResult(match: MatchDto, result: MatchResult) {
        await tournamentPageRepository.postMatchResult(id!!, roundId!!, match.id, result)
        await refetch()
    }

    if (!isDataReady) return <>Loading</>

    async function deleteRound() {
        await tournamentPageRepository.deleteRound(id!!, roundId!!)
        navigate(`/tournament/${id}`)
    }

    async function finishRound() {
        await tournamentPageRepository.finishRound(id!!, roundId!!)
        await refetch()
    }

    async function reopenRound() {
        await tournamentPageRepository.reopenRound(id!!, roundId!!)
        await refetch()
    }

    return <>
        <h2 className={"text-lg font-bold mt-4"}>
            Tournament {id}
        </h2>
        <div className={"grid grid-cols-12 w-full px-2 my-4"}>
            <Link className={"col-span-3 lg:col-span-1"} to={`/tournament/${id}`}>
                <button
                    className={`w-full p-2 rounded ${!roundId ? "bg-yellow-300" : "bg-gray-100 hover:bg-yellow-100"}`}
                >Home
                </button>
            </Link>
            {roundNumbers.map(rid => {
                return <Link key={rid} to={`/tournament/${id}/round/${rid}`}>
                    <button className={`w-full rounded p-2 ${rid === roundId ? "bg-blue-300" : "hover:bg-blue-100"}`}>
                        {rid}
                    </button>
                </Link>
            })}
            <ConditionalOnUserRole role={"ROLE_ADMIN"}>
                <button className={`w-full rounded p-2 bg-gray-100 col-span-2 lg:col-span-1`}
                        onClick={createRound}
                >+
                </button>
            </ConditionalOnUserRole>
        </div>

        {
            !roundId
                ? (
                    <>
                        <h3>Status</h3>
                        <ResultsTable participants={participants}
                                      addParticipant={(it) => {
                                          addParticipant(it)
                                      }}
                                      openParticipant={(it) => {
                                          openParticipant(it)
                                      }}
                        />
                    </>
                )
                : (
                    <RoundTab
                        round={tournamentData?.rounds[roundId - 1]!!}
                        submitMatchResult={(match, result) => {
                            submitMatchResult(match, result!!);
                        }}
                        submitRoundFinished={() => {
                            finishRound()
                        }}
                        deleteRound={() => deleteRound()}
                        drawRound={() => drawRound()}
                        reopenRound={() => reopenRound()}
                    />
                )
        }
    </>
}

export default TournamentPage;
