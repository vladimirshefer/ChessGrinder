import React, {useState} from "react";
import {Property} from "lib/util/misc";

export const LanguageContext = React.createContext<Property<string>>(["en", (l: string) => {
}]);

export function LanguageContextProvider({children}: { children: any }) {
    const languageContextValue = useState(localStorage.getItem("cgd.language"));

    return <LanguageContext.Provider value={[languageContextValue[0] || "ru", (val) => {
        languageContextValue[1](val);
        localStorage.setItem("cgd.language", val)
    }]}>
        {children}
    </LanguageContext.Provider>
}
