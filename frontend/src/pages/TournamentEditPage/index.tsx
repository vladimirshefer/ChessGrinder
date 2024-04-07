import {Link, useNavigate, useParams} from "react-router-dom";
import {useQuery} from "@tanstack/react-query";
import tournamentPageRepository from "lib/api/repository/TournamentPageRepository";
import {useForm} from "react-hook-form";
import {useLoc} from "strings/loc";
import {requirePresent} from "lib/util/common";
import dayjs from "dayjs";
import {TournamentPageData} from "../../lib/api/dto/TournamentPageData";
import tournamentRepository from "../../lib/api/repository/TournamentRepository";
import {DEFAULT_DATETIME_FORMAT} from "../../lib/api/dto/MainPageData";

export default function TournamentEditPage() {
    let loc = useLoc()
    let navigate = useNavigate();
    let {tournamentId} = useParams();
    let tournamentQuery = useQuery({
        queryKey: ["tournament", tournamentId],
        queryFn: async () => {
            return await tournamentPageRepository.getData(tournamentId!!);
        },
    })

    const {register, handleSubmit} = useForm();

    async function saveTournament(data: { [key: string]: string}) {
        let tournamentPageData = requirePresent(tournamentQuery.data, "Tournament not loaded");
        let tournament = tournamentPageData.tournament
        tournament.name = data.name
        tournament.locationUrl = data.locationUrl
        tournament.locationName = data.locationName
        tournament.numberOfRounds = parseInt(data.numberOfRounds, 10);
        let startTime: string = data.startTime || "20:00";
        let startDate: string = data.startDate || "2023-10-11"
        tournament.date = dayjs(startDate + "T" + startTime, DEFAULT_DATETIME_FORMAT).format(DEFAULT_DATETIME_FORMAT)
        try {
            await tournamentRepository.updateTournament(tournament);
            navigate(`/tournament/${tournamentId}`);
        }
        catch (error: any) {
            alert(loc(error.response.data.message));
        }
    }

    if (!tournamentId) {
        return <>No tournament id provided</>
    }

    if (tournamentQuery.isError) {
        return <>Loading error</>
    }

    if (tournamentQuery.isLoading) {
        return <>Loading</>
    }

    let tournament: TournamentPageData = {...tournamentQuery.data!!} as TournamentPageData
    let tournamentStartDate = dayjs(tournament.tournament.date, DEFAULT_DATETIME_FORMAT).format("YYYY-MM-DD")
    let tournamentStartTime = dayjs(tournament.tournament.date, DEFAULT_DATETIME_FORMAT).format("HH:mm")
    const tournamentNumberOfRounds: number = tournament.tournament.numberOfRounds;

    return <>
        <h1 className={"text-left text-lg uppercase font-semibold p-2"}>{loc("Edit tournament")}</h1>
        <form className={"grid gap-2 p-2 text-left"} onSubmit={handleSubmit(saveTournament)}>
            <input type={"text"} id={"tournamentName"}
                   placeholder={loc("Tournament Name").toUpperCase()} {...register("name")}
                   className={"border-b"}
                   defaultValue={tournament.tournament.name}/>
            <input type={"text"} id={"locationName"} placeholder={loc("Location Name")} {...register("locationName")}
                   className={"border-b"}
                   defaultValue={tournament.tournament.locationName}/>
            <input type={"text"} id={"locationUrl"} placeholder={loc("Location Link")} {...register("locationUrl")}
                   className={"border-b"}
                   defaultValue={tournament.tournament.locationUrl}/>
            <input type={"date"} id={"startDate"} placeholder={"Start Date"} {...register("startDate")}
                   className={"border-b"}
                   defaultValue={tournamentStartDate}/>
            <input type={"time"} id={"startTime"} placeholder={"Start Time"} {...register("startTime")}
                   className={"border-b"}
                   defaultValue={tournamentStartTime}/>
            <label htmlFor={"numberOfRounds"}>{loc("Number of rounds")}</label>
            <input type={"number"} id={"numberOfRounds"} placeholder={loc("Number of rounds")}
                    {...register("numberOfRounds")}
                    defaultValue={tournamentNumberOfRounds}/>

            <div className={"flex gap-2 justify-end"}>
                <button type={"submit"} className={"btn-primary uppercase font-semibold"}>
                    {loc("Save")}
                </button>
                <Link to={`/tournament/${tournamentId}`}>
                    <button className={"btn-light uppercase font-semibold"}>
                        {loc("Cancel")}
                    </button>
                </Link>
            </div>
        </form>
    </>
}
