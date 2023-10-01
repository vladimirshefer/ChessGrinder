import locale_default from "./locale_default";
import locale_ru from "./locale_ru";

let selectedLocale = locale_ru;
let defaultLocale = locale_default

/**
 * Localization function.
 * Used for site text.
 * Translates text to desired language.
 * @param key
 */
export default function loc(key: string): string {
    return selectedLocale[key] || defaultLocale[key] || key
}
