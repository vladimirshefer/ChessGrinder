export let GLOBAL_SETTINGS = {
    getProfile() {
        return localStorage.getItem("cgd.profile") || "local";
    },
    setProfile(value: string | null) {
        !!value ?
            localStorage.setItem("cgd.profile", value)
            : localStorage.removeItem("cgd.profile")
    },
    restApiHost: "http://localhost:8080",
}

export function qualifiedService<T>(services: {[key: string /*profile name [local/production]*/]: T}): T {
   return services[GLOBAL_SETTINGS.getProfile()];
}

export function qualifiedServiceProxy<T extends object>(services: {[key: string /*profile name [local/production]*/]: T}): T {
    return new Proxy<T>({} as unknown as T, {
        get(target: T, p: string | symbol, receiver: any): any {
            return (services[GLOBAL_SETTINGS.getProfile()] as any)[p]
        }
    })
}
