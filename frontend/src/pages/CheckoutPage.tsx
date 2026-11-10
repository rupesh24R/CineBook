import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { ArrowLeft, ShieldCheck, Ticket, CreditCard, Lock, Sparkles, AlertCircle, CheckCircle2 } from 'lucide-react';
import { useBooking } from '../context/BookingContext';
import { createBooking } from '../api/client';

export const CheckoutPage: React.FC = () => {
  const {
    selectedShow,
    selectedSeats,
    appliedCoupon,
    originalTotal,
    discountAmount,
    finalTotal,
    seatCategoriesText,
    resetBookingFlow,
  } = useBooking();

  const [customerName, setCustomerName] = useState('Alex Morgan');
  const [customerEmail, setCustomerEmail] = useState('alex.morgan@example.com');
  const [customerPhone, setCustomerPhone] = useState('+91 98765 43210');
  const [paymentMethod, setPaymentMethod] = useState<'CARD' | 'UPI' | 'NETBANKING'>('UPI');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [bookingError, setBookingError] = useState<string | null>(null);

  const navigate = useNavigate();

  if (!selectedShow || selectedSeats.length === 0) {
    return (
      <div className="max-w-xl mx-auto py-20 text-center space-y-4">
        <div className="p-8 bg-cinema-card border border-cinema-border rounded-3xl space-y-3">
          <Ticket className="w-12 h-12 text-slate-500 mx-auto" />
          <h2 className="text-xl font-bold text-white">No Seats Selected</h2>
          <p className="text-xs text-cinema-muted">Please select a movie and seats before proceeding to checkout.</p>
          <div className="pt-2">
            <Link
              to="/showtimes"
              className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl text-xs font-bold bg-accent-purple text-white"
            >
              Browse Showtimes
            </Link>
          </div>
        </div>
      </div>
    );
  }

  const handleConfirmPayment = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setBookingError(null);

    try {
      const seatCodes = selectedSeats.map((s) => s.seatCode);
      const booking = await createBooking({
        showId: selectedShow.id,
        seatCodes: seatCodes,
        couponCode: appliedCoupon?.code,
      });

      // Clear selection and navigate to confirmation receipt
      resetBookingFlow();
      navigate(`/confirmation/${booking.bookingId}`);
    } catch (err: any) {
      const msg = err.response?.data?.message || err.message || 'Booking transaction failed. Please retry.';
      setBookingError(msg);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-8 pb-16">
      {/* Navigation */}
      <div className="flex items-center gap-3">
        <Link
          to={`/seats/${selectedShow.id}`}
          className="p-2 rounded-xl bg-cinema-surface hover:bg-slate-800 border border-cinema-border text-slate-300 hover:text-white transition-colors"
        >
          <ArrowLeft className="w-5 h-5" />
        </Link>
        <div>
          <h1 className="text-2xl font-black text-white tracking-tight">Checkout & Payment</h1>
          <p className="text-xs text-cinema-muted">Review your reservation details and complete booking</p>
        </div>
      </div>

      {bookingError && (
        <div className="p-4 rounded-xl bg-red-950/50 border border-red-500/60 text-red-300 text-sm flex items-start gap-3">
          <AlertCircle className="w-5 h-5 text-red-400 shrink-0 mt-0.5" />
          <div className="space-y-1">
            <p className="font-bold">Transaction Failed</p>
            <p className="text-xs text-red-200">{bookingError}</p>
            <p className="text-[11px] text-red-300/80 pt-1">
              If another user booked these seats simultaneously, please return to seat selection and pick alternate seats.
            </p>
          </div>
        </div>
      )}

      <form onSubmit={handleConfirmPayment} className="grid grid-cols-1 md:grid-cols-12 gap-8 items-start">
        {/* Left Col: Customer & Payment Details */}
        <div className="md:col-span-7 space-y-6">
          {/* Contact Details Card */}
          <div className="bg-cinema-card rounded-2xl border border-cinema-border/80 p-6 space-y-4">
            <h3 className="text-base font-bold text-white flex items-center gap-2">
              <ShieldCheck className="w-5 h-5 text-accent-purple" />
              Ticket Delivery Information
            </h3>

            <div className="space-y-3 text-xs">
              <div>
                <label className="block text-slate-400 font-semibold mb-1">Full Name</label>
                <input
                  type="text"
                  required
                  value={customerName}
                  onChange={(e) => setCustomerName(e.target.value)}
                  className="w-full bg-cinema-surface border border-cinema-border rounded-xl px-3.5 py-2.5 text-white placeholder:text-slate-500 focus:outline-none focus:border-accent-purple"
                />
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label className="block text-slate-400 font-semibold mb-1">Email Address</label>
                  <input
                    type="email"
                    required
                    value={customerEmail}
                    onChange={(e) => setCustomerEmail(e.target.value)}
                    className="w-full bg-cinema-surface border border-cinema-border rounded-xl px-3.5 py-2.5 text-white placeholder:text-slate-500 focus:outline-none focus:border-accent-purple"
                  />
                </div>
                <div>
                  <label className="block text-slate-400 font-semibold mb-1">Mobile Number</label>
                  <input
                    type="tel"
                    required
                    value={customerPhone}
                    onChange={(e) => setCustomerPhone(e.target.value)}
                    className="w-full bg-cinema-surface border border-cinema-border rounded-xl px-3.5 py-2.5 text-white placeholder:text-slate-500 focus:outline-none focus:border-accent-purple"
                  />
                </div>
              </div>
            </div>
          </div>

          {/* Payment Method Selector */}
          <div className="bg-cinema-card rounded-2xl border border-cinema-border/80 p-6 space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-base font-bold text-white flex items-center gap-2">
                <CreditCard className="w-5 h-5 text-accent-gold" />
                Select Payment Mode
              </h3>
              <span className="text-[10px] font-bold uppercase tracking-wider text-emerald-400 bg-emerald-950/60 border border-emerald-500/40 px-2 py-0.5 rounded">
                Simulated Sandbox
              </span>
            </div>

            <div className="grid grid-cols-3 gap-3">
              {[
                { id: 'UPI', label: 'Instant UPI', desc: 'GPay / PhonePe / Paytm' },
                { id: 'CARD', label: 'Cards', desc: 'Credit or Debit' },
                { id: 'NETBANKING', label: 'Net Banking', desc: 'All Major Banks' },
              ].map((m) => (
                <button
                  type="button"
                  key={m.id}
                  onClick={() => setPaymentMethod(m.id as any)}
                  className={`p-3 rounded-xl border text-left transition-all ${
                    paymentMethod === m.id
                      ? 'bg-accent-purple/20 border-accent-purple text-white shadow-cinema-glow'
                      : 'bg-cinema-surface border-cinema-border text-slate-400 hover:text-white'
                  }`}
                >
                  <span className="block font-bold text-xs text-white">{m.label}</span>
                  <span className="block text-[10px] text-slate-400 mt-0.5">{m.desc}</span>
                </button>
              ))}
            </div>

            <div className="p-3 bg-cinema-surface rounded-xl border border-cinema-border/60 text-xs text-slate-400 flex items-center gap-2">
              <Lock className="w-4 h-4 text-emerald-400 shrink-0" />
              <span>Payments are encrypted with 256-bit SSL. Booking is atomic and guaranteed by backend locking.</span>
            </div>
          </div>
        </div>

        {/* Right Col: Order Breakdown Card */}
        <div className="md:col-span-5 bg-cinema-card rounded-2xl border border-cinema-border/80 p-6 space-y-5 shadow-xl">
          <h3 className="text-base font-bold text-white pb-3 border-b border-cinema-border/60">
            Booking Breakdown
          </h3>

          <div className="space-y-3 text-xs">
            <div>
              <span className="text-slate-400">Movie</span>
              <p className="font-bold text-sm text-white">{selectedShow.movieTitle}</p>
            </div>

            <div className="grid grid-cols-2 gap-2 text-slate-300">
              <div>
                <span className="text-slate-400 block">Date</span>
                <span className="font-semibold">{selectedShow.showDate}</span>
              </div>
              <div>
                <span className="text-slate-400 block">Time</span>
                <span className="font-semibold">{selectedShow.showTime}</span>
              </div>
            </div>

            <div>
              <span className="text-slate-400 block mb-1">Seats Selected ({selectedSeats.length})</span>
              <div className="flex flex-wrap gap-1">
                {selectedSeats.map((s) => (
                  <span
                    key={s.id}
                    className="px-2 py-0.5 rounded font-black text-xs bg-accent-purple/20 text-accent-purpleLight border border-accent-purple/40"
                  >
                    {s.seatCode}
                  </span>
                ))}
              </div>
            </div>

            <div className="flex justify-between text-slate-400 pt-2 border-t border-cinema-border/60">
              <span>Seating Tier(s)</span>
              <span className="font-semibold text-slate-200">{seatCategoriesText}</span>
            </div>

            <div className="flex justify-between text-slate-400">
              <span>Standard Price</span>
              <span className="font-bold text-slate-200">Rs. {originalTotal.toFixed(2)}</span>
            </div>

            {discountAmount > 0 && (
              <div className="flex justify-between text-emerald-400 font-bold">
                <span>Coupon ({appliedCoupon?.code})</span>
                <span>- Rs. {discountAmount.toFixed(2)}</span>
              </div>
            )}

            <div className="flex justify-between items-baseline pt-3 border-t border-cinema-border text-sm">
              <span className="font-extrabold text-white">Amount to Pay</span>
              <span className="text-2xl font-black text-accent-gold">
                Rs. {finalTotal.toFixed(2)}
              </span>
            </div>
          </div>

          <button
            type="submit"
            disabled={isSubmitting}
            className="w-full flex items-center justify-center gap-2 py-3.5 px-4 rounded-xl font-bold text-sm bg-gradient-to-r from-accent-purple via-accent-purpleDark to-accent-gold text-white hover:brightness-110 active:scale-[0.99] disabled:opacity-50 transition-all shadow-lg"
          >
            {isSubmitting ? (
              <>
                <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                Reserving Seats...
              </>
            ) : (
              <>
                <CheckCircle2 className="w-4 h-4" />
                Pay & Confirm Booking
              </>
            )}
          </button>
        </div>
      </form>
    </div>
  );
};

