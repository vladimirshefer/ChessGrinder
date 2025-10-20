class LocalStorageUtil {

    /**
     * Reads string value from localStorage by key and parses it into object.
     * If specified key does not exist in localStorage, then returns null.
     * @param key
     */
    getObject<T>(key: string): T | null {
        let value: string | null= localStorage.getItem(key);
        if (value === null) return null;
        else return JSON.parse(value) as T;
    }

    /**
     * Sets new value for specified key in localStorage.
     * If value already exists, then rewrites it.
     * @param key the key of localStorage.
     * @param value any object to be stored in localStorage.
     */
    setObject<T>(key: string, value: T) {
        localStorage.setItem(key, JSON.stringify(value))
    }

    removeObject(key: string) {
        localStorage.removeItem(key)
    }

    /**
     * Iterates over localStorage, selecting values,
     * which stored by key, wtarting with specified prefix.
     * @param keyPrefix
     */
    getAllObjectsByPrefix<T>(keyPrefix: string): T[] {
        let result = [] as T[]
        for (let localStorageKey in localStorage) {
            if (localStorageKey.startsWith(`${keyPrefix}`)) {
                result.push(JSON.parse(localStorage.getItem(localStorageKey)!));
            }
        }
        return result;
    }

    forEach<T>(keyPrefix: string, action: (id: string, value: T) => void) {
        for (let localStorageKey in localStorage) {
            if (localStorageKey.startsWith(`${keyPrefix}`)) {
                let value = JSON.parse(localStorage.getItem(localStorageKey)!) as T;
                action(localStorageKey.substring(keyPrefix.length), value)
            }
        }
    }
}

let localStorageUtil = new LocalStorageUtil();
export default localStorageUtil;
