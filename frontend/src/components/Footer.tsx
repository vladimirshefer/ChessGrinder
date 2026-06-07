import {Link} from "react-router-dom";
import React from "react";
import {useConfigurationProperty} from "contexts/ConfigurationContext";

function Footer() {
    let [buildTime] = useConfigurationProperty("build.time", undefined)
    let [buildVersion] = useConfigurationProperty("build.version", undefined)

    return (
        <footer className={"text-left grid md:grid-cols-2 xl:grid-cols-3 gap-4 text-xs text-gray-500 w-full border-t border-black p-2"}>
            <ul>
                <li>
                    <span>{"ChessGrinder © Vladimir Shefer, 2023-2026."}</span>
                </li>
                <li>
                    <span>{`Build ${buildVersion} (${buildTime})`}</span>
                </li>
            </ul>
            <ul>
                <li>
                    <Link className={"underline hover:text-gray-800"} to={'/privacyPolicy'}>Privacy Policy</Link>
                </li>
                <li>
                    <span className={""}>
                        {"This site is protected by reCAPTCHA and the Google "}
                        <a className={"underline hover:text-gray-800"} href="https://policies.google.com/privacy">Privacy Policy →</a>
                        {" and "}
                        <a className={"underline hover:text-gray-800"} href="https://policies.google.com/terms">Terms of Service →</a>
                        {" apply."}
                    </span>
                </li>
            </ul>
            <ul>
                <li>
                    <Link className={"underline hover:text-gray-800"} to={'https://chessarium.com'} target={"_blank"} rel={"noreferrer noopener"}>Train through chess dungeons →</Link>
                </li>
            </ul>
            <ul>
                <li>
                    <Link className={"underline hover:text-gray-800"} to={'https://t.me/chess_grinder_de'} target={"_blank"} rel={"noreferrer noopener"}>Telegram Group - Germany →</Link>
                    <Link className={"underline hover:text-gray-800"} to={'https://t.me/chess_grinder_cy'} target={"_blank"} rel={"noreferrer noopener"}>Telegram Chat - Cyprus →</Link>
                    <Link className={"underline hover:text-gray-800"} to={'https://t.me/chess_grinder_for_life'} target={"_blank"} rel={"noreferrer noopener"}>Telegram Group - Georgia →</Link>
                    <Link className={"underline hover:text-gray-800"} to={'https://instagrem.com/chess_grinder_tbilisi'} target={"_blank"} rel={"noreferrer noopener"}>Instagram - Georgia →</Link>
                    <Link className={"underline hover:text-gray-800"} to={'https://instagram.com/chessgrinder'} target={"_blank"} rel={"noreferrer noopener"}>Instagram - Global →</Link>
                </li>
            </ul>
        </footer>
    );
}

export default Footer;
