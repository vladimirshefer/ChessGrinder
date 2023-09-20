import React from "react";
import {MemberList} from "./MemberList";
import {TournamentsList} from "./TournamentsList";
import {useQuery} from "@tanstack/react-query";
import {MainPageData, MemberDto, TournamentDto} from "lib/api/dto/MainPageData";
import mainPageRepository from "lib/api/repository/MainPageRepository";

let mockMembers: MemberDto[] = [
    {
        id: "ab1234567",
        name: "Alexander Boldyrev",
        badges: [
            {
                title: "",
                imageUrl: "💥",
                description: "Огонь-огонечек",
            },
            {
                title: "",
                imageUrl: "🎃",
                description: "За участие в хэллоуин-вечеринке 2019",
            },
        ],
        username: "ab1234567",
        roles: ["ADMIN"]
    } as MemberDto,
    {
        id: "vs234823476",
        name: "Vladimir Shefer",
        badges: [
            {
                title: "",
                imageUrl: "🍒",
                description: "Ягодный никнейм",
            },
            {
                title: "",
                imageUrl: "🎯",
                description: "За партию с самой высокой точностью на неделе.",
            },
        ]
    } as MemberDto,
];

function MainPage() {

    let {
        data: {
            members = mockMembers,
            tournaments = [] as TournamentDto[]
        } = {} as MainPageData,
        refetch: refetchData,
    } = useQuery({
            queryKey: ["mainPage"],
            queryFn: () => mainPageRepository.getData(),
        }
    );

    async function createTournament() {
        await mainPageRepository.postTournament()
        await refetchData()
    }

    async function createMember(memberName: string) {
        await mainPageRepository.createMember({
            id: memberName,
            username: memberName,
            name: memberName,
            badges: []
        })
        await refetchData()
    }

    return <>
        <MemberList
            members={members}
            createMember={createMember}
        />
        <TournamentsList tournaments={tournaments} createTournament={createTournament}/>
    </>

}

export default MainPage
