import {MatchDto, MatchResult} from "lib/api/dto/TournamentPageData";
import {Conditional} from "components/Conditional";
import {useLoc} from "strings/loc";
import {Fragment, useRef, useState} from "react";
import {useClickOutsideHandler} from "lib/util/ClickOutside";
import {IoMdArrowDropdown} from "react-icons/io";

function MatchResultSelector(
    {
        canSetResult,
        result,
        setResult,
    }: {
        canSetResult: boolean,
        result: MatchResult | undefined,
        setResult: (selectedResult: MatchResult | null) => void,
    }
) {
    let [selectOpened, setSelectOpened] = useState(false)
    let selectRef = useRef(null)
    useClickOutsideHandler(selectRef, () => setSelectOpened(false));


    function getResultStr(result: MatchResult | undefined): string {
        switch (result) {
            case "WHITE_WIN":
                return "1 - 0"
            case "BLACK_WIN":
                return "0 - 1"
            case "DRAW":
                return "½ - ½"
            case "BUY":
                return "BYE"
            case "MISS":
                return "MISS"
            default:
                return "? - ?"
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
        return <li className={"p-2 cursor-pointer text-lg border-gray-200 border"} onClick={onClick}>
            {result}
        </li>;
    }

    return <>
        <Conditional on={canSetResult}>
            <div ref={selectRef} className={"relative h-full"}>
                <div className={"grid h-full"}>
                    <button className={"flex gap-1 place-content-center place-items-center p-1 font-semibold"}
                            onClick={() => setSelectOpened(!selectOpened)}
                    >
                        <span>{getResultStr(result)}</span>
                        <span><IoMdArrowDropdown/></span>
                    </button>
                </div>
                <Conditional on={selectOpened}>
                    <ul className={"absolute t-[100%] z-50 w-full font-semibold bg-white shadow-lg"}
                        onClick={() => setSelectOpened(false)}
                    >
                        <ResultSelectItem result={"1 - 0"} onClick={() => setResult("WHITE_WIN")}/>
                        <ResultSelectItem result={"0 - 1"} onClick={() => setResult("BLACK_WIN")}/>
                        <ResultSelectItem result={"½ - ½"} onClick={() => setResult("DRAW")}/>
                        <ResultSelectItem result={"? - ?"} onClick={() => setResult(null)}/>
                        <ResultSelectItem result={"BYE"} onClick={() => setResult("BUY")}/>
                        <ResultSelectItem result={"MISS"} onClick={() => setResult("MISS")}/>
                    </ul>
                </Conditional>
            </div>
        </Conditional>
        <Conditional on={!canSetResult}>
            <span className={"font-semibold"}>{getResultStr(result)}</span>
        </Conditional>
    </>;
}

export function MatchRow(
    {
        idx,
        match,
        tableNumber,
        canEditResults,
        setResult,
    }: {

        idx: number,
        match: MatchDto,
        tableNumber: number,
        canEditResults: boolean,
        setResult: (selectedResult: MatchResult | null) => void
    }
) {
    return <Fragment key={idx}>
        <div className={`grid text-xs p-3 
                        ${match.result === "WHITE_WIN" ? "bg-anzac-400" : ""} 
                        ${match.result === "BLACK_WIN" ? "bg-gray-200" : ""}
                        ${match.result === "BUY" ? "bg-anzac-400" : ""} 
                        ${match.result === "DRAW" ? "bg-anzac-200" : ""} 
                        ${match.result === "MISS" ? "bg-gray-200" : ""} 
                        ${!match.result ? "bg-gray-50" : ""} 
                        `}
        >
            <span className={"font-semibold text-ellipsis overflow-hidden line-clamp-2"}>{match.white?.name || "-"}</span>
            {match.white?.userFullName &&
                <span className={"text-ellipsis overflow-hidden"}>{match.white?.userFullName}</span>
            }
        </div>
        <div className={"text-xl text-center grid"}>
            <MatchResultSelector canSetResult={canEditResults} result={match.result} setResult={setResult}/>
            <span className={"text-xs"}>{tableNumber || 0}</span>
        </div>
        <div className={`grid text-xs p-3 
                        ${match.result === "BLACK_WIN" ? "bg-anzac-400" : ""} 
                        ${match.result === "WHITE_WIN" ? "bg-gray-200" : ""}
                        ${match.result === "BUY" ? "bg-anzac-400" : ""} 
                        ${match.result === "DRAW" ? "bg-anzac-200" : ""} 
                        ${match.result === "MISS" ? "bg-gray-200" : ""} 
                        ${!match.result ? "bg-gray-50" : ""} 
                        `}
        >
            <span className={"font-semibold text-ellipsis overflow-hidden line-clamp-2"}>{match.black?.name || "-"}</span>
            {match.black?.userFullName &&
                <span className={"text-ellipsis overflow-hidden"}>{match.black?.userFullName}</span>
            }
        </div>
    </Fragment>;
}

function MatchesTable(
    {
        matches,
        canEditResults,
        submitMatchResult,
    }: {
        matches: MatchDto[],
        canEditResults: boolean,
        submitMatchResult: (match: MatchDto, result: MatchResult | null) => void,
    }
) {
    let loc = useLoc()

    return <div className={"grid grid-cols-[1fr_auto_1fr] p-2 gap-y-2 gap-x-2 text-left"}>
        <div className={"font-semibold uppercase text-left"}>{loc("White")}</div>
        <div className={"font-semibold uppercase text-center"}>{loc("Result")}</div>
        <div className={"font-semibold uppercase text-left"}>{loc("Black")}</div>
        {
            matches.map((match, idx) => {
                return <MatchRow
                    idx={idx}
                    key={idx}
                    match={match}
                    tableNumber={idx + 1}
                    canEditResults={canEditResults}
                    setResult={(selectedResult: MatchResult | null) => {
                        if (canEditResults) {
                            submitMatchResult(match, selectedResult);
                        } else {
                            alert("Can not set results!");
                        }
                    }}
                />
            })
        }
    </div>
}

export default MatchesTable
