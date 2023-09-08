import {Link} from "react-router-dom";

function Header () {
    return <>
        <div className={"w-full flex justify-between bg-blue-200 p-2"}>
            <Link className={""} to={"/"}><h1 className={""}>Chess Grinder</h1></Link>
            <div>
                <Link to={"/login"}
                    className={"px-1"}
                ><button>Login</button></Link>
                <Link to={"/admin"}
                      className={"px-1"}
                ><button>Admin</button></Link>
            </div>
        </div>
    </>
}

export default Header;
