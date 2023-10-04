import {useQuery} from "@tanstack/react-query";
import badgeRepository from "lib/api/repository/BadgeRepository";
import ConditionalOnUserRole, {Conditional} from "components/Conditional";
import {useForm} from "react-hook-form";
import {UserRoles} from "lib/api/dto/MainPageData";

export default function BadgesPage() {
    const { register, handleSubmit } = useForm();

    let badgesQuery = useQuery({
        queryKey: ["badges"],
        queryFn: async () => {
            return badgeRepository.getBadges()
        }
    })

    async function createBadge(data: any){
        await badgeRepository.createBadge({
            description: data.description,
            title: data.title,
            imageUrl: data.imageUrl,
        })
        await badgesQuery.refetch()
    }

    return <>
        Badges page
        {
            <Conditional on={!badgesQuery.isSuccess || !(badgesQuery.data?.values?.length !== 0 || false)}>
                <div>No badges</div>
            </Conditional>
        }
        <div>
            {
                badgesQuery.data?.values?.map(badge => {
                    return <div title={badge.description}>
                        <span>{badge.imageUrl}</span>
                        <span>{badge.title}</span>
                    </div>
                })
            }
        </div>
        <ConditionalOnUserRole role={UserRoles.ADMIN}>
            <form className={"grid p-4"}
                onSubmit={handleSubmit(createBadge)}
            >
                <h3 className={"font-bold"}>Create badge</h3>
                <input type={"text"} placeholder={"Image Url"} defaultValue={"⭐"} {...register("imageUrl")}/>
                <input type={"text"} placeholder={"Title"}  {...register("title")}/>
                <input type={"text"} placeholder={"Description"}  {...register("description")}/>
                <button
                    type={"submit"}
                    className={"btn bg-primary"}
                >
                    Create badge
                </button>
            </form>
        </ConditionalOnUserRole>
    </>
}
