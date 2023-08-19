import React from "react";
import {MemberList} from "./MemberList";
import {TournamentsList} from "./TournamentsList";

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

    let tournaments = [
        {
            id: "uuid-uuid-1",
            name: "Tournament 1",
            date: "2023-07-10"
        },
        {
            id: "uuid-uuid-2",
            name: "Tournament 2",
            date: "2023-07-17"
        },
        {
            id: "uuid-uuid-3",
            name: "Tournament 3",
            date: "2023-07-24"
        },
    ]

    return <>
        <MemberList members={members}/>
        <TournamentsList tournaments={tournaments}/>
    </>
}

export default MainPage
