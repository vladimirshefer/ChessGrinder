import {ReactElement, useMemo, useRef, useState} from "react";
import {useClickOutsideHandler} from "../lib/util/ClickOutside";

export default function DropdownSelect<T>(
    {
        values,
        presenter,
        onSelect,
        keyExtractor,
        emptyPresenter = () => <button>Select</button>,
        search = true,
        matchesSearch = () => true,
    }: {
        values: T[],
        presenter: (value: T) => ReactElement,
        onSelect: (value: T | undefined) => void,
        keyExtractor: (value: T) => any,
        emptyPresenter?: () => ReactElement,
        search?: boolean
        matchesSearch?: (searchQuery: string, value: T) => boolean,
    }
) {
    let [selectedValue, setSelectedValue] = useState<T>()
    let [dropdownActive, setDropdownActive] = useState(false)
    let [searchQuery, setSearchQuery] = useState("")
    let dropdownRef = useRef(null);
    useClickOutsideHandler(dropdownRef, () => setDropdownActive(false))

    const filteredValues = useMemo(
        () => {
            if (!searchQuery) return values;
            return values.filter(v => matchesSearch(searchQuery, v));
        },
        [values, searchQuery, matchesSearch]
    );

    return <div ref={dropdownRef}>
        <div
            onClick={() => setDropdownActive(!dropdownActive)}
        >
            {selectedValue ?
                presenter(selectedValue)
                :
                emptyPresenter()
            }
        </div>
        {dropdownActive &&
            <div className={"grid absolute t-[100%]"}>
                <input type={"text"}
                       placeholder={"Filter values"}
                       autoFocus={true}
                       onChange={(e) => setSearchQuery(e.target.value)}
                />
                {
                    filteredValues.map(value =>
                        <div
                            key={keyExtractor(value)}
                            onClick={() => {
                                setSelectedValue(value);
                                setDropdownActive(false)
                                setSearchQuery("");
                                onSelect(value);
                            }}>
                            {presenter(value)}
                        </div>
                    )
                }
                <div className={"bg-white p-2 border"}>
                    <button onClick={() => {
                        setSelectedValue(undefined);
                        setDropdownActive(false)
                    }}>
                        Clear
                    </button>
                </div>
            </div>
        }
    </div>

}
