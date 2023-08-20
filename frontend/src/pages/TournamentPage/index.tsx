import {Link, useParams} from "react-router-dom";
import React, {useMemo} from "react";
import ResultsTable from "./ResultsTable";
import RoundTable from "./RoundTable";
import {useQuery} from "@tanstack/react-query";
import tournamentPageRepository from "../../lib/pageRepository/TournamentPageRepository";
import {MatchParticipantDto, ParticipantDto, RoundDto, TournamentPageData} from "../../lib/api/dto/TournamentPageData";

function TournamentPage() {
    let {id, roundId: roundIdStr} = useParams();
    let roundId = useMemo(() => roundIdStr ? parseInt(roundIdStr) : null, [roundIdStr]);
    let data = useQuery(
        ["mainPage"],
        () => tournamentPageRepository.getData(id!!)
    );
    let roundData: RoundDto[] = useMemo(() => data?.data?.rounds || [
        {
            matches: [
                {
                    white: {
                        userId: "UUID-UUID-1",
                        name: "Mikhail Boba",
                    },
                    black: {
                        userId: "UUID-UUID-1",
                        name: "Sergey Pavlov"
                    },
                    result: "DRAW"
                },
                {
                    white: {
                        userId: "UUID-UUID-1",
                        name: "Ivan Ivanov"
                    },
                    black: {
                        userId: "UUID-UUID-1",
                        name: "Petr Petrov"
                    },
                    result: "WHITE_WIN"
                },
            ]
        } as RoundDto,
        {
            matches: [
                {
                    black: {
                        userId: "UUID-UUID-1",
                        name: "Ivan Ivanov",
                    },
                    white: {
                        userId: "UUID-UUID-1",
                        name: "Sergey Pavlov"
                    },
                    result: "BLACK_WIN"
                },
                {
                    white: {
                        userId: "UUID-UUID-1",
                        name: "Mikhail Boba"
                    },
                    black: {
                        userId: "UUID-UUID-1",
                        name: "Petr Petrov"
                    },
                    result: "WHITE_WIN"
                },
            ]
        } as RoundDto
    ] as RoundDto[], [data, id])
    let rounds = roundData.map((e, idx) => idx + 1);
    let participants: ParticipantDto[] = useMemo(() => {
        return data?.data?.participants || [
            {
                name: "Mikhail Boba",
                score: 5.5,
                buchholz: 24
            } as ParticipantDto,
            {
                name: "Sergey Pavlov",
                score: 5.0,
                buchholz: 22
            } as ParticipantDto
        ]
    }, [id, data])

    return <>
        <h2>
            Tournament {id}
        </h2>
        <div>
            <Link to={`/tournament/${id}`}>
                <button>Home</button>
            </Link>
            {rounds.map(rid => {
                return <Link key={rid} to={`/tournament/${id}/round/${rid}`}>
                    <button style={{backgroundColor: rid === roundId ? "aqua" : "white"}}>
                        {rid}
                    </button>
                </Link>
            })}
            <button>+</button>
        </div>

        {
            !roundId
                ? <>
                    <h3>Status</h3>
                    <ResultsTable participants={participants}/>
                </>
                : <>
                    <h3>Round</h3>
                    <RoundTable matches={roundData[roundId - 1].matches}/>
                </>
        }
    </>
}

export default TournamentPage;
