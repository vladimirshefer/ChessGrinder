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
        return authData?.roles?.includes(role) || false
    }, [authData, role])

    return <Conditional on={show}>
        {children}
    </Conditional>
}

export function ConditionalOnAuthorized(
    {
        children
    } : {
        children: ReactElement | ReactElement[]
    }
) {
    let authData = useAuthData();
    let show = useMemo(() => {
        return !!authData
    }, [authData])

    return <Conditional on={show}>
        {children}
    </Conditional>
}

export function Conditional(
    {
        on,
        children,
    }: {
        on: boolean,
        children: ReactElement | ReactElement[]
    }
) {
    if (on) {
        return <>{children}</>
    } else return <>{[] as ReactElement []}</>
}
