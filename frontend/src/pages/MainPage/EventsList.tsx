import {TournamentEventDto} from "lib/api/dto/TournamentEventData";
import ConditionalOnUserRole, {Conditional} from "components/Conditional";
import {useLoc} from "strings/loc";
import {UserRoles} from "lib/api/dto/MainPageData";
import {EventPane} from "./EventPane";

export function EventsList(
    {
        events,
        createEvent,
    }: {
        events: TournamentEventDto[]
        createEvent: () => void
    }
) {
    let loc = useLoc()

    return <div>
        <h2 className={"text-xl my-2 text-left px-3 uppercase font-semibold"}>{loc("Events")}</h2>
        <ul className={"grid grid-cols-12 gap-2"}>
            <ConditionalOnUserRole role={UserRoles.ADMIN}>
                <li className={"col-span-12 md:col-span-6 lg:col-span-4 xl:col-span-3 " +
                    "bg-gray-200 flex justify-center content-center"}>
                    <button className={"h-full w-full text-xl text-primary-600 p-3"}
                            onClick={createEvent}
                    >
                        + {loc("Event")}
                    </button>
                </li>
            </ConditionalOnUserRole>
            <Conditional on={!events || events.length === 0}>
                <div className={"col-span-12"}>
                    <span>No events</span>
                </div>
            </Conditional>
            {events.map(event => {
                return <li key={event.id}
                           className={`col-span-12 md:col-span-6 lg:col-span-4 xl:col-span-3 
                                       flex content-center`}>
                    <EventPane event={event}/>
                </li>
            })}
        </ul>
    </div>;
}

export default EventsList