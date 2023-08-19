import React from 'react';
import './App.css';
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";
import MainPage from "pages/MainPage"
import TournamentPage from "pages/TournamentPage"
import Header from "components/Header";

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
                <Route path="/tournament/:id/round/:roundId" element={<TournamentPage/>}/>
                <Route path='/f' element={<Navigate to="/files"/>}/>
                {/*<Route path="/files/*" element={<FilesPage/>}/>*/}
                {/*<Route path="/edit/*" element={<FileEditPage/>}/>*/}
              </Routes>
            </React.StrictMode>
          </BrowserRouter>
        </QueryClientProvider>
      </div>
  );
}

export default App;
