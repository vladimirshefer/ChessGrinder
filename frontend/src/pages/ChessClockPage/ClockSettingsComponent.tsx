import React, {useEffect, useMemo, useState} from 'react';
import NumberWheel from "pages/ChessClockPage/NumberWheel";

const ClockSettingsComponent = (
    {
        close = () => {
        },
        time,
        setTime,
        bonus,
        setBonus,
    }: {
        close: () => void,
        time: number,
        setTime: (seconds: number) => void,
        bonus: number,
        setBonus: (seconds: number) => void,
    }
) => {
    const [totalTime, setTotalTime] = useState(time);
    const [bonusTime, setBonusTime] = useState(bonus);

    useEffect(() => setTotalTime(time), [time]);
    useEffect(() => setBonusTime(bonus), [bonus]);

    const handleTimeChange = (hours: number, minutes: number, seconds: number) => {
        const totalSeconds = hours * 3600 + minutes * 60 + seconds;
        setTotalTime(totalSeconds);
    };

    const handleQuickTimeSet = (minutes: number, bonus: number) => {
        handleTimeChange(0, minutes, 0)
        setBonusTime(bonus)
    };

    const hours = useMemo(() => Math.floor(totalTime / 3600), [totalTime]);
    const minutes = useMemo(() => Math.floor((totalTime % 3600) / 60), [totalTime]);
    const seconds = useMemo(() => totalTime % 60, [totalTime]);

    return (
        <div className="h-screen flex flex-col items-center justify-center gap-4">
            <div className="flex flex-col gap-4">
                <div className="flex gap-2 items-center justify-center">
                    <button
                        className="bg-primary-400 text-white font-bold py-2 px-4"
                        onClick={() => handleQuickTimeSet(1, 1)}
                    >
                        1 | 1
                    </button>
                    <button
                        className="bg-primary-400 text-white font-bold py-2 px-4"
                        onClick={() => handleQuickTimeSet(3, 2)}
                    >
                        3 | 2
                    </button>
                    <button
                        className="bg-primary-400 text-white font-bold py-2 px-4"
                        onClick={() => handleQuickTimeSet(10, 3)}
                    >
                        10 | 3
                    </button>
                    <button
                        className="bg-primary-400 text-white font-bold py-2 px-4"
                        onClick={() => handleQuickTimeSet(15, 10)}
                    >
                        15 | 10
                    </button>
                </div>
                <div className="flex items-center">
                    <NumberWheel
                        min={0}
                        max={23}
                        value={hours}
                        onChange={(value) => handleTimeChange(value, minutes, seconds)}
                        renderer={(it) => it + "h"}
                    />
                    <NumberWheel
                        min={0}
                        max={59}
                        value={minutes}
                        onChange={(value) => handleTimeChange(hours, value, seconds)}
                        renderer={(it) => it + "m"}
                    />
                    <NumberWheel
                        min={0}
                        max={59}
                        value={seconds}
                        onChange={(value) => handleTimeChange(hours, minutes, value)}
                        renderer={(it) => it + "s"}
                    />
                    <NumberWheel
                        min={0}
                        max={60}
                        value={bonusTime}
                        onChange={(value) => setBonusTime(value)}
                        renderer={(it) => "+" + it.toString() + "s"}
                    />
                </div>
            </div>
            <div className="flex gap-2 items-center justify-center">
                <button
                    className="bg-primary-400 text-white font-bold py-4 px-8 text-xl"
                    onClick={() => {
                        setTime(totalTime);
                        setBonus(bonusTime);
                        close();
                    }}
                >
                    Start Game
                </button>
                <button
                    className="bg-gray-400 text-white font-bold py-4 px-8 text-xl"
                    onClick={close}
                >
                    Cancel
                </button>
            </div>
        </div>
    );
};

export default ClockSettingsComponent;