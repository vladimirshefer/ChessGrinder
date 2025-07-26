import React from "react";
import {MemberList} from "pages/MainPage/MemberList";
import {TournamentsList} from "./TournamentsList";
import {EventsList} from "./EventsList";
import {SchedulesList} from "./SchedulesList";
import {useQuery} from "@tanstack/react-query";
import {ListDto, UserDto, TournamentDto, TournamentListDto} from "lib/api/dto/MainPageData";
import {TournamentEventDto, TournamentEventListDto, TournamentEventScheduleDto, TournamentEventScheduleListDto} from "lib/api/dto/TournamentEventData";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import tournamentEventRepository from "lib/api/repository/TournamentEventRepository";
import tournamentEventScheduleRepository from "lib/api/repository/TournamentEventScheduleRepository";
import userRepository from "lib/api/repository/UserRepository";
import {useLoc} from "strings/loc";
import {Link, useNavigate} from "react-router-dom";
import MyActiveTournamentPane from "pages/MainPage/MyActiveTournamentPane";

function MainPage() {
    let loc = useLoc()
    let navigate = useNavigate()

    let {
        data: {
            values: users = [] as UserDto[],
        } = {} as ListDto<UserDto>
    } = useQuery({
        queryKey: ["members10"],
        queryFn: () => userRepository.getUsers(10),
    })

    let {
        data: {
            tournaments = [] as TournamentDto[],
        } = {} as TournamentListDto,
    } = useQuery({
        queryKey: ["tournaments"],
        queryFn: () => tournamentRepository.getTournaments()
    })

    let {
        data: {
            events = [] as TournamentEventDto[],
        } = {} as TournamentEventListDto,
    } = useQuery({
        queryKey: ["events"],
        queryFn: () => tournamentEventRepository.getTournamentEvents()
    })

    let {
        data: {
            schedules = [] as TournamentEventScheduleDto[],
        } = {} as TournamentEventScheduleListDto,
    } = useQuery({
        queryKey: ["schedules"],
        queryFn: () => tournamentEventScheduleRepository.getAllSchedules()
    })

    async function createTournament() {
        let tournament = await tournamentRepository.postTournament();
        await navigate(`/tournament/${tournament.id}/edit`)
    }

    async function createEvent() {
        const now = new Date();
        const date = now.toISOString().split('T')[0] + 'T18:00';
        let event = await tournamentEventRepository.createTournamentEvent(
            "New Event", 
            date, 
            undefined, 
            undefined, 
            5
        );
        await navigate(`/tournament-event/${event.id}/edit`)
    }

    async function createSchedule() {
        // Default to Monday at 18:00
        let schedule = await tournamentEventScheduleRepository.createSchedule(
            "New Schedule", 
            1, // Monday
            "18:00"
        );
        await navigate(`/tournament-event-schedule/${schedule.id}/edit`)
    }

    let tournamentsVisible = tournaments.filter(
        it => it.status === "ACTIVE" || it.status === "PLANNED"
    );
    if (tournamentsVisible.length === 0) {
        const maxTourNumber: number = 2;
        //sorted on server's side
        tournamentsVisible = tournaments.slice(0, maxTourNumber);
    }

    // Filter events to show only upcoming (PLANNED) and active ones
    let eventsVisible = events.filter(
        it => it.status === "ACTIVE" || it.status === "PLANNED"
    );
    if (eventsVisible.length === 0) {
        const maxEventNumber: number = 2;
        eventsVisible = events.slice(0, maxEventNumber);
    }

    // Filter schedules to show only active ones
    let schedulesVisible = schedules.filter(
        it => it.status === "ACTIVE"
    );
    if (schedulesVisible.length === 0) {
        const maxScheduleNumber: number = 2;
        schedulesVisible = schedules.slice(0, maxScheduleNumber);
    }

    const maxUsers: number = 8;
    return <>
        <MyActiveTournamentPane/>
        <div className={"p-3"}>
            <MemberList users={users.slice(0, maxUsers)}/>
            <div className={"grid py-2"}>
                <Link to={"/users"}>
                    <button className={"btn bg-primary-400 w-full"}>
                        {loc("All users")}
                    </button>
                </Link>
            </div>
        </div>
        <div className={"p-3"}>
            <TournamentsList tournaments={tournamentsVisible} createTournament={createTournament}/>
            <div className={"grid py-2"}>
                <Link to={"/tournaments"}>
                    <button className={"btn bg-primary-400 w-full"}>
                        {loc("All tournaments")}
                    </button>
                </Link>
            </div>
        </div>
        <div className={"p-3"}>
            <EventsList events={eventsVisible} createEvent={createEvent}/>
            <div className={"grid py-2"}>
                <Link to={"/tournament-events"}>
                    <button className={"btn bg-primary-400 w-full"}>
                        {loc("All events")}
                    </button>
                </Link>
            </div>
        </div>
        <div className={"p-3"}>
            <SchedulesList schedules={schedulesVisible} createSchedule={createSchedule}/>
            <div className={"grid py-2"}>
                <Link to={"/tournament-event-schedules"}>
                    <button className={"btn bg-primary-400 w-full"}>
                        {loc("All schedules")}
                    </button>
                </Link>
            </div>
        </div>
    </>
}

export default MainPage
