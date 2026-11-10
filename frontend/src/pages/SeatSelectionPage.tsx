import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, Clock, Calendar, Ticket, AlertCircle } from 'lucide-react';
import { Seat, Show } from '../types';
import { fetchSeatsByShowId, fetchShowById } from '../api/client';
import { useBooking } from '../context/BookingContext';
import { SeatGrid } from '../components/SeatGrid';
import { SeatLegend } from '../components/SeatLegend';
import { BookingSummaryCard } from '../components/BookingSummaryCard';

export const SeatSelectionPage: React.FC = () => {
  const { showId } = useParams<{ showId: string }>();
  const [seats, setSeats] = useState<Seat[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const { selectedShow, setSelectedShow } = useBooking();

  useEffect(() => {
    if (!showId) return;

    const loadShowAndSeats = async () => {
      try {
        setLoading(true);
        // Load show if not already present in context
        if (!selectedShow || selectedShow.id !== Number(showId)) {
          const showData = await fetchShowById(showId);
          setSelectedShow(showData);
        }

        const seatsData = await fetchSeatsByShowId(showId);
        setSeats(seatsData);
      } catch (err: any) {
        setError('Failed to load seat availability. Please check the backend connection.');
      } finally {
        setLoading(false);
      }
    };

    loadShowAndSeats();
  }, [showId, selectedShow, setSelectedShow]);

  if (loading) {
    return (
      <div className="max-w-6xl mx-auto py-20 text-center text-slate-400">
        <div className="w-12 h-12 border-4 border-accent-purple border-t-transparent rounded-full animate-spin mx-auto mb-4" />
        <p className="text-base font-semibold text-white">Loading interactive seat map...</p>
        <p className="text-xs text-slate-500 mt-1">Acquiring current seat locks for Screen 1</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-2xl mx-auto py-16 text-center space-y-4">
        <div className="p-6 bg-cinema-card border border-red-900/50 rounded-2xl text-red-400 flex flex-col items-center gap-2">
          <AlertCircle className="w-8 h-8 text-red-400" />
          <p className="font-semibold">{error}</p>
        </div>
        <Link to="/showtimes" className="inline-flex items-center gap-2 text-sm text-accent-purpleLight hover:underline">
          <ArrowLeft className="w-4 h-4" /> Back to Showtimes
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto space-y-8 pb-16">
      {/* Navigation & Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-cinema-border/70 pb-5">
        <div className="flex items-center gap-4">
          <Link
            to={selectedShow ? `/movies/${selectedShow.movieId}` : '/showtimes'}
            className="p-2 rounded-xl bg-cinema-surface hover:bg-slate-800 border border-cinema-border text-slate-300 hover:text-white transition-colors"
          >
            <ArrowLeft className="w-5 h-5" />
          </Link>
          <div>
            <h1 className="text-2xl font-black text-white tracking-tight flex items-center gap-2">
              <Ticket className="w-6 h-6 text-accent-purple" />
              {selectedShow?.movieTitle || 'Movie'} – Select Seats
            </h1>
            <div className="flex flex-wrap items-center gap-3 text-xs text-cinema-muted mt-1 font-semibold">
              <span className="flex items-center gap-1 text-slate-200">
                <Calendar className="w-3.5 h-3.5 text-accent-gold" />
                {selectedShow?.showDate}
              </span>
              <span>•</span>
              <span className="flex items-center gap-1 text-slate-200">
                <Clock className="w-3.5 h-3.5 text-accent-gold" />
                {selectedShow?.showTime}
              </span>
              <span>•</span>
              <span className="text-accent-purpleLight font-bold">Screen 1 (Single-Screen)</span>
            </div>
          </div>
        </div>

        <div className="flex items-center gap-2 self-start sm:self-auto">
          <span className="px-3 py-1 rounded-full text-xs font-bold bg-accent-gold/15 text-accent-gold border border-accent-gold/30">
            5 × 8 Auditorium Layout
          </span>
        </div>
      </div>

      {/* Main Seat Selection Grid & Live Summary */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        {/* Left Col: Screen, Seat Map, and Legend */}
        <div className="lg:col-span-8 space-y-6">
          <SeatGrid seats={seats} />
          <SeatLegend />
        </div>

        {/* Right Col: Sticky Live Checkout Summary */}
        <div className="lg:col-span-4">
          <BookingSummaryCard />
        </div>
      </div>
    </div>
  );
};

