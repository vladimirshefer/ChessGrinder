/**
 * If you change this object, then you must restart the server.
 */
export const config = {
    features: {
        "auth.signupWithPasswordEnabled": process.env["REACT_APP_auth.signupWithPasswordEnabled"] === "true",
        "tournament.submitResultByParticipantsEnabled": process.env["REACT_APP_tournament.submitResultByParticipantsEnabled"] === "true",
    }
}

export default config
