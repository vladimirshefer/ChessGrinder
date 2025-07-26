import {TournamentEventScheduleDto} from "lib/api/dto/TournamentEventData";
import {useLoc} from "strings/loc";
import {Link} from "react-router-dom";
import {Conditional} from "components/Conditional";
import {AiFillClockCircle} from "react-icons/ai";
import {BsCalendarWeek} from "react-icons/bs";
import {IconTag} from "./TournamentPane";

export function SchedulePane(
    {
        schedule
    }: {
        schedule: TournamentEventScheduleDto
    }
) {
    let loc = useLoc();

    // Map day of week number to name
    const dayNames = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];
    const dayName = dayNames[schedule.dayOfWeek - 1] || "Unknown";
    
    // Format time (HH:mm)
    const time = schedule.time;

    // Determine CSS class based on status
    const statusClass = 
        schedule.status === "ACTIVE" ? "tournament-active" : 
        schedule.status === "PAUSED" ? "tournament-planned" : 
        "tournament-finished"; // ARCHIVED

    return <div className={`grid justify-items-start w-full p-4 ${statusClass}`}>
        <div className={"grid w-full justify-items-start"}>
            <Link className={"flex w-full gap-2 text-lg text-left justify-between items-center"}
                  to={`/tournament-event-schedule/${schedule.id}`}
            >
                <div className="flex gap-2 hover:underline font-semibold">
                    <span className={"grow"}>
                        {schedule.name || loc("Unnamed Schedule")}
                    </span>
                </div>
                <span className={`text-sm px-2 py-1 rounded-full ${
                    schedule.status === "ACTIVE" ? "bg-green-100 text-green-800" : 
                    schedule.status === "PAUSED" ? "bg-yellow-100 text-yellow-800" : 
                    "bg-gray-100 text-gray-800"
                }`}>
                    {schedule.status}
                </span>
            </Link>
        </div>
        <div className={"p-1"}></div>
        <div className={"py-3"}>
            <IconTag
                icon={<BsCalendarWeek className={"text-primary-400"}/>}
                text={dayName}
            />
            <IconTag
                icon={<AiFillClockCircle className={"text-primary-400"}/>}
                text={time}
            />
        </div>
        <div className={"p-1"}></div>
        <Conditional on={schedule.events && schedule.events.length > 0}>
            <div className="text-sm text-gray-600">
                {loc("Events")}: {schedule.events?.length || 0}
            </div>
        </Conditional>
        <Link className={"btn-primary w-full uppercase mt-2"}
              to={`/tournament-event-schedule/${schedule.id}`}
        >
            {loc("View")}
        </Link>
    </div>;
}

export default SchedulePane;