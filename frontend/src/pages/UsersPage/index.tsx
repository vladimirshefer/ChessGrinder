import React, {useMemo} from "react";
import {useQuery} from "@tanstack/react-query";
import {useLoc} from "strings/loc";
import useSearchParam from "lib/react/hooks/useSearchParam";
import userRepository from "lib/api/repository/UserRepository";
import MemberList from "pages/MainPage/MemberList";
import {FaSortAmountDown} from "react-icons/fa";
import {IoLocationSharp} from "react-icons/io5";

export default function UsersPage() {
    let loc = useLoc();
    const [sort, setSort] = useSearchParam("sort");
    const [city, setCity] = useSearchParam("city");

    let usersQuery = useQuery({
        queryKey: ["members", city],
        queryFn: async () => {
            try {
                return await userRepository.getUsers(undefined, city ?? undefined);
            }
            catch (error: any) {
                alert(loc(error.response.data.message));
                return null;
            }
        },
    })

    let users = useMemo(() => {
        let list = usersQuery.data?.values;

        if (sort === "reputation") {
            list = list?.sort((a, b) => (b.reputation || 0) - (a.reputation || 0));
        }
        if (sort === "rating") {
            list = list?.sort((a, b) => (b.eloPoints || 0) - (a.eloPoints || 0));
        }
        return list;
    }, [usersQuery.data, sort]);

    if (!users) {
        return <>Loading...</>
    }
    return <div className={"p-2 grid gap-2"}>
        <div className={"flex gap-3 items-center"}>
            <h2 className={"text-xl my-2 uppercase text-left font-semibold grow"}>{loc("Members")}</h2>
            <div className={"flex gap-1 items-center border-b"} title={"Filter by city"}>
                <span><IoLocationSharp/></span>
                <select defaultValue={""} className={"bg-white"} onChange={e => setCity(e.target.value)} value={city || ""}>
                    <option value={""}>Any</option>
                    <option value={"Berlin"}>Berlin</option>
                    <option value={"Limassol"}>Limassol</option>
                    <option value={"Tbilisi"}>Tbilisi</option>
                </select>
            </div>
            <div className={"flex gap-1 items-center border-b"} title={"Sort by"}>
                <span><FaSortAmountDown /></span>
                <select defaultValue={"rating"} className={"bg-white"} onChange={e => setSort(e.target.value)} value={sort || "rating"}>
                    <option value={"rating"}>Rating</option>
                    <option value={"reputation"}>Reputation</option>
                </select>
            </div>
        </div>
        <MemberList users={users}/>
    </div>
}
