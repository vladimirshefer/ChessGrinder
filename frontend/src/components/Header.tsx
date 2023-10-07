import {Link} from "react-router-dom";
import {useAuthData} from "lib/auth/AuthService";
import React, {useRef, useState} from "react";
import loc from "strings/loc";
import {useClickOutsideHandler} from "lib/util/ClickOutside";
import Gravatar, {GravatarType} from "components/Gravatar";
import {AiOutlineClose, AiOutlineMenu} from "react-icons/ai";

function Header() {
    let authData = useAuthData()
    const droprownRef = useRef(null);
    let [dropdownOpened, setDropdownOpened] = useState(false);

    useClickOutsideHandler(droprownRef, () => setDropdownOpened(false));

    function NavLink(
        {
            to,
            text,
        }: {
            to: string,
            text: string
        }
    ) {
        return <li className={"p-2"}>
            <Link to={to}
                  className={"p-2"}
            >
                <button className={"uppercase"}>{text}</button>
            </Link>
        </li>
    }

    return <div>
        <div className={"w-full flex justify-between content-center items-center bg-white text-black border-b-2 border-black p-2"}>
            <Link className={"font-bold h-10 flex items-center"} to={"/"}><h1 className={"text-lg"}>Chess Grinder</h1></Link>
            <div className={"flex"}>

                <div className={"rounded-full overflow-hidden h-8 w-8 bg-white mx-2 border border-black"}>
                    {
                        !authData ? (
                            <Link to={"/login"}>
                                <Gravatar text={""} type={GravatarType.MysteryPerson} size={50}/>
                                loc("Login")
                            </Link>
                        ) : (
                            <Link to={"user"}>
                                <Gravatar text={authData!!.username} type={GravatarType.Robohash} size={50}/>
                            </Link>
                        )
                    }
                </div>
                <div className={"relative flex items-center px-1"}>
                    <button className={"font-bold text-[1.2rem]"}
                            onClick={() => setDropdownOpened(!dropdownOpened)}
                    >
                        {dropdownOpened ?<AiOutlineClose/>:<AiOutlineMenu/>}
                    </button>
                </div>
            </div>
        </div>
        <ul ref={droprownRef} className={`bg-white shadow w-full absolute z-25 py-5
                                    ${dropdownOpened ? "" : "hidden"}`}
            onClick={() => setDropdownOpened(false)}
        >
            <NavLink to={"/badges"} text={loc("Badges")}/>
            <NavLink to={"/admin"} text={loc("Admin")}/>
        </ul>
    </div>
}

export default Header;
