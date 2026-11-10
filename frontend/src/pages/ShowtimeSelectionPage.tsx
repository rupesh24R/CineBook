import React, { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Calendar, Clock, Film, Search, Star, Ticket, ArrowRight } from 'lucide-react';
import { Show } from '../types';
import { fetchAllShows } from '../api/client';
import { useBooking } from '../context/BookingContext';

export const ShowtimeSelectionPage: React.FC = () => {
  const [shows, setShows] = useState<Show[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedDate, setSelectedDate] = useState<string>('ALL');

  const navigate = useNavigate();
  const { setSelectedShow, clearSelectedSeats } = useBooking();

  useEffect(() => {
    const loadShows = async () => {
      try {
        setLoading(true);
        const data = await fetchAllShows();
        setShows(data);
      } catch (err: any) {
        setError('Failed to fetch showtimes. Please check the backend connection.');
      } finally {
        setLoading(false);
      }
    };
    loadShows();
  }, []);

  const handleSelectShow = (show: Show) => {
    setSelectedShow(show);
    clearSelectedSeats();
    navigate(`/seats/${show.id}`);
  };

  const uniqueDates = Array.from(new Set(shows.map((s) => s.showDate)));

  const filteredShows = shows.filter((show) => {
    const matchesSearch =
      show.movieTitle.toLowerCase().includes(searchQuery.toLowerCase()) ||
      show.showTime.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesDate = selectedDate === 'ALL' || show.showDate === selectedDate;
    return matchesSearch && matchesDate;
  });

  return (
    <div className="max-w-6xl mx-auto space-y-8 pb-16">
      {/* Page Header */}
      <div>
        <h1 className="text-3xl font-black text-white tracking-tight flex items-center gap-3">
          <Calendar className="w-8 h-8 text-accent-gold" />
          Cinema Showtimes Schedule
        </h1>
        <p className="text-sm text-cinema-muted mt-1.5">
          Select your screening slot to view real-time seat availability for our single-screen auditorium.
        </p>
      </div>

      {/* Filter and Search Bar */}
      <div className="flex flex-col sm:flex-row items-center gap-4 bg-cinema-card p-4 rounded-2xl border border-cinema-border/70">
        {/* Search */}
        <div className="relative flex-1 w-full">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
          <input
            type="text"
            placeholder="Search by movie title or showtime..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-cinema-surface border border-cinema-border rounded-xl pl-10 pr-4 py-2 text-sm text-white placeholder:text-slate-500 focus:outline-none focus:border-accent-purple"
          />
        </div>

        {/* Date Filter Buttons */}
        <div className="flex items-center gap-2 overflow-x-auto w-full sm:w-auto pb-1 sm:pb-0">
          <button
            onClick={() => setSelectedDate('ALL')}
            className={`px-3.5 py-2 rounded-xl text-xs font-bold transition-colors whitespace-nowrap ${
              selectedDate === 'ALL'
                ? 'bg-accent-purple text-white shadow-md'
                : 'bg-cinema-surface text-slate-300 hover:bg-slate-800 border border-cinema-border'
            }`}
          >
            All Dates
          </button>
          {uniqueDates.map((date) => (
            <button
              key={date}
              onClick={() => setSelectedDate(date)}
              className={`px-3.5 py-2 rounded-xl text-xs font-bold transition-colors whitespace-nowrap ${
                selectedDate === date
                  ? 'bg-accent-purple text-white shadow-md'
                  : 'bg-cinema-surface text-slate-300 hover:bg-slate-800 border border-cinema-border'
              }`}
            >
              {date}
            </button>
          ))}
        </div>
      </div>

      {/* Shows List */}
      {loading ? (
        <div className="py-16 text-center text-slate-400">
          <div className="w-10 h-10 border-4 border-accent-purple border-t-transparent rounded-full animate-spin mx-auto mb-4" />
          <p>Loading showtime schedule...</p>
        </div>
      ) : error ? (
        <div className="p-8 text-center bg-cinema-card rounded-2xl border border-red-900/50 text-red-400">
          {error}
        </div>
      ) : filteredShows.length === 0 ? (
        <div className="p-12 text-center bg-cinema-card rounded-2xl border border-cinema-border text-slate-400 space-y-2">
          <p className="font-semibold text-white">No showtimes match your filter.</p>
          <p className="text-xs">Try selecting 'All Dates' or resetting your search query.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {filteredShows.map((show) => {
            const isSoldOut = show.availableSeats === 0;
            return (
              <div
                key={show.id}
                className="bg-cinema-card/70 border border-cinema-border/70 hover:border-accent-purple/60 rounded-2xl p-5 flex gap-4 items-center justify-between transition-all duration-300 hover:shadow-cinema-glow"
              >
                {/* Poster Thumbnail */}
                <img
                  src={show.moviePosterUrl}
                  alt={show.movieTitle}
                  className="w-20 h-28 object-cover rounded-xl border border-cinema-border/50 shrink-0"
                />

                {/* Show Details */}
                <div className="flex-1 space-y-1.5 min-w-0">
                  <span className="text-[11px] font-bold uppercase tracking-wider text-accent-purpleLight flex items-center gap-1">
                    <Calendar className="w-3 h-3 text-accent-gold" />
                    {show.showDate}
                  </span>
                  <h3 className="text-lg font-bold text-white truncate">{show.movieTitle}</h3>
                  <div className="flex items-center gap-3 text-xs text-cinema-muted">
                    <span className="flex items-center gap-1 font-extrabold text-white bg-cinema-surface px-2 py-0.5 rounded border border-cinema-border">
                      <Clock className="w-3.5 h-3.5 text-accent-gold" />
                      {show.showTime}
                    </span>
                    <span>{show.movieDuration}m</span>
                    <span className="text-accent-goldLight font-semibold">{show.movieRating}</span>
                  </div>
                  <div className="pt-1 text-xs">
                    <span className={isSoldOut ? 'text-red-400 font-bold' : 'text-emerald-400 font-semibold'}>
                      {isSoldOut ? 'Sold Out' : `${show.availableSeats} of ${show.totalSeats} seats available`}
                    </span>
                  </div>
                </div>

                {/* Action */}
                <button
                  onClick={() => handleSelectShow(show)}
                  disabled={isSoldOut}
                  className="px-4 py-2.5 rounded-xl text-xs font-bold bg-accent-purple hover:bg-accent-purpleDark text-white disabled:opacity-40 disabled:cursor-not-allowed flex items-center gap-1.5 transition-all shadow-md shrink-0"
                >
                  <Ticket className="w-3.5 h-3.5" />
                  Select
                </button>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};

