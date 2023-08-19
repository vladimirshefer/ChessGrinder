import React from "react";
import {MemberList} from "./MemberList";

function MainPage() {
    let members = [
        {
            name: "Alexander Boldyrev",
            badges: [
                {
                    imageUrl: "💥",
                    description: "Огонь-огонечек",
                },
                {
                    imageUrl: "🎃",
                    description: "За участие в хэллоуин-вечеринке 2019",
                },
            ]
        },
        {
            name: "Vladimir Shefer",
            badges: [
                {
                    imageUrl: "🍒",
                    description: "Ягодный никнейм",
                },
                {
                    imageUrl: "🎯",
                    description: "За партию с самой высокой точностью на неделе.",
                },
            ]
        },
    ]


    return <>
        Hello, World!

        <MemberList members={members}/>

        <div>
            <h2>Tournaments</h2>
            <ul>
                <li><a href={"/tournament/Tournament 1"}>Tournament 1</a></li>
                <li>Tournament 2</li>
                <li>Tournament 3</li>
            </ul>
        </div>

    </>
}

export default MainPage
