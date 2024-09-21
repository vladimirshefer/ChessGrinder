import {useEffect} from 'react';

export function usePageTitle(title: string, dependencies: any[] = [title]): void {
    useEffect(
        () => {
            document.title = title;
            return () => {
                document.title = "ChessGrinder - International Chess Club";
            };
        },
        // eslint-disable-next-line react-hooks/exhaustive-deps
        dependencies
    )
}
