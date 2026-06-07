import {AiOutlineRight} from "react-icons/ai";
import {FaCrown} from "react-icons/fa";

function DoorPreview(
    {
        src,
        alt,
        className = "",
    }: {
        src: string
        alt: string
        className?: string
    }
) {
    return <div className={`relative flex min-w-0 items-end justify-center ${className}`}>
        <div className={"absolute bottom-1 h-7 w-[72%] rounded-full bg-black/15 blur-md"}/>
        <img
            src={src}
            alt={alt}
            className={"relative z-10 max-h-full w-auto object-contain drop-shadow-[0_12px_18px_rgba(57,29,15,0.20)]"}
        />
    </div>
}

function DoorShowcase() {
    return <>
        <div className={"flex w-28 flex-col items-center gap-2 sm:hidden"}>
            <div className={"relative h-[118px] w-27"}>
                <div className={"absolute inset-x-2 bottom-2 h-14 rounded-full bg-primary-300/30 blur-2xl"}/>
                <DoorPreview
                    src={"/chessarium/door-opening.webp"}
                    alt={"Opening training door"}
                    className={"h-[118px]"}
                />
            </div>
            <div className={"w-full rounded-[10px] border border-primary-200 bg-white/90 px-2 py-1.5 text-center shadow-sm"}>
                <div className={"text-[9px] font-semibold uppercase leading-none tracking-wide text-primary-700"}>
                    USE
                </div>
                <div className={"mt-1 rounded-md bg-[#fff4d7] px-1.5 py-1 font-mono text-[10px] font-bold leading-none tracking-[0.08em] text-gray-950"}>
                    CHESSGRINDER
                </div>
                <div className={"mt-1 text-[10px] font-semibold leading-none text-primary-700"}>
                    20% off
                </div>
            </div>
        </div>

        <div className={"hidden flex-col items-center gap-2 sm:flex"}>
            <div className={"relative h-33 w-[242px] md:h-[154px] md:w-71"}>
                <div className={"absolute inset-x-4 bottom-2 h-16 rounded-full bg-primary-300/30 blur-2xl"}/>
                <div className={"absolute inset-x-0 bottom-0 grid h-full grid-cols-3 items-end gap-1.5"}>
                    <DoorPreview
                        src={"/chessarium/door-opening.webp"}
                        alt={"Opening training door"}
                        className={"h-26 -rotate-3 opacity-95 md:h-[122px]"}
                    />
                    <DoorPreview
                        src={"/chessarium/door-boss.webp"}
                        alt={"Boss challenge door"}
                        className={"h-33 md:h-[154px]"}
                    />
                    <DoorPreview
                        src={"/chessarium/door-tactic.webp"}
                        alt={"Tactics training door"}
                        className={"h-27 rotate-3 opacity-95 md:h-[126px]"}
                    />
                </div>
                <div className={"absolute right-4 top-3 rounded-full border border-primary-200 bg-white/90 px-2 py-1 text-xs font-bold text-primary-800 shadow-sm"}>
                    +XP
                </div>
            </div>
            <div className={"flex items-center gap-2 rounded-xl border border-primary-200 bg-white/90 px-3 py-1.5 text-center shadow-sm"}>
                <span className={"text-[11px] font-semibold uppercase tracking-wide text-primary-700"}>
                    USE
                </span>
                <span className={"rounded-md bg-[#fff4d7] px-2 py-1 font-mono text-xs font-bold leading-none tracking-[0.08em] text-gray-950"}>
                    CHESSGRINDER
                </span>
                <span className={"text-xs font-semibold text-primary-700"}>
                    20% off
                </span>
            </div>
        </div>
    </>
}

function ChessariumBanner() {
    return <section className={"w-full overflow-hidden rounded-[18px] border border-primary-200 bg-[#fffdf8] shadow-sm sm:w-fit"}>
        <div className={"grid grid-cols-[minmax(0,1fr)_auto] items-center justify-items-start gap-4 p-5 sm:grid-cols-[340px_auto] sm:gap-5 sm:p-6"}>
            <div className={"flex min-w-0 flex-col items-start gap-4 text-left"}>
                <div>
                    <div className={"mb-3 flex items-center gap-3 text-gray-950"}>
                        <div className={"inline-flex h-10 w-10 items-center justify-center rounded-lg bg-[#d4942e] shadow-lg shadow-[#d4942e]/20"}>
                            <FaCrown className={"h-5 w-5 text-white"}/>
                        </div>
                        <span className={"text-2xl font-normal leading-none"}>
                            Chessarium
                        </span>
                    </div>
                    <h2 className={"text-2xl font-bold leading-tight text-gray-950 md:text-3xl"}>
                        Train through chess dungeons.
                    </h2>
                </div>

                <div className={"flex flex-wrap items-center gap-3"}>
                    <a
                        href={"https://chessarium.com"}
                        target={"_blank"}
                        rel={"noreferrer"}
                        className={"bg-primary-400 text-black font-semibold inline-flex items-center gap-2 rounded-xl px-5 py-2 shadow-sm transition-colors hover:bg-primary-300"}
                    >
                        Start training
                        <AiOutlineRight/>
                    </a>
                    <span className={"text-xs text-gray-500"}>
                        * Unbezahlte Werbung
                    </span>
                </div>
            </div>

            <div className={"flex justify-center"}>
                <DoorShowcase/>
            </div>
        </div>
    </section>
}

export default ChessariumBanner
