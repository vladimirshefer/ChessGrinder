import React, {useState} from 'react';
import {CalendarEvent} from 'calendar-link';
import {google, ics} from 'calendar-link';
import {TournamentDto} from 'lib/api/dto/MainPageData';
import dayjs from 'dayjs';
import {useLoc} from 'strings/loc';
import {AiOutlineCalendar, AiOutlineDown} from 'react-icons/ai';

interface AddToCalendarButtonProps {
    tournament: TournamentDto;
}

function createCalendarEvent(tournament: TournamentDto): CalendarEvent {
    const startDateTime = dayjs(tournament.date);
    const endDateTime = startDateTime.add(2, 'hours');

    return {
        title: tournament.name,
        description: `Chess tournament: ${tournament.name}`,
        start: startDateTime.toDate(),
        end: endDateTime.toDate(),
        location: tournament.locationUrl || tournament.locationName || tournament.city || 'Planet Earth',
    };
}

function AddToCalendarButton({tournament}: AddToCalendarButtonProps) {
    const [showDropdown, setShowDropdown] = useState(false);
    const loc = useLoc();
    
    const calendarEvent = createCalendarEvent(tournament);
    
    const calendarOptions = [
        {
            name: 'Google Calendar',
            url: google(calendarEvent),
        },
        {
            name: 'Apple / Others',
            url: ics(calendarEvent),
            download: true,
        },
    ];
    
    const handleCalendarClick = (option: typeof calendarOptions[0]) => {
        if (option.download) {
            // For ICS files, trigger download
            const link = document.createElement('a');
            link.href = option.url;
            link.download = `${tournament.name.replace(/[^a-z0-9]/gi, '_').toLowerCase()}.ics`;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        } else {
            // For web calendars, open in new tab
            window.open(option.url, '_blank');
        }
        setShowDropdown(false);
    };
    
    return (
        <div className="relative">
            <button
                className="btn-light flex items-center gap-2 !px-4"
                onClick={() => setShowDropdown(!showDropdown)}
                title={loc('Add to calendar')}
            >
                <AiOutlineCalendar />
                <span className="hidden sm:inline">{loc('Add to calendar')}</span>
                <AiOutlineDown className={`transition-transform ${showDropdown ? 'rotate-180' : ''}`} />
            </button>
            
            {showDropdown && (
                <>
                    <div
                        className="fixed inset-0 z-10"
                        onClick={() => setShowDropdown(false)}
                    />
                    <div className="absolute right-0 mt-2 w-48 bg-white border border-gray-300 rounded-md shadow-lg z-20">
                        {calendarOptions.map((option) => (
                            <button
                                key={option.name}
                                className="block w-full text-left px-4 py-2 text-sm hover:bg-gray-100 first:rounded-t-md last:rounded-b-md"
                                onClick={() => handleCalendarClick(option)}
                            >
                                {option.name}
                            </button>
                        ))}
                    </div>
                </>
            )}
        </div>
    );
}

export default AddToCalendarButton;