import {Link} from "react-router-dom";
import {useAuthData} from "lib/auth/AuthService";

function Header() {
    let authData = useAuthData()

    return <>
        <div className={"w-full flex justify-between bg-blue-200 p-2"}>
            <Link className={""} to={"/"}><h1 className={""}>Chess Grinder</h1></Link>
            <div>
                {
                    !authData ? (
                        <Link to={"/login"}
                              className={"px-1"}
                        >
                            <button>Login</button>
                        </Link>
                    ) : (
                        <Link to={"/user"}
                              className={"px-1"}
                        >
                            <button>Profile</button>
                        </Link>
                    )
                }
                <Link to={"/admin"}
                      className={"px-1"}
                >
                    <button>Admin</button>
                </Link>
            </div>
        </div>
    </>
}

export default Header;
