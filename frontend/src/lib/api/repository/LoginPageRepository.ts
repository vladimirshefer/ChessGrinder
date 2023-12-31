import {qualifiedService} from "lib/api/repository/apiSettings";
import authService from "lib/auth/AuthService";
import restApiClient from "lib/api/RestApiClient";
import userRepository from "lib/api/repository/UserRepository";
import {UserRoles} from "lib/api/dto/MainPageData";
import localStorageUtil from "lib/util/LocalStorageUtil";

export interface LoginPageRepository {
    login(username: string, password: string): Promise<string>

    logout(): Promise<void>
}

class LocalStorageLoginPageRepository implements LoginPageRepository {
    async login(username: string, password: string): Promise<string> {
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

    async logout(): Promise<void> {
        authService.clearAuthData()
        window.location.reload()
    }
}

class RestApiLoginPageRepository implements LoginPageRepository {
    async login(login: string, password: string): Promise<string> {
        return "";
    }

    async logout(): Promise<void> {
        await restApiClient.get("/logout");
        window.location.reload();
    }

}

let loginPageRepository: LoginPageRepository = qualifiedService({
    local: new LocalStorageLoginPageRepository(),
    production: new RestApiLoginPageRepository(),
})

export default loginPageRepository
