import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Film, Ticket, Calendar, Search, Menu, X, ShieldCheck } from 'lucide-react';

export const Navbar: React.FC = () => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const location = useLocation();

  const navLinks = [
    { name: 'Home', path: '/', icon: Film },
    { name: 'Movies', path: '/#movies', icon: Film },
    { name: 'Showtimes', path: '/showtimes', icon: Calendar },
    { name: 'My Bookings', path: '/my-bookings', icon: Ticket },
  ];

  const isActive = (path: string) => {
    if (path === '/') return location.pathname === '/';
    return location.pathname.startsWith(path);
  };

  return (
    <header className="sticky top-0 z-50 bg-cinema-darkest/90 backdrop-blur-md border-b border-cinema-border/70">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-20 flex items-center justify-between">
        {/* Brand Logo */}
        <Link to="/" className="flex items-center gap-3 group">
          <div className="w-11 h-11 rounded-xl bg-gradient-to-tr from-accent-purpleDark to-accent-purple flex items-center justify-center shadow-cinema-glow group-hover:scale-105 transition-transform duration-300">
            <Film className="w-6 h-6 text-white" />
          </div>
          <div>
            <span className="text-2xl font-extrabold tracking-tight bg-gradient-to-r from-white via-slate-100 to-accent-goldLight bg-clip-text text-transparent">
              CINE<span className="text-accent-gold">BOOK</span>
            </span>
            <span className="block text-[10px] uppercase font-bold tracking-widest text-accent-purpleLight">
              Cinema Platform
            </span>
          </div>
        </Link>

        {/* Desktop Nav */}
        <nav className="hidden md:flex items-center gap-1 bg-cinema-surface/60 border border-cinema-border/60 rounded-full px-4 py-1.5 shadow-inner">
          {navLinks.map((link) => {
            const Icon = link.icon;
            const active = isActive(link.path);
            return (
              <Link
                key={link.name}
                to={link.path}
                className={`flex items-center gap-2 px-4 py-2 rounded-full text-sm font-semibold transition-all duration-200 ${
                  active
                    ? 'bg-gradient-to-r from-accent-purple to-accent-purpleDark text-white shadow-md'
                    : 'text-cinema-muted hover:text-white hover:bg-cinema-card/50'
                }`}
              >
                <Icon className="w-4 h-4" />
                {link.name}
              </Link>
            );
          })}
        </nav>

        {/* Action Button */}
        <div className="hidden md:flex items-center gap-4">
          <Link
            to="/my-bookings"
            className="flex items-center gap-2 px-5 py-2.5 rounded-xl text-sm font-bold border border-accent-gold/40 text-accent-goldLight bg-accent-gold/10 hover:bg-accent-gold/20 hover:border-accent-gold transition-all duration-200 shadow-sm"
          >
            <Ticket className="w-4 h-4 text-accent-gold" />
            Check Booking
          </Link>
        </div>

        {/* Mobile menu button */}
        <div className="flex md:hidden">
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="p-2 rounded-lg bg-cinema-surface text-slate-300 hover:text-white border border-cinema-border"
          >
            {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
          </button>
        </div>
      </div>

      {/* Mobile Menu Dropdown */}
      {mobileMenuOpen && (
        <div className="md:hidden bg-cinema-surface border-b border-cinema-border px-4 pt-3 pb-6 space-y-2">
          {navLinks.map((link) => {
            const Icon = link.icon;
            return (
              <Link
                key={link.name}
                to={link.path}
                onClick={() => setMobileMenuOpen(false)}
                className="flex items-center gap-3 px-4 py-3 rounded-lg text-base font-medium text-slate-200 hover:bg-cinema-card hover:text-white"
              >
                <Icon className="w-5 h-5 text-accent-purple" />
                {link.name}
              </Link>
            );
          })}
          <div className="pt-2">
            <Link
              to="/my-bookings"
              onClick={() => setMobileMenuOpen(false)}
              className="w-full flex items-center justify-center gap-2 px-4 py-3 rounded-xl font-bold bg-accent-gold/15 text-accent-goldLight border border-accent-gold/40"
            >
              <Ticket className="w-5 h-5 text-accent-gold" />
              Manage Bookings
            </Link>
          </div>
        </div>
      )}
    </header>
  );
};

