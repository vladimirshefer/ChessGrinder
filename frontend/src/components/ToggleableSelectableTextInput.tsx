import React, {useState} from "react";

export default function ToggleableSelectableTextInput(
    {
        values,
        submitValue,
        buttonText = "Add",
    }: {
        values: string[] | undefined,
        submitValue: (value: string) => void,
        buttonText?: string,
    }
) {
    let [inputEnabled, setInputEnabled] = useState<boolean>(false);
    let [selectedValue, setSelectedValue] = useState<string>("")

    return <>
        {
            inputEnabled ?
                <div className={"w-full grid grid-cols-12 p-1"}>
                    <div className={"col-span-9"}>
                        <input className={"border-b-2 border-b-blue-300 w-full px-2 outline-none"}
                               autoFocus list="members" name="myBrowser"
                               onChange={event => setSelectedValue(event.target.value)}
                        />
                        <datalist id="members">
                            {values ? values.map(value => <option key={value} value={value}/>) : []}
                        </datalist>
                    </div>
                    <div className={"col-span-3 px-2 grid grid-cols-12 gap-x-1"}>
                        <button className={"w-full bg-blue-300 rounded-full col-span-8"}
                                onClick={() => {
                                    if (selectedValue) {
                                        submitValue(selectedValue)
                                    }
                                    setInputEnabled(false)
                                }}>
                            Add
                        </button>
                        <button className={"w-full bg-red-300 rounded-full col-span-4"}
                                onClick={() => {
                                    setInputEnabled(false)
                                }}>
                            X
                        </button>
                    </div>
                </div>
                : <button className={"w-full bg-blue-300 rounded-full p-1"}
                          onClick={() => setInputEnabled(true)}> {buttonText} </button>
        }
    </>;
}
