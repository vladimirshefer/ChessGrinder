import {MatchDto, MatchResult} from "lib/api/dto/TournamentPageData";
import {Fragment} from "react";

function MatchesTable(
    {
        matches,
        roundIsFinished,
        submitMatchResult,
    }: {
        matches: MatchDto[],
        roundIsFinished: boolean,
        submitMatchResult: (match: MatchDto, result: MatchResult | null) => void,
    }
) {
    return <div className={"grid grid-cols-12"}>
        <div className={"col-span-4 font-bold"}>White</div>
        <div className={"col-span-4 font-bold"}>Result</div>
        <div className={"col-span-4 font-bold"}>Black</div>
        {
            matches.map((match, idx) => {
                return <Fragment key={idx}>
                    <div className={"col-span-4"}>{match.white.name}</div>
                    <div className={"col-span-4"}>
                        <select defaultValue={match.result || ""}
                                disabled={roundIsFinished}
                                onChange={(e) => {
                                    submitMatchResult(match, e.target.value as MatchResult)
                                }}>
                            <option value={""}>Unknown</option>
                            <option value={"WHITE_WIN"}>White won</option>
                            <option value={"BLACK_WIN"}>Black won</option>
                            <option value={"DRAW"}>Draw</option>
                        </select>
                    </div>
                    <div className={"col-span-4"}>{match.black.name}</div>
                </Fragment>
            })
        }
    </div>
}

export default MatchesTable
