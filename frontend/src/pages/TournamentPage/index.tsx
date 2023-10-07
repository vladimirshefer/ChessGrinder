import {Link, useNavigate, useParams} from "react-router-dom";
import React, {useMemo, useState} from "react";
import ResultsTable from "pages/TournamentPage/ResultsTable";
import {useQuery} from "@tanstack/react-query";
import tournamentPageRepository from "lib/api/repository/TournamentPageRepository";
import {MatchDto, MatchResult, ParticipantDto, TournamentPageData} from "lib/api/dto/TournamentPageData";
import RoundTab from "pages/TournamentPage/RoundTab";
import ConditionalOnUserRole, {Conditional} from "components/Conditional";
import participantRepository from "lib/api/repository/ParticipantRepository";
import {ListDto, MemberDto, UserRoles} from "lib/api/dto/MainPageData";
import userRepository from "lib/api/repository/UserRepository";
import {AiOutlineClose, AiOutlineHome, AiOutlineInfoCircle} from "react-icons/ai";
import loc from "strings/loc";
import tournamentRepository from "lib/api/repository/TournamentRepository";

function AddParticipant(
    {
        participants,
        addParticipant
    }: {
        participants: ParticipantDto[],
        addParticipant: (participant: ParticipantDto) => Promise<void>
    }
) {
    let [inputEnabled, setInputEnabled] = useState<boolean>(false);
    let [selectedValue, setSelectedValue] = useState<string>("")
    let [nickName, setNickName] = useState("")

    let {
        data: {
            values: users = [] as MemberDto[]
        } = {} as ListDto<MemberDto>
    } = useQuery({
        queryKey: ["users"],
        queryFn: async () => await userRepository.getUsers(),
        enabled: inputEnabled
    })

    async function getSubmitValue(userId: string) {
        let participant: ParticipantDto = {
            id: userId || nickName,
            userId: userId || undefined,
            name: nickName || participants.find(it => it.id === userId)?.name || userId,
            score: 0,
            buchholz: 0,
        };
        console.log(participant)
        return await addParticipant(participant);
    }

    return <div className={"col-span-12 px-2 my-1"}>

        <>
            <Conditional on={!inputEnabled}>
                <button className={"w-full bg-black text-white p-1 uppercase"}
                        onClick={() => setInputEnabled(true)}> {loc("Add participant")} </button>
            </Conditional>
            <Conditional on={inputEnabled}>
                <div className={"w-full grid grid-cols-12 p-1"}>
                    <div className={"col-span-12 lg:col-span-9 py-1"}>
                        <input className={"border-b-2 border-b-blue-300 w-full px-2 outline-none"}
                               autoFocus
                               name={"nickname"}
                               onChange={event => setNickName(event.target.value)}
                               placeholder={loc("Nickname")}
                        />
                    </div>
                    <div className={"col-span-12 lg:col-span-9 text-sm py-1"}>
                        <input className={"border-b-2 border-b-blue-300 w-full px-2 outline-none"}
                               list="users" name="user"
                               onChange={event => setSelectedValue(event.target.value)}
                               placeholder={loc("Username")}
                        />
                        <datalist id="users">
                            {users ? users.map(user =>
                                <option
                                    key={user.id}
                                    value={user.id}>
                                    {user.name}
                                </option>
                            ) : []}
                        </datalist>
                        <span className={"text-left text-sm w-full block text-gray-500 px-2"}
                        >
                            <AiOutlineInfoCircle className={"inline-block mr-1"}/>
                            {loc("Leave empty for anonymous/guest participant")}
                        </span>
                    </div>
                    <div className={"col-span-12 lg:col-span-3 p-1 px-2 grid grid-cols-12 gap-x-1"}>
                        <button className={"btn-dark w-full col-span-8"}
                                onClick={() => {
                                    if (selectedValue || nickName) {
                                        getSubmitValue(selectedValue);
                                    }
                                    setInputEnabled(false)
                                }}>
                            Add
                        </button>
                        <button className={"btn-light w-full bg-red-300 col-span-4"}
                                onClick={() => {
                                    setInputEnabled(false)
                                }}>
                            <span className={"inline-block"}><AiOutlineClose/></span>
                        </button>
                    </div>
                </div>
            </Conditional>
        </>
    </div>;
}

function TournamentPage() {
    let {id, roundId: roundIdStr} = useParams();
    let roundId = useMemo(() => roundIdStr ? parseInt(roundIdStr) : null, [roundIdStr]);
    let tournamentQuery = useQuery({
        queryKey: ["tournamentPageData", id],
        queryFn: () => id ? tournamentPageRepository.getData(id) : Promise.reject<TournamentPageData>()
    });

    let {data: tournamentData} = tournamentQuery;

    let navigate = useNavigate()
    let roundNumbers = tournamentData?.rounds?.map((e, idx) => idx + 1) || [];
    let participants: ParticipantDto[] = useMemo(() => tournamentQuery.data?.participants || [], [tournamentData])

    async function addParticipant(participant: ParticipantDto) {
        await participantRepository.postParticipant(id!!, participant)
        await tournamentQuery.refetch()
    }

    async function openParticipant(participant: ParticipantDto) {
        navigate(`/tournament/${id}/participant/${participant.id}`)
    }

    async function createRound() {
        await tournamentPageRepository.postRound(id!!)
        await tournamentQuery.refetch()
        navigate(`/tournament/${id}` + (tournamentData && tournamentData.rounds ? `/round/${tournamentData.rounds.length + 1}` : ""))
    }

    async function drawRound() {
        await tournamentPageRepository.drawRound(id!!, roundId!!)
        await tournamentQuery.refetch()
    }

    async function submitMatchResult(match: MatchDto, result: MatchResult) {
        await tournamentPageRepository.postMatchResult(id!!, roundId!!, match.id, result)
        await tournamentQuery.refetch()
    }

    if (!tournamentQuery.isSuccess) return <>Loading</>

    async function deleteRound() {
        await tournamentPageRepository.deleteRound(id!!, roundId!!)
        navigate(`/tournament/${id}`)
    }

    async function finishRound() {
        await tournamentPageRepository.finishRound(id!!, roundId!!)
        await tournamentQuery.refetch()
    }

    async function reopenRound() {
        await tournamentPageRepository.reopenRound(id!!, roundId!!)
        await tournamentQuery.refetch()
    }

    return <>
        <h2 className={"text-lg font-bold mt-4"}>
            Tournament {id}
        </h2>
        <div className={"flex flex-wrap justify-start place-items-stretch w-full px-2 my-4"}>
            <Link className={"lg:col-span-1"} to={`/tournament/${id}`}>
                <button
                    className={`w-full h-full py-2 px-4 uppercase ${!roundId ? "bg-black text-white" : "bg-primary hover:bg-gray-300 text-black"}`}
                >
                    <AiOutlineHome/>
                </button>
            </Link>
            {roundNumbers.map(rid => {
                return <Link key={rid} to={`/tournament/${id}/round/${rid}`}>
                    <button
                        className={`w-full py-2 px-3 
                                    ${rid === roundId ? "bg-black text-white" : "hover:bg-gray-300"}`}>
                        {rid}
                    </button>
                </Link>
            })}
            <ConditionalOnUserRole role={UserRoles.ADMIN}>
                <button className={`py-2 px-3`}
                        onClick={createRound}
                >+
                </button>
            </ConditionalOnUserRole>
        </div>
        <>
            <Conditional on={!roundId}>
                <ConditionalOnUserRole role={UserRoles.ADMIN}>
                    <AddParticipant participants={participants} addParticipant={addParticipant}/>
                </ConditionalOnUserRole>
                <div className={"p-2"}>{/*Just padding*/}</div>
                <ResultsTable participants={participants}
                              openParticipant={(it) => {
                                  openParticipant(it)
                              }}
                />
            </Conditional>
            <Conditional on={!!roundId}>
                <RoundTab
                    round={tournamentData?.rounds[roundId!! - 1]!!}
                    submitMatchResult={(match, result) => {
                        submitMatchResult(match, result!!);
                    }}
                    submitRoundFinished={() => {
                        finishRound()
                    }}
                    deleteRound={() => deleteRound()}
                    drawRound={() => drawRound()}
                    reopenRound={() => reopenRound()}
                />
            </Conditional>
        </>
        <div className={"p-2"}></div>
        <div className={"flex gap-1 justify-end p-2"}>
            <button className={"btn-dark"}
                onClick={() => alert("Edit not supported yet")}
            >Edit</button>
            <button className={"btn-dark"}
                    onClick={async () => {
                        await tournamentRepository.startTournament(tournamentData?.tournament.id!!);
                        await tournamentQuery.refetch()
                    }}
            >Start</button>
            <button className={"btn-dark"}
                    onClick={async () => {
                        await tournamentRepository.finishTournament(tournamentData?.tournament.id!!);
                        await tournamentQuery.refetch()
                    }}
            >Finish</button>
            <button className={"btn-danger"}
                    onClick={() => alert("Delete not supported yet")}
            >Delete</button>
        </div>
    </>
}

export default TournamentPage;
