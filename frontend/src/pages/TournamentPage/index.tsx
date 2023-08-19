import {Link, useParams} from "react-router-dom";
import React, {useMemo} from "react";
import ResultsTable from "./ResultsTable";
import RoundTable from "./RoundTable";

function TournamentPage() {
    let {id, roundId: roundIdStr} = useParams();
    let roundId = useMemo(() => roundIdStr ? parseInt(roundIdStr) : null, [roundIdStr]);
    let roundData = [
        [
            {
                white: "Mikhail Boba",
                black: "Sergey Pavlov",
                result: "DRAW"
            },
            {
                white: "Ivan Ivanov",
                black: "Petr Petrov",
                result: "WHITE_WIN"
            },
        ],
        [
            {
                black: "Ivan Ivanov",
                white: "Sergey Pavlov",
                result: "BLACK_WIN"
            },
            {
                white: "Mikhail Boba",
                black: "Petr Petrov",
                result: "WHITE_WIN"
            },
        ]
    ]
    let rounds = roundData.map((e, idx) => idx + 1);
    let tournamentData = {
        participants: [
            {
                name: "Mikhail Boba",
                score: 5.5,
                bhz: 24
            },
            {
                name: "Sergey Pavlov",
                score: 5.0,
                bhz: 22
            }
        ]
    }

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
                    <ResultsTable participants={tournamentData.participants}/>
                </>
                : <>
                    <h3>Round</h3>
                    <RoundTable matches={roundData[roundId - 1]}/>
                </>
        }
    </>
}

export default TournamentPage;
