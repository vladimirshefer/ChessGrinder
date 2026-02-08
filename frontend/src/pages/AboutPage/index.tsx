import {BsLightning, BsStars} from "react-icons/bs";

export default function AboutPage() {
    return <div className={"p-5 text-left grid gap-5"}>
        <h1 className={"text-4xl md:text-7xl uppercase font-bold text-left py-3"}>
            <span>We are</span>
            <br/>
            <span className={"text-primary-400"}>Chess</span>Grinder
        </h1>
        <span className={"md:text-xl text-l"}>
            A Friendly Chess Community across the Europe.
        </span>


        <div className={"p-5"}></div>

        <div className={"flex items-grow text-center"}>
            <div
                className={"flex gap-2 justify-center items-center p-3 text-3xl font-bold grow hover:ring-1 hover:ring-primary-400"}>
                <span className={"text-5xl"} title={"Germany"}>🇩🇪</span>
                <span>Berlin</span>
            </div>
            <div
                className={"flex gap-2 justify-center items-center p-3 text-3xl font-bold grow hover:ring-1 hover:ring-primary-400"}>
                <span className={"text-5xl"} title={"Cyprus"}>🇨🇾</span>
                <span>Limassol</span>
            </div>
            <div
                className={"flex gap-2 justify-center items-center p-3 text-3xl font-bold grow hover:ring-1 hover:ring-primary-400"}>
                <span className={"text-5xl"} title={"Georgia"}>🇬🇪</span>
                <span>Tbilisi</span>
            </div>
        </div>


        <div className={"p-5"}></div>



        <h2 className={"text-2xl font-bold"}>How it works</h2>
        <div className={"grid gap-5 grid-cols-2 md2:grid-cols-3 lg2:grid-cols-3 text-center"}>
            <div
                className={"grid ring-2 ring-primary-200 bg-primary-200 px-3 py-5 gap-2 content-center place-items-center"}>
                <h5 className={"text-xl"}>Rapid tournament</h5>
                <span className={"font-bold text-2xl"}>Every Thursday</span>
                <span>8:00 PM</span>
            </div>
            <div className={"grid ring-2 ring-primary-200 px-3 py-5gap-2 content-center place-items-center"}>
                <h5 className={"text-xl"}>5 games</h5>
                <h5 className={"text-xl"}></h5>
                <h5 className={"text-2xl font-bold"}>⏳ 10+3</h5>
                <span
                    className={"text-xs"}>Time control: 10 minutes on the timer, 3 seconds bonus after each move.</span>
            </div>
            <div className={"grid ring-2 ring-gray-200 px-3 py-5 gap-2 content-center place-items-center"}>
                <h5 className={"text-xl font-bold"}>Swiss pairing</h5>
                <ul>
                    <li className={"text-xs"}>⚖️ Playing with opponents of the same level</li>
                    <li className={"text-xs"}>▶️ No one is knocked out early, everyone keeps playing</li>
                </ul>
            </div>
            <div className={"grid ring-2 ring-gray-200 px-3 py-5 gap-2 content-center place-items-center"}>
                <h5 className={"text-xl font-bold"}>Relaxed rules</h5>
                <ul>
                    <li className={"text-xs"}>🛎️ No touch-move rule — your move is final once you press the clock.</li>
                    <li className={"text-xs"}>🤝 Takebacks by mutual agreement only, as a friendly courtesy.</li>
                </ul>
            </div>
        </div>



        <div className={"p-5"}></div>



        <h2 className={"text-2xl font-bold"}>Any level</h2>
        <div className={"grid grid-cols-1 md:grid-cols-2 gap-3"}>
            <div className={"col-span-1 grid p-3 border border-gray-200 hover:border-primary-200"}>
                <div className={"flex items-center"}>
                    <h3 className={"text-3xl font-semibold"}>Chill League</h3>
                    <div className={"grow"}/>
                    <span className={"text-gray-700 flex items-center px-1"}><BsStars/> Beginner-friendly</span>
                </div>
                <ul>
                    <li>For newcomers and casual players.</li>
                    <li>Relaxed atmosphere. Focus on fun & learning.</li>
                    <li>Coaching tips and friendly pairings.</li>
                </ul>
            </div>
            <div className={"col-span-1 grid gap-2 p-3 border border-gray-200 hover:border-primary-200"}>
                <div className={"flex items-center"}>
                    <h3 className={"text-3xl font-semibold"}>TryHard League</h3>
                    <div className={"grow"}/>
                    <span className={"text-gray-700 flex items-center px-1"}><BsLightning/> Competitive</span>
                </div>
                <ul>
                    <li>For experienced players looking for strong opponents.</li>
                    <li>Bragging rights and a free drink for the winner.</li>
                    <li>Competitive environment.</li>
                </ul>
            </div>
        </div>
    </div>
}
