import {TournamentDto, UserRoles} from "lib/api/dto/MainPageData";
import ConditionalOnUserRole, {Conditional} from "components/Conditional";
import {useLoc} from "strings/loc";
import "pages/MainPage/TournamentsList.css"
import {TournamentPane} from "pages/MainPage/TournamentPane";

export function TournamentsList(
    {
        tournaments,
        createTournament,
    }: {
        tournaments: TournamentDto[]
        createTournament: () => void
    }
) {
    let loc = useLoc()

    return <div>
        <div className={"flex gap-3 items-stretch my-2"}>
            <h2 className={"text-xl uppercase text-left font-semibold"}>{loc("Tournaments")}</h2>
            <ConditionalOnUserRole role={UserRoles.ADMIN}>
                <button className={"bg-primary-400 text-white px-3 uppercase text-sm"}
                    onClick={createTournament}
                >
                    + {loc("Create")}
                </button>
            </ConditionalOnUserRole>
        </div>
        <ul className={"grid grid-cols-12 gap-2"}>
            <Conditional on={!tournaments || tournaments.length === 0}>
                <div className={"col-span-12"}>
                    <span>No tournaments</span>
                </div>
            </Conditional>
            {tournaments.map(tournament => {
                return <li key={tournament.id}
                           className={`col-span-12 md:col-span-6 lg:col-span-4 xl:col-span-3 
                                       flex content-center`}>
                    <TournamentPane tournament={tournament}/>
                </li>
            })}
        </ul>
    </div>;
}

export default TournamentsList
