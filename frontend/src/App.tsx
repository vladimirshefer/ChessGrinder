import ChessClockPage from "pages/ChessClockPage";
import React from 'react';
import 'App.css';
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import {BrowserRouter, Outlet, Route, Routes} from "react-router-dom";
import MainPage from "pages/MainPage"
import TournamentPage from "pages/TournamentPage"
import Header from "components/Header";
import AdminPage from "pages/AdminPage";
import LoginPage from "pages/LoginPage";
import UserProfilePage from "pages/UserProfilePage";
import ParticipantPage from "pages/ParticipantPage";
import BadgesPage from "pages/BadgesPage";
import UsersPage from "pages/UsersPage";
import AllTournamentsPage from "pages/AllTournamentsPage";
import TournamentEditPage, {TournamentCreatePage} from "pages/TournamentEditPage";
import BadgePage from "pages/BadgePage";
import UserProfileEditPage from "pages/UserProfileEditPage";
import {AuthenticatedUserContextProvider} from "contexts/AuthenticatedUserContext";
import {LanguageContextProvider} from "contexts/LanguageContext";
import PrivacyPolicyPage from "pages/PrivacyPolicy";
import NotFoundPage from "pages/NotFoundPage";
import {ConfigurationContextProvider} from "contexts/ConfigurationContext";
import Footer from "components/Footer";
import AboutPage from "./pages/AboutPage";
import EventPage from "./pages/TournamentPage/EventPage";

const queryClient = new QueryClient()

let ApplicationRouter = BrowserRouter

function App() {

    return (
        <LanguageContextProvider>
            <ConfigurationContextProvider>
            <AuthenticatedUserContextProvider>
                <div className='App flex flex-col min-h-screen'>
                    <QueryClientProvider client={queryClient}>
                        <ApplicationRouter>
                            <React.StrictMode>
                                <Routes>
                                <Route element={<DefaultLayout/>}>
                                    <Route path="/" element={<MainPage/>}/>
                                    <Route path="/about" element={<AboutPage/>}/>
                                    <Route path="/tournament/:id" element={<TournamentPage/>}/>
                                    <Route path="/event/:city/:date" element={<EventPage/>}/>
                                    <Route path="/tournament/:id/round/:roundId" element={<TournamentPage />}/>
                                    <Route path="/tournament/:tournamentId/participant/:participantId"
                                           element={<ParticipantPage/>}/>
                                    <Route path="/tournament/:tournamentId/edit"
                                           element={<TournamentEditPage/>}
                                    />
                                    <Route path="/tournament/create" element={<TournamentCreatePage/>}/>
                                    <Route path="/login" element={<LoginPage/>}/>
                                    <Route path="/admin" element={<AdminPage/>}/>
                                    <Route path="/user" element={<UserProfilePage/>}/>
                                    <Route path="/user/:username" element={<UserProfilePage/>}/>
                                    <Route path="/user/me/edit" element={<UserProfileEditPage/>}/>
                                    <Route path="/users" element={<UsersPage/>}/>
                                    <Route path="/tournaments" element={<AllTournamentsPage/>}/>
                                    <Route path="/badges" element={<BadgesPage/>}/>
                                    <Route path="/badge/:badgeId" element={<BadgePage/>}/>
                                    <Route path="/privacyPolicy" element={<PrivacyPolicyPage/>}/>
                                    <Route path='*' element={<NotFoundPage/>}/>
                                    </Route>
                                    <Route path="/clock" element={<ChessClockPage/>}/>
                                </Routes>
                            </React.StrictMode>
                        </ApplicationRouter>
                    </QueryClientProvider>
                </div>
            </AuthenticatedUserContextProvider>
            </ConfigurationContextProvider>
        </LanguageContextProvider>
    );
}

function DefaultLayout() {
    return <>
                <Header/>
                <div className="grow">
                    <Outlet/>
                </div>
                <Footer/>
    </>;
}

export default App;
