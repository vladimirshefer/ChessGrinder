/**
 * If you change this object, then you must restart the server.
 */
export const config = {
    features: {
        "auth.signupWithPasswordEnabled": process.env["REACT_APP_auth.signupWithPasswordEnabled"] === "true"
    }
}

export default config
