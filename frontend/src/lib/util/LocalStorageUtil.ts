class LocalStorageUtil {

    /**
     * Reads string value from localStorage by key and parses it into object.
     * If specified key does not exist in localStorage, then returns null.
     * @param key
     */
    getObject<T>(key: string): T | null {
        let value: string | null= localStorage.getItem(key);
        if (value === null) return null;
        else return JSON.parse(value);
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

    /**
     * Iterates over localStorage, selecting values,
     * which stored by key, wtarting with specified prefix.
     * @param keyPrefix
     */
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
