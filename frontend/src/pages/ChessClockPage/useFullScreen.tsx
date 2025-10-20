import {useEffect, useState} from "react";

/**
 * Due to security reasons, browsers prevent changing fullScreen without explicit user activity (clicks, gestures)
 */
function useFullScreen(): [boolean, (value: boolean) => void] {
    const [isFullscreen, setIsFullscreen] = useState(false);

    useEffect(() => {
        const handleFullscreenChange = () => {
            try {
                setIsFullscreen(!!document.fullscreenElement);
            } catch {
                // ignored
            }
        };
        document.addEventListener('fullscreenchange', handleFullscreenChange);
        return () => document.removeEventListener('fullscreenchange', handleFullscreenChange);
    }, []);

    async function actionSetFullscreen(value: boolean) {
        try {
            if (value) {
                await document.documentElement.requestFullscreen()
            } else {
                await document.exitFullscreen();
            }
        } catch {
            // ignored
        }
    }

    return [isFullscreen, actionSetFullscreen];
}

export default useFullScreen;