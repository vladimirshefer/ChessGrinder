import {GLOBAL_SETTINGS} from "lib/pageRepository/apiSettings";
import authService from "lib/auth/AuthService";
import mainPageRepository from "./MainPageRepository";

export interface LoginPageRepository {
    login(username: string, password: string) : Promise<string>
}

class LocalStorageLoginPageRepository implements LoginPageRepository {
    async login(username: string, password: string): Promise<string> {
        let memberDto = (await mainPageRepository.getData()).members.find(it => it.id === username);
        if (!memberDto) {
            await mainPageRepository.createMember({
                id: username,
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
}

interface UserProfile {
    id: string
    name: string
    username: string
}

class RestApiLoginPageRepository implements LoginPageRepository {
    login(login: string, password: string): Promise<string> {
        return Promise.resolve("");
    }

}

let loginPageRepository: LoginPageRepository = GLOBAL_SETTINGS.getProfile() === "local"
    ? new LocalStorageLoginPageRepository()
    : new RestApiLoginPageRepository();

export default loginPageRepository
