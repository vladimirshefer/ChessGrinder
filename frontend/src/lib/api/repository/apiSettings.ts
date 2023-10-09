import {useEffect, useState} from "react";

export class ListenableProperty<T> {
    constructor(get: () => T, set: (value: T) => void) {
        this.get = get;
        this.set = (value: T) => {
            this.listeners.forEach(listener => listener(value))
            set(value);
        }
    }

    public readonly get: () => T;
    public readonly set: (value: T) => void;
    private listeners: ((value: T) => void)[] = [];

    public onChange(listener: (value: T) => void): (() => void) {
        this.listeners.push(listener)
        return () => {
            this.listeners = this.listeners.filter(it => it !== listener)
        };
    }

    // public static of<T>([get, set]: [() => T, (value: T) => T]): ListenableProperty<T> {
    //     return new ListenableProperty<T>(get,  set)
    // }

}

export function useProperty<T>(mode: ListenableProperty<T>): [T, (value: T) => void] {
    let [authData, setValue] = useState<T>(mode.get());

    useEffect(() => {
        return mode.onChange((value) => setValue(value))
    }, [mode])

    return [authData, mode.set]
}

export function useMode(): [string, (v: string) => void] {
    return useProperty(GLOBAL_SETTINGS.profile)
}

export let GLOBAL_SETTINGS = {
    profile: new ListenableProperty<string>(() => localStorage.getItem("cgd.profile") || "production", (value) => {
        !!value ?
            localStorage.setItem("cgd.profile", value)
            : localStorage.removeItem("cgd.profile")
    }),
    restApiHost: "",
}

export function qualifiedService<T>(services: { [key: string /*profile name [local/production]*/]: T }): T {
    return services[GLOBAL_SETTINGS.profile.get()];
}

export function qualifiedServiceProxy<T extends object>(services: {
    [key: string /*profile name [local/production]*/]: T
}): T {
    return new Proxy<T>({} as unknown as T, {
        get(target: T, p: string | symbol, receiver: any): any {
            return (services[GLOBAL_SETTINGS.profile.get()] as any)[p]
        }
    })
}
