import React, {useEffect, useMemo, useState} from 'react';
import ClockComponent from "./ClockComponent";
import ClockSettingsComponent from "./ClockSettingsComponent";
import {Conditional} from "../../components/Conditional";
import {useSearchParams} from "react-router-dom";


function ChessClockPage() {
    let [isSettingsOpen, setSettingsOpen] = useState(false);

    let [searchParams] = useSearchParams()
    let explicitTime = searchParams.has("time") ? parseInt(searchParams.get("time")!!) : undefined;
    let explicitBonus = searchParams.has("bonus") ? parseInt(searchParams.get("bonus")!!) : undefined;

    let lastTime1 =
        localStorage.getItem("chessclock.time_left_1")
            ? parseInt(localStorage.getItem("chessclock.time_left_1")!!)
            : undefined;
    let lastTime2 =
        localStorage.getItem("chessclock.time_left_2")
            ? parseInt(localStorage.getItem("chessclock.time_left_2")!!)
            : undefined;
    let lastBonus =
        localStorage.getItem("chessclock.last_bonus")
            ? parseInt(localStorage.getItem("chessclock.last_bonus")!!)
            : undefined;

    let [time1, setTime1] = useState(lastTime1 || explicitTime || DEFAULT_TIME_LIMIT);
    let [time2, setTime2] = useState(lastTime2 || explicitTime || DEFAULT_TIME_LIMIT);
    let [bonusTime, setBonusTime] = useState(lastBonus || explicitBonus || DEFAULT_BONUS);

    useEffect(() => {
        if (
            (!!lastTime1 && lastTime1 !== (explicitTime || DEFAULT_TIME_LIMIT)) ||
            (!!lastTime2 && lastTime2 !== (explicitTime || DEFAULT_TIME_LIMIT))
        ) {
            if (!window.confirm("You have unfinished game. OK - Resume, Cancel - Reset game?")) {
                resetTime();
            }
        }
    }, [])

    function resetTime() {
        localStorage.removeItem("chessclock.time_left_1");
        localStorage.removeItem("chessclock.time_left_2");
        localStorage.removeItem("chessclock.last_bonus");
        setTime1(explicitTime || DEFAULT_TIME_LIMIT);
        setTime2(explicitTime || DEFAULT_TIME_LIMIT);
        setBonusTime(explicitBonus || DEFAULT_BONUS);
    }

    return <>
        <Conditional on={isSettingsOpen}>
            <ClockSettingsComponent
                time={explicitTime || DEFAULT_TIME_LIMIT}
                bonus={explicitBonus || DEFAULT_BONUS}
                setTime={(seconds) => {
                    setTime1(seconds);
                    setTime2(seconds);
                }}
                setBonus={setBonusTime}
                close={() => setSettingsOpen(false)}
            />
        </Conditional>
        <Conditional on={!isSettingsOpen}>
            <ClockComponent
                time1={time1}
                time2={time2}
                bonusTime={bonusTime}
                openSettings={() => setSettingsOpen(true)}
                resetTime={() => resetTime()}
            />
        </Conditional>
    </>
}

const DEFAULT_TIME_LIMIT = 600;
const DEFAULT_BONUS = 3;

export default ChessClockPage;