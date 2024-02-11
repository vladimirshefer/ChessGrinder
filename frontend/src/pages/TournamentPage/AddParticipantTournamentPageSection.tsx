import {ParticipantDto} from "lib/api/dto/TournamentPageData";
import {useLoc} from "strings/loc";
import React, {useState} from "react";
import {ListDto, UserDto} from "lib/api/dto/MainPageData";
import {useQuery} from "@tanstack/react-query";
import userRepository from "lib/api/repository/UserRepository";
import {Conditional} from "components/Conditional";
import DropdownSelect from "components/DropdownSelect";
import {AiOutlineClose} from "react-icons/ai";

export default function AddParticipantTournamentPageSection(
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
            isMissing: false,
            place: -1,
        };
        console.log(participant)
        return await addParticipant(participant);
    }

    return <div className={"col-span-12 px-2 my-1"}>

        <>
            <Conditional on={!inputEnabled}>
                <button className={"w-full bg-black text-white p-1 uppercase"}
                        title={loc("Add participant")}
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
                                    <span className={"text-xs text-gray-500"}>{user.id}</span>
                                </div>
                            </div>}
                        />
                    </div>
                    <div className={"col-span-12 flex gap-x-1"}>
                        <button className={"btn-dark uppercase col-span-8 grow"}
                                title={loc("Add participant")}
                                onClick={() => {
                                    if (selectedValue || nickName) {
                                        getSubmitValue(selectedValue);
                                    }
                                    setInputEnabled(false)
                                }}>
                            {loc("Add participant")}
                        </button>
                        <button className={"btn-light !px-6"}
                                title={loc("Cancel")}
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
