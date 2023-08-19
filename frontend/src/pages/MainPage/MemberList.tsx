export function MemberList(
    {
        members
    }: {
        members: {
            name: string,
            badges: {
                imageUrl: string,
                description: string
            }[],
        }[]
    }
) {
    return <div>
        <h2>Members</h2>
        <table>
            <thead>
            <tr>
                <th>Name</th>
                <th>Badges</th>
            </tr>
            </thead>
            <tbody>
            {members.map(member => {
                return <tr key={member.name}>
                    <td>{member.name}</td>
                    <td>
                        {
                            member.badges.map(badge => {
                                return <span key={badge.imageUrl} title={badge.description}>{badge.imageUrl}</span>
                            })
                        }
                    </td>
                </tr>
            })}
            </tbody>

        </table>
    </div>;
}

export default MemberList
