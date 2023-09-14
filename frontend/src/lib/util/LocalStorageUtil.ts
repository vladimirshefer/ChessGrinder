class LocalStorageUtil {
    getObject<T>(key: string): T | undefined {
        let value = localStorage.getItem(key);
        if (value === undefined || value === null) return undefined
        else return JSON.parse(value);
    }

    setObject<T>(key: string, value: T) {
        localStorage.setItem(key, JSON.stringify(value))
    }

    getAllObjectsByPrefix<T>(keyPrefix: string): T[] {
        let result = []
        for (let localStorageKey in localStorage) {
            if (localStorageKey.startsWith(`${keyPrefix}`)) {
                result.push(JSON.parse(localStorage.getItem(localStorageKey)!!));
            }
        }
        return result;
    }
}

let localStorageUtil = new LocalStorageUtil();
export default localStorageUtil;
