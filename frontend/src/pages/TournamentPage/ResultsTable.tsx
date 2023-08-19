import React from "react";

function ResultsTable(
    {
        participants
    }: {
        participants: { name: string, score: number, bhz: number }[],
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
                        <td>{participant.bhz}</td>
                    </tr>
                })
            }
            </tbody>
        </table>
    </>
}

export default ResultsTable
