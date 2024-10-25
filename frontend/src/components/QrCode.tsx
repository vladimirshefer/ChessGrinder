import QrCreator from 'qr-creator';
import {useEffect, useRef} from "react";

function QrCode(
    {
        text
    }: {
        text: string,
    }
) {
    let ref = useRef(null)
    useEffect(() => {
        let element = ref.current;
        if (element === null || element === undefined) return;
        QrCreator.render({
            text: text,
            size: 1000,
            ecLevel: 'L',
            radius: 0,
            fill: '#000',
            background: '#fff',
        }, element)
    }, [ref, text])

    return <canvas title={text} ref={ref} className={"max-w-full max-h-full"}></canvas>
}

export default QrCode;
