import {Link} from "react-router-dom";

function Header () {
    return <>
        <div>
            <Link to={"/"}><button>Home</button></Link>
            Header Of Chess Grinder
            <button>+ Tournament</button>
        </div>
    </>
}

export default Header;
