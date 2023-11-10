export type Property<T> = [T, (v: T) => void]

export function propagate(handler: (e: any) => void) {
    return (e: any) => {
        handler(e);
        return Promise.reject(e);
    }
}
