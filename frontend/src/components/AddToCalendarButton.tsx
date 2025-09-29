import React, {useState} from 'react';
import {TournamentDto} from 'lib/api/dto/MainPageData';
import {useLoc} from 'strings/loc';
import {AiOutlineCalendar, AiOutlineDown} from 'react-icons/ai';
import {useCalendar} from 'lib/react/hooks/useCalendar';

interface AddToCalendarButtonProps {
    tournament: TournamentDto;
}

function AddToCalendarButton({tournament}: AddToCalendarButtonProps) {
    const loc = useLoc();
    const [showDropdown, setShowDropdown] = useState(false);
    const {calendarOptions, handleCalendarClick} = useCalendar(tournament);
    
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
                                onClick={() => {
                                    handleCalendarClick(option);
                                    setShowDropdown(false);
                                }}
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