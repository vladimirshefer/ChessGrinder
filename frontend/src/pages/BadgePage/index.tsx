import {useQuery} from "@tanstack/react-query";
import {useNavigate, useParams} from "react-router-dom";
import badgeRepository from "lib/api/repository/BadgeRepository";
import ConditionalOnUserRole from "components/Conditional";
import {UserRoles} from "lib/api/dto/MainPageData";
import {UserPane} from "pages/MainPage/UserPane";
import {BadgeIcon} from "components/Gravatar";

export default function BadgePage() {
    let {badgeId} = useParams()
    let navigate = useNavigate();

    let badgeQuery = useQuery({
        queryKey: ["badge", badgeId],
        queryFn: async () => {
            return badgeRepository.getBadge(badgeId!!)
        },
    })

    let usersQuery = useQuery({
        queryKey: ["badgeUsers", badgeId],
        queryFn: async () => {
            return await badgeRepository.getUsers(badgeId!!)
        }
    })

    if (!badgeId) return <>No badge selected.</>
    if (badgeQuery.isError) return <>Badge not found. {badgeQuery.error}</>
    if (badgeQuery.isLoading) return <>Loading...</>
    if (!badgeQuery.isSuccess)  return <>Not successful</>

    let badge = badgeQuery.data!!;

    async function deleteBadge() {
        let confirmation = prompt(`Are you sure you wand to delete badge? Enter name '${badge.title}' to confirm delete.`);
        if (confirmation !== badge.title) return;
        await badgeRepository.deleteBadge(badge.id);
        navigate("/badges")
    }

    return <div className={"p-3"}>
        <div className={"text-left py-3 uppercase font-semibold"}>
            <span>
                Badge page
            </span>
        </div>
        <div className={"flex gap-3"}>
            <div className={"min-w-[100px]"}>
                <BadgeIcon title={badge.title} size={100}/>
            </div>
            <div className={"grid text-left"}>
                <div>
                    <h1 className={"text-xl font-semibold"}>
                        {badge.title}
                    </h1>
                    <span className={"text-sm text-gray-500"}>
                        {badge.description}
                    </span>
                </div>
            </div>
        </div>
        <ConditionalOnUserRole role={UserRoles.ADMIN}>
            <div className={"text-right"}>
                <button className={"btn-danger"}
                        onClick={() => deleteBadge()}
                >
                    Delete
                </button>
            </div>
        </ConditionalOnUserRole>
        <div className={"text-left py-3 uppercase font-semibold"}>
            <span>Users</span>
        </div>
        <div className={"grid"}>
            {
                usersQuery.isSuccess && usersQuery.data!!.values.map(it => <UserPane user={it}/>)
            }
        </div>
    </div>
}
