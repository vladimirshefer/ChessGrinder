import axios from "axios";
import {GLOBAL_SETTINGS} from "lib/api/repository/apiSettings";
import authService from "lib/auth/AuthService";

let apiPathPrefix = "/api"

let restAxios = axios
    .create({
        baseURL: GLOBAL_SETTINGS.restApiHost,
        // timeout: 1000,
        // headers: {'X-Custom-Header': 'foobar'}
    })

restAxios.interceptors
    .response
    .use(response => {
        return response;
    }, error => {
        if (!error?.response || error.response.status === 401) {
            authService.clearAuthData();
        }
        throw error;
    });

class RestApiClient {

    async post<T>(
        path: string,
        body: any = undefined,
        pathPrefix: string = apiPathPrefix
    ) {
        let axiosResponse = await restAxios.post(
            apiPathPrefix + path,
            body
        );
        return axiosResponse.data as T;
    }

    async put<T>(
        path: string,
        body: any = undefined,
        pathPrefix: string = apiPathPrefix
    ) {
        let axiosResponse = await restAxios.put(
            apiPathPrefix + path,
            body
        );
        return axiosResponse.data as T;
    }

    async get<T>(
        path: string,
        pathPrefix: string = apiPathPrefix
    ) {
        let axiosResponse = await restAxios.get(
            apiPathPrefix + path
        ).catch();
        return axiosResponse.data as T;
    }

    async delete<T>(
        path: string,
        pathPrefix: string = apiPathPrefix
    ) {
        let axiosResponse = await restAxios.delete(
            apiPathPrefix + path
        );
        return axiosResponse.data as T;
    }
}

let restApiClient = new RestApiClient()

export default restApiClient
