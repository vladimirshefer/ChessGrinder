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
import {AiOutlineClose, AiOutlineInfoCircle} from "react-icons/ai";
import loc from "strings/loc";

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
                    <div className={"col-span-12 lg:col-span-9"}>
                        <input className={"border-b-2 border-b-blue-300 w-full px-2 outline-none"}
                               autoFocus list="users" name="user"
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
                    <div className={"col-span-12 lg:col-span-9"}>
                        <input className={"border-b-2 border-b-blue-300 w-full px-2 outline-none"}
                               name={"nickname"}
                               onChange={event => setNickName(event.target.value)}
                               placeholder={loc("Nickname")}
                        />
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
    let {data: tournamentData, refetch, isSuccess: isDataReady} = useQuery({
        queryKey: ["tournamentPageData", id],
        queryFn: () => id ? tournamentPageRepository.getData(id) : Promise.reject<TournamentPageData>()
    });
    let navigate = useNavigate()
    let roundNumbers = tournamentData?.rounds?.map((e, idx) => idx + 1) || [];
    let participants: ParticipantDto[] = useMemo(() => tournamentData?.participants || [], [tournamentData])

    async function addParticipant(participant: ParticipantDto) {
        await participantRepository.postParticipant(id!!, participant)
        await refetch()
    }

    async function openParticipant(participant: ParticipantDto) {
        navigate(`/tournament/${id}/participant/${participant.id}`)
    }

    async function createRound() {
        await tournamentPageRepository.postRound(id!!)
        await refetch()
        navigate(`/tournament/${id}` + (tournamentData && tournamentData.rounds ? `/round/${tournamentData.rounds.length + 1}` : ""))
    }

    async function drawRound() {
        await tournamentPageRepository.drawRound(id!!, roundId!!)
        await refetch()
    }

    async function submitMatchResult(match: MatchDto, result: MatchResult) {
        await tournamentPageRepository.postMatchResult(id!!, roundId!!, match.id, result)
        await refetch()
    }

    if (!isDataReady) return <>Loading</>

    async function deleteRound() {
        await tournamentPageRepository.deleteRound(id!!, roundId!!)
        navigate(`/tournament/${id}`)
    }

    async function finishRound() {
        await tournamentPageRepository.finishRound(id!!, roundId!!)
        await refetch()
    }

    async function reopenRound() {
        await tournamentPageRepository.reopenRound(id!!, roundId!!)
        await refetch()
    }

    return <>
        <h2 className={"text-lg font-bold mt-4"}>
            Tournament {id}
        </h2>
        <div className={"grid grid-cols-12 w-full px-2 my-4"}>
            <Link className={"col-span-3 lg:col-span-1"} to={`/tournament/${id}`}>
                <button
                    className={`w-full p-2 rounded ${!roundId ? "bg-yellow-300" : "bg-gray-100 hover:bg-yellow-100"}`}
                >Home
                </button>
            </Link>
            {roundNumbers.map(rid => {
                return <Link key={rid} to={`/tournament/${id}/round/${rid}`}>
                    <button className={`w-full rounded p-2 ${rid === roundId ? "bg-blue-300" : "hover:bg-blue-100"}`}>
                        {rid}
                    </button>
                </Link>
            })}
            <ConditionalOnUserRole role={UserRoles.ADMIN}>
                <button className={`w-full rounded p-2 bg-gray-100 col-span-2 lg:col-span-1`}
                        onClick={createRound}
                >+
                </button>
            </ConditionalOnUserRole>
        </div>

        {
            !roundId
                ? (
                    <>
                        <h3>Status</h3>
                        <ConditionalOnUserRole role={UserRoles.ADMIN}>
                            <AddParticipant participants={participants} addParticipant={addParticipant}/>
                        </ConditionalOnUserRole>
                        <ResultsTable participants={participants}
                                      openParticipant={(it) => {
                                          openParticipant(it)
                                      }}
                        />
                    </>
                )
                : (
                    <RoundTab
                        round={tournamentData?.rounds[roundId - 1]!!}
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
                )
        }
    </>
}

export default TournamentPage;
