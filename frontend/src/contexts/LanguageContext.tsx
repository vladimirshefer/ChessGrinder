import React, {useState} from "react";
import {Property} from "lib/util/misc";

export const LanguageContext = React.createContext<Property<string>>(["en", (l: string) => {
}]);

export function LanguageContextProvider({children}: { children: any }) {
    const languageContextValue = useState("ru");

    return <LanguageContext.Provider value={languageContextValue}>
        {children}
    </LanguageContext.Provider>
}
