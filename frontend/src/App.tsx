import React, {useEffect, useState} from 'react';
import 'App.css';
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import {BrowserRouter, HashRouter, Route, Routes} from "react-router-dom";
import MainPage from "pages/MainPage"
import TournamentPage from "pages/TournamentPage"
import Header from "components/Header";
import AdminPage from "pages/AdminPage";
import LoginPage from "pages/LoginPage";
import UserProfilePage from "pages/UserProfilePage";
import userRepository from "lib/api/repository/UserRepository";
import authService, {AuthData} from "lib/auth/AuthService";
import ParticipantPage from "pages/ParticipantPage";
import BadgesPage from "pages/BadgesPage";
import UsersPage from "pages/UsersPage";
import TournamentEditPage from "pages/TournamentEditPage";
import BadgePage from "pages/BadgePage";
import UserProfileEditPage from "pages/UserProfileEditPage";
import {MemberDto} from "lib/api/dto/MainPageData";

const queryClient = new QueryClient()

let ApplicationRouter = 1 > 0 /*TODO*/ ? HashRouter : BrowserRouter

type Property<T> = [T, (v: T) => void]

export const LanguageContext = React.createContext<Property<string>>(["en", (l: string) => {
}]);
export const UserContext = React.createContext<Property<MemberDto | null>>([null, () => {
}])

function App() {
    const languageContextValue = useState("ru");
    const [user, setUser] = useState<MemberDto | null>(null);

    useEffect(() => {
        checkAuthData()
    }, [])

    async function checkAuthData() {
        let me = await userRepository.getMe();
        if (!me) {
            authService.setAuthData(null);
            setUser(null);
        } else {
            setUser(me)
            let authData: AuthData = {
                username: me!!.username,
                roles: me.roles,
                accessToken: "",
            };
            authService.setAuthData(authData)
        }
    }

    return (
        <LanguageContext.Provider value={languageContextValue}>
            <UserContext.Provider value={[user, (it) => setUser(it)]}>
                <div className='App'>
                    <QueryClientProvider client={queryClient}>
                        <ApplicationRouter>
                            <React.StrictMode>
                                <Header/>
                                <Routes>
                                    <Route path="/" element={<MainPage/>}/>
                                    <Route path="/tournament/:id" element={<TournamentPage/>}/>
                                    <Route path="/tournament/:id/round/:roundId" element={<TournamentPage/>}/>
                                    <Route path="/tournament/:tournamentId/participant/:participantId"
                                           element={<ParticipantPage/>}/>
                                    <Route path="/tournament/:tournamentId/edit"
                                           element={<TournamentEditPage/>}
                                    />
                                    <Route path="/login" element={<LoginPage/>}/>
                                    <Route path="/admin" element={<AdminPage/>}/>
                                    <Route path="/user" element={<UserProfilePage/>}/>
                                    <Route path="/user/:username" element={<UserProfilePage/>}/>
                                    <Route path="/user/me/edit" element={<UserProfileEditPage/>}/>
                                    <Route path="/users" element={<UsersPage/>}/>
                                    <Route path="/badges" element={<BadgesPage/>}/>
                                    <Route path="/badge/:badgeId" element={<BadgePage/>}/>
                                </Routes>
                            </React.StrictMode>
                        </ApplicationRouter>
                    </QueryClientProvider>
                </div>
            </UserContext.Provider>
        </LanguageContext.Provider>
    );
}

export default App;
