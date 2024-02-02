import {qualifiedService} from "lib/api/repository/apiSettings";
import authService from "lib/auth/AuthService";
import restApiClient from "lib/api/RestApiClient";
import userRepository from "lib/api/repository/UserRepository";
import {UserRoles} from "lib/api/dto/MainPageData";
import localStorageUtil from "lib/util/LocalStorageUtil";
import {UserSignUpRequest} from "../dto";

export interface LoginPageRepository {
    signIn(username: string, password: string): Promise<string>

    signOut(): Promise<void>

    signUp(data: UserSignUpRequest): Promise<void>
}

class LocalStorageLoginPageRepository implements LoginPageRepository {
    async signIn(username: string, password: string): Promise<string> {
        let roles = username.toLowerCase().includes("admin") ? [UserRoles.ADMIN] : [];
        let memberDto = (await userRepository.getUsers()).values.find(it => it.id === username);
        if (!memberDto) {
            await localStorageUtil.setObject(`cgd.user.${username}`, {
                id: username,
                username: username,
                name: username,
                badges: [],
                roles
            })
        }
        authService.setAuthData({
            username: username,
            accessToken: username,
            roles: memberDto?.roles || roles
        })
        window.location.reload()
        return username
    }

    async signOut(): Promise<void> {
        authService.clearAuthData()
        window.location.reload()
    }

    async signUp(data: UserSignUpRequest): Promise<void> {
        return Promise.reject();
    }
}

class RestApiLoginPageRepository implements LoginPageRepository {
    async signIn(username: string, password: string): Promise<string> {
        await restApiClient.post("/login", `username=${username}&password=${password}`) // toto urlencode parameters
        return username;
    }

    async signOut(): Promise<void> {
        await restApiClient.get("/logout");
    }


    async signUp(data: UserSignUpRequest): Promise<void> {
        await restApiClient.post("/user/signUp", data)
    }
}

let loginPageRepository: LoginPageRepository = qualifiedService({
    local: new LocalStorageLoginPageRepository(),
    production: new RestApiLoginPageRepository(),
})

export default loginPageRepository
