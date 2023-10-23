import React, {useContext} from "react";
import {MemberDto} from "lib/api/dto/MainPageData";
import {Property} from "lib/util/misc";

export const AuthenticatedUserContext = React.createContext<Property<MemberDto | null>>([null, () => {
}])

export function useAuthenticatedUser(): MemberDto | null {
    let [user]: [(MemberDto | null), ((v: (MemberDto | null)) => void)] = useContext(AuthenticatedUserContext)
    return user
}
