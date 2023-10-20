import {useSearchParams} from "react-router-dom";

export default function useSearchParam(name: string): [string | null, (value: string) => void] {
    let [searchParams, setSearchParams] = useSearchParams()
    return [
        searchParams.get(name),
        (value: string) => {
            let newSearchParams: any = {...searchParams};
            if (!value) {
                delete newSearchParams[name];
            } else {
                newSearchParams[name] = value;
            }
            setSearchParams(newSearchParams)
        }
    ]
}
