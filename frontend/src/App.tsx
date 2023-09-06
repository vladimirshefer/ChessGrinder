import React from 'react';
import './App.css';
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import MainPage from "pages/MainPage"
import TournamentPage from "pages/TournamentPage"
import Header from "components/Header";
import AdminPage from "pages/AdminPage";

const queryClient = new QueryClient()

function App() {
  return (
      <div className='App'>
        <QueryClientProvider client={queryClient}>
          <BrowserRouter>
            <React.StrictMode>
              <Header/>
              <Routes>
                <Route path="/" element={<MainPage/>}/>
                <Route path="/tournament/:id" element={<TournamentPage/>}/>
                <Route path="/admin" element={<AdminPage/>}/>
              </Routes>
            </React.StrictMode>
          </BrowserRouter>
        </QueryClientProvider>
      </div>
  );
}

export default App;
