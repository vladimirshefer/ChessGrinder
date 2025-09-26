import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import restApiClient from "lib/api/RestApiClient";
import { Link } from "react-router-dom";
import { LuArrowRight, LuChevronDown } from "react-icons/lu";

function NicknameContextPane({ tournamentId }: { tournamentId: string }) {
    const [isCollapsed, setIsCollapsed] = useState(true)

    const pollQuery = useQuery({
        queryKey: ["poll", tournamentId],
        queryFn: async () => restApiClient.get<string>(`/nickname-contest/${tournamentId}`),
        retry: false,
    })

    if (pollQuery.isLoading) return <Skeleton />
    if (pollQuery.isError || !pollQuery.data) return null

    return (
        <div className="grid gap-3 bg-gradient-to-br from-primary-400 via-primary-500 to-primary-600 p-5 text-white shadow-xl">
            <div className="flex flex-wrap items-center gap-3">
                <p className="text-sm font-semibold uppercase tracking-[0.2em] text-primary-50/80">Nickname Contest</p>
                <Link
                    to={pollQuery.data}
                    target="_blank"
                    className="group ml-auto inline-flex items-center gap-2 bg-white/15 px-5 py-2 text-sm font-semibold text-white transition hover:bg-white hover:text-primary-600"
                >
                    Vote now
                    <LuArrowRight className="h-4 w-4 transition group-hover:translate-x-1 group-hover:text-primary-600" />
                </Link>
                <button
                    type="button"
                    aria-expanded={!isCollapsed}
                    aria-controls="nickname-contest-details"
                    onClick={() => setIsCollapsed((prev) => !prev)}
                    className="bg-white/10 p-2 text-white transition hover:bg-white/20 focus:outline-none focus:ring-1 focus:ring-white/60 focus:ring-offset-primary-500/40"
                >
                    <LuChevronDown className={`h-4 w-4 transform transition-transform ${isCollapsed ? "-rotate-90" : "rotate-0"}`} />
                </button>
            </div>

            {!isCollapsed && (<div className={"grid gap-2"}>
                <div>
                    <h2 className="text-xl font-bold">Voting is live!</h2>
                </div>
                <div id="nickname-contest-details" className="text-sm text-primary-50/90">
                    Help pick the most creative nickname for this tournament. Cast your vote before the tournament closes.
                </div>
            </div>)}
        </div>
    )
}

function Skeleton() {
    return <div className="h-15 animate-pulse bg-primary-300/40" />
}

export default NicknameContextPane
