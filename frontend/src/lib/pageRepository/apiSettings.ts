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
