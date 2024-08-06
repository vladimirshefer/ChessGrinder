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

export function useTransliterate(): (key: string) => string {
    let [language] = useContext(LanguageContext);

    if (language === "ru") {
        return (key: string): string => key
    }

    return (key: string): string => {
        let result = '';

        for (let i = 0; i < key.length; i++) {
            const char = key[i];
            result += translit_ru_en[char] || char;
        }

        return result;
    }
}

const translit_ru_en: { [key: string]: string } = {
    'А': 'A', 'а': 'a', 'Б': 'B', 'б': 'b', 'В': 'V', 'в': 'v',
    'Г': 'G', 'г': 'g', 'Д': 'D', 'д': 'd', 'Е': 'E', 'е': 'e',
    'Ё': 'YO', 'ё': 'yo', 'Ж': 'ZH', 'ж': 'zh', 'З': 'Z', 'з': 'z',
    'И': 'I', 'и': 'i', 'Й': 'J', 'й': 'j', 'К': 'K', 'к': 'k',
    'Л': 'L', 'л': 'l', 'М': 'M', 'м': 'm', 'Н': 'N', 'н': 'n',
    'О': 'O', 'о': 'o', 'П': 'P', 'п': 'p', 'Р': 'R', 'р': 'r',
    'С': 'S', 'с': 's', 'Т': 'T', 'т': 't', 'У': 'U', 'у': 'u',
    'Ф': 'F', 'ф': 'f', 'Х': 'H', 'х': 'h', 'Ц': 'C', 'ц': 'c',
    'Ч': 'CH', 'ч': 'ch', 'Ш': 'SH', 'ш': 'sh', 'Щ': 'SCH', 'щ': 'sch',
    'Ъ': '', 'ъ': '', 'Ы': 'Y', 'ы': 'y', 'Ь': '', 'ь': '',
    'Э': 'E', 'э': 'e', 'Ю': 'YU', 'ю': 'yu', 'Я': 'YA', 'я': 'ya',
}

