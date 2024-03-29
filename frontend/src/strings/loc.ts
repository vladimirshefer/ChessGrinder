import locale_default from "./locale_default";
import locale_ru from "./locale_ru";
import {useContext} from "react";
import {LanguageContext} from "contexts/LanguageContext";

let locales: { [lang: string]: { [word: string]: string } } = {
    "ru": locale_ru,
    "en": locale_default,
    "default": locale_default,
}

/**
 * React hook;
 * Returns localization function;
 *
 */
export function useLoc(): (key: string) => string {
    let [language] = useContext(LanguageContext);

    return (key: string): string => {
        return locales?.[language]?.[key] || locales?.["default"]?.[key] || key;
    }
}
