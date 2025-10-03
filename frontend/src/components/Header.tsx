import {Link, useNavigate} from "react-router-dom";
import React, {useContext, useRef, useState} from "react";
import {useLoc} from "strings/loc";
import {useClickOutsideHandler} from "lib/util/ClickOutside";
import {EmptyUserAvatarImg, UserAvatarImg} from "components/Gravatar";
import {AiOutlineClose, AiOutlineMenu} from "react-icons/ai";
import ConditionalOnUserRole, {Conditional, ConditionalOnAuthorized} from "components/Conditional";
import loginPageRepository from "lib/api/repository/LoginPageRepository";
import {UserRoles} from "lib/api/dto/MainPageData";
import {useAuthenticatedUser} from "contexts/AuthenticatedUserContext";
import {LanguageContext} from "contexts/LanguageContext";
import useLoginPageLink from "lib/react/hooks/useLoginPageLink";

function Header() {
    let navigate = useNavigate()
    let [authenticatedUser, authenticatedUserReload] = useAuthenticatedUser()
    const droprownRef = useRef(null);
    let [dropdownOpened, setDropdownOpened] = useState(false);
    let [, setLanguage] = useContext(LanguageContext)
    let loc = useLoc()

    let loginPageLink = useLoginPageLink();

    useClickOutsideHandler(droprownRef, () => setDropdownOpened(false));

    function NavLink(
        {
            to,
            onClick = () => {
            },
            text,
            isAdminOnly = false,
        }: {
            to?: string,
            onClick?: () => void,
            text: string,
            isAdminOnly?: boolean,
        }
    ) {
        let button = <button onClick={() => onClick()} className={`uppercase ${isAdminOnly ? "text-danger-800" : ""}`}>{text}</button>;
        return <li className={"p-2"}>
            <Conditional on={!!to}>
                <Link to={to!!}>{button}</Link>
            </Conditional>
            <Conditional on={!to}>
                {button}
            </Conditional>
        </li>
    }

    return <div>
        <div
            className={"w-full flex justify-between content-center items-center bg-white text-black border-b-2 border-black p-2"}>
            <Link className={"font-bold h-10 flex items-center"} to={"/"}>
                <h1 className={"text-lg"}>Chess Grinder</h1>
            </Link>
            <div className={"flex"}>

                <div className={"rounded-full overflow-hidden h-8 w-8 bg-white mx-2 border border-black"}>
                    {
                        !authenticatedUser ? (
                            <Link to={loginPageLink}>
                                <EmptyUserAvatarImg size={50}/>
                            </Link>
                        ) : (
                            <Link to={"/user"}>
                                <UserAvatarImg emailHash={authenticatedUser.emailHash} size={50}/>
                            </Link>
                        )
                    }
                </div>
                <div className={"relative flex items-center px-1"}>
                    <button className={"font-bold text-[1.2rem]"}
                            onClick={() => setDropdownOpened(!dropdownOpened)}
                    >
                        {dropdownOpened ? <AiOutlineClose/> : <AiOutlineMenu/>}
                    </button>
                </div>
            </div>
        </div>
        <ul ref={droprownRef} className={`bg-white shadow w-full absolute z-50 py-5
                                    ${dropdownOpened ? "" : "hidden"}`}
            onClick={() => setDropdownOpened(false)}
        >
            <NavLink to={"/clock"} text={loc("Clock")}/>
            <NavLink to={"/users"} text={loc("Users")}/>
            <ConditionalOnAuthorized>
                <NavLink onClick={async () => {
                    await loginPageRepository.signOut()
                    authenticatedUserReload()
                }} text={loc("Logout")}/>
            </ConditionalOnAuthorized>
            <ConditionalOnAuthorized authorized={false}>
                <NavLink onClick={() => navigate(loginPageLink)} text={loc("Login")}/>
            </ConditionalOnAuthorized>
            <li className={"flex gap-2 justify-center p-2"}>
                <button onClick={() => setLanguage("ru")}>RU</button>
                <button onClick={() => setLanguage("en")}>EN</button>
            </li>
            <ConditionalOnUserRole role={UserRoles.ADMIN}>
                <NavLink to={"/badges"} text={loc("Badges")} isAdminOnly={true}/>
            </ConditionalOnUserRole>
            <ConditionalOnUserRole role={UserRoles.ADMIN}>
                <NavLink to={"/admin"} text={loc("Admin")} isAdminOnly={true}/>
            </ConditionalOnUserRole>
        </ul>
    </div>
}

export default Header;
