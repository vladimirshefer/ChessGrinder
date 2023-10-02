import {Link} from "react-router-dom";
import {useAuthData} from "lib/auth/AuthService";
import {useState} from "react";
import loc from "strings/loc";

function Header() {
    let authData = useAuthData()

    let [dropdownOpened, setDropdownOpened] = useState(false);

    return <>
        <div className={"w-full flex justify-between bg-blue-200 p-2"}>
            <Link className={"font-bold"} to={"/"}><h1 className={""}>Chess Grinder</h1></Link>
            <div>
                <div className={"relative"}>
                    <button className={"font-bold"}
                        onClick={() => {
                            setDropdownOpened(!dropdownOpened)
                        }}
                    >
                        Menu
                    </button>
                    <ul className={`bg-gray-100 absolute rounded-md z-25
                                    ${dropdownOpened ? "" : "hidden"} right-0`}
                        onClick={() => setDropdownOpened(false)}
                    >
                        <li className={"p-2"}>
                            {
                                !authData ? (
                                    <Link to={"/login"}
                                          className={"p-2"}
                                    >
                                        <button className={"underline"}>{loc("Login")}</button>
                                    </Link>
                                ) : (
                                    <Link to={"/user"}
                                          className={"p-2"}
                                    >
                                        <button className={"underline"}>{authData.username}</button>
                                    </Link>
                                )
                            }
                        </li>
                        <li className={"p-1"}>
                            <Link to={"/admin"}
                                  className={"p-2"}
                            >
                                <button className={"underline"}>Admin</button>
                            </Link>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </>
}

export default Header;
