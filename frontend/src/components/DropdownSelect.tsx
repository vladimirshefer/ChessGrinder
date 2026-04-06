import {ReactElement, useMemo, useRef, useState} from "react";
import {useClickOutsideHandler} from "lib/util/ClickOutside";
import {BsTrash} from "react-icons/bs";
import {AiFillCaretDown, AiOutlineSearch} from "react-icons/ai";
import {useLoc} from "strings/loc";
import {RxCross2} from "react-icons/rx";

export default function DropdownSelect<T>(
    {
        values,
        className = "",
        presenter,
        onSelect,
        keyExtractor,
        emptyPresenter = () => <button>Select</button>,
        matchesSearch = () => true,
        searchQuery: controlledSearchQuery,
        onSearchQueryChange,
    }: {
        values: T[],
        className?: string
        presenter: (value: T) => ReactElement,
        onSelect: (value: T | undefined) => void,
        keyExtractor: (value: T) => any,
        emptyPresenter?: () => ReactElement,
        matchesSearch?: (searchQuery: string, value: T) => boolean,
        searchQuery?: string,
        onSearchQueryChange?: (value: string) => void,
    }
) {
    let loc = useLoc()
    let [selectedValue, setSelectedValue] = useState<T>()
    let [dropdownActive, setDropdownActive] = useState(false)
    let [internalSearchQuery, setInternalSearchQuery] = useState("")
    let dropdownRef = useRef(null);
    useClickOutsideHandler(dropdownRef, () => setDropdownActive(false))
    const searchQuery = controlledSearchQuery ?? internalSearchQuery;

    function updateSearchQuery(value: string) {
        if (typeof controlledSearchQuery === "undefined") {
            setInternalSearchQuery(value);
        }
        onSearchQueryChange?.(value);
    }

    const filteredValues = useMemo(
        () => {
            if (!searchQuery) return values;
            return values.filter(v => matchesSearch(searchQuery, v));
        },
        [values, searchQuery, matchesSearch]
    );

    return <div ref={dropdownRef} className={className + " relative bg-inherit"}>
        <div className={"flex gap-2 items-stretch bg-inherit"}>
            <div className={"flex gap-2 items-center grow bg-inherit"}
                 onClick={() => setDropdownActive(!dropdownActive)}>
                <div className={"grow bg-inherit"}>
                    {selectedValue ?
                        presenter(selectedValue)
                        :
                        emptyPresenter()
                    }
                </div>
                {!selectedValue &&
                    <button>
                        <AiFillCaretDown/>
                    </button>
                }
            </div>
            {selectedValue &&
                <button className={"px-3"}
                        onClick={() => {
                            setSelectedValue(undefined);
                            updateSearchQuery("")
                            setDropdownActive(false)
                            onSelect(undefined);
                        }}
                ><BsTrash/>
                </button>
            }
        </div>
        {dropdownActive &&
            <div className={"grid absolute t-[100%] w-full shadow-sm bg-inherit"}>
                <div className={"flex bg-inherit"}>
                    <button className={"px-2"}><AiOutlineSearch/></button>
                    <input type={"text"}
                           placeholder={loc("Search")}
                           autoFocus={true}
                           className={"grow"}
                           value={searchQuery}
                           onChange={(e) => updateSearchQuery(e.target.value)}
                    />
                    <button className={"px-3"}
                            onClick={() => {
                                setSelectedValue(undefined);
                                updateSearchQuery("")
                                setDropdownActive(false)
                            }}
                    ><RxCross2/></button>
                </div>
                {
                    filteredValues.map(value =>
                        <button
                            key={keyExtractor(value)}
                            className={"bg-inherit"}
                            onClick={() => {
                                setSelectedValue(value);
                                setDropdownActive(false)
                                updateSearchQuery("");
                                onSelect(value);
                            }}>
                            {presenter(value)}
                        </button>
                    )
                }
            </div>
        }
    </div>

}
