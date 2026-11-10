import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { BookingProvider } from './context/BookingContext';
import { Navbar } from './components/Navbar';
import { Footer } from './components/Footer';
import { HomePage } from './pages/HomePage';
import { MovieDetailsPage } from './pages/MovieDetailsPage';
import { ShowtimeSelectionPage } from './pages/ShowtimeSelectionPage';
import { SeatSelectionPage } from './pages/SeatSelectionPage';
import { CheckoutPage } from './pages/CheckoutPage';
import { ConfirmationPage } from './pages/ConfirmationPage';
import { MyBookingsPage } from './pages/MyBookingsPage';

export const App: React.FC = () => {
  return (
    <BrowserRouter>
      <BookingProvider>
        <div className="min-h-screen flex flex-col bg-cinema-darkest text-cinema-text">
          <Navbar />
          <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 pt-8">
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/movies/:id" element={<MovieDetailsPage />} />
              <Route path="/showtimes" element={<ShowtimeSelectionPage />} />
              <Route path="/seats/:showId" element={<SeatSelectionPage />} />
              <Route path="/checkout" element={<CheckoutPage />} />
              <Route path="/confirmation/:bookingId" element={<ConfirmationPage />} />
              <Route path="/my-bookings" element={<MyBookingsPage />} />
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </main>
          <Footer />
        </div>
      </BookingProvider>
    </BrowserRouter>
  );
};

export default App;

