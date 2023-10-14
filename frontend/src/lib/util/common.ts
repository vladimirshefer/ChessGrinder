export function requirePresent<T>(value: T | undefined | null, message?: string): T {
    if (!value) throw new Error(`No value present: ${message}`)
    return value
}
