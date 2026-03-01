import {TournamentDto, UserRoles} from "lib/api/dto/MainPageData";
import ConditionalOnUserRole, {Conditional} from "components/Conditional";
import {useLoc} from "strings/loc";
import "pages/MainPage/TournamentsList.css"
import {TournamentPane} from "pages/MainPage/TournamentPane";
import dayjs from "dayjs";
import {useMemo} from "react";

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

    /**
     * sort by closeness to now with a head start for upcoming tournaments
     * score = |deltaDays| - 7 for future dates, |deltaDays| for past dates; lower score first
     */
    const sortedTournaments = useMemo(() => {
        const HEAD_START_DAYS = 7;
        const now = dayjs();
        return [...(tournaments || [])].sort((a, b) => {
            const deltaA = dayjs(a.date).diff(now, 'day', true); // positive -> future, negative -> past
            const deltaB = dayjs(b.date).diff(now, 'day', true);

            const scoreA =  Math.abs(deltaA) - (deltaA >= 0 ? HEAD_START_DAYS : 0);
            const scoreB = Math.abs(deltaB) - (deltaB >= 0 ? HEAD_START_DAYS : 0);
            return scoreA - scoreB;
        });
    }, [tournaments]);

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
            <Conditional on={!sortedTournaments || sortedTournaments.length === 0}>
                <div className={"col-span-12"}>
                    <span>No tournaments</span>
                </div>
            </Conditional>
            {sortedTournaments.map(tournament => {
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
