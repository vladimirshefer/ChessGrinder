import {Link, useParams} from "react-router-dom";
import React from "react";

function TournamentPage() {
    let {id, roundId: roundIdStr} = useParams();
    let roundId = roundIdStr ? parseInt(roundIdStr) : "1";
    let rounds = [1, 2, 3];
    let roundData = [
        {
            white: {
                name: "Mikhail Boba",
            },
            black: {
                name: "Sergey Pavlov"
            }
        }
    ]
    let tournamentData= {
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
            {rounds.map(rid => {
                return <Link to={`/tournament/${id}/round/${rid}`}>
                    <button style={{backgroundColor: rid === roundId ? "aqua" : "white"}}>
                        {rid}
                    </button>
                </Link>
            })}
            <button>+</button>
        </div>
        <table>
            <thead>
            <tr>
                <th>Name</th>
                <th>PTS</th>
                <th>BHZ</th>
            </tr>
            </thead>
            <tbody>
            {
                tournamentData.participants.map(participant => {
                    return <tr>
                        <td>{participant.name}</td>
                        <td>{participant.score}</td>
                        <td>{participant.bhz}</td>
                    </tr>
                })
            }
            </tbody>
        </table>
    </>
}

export default TournamentPage;
