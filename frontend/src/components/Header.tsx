import {Link} from "react-router-dom";

function Header () {
    return <>
        <div className={"w-full flex justify-between bg-blue-200 p-2"}>
            <Link className={""} to={"/"}><button>Home</button></Link>
            <h1 className={""}>Header Of Chess Grinder</h1>
            <button className={"rounded-full bg-black text-blue-200 px-2"}>+ Tournament</button>
        </div>
    </>
}

export default Header;
