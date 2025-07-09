import {useSearchParams} from "react-router-dom";

export default function useSearchParam(name: string, defaultValue: string | null = null): [string | null, (value: string) => void] {
    let [searchParams, setSearchParams] = useSearchParams()
    return [
        searchParams.get(name) || defaultValue,
        (value: string) => {
            setSearchParams((searchParams) => {
                if (!value) {
                    searchParams.delete(name)
                } else {
                    searchParams.set(name, value);
                }
                return searchParams;
            });
        }
    ]
}
