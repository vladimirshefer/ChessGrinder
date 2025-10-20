import React, {useRef, useState} from "react";
import {useClickOutsideHandler} from "lib/util/ClickOutside";

function useDropdownControls(): [boolean, (value: (((prevState: boolean) => boolean) | boolean)) => void, React.MutableRefObject<null>] {
    let [showDropdown, setShowDropdown] = useState(false);
    let ref = useRef(null);
    useClickOutsideHandler(ref, () => setShowDropdown(false));

    return [showDropdown, setShowDropdown, ref]
}

export default useDropdownControls;