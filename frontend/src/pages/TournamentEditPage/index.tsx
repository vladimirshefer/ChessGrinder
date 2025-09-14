import {Link, useNavigate, useParams} from "react-router-dom";
import {useQuery} from "@tanstack/react-query";
import {useForm} from "react-hook-form";
import {useLoc} from "strings/loc";
import {requirePresent} from "lib/util/common";
import dayjs from "dayjs";
import tournamentRepository from "lib/api/repository/TournamentRepository";
import {DEFAULT_DATETIME_FORMAT, PairingStrategy, RepeatableType, TournamentDto} from "lib/api/dto/MainPageData";
import DropdownSelect from "components/DropdownSelect";
import React, {useEffect, useMemo, useState} from "react";

export default function TournamentEditPage() {
    let loc = useLoc()
    let navigate = useNavigate();
    let {tournamentId} = useParams();
    let tournamentQuery = useQuery({
        queryKey: ["tournament", tournamentId],
        queryFn: async () => {
            return await tournamentRepository.getTournament(tournamentId!!);
        },
        enabled: !!tournamentId,
    })

    if (!tournamentId) {
        return <>No tournament id provided</>
    }

    if (tournamentQuery.isError) {
        return <>Loading error</>
    }

    if (tournamentQuery.isLoading || !tournamentQuery.isSuccess || !tournamentQuery.data) {
        return <>Loading</>
    }

    let tournament: TournamentDto = {...tournamentQuery.data!!} as TournamentDto// Added - Default value
    let cancelLink = !!tournamentId ? `/tournament/${tournamentId}` : ``;

    async function saveTournament(data: { [key: string]: string }) {
        let tournament = requirePresent(tournamentQuery.data, "Tournament not loaded")
        setModifiedTournamentData(tournament, data);
        try {
            await tournamentRepository.updateTournament(tournament);
            navigate(`/tournament/${tournamentId}`, {replace: true});
        }
        catch (error: any) {
            alert(loc(error.response.data.message));
        }
    }

    return <>
        <h1 className={"text-left text-lg uppercase font-semibold p-2"}>{loc("Edit tournament")}</h1>
        <TournamentEditPageImpl
            tournament={tournament}
            saveTournament={saveTournament}
            cancelLink={cancelLink}
        />
    </>
}

export function TournamentCreatePage() {
    let loc = useLoc()
    let navigate = useNavigate();

    let allTournamentsQuery = useQuery({
        queryKey: ["tournaments"],
        queryFn: async () => {
            return await tournamentRepository.getTournaments();
        }
    })

    let [copyFrom, setCopyFrom] = useState<TournamentDto | undefined>()

    let tournament: TournamentDto = useMemo(() => {
        return copyFrom || {
            id: "",
            name: "",
            repeatable: undefined,
            locationName: undefined,
            locationUrl: undefined,
            pairingStrategy: "SWISS",
            registrationLimit: undefined,
            roundsNumber: 6,
            status: undefined,
            date: dayjs(new Date()).format(DEFAULT_DATETIME_FORMAT),
            city: undefined,
        };
    }, [copyFrom])

    async function saveTournament(data: { [key: string]: string }) {
        try {
            let tournamentCopy = {...tournament};
            setModifiedTournamentData(tournamentCopy, data);
            let createdTournament = await tournamentRepository.postTournament();
            tournamentCopy.id = createdTournament.id;
            await tournamentRepository.updateTournament(tournamentCopy);
            navigate(`/tournament/${createdTournament.id}`, {replace: true});
        } catch (error: any) {
            alert(loc(error.response.data.message));
        }
    }

    return <>
        <h1 className={"text-left text-lg uppercase font-semibold p-2"}>{loc("Create tournament")}</h1>
        <div className={"bg-white text-left p-2"}>
            <DropdownSelect
                values={allTournamentsQuery.data?.values || []}
                emptyPresenter={() => <div>
                <span className={"text-sm text-gray-500 p-1 uppercase"}>
                    {loc("Copy from")}
                </span>
                </div>}
                presenter={it => <div>
                    <div className={"grid text-left bg-white p-2"}>
                        <span className={"text-sm"}>{it.name}</span>
                        <span className={"text-xs text-gray-500"}>{`${it.city} ${it.date}`}</span>
                    </div>
                </div>}
                onSelect={it => setCopyFrom(it)}
                keyExtractor={it => it.id}
                matchesSearch={(s, trn) => {
                    return trn.name.toLowerCase().includes(s.toLowerCase())
                        || trn.city?.toLowerCase()?.includes(s.toLowerCase())
                        || trn.locationName?.toLowerCase()?.includes(s.toLowerCase())
                        || trn.id.toLowerCase().includes(s.toLowerCase());
                }}
            />
        </div>
        <TournamentEditPageImpl
            tournament={tournament}
            saveTournament={saveTournament}
            cancelLink={`/`}
        />
    </>
}

function TournamentEditPageImpl(
    {
        tournament,
        saveTournament,
        cancelLink,
    }: {
        tournament: TournamentDto,
        saveTournament: (data: { [key: string]: string }) => void,
        cancelLink: string,
    }
) {
    let loc = useLoc()
    const {register, handleSubmit, reset} = useForm();

    useEffect(() => {
        reset();
    }, [tournament, reset]);

    let now = new Date();
    let tournamentStartDate = dayjs(tournament.date || now, DEFAULT_DATETIME_FORMAT).format("YYYY-MM-DD")
    let tournamentStartTime = dayjs(tournament.date || now, DEFAULT_DATETIME_FORMAT).format("HH:mm")

    return <>
        <form className={"grid gap-2 p-2 text-left"} onSubmit={handleSubmit(saveTournament)}>
            <input type={"text"} id={"tournamentName"}
                   placeholder={loc("Tournament Name").toUpperCase()} {...register("name")}
                   className={"border-b"}
                   defaultValue={tournament.name}/>
            <input type={"text"} id={"locationName"} placeholder={loc("Location Name")} {...register("locationName")}
                   className={"border-b"}
                   defaultValue={tournament.locationName}/>
            <input type={"text"} id={"locationUrl"} placeholder={loc("Location Link")} {...register("locationUrl")}
                   className={"border-b"}
                   defaultValue={tournament.locationUrl}/>
            <input type={"date"} id={"startDate"} placeholder={"Start Date"} {...register("startDate")}
                   className={"border-b"}
                   defaultValue={tournamentStartDate}/>
            <input type={"time"} id={"startTime"} placeholder={"Start Time"} {...register("startTime")}
                   className={"border-b"}
                   defaultValue={tournamentStartTime}/>
            <label htmlFor={"city"}>{loc("City")}</label>
            <select id={"city"} {...register("city")} defaultValue={tournament.city || "Other"} className={"border-b"}>
                <option value="Berlin">Berlin</option>
                <option value="Tbilisi">Tbilisi</option>
                <option value="Limassol">Limassol</option>
                <option value="Other">Other</option>
            </select>
            <label htmlFor={"roundsNumber"}>{loc("Rounds number")}</label>
            <input type={"number"} id={"roundsNumber"} placeholder={loc("Rounds number")}
                    {...register("roundsNumber")}
                   defaultValue={tournament.roundsNumber || 0}/>
            <input type={"number"} id={"registrationLimit"} placeholder={loc("Registration limit")}
                    {...register("registrationLimit")}
                   defaultValue={tournament.registrationLimit || 0}/>

            <label htmlFor={"pairingStrategy"}>{loc("Pairing Strategy")}</label>
            <select id={"pairingStrategy"} {...register("pairingStrategy")}
                    defaultValue={tournament.pairingStrategy || "SWISS"} className={"border-b"}>
                       <option value="SWISS">{loc("Swiss")}</option>
                       <option value="ROUND_ROBIN">{loc("Round Robin")}</option>
                       <option value="SIMPLE">{loc("Simple")}</option>
            </select>

            <label htmlFor={"repeatable"}>{loc("Repeatable")}</label>
            <select id={"repeatable"} {...register("repeatable")} defaultValue={tournament.repeatable || ""}
                    className={"border-b"}>
                       <option value="">{loc("None")}</option>
                       <option value="WEEKLY">{loc("Weekly")}</option>
            </select>

            <div className={"flex gap-2 justify-end"}>
                <button type={"submit"} className={"btn-primary uppercase font-semibold"}>
                    {loc("Save")}
                </button>
                <Link to={cancelLink} replace={true}>
                    <button className={"btn-light uppercase font-semibold"}>
                        {loc("Cancel")}
                    </button>
                </Link>
            </div>
        </form>
    </>
}

function setModifiedTournamentData(tournament: TournamentDto, data: { [p: string]: string }) {
    tournament.name = data.name
    tournament.locationUrl = data.locationUrl
    tournament.locationName = data.locationName
    tournament.city = data.city !== "Other" ? data.city : undefined;
    tournament.roundsNumber = parseInt(data.roundsNumber, 10);
    tournament.pairingStrategy = data.pairingStrategy as PairingStrategy;
    tournament.registrationLimit = !!data.registrationLimit ? parseInt(data.registrationLimit, 10) : undefined;
    tournament.repeatable = data.repeatable as RepeatableType || null;
    let startTime: string = data.startTime || "20:00";
    let startDate: string = data.startDate || "2023-10-11"
    tournament.date = dayjs(startDate + "T" + startTime, DEFAULT_DATETIME_FORMAT).format(DEFAULT_DATETIME_FORMAT)
}
