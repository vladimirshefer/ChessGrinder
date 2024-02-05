import {Link, useNavigate, useParams} from "react-router-dom";
import React, {useMemo, useState} from "react";
import ResultsTable from "pages/TournamentPage/ResultsTable";
import {useQuery} from "@tanstack/react-query";
import tournamentPageRepository from "lib/api/repository/TournamentPageRepository";
import {MatchDto, MatchResult, ParticipantDto, TournamentPageData} from "lib/api/dto/TournamentPageData";
import RoundTab from "pages/TournamentPage/RoundTab";
import ConditionalOnUserRole, {Conditional} from "components/Conditional";
import participantRepository from "lib/api/repository/ParticipantRepository";
import {ListDto, UserDto, UserRoles} from "lib/api/dto/MainPageData";
import userRepository from "lib/api/repository/UserRepository";
import {AiOutlineClose, AiOutlineDelete, AiOutlineEdit, AiOutlineHome, AiOutlinePlus} from "react-icons/ai";
import {useLoc} from "strings/loc";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import dayjs from "dayjs";
import DropdownSelect from "../../components/DropdownSelect";

function AddParticipant(
    {
        participants,
        addParticipant
    }: {
        participants: ParticipantDto[],
        addParticipant: (participant: ParticipantDto) => Promise<void>
    }
) {
    let loc = useLoc()
    let [inputEnabled, setInputEnabled] = useState<boolean>(false);
    let [selectedValue, setSelectedValue] = useState<string>("")
    let [nickName, setNickName] = useState("")

    let {
        data: {
            values: users = [] as UserDto[]
        } = {} as ListDto<UserDto>
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
            isMissing: false
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
                    <div className={"col-span-12 py-1"}>
                        <input className={"border-b-2 border-b-blue-300 w-full px-2 outline-none"}
                               autoFocus
                               name={"nickname"}
                               onChange={event => setNickName(event.target.value)}
                               placeholder={`${loc("Nickname")}*`.toUpperCase()}
                        />
                    </div>
                    <div className={"col-span-12 grid text-left py-2 bg-white"}>
                        <DropdownSelect<UserDto>
                            className={"border-b-2 border-blue-300 w-full"}
                            values={users}
                            onSelect={user => setSelectedValue(user?.id || "")}
                            keyExtractor={user => user.id}
                            matchesSearch={(searchQuery, user) =>
                                user.username.toLowerCase().includes(searchQuery.toLowerCase()) ||
                                user.name.toLowerCase().includes(searchQuery.toLowerCase())
                            }
                            emptyPresenter={() => <div>
                                <span className={"text-sm text-gray-500 p-1"}>
                                    {loc("Guest")}
                                </span>
                            </div>}
                            presenter={user => <div>
                                <div className={"grid text-left bg-white p-2"}>
                                    <span className={"text-sm"}>{user.name}</span>
                                    <span className={"text-xs text-gray-500"}>{user.username}</span>
                                </div>
                            </div>}
                        />
                    </div>
                    <div className={"col-span-12 flex gap-x-1"}>
                        <button className={"btn-dark uppercase col-span-8 grow"}
                                onClick={() => {
                                    if (selectedValue || nickName) {
                                        getSubmitValue(selectedValue);
                                    }
                                    setInputEnabled(false)
                                }}>
                            {loc("Add participant")}
                        </button>
                        <button className={"btn-light !px-6"}
                                onClick={() => {
                                    setInputEnabled(false)
                                }}>
                            <AiOutlineClose className={"block"}/>
                        </button>
                    </div>
                </div>
            </Conditional>
        </>
    </div>;
}

function TournamentPage() {
    let loc = useLoc()
    let {id, roundId: roundIdStr} = useParams();
    let roundId = useMemo(() => roundIdStr ? parseInt(roundIdStr) : null, [roundIdStr]);
    let tournamentQuery = useQuery({
        queryKey: ["tournamentPageData", id],
        queryFn: () => id ? tournamentPageRepository.getData(id) : Promise.reject<TournamentPageData>()
    });

    let {data: tournamentData} = tournamentQuery;

    let navigate = useNavigate()
    let roundNumbers = tournamentQuery.data?.rounds?.map((e, idx) => idx + 1) || [];
    let participants: ParticipantDto[] = tournamentQuery.data?.participants || [];

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

    if (tournamentQuery.isError) return <>Error!</>
    if (!tournamentQuery.isSuccess) return <>Loading</>
    let tournament = tournamentQuery.data!!.tournament;

    return <>
        <div className={"flex mt-4 p-2 items-top content-center"}>
            <h2 className={"text-lg font-semibold text-left grow"}>
                {tournament.name || loc(`Unnamed Tournament`)}
            </h2>
            <div className={"px-2"}>
                <div>
                    <small className={"font-semibold text-gray-500"}>{tournament?.status}</small>
                </div>
                <div>
                    <span className={"font-semibold"}>{tournament?.date && dayjs(tournament.date).format("DD.MM.YY")}</span>
                </div>
            </div>
        </div>
        <div className={"flex flex-wrap text-sm justify-start place-items-stretch w-full px-2 my-4"}>
            <Link className={"lg:col-span-1"} to={`/tournament/${id}`}>
                <button
                    className={`w-full h-full py-1 px-3 border border-black uppercase ${!roundId ? "bg-anzac-400 text-white" : "hover:bg-gray-300 text-black"}`}
                >
                    <AiOutlineHome/>
                </button>
            </Link>
            {roundNumbers.map(rid => {
                return <Link key={rid} to={`/tournament/${id}/round/${rid}`}>
                    <button
                        className={`w-full py-1 px-3  border border-black
                                    ${rid === roundId ? "bg-primary text-white" : "hover:bg-gray-300"}`}>
                        {rid}
                    </button>
                </Link>
            })}
            <Conditional on={tournament.status !== "FINISHED"}>
                <ConditionalOnUserRole role={UserRoles.ADMIN}>
                    <button className={`py-1 px-3`}
                            onClick={createRound}
                    ><AiOutlinePlus/>
                    </button>
                </ConditionalOnUserRole>
            </Conditional>
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
        <Conditional on={!roundId}>
            <ConditionalOnUserRole role={UserRoles.ADMIN}>
                <div className={"p-2"}></div>
                <div className={"flex gap-1 justify-end p-2"}>
                    <Conditional on={tournament.status !== "ACTIVE"}>
                        <button className={"btn-primary uppercase !px-4"}
                                onClick={async () => {
                                    await tournamentRepository.startTournament(tournament?.id!!);
                                    await tournamentQuery.refetch()
                                }}
                        >{loc("Start")}
                        </button>
                    </Conditional>
                    <Conditional on={tournament.status === "ACTIVE"}>
                        <button className={"btn-primary uppercase"}
                                onClick={async () => {
                                    await tournamentRepository.finishTournament(tournament?.id!!);
                                    await tournamentQuery.refetch()
                                }}
                        >{loc("Finish")}
                        </button>
                    </Conditional>
                    <Link to={`/tournament/${tournament.id}/edit`}>
                        <button className={"btn-light h-full !px-4"}>
                            <AiOutlineEdit/>
                        </button>
                    </Link>
                    <button className={"btn-danger uppercase !px-4"}
                            onClick={async () => {
                                let expectedConfirmation = (tournament?.name || tournament?.id || "");
                                let confirmation = prompt(`Are you sure?\nTo delete tournament enter \n${expectedConfirmation}`);
                                if (confirmation !== expectedConfirmation) {
                                    alert("You entered wrong id. Tournament will not be deleted.");
                                    return;
                                }
                                await tournamentRepository.deleteTournament(tournament?.id!!);
                                await navigate("/");
                            }}
                    >
                        <AiOutlineDelete/>
                    </button>
                </div>
            </ConditionalOnUserRole>
        </Conditional>
    </>
}

export default TournamentPage;
