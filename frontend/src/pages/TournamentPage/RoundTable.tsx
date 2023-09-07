import {MatchDto, MatchResult} from "lib/api/dto/TournamentPageData";

function RoundTable(
    {
        matches,
        submitMatchResult
    }: {
        matches: MatchDto[],
        submitMatchResult: (match: MatchDto, result: MatchResult | null) => void,
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
                            <select defaultValue={match.result || ""}
                                    onChange={(e) => {
                                        submitMatchResult(match, e.target.value as MatchResult)
                                    }}>
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
