import React from 'react';
import { Seat } from '../types';
import { useBooking } from '../context/BookingContext';

interface SeatGridProps {
  seats: Seat[];
}

export const SeatGrid: React.FC<SeatGridProps> = ({ seats }) => {
  const { selectedSeats, toggleSeat } = useBooking();

  const rows = ['A', 'B', 'C', 'D', 'E'];
  const cols = [1, 2, 3, 4, 5, 6, 7, 8];

  // Map seatCode -> Seat for fast lookup
  const seatMap = new Map<string, Seat>();
  seats.forEach((s) => seatMap.set(s.seatCode.toUpperCase(), s));

  const isSelected = (seatCode: string) => {
    return selectedSeats.some((s) => s.seatCode.toUpperCase() === seatCode.toUpperCase());
  };

  const getSeatStyle = (seat?: Seat) => {
    if (!seat) return 'bg-slate-800 opacity-20 cursor-not-allowed';

    if (seat.isBooked) {
      return 'bg-cinema-darkest text-slate-600 border border-red-950/40 opacity-45 cursor-not-allowed line-through';
    }

    if (isSelected(seat.seatCode)) {
      return 'bg-emerald-500 text-black border border-emerald-300 font-extrabold shadow-[0_0_15px_rgba(16,185,129,0.7)] scale-105';
    }

    switch (seat.seatType) {
      case 'VIP':
        return 'bg-amber-950/60 border border-accent-gold/60 text-amber-300 hover:bg-accent-gold hover:text-black hover:shadow-gold-glow';
      case 'PREMIUM':
        return 'bg-purple-950/60 border border-purple-500/50 text-purple-200 hover:bg-accent-purple hover:text-white hover:shadow-cinema-glow';
      case 'REGULAR':
      default:
        return 'bg-slate-800 border border-slate-600 text-slate-300 hover:bg-slate-700 hover:text-white';
    }
  };

  return (
    <div className="w-full flex flex-col items-center">
      {/* Screen Graphic */}
      <div className="w-full max-w-2xl mb-12 flex flex-col items-center">
        <div className="w-full h-8 curved-screen flex items-center justify-center">
          <span className="text-[11px] font-extrabold tracking-[0.3em] uppercase text-accent-purpleLight drop-shadow-md">
            All Eyes This Way • Cinema Screen
          </span>
        </div>
        <div className="w-3/4 h-6 bg-gradient-to-b from-accent-purple/10 to-transparent blur-md -mt-2"></div>
      </div>

      {/* 5x8 Seats Table */}
      <div className="overflow-x-auto w-full flex justify-center py-4">
        <div className="inline-block min-w-[500px] sm:min-w-[620px] bg-cinema-card/50 p-6 rounded-3xl border border-cinema-border/70 shadow-2xl">
          {/* Column Header Numbers 1-8 */}
          <div className="grid grid-cols-[40px_repeat(8,1fr)] gap-2.5 sm:gap-4 mb-4 text-center">
            <span className="text-xs font-bold text-slate-500">Row</span>
            {cols.map((col) => (
              <span key={col} className="text-xs font-bold text-slate-400">
                {col}
              </span>
            ))}
          </div>

          {/* Rows A through E */}
          <div className="space-y-3">
            {rows.map((row) => (
              <div key={row} className="grid grid-cols-[40px_repeat(8,1fr)] gap-2.5 sm:gap-4 items-center">
                {/* Row Label with Category Tag */}
                <div className="flex items-center justify-center font-black text-sm text-slate-400">
                  {row}
                </div>

                {/* 8 Columns for this row */}
                {cols.map((col) => {
                  const seatCode = `${row}${col}`;
                  const seat = seatMap.get(seatCode);
                  const selected = isSelected(seatCode);

                  return (
                    <button
                      key={seatCode}
                      onClick={() => seat && toggleSeat(seat)}
                      disabled={!seat || seat.isBooked}
                      title={
                        seat
                          ? `${seatCode} • ${seat.seatType} • Rs. ${seat.price} ${
                              seat.isBooked ? '(Sold Out)' : selected ? '(Selected)' : '(Click to select)'
                            }`
                          : 'Not available'
                      }
                      className={`relative aspect-square rounded-xl flex items-center justify-center text-xs font-bold transition-all duration-200 transform active:scale-95 ${getSeatStyle(
                        seat
                      )}`}
                    >
                      {seatCode}
                      {selected && (
                        <span className="absolute -top-1 -right-1 w-3 h-3 bg-white rounded-full flex items-center justify-center text-[8px] text-black">
                          ✓
                        </span>
                      )}
                    </button>
                  );
                })}
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

