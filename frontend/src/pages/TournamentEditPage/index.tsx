import {useParams} from "react-router-dom";
import {useQuery} from "@tanstack/react-query";
import tournamentPageRepository from "lib/api/repository/TournamentPageRepository";
import {useForm} from "react-hook-form";
import {useLoc} from "strings/loc";

export default function TournamentEditPage() {
    let loc = useLoc()
    let {tournamentId} = useParams();
    let tournamentQuery = useQuery({
        queryKey: ["tournament", tournamentId],
        queryFn: async () => {
            return await tournamentPageRepository.getData(tournamentId!!);
        },
    })

    const {register, handleSubmit} = useForm();

    function saveTournament(data: any) {
        alert("Tournament edit is not supported yet")
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

    let tournament = tournamentQuery.data!!

    return <>
        <h1>Edit tournament</h1>
        <form className={"grid"} onSubmit={handleSubmit(saveTournament)}>
            <label htmlFor="tournamentName" className={"text-sm text-gray-800"}>
                {loc("Tournament Name")}
            </label>
            <input type={"text"} id={"tournamentName"} placeholder={loc("Tournament Name")} {...register("name")}
                   value={tournament.tournament.name}/>

            <label htmlFor="locationName" className={"text-sm text-gray-800"}>
                {loc("Location Name")}
            </label>
            <input type={"text"} id={"locationName"} placeholder={"Location Name"} {...register("locationName")}/>

            <label htmlFor="locationLink" className={"text-sm text-gray-800"}>
                {loc("Location Link")}

            </label>
            <input type={"text"} id={"locationLink"} placeholder={"Location Link"} {...register("locationLink")}/>

            <label htmlFor="startDate" className={"text-sm text-gray-800"}>
                {loc("Start Date")}
            </label>
            <input type={"date"} id={"startDate"} placeholder={"Start Date"} {...register("startDate")}/>

            <label htmlFor="startTime" className={"text-sm text-gray-800"}>
                {loc("Start Time")}
            </label>
            <input type={"time"} id={"startTime"} placeholder={"Start Time"} {...register("startTime")}/>

            <button type={"submit"} className={"btn-dark"}>Save</button>
        </form>
    </>
}
