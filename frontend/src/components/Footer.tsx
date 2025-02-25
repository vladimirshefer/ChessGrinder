import {Link} from "react-router-dom";
import React from "react";
import {useConfigurationProperty} from "contexts/ConfigurationContext";

function Footer() {
    let [buildTime] = useConfigurationProperty("build.time", undefined)

    return (
        <footer className={"text-left text-xs text-gray-500 w-full border-t border-black p-2"}>
            <ul>
                <li>
                    <span>{"ChessGrinder © Vladimir Shefer, 2023-2025."}</span>
                </li>
                <li>
                    <span>{"Build " + buildTime}</span>
                </li>
                <li>
                    <Link className={"underline hover:text-gray-800"} to={'/privacyPolicy'}>Privacy Policy</Link>
                </li>
                <li>
                    <span className={""}>
                        {"This site is protected by reCAPTCHA and the Google "}
                        <a className={"underline hover:text-gray-800"} href="https://policies.google.com/privacy">Privacy Policy</a>
                        {" and "}
                        <a className={"underline hover:text-gray-800"} href="https://policies.google.com/terms">Terms of Service</a>
                        {" apply."}
                    </span>
                </li>
            </ul>
        </footer>
    );
}

export default Footer;
