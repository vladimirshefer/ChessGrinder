import {ReactElement, useMemo} from "react";
import {useMode} from "lib/api/repository/apiSettings";
import {useAuthenticatedUser} from "contexts/AuthenticatedUserContext";
import {useQuery} from "@tanstack/react-query";
import userRepository from "lib/api/repository/UserRepository";

export function Conditional(
    {
        on,
        children,
    }: {
        // eslint-disable-next-line @typescript-eslint/no-redundant-type-constituents
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
    let [authenticatedUser] = useAuthenticatedUser();
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
        children?: ReactElement | ReactElement[] | (() => ReactElement),
        authorized?: boolean
    }
) {
    let [authenticatedUser] = useAuthenticatedUser();
    let show = useMemo(() => {
        return !!authenticatedUser === authorized
    }, [authenticatedUser, authorized])

    return <Conditional on={show}>
        {
            typeof children === 'function' ?  children() : children
        }
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

export function usePermissionGranted(
    targetId: string,
    targetType: string,
    permission: string,
) {
    let [authenticatedUser] = useAuthenticatedUser();
    let permissionQuery = useQuery({
        queryKey: ["permission", authenticatedUser?.id, targetId, targetType, permission],
        queryFn: async () => {
            if (!authenticatedUser?.id) return false;
            return await userRepository.checkPermission(authenticatedUser.id, targetId, targetType, permission)
        }
    })

    return permissionQuery.data || false
}
