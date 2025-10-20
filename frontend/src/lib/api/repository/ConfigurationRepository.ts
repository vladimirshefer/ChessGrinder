import localStorageUtil from "lib/util/LocalStorageUtil";
import {qualifiedService} from "lib/api/repository/apiSettings";
import restApiClient from "lib/api/RestApiClient";

export interface ConfigurationRepository {
    getConfiguration(): Promise<{[key: string]: string}>
}

class LocalStorageConfigurationRepository implements ConfigurationRepository {
    private keyPrefix = "cgd.configuration";

    async getConfiguration(): Promise<{[key: string]: string}> {
        return localStorageUtil.getObject<{[key: string]: string}>(`${this.keyPrefix}}`) || {}
    }
}

class RestApiConfigurationRepository implements ConfigurationRepository {
    async getConfiguration(): Promise<{[key: string]: string}> {
        try {
            return await restApiClient.get(`/configuration`);
        } catch {
            return {}
        }
    }
}

let configurationRepository = qualifiedService({
    local: new LocalStorageConfigurationRepository(),
    production: new RestApiConfigurationRepository(),
})

export default configurationRepository;
