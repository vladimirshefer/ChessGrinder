import {MatchDto, MatchResult} from "lib/api/dto/TournamentPageData";
import {useAuthData} from "lib/auth/AuthService";
import {UserRoles} from "lib/api/dto/MainPageData";
import {Conditional} from "components/Conditional";
import {useLoc} from "strings/loc";
import {useState} from "react";
import {HiSelector} from "react-icons/hi";

function MatchResultSelector(
    canSetResult: boolean,
    result: MatchResult | undefined,
    setResult: (selectedResult: MatchResult | null) => void,
) {
    let [selectOpened, setSelectOpened] = useState(false)

    function getResultStr(result: MatchResult | undefined): string {
        switch (result) {
            case "WHITE_WIN":
                return "1 - 0"
            case "BLACK_WIN":
                return "0 - 1"
            case "DRAW":
                return "½ - ½"
            case "BUY":
                return "BUY"
            default:
                return "?"
        }
    }

    function ResultSelectItem(
        {
            result,
            onClick,
        }: {
            result: string,
            onClick: () => void,
        }
    ) {
        return <li className={"p-2 border border-gray-300 cursor-pointer"} onClick={onClick}>
            {result}
        </li>;
    }

    return <>
        <Conditional on={canSetResult}>
            <div className={"relative h-full"}>
                <div className={"grid h-full"}>
                    <button className={"flex gap-1 place-content-center place-items-center p-1 font-semibold"}
                            onClick={() => setSelectOpened(!selectOpened)}
                    >
                        <span>{getResultStr(result)}</span>
                        <span><HiSelector/></span>
                    </button>
                </div>
                <Conditional on={selectOpened}>
                    <ul className={"absolute t-[100%] bg-white z-50 w-full font-semibold"}
                        onClick={() => setSelectOpened(false)}
                    >
                        <ResultSelectItem result={"1 - 0"} onClick={() => setResult("WHITE_WIN")}/>
                        <ResultSelectItem result={"0 - 1"} onClick={() => setResult("BLACK_WIN")}/>
                        <ResultSelectItem result={"½ - ½"} onClick={() => setResult("DRAW")}/>
                        <ResultSelectItem result={"?"} onClick={() => setResult(null)}/>
                        <ResultSelectItem result={"BUY"} onClick={() => setResult("BUY")}/>
                    </ul>
                </Conditional>
            </div>
        </Conditional>
        <Conditional on={!canSetResult}>
            <span className={"font-semibold"}>{getResultStr(result)}</span>
        </Conditional>
    </>;
}

function MatchRow(
    idx: number,
    match: MatchDto,
    canEditResults: boolean,
    setResult: (selectedResult: MatchResult | null) => void
) {

    return <div className={"col-span-12 grid grid-cols-12 border-b border-gray text-left"} key={idx}>
        <div className={"col-span-4 text-xs p-3"}>
            <span>{match.white?.name || "-"}</span>
        </div>
        <div className={"col-span-4 text-xl text-center"}>
            {MatchResultSelector(canEditResults, match.result, setResult)}
        </div>
        <div className={"col-span-4 text-xs p-3"}>
            <span>{match.black?.name || "—"}</span>
        </div>
    </div>;
}

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
                return MatchRow(idx, match, canEditResults,
                    (selectedResult: MatchResult | null) => {
                        if (canEditResults) {
                            submitMatchResult(match, selectedResult);
                        } else {
                            alert("Can not set results!");
                        }
                    }
                )
            })
        }
    </div>
}

export default MatchesTable
