import {Link} from "react-router-dom";
import {useAuthData} from "lib/auth/AuthService";
import React, {useRef, useState} from "react";
import loc from "strings/loc";
import {polynomialHashcodeHex} from "lib/util/Hashcode";
import {useClickOutsideHandler} from "lib/util/ClickOutside";

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
                <button className={"underline"}>{text}</button>
            </Link>
        </li>
    }

    return <div>
        <div className={"w-full flex justify-between content-center items-center bg-blue-200 p-2"}>
            <Link className={"font-bold h-10 flex items-center"} to={"/"}><h1 className={"text-lg"}>Chess Grinder</h1></Link>
            <div className={"flex"}>

                <div className={"rounded-full overflow-hidden h-8 w-8 bg-white mx-2 border border-black"}>
                {
                    !authData ? (
                            <Link to={"/login"}>
                                <img
                                    // See https://gravatar.com/site/implement/images/
                                    src={`https://www.gravatar.com/avatar/?s=50&r=pg&d=mp`}
                                    alt={"User avatar"}/>loc("Login")
                            </Link>
                    ) : (
                        <Link to={"user"}>
                            <img
                                // See https://gravatar.com/site/implement/images/
                                src={`https://gravatar.com/avatar/${polynomialHashcodeHex(authData!!.username)}?d=robohash&s=50`}
                                alt={"User avatar"}/>
                        </Link>
                    )
                }
                </div>
                <div className={"relative flex items-center"}>
                    <button className={"font-bold"}
                            onClick={() => setDropdownOpened(!dropdownOpened)}
                    >
                        Menu
                    </button>
                </div>
            </div>
        </div>
        <ul ref={droprownRef} className={`bg-blue-100 w-full absolute z-25
                                    ${dropdownOpened ? "" : "hidden"}`}
            onClick={() => setDropdownOpened(false)}
        >
            <NavLink to={"/badges"} text={loc("Badges")}/>
            <NavLink to={"/admin"} text={loc("Admin")}/>
        </ul>
    </div>
}

export default Header;
