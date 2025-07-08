import React, {useEffect, useRef, useState} from "react";

function NumberWheel(
    {
        min = 0,
        max = 100,
        value,
        onChange,
        renderer = (it) => it.toString(),
    }: {
        min?: number;
        max?: number;
        value: number;
        onChange?: (value: number) => void,
        renderer?: (value: number) => React.ReactNode,
    }
) {
    const itemHeight = 40;
    const visibleCount = 5;
    const scrollRef = useRef<HTMLDivElement>(null);
    const [internalValue, setInternalValue] = useState<number>(value);

    const numbers = Array.from({length: max - min + 1}, (_, i) => min + i);

    useEffect(() => {
        setInternalValue(value);
    }, [value]);

    useEffect(() => {
        const index = internalValue - min;
        if (scrollRef.current) {
            scrollRef.current.scrollTop = index * itemHeight;
        }
    }, [internalValue, min]);

    const handleScroll = () => {
        if (!scrollRef.current) return;
        const scrollTop = scrollRef.current.scrollTop;
        const index = Math.round(scrollTop / itemHeight);
        const newValue = numbers[index];
        if (newValue !== internalValue) {
            setInternalValue(newValue);
            onChange?.(newValue);
        }
    };

    const spacerHeight = ((visibleCount - 1) / 2) * itemHeight;

    return (
        <div className="relative w-24 h-52 overflow-hidden mx-auto">
            <div
                className="h-full overflow-y-scroll scroll-snap-y-mandatory snap-y [&::-webkit-scrollbar]:hidden [-ms-overflow-style:none] [scrollbar-width:none]"
                ref={scrollRef}
                onScroll={handleScroll}
                style={{WebkitOverflowScrolling: 'touch'}}
            >
                <div style={{height: spacerHeight}}/>
                {numbers.map((num) => (
                    <div
                        key={num}
                        className="h-10 leading-10 text-center text-lg snap-center"
                    >
                        {renderer(num)}
                    </div>
                ))}
                <div style={{height: spacerHeight}}/>
            </div>
            <div
                className="absolute top-1/2 left-0 w-full h-10 -translate-y-1/2 border-y border-gray-400 bg-white/60 pointer-events-none z-10"/>
        </div>
    );
}

export default NumberWheel;