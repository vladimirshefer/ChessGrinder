export type Comparator<T> = (v1: T, v2: T) => number;

export function compareBy<T, R>(keyExtractor: (t: T) => R): Comparator<T> {
    return (v1: T, v2: T) => {
        let key1 = keyExtractor(v1);
        let key2 = keyExtractor(v2);
        if (key1 === key2) return 0;
        else if (key1 > key2) return 1;
        else if (key1 < key2) return -1;
        else return 0;
    }
}

export function reverse<T>(comparator: Comparator<T>): Comparator<T> {
    return (v1: T, v2: T) => {
        return -comparator(v1, v2)
    }
}
