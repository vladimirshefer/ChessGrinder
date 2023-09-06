import {Link, useNavigate, useParams} from "react-router-dom";
import React, {useMemo} from "react";
import ResultsTable from "./ResultsTable";
import RoundTable from "./RoundTable";
import {useQuery} from "@tanstack/react-query";
import tournamentPageRepository from "lib/pageRepository/TournamentPageRepository";
import {ParticipantDto, RoundDto} from "lib/api/dto/TournamentPageData";

function TournamentPage() {
    let {id, roundId: roundIdStr} = useParams();
    let roundId = useMemo(() => roundIdStr ? parseInt(roundIdStr) : null, [roundIdStr]);
    let {data, refetch} = useQuery({
        queryKey: ["tournamentPage", id],
        queryFn: () => tournamentPageRepository.getData(id!!)
    });
    let navigate = useNavigate()

    let roundData: RoundDto[] = useMemo(() => data?.rounds || [], [data])
    let rounds = roundData.map((e, idx) => idx + 1);
    let participants: ParticipantDto[] = useMemo(() => data?.participants || [], [data])

    async function addParticipant(name: string) {
        await tournamentPageRepository.postParticipant(id!!, name)
        await refetch()
    }

    async function createRound() {
        await tournamentPageRepository.postRound(id!!)
        await refetch()
        navigate(`/tournament/${id}` + (data && data.rounds ? `/round/${data.rounds.length + 1}` : ""))
    }


    return <>
        <h2>
            Tournament {id}
        </h2>
        <div className={"grid grid-cols-12 w-full px-2"}>
            <Link to={`/tournament/${id}`}>
                <button
                    className={`p-2 rounded ${!roundId ? "bg-yellow-300" : "bg-gray-100 hover:bg-yellow-100"}`}
                >Home
                </button>
            </Link>
            {rounds.map(rid => {
                return <Link key={rid} to={`/tournament/${id}/round/${rid}`}>
                    <button className={`w-full rounded  p-2 ${rid === roundId ? "bg-blue-300" : "hover:bg-blue-100"}`}>
                        {rid}
                    </button>
                </Link>
            })}
            <button className={`w-full rounded p-2 bg-gray-100`}
                    onClick={createRound}
            >+</button>
        </div>

        {
            !roundId
                ? <>
                    <h3>Status</h3>
                    <ResultsTable participants={participants} addParticipant={(it) => {
                        addParticipant(it)
                    }}/>
                </>
                : <>
                    <h3>Round</h3>
                    <RoundTable matches={roundData[roundId - 1].matches}/>
                </>
        }
    </>
}

export default TournamentPage;
