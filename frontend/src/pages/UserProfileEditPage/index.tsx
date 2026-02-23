import {UserAvatarImg} from "components/Gravatar";
import userRepository from "lib/api/repository/UserRepository";
import React, {useEffect} from "react";
import {Link, useNavigate} from "react-router-dom";
import {useForm} from "react-hook-form";
import {useLoc} from "strings/loc";
import {useAuthenticatedUser} from "contexts/AuthenticatedUserContext";
import {UserDto} from "lib/api/dto/MainPageData";
import loginPageRepository from "lib/api/repository/LoginPageRepository";
import {FiExternalLink} from "react-icons/fi";
import {MdInfoOutline} from "react-icons/md";

export default function UserProfileEditPage() {
    let [authenticatedUser, refresh] = useAuthenticatedUser();
    let navigate = useNavigate()
    let loc = useLoc()

    useEffect(() => {
        if (!authenticatedUser) {
            navigate("/login")
        }
    }, [authenticatedUser, navigate]);

    const currentUserId: string = authenticatedUser!.id;

    const {register, handleSubmit} = useForm();
    if (!authenticatedUser) {
        return <>You are not logged in</>
    }

    //data - данные из html-формы
    async function saveUserData(data: { [key: string]: string}) {
        let userPageData = {
            name : data.fullName?.trim() || undefined,
        } as UserDto;

        const desiredUsertagRaw = data.usertag?.trim();
        const currentUsertag = authenticatedUser?.usertag || "";
        if (!!desiredUsertagRaw && desiredUsertagRaw !== currentUsertag) {
            userPageData.usertag = desiredUsertagRaw;
        }
        try {
            await userRepository.updateUser(currentUserId, userPageData);
            //If method above won't throw exception, program will go further
            const newUsertag = userPageData.usertag || currentUsertag;
            if (newUsertag) {
                navigate(`/user/${newUsertag}`);
            } else {
                navigate(`/user/${currentUserId}`);
            }
            if (authenticatedUser) {
                refresh();
            }
        }
        catch (e: any) {
            const message: string = e?.response?.data?.message || "Can not update user profile";
            alert(loc(message));
        }
    }

    const handleDeleteProfile = async () => {
        let userNameOrId = authenticatedUser?.name || authenticatedUser?.username || "";
        let expectedConfirmation = loc("I confirm the deletion of my profile") + " " + userNameOrId;
        const userConfirmation = window.prompt(loc("Enter") + " \"" + expectedConfirmation
            + "\"\n" + loc("Deletion is final and cannot be undone") + ".");

        if (userConfirmation?.trim() !== expectedConfirmation.trim()) {
            alert(loc("You entered wrong text. Profile won't be deleted"));
            return;
        }

        try {
            await userRepository.deleteUser(authenticatedUser?.id);
            navigate("/");
            await loginPageRepository.signOut();
            refresh();
        }
        catch {
            alert("Can not delete user")
        }
    };

    return <div className={"p-3"}>
        <div className="flex py-2">
            <h1 className={"font-semibold uppercase"}>{loc("Settings")}</h1>
        </div>
        <form className={"grid gap-2"} onSubmit={handleSubmit(saveUserData)}>
            <div className={"grid gap-1"}>
                <UserAvatarImg emailHash={authenticatedUser.emailHash} size={100} className={"rounded-full"}/>
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
                <input type={"text"} className={"text-sm border-b-2"}
                       defaultValue={authenticatedUser.usertag}
                       title={loc("Username")}
                       placeholder={loc("Username")}
                       {...register("usertag")}
                />
                <p className={"text-xs text-left text-gray-600"}>
                    {loc("Choose a public username for your profile link (letters and digits, must start with a letter).")}
                </p>
                <div className="p-2"></div>
                <div className={"hidden"}>
                    <h3 className={"text-sm uppercase font-semibold"}>{loc("Change password")}</h3>
                    <div className={"grid gap-2 p-2 border"}>
                        <div className={"grid"}>
                            <input type={"password"} className={"font-semibold truncate border-b-2"}
                                   title={loc("Old password")}
                                   placeholder={loc("Old password")}
                                   {...register("oldPassword")}
                            />
                            <p className={"text-sm text-gray-500 flex items-center gap-1"}>
                                <MdInfoOutline/>{"If you have no password set, you can leave this field empty."}
                            </p>
                        </div>
                        <input type={"password"} className={"font-semibold truncate border-b-2"}
                               title={loc("New password")}
                               placeholder={loc("New password")}
                               {...register("newPassword")}
                        />
                        <input type={"password"} className={"font-semibold truncate border-b-2"}
                               title={loc("Confirm new password")}
                               placeholder={loc("Confirm new password")}
                               {...register("newPasswordConfirm")}
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
                        <Link to={authenticatedUser.usertag ? `/user/${authenticatedUser.usertag}` : `/user/${currentUserId}`}>
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
