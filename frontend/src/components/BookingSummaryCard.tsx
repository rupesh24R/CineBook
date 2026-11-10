import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Ticket, Tag, Check, X, ArrowRight, ShieldCheck } from 'lucide-react';
import { useBooking } from '../context/BookingContext';
import { validateCoupon } from '../api/client';

export const BookingSummaryCard: React.FC = () => {
  const {
    selectedMovie,
    selectedShow,
    selectedSeats,
    appliedCoupon,
    setAppliedCoupon,
    originalTotal,
    discountAmount,
    finalTotal,
    seatCategoriesText,
  } = useBooking();

  const [inputCoupon, setInputCoupon] = useState('');
  const [couponLoading, setCouponLoading] = useState(false);
  const [couponError, setCouponError] = useState<string | null>(null);
  const navigate = useNavigate();

  const handleApplyCoupon = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputCoupon.trim()) return;

    setCouponLoading(true);
    setCouponError(null);

    try {
      const result = await validateCoupon(inputCoupon.trim(), originalTotal);
      if (result.valid) {
        setAppliedCoupon(result);
        setCouponError(null);
      } else {
        setAppliedCoupon(null);
        setCouponError(result.message || 'Invalid coupon code.');
      }
    } catch (err: any) {
      setAppliedCoupon(null);
      setCouponError(err.response?.data?.message || 'Error validating coupon.');
    } finally {
      setCouponLoading(false);
    }
  };

  const handleRemoveCoupon = () => {
    setAppliedCoupon(null);
    setInputCoupon('');
    setCouponError(null);
  };

  const hasSelectedSeats = selectedSeats.length > 0;

  return (
    <div className="bg-cinema-card rounded-2xl border border-cinema-border/80 p-6 shadow-xl sticky top-28 space-y-5">
      <div className="flex items-center justify-between pb-4 border-b border-cinema-border/60">
        <h3 className="text-lg font-bold text-white flex items-center gap-2">
          <Ticket className="w-5 h-5 text-accent-gold" />
          Booking Summary
        </h3>
        <span className="text-xs px-2.5 py-1 rounded-full font-bold bg-accent-purple/15 text-accent-purpleLight border border-accent-purple/30">
          {selectedSeats.length} {selectedSeats.length === 1 ? 'Seat' : 'Seats'}
        </span>
      </div>

      {/* Movie & Show Meta */}
      {selectedShow && (
        <div className="space-y-1.5 text-xs text-cinema-muted pb-4 border-b border-cinema-border/60">
          <p className="font-bold text-sm text-white">{selectedMovie?.title || selectedShow.movieTitle}</p>
          <p>
            Showtime: <span className="font-semibold text-slate-200">{selectedShow.showTime}</span> ({selectedShow.showDate})
          </p>
          <p>
            Auditorium: <span className="font-semibold text-slate-200">Screen 1 (Single-Screen)</span>
          </p>
        </div>
      )}

      {/* Selected Seats Badges */}
      <div>
        <span className="text-xs font-bold uppercase tracking-wider text-slate-400 block mb-2">
          Selected Seats
        </span>
        {hasSelectedSeats ? (
          <div className="flex flex-wrap gap-1.5">
            {selectedSeats.map((seat) => (
              <span
                key={seat.id}
                className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg text-xs font-black bg-accent-purple/20 text-accent-purpleLight border border-accent-purple/40"
              >
                {seat.seatCode}
                <span className="text-[10px] text-slate-400 font-normal">({seat.seatType[0]})</span>
              </span>
            ))}
          </div>
        ) : (
          <p className="text-xs text-slate-500 italic">No seats selected yet. Click seats on the map above.</p>
        )}
      </div>

      {/* Category breakdown */}
      {hasSelectedSeats && (
        <div className="text-xs flex justify-between text-slate-400">
          <span>Categories:</span>
          <span className="font-semibold text-slate-200">{seatCategoriesText}</span>
        </div>
      )}

      {/* Coupon Application Form */}
      {hasSelectedSeats && (
        <div className="pt-2">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-400 block mb-2">
            Promotional Coupon
          </span>

          {appliedCoupon && appliedCoupon.valid ? (
            <div className="flex items-center justify-between p-3 rounded-xl bg-emerald-950/40 border border-emerald-500/50 text-xs">
              <div className="flex items-center gap-2 text-emerald-400 font-bold">
                <Tag className="w-4 h-4" />
                <span>{appliedCoupon.code} applied ({appliedCoupon.discountPercentage}% off)</span>
              </div>
              <button
                type="button"
                onClick={handleRemoveCoupon}
                className="p-1 hover:bg-emerald-900/50 rounded text-emerald-400 hover:text-white"
                title="Remove Coupon"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
          ) : (
            <form onSubmit={handleApplyCoupon} className="flex gap-2">
              <input
                type="text"
                placeholder="Enter CINE10, VIP20"
                value={inputCoupon}
                onChange={(e) => setInputCoupon(e.target.value.toUpperCase())}
                className="flex-1 bg-cinema-surface border border-cinema-border rounded-xl px-3 py-2 text-xs text-white uppercase placeholder:normal-case placeholder:text-slate-500 focus:outline-none focus:border-accent-purple"
              />
              <button
                type="submit"
                disabled={couponLoading || !inputCoupon.trim()}
                className="px-3.5 py-2 rounded-xl bg-accent-purple hover:bg-accent-purpleDark disabled:opacity-50 text-xs font-bold text-white transition-colors"
              >
                {couponLoading ? 'Checking...' : 'Apply'}
              </button>
            </form>
          )}

          {couponError && (
            <p className="text-[11px] text-red-400 mt-1.5 flex items-center gap-1">
              <X className="w-3.5 h-3.5" />
              {couponError}
            </p>
          )}
        </div>
      )}

      {/* Financial Calculations */}
      <div className="pt-3 border-t border-cinema-border/60 space-y-2 text-xs">
        <div className="flex justify-between text-slate-400">
          <span>Subtotal</span>
          <span className="font-bold text-slate-200">Rs. {originalTotal.toFixed(2)}</span>
        </div>

        {discountAmount > 0 && (
          <div className="flex justify-between text-emerald-400 font-bold">
            <span>Coupon Discount</span>
            <span>- Rs. {discountAmount.toFixed(2)}</span>
          </div>
        )}

        <div className="flex justify-between items-baseline pt-2 border-t border-cinema-border text-sm">
          <span className="font-extrabold text-white">Final Total</span>
          <span className="text-xl font-black text-accent-gold">
            Rs. {finalTotal.toFixed(2)}
          </span>
        </div>
      </div>

      {/* Action Button */}
      <button
        disabled={!hasSelectedSeats}
        onClick={() => navigate('/checkout')}
        className="w-full flex items-center justify-center gap-2 py-3.5 px-4 rounded-xl font-bold text-sm bg-gradient-to-r from-accent-purpleDark via-accent-purple to-accent-gold text-white hover:brightness-110 active:scale-[0.99] disabled:opacity-40 disabled:cursor-not-allowed transition-all shadow-lg shadow-accent-purple/20"
      >
        Proceed to Checkout
        <ArrowRight className="w-4 h-4" />
      </button>

      <div className="flex items-center justify-center gap-1.5 text-[11px] text-slate-500 text-center pt-1">
        <ShieldCheck className="w-3.5 h-3.5 text-accent-purpleLight" />
        <span>Atomic Seat Reservation • 80% Refund Policy</span>
      </div>
    </div>
  );
};

