import React from "react";
import {useQuery} from "@tanstack/react-query";
import {useLoc} from "strings/loc";
import useSearchParam from "lib/react/hooks/useSearchParam";
import userRepository from "lib/api/repository/UserRepository";
import MemberList from "pages/MainPage/MemberList";
import {FaSortAmountDown} from "react-icons/fa";
import {IoLocationSharp} from "react-icons/io5";
import {FaArrowLeft, FaArrowRight} from "react-icons/fa6";

export default function UsersPage() {
    let loc = useLoc();
    const [sort, setSort] = useSearchParam("sort");
    const [city, setCity] = useSearchParam("city");
    const [pageParam, setPageParam] = useSearchParam("page");

    const page = Number(pageParam || 0) || 0;
    const pageSize = 50;

    let usersQuery = useQuery({
        queryKey: ["members", city, sort, page],
        queryFn: async () => {
            try {
                return await userRepository.getUsers(pageSize, page, city ?? undefined, (sort as any)?.toUpperCase() ?? "RATING");
            }
            catch (error: any) {
                alert(loc(error.response.data.message));
                return null;
            }
        },
    })

    let users = usersQuery.data?.values;

    if (!users) {
        return <>Loading...</>
    }
    return <div className={"p-2 grid gap-2"}>
        <div className={"flex gap-3 items-center"}>
            <h2 className={"text-xl my-2 uppercase text-left font-semibold grow"}>{loc("Members")}</h2>
            <div className={"flex gap-1 items-center border-b"} title={"Filter by city"}>
                <span><IoLocationSharp/></span>
                <select defaultValue={""} className={"bg-white"} onChange={e => setCity(e.target.value)} value={city || ""}>
                    <option value={""}>—</option>
                    <option value={"Berlin"}>{loc("Berlin")}</option>
                    <option value={"Limassol"}>{loc("Limassol")}</option>
                    <option value={"Tbilisi"}>{loc("Tbilisi")}</option>
                </select>
            </div>
            <div className={"flex gap-1 items-center border-b"} title={"Sort by"}>
                <span><FaSortAmountDown /></span>
                <select defaultValue={"rating"} className={"bg-white"} onChange={e => setSort(e.target.value)} value={sort || "rating"}>
                    <option value={"rating"}>{loc("Rating")}</option>
                    <option value={"reputation"}>{loc("Reputation")}</option>
                </select>
            </div>
        </div>
        <MemberList users={users}/>
        <div className={"flex gap-2 items-center justify-center mt-2"}>
            <button
                className={"px-3 py-1 disabled:opacity-50"}
                disabled={page <= 0}
                onClick={() => setPageParam(String(Math.max(0, page - 1)))}
            ><FaArrowLeft /></button>
            <span>{loc("Page")} {page + 1}</span>
            <button
                className={"px-3 py-1 disabled:opacity-50"}
                disabled={(users?.length || 0) < pageSize}
                onClick={() => setPageParam(String(page + 1))}
            ><FaArrowRight /></button>
        </div>
    </div>
}
