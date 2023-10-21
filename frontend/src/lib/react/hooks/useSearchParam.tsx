import {useSearchParams} from "react-router-dom";

export default function useSearchParam(name: string, defaultValue: string | null = null): [string | null, (value: string) => void] {
    let [searchParams, setSearchParams] = useSearchParams()
    return [
        searchParams.get(name) || defaultValue,
        (value: string) => {
            let newSearchParams: any = {...searchParams};
            if (!value) {
                delete newSearchParams[name];
            } else {
                newSearchParams[name] = value;
            }
            setSearchParams(newSearchParams, {replace: true})
        }
    ]
}
