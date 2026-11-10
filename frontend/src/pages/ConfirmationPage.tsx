import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { CheckCircle2, Ticket, Printer, Calendar, Clock, Film, ShieldCheck, Copy, Check, Home } from 'lucide-react';
import confetti from 'canvas-confetti';
import { Booking } from '../types';
import { fetchBookingById } from '../api/client';

export const ConfirmationPage: React.FC = () => {
  const { bookingId } = useParams<{ bookingId: string }>();
  const [booking, setBooking] = useState<Booking | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [copied, setCopied] = useState(false);

  useEffect(() => {
    if (!bookingId) return;

    // Trigger confetti on arrival
    confetti({
      particleCount: 80,
      spread: 70,
      origin: { y: 0.6 },
      colors: ['#8b5cf6', '#f59e0b', '#10b981', '#ffffff'],
    });

    const loadBooking = async () => {
      try {
        setLoading(true);
        const data = await fetchBookingById(bookingId);
        setBooking(data);
      } catch (err: any) {
        setError('Failed to retrieve booking confirmation.');
      } finally {
        setLoading(false);
      }
    };

    loadBooking();
  }, [bookingId]);

  const copyBookingId = () => {
    if (!booking) return;
    navigator.clipboard.writeText(booking.bookingId);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handlePrint = () => {
    window.print();
  };

  if (loading) {
    return (
      <div className="max-w-xl mx-auto py-20 text-center text-slate-400">
        <div className="w-10 h-10 border-4 border-accent-purple border-t-transparent rounded-full animate-spin mx-auto mb-4" />
        <p>Generating your cinema boarding pass...</p>
      </div>
    );
  }

  if (error || !booking) {
    return (
      <div className="max-w-md mx-auto py-16 text-center space-y-4">
        <div className="p-6 bg-cinema-card border border-red-900/50 rounded-2xl text-red-400">
          <p>{error || 'Booking record could not be found.'}</p>
        </div>
        <Link to="/" className="inline-flex items-center gap-2 text-xs font-bold text-accent-purpleLight hover:underline">
          Return to Home
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto space-y-8 pb-16">
      {/* Success Banner */}
      <div className="text-center space-y-2">
        <div className="w-14 h-14 bg-emerald-500/20 text-emerald-400 border border-emerald-500/40 rounded-full flex items-center justify-center mx-auto mb-3 shadow-[0_0_25px_rgba(16,185,129,0.3)]">
          <CheckCircle2 className="w-8 h-8" />
        </div>
        <h1 className="text-3xl font-black text-white tracking-tight">Booking Confirmed!</h1>
        <p className="text-xs text-cinema-muted">
          Your reservation is locked in the system. Present this pass at Screen 1 entrance.
        </p>
      </div>

      {/* Digital Ticket Pass Card */}
      <div className="bg-gradient-to-b from-cinema-card to-cinema-surface rounded-3xl border border-cinema-border/90 overflow-hidden shadow-2xl relative">
        {/* Ticket Top Header */}
        <div className="p-6 bg-gradient-to-r from-accent-purpleDark via-accent-purple to-cinema-card text-white flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Film className="w-6 h-6 text-accent-gold" />
            <span className="font-extrabold text-lg tracking-wider">CINEBOOK PASS</span>
          </div>
          <span className="px-3 py-1 rounded-full text-[11px] font-black uppercase tracking-wider bg-black/40 text-emerald-300 border border-emerald-400/40 backdrop-blur-sm">
            {booking.bookingStatus}
          </span>
        </div>

        {/* Ticket Body */}
        <div className="p-6 sm:p-8 space-y-6">
          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 pb-6 border-b border-dashed border-cinema-border">
            <div>
              <span className="text-[11px] font-bold uppercase tracking-wider text-slate-400">Booking Reference</span>
              <div className="flex items-center gap-2 mt-1">
                <span className="text-2xl font-black font-mono text-accent-gold">{booking.bookingId}</span>
                <button
                  onClick={copyBookingId}
                  className="p-1.5 rounded-lg bg-cinema-surface hover:bg-slate-700 text-slate-300 transition-colors"
                  title="Copy Booking ID"
                >
                  {copied ? <Check className="w-4 h-4 text-emerald-400" /> : <Copy className="w-4 h-4" />}
                </button>
              </div>
            </div>

            <div className="text-left sm:text-right">
              <span className="text-[11px] font-bold uppercase tracking-wider text-slate-400">Auditorium</span>
              <p className="text-base font-extrabold text-white">Screen 1 (Single)</p>
            </div>
          </div>

          {/* Movie Details */}
          <div className="space-y-4">
            <div>
              <span className="text-[11px] font-bold uppercase tracking-wider text-accent-purpleLight">Movie</span>
              <h2 className="text-2xl font-black text-white">{booking.movieTitle}</h2>
            </div>

            <div className="grid grid-cols-2 sm:grid-cols-3 gap-4 text-xs">
              <div>
                <span className="text-slate-400 block mb-1">Date</span>
                <span className="font-bold text-slate-200 flex items-center gap-1.5">
                  <Calendar className="w-4 h-4 text-accent-gold" />
                  {booking.showDate}
                </span>
              </div>
              <div>
                <span className="text-slate-400 block mb-1">Showtime</span>
                <span className="font-bold text-slate-200 flex items-center gap-1.5">
                  <Clock className="w-4 h-4 text-accent-gold" />
                  {booking.showTime}
                </span>
              </div>
              <div>
                <span className="text-slate-400 block mb-1">Seats Reserved</span>
                <span className="font-bold text-white flex flex-wrap gap-1">
                  {booking.seatCodes.map((c) => (
                    <span key={c} className="px-2 py-0.5 rounded bg-accent-purple/20 text-accent-purpleLight border border-accent-purple/40 font-black">
                      {c}
                    </span>
                  ))}
                </span>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4 text-xs pt-3 border-t border-cinema-border/60">
              <div>
                <span className="text-slate-400 block">Category</span>
                <span className="font-semibold text-slate-200">{booking.seatCategories || 'Regular'}</span>
              </div>
              <div className="text-right">
                <span className="text-slate-400 block">Total Amount Paid</span>
                <span className="text-xl font-black text-accent-gold">Rs. {Number(booking.totalAmount).toFixed(2)}</span>
              </div>
            </div>
          </div>

          {/* Barcode / Perforation Decorative Footer */}
          <div className="pt-6 border-t border-dashed border-cinema-border text-center space-y-2">
            <div className="h-10 bg-[repeating-linear-gradient(90deg,#94a3b8,#94a3b8_2px,transparent_2px,transparent_6px)] opacity-40 mx-auto max-w-sm rounded" />
            <p className="text-[10px] font-mono text-slate-500 uppercase tracking-widest">
              VALID FOR ONE-TIME ADMISSION • CINEBOOK TICKETING SYSTEM
            </p>
          </div>
        </div>
      </div>

      {/* Action Buttons */}
      <div className="flex flex-wrap items-center justify-center gap-4">
        <button
          onClick={handlePrint}
          className="flex items-center gap-2 px-5 py-2.5 rounded-xl text-xs font-bold bg-cinema-surface hover:bg-cinema-card border border-cinema-border text-white transition-colors"
        >
          <Printer className="w-4 h-4 text-accent-gold" />
          Print Ticket Pass
        </button>

        <Link
          to="/my-bookings"
          className="flex items-center gap-2 px-5 py-2.5 rounded-xl text-xs font-bold bg-accent-purple hover:bg-accent-purpleDark text-white transition-colors"
        >
          <Ticket className="w-4 h-4" />
          View All Bookings
        </Link>

        <Link
          to="/"
          className="flex items-center gap-2 px-4 py-2.5 rounded-xl text-xs font-bold text-slate-400 hover:text-white transition-colors"
        >
          <Home className="w-4 h-4" />
          Home
        </Link>
      </div>
    </div>
  );
};

