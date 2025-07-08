import {useEffect} from "react";

/**
 * A custom React hook that prevents the screen from going to sleep
 * using the Screen Wake Lock API.
 *
 * @param shouldKeepAwake - Boolean flag to control wake lock
 *
 * Example usage:
 * ```tsx
 * function MyComponent() {
 *   const [isActive, setIsActive] = useState(false);
 *
 *   // Screen will stay awake when isActive is true
 *   useCaffeinate(isActive);
 *
 *   return (
 *     <button onClick={() => setIsActive(!isActive)}>
 *       {isActive ? 'Stop' : 'Start'}
 *     </button>
 *   );
 * }
 * ```
 */
const useCaffeinate = (shouldKeepAwake: boolean) => {
    useEffect(() => {
        if (shouldKeepAwake) {
            const wakeLock = async () => {
                try {
                    // @ts-ignore
                    await navigator.wakeLock.request('screen');
                } catch (err) {
                    console.log(err);
                }
            };
            wakeLock();
        }

        return () => {
            if (!shouldKeepAwake) {
                // Release wake lock when paused
                // @ts-ignore
                navigator.wakeLock?.release?.();
            }
        };
    }, [shouldKeepAwake]);
};

export default useCaffeinate;