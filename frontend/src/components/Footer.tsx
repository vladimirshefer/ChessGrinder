import {Link} from "react-router-dom";
import React from "react";

function Footer() {
    return (
        <footer>
            <div className={"w-full text-sm text-gray-800"}>
                <Link className={"underline"} to={'/privacyPolicy'}>Privacy Policy</Link>
            </div>
        </footer>
    );
}

export default Footer;
