import {ReactElement, useMemo, useRef, useState} from "react";
import {useClickOutsideHandler} from "../lib/util/ClickOutside";
import {BsTrash} from "react-icons/bs";
import {AiFillCaretDown, AiOutlineSearch} from "react-icons/ai";
import {useLoc} from "../strings/loc";
import {RxCross2} from "react-icons/rx";

export default function DropdownSelect<T>(
    {
        values,
        className = "",
        presenter,
        onSelect,
        keyExtractor,
        emptyPresenter = () => <button>Select</button>,
        search = true,
        matchesSearch = () => true,
    }: {
        values: T[],
        className?: string
        presenter: (value: T) => ReactElement,
        onSelect: (value: T | undefined) => void,
        keyExtractor: (value: T) => any,
        emptyPresenter?: () => ReactElement,
        search?: boolean
        matchesSearch?: (searchQuery: string, value: T) => boolean,
    }
) {
    let loc = useLoc()
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

    return <div ref={dropdownRef} className={className + " relative"}>
        <div className={"flex gap-2 items-stretch"}
        >
            <div className={"flex gap-2 items-center grow"}
                 onClick={() => setDropdownActive(!dropdownActive)}>
                <div className={"grow"}>
                    {selectedValue ?
                        presenter(selectedValue)
                        :
                        emptyPresenter()
                    }
                </div>

            </div>
            {selectedValue ?
                <button className={"px-3"}
                        onClick={() => {
                            setSelectedValue(undefined);
                            setSearchQuery("")
                            setDropdownActive(false)
                        }}
                ><BsTrash/></button>
            :
            <span><AiFillCaretDown/></span>
            }
        </div>
        {dropdownActive &&
            <div className={"grid absolute t-[100%] w-full shadow"}>
                <div className={"flex bg-white"}>
                    <button className={"px-2"}><AiOutlineSearch/></button>
                    <input type={"text"}
                           placeholder={loc("Search")}
                           autoFocus={true}
                           className={"grow"}
                           onChange={(e) => setSearchQuery(e.target.value)}
                    />
                    <button className={"px-3"}
                            onClick={() => {
                                setSelectedValue(undefined);
                                setSearchQuery("")
                                setDropdownActive(false)
                            }}
                    ><RxCross2/></button>
                </div>
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
            </div>
        }
    </div>

}
