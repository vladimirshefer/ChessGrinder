import {ReactElement, useMemo} from "react";
import {useMode} from "lib/api/repository/apiSettings";
import {useAuthenticatedUser} from "../contexts/AuthenticatedUserContext";

export function Conditional(
    {
        on,
        children,
    }: {
        on: boolean | any,
        children?: ReactElement | ReactElement[] | (() => ReactElement) | (() => ReactElement[])
    }
) {
    if (!!on) {
        if (typeof children === 'function') {
            return <>{children() || null}</>
        }
        return <>{children || null}</>
    } else return <>{[] as ReactElement []}</>
}

export default function ConditionalOnUserRole(
    {
        role,
        children,
    }: {
        role: string,
        children?: ReactElement | ReactElement[]
    }
) {
    let authenticatedUser = useAuthenticatedUser();
    let show = useMemo(() => {
        return authenticatedUser?.roles?.includes(role) || false
    }, [authenticatedUser, role])

    return <Conditional on={show}>
        {children}
    </Conditional>
}

export function ConditionalOnAuthorized(
    {
        children,
        authorized = true,
    } : {
        children?: ReactElement | ReactElement[],
        authorized?: boolean
    }
) {
    let authenticatedUser = useAuthenticatedUser();
    let show = useMemo(() => {
        return !!authenticatedUser === authorized
    }, [authenticatedUser, authorized])

    return <Conditional on={show}>
        {children}
    </Conditional>
}

export function ConditionalOnMode(
    {
        mode,
        children
    } : {
        mode: string,
        children?: ReactElement | ReactElement[]
    }
) {
    let [currentMode] = useMode()

    return <Conditional on={currentMode === mode}>
        {children}
    </Conditional>
}
