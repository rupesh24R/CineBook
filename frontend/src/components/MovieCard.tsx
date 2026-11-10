import React from 'react';
import { Link } from 'react-router-dom';
import { Clock, Star, Film, ChevronRight } from 'lucide-react';
import { Movie } from '../types';

interface MovieCardProps {
  movie: Movie;
}

export const MovieCard: React.FC<MovieCardProps> = ({ movie }) => {
  return (
    <div className="group relative bg-cinema-card rounded-2xl overflow-hidden border border-cinema-border/70 hover:border-accent-purple/60 transition-all duration-300 hover:shadow-cinema-glow flex flex-col">
      {/* Poster Image Container */}
      <div className="relative aspect-[2/3] w-full overflow-hidden bg-cinema-surface">
        <img
          src={movie.posterUrl}
          alt={movie.title}
          className="w-full h-full object-cover object-center group-hover:scale-105 transition-transform duration-500"
          loading="lazy"
        />

        {/* Gradient Overlay */}
        <div className="absolute inset-0 bg-gradient-to-t from-cinema-card via-cinema-card/20 to-transparent opacity-80 group-hover:opacity-60 transition-opacity" />

        {/* Status Badge */}
        <div className="absolute top-3 left-3">
          <span
            className={`px-2.5 py-1 rounded-md text-[11px] font-extrabold tracking-wider uppercase backdrop-blur-md ${
              movie.status === 'NOW_SHOWING'
                ? 'bg-emerald-500/80 text-white'
                : 'bg-accent-gold/80 text-black'
            }`}
          >
            {movie.status === 'NOW_SHOWING' ? 'Now Showing' : 'Upcoming'}
          </span>
        </div>

        {/* Rating Badge */}
        <div className="absolute top-3 right-3 flex items-center gap-1 px-2.5 py-1 rounded-md text-xs font-bold bg-cinema-darkest/80 text-accent-goldLight border border-cinema-border/50 backdrop-blur-md">
          <Star className="w-3.5 h-3.5 fill-accent-gold text-accent-gold" />
          <span>{movie.rating}</span>
        </div>

        {/* Quick Specs overlay at bottom of poster */}
        <div className="absolute bottom-3 left-3 right-3 flex items-center justify-between text-xs font-medium text-slate-300">
          <span className="flex items-center gap-1.5 bg-black/50 px-2 py-0.5 rounded backdrop-blur-sm">
            <Clock className="w-3.5 h-3.5 text-accent-purpleLight" />
            {movie.duration}m
          </span>
          <span className="bg-black/50 px-2 py-0.5 rounded backdrop-blur-sm uppercase tracking-wider text-[11px]">
            {movie.language}
          </span>
        </div>
      </div>

      {/* Movie Details */}
      <div className="p-5 flex-1 flex flex-col justify-between">
        <div>
          <h3 className="text-lg font-bold text-white group-hover:text-accent-gold transition-colors line-clamp-1">
            {movie.title}
          </h3>
          <p className="text-xs font-semibold text-accent-purpleLight mt-1 tracking-wide line-clamp-1">
            {movie.genre}
          </p>
          <p className="text-xs text-cinema-muted mt-2.5 line-clamp-2 leading-relaxed">
            {movie.description}
          </p>
        </div>

        <div className="mt-5 pt-4 border-t border-cinema-border/60">
          <Link
            to={`/movies/${movie.id}`}
            className="w-full flex items-center justify-center gap-2 py-2.5 px-4 rounded-xl font-bold text-sm bg-gradient-to-r from-accent-purpleDark to-accent-purple text-white hover:brightness-110 active:scale-[0.98] transition-all shadow-md group-hover:shadow-cinema-glow"
          >
            Book Tickets
            <ChevronRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
          </Link>
        </div>
      </div>
    </div>
  );
};

