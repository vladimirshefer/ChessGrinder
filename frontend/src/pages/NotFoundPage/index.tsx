import {useLoc} from "strings/loc";
import {Link} from "react-router-dom";

export default function NotFoundPage() {
    let loc = useLoc();
    return <div className={"grid w-full p-5 gap-3 justify-center"}>
        <h1 className={"font-bold"}>Oops! Not found!</h1>
        <Link className={"underline"} to={"/"}>{loc("Main Page")}</Link>
        <img src={"/not_found.png"} alt={"Page not found"}/>
    </div>
}
