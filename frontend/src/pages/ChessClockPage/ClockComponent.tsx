import React, {useEffect, useState} from 'react';
import {FaPause} from "react-icons/fa6";
import {LuExpand, LuHome, LuPlay, LuSettings, LuTimerReset} from "react-icons/lu";
import {MdFullscreenExit} from "react-icons/md";
import useCaffeinate from "pages/ChessClockPage/useCaffeinate";
import {Link} from "react-router-dom";
import useFullScreen from "pages/ChessClockPage/useFullScreen";

const ClockComponent = (
    {
        openSettings,
        time1,
        time2,
        bonusTime,
        resetTime,
    }
    : {
        openSettings?: () => void,
        time1: number,
        time2: number,
        bonusTime: number,
        resetTime: () => void,
    }
) => {
    const [timeLeft1, setTimeLeft1] = useState(time1);
    const [timeLeft2, setTimeLeft2] = useState(time2);
    const [isFirstActive, setIsFirstActive] = useState(true);
    const [isPaused, setIsPaused] = useState(true);
    const [isFullscreen, setIsFullscreen] = useFullScreen();

    useCaffeinate(!isPaused);

    useEffect(() => {
        setTimeLeft1(time1);
        setTimeLeft2(time2);
    }, [time1, time2]);

    function resetClock() {
        resetTime();
        setTimeLeft1(time1)
        setTimeLeft2(time2)
        setIsPaused(true)
        setIsFirstActive(true)
    }

    useEffect(() => localStorage.setItem("chessclock.time_left_1", timeLeft1.toString()), [timeLeft1]);
    useEffect(() => localStorage.setItem("chessclock.time_left_2", timeLeft2.toString()), [timeLeft2]);
    useEffect(() => localStorage.setItem("chessclock.last_bonus", bonusTime.toString()), [bonusTime]);

    useEffect(() => {
        let interval: NodeJS.Timeout;

        if (!isPaused) {
            interval = setInterval(() => {
                if (isFirstActive) {
                    setTimeLeft1(time => time > 0 ? time - 1 : 0);
                } else {
                    setTimeLeft2(time => time > 0 ? time - 1 : 0);
                }
            }, 1000);
        }

        return () => {
            clearInterval(interval);
        };
    }, [isPaused, isFirstActive]);

    const formatTime = (seconds: number) => {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
    };

    const handleClick = (isFirstTimer: boolean) => {
        // Do not allow resume the game if timeout
        if (timeLeft1 === 0 || timeLeft2 === 0) {
            return;
        }

        if (isPaused) {
            setIsPaused(false);
            setIsFirstActive(!isFirstTimer);
        } else if (isFirstTimer === isFirstActive) {
            setIsFirstActive(!isFirstTimer);
            if (isFirstTimer) {
                setTimeLeft1(time => time + bonusTime);
            } else {
                setTimeLeft2(time => time + bonusTime);
            }
        }
    };

    // Auto-pause when either timer reaches 0
    useEffect(() => {
        if (timeLeft1 === 0 || timeLeft2 === 0) {
            setIsPaused(true);
        }
    }, [timeLeft1, timeLeft2]);

    return (
        <div className="flex flex-col h-screen">
            <button
                className={`
                    text-9xl font-bold flex items-center justify-center transition-all duration-300 rotate-180
                    ${timeLeft2 === 0 ? 'bg-red-500' : !isFirstActive && !isPaused ? 'bg-primary-400 text-white' : 'bg-white text-primary-600'} 
                    ${!isFirstActive && !isPaused ? 'h-2/3' : isPaused ? 'h-1/2' : 'h-1/3'}
                `}
                onClick={() => handleClick(false)}
            >
                {formatTime(timeLeft2)}
            </button>
            <button
                className={`
                    text-3xl font-bold flex items-center justify-center transition-all duration-300 p-1 text-primary-600
                    ${isPaused ? "bg-white " : "bg-white transition-colors duration-300"}
                `}
            >
                {!isPaused ? (
                    <FaPause onClick={() => setIsPaused(true)}/>
                ) : (<div className={"flex gap-2"}>
                        {timeLeft1 > 0 && timeLeft2 > 0 && (
                            <LuPlay onClick={() => setIsPaused(false)}/>
                        )}
                        {!isFullscreen ? (
                            <LuExpand onClick={() => setIsFullscreen(true)}/>
                        ) : (
                            <MdFullscreenExit onClick={() => setIsFullscreen(false)}/>
                        )}
                        <LuSettings onClick={openSettings}/>
                        <LuTimerReset onClick={() => {
                            if (window.confirm("Reset the clock?")) {
                                resetClock();
                            }
                        }}/>
                        <Link to={"/"} onClick={() => setIsFullscreen(false)}>
                            <LuHome/>
                        </Link>
                    </div>
                )}
            </button>
            <button
                className={`
                    text-9xl font-bold flex items-center justify-center transition-all duration-300
                    ${timeLeft1 === 0 ? 'bg-red-500' : isFirstActive && !isPaused ? 'bg-primary-400 text-white' : 'bg-white text-primary-600'} 
                    ${isFirstActive && !isPaused ? 'h-2/3' : isPaused ? 'h-1/2' : 'h-1/3'}
                `}
                onClick={() => handleClick(true)}
            >
                {formatTime(timeLeft1)}
            </button>
        </div>
    );
};

export default ClockComponent;