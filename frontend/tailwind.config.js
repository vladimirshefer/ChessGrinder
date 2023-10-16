let anzac = {
    '50': '#fbf8eb',
    '100': '#f5edcc',
    '200': '#ecdb9c',
    '300': '#e1c163',
    '400': '#d9ae46',
    '500': '#c7932b',
    '600': '#ab7323',
    '700': '#89541f',
    '800': '#724521',
    '900': '#623a21',
    '950': '#391d0f',
};

/** @type {import('tailwindcss').Config} */
module.exports = {
    content: ["./src/**/*.{html,js,jsx,ts,tsx}"],
    theme: {
        extend: {
            colors: {
                anzac: anzac,
                primary: anzac["400"],
            },
        },
    },
    plugins: [],
}
