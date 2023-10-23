import React, {useContext, useEffect, useState} from "react";
import {MemberDto} from "lib/api/dto/MainPageData";
import {Property} from "lib/util/misc";
import userRepository from "../lib/api/repository/UserRepository";
import authService, {AuthData} from "../lib/auth/AuthService";

export const AuthenticatedUserContext = React.createContext<Property<MemberDto | null>>([null, () => {
}])

export function useAuthenticatedUser(): MemberDto | null {
    let [user]: [(MemberDto | null), ((v: (MemberDto | null)) => void)] = useContext(AuthenticatedUserContext)
    return user
}

export function AuthenticatedUserContextProvider({children}: { children: any }) {
    const [user, setUser] = useState<MemberDto | null>(null);

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
