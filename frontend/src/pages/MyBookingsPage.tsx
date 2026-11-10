import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Ticket, Search, Calendar, Clock, Film, AlertTriangle, CheckCircle, X, ShieldAlert, ArrowRight } from 'lucide-react';
import { Booking, CancellationResult } from '../types';
import { fetchAllBookings, cancelBooking, fetchBookingById } from '../api/client';

export const MyBookingsPage: React.FC = () => {
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchId, setSearchId] = useState('');
  const [searchResult, setSearchResult] = useState<Booking | null>(null);
  const [searchError, setSearchError] = useState<string | null>(null);

  // Cancellation modal state
  const [cancellingBooking, setCancellingBooking] = useState<Booking | null>(null);
  const [cancelLoading, setCancelLoading] = useState(false);
  const [cancellationResult, setCancellationResult] = useState<CancellationResult | null>(null);

  const loadBookings = async () => {
    try {
      setLoading(true);
      const data = await fetchAllBookings();
      setBookings(data);
    } catch (err: any) {
      setError('Could not load booking history. Make sure backend is running.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadBookings();
  }, []);

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!searchId.trim()) return;

    setSearchError(null);
    setSearchResult(null);

    try {
      const result = await fetchBookingById(searchId.trim().toUpperCase());
      setSearchResult(result);
    } catch (err: any) {
      setSearchError(err.response?.data?.message || `No booking found with ID: ${searchId.toUpperCase()}`);
    }
  };

  const handleOpenCancelModal = (booking: Booking) => {
    setCancellingBooking(booking);
    setCancellationResult(null);
  };

  const handleConfirmCancel = async () => {
    if (!cancellingBooking) return;

    setCancelLoading(true);
    try {
      const result = await cancelBooking(cancellingBooking.bookingId);
      setCancellationResult(result);

      // Update state locally
      setBookings((prev) =>
        prev.map((b) =>
          b.bookingId === cancellingBooking.bookingId
            ? { ...b, bookingStatus: 'CANCELLED', cancelledAt: result.cancelledAt }
            : b
        )
      );

      if (searchResult && searchResult.bookingId === cancellingBooking.bookingId) {
        setSearchResult({
          ...searchResult,
          bookingStatus: 'CANCELLED',
          cancelledAt: result.cancelledAt,
        });
      }
    } catch (err: any) {
      alert(err.response?.data?.message || 'Cancellation failed.');
    } finally {
      setCancelLoading(false);
    }
  };

  return (
    <div className="max-w-6xl mx-auto space-y-10 pb-16">
      {/* Page Header */}
      <div>
        <h1 className="text-3xl font-black text-white tracking-tight flex items-center gap-3">
          <Ticket className="w-8 h-8 text-accent-gold" />
          Booking History & Management
        </h1>
        <p className="text-sm text-cinema-muted mt-1.5">
          View your active reservations, verify tickets, or initiate cancellations with an 80% refund.
        </p>
      </div>

      {/* Quick Lookup Bar */}
      <div className="bg-cinema-card p-6 rounded-2xl border border-cinema-border/80 space-y-4">
        <h3 className="text-sm font-bold uppercase tracking-wider text-slate-300">
          Find Booking by ID
        </h3>
        <form onSubmit={handleSearch} className="flex flex-col sm:flex-row gap-3">
          <div className="relative flex-1">
            <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3.5" />
            <input
              type="text"
              placeholder="Enter Booking ID (e.g., BK1001)"
              value={searchId}
              onChange={(e) => setSearchId(e.target.value.toUpperCase())}
              className="w-full bg-cinema-surface border border-cinema-border rounded-xl pl-10 pr-4 py-2.5 text-sm text-white uppercase placeholder:normal-case placeholder:text-slate-500 focus:outline-none focus:border-accent-purple"
            />
          </div>
          <button
            type="submit"
            className="px-6 py-2.5 rounded-xl font-bold text-xs bg-accent-purple hover:bg-accent-purpleDark text-white transition-colors"
          >
            Search
          </button>
        </form>

        {searchError && (
          <p className="text-xs text-red-400 font-semibold">{searchError}</p>
        )}

        {searchResult && (
          <div className="pt-4 border-t border-cinema-border/60">
            <h4 className="text-xs font-bold uppercase text-accent-goldLight mb-3">Search Result:</h4>
            <BookingCard booking={searchResult} onCancelClick={() => handleOpenCancelModal(searchResult)} />
          </div>
        )}
      </div>

      {/* All Bookings List */}
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h2 className="text-xl font-bold text-white">All Recent Bookings</h2>
          <button
            onClick={loadBookings}
            className="text-xs font-semibold text-accent-purpleLight hover:text-white"
          >
            Refresh
          </button>
        </div>

        {loading ? (
          <div className="py-16 text-center text-slate-400">
            <div className="w-10 h-10 border-4 border-accent-purple border-t-transparent rounded-full animate-spin mx-auto mb-4" />
            <p>Loading bookings...</p>
          </div>
        ) : error ? (
          <div className="p-8 text-center bg-cinema-card rounded-2xl border border-red-900/50 text-red-400">
            {error}
          </div>
        ) : bookings.length === 0 ? (
          <div className="p-12 text-center bg-cinema-card rounded-2xl border border-cinema-border text-slate-400 space-y-3">
            <Ticket className="w-10 h-10 text-slate-600 mx-auto" />
            <p className="font-semibold text-white">No bookings recorded in this session yet.</p>
            <p className="text-xs">Browse our current movie showtimes and book your tickets.</p>
            <Link
              to="/showtimes"
              className="inline-flex items-center gap-1.5 px-4 py-2 rounded-xl text-xs font-bold bg-accent-purple text-white"
            >
              Browse Showtimes <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>
        ) : (
          <div className="grid grid-cols-1 gap-4">
            {bookings.map((b) => (
              <BookingCard
                key={b.id || b.bookingId}
                booking={b}
                onCancelClick={() => handleOpenCancelModal(b)}
              />
            ))}
          </div>
        )}
      </div>

      {/* Cancellation Modal */}
      {cancellingBooking && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm animate-in fade-in">
          <div className="bg-cinema-card rounded-3xl border border-cinema-border/90 max-w-md w-full p-6 space-y-5 shadow-2xl relative">
            <button
              onClick={() => setCancellingBooking(null)}
              className="absolute top-4 right-4 p-1 rounded-lg text-slate-400 hover:text-white"
            >
              <X className="w-5 h-5" />
            </button>

            {!cancellationResult ? (
              <>
                <div className="w-12 h-12 rounded-2xl bg-red-950/70 border border-red-500/40 text-red-400 flex items-center justify-center">
                  <AlertTriangle className="w-6 h-6" />
                </div>

                <div>
                  <h3 className="text-xl font-bold text-white">Cancel Booking {cancellingBooking.bookingId}?</h3>
                  <p className="text-xs text-cinema-muted mt-1 leading-relaxed">
                    Under cinema policy, cancelling will immediately release your reserved seats ({cancellingBooking.seatCodes.join(', ')}) back to the public pool.
                  </p>
                </div>

                <div className="p-4 rounded-xl bg-cinema-surface border border-cinema-border space-y-2 text-xs">
                  <div className="flex justify-between text-slate-400">
                    <span>Original Paid Amount:</span>
                    <span className="font-bold text-white">Rs. {Number(cancellingBooking.totalAmount).toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between text-emerald-400 font-extrabold text-sm pt-2 border-t border-cinema-border">
                    <span>80% Refund Amount:</span>
                    <span>Rs. {(Number(cancellingBooking.totalAmount) * 0.8).toFixed(2)}</span>
                  </div>
                </div>

                <div className="flex gap-3 pt-2">
                  <button
                    type="button"
                    onClick={() => setCancellingBooking(null)}
                    className="flex-1 py-2.5 rounded-xl text-xs font-bold bg-cinema-surface hover:bg-slate-800 text-slate-300 border border-cinema-border"
                  >
                    Keep Booking
                  </button>
                  <button
                    type="button"
                    disabled={cancelLoading}
                    onClick={handleConfirmCancel}
                    className="flex-1 py-2.5 rounded-xl text-xs font-bold bg-red-600 hover:bg-red-700 text-white disabled:opacity-50 transition-colors"
                  >
                    {cancelLoading ? 'Cancelling...' : 'Confirm 80% Refund'}
                  </button>
                </div>
              </>
            ) : (
              <div className="space-y-4 text-center">
                <div className="w-12 h-12 rounded-full bg-emerald-500/20 text-emerald-400 border border-emerald-500/40 flex items-center justify-center mx-auto">
                  <CheckCircle className="w-6 h-6" />
                </div>
                <h3 className="text-xl font-bold text-white">Cancellation Successful</h3>
                <p className="text-xs text-slate-300">{cancellationResult.message}</p>
                <div className="p-4 bg-cinema-surface rounded-xl border border-cinema-border text-xs text-left space-y-1.5">
                  <div className="flex justify-between">
                    <span className="text-slate-400">Booking ID:</span>
                    <span className="font-mono font-bold text-white">{cancellationResult.bookingId}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-slate-400">Refund Credited:</span>
                    <span className="font-bold text-emerald-400">Rs. {Number(cancellationResult.refundAmount).toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-slate-400">Seats Released:</span>
                    <span className="font-semibold text-slate-200">{cancellationResult.releasedSeats.join(', ')}</span>
                  </div>
                </div>
                <button
                  onClick={() => setCancellingBooking(null)}
                  className="w-full py-2.5 rounded-xl text-xs font-bold bg-accent-purple text-white"
                >
                  Close
                </button>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

const BookingCard: React.FC<{ booking: Booking; onCancelClick: () => void }> = ({
  booking,
  onCancelClick,
}) => {
  const isCancelled = booking.bookingStatus === 'CANCELLED';

  return (
    <div className="bg-cinema-card/70 border border-cinema-border/80 hover:border-accent-purple/50 rounded-2xl p-5 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 transition-all">
      <div className="space-y-2 min-w-0">
        <div className="flex items-center gap-2">
          <span className="font-mono font-black text-sm text-accent-gold">{booking.bookingId}</span>
          <span
            className={`px-2 py-0.5 rounded text-[10px] font-black uppercase tracking-wider ${
              isCancelled
                ? 'bg-red-950/60 text-red-400 border border-red-500/40'
                : 'bg-emerald-950/60 text-emerald-300 border border-emerald-500/40'
            }`}
          >
            {booking.bookingStatus}
          </span>
        </div>

        <h3 className="text-lg font-black text-white truncate">{booking.movieTitle}</h3>

        <div className="flex flex-wrap items-center gap-3 text-xs text-cinema-muted">
          <span className="flex items-center gap-1 text-slate-300">
            <Calendar className="w-3.5 h-3.5 text-accent-gold" />
            {booking.showDate}
          </span>
          <span>•</span>
          <span className="flex items-center gap-1 text-slate-300">
            <Clock className="w-3.5 h-3.5 text-accent-gold" />
            {booking.showTime}
          </span>
          <span>•</span>
          <span>
            Seats: <strong className="text-white">{booking.seatCodes.join(', ')}</strong>
          </span>
        </div>
      </div>

      <div className="flex sm:flex-col items-center sm:items-end justify-between w-full sm:w-auto pt-3 sm:pt-0 border-t sm:border-t-0 border-cinema-border gap-2">
        <div className="text-left sm:text-right">
          <span className="text-[10px] uppercase font-bold text-slate-400 block">Total Paid</span>
          <span className="text-base font-black text-accent-gold">
            Rs. {Number(booking.totalAmount).toFixed(2)}
          </span>
        </div>

        <div className="flex gap-2">
          <Link
            to={`/confirmation/${booking.bookingId}`}
            className="px-3 py-1.5 rounded-lg text-xs font-bold bg-cinema-surface hover:bg-slate-700 text-slate-300 border border-cinema-border"
          >
            Pass
          </Link>
          {!isCancelled && (
            <button
              onClick={onCancelClick}
              className="px-3 py-1.5 rounded-lg text-xs font-bold bg-red-950/60 hover:bg-red-900/80 text-red-300 border border-red-500/50 transition-colors"
            >
              Cancel
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

