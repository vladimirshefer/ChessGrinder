import {useMemo} from "react";

export default function BrandedLoginButton(
    {
        provider,
        showText = true,
    }: {
        provider: "google" | "chesscom",
        showText?: boolean,
    }
) {

    let GOOGLE_LOGO_SVG = useMemo(() => (
        <svg version="1.1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48"
             className="max-w-full max-h-full w-auto h-auto"
             xmlnsXlink="http://www.w3.org/1999/xlink">
            <path
                fill="#EA4335"
                d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"
            ></path>
            <path
                fill="#4285F4"
                d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"
            ></path>
            <path
                fill="#FBBC05"
                d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"
            ></path>
            <path
                fill="#34A853"
                d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"
            ></path>
            <path
                fill="none"
                d="M0 0h48v48H0z"
            ></path>
        </svg>
    ), []);

    let CHESSCOM_LOGO_SVG = useMemo(() => (
        <svg version="1.1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 22 32"
             className="max-w-full max-h-full w-auto h-auto"
        >
            <path
                fill="#5D9948"
                d="M18.7635 23.0905C13.3587 18.9895 13.9594 15.4295 13.8917 13.9663H17.1875C17.5725 13.2547 17.7692 12.5937 17.7692 11.7726L14.0335 9.3242C15.3323 8.38735 16.1785 6.86946 16.1785 5.15157C16.1785 2.98946 14.8394 1.13893 12.9419 0.374723C12.3433 0.132618 8.1104 13.9663 8.1104 13.9663C8.09559 14.2842 8.09136 14.6989 8.09136 15.1979C8.09136 16.5726 11.4844 16.3642 11.3046 17.5768C11.0339 19.3916 10.9767 20.7705 9.40713 25.1326C8.34732 28.0758 1.27982 25.1326 0.774246 26.5768C0.423092 27.5831 0.236938 28.7095 0.236938 29.9221C0.236938 30.0526 0.5204 32 11.0021 32C21.4839 32 21.7673 30.0526 21.7673 29.9221C21.7673 26.9642 20.6567 24.5242 18.7656 23.0905H18.7635Z"
            />
            <path
                fill="#81B64C"
                d="M10.8097 24.9305C11.3935 22.2926 11.9097 19.48 12.2227 17.7937C12.6141 15.6905 9.40928 15.3158 8.09139 15.1221C8.03216 16.9179 7.5287 19.8316 3.23447 23.0884C2.07736 23.9663 1.21428 25.221 0.717163 26.741C1.87639 27.3032 3.42274 27.6379 5.80678 27.6379C7.3362 27.6379 10.1687 27.821 10.8075 24.9284L10.8097 24.9305Z"
            />
            <path
                fill="#81B64C"
                d="M13.0604 13.9663C13.5681 12.6547 13.5025 11.7726 13.5025 11.7726L11.3871 9.32421C13.6379 8.36842 14.9918 6.57053 14.9918 4.48C14.9918 2.80842 14.1921 1.32421 12.9525 0.383158C12.3496 0.138948 11.6896 0.00210571 11 0.00210571C8.14214 0.00210571 5.82368 2.30737 5.82368 5.15368C5.82368 6.87158 6.66983 8.38947 7.96868 9.32632L4.23291 11.7747C4.23291 12.5958 4.42753 13.2568 4.81464 13.9684H13.0625L13.0604 13.9663Z"
            />
            <path
                d="M10.7038 1.05052C13.6886 1.51157 9.32879 4.95789 7.95591 4.79578C6.64648 4.63999 7.90725 0.61894 10.7038 1.05052Z"
                fill="#B2E068"
            />
        </svg>
    ), []);

    let logo = provider === "google" ? GOOGLE_LOGO_SVG : CHESSCOM_LOGO_SVG;
    let brand = provider === "google" ? "Google" : "Chess.com";
    let text = `Sign in with ${brand}`;

    return (
        <button className="
            select-none appearance-none bg-white border border-[#747775] rounded box-border text-[#1f1f1f]
            cursor-pointer text-sm h-10 tracking-[0.25px]
            outline-none overflow-hidden px-3 py-0 relative text-center
            transition-[background-color,border-color,box-shadow] duration-200
            align-middle whitespace-nowrap w-auto max-w-[400px]
            min-w-min hover:shadow-[0_1px_2px_0_rgba(60,64,67,.30),0_1px_3px_1px_rgba(60,64,67,.15)]
            disabled:cursor-default disabled:bg-[#ffffff61]
            disabled:border-[#1f1f1f1f] group"
                title={text}
        >
            <div className="
                absolute inset-0 opacity-0 transition-opacity duration-200
                group-hover:bg-[#303030] group-hover:opacity-[0.08] group-active:bg-[#303030] group-active:opacity-[0.12]
                group-focus:bg-[#303030] group-focus:opacity-[0.12] group-disabled:hidden"
            />
            <div className="flex flex-row flex-nowrap items-center justify-between h-full relative w-full gap-3">
                <div className="h-5 w-5 flex items-center justify-center group-disabled:opacity-[0.38]">
                    {logo}
                </div>
                {showText && (
                    <span className="
                        flex-grow font-['Roboto',arial,sans-serif] font-medium overflow-hidden text-ellipsis align-top
                        group-disabled:opacity-[0.38]"
                    >
                        {text}
                    </span>
                )}
                <span className={"hidden"}>
                    {text}
                </span>
            </div>
        </button>
    )
}
