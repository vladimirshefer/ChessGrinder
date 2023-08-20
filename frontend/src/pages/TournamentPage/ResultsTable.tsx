import React from "react";
import {ParticipantDto} from "lib/api/dto/TournamentPageData";

function ResultsTable(
    {
        participants
    }: {
        participants: ParticipantDto[],
    }
) {

    return <>
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
                participants.map(participant => {
                    return <tr key={participant.name}>
                        <td>{participant.name}</td>
                        <td>{participant.score}</td>
                        <td>{participant.buchholz}</td>
                    </tr>
                })
            }
            </tbody>
        </table>
    </>
}

export default ResultsTable
