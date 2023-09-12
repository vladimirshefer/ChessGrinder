import localStorageUtil from "../util/LocalStorageUtil";

export interface AuthData {
    username: string
    accessToken: string
}

class AuthService {
    setAuthData(authData: AuthData) {
        localStorageUtil.setObject("cgd.auth", authData)
    }

    isLoggedIn(): boolean {
        return !!this.getAuthData()
    }

    getAuthData(): AuthData | undefined {
        return localStorageUtil.getObject<AuthData>("cgd.auth")
    }

    getCurrentUserId(): string | undefined {
        return this.getAuthData()?.username
    }
}

let authService = new AuthService()
export default authService
