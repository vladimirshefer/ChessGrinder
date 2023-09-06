import {Link} from "react-router-dom";

function Header () {
    return <>
        <div className={"w-full flex justify-between bg-blue-200 p-2"}>
            <Link className={""} to={"/"}><button>Home</button></Link>
            <h1 className={""}>Chess Grinder</h1>
            <div>
                <Link to={"/admin"}><button>Admin</button></Link>
            </div>
        </div>
    </>
}

export default Header;
