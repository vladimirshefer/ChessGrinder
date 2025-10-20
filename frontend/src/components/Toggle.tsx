import React, {useState} from "react";

export default function Toggle(
    {
        checked,
        setChecked,
        defaultChecked,
        title,
    }: {
        checked?: boolean | undefined,
        setChecked?: ((c: boolean) => void) | undefined,
        defaultChecked?: boolean | undefined,
        title?: string | undefined
    }
) {
    let [checkedInternal, setCheckedInternal] = useState(defaultChecked || false)

    let checkedReal = checked !== undefined ? checked : checkedInternal

    // Source: https://flowbite.com/docs/forms/toggle/
    return <label className="inline-flex items-center cursor-pointer" title={title}>
        <input type="checkbox" className="sr-only peer" checked={checkedReal} title={title}
               onChange={e => {
                   setChecked?.(e.target.checked)
                   setCheckedInternal(e.target.checked)
               }}
        />
        <div className="relative
                        w-11
                        h-6
                        bg-gray-200
                        peer-focus:outline-hidden
                        peer-focus:ring-4
                        peer-focus:ring-primary-300
                        dark:peer-focus:ring-primary-800
                        rounded-full
                        peer
                        dark:bg-gray-700
                        peer-checked:after:translate-x-full
                        peer-checked:rtl:after:-translate-x-full
                        peer-checked:after:border-white
                        after:content-['']
                        after:absolute
                        after:top-[2px]
                        after:start-[2px]
                        after:bg-white
                        after:border-gray-300
                        after:border
                        after:rounded-full
                        after:h-5
                        after:w-5
                        after:transition-all
                        dark:border-gray-600
                        peer-checked:bg-primary-600
                        "
             title={title}
        ></div>
    </label>;
}
