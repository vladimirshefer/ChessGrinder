import { CalendarEvent, google, ics } from 'calendar-link';
import { TournamentDto } from 'lib/api/dto/MainPageData';
import dayjs from 'dayjs';

interface CalendarOption {
    name: string;
    url: string;
    download?: boolean;
}

export function useCalendar(tournament: TournamentDto) {
    const createCalendarEvent = (): CalendarEvent => ({
        title: tournament.name,
        description: `Chess tournament: ${tournament.name}`,
        start: dayjs(tournament.date).toDate(),
        end: dayjs(tournament.date).add(2, 'hours').toDate(),
        location: tournament.locationUrl || tournament.locationName || tournament.city || 'Planet Earth',
    });

    const calendarOptions: CalendarOption[] = [
        {
            name: 'Google Calendar',
            url: google(createCalendarEvent()),
        },
        {
            name: 'Apple / Others',
            url: ics(createCalendarEvent()),
            download: true,
        },
    ];

    const handleCalendarClick = (option: CalendarOption) => {
        if (option.download) {
            const link = document.createElement('a');
            link.href = option.url;
            link.download = `${tournament.name.replace(/[^a-z0-9]/gi, '_').toLowerCase()}.ics`;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        } else {
            window.open(option.url, '_blank');
        }
    };

    return {
        calendarOptions,
        handleCalendarClick,
    };
}