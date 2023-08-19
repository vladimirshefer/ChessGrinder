function RoundTable(
    {
        matches
    }: {
        matches: {
            white: string,
            black: string,
            result: string
        }[]
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
                matches.map(match => {
                    return <tr key={match.white + "_" + match.black}>
                        <td>{match.white}</td>
                        <td>
                            <select>
                                <option selected={match.result === "WHITE_WIN"} value={"WHITE_WIN"}>White won</option>
                                <option selected={match.result === "BLACK_WIN"} value={"BLACK_WIN"}>Black won</option>
                                <option selected={match.result === "DRAW"} value={"DRAW"}>Draw</option>
                            </select>
                        </td>
                        <td>{match.black}</td>
                    </tr>
                })
            }
            </tbody>
        </table>
    </>
}

export default RoundTable
