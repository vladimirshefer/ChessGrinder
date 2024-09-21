import {BsLightning, BsStars} from "react-icons/bs";

export default function AboutPage() {
    return <div className={"p-5 text-left grid gap-5"}>
        <h1 className={"text-4xl md:text-7xl uppercase font-bold text-left py-3"}>
            <span>We are</span>
            <br/>
            <span className={"text-primary-400"}>Chess</span>Grinder
        </h1>
        <span>
            A friendly chess community across the Europe.
            Chill or TryHard — your choice.
            Weekly Thursday meetups in Tbilisi, Berlin, and Limassol (Belgrade coming soon).
        </span>
        <hr className={"my-5"}/>
        <h2 className={"text-2xl font-bold"}>How it works</h2>
        <div className={"grid grid-cols-1 md:grid-cols-2 gap-3"}>
            <div className={"col-span-1 grid p-3 border border-gray-200 hover:border-primary-200"}>
                <div className={"flex items-center"}>
                    <h3 className={"text-xl font-semibold"}>Chill League</h3>
                    <div className={"grow"}/>
                    <span className={"text-gray-700 flex items-center"}><BsStars/> Beginner-friendly</span>
                </div>
                <ul>
                    <li>For newcomers and casual players.</li>
                    <li>Relaxed atmosphere. Focus on fun & learning.</li>
                    <li>Coaching tips and friendly pairings.</li>
                </ul>
            </div>
            <div className={"col-span-1 grid p-3 border border-gray-200 hover:border-primary-200"}>
                <div className={"flex items-center"}>
                    <h3 className={"text-xl font-semibold"}>TryHard League</h3>
                    <div className={"grow"}/>
                    <span className={"text-gray-700 flex items-center"}><BsLightning/> Competitive</span>
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
