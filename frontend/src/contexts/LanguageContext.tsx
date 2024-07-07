import React, {useState} from "react";
import {Property} from "lib/util/misc";

export const LanguageContext = React.createContext<Property<string>>(["en", (l: string) => {
}]);

export function LanguageContextProvider({children}: { children: any }) {
    const languageContextValue = useState(localStorage.getItem("cgd.language"));
    let navigatorLanguage: string | undefined = navigator?.language?.substring(0, 2)?.toLowerCase();

    return <LanguageContext.Provider value={[languageContextValue[0] || navigatorLanguage || "en", (val) => {
        languageContextValue[1](val);
        localStorage.setItem("cgd.language", val)
    }]}>
        {children}
    </LanguageContext.Provider>
}
