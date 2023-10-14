import Gravatar, {GravatarType} from "components/Gravatar";
import {useQuery} from "@tanstack/react-query";
import {useParams} from "react-router-dom";
import badgeRepository from "lib/api/repository/BadgeRepository";

export default function BadgePage() {
    let {badgeId} = useParams()

    let badgeQuery = useQuery({
        queryKey: ["badge", badgeId],
        queryFn: async () => {
            return badgeRepository.getBadge(badgeId!!)
        },
    })

    if (!badgeId) return <>No badge selected.</>
    if (badgeQuery.isError) return <>Badge not found. {badgeQuery.error}</>
    if (badgeQuery.isLoading) return <>Loading...</>
    if (!badgeQuery.isSuccess)  return <>Not successful</>

    let badge = badgeQuery.data!!;

    return <div className={"p-3"}>
        <div className={"text-left py-3 uppercase font-semibold"}>
            <span>
                Badge page
            </span>
        </div>
        <div className={"flex gap-3"}>
            <div className={""}>
                <Gravatar
                    text={badge.title}
                    type={GravatarType.Identicon}
                    size={150}
                    className={"rounded-full"}
                />
            </div>
            <div className={"grid text-left"}>
                <div>
                    <h1 className={"text-xl font-semibold"}>
                        {badge.title}
                    </h1>
                    <span>
                        {badge.description}
                    </span>
                </div>
            </div>
        </div>
        <div className={"text-left py-3 uppercase font-semibold"}>
            <span>Users</span>
        </div>
        <div className={"grid"}>
            <div className={"flex gap-2"}>
                <div>
                    <Gravatar
                        text={"345"}
                        type={GravatarType.Robohash}
                        size={50}
                        className={"rounded-full"}
                    />
                </div>
                <div className={"grid"}>
                    <div>
                        <span>Vladimir Shefer</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
}
