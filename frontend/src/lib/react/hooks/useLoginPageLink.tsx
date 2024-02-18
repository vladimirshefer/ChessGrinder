import {useLocation} from "react-router-dom";

/**
 * Returns link to login page with saving current location to be redirected back.
 */
export default function useLoginPageLink(){
    let location = useLocation()
    return "/login?referer=" + location.pathname
}
