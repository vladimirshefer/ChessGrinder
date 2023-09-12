class LocalStorageUtil {
    getObject<T>(key: string): T | undefined {
        let value = localStorage.getItem(key);
        if (value === undefined || value === null) return undefined
        else return JSON.parse(value);
    }

    setObject<T>(key: string, value: T) {
        localStorage.setItem(key, JSON.stringify(value))
    }
}

let localStorageUtil = new LocalStorageUtil();
export default localStorageUtil;
