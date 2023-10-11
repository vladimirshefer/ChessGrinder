import locale_default from "./locale_default";
import locale_ru from "./locale_ru";
import {useContext} from "react";
import {LanguageContext} from "../App";

let locales: { [lang: string]: { [word: string]: string } } = {
    "ru": locale_ru,
    "en": locale_default,
    "default": locale_default,
}

export function useLoc() {
    let [language] = useContext(LanguageContext);

    return (key: string): string => {
        return locales?.[language]?.[key] || locales?.["default"]?.[key] || key;
    }
}
