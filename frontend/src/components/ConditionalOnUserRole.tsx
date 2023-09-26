import {ReactElement, useMemo} from "react";
import {useAuthData} from "../lib/auth/AuthService";

export default function ConditionalOnUserRole(
    {
        role,
        children,
    }: {
        role: string,
        children: ReactElement | ReactElement[]
    }
) {
    let authData = useAuthData();
    let show = useMemo(() => {
        return authData?.roles?.includes(role)
    }, [authData, role])

    if (show) {
        return <>{children}</>
    } else return <>{[] as ReactElement []}</>
}
