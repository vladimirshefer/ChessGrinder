import Gravatar, {GravatarType} from "components/Gravatar";
import userRepository from "lib/api/repository/UserRepository";
import React, {useEffect} from "react";
import {Link, useNavigate} from "react-router-dom";
import {useForm} from "react-hook-form";
import {useLoc} from "strings/loc";
import {useAuthenticatedUser} from "contexts/AuthenticatedUserContext";
import {UserDto} from "lib/api/dto/MainPageData";
import loginPageRepository from "lib/api/repository/LoginPageRepository";
import {FiExternalLink} from "react-icons/fi";

export default function UserProfileEditPage() {
    let [authenticatedUser, refresh] = useAuthenticatedUser();
    let navigate = useNavigate()
    let loc = useLoc()

    useEffect(() => {
        if (!authenticatedUser) {
            navigate("/login")
        }
    }, [authenticatedUser, navigate]);

    const currentUserId: string = authenticatedUser?.id!!;

    const {register, handleSubmit} = useForm();
    if (!authenticatedUser) {
        return <>You are not logged in</>
    }

    //data - данные из html-формы
    async function saveUserData(data: { [key: string]: string}) {
        let userPageData = {
            name : data.fullName
        } as UserDto;
        try {
            await userRepository.updateUser(currentUserId, userPageData);
            //If method above won't throw exception, program will go further
            navigate(`/user/${currentUserId}`);
            if (authenticatedUser) {
                refresh();
            }
        }
        catch(e) {
            alert(loc("Can not update user name"));
        }
    }

    const handleDeleteProfile = async () => {
        let userNameOrId = authenticatedUser?.name || authenticatedUser?.username || "";
        let expectedConfirmation = loc("I confirm the deletion of my profile") + " " + userNameOrId;
        const userConfirmation = prompt(loc("Enter") + " \"" + expectedConfirmation
            + "\"\n" + loc("Deletion is final and cannot be undone") + ".");

        if (userConfirmation?.trim() !== expectedConfirmation.trim()) {
            alert(loc("You entered wrong text. Profile won't be deleted"));
            return;
        }

        try {
            await userRepository.deleteUser(authenticatedUser?.id!!);
            await navigate("/");
            await loginPageRepository.signOut();
            await refresh();
        }
        catch(e) {
            alert("Can not delete user")
        }
    };

    return <div className={"p-3"}>
        <div className="flex py-2">
            <h1 className={"font-semibold uppercase"}>{loc("Settings")}</h1>
        </div>
        <form className={"grid gap-2"} onSubmit={handleSubmit(saveUserData)}>
            <div className={"grid gap-1"}>
                <Gravatar text={authenticatedUser.emailHash} type={GravatarType.Robohash} size={100}
                          className={"rounded-full"} inputType={"MD5"}/>
                <div className={"text-left btn btn-light btn-sm text-sm"}>
                    <Link to={"https://gravatar.com/profile/avatars"} target={"_blank"}>
                        <div className={"flex gap-1 items-center"}>
                            <span>Change avatar</span> <FiExternalLink/>
                        </div>
                    </Link>
                </div>
            </div>
            <div className={"grid grow text-left gap-2"}>
                <input type={"text"} className={"font-semibold truncate border-b-2"}
                       defaultValue={authenticatedUser.name || authenticatedUser.username || authenticatedUser.id || "Unknown"}
                       title={loc("Full name")}
                       placeholder={loc("Full name")}
                       {...register("fullName")}
                />
                <input type={"text"} disabled={true} className={"text-sm text-gray-500 border-b-2"}
                       defaultValue={authenticatedUser.username}
                       title={loc("Username")}
                       placeholder={loc("Username")}
                       {...register("userName")}
                />
                <div className="p-2"></div>
                <div className={"hidden"}>
                    <h3 className={"text-sm uppercase font-semibold"}>{loc("Change password")}</h3>
                    <div className={"grid gap-2 p-2 border"}>
                        <input type={"password"} className={"font-semibold uppercase truncate border-b-2"}
                               defaultValue={"*********"}
                               title={loc("Old password")}
                               placeholder={loc("Old password")}
                        />
                        <input type={"password"} className={"font-semibold uppercase truncate border-b-2"}
                               title={loc("New password")}
                               placeholder={loc("New password")}
                        />
                        <input type={"password"} className={"font-semibold uppercase truncate border-b-2"}
                               title={loc("Confirm new password")}
                               placeholder={loc("Confirm new password")}
                        />
                    </div>
                </div>
            </div>
            <div className={"grid gap-2"}>
                <div className={"grid gap-2"}>
                    <div className="flex gap-2 justify-end">
                        <button type={"submit"} className="btn-primary uppercase">
                            {loc("Save")}
                        </button>
                        <Link to={`/user/${currentUserId}`}>
                            <button className="btn-light uppercase">{loc("Cancel")}</button>
                        </Link>
                    </div>
                    <div className={"flex justify-end"}>
                        <button type="button" className="btn-danger uppercase" onClick={handleDeleteProfile}>
                            {loc("Delete profile")}
                        </button>
                    </div>
                </div>
            </div>
        </form>
    </div>
}
