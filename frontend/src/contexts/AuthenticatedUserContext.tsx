import React, {useContext, useEffect, useState} from "react";
import {UserDto} from "lib/api/dto/MainPageData";
import {Property} from "lib/util/misc";
import userRepository from "../lib/api/repository/UserRepository";
import authService, {AuthData} from "../lib/auth/AuthService";

export const AuthenticatedUserContext = React.createContext<Property<UserDto | null>>([null, () => {
}])

export function useAuthenticatedUser(): UserDto | null {
    let [user]: [(UserDto | null), ((v: (UserDto | null)) => void)] = useContext(AuthenticatedUserContext)
    return user
}

export function AuthenticatedUserContextProvider({children}: { children: any }) {
    const [user, setUser] = useState<UserDto | null>(null);

    useEffect(() => {
        checkAuthData()
    }, [])

    async function checkAuthData() {
        let me = await userRepository.getMe();
        if (!me) {
            authService.setAuthData(null);
            setUser(null);
        } else {
            setUser(me)
            let authData: AuthData = {
                username: me!!.username,
                roles: me.roles,
                accessToken: "",
            };
            authService.setAuthData(authData)
        }
    }

    return <AuthenticatedUserContext.Provider value={[user, setUser]}>
        {children}
    </AuthenticatedUserContext.Provider>

}
