import React, {useContext, useEffect, useState} from "react";
import configurationRepository from "lib/api/repository/ConfigurationRepository";

export type ConfigurationType = { [key: string]: string }

export const ConfigurationContext = React.createContext<[ConfigurationType]>([{}])

export function useConfiguration(): [ConfigurationType] {
    let [configuration]: [ConfigurationType] = useContext(ConfigurationContext)
    return [configuration]
}

export function useConfigurationProperty(key: string, defaultValue: string | undefined = undefined): [string | undefined] {
    let [configuration] = useConfiguration()
    return [configuration[key] || defaultValue]
}

export function useConfigurationPropertyEnabled(key: string, defaultValue: boolean): [boolean | undefined] {
    let [value] = useConfigurationProperty(key)
    return [(value === "true") || defaultValue]
}

export function ConfigurationContextProvider({children}: { children: any }) {
    const [configuration, setConfiguration] = useState<ConfigurationType>({});

    useEffect(() => {
        loadConfiguration()
    }, [])

    async function loadConfiguration() {
        let configuration = await configurationRepository.getConfiguration()
            .catch(() => ({} as ConfigurationType));
        if (!configuration) {
            setConfiguration({})
        } else {
            setConfiguration(configuration)
        }
    }

    return <ConfigurationContext.Provider value={[configuration]}>
        {children}
    </ConfigurationContext.Provider>

}
