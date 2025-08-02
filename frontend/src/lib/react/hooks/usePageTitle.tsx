import React, {useEffect} from 'react';

export function usePageTitle(title: string, dependencies: React.DependencyList = [title]): void {
    useEffect(() => {
        document.title = title;
        return () => {
            document.title = "ChessGrinder - International Chess Club";
        };
    }, dependencies)
}
