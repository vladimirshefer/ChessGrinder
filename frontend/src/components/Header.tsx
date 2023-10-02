import {Link} from "react-router-dom";
import {useAuthData} from "lib/auth/AuthService";
import {useState} from "react";
import loc from "strings/loc";

function Header() {
    let authData = useAuthData()

    let [dropdownOpened, setDropdownOpened] = useState(false);

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

    return <>
        <div className={"w-full flex justify-between bg-blue-200 p-2"}>
            <Link className={"font-bold"} to={"/"}><h1 className={""}>Chess Grinder</h1></Link>
            <div>
                <div className={"relative"}>
                    <button className={"font-bold"}
                            onClick={() => setDropdownOpened(!dropdownOpened)}
                    >
                        Menu
                    </button>
                    <ul className={`bg-gray-100 absolute rounded-md z-25
                                    ${dropdownOpened ? "" : "hidden"} right-0`}
                        onClick={() => setDropdownOpened(false)}
                    >
                        {
                            !authData ? (
                                <NavLink to={"/login"} text={loc("Login")}/>
                            ) : (
                                <NavLink to={"user"} text={authData.username}/>
                            )
                        }
                        <NavLink to={"/admin"} text={loc("Admin")}/>
                    </ul>
                </div>
            </div>
        </div>
    </>
}

export default Header;
