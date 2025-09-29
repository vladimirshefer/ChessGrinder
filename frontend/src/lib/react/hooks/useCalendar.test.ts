import { renderHook, act } from '@testing-library/react';
import { TournamentDto } from 'lib/api/dto/MainPageData';
import { useCalendar } from './useCalendar';

jest.mock('dayjs', () => {
    const mockDayjs = (date: string) => ({
        toDate: () => new Date(date),
        add: (value: number, unit: string) => ({
            toDate: () => new Date(new Date(date).getTime() + (unit === 'hours' ? value * 60 * 60 * 1000 : 0))
        })
    });
    return mockDayjs;
});

jest.mock('calendar-link', () => ({
    google: jest.fn((event) => `https://google.com/calendar?title=${event.title}`),
    ics: jest.fn((event) => `data:text/calendar;charset=utf8,${event.title}`)
}));

describe('useCalendar', () => {
    const mockTournament: TournamentDto = {
        id: '1',
        name: 'Test Tournament',
        date: '2025-09-29T19:30:00',
        status: 'PLANNED',
        locationName: 'Chess Club',
        locationUrl: 'https://maps.google.com/chess-club',
        city: 'Limassol',
        roundsNumber: 5,
        pairingStrategy: 'SWISS'
    };

    beforeEach(() => {
        jest.clearAllMocks();

        jest.spyOn(document.body, 'appendChild').mockImplementation((node) => node);
        jest.spyOn(document.body, 'removeChild').mockImplementation((node) => node);
        jest.spyOn(window, 'open').mockImplementation(() => null);
    });

    it('should initialize with dropdown closed', () => {
        const container = document.createElement('div');
        const { result } = renderHook(() => useCalendar(mockTournament), { container });
        
        expect(result.current.showDropdown).toBe(false);
    });

    it('should provide calendar options', () => {
        const container = document.createElement('div');
        const { result } = renderHook(() => useCalendar(mockTournament), { container });
        
        expect(result.current.calendarOptions).toHaveLength(2);
        expect(result.current.calendarOptions[0].name).toBe('Google Calendar');
        expect(result.current.calendarOptions[1].name).toBe('Apple / Others');
        expect(result.current.calendarOptions[1].download).toBe(true);
    });

    it('should provide setShowDropdown function', () => {
        const container = document.createElement('div');
        const { result } = renderHook(() => useCalendar(mockTournament), { container });
        
        expect(typeof result.current.setShowDropdown).toBe('function');

        act(() => {
            result.current.setShowDropdown(true);
        });

        expect(result.current.showDropdown).toBe(true);
    });

    it('should handle Google Calendar click', () => {
        const container = document.createElement('div');
        const { result } = renderHook(() => useCalendar(mockTournament), { container });
        
        const googleOption = result.current.calendarOptions[0];
        
        act(() => {
            result.current.handleCalendarClick(googleOption);
        });
        
        expect(window.open).toHaveBeenCalledWith(googleOption.url, '_blank');
        expect(result.current.showDropdown).toBe(false);
    });

    it('should handle ICS download click', () => {
        const mockLink = {
            href: '',
            download: '',
            click: jest.fn(),
        };

        const container = document.createElement('div');
        
        jest.spyOn(document, 'createElement').mockImplementation((tagName) => {
            if (tagName === 'a') {
                return mockLink as any;
            }

            return { tagName: tagName.toUpperCase() } as any;
        });
        
        const { result } = renderHook(() => useCalendar(mockTournament), { container });
        
        const icsOption = result.current.calendarOptions[1];
        
        act(() => {
            result.current.handleCalendarClick(icsOption);
        });
        
        expect(document.createElement).toHaveBeenCalledWith('a');
        expect(mockLink.href).toBe(icsOption.url);
        expect(mockLink.download).toBe('test_tournament.ics');
        expect(mockLink.click).toHaveBeenCalled();
        expect(document.body.appendChild).toHaveBeenCalledWith(mockLink);
        expect(document.body.removeChild).toHaveBeenCalledWith(mockLink);
        expect(result.current.showDropdown).toBe(false);
        
        jest.restoreAllMocks();
    });

    it('should generate correct filename for ICS download', () => {
        const tournamentWithSpecialChars: TournamentDto = {
            ...mockTournament,
            name: 'Test Tournament! @#$%^&*()',
        };
        
        const mockLink = {
            href: '',
            download: '',
            click: jest.fn(),
        };

        const container = document.createElement('div');
        
        jest.spyOn(document, 'createElement').mockImplementation((tagName) => {
            if (tagName === 'a') {
                return mockLink as any;
            }

            return { tagName: tagName.toUpperCase() } as any;
        });
        
        const { result } = renderHook(() => useCalendar(tournamentWithSpecialChars), { container });
        
        const icsOption = result.current.calendarOptions[1];
        
        act(() => {
            result.current.handleCalendarClick(icsOption);
        });
        
        expect(mockLink.download).toBe('test_tournament___________.ics');
        
        jest.restoreAllMocks();
    });
});