import React from "react";

function MainPage() {
    return <>
        Hello, World!

        <div>
            <h2>Members</h2>
            <table>
                <tr>
                    <th>Name</th>
                    <th>Badges</th>
                </tr>
                <tr>
                    <td>Alexander Boldyrev</td>
                    <td>💥 🎃</td>
                </tr>
                <tr>
                    <td>Vladimir Shefer</td>
                    <td>🍒 🎯</td>
                </tr>
            </table>
        </div>

        <div>
            <h2>Tournaments</h2>
            <ul>
                <li><a href={"/tournament/Tournament 1"}>Tournament 1</a></li>
                <li>Tournament 2</li>
                <li>Tournament 3</li>
            </ul>
        </div>

    </>
}

export default MainPage
