import localStorageUtil from "../util/LocalStorageUtil";
import {useEffect, useState} from "react";

export interface AuthData {
    username: string
    roles?: string[]
    accessToken: string
}

class AuthService {
    private authDataListeners: ((authData: AuthData | undefined) => void)[] = []

    setAuthData(authData: AuthData | null | undefined) {
        if (!!authData) {
            localStorageUtil.setObject("cgd.auth", authData)
        } else {
            localStorage.removeItem("cgd.auth")
        }
        this.authDataListeners.forEach(listener => listener(authData || undefined))
    }

    getAuthData(): AuthData | undefined {
        return localStorageUtil.getObject<AuthData>("cgd.auth") || undefined
    }

    clearAuthData() {
        this.setAuthData(null)
    }

    /**
     * Adds listener for authData state. Returns the function to delete this listener.
     * Always call returned callback to avoid memory leaks.
     */
    onAuthDataChanged(authDataListener: (authData: AuthData | undefined) => void): () => void {
        this.authDataListeners.push(authDataListener)
        return () => {
            this.authDataListeners = this.authDataListeners.filter(it => it !== authDataListener)
        };
    }

}

let authService = new AuthService()

export default authService

export function useAuthData(): AuthData | undefined {
    let [authData, setAuthData] = useState<AuthData | undefined>(authService.getAuthData());

    useEffect(() => {
        return authService.onAuthDataChanged((updatedAuthData) => setAuthData(updatedAuthData))
    }, [])

    return authData
}
