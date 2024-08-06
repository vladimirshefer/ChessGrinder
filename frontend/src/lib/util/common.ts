export function requirePresent<T>(value: T | undefined | null, message?: string): T {
    if (!value) throw new Error(`No value present: ${message}`)
    return value
}

export function isNotEmptyArray<T>(array: T[] | undefined | null): boolean {
    if (array === undefined || array === null) {
        return false
    }
    return array.length > 0;
}
