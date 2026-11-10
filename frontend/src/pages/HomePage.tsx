import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Film, Sparkles, Star, Calendar, Shield, Ticket, ArrowRight, Award } from 'lucide-react';
import { Movie } from '../types';
import { fetchMovies } from '../api/client';
import { MovieCard } from '../components/MovieCard';

export const HomePage: React.FC = () => {
  const [movies, setMovies] = useState<Movie[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadMovies = async () => {
      try {
        setLoading(true);
        const data = await fetchMovies();
        setMovies(data);
      } catch (err: any) {
        setError('Failed to connect to backend server. Make sure the Spring Boot API is running.');
      } finally {
        setLoading(false);
      }
    };
    loadMovies();
  }, []);

  const nowShowing = movies.filter((m) => m.status === 'NOW_SHOWING');
  const upcoming = movies.filter((m) => m.status === 'UPCOMING');

  const heroMovie = nowShowing[0] || movies[0];

  return (
    <div className="space-y-16 pb-12">
      {/* Hero Showcase Section */}
      {heroMovie && (
        <section className="relative rounded-3xl overflow-hidden border border-cinema-border/80 min-h-[480px] sm:min-h-[540px] flex items-end p-6 sm:p-12 shadow-2xl">
          {/* Backdrop Image with Multiple Gradients */}
          <div className="absolute inset-0 z-0">
            <img
              src={heroMovie.posterUrl}
              alt={heroMovie.title}
              className="w-full h-full object-cover object-center brightness-75 scale-105"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-cinema-darkest via-cinema-darkest/75 to-transparent" />
            <div className="absolute inset-0 bg-gradient-to-r from-cinema-darkest/95 via-cinema-darkest/60 to-transparent" />
          </div>

          {/* Hero Content */}
          <div className="relative z-10 max-w-2xl space-y-4">
            <div className="inline-flex items-center gap-2 px-3.5 py-1 rounded-full text-xs font-extrabold uppercase tracking-wider bg-accent-gold/20 text-accent-gold border border-accent-gold/40 backdrop-blur-md">
              <Sparkles className="w-3.5 h-3.5" />
              Featured Presentation
            </div>

            <h1 className="text-3xl sm:text-5xl font-black text-white tracking-tight leading-tight">
              {heroMovie.title}
            </h1>

            <div className="flex flex-wrap items-center gap-4 text-xs font-semibold text-slate-300">
              <span className="flex items-center gap-1 text-accent-goldLight">
                <Star className="w-4 h-4 fill-accent-gold text-accent-gold" />
                {heroMovie.rating}
              </span>
              <span>•</span>
              <span>{heroMovie.duration} mins</span>
              <span>•</span>
              <span className="text-accent-purpleLight">{heroMovie.genre}</span>
              <span>•</span>
              <span className="uppercase">{heroMovie.language}</span>
            </div>

            <p className="text-sm text-slate-300 line-clamp-3 leading-relaxed max-w-xl">
              {heroMovie.description}
            </p>

            <div className="flex flex-wrap items-center gap-4 pt-2">
              <Link
                to={`/movies/${heroMovie.id}`}
                className="flex items-center gap-2 px-6 py-3.5 rounded-xl font-bold text-sm bg-gradient-to-r from-accent-purpleDark via-accent-purple to-accent-gold text-white hover:brightness-110 active:scale-95 transition-all shadow-cinema-glow"
              >
                <Ticket className="w-4 h-4" />
                Select Showtime & Seats
              </Link>
              <Link
                to="/showtimes"
                className="flex items-center gap-2 px-5 py-3.5 rounded-xl font-bold text-sm bg-cinema-surface/80 hover:bg-cinema-surface border border-cinema-border text-slate-200 hover:text-white transition-all backdrop-blur-md"
              >
                <Calendar className="w-4 h-4 text-accent-gold" />
                View All Showtimes
              </Link>
            </div>
          </div>
        </section>
      )}

      {/* Promotional Banner */}
      <section className="bg-gradient-to-r from-accent-purpleDark/30 via-cinema-card to-accent-goldDark/20 border border-accent-purple/30 rounded-2xl p-6 flex flex-col sm:flex-row items-center justify-between gap-4 shadow-lg">
        <div className="flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-accent-gold/20 border border-accent-gold/40 flex items-center justify-center text-accent-gold shrink-0">
            <Award className="w-6 h-6" />
          </div>
          <div>
            <h4 className="font-extrabold text-white text-base">Special Promo: Get 10% Off Your Tickets</h4>
            <p className="text-xs text-cinema-muted">
              Apply code <span className="font-mono font-bold text-accent-goldLight bg-accent-gold/15 px-2 py-0.5 rounded border border-accent-gold/30">CINE10</span> during checkout for instant discount.
            </p>
          </div>
        </div>
        <Link
          to="/showtimes"
          className="whitespace-nowrap px-4 py-2 rounded-xl text-xs font-bold bg-accent-purple hover:bg-accent-purpleDark text-white transition-colors"
        >
          Book Now
        </Link>
      </section>

      {/* Now Showing Section */}
      <section id="movies" className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-2xl font-black text-white tracking-tight">Now Showing</h2>
            <p className="text-xs text-cinema-muted mt-1">Book your tickets for today's screenings</p>
          </div>
          <Link to="/showtimes" className="text-xs font-bold text-accent-purpleLight hover:text-white flex items-center gap-1">
            See all showtimes <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>

        {loading ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
            {[1, 2, 3, 4].map((n) => (
              <div key={n} className="aspect-[2/3] bg-cinema-card/50 rounded-2xl animate-pulse border border-cinema-border/50" />
            ))}
          </div>
        ) : error ? (
          <div className="p-8 text-center bg-cinema-card rounded-2xl border border-red-900/50">
            <p className="text-sm text-red-400 font-semibold">{error}</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
            {nowShowing.map((movie) => (
              <MovieCard key={movie.id} movie={movie} />
            ))}
          </div>
        )}
      </section>

      {/* Upcoming Releases */}
      {upcoming.length > 0 && (
        <section className="space-y-6 pt-6">
          <div>
            <h2 className="text-2xl font-black text-white tracking-tight">Coming Soon</h2>
            <p className="text-xs text-cinema-muted mt-1">Exciting blockbusters heading to CineBook</p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
            {upcoming.map((movie) => (
              <MovieCard key={movie.id} movie={movie} />
            ))}
          </div>
        </section>
      )}

      {/* Cinema Experience Pillars */}
      <section className="grid grid-cols-1 md:grid-cols-3 gap-6 pt-8">
        <div className="p-6 rounded-2xl bg-cinema-surface/70 border border-cinema-border/80 space-y-3">
          <div className="w-10 h-10 rounded-xl bg-accent-purple/20 text-accent-purpleLight flex items-center justify-center font-bold">
            01
          </div>
          <h3 className="font-extrabold text-white text-base">Tiered Luxury Seating</h3>
          <p className="text-xs text-cinema-muted leading-relaxed">
            Choose between Regular (Rs. 150), Premium (Rs. 250), and VIP Recliners (Rs. 400). Live 5x8 interactive seating maps ensure you pick the perfect view.
          </p>
        </div>

        <div className="p-6 rounded-2xl bg-cinema-surface/70 border border-cinema-border/80 space-y-3">
          <div className="w-10 h-10 rounded-xl bg-accent-gold/20 text-accent-gold flex items-center justify-center font-bold">
            02
          </div>
          <h3 className="font-extrabold text-white text-base">Atomic Double-Booking Guard</h3>
          <p className="text-xs text-cinema-muted leading-relaxed">
            Our Java Spring Boot engine utilizes pessimistic database row locks, guaranteeing no seat can ever be double-booked even during peak release rushes.
          </p>
        </div>

        <div className="p-6 rounded-2xl bg-cinema-surface/70 border border-cinema-border/80 space-y-3">
          <div className="w-10 h-10 rounded-xl bg-emerald-500/20 text-emerald-400 flex items-center justify-center font-bold">
            03
          </div>
          <h3 className="font-extrabold text-white text-base">80% Instant Cancellation Refund</h3>
          <p className="text-xs text-cinema-muted leading-relaxed">
            Plans change? Cancel any booking effortlessly with your Booking ID and receive an automatic 80% refund on the amount actually paid.
          </p>
        </div>
      </section>
    </div>
  );
};

