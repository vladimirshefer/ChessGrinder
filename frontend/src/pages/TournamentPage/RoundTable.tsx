import {MatchDto} from "lib/api/dto/TournamentPageData";

function RoundTable(
    {
        matches
    }: {
        matches: MatchDto[]
    }
) {
    return <>
        <table>
            <thead>
            <tr>
                <th>White</th>
                <th>Result</th>
                <th>Black</th>
            </tr>
            </thead>
            <tbody>
            {
                matches.map((match, idx) => {
                    return <tr key={idx}>
                        <td>{match.white.name}</td>
                        <td>
                            <select defaultValue={match.result || ""}>
                                <option value={""}>Unknown</option>
                                <option value={"WHITE_WIN"}>White won</option>
                                <option value={"BLACK_WIN"}>Black won</option>
                                <option value={"DRAW"}>Draw</option>
                            </select>
                        </td>
                        <td>{match.black.name}</td>
                    </tr>
                })
            }
            </tbody>
        </table>
    </>
}

export default RoundTable
