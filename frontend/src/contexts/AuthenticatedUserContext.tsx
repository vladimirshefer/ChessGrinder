import React, {useContext, useEffect, useState} from "react";
import {UserDto} from "lib/api/dto/MainPageData";
import userRepository from "lib/api/repository/UserRepository";
import authService, {AuthData} from "lib/auth/AuthService";

export const AuthenticatedUserContext = React.createContext<
    [(UserDto | null), (v: (UserDto | null)) => void, () => void]
>([null, () => {}, () => {}])

export function useAuthenticatedUser(): [UserDto | null, () => void] {
    let [user, ,refresh]: [(UserDto | null), (v: (UserDto | null)) => void, () => void]
        = useContext(AuthenticatedUserContext)
    return [user, refresh]
}

export function AuthenticatedUserContextProvider({children}: { children: any }) {
    const [user, setUser] = useState<UserDto | null>(null);

    useEffect(() => {
        checkAuthData().catch(console.error)
    }, [])

    async function checkAuthData() {
        let me = await userRepository.getMe()
            .catch(() => null);
        if (!me) {
            authService.setAuthData(null);
            setUser(null);
        } else {
            setUser(me)
            let authData: AuthData = {
                username: me.username,
                roles: me.roles,
                accessToken: "",
            };
            authService.setAuthData(authData)
        }
    }

    return <AuthenticatedUserContext.Provider value={[user, setUser, checkAuthData]}>
        {children}
    </AuthenticatedUserContext.Provider>

}
