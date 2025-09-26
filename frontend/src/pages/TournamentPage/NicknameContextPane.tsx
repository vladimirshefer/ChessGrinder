import { useQuery } from "@tanstack/react-query";
import restApiClient from "lib/api/RestApiClient";
import { Link } from "react-router-dom";
// import { ArrowRight } from "lucide-react";
import {LuArrowRight} from "react-icons/lu";

function NicknameContextPane({ tournamentId }: { tournamentId: string }) {
    const pollQuery = useQuery({
        queryKey: ["poll", tournamentId],
        queryFn: async () => restApiClient.get<string>(`/nickname-contest/${tournamentId}`),
    });

    if (pollQuery.isLoading) return <Skeleton />
    if (pollQuery.isError || !pollQuery.data) return null

    return (
        <div className="bg-gradient-to-br from-primary-400 via-primary-500 to-primary-600 p-5 text-white shadow-xl">
            <div className="grid justify-items-center gap-3">
                    <p className="text-sm font-semibold uppercase tracking-[0.2em] text-primary-50/80">Nickname Contest</p>
                    <h2 className="text-xl font-bold">Voting is live!</h2>
                    <p className="max-w-md text-sm text-primary-50/90">
                        Help pick the most creative nickname for this tournament. Cast your vote before the tournament closes.
                    </p>
            </div>

            <Link
                to={pollQuery.data}
                className="group mt-5 inline-flex items-center gap-2 bg-white/15 px-5 py-2 text-sm font-semibold text-white transition hover:bg-white hover:text-primary-600"
            >
                Vote now
                <LuArrowRight className="h-4 w-4 transition group-hover:translate-x-1 group-hover:text-primary-600" />
            </Link>
        </div>
    )
}

function Skeleton() {
    return <div className="h-32 animate-pulse bg-primary-300/40" />
}

export default NicknameContextPane
