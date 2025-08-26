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
        <h2 className={"text-xl my-2 text-left px-3 uppercase font-semibold"}>{loc("Tournaments")}</h2>
        <ul className={"grid grid-cols-12 gap-2"}>
            <ConditionalOnUserRole role={UserRoles.ADMIN}>
                <li className={"col-span-12 md:col-span-6 lg:col-span-4 xl:col-span-3 " +
                    "bg-gray-200 flex justify-center content-center"}>
                    <button className={"h-full w-full text-xl text-primary-600 p-3"}
                            onClick={createTournament}
                    >
                        + {loc("Tournament")}
                    </button>
                </li>
            </ConditionalOnUserRole>
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
