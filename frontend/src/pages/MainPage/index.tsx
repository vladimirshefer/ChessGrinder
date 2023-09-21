import React from "react";
import {MemberList} from "./MemberList";
import {TournamentsList} from "./TournamentsList";
import {useQuery} from "@tanstack/react-query";
import {ListDto, MemberDto, TournamentDto, TournamentListDto} from "lib/api/dto/MainPageData";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import userRepository from "lib/api/repository/UserRepository";

function MainPage() {

    let {
        data: {
            values: members = [] as MemberDto[],
        } = {} as ListDto<MemberDto>,
        refetch: refetchUsers
    } = useQuery({
        queryKey: ["members"],
        queryFn: () => userRepository.getUsers(),
    })

    let {
        data: {
            tournaments = [] as TournamentDto[],
        } = {} as TournamentListDto,
        refetch: refetchTournaments
    } = useQuery({
        queryKey: ["tournaments"],
        queryFn: () => tournamentRepository.getTournaments()
    })

    async function createTournament() {
        await tournamentRepository.postTournament()
        await refetchTournaments()
    }

    async function createMember(memberName: string) {
        await userRepository.postGuest({
            id: memberName,
            username: memberName,
            name: memberName,
            badges: [],
            roles: []
        } as MemberDto)
        await refetchUsers()
    }

    return <>
        <MemberList
            members={members!!}
            createMember={createMember}
        />
        <TournamentsList tournaments={tournaments} createTournament={createTournament}/>
    </>

}

export default MainPage
