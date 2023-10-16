import {MatchDto, MatchResult} from "lib/api/dto/TournamentPageData";
import {useAuthData} from "lib/auth/AuthService";
import {UserRoles} from "lib/api/dto/MainPageData";
import {Conditional} from "components/Conditional";
import {useLoc} from "strings/loc";

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
    let loc = useLoc()
    let authData = useAuthData();
    let canEditResults = !roundIsFinished && (authData?.roles?.includes(UserRoles.ADMIN) || false);

    return <div className={"grid grid-cols-12 p-2"}>
        <div className={"col-span-12 grid grid-cols-12 border-b border-black justify-items-start"}>
            <div className={"col-span-4 font-semibold uppercase"}>{loc("White")}</div>
            <div className={"col-span-4 font-semibold uppercase justify-self-center"}>{loc("Result")}</div>
            <div className={"col-span-4 font-semibold uppercase"}>{loc("Black")}</div>
        </div>
        {
            matches.map((match, idx) => {
                return <div className={"col-span-12 grid grid-cols-12 border-b border-gray text-left"} key={idx}>
                    <div className={"col-span-4 text-xs p-3"}>
                        <span>{match.white?.name || "-"}</span>
                    </div>
                    <div className={"col-span-4 text-xl overflow-hidden"}>
                        <Conditional on={canEditResults}>
                            <select defaultValue={match.result || "—"}
                                    onChange={(e) => {
                                        submitMatchResult(match, e.target.value as MatchResult)
                                    }}>
                                <option value={""}>Unknown</option>
                                <option value={"WHITE_WIN"}>White won</option>
                                <option value={"BLACK_WIN"}>Black won</option>
                                <option value={"DRAW"}>Draw</option>
                                <option value={"BUY"}>Buy</option>
                            </select>
                        </Conditional>
                        <Conditional on={!canEditResults}>
                            <span>{match.result || "—"}</span>
                        </Conditional>
                    </div>
                    <div className={"col-span-4 text-xs p-3"}>
                        <span>{match.black?.name || "—"}</span>
                    </div>
                </div>
            })
        }
    </div>
}

export default MatchesTable
