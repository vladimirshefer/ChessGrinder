import React from 'react';
import 'App.css';
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import {BrowserRouter, HashRouter, Route, Routes} from "react-router-dom";
import MainPage from "pages/MainPage"
import TournamentPage from "pages/TournamentPage"
import Header from "components/Header";
import AdminPage from "pages/AdminPage";
import LoginPage from "pages/LoginPage";
import UserProfilePage from "pages/UserProfilePage";
import ParticipantPage from "pages/ParticipantPage";
import BadgesPage from "pages/BadgesPage";
import UsersPage from "pages/UsersPage";
import TournamentEditPage from "pages/TournamentEditPage";
import BadgePage from "pages/BadgePage";
import UserProfileEditPage from "pages/UserProfileEditPage";
import {AuthenticatedUserContextProvider} from "contexts/AuthenticatedUserContext";
import {LanguageContextProvider} from "contexts/LanguageContext";

const queryClient = new QueryClient()

let ApplicationRouter = 1 > 0 /*TODO*/ ? HashRouter : BrowserRouter

function App() {

    return (
        <LanguageContextProvider>
            <AuthenticatedUserContextProvider>
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
            </AuthenticatedUserContextProvider>
        </LanguageContextProvider>
    );
}

export default App;
