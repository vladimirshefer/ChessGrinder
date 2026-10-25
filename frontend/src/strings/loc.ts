import locale_default from "strings/locale_default";
import locale_ru from "strings/locale_ru";
import locale_de from "strings/locale_de";
import {useContext} from "react";
import {LanguageContext} from "contexts/LanguageContext";

let locales: { [lang: string]: { [word: string]: string } } = {
    "ru": locale_ru,
    "de": locale_de,
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

    const table = language === "de" ? translit_ru_de : translit_to_en;

    return (key: string): string => {
        if (!key) return "";
        let result = '';
        let translitedChar;
        for (let i = 0; i < key.length; i++) {
            const char = key[i];
            translitedChar = table[char];
            result += (translitedChar === undefined) ? char : translitedChar;
        }

        return result;
    }
}

const translit_ru_en: { [key: string]: string } = {
    'А': 'A', 'а': 'a', 'Б': 'B', 'б': 'b', 'В': 'V', 'в': 'v',
    'Г': 'G', 'г': 'g', 'Д': 'D', 'д': 'd', 'Е': 'E', 'е': 'e',
    'Ё': 'Yo', 'ё': 'yo', 'Ж': 'Zh', 'ж': 'zh', 'З': 'Z', 'з': 'z',
    'И': 'I', 'и': 'i', 'Й': 'J', 'й': 'j', 'К': 'K', 'к': 'k',
    'Л': 'L', 'л': 'l', 'М': 'M', 'м': 'm', 'Н': 'N', 'н': 'n',
    'О': 'O', 'о': 'o', 'П': 'P', 'п': 'p', 'Р': 'R', 'р': 'r',
    'С': 'S', 'с': 's', 'Т': 'T', 'т': 't', 'У': 'U', 'у': 'u',
    'Ф': 'F', 'ф': 'f', 'Х': 'H', 'х': 'h', 'Ц': 'C', 'ц': 'c',
    'Ч': 'Ch', 'ч': 'ch', 'Ш': 'Sh', 'ш': 'sh', 'Щ': 'Sch', 'щ': 'sch',
    'Ъ': '', 'ъ': '', 'Ы': 'Y', 'ы': 'y', 'Ь': '', 'ь': '',
    'Э': 'E', 'э': 'e', 'Ю': 'Yu', 'ю': 'yu', 'Я': 'Ya', 'я': 'ya',
}

/** German letters to English (ASCII) spelling, e.g. umlauts: "Müller" -> "Mueller". */
const translit_de_en: { [key: string]: string } = {
    'Ä': 'Ae', 'ä': 'ae', 'Ö': 'Oe', 'ö': 'oe', 'Ü': 'Ue', 'ü': 'ue', 'ß': 'ss', 'ẞ': 'SS',
}

/** Table for non-RU, non-DE languages: Cyrillic and German letters to English. */
const translit_to_en: { [key: string]: string } = {
    ...translit_ru_en,
    ...translit_de_en,
}

/** German (Duden-like) romanization of Russian letters: differences from the English table. */
const translit_ru_de: { [key: string]: string } = {
    ...translit_ru_en,
    'В': 'W', 'в': 'w', 'Ё': 'Jo', 'ё': 'jo', 'Ж': 'Sch', 'ж': 'sch',
    'З': 'S', 'з': 's', 'Х': 'Ch', 'х': 'ch',
    'Ц': 'Z', 'ц': 'z', 'Ч': 'Tsch', 'ч': 'tsch', 'Ш': 'Sch', 'ш': 'sch',
    'Щ': 'Schtsch', 'щ': 'schtsch', 'Ю': 'Ju', 'ю': 'ju', 'Я': 'Ja', 'я': 'ja',
}

