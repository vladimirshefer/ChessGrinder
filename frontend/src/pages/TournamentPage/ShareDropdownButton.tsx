import React, {useState} from 'react';
import {DEFAULT_DATETIME_FORMAT, TournamentDto} from 'lib/api/dto/MainPageData';
import {useLoc} from 'strings/loc';
import {LuCalendarPlus, LuLink2, LuQrCode, LuShare2, LuTable} from "react-icons/lu";
import {Link} from "react-router-dom";
import {google} from "calendar-link";
import dayjs from "dayjs";
import QrCode from "components/QrCode";
import useDropdownControls from "lib/react/hooks/useDropdownControls";

function ShareDropdownButton(
    {
        tournament
    }
    : {
        tournament: TournamentDto
    }
) {
    const loc = useLoc();

    const [showDropdown, setShowDropdown, dropdownRef] = useDropdownControls();

    const [showQrModal, setShowQrModal] = useState(false);

    const addToCalendarUrl = google({
        title: tournament.name,
        description: `Chess tournament: ${tournament.name}`,
        start: dayjs(tournament.date).toDate(),
        end: dayjs(tournament.date).add(4, 'hours').toDate(),
        location: tournament.locationUrl || tournament.locationName || tournament.city || 'Planet Earth',
    });

    let locationGeneratedUrl = tournament.locationName
        ? `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(tournament.locationName + " " + (tournament.city || ""))}`
        : undefined;

    let tournamentUrl = `/api/tournament/${tournament.id}/export/trf`;
    return (
        <div className="relative flex">
            <button
                className="flex items-center gap-2 px-4!"
                onClick={() => setShowDropdown(it => !it)}
                title={loc('Add to calendar')}
            >
                <LuShare2/>
            </button>

            {showDropdown && (
                <div className="absolute grid right-0 top-full bg-white border border-gray-300 shadow-lg z-20 min-w-max"
                     ref={dropdownRef}
                >
                    <li className={"flex items-center gap-2 hover:bg-gray-100 p-2"}>
                        <LuCalendarPlus/>
                        <Link
                            className="text-left text-sm"
                            to={addToCalendarUrl}
                        >
                            {"Add to Calendar"}
                        </Link>
                    </li>
                    <li className={"flex items-center gap-2 hover:bg-gray-100 p-2"}>
                        <LuQrCode/>
                        <button
                            className="text-left text-sm"
                            onClick={() => {
                                setShowQrModal(true);
                                setShowDropdown(false);
                            }}
                        >
                            {"QR Code"}
                        </button>
                    </li>
                    <li className={"flex items-center gap-2 hover:bg-gray-100 p-2"}>
                        <LuLink2/>
                        <button
                            className="text-left text-sm"
                            onClick={() => {
                                window.navigator.share({
                                    title: `"${tournament.name}" - Tournament - Chess Grinder`,
                                    text:
                                        `Date: ${dayjs(tournament.date, DEFAULT_DATETIME_FORMAT).format("MMMM D, HH:mm")}\n` +
                                        `Location: "${tournament.locationName}" ${tournament.locationUrl || locationGeneratedUrl}`,
                                    url: tournamentUrl,
                                }).catch(() => {
                                    // Ignore aborted share attempts
                                });
                                setShowDropdown(false);
                            }}
                        >
                            {loc('Share link')}
                        </button>
                    </li>
                    <li className={"flex items-center gap-2 hover:bg-gray-100 p-2"}>
                        <LuTable/>
                        <Link
                            className="text-left text-sm"
                            to={tournamentUrl}
                            target={"_blank"}
                        >
                            {"Export TRF"}
                        </Link>
                    </li>
                </div>
            )}

            {showQrModal && (
                <div
                    className="fixed inset-0 z-30 flex items-center justify-center bg-black/60"
                    role="dialog"
                    aria-modal="true"
                >
                    <div className="relative w-[min(90vw,320px)] rounded-lg bg-white p-4 shadow-xl">
                        <button
                            className="absolute right-2 top-2 text-sm text-gray-500 hover:text-gray-800"
                            onClick={() => {
                                setShowQrModal(false);
                            }}
                            aria-label={loc('Close')}
                        >
                            ×
                        </button>
                        <h2 className="mb-3 text-center text-sm font-semibold uppercase text-gray-600">
                            {loc('Share via QR')}
                        </h2>
                        <div className="flex justify-center">
                            <div className="h-48 w-48">
                                <QrCode text={(new URL(`/tournament/${tournament.id}`, document.location.href)).href}/>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default ShareDropdownButton;
