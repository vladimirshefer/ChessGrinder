import React, {useState} from "react";
import {BsChevronLeft, BsChevronRight, BsLightning, BsStars} from "react-icons/bs";

const cardClass = "grid gap-3 content-center place-items-center text-center px-3 py-5 min-h-72 w-full";

function TitleCard() {
    return <div className={cardClass}>
        <h1 className={"text-3xl md:text-5xl uppercase font-bold"}>
            <span className={"text-primary-400"}>Chess</span>Grinder
        </h1>
        <span className={"md:text-xl"}>A Friendly Chess Community across the Europe.</span>
    </div>
}

function CitiesCard() {
    return <div className={cardClass}>
        <div className={"flex flex-wrap justify-center w-full"}>
            <div className={"flex gap-2 justify-center items-center p-3 font-bold"}>
                <span className={"text-3xl md:text-5xl"} title={"Germany"}>🇩🇪</span>
                <span className={"md:text-3xl"}>Berlin</span>
            </div>
            <div className={"flex gap-2 justify-center items-center p-3 font-bold"}>
                <span className={"text-3xl md:text-5xl"} title={"Cyprus"}>🇨🇾</span>
                <span className={"md:text-3xl"}>Limassol</span>
            </div>
            <div className={"flex gap-2 justify-center items-center p-3 font-bold"}>
                <span className={"text-3xl md:text-5xl"} title={"Georgia"}>🇬🇪</span>
                <span className={"md:text-3xl"}>Tbilisi</span>
            </div>
        </div>
    </div>
}

function GamesCard() {
    return <div className={cardClass}>
        <h5 className={"text-xl"}>Rapid tournament · 5 games</h5>
        <span className={"font-bold text-2xl"}>Every Thursday, 8:00 PM</span>
        <h5 className={"text-3xl font-bold"}>⏳ 10+3</h5>
        <span className={"text-sm"}>Time control: 10 minutes on the timer, 3 seconds bonus after each move.</span>
    </div>
}

function SwissCard() {
    return <div className={cardClass}>
        <h5 className={"text-2xl font-bold"}>Swiss pairing</h5>
        <ul className={"grid gap-2"}>
            <li>⚖️ Playing with opponents of the same level</li>
            <li>▶️ No one is knocked out early, everyone keeps playing</li>
        </ul>
    </div>
}

function RelaxedRulesCard() {
    return <div className={cardClass}>
        <h5 className={"text-2xl font-bold"}>Relaxed rules</h5>
        <ul className={"grid gap-2"}>
            <li>🛎️ No touch-move rule — your move is final once you press the clock.</li>
            <li>🤝 Takebacks by mutual agreement only, as a friendly courtesy.</li>
        </ul>
    </div>
}

function AnyLevelCard() {
    return <div className={cardClass}>
        <h5 className={"text-2xl font-bold"}>Any level</h5>
        <ul className={"grid gap-2"}>
            <li>
                <span className={"font-semibold inline-flex items-center gap-1"}><BsStars/> Chill League:</span>
                {" "}newcomers and casual players, fun and learning.
            </li>
            <li>
                <span className={"font-semibold inline-flex items-center gap-1"}><BsLightning/> TryHard League:</span>
                {" "}strong opponents, bragging rights and a free drink for the winner.
            </li>
        </ul>
    </div>
}

const cards = [TitleCard, CitiesCard, GamesCard, SwissCard, RelaxedRulesCard, AnyLevelCard];

/**
 * Carousel with the short intro about the club. Always shows exactly one card.
 */
export default function AboutPane() {
    const [index, setIndex] = useState(0);
    const [touchStartX, setTouchStartX] = useState<number | null>(null);

    const count = cards.length;
    const go = (delta: number) => setIndex(i => (i + delta + count) % count);
    const Card = cards[index];

    function onTouchEnd(endX: number) {
        if (touchStartX === null) return;
        const dx = endX - touchStartX;
        if (Math.abs(dx) > 40) go(dx < 0 ? 1 : -1);
        setTouchStartX(null);
    }

    return <div className={"p-3 grid gap-2"}>
        <div className={"flex items-center gap-1"}
             onTouchStart={e => setTouchStartX(e.touches[0].clientX)}
             onTouchEnd={e => onTouchEnd(e.changedTouches[0].clientX)}>
            <button className={"p-2 shrink-0"} aria-label={"Previous"} onClick={() => go(-1)}>
                <BsChevronLeft/>
            </button>
            <Card/>
            <button className={"p-2 shrink-0"} aria-label={"Next"} onClick={() => go(1)}>
                <BsChevronRight/>
            </button>
        </div>
        <div className={"flex justify-center gap-2"}>
            {cards.map((_, i) =>
                <button key={i}
                        aria-label={`Card ${i + 1}`}
                        className={`w-2 h-2 rounded-full ${i === index ? "bg-primary-400" : "bg-gray-300"}`}
                        onClick={() => setIndex(i)}/>
            )}
        </div>
    </div>
}
