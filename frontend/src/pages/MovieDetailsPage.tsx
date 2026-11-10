import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { Clock, Star, Calendar, Film, ArrowLeft, Ticket, ShieldAlert } from 'lucide-react';
import { Movie, Show } from '../types';
import { fetchMovieById, fetchShowsByMovieId } from '../api/client';
import { useBooking } from '../context/BookingContext';

export const MovieDetailsPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [movie, setMovie] = useState<Movie | null>(null);
  const [shows, setShows] = useState<Show[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const navigate = useNavigate();
  const { setSelectedMovie, setSelectedShow, clearSelectedSeats } = useBooking();

  useEffect(() => {
    if (!id) return;
    const loadDetails = async () => {
      try {
        setLoading(true);
        const [movieData, showsData] = await Promise.all([
          fetchMovieById(id),
          fetchShowsByMovieId(id),
        ]);
        setMovie(movieData);
        setShows(showsData);
        setSelectedMovie(movieData);
      } catch (err: any) {
        setError('Failed to load movie details. Please check the backend connection.');
      } finally {
        setLoading(false);
      }
    };
    loadDetails();
  }, [id, setSelectedMovie]);

  const handleSelectShow = (show: Show) => {
    setSelectedShow(show);
    clearSelectedSeats();
    navigate(`/seats/${show.id}`);
  };

  // Group shows by date
  const showsByDate = shows.reduce((acc, show) => {
    if (!acc[show.showDate]) {
      acc[show.showDate] = [];
    }
    acc[show.showDate].push(show);
    return acc;
  }, {} as Record<string, Show[]>);

  if (loading) {
    return (
      <div className="max-w-6xl mx-auto py-12 text-center text-slate-400">
        <div className="w-12 h-12 border-4 border-accent-purple border-t-transparent rounded-full animate-spin mx-auto mb-4" />
        <p>Loading movie & showtime schedule...</p>
      </div>
    );
  }

  if (error || !movie) {
    return (
      <div className="max-w-2xl mx-auto py-16 text-center space-y-4">
        <div className="p-6 bg-cinema-card border border-red-900/50 rounded-2xl text-red-400">
          <p className="font-semibold">{error || 'Movie not found.'}</p>
        </div>
        <Link to="/" className="inline-flex items-center gap-2 text-sm text-accent-purpleLight hover:underline">
          <ArrowLeft className="w-4 h-4" /> Back to Movies
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto space-y-12 pb-16">
      {/* Back Button */}
      <Link
        to="/"
        className="inline-flex items-center gap-2 text-xs font-bold text-slate-400 hover:text-white transition-colors bg-cinema-surface px-3 py-1.5 rounded-lg border border-cinema-border"
      >
        <ArrowLeft className="w-4 h-4" /> Back to Home
      </Link>

      {/* Hero Overview */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-8 sm:gap-12 bg-cinema-card/60 p-6 sm:p-10 rounded-3xl border border-cinema-border/70 shadow-2xl backdrop-blur-md">
        {/* Poster */}
        <div className="aspect-[2/3] rounded-2xl overflow-hidden shadow-2xl border border-cinema-border/50 bg-cinema-surface relative">
          <img
            src={movie.posterUrl}
            alt={movie.title}
            className="w-full h-full object-cover"
          />
          <div className="absolute top-3 right-3 flex items-center gap-1 px-3 py-1 rounded-md text-xs font-bold bg-cinema-darkest/90 text-accent-goldLight border border-cinema-border/60">
            <Star className="w-3.5 h-3.5 fill-accent-gold text-accent-gold" />
            {movie.rating}
          </div>
        </div>

        {/* Info */}
        <div className="md:col-span-2 space-y-5 flex flex-col justify-center">
          <div>
            <span className="text-xs font-extrabold uppercase tracking-widest text-accent-purpleLight">
              {movie.genre}
            </span>
            <h1 className="text-3xl sm:text-4xl font-black text-white tracking-tight mt-1">
              {movie.title}
            </h1>
          </div>

          <div className="flex flex-wrap items-center gap-4 text-xs font-semibold text-slate-300">
            <span className="flex items-center gap-1.5 bg-cinema-surface px-2.5 py-1 rounded-lg border border-cinema-border">
              <Clock className="w-3.5 h-3.5 text-accent-purple" />
              {movie.duration} mins
            </span>
            <span className="bg-cinema-surface px-2.5 py-1 rounded-lg border border-cinema-border uppercase">
              {movie.language}
            </span>
            <span className="bg-cinema-surface px-2.5 py-1 rounded-lg border border-cinema-border">
              Released: {movie.releaseDate}
            </span>
          </div>

          <div>
            <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400 mb-2">Synopsis</h3>
            <p className="text-sm text-slate-300 leading-relaxed">{movie.description}</p>
          </div>

          <div className="pt-2 flex flex-wrap gap-4 text-xs text-cinema-muted">
            <div className="p-3 bg-cinema-surface rounded-xl border border-cinema-border flex items-center gap-3">
              <Ticket className="w-5 h-5 text-accent-gold" />
              <div>
                <span className="block font-bold text-white">Single-Screen Cinema</span>
                <span className="text-[11px]">40 seats per screening</span>
              </div>
            </div>
            <div className="p-3 bg-cinema-surface rounded-xl border border-cinema-border flex items-center gap-3">
              <ShieldAlert className="w-5 h-5 text-accent-purpleLight" />
              <div>
                <span className="block font-bold text-white">80% Refund Policy</span>
                <span className="text-[11px]">Instant cancellation anytime</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Showtime Selection Section */}
      <div className="space-y-6">
        <div>
          <h2 className="text-2xl font-black text-white tracking-tight flex items-center gap-2">
            <Calendar className="w-6 h-6 text-accent-purple" />
            Select Showtime
          </h2>
          <p className="text-xs text-cinema-muted mt-1">Choose a slot to view live seat availability</p>
        </div>

        {Object.keys(showsByDate).length === 0 ? (
          <div className="p-8 text-center bg-cinema-card rounded-2xl border border-cinema-border">
            <p className="text-sm text-slate-400">No active showtimes currently scheduled for this movie.</p>
          </div>
        ) : (
          <div className="space-y-8">
            {Object.entries(showsByDate).map(([date, dateShows]) => (
              <div key={date} className="bg-cinema-card/50 p-6 rounded-2xl border border-cinema-border/70 space-y-4">
                <div className="flex items-center gap-2 text-sm font-bold text-accent-goldLight">
                  <Calendar className="w-4 h-4 text-accent-gold" />
                  <span>Screenings for {date}</span>
                </div>

                <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-3">
                  {dateShows.map((show) => {
                    const isSoldOut = show.availableSeats === 0;
                    return (
                      <button
                        key={show.id}
                        onClick={() => handleSelectShow(show)}
                        disabled={isSoldOut}
                        className={`group p-4 rounded-xl border text-left transition-all duration-200 flex flex-col justify-between ${
                          isSoldOut
                            ? 'bg-cinema-surface/40 border-cinema-border/40 opacity-50 cursor-not-allowed'
                            : 'bg-cinema-surface border-cinema-border hover:border-accent-purple hover:bg-cinema-surface/80 hover:shadow-cinema-glow'
                        }`}
                      >
                        <div>
                          <span className="text-base font-extrabold text-white group-hover:text-accent-gold transition-colors">
                            {show.showTime}
                          </span>
                          <span className="block text-[11px] text-slate-400 mt-1">
                            Screen 1 (Single)
                          </span>
                        </div>
                        <div className="mt-3 pt-2 border-t border-cinema-border/40 flex items-center justify-between text-[11px]">
                          <span className={isSoldOut ? 'text-red-400 font-bold' : 'text-emerald-400 font-semibold'}>
                            {isSoldOut ? 'Sold Out' : `${show.availableSeats} seats left`}
                          </span>
                        </div>
                      </button>
                    );
                  })}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

