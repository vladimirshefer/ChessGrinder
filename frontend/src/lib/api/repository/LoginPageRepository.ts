import {qualifiedService} from "lib/api/repository/apiSettings";
import authService from "lib/auth/AuthService";
import mainPageRepository from "./MainPageRepository";
import restApiClient from "lib/api/RestApiClient";

export interface LoginPageRepository {
    login(username: string, password: string) : Promise<string>

    logout(): Promise<void>
}

class LocalStorageLoginPageRepository implements LoginPageRepository {
    async login(username: string, password: string): Promise<string> {
        let memberDto = (await mainPageRepository.getData()).members.find(it => it.id === username);
        if (!memberDto) {
            await mainPageRepository.createMember({
                id: username,
                username: username,
                name: username,
                badges: []
            })
        }
        authService.setAuthData({
            username: username,
            accessToken: username,
        })
        return username
    }

    async logout(): Promise<void> {
        authService.clearAuthData()
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
