import {MutableRefObject, useEffect} from "react";

/**
 * Hook that fires on outside of the passed ref
 */
export function useClickOutsideHandler(ref: MutableRefObject<any>, action: () => void) {
  useEffect(() => {
    function handleClickOutside(event: any) {
      if (ref.current && !ref.current.contains(event.target)) {
        action();
      }
    }
    // Bind the event listener
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      // Unbind the event listener on clean up
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [ref, action]);
}
