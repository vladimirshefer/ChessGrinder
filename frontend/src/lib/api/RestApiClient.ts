import axios, {AxiosRequestConfig} from "axios";
import {GLOBAL_SETTINGS} from "lib/api/repository/apiSettings";

class RestApiClient {
    private axiosRequestConfig = {
        headers: {
            "Authorization": "Basic dm92YTpzaGVmZXI="
        }
    } as AxiosRequestConfig;

    async post<T>(path: string, body: any = undefined) {
        let axiosResponse = await axios.post(
            GLOBAL_SETTINGS.restApiHost + path,
            body,
            this.axiosRequestConfig
        );
        return axiosResponse.data as T;
    }

    async get<T>(path: string) {
        let axiosResponse = await axios.get(
            GLOBAL_SETTINGS.restApiHost + path,
            this.axiosRequestConfig
        );
        return axiosResponse.data as T;
    }

    async delete<T>(path: string) {
        let axiosResponse = await axios.delete(
            GLOBAL_SETTINGS.restApiHost + path,
            this.axiosRequestConfig
        );
        return axiosResponse.data as T;
    }
}

let restApiClient = new RestApiClient()

export default restApiClient
