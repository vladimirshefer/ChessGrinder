export function polynomialHashcodeHex(str: string) {
    return polynomialHashcode(str).toString(16)
}

function polynomialHashcode (str: string): number {
    let hash = 0;
    let i;
    let chr;
    if (str.length === 0) return hash;
    for (i = 0; i < str.length; i++) {
        chr = str.charCodeAt(i);
        hash = ((hash << 5) - hash) + chr;
        hash |= 0; // Convert to 32bit integer
    }
    return hash;
}
