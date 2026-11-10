import React from 'react';

export const SeatLegend: React.FC = () => {
  return (
    <div className="bg-cinema-surface/70 border border-cinema-border/70 rounded-2xl p-4 sm:p-5 shadow-inner">
      <div className="text-xs font-bold uppercase tracking-wider text-slate-400 text-center mb-3">
        Seating Categories & Status Legend
      </div>

      <div className="flex flex-wrap items-center justify-center gap-4 sm:gap-8 text-xs font-semibold">
        {/* Regular */}
        <div className="flex items-center gap-2">
          <div className="w-6 h-6 rounded-md bg-slate-700 border border-slate-500/50 flex items-center justify-center text-[10px] text-slate-300 font-bold">
            A1
          </div>
          <div className="flex flex-col">
            <span className="text-white font-bold">Regular</span>
            <span className="text-[11px] text-accent-purpleLight">Rs. 150 (Rows A-B)</span>
          </div>
        </div>

        {/* Premium */}
        <div className="flex items-center gap-2">
          <div className="w-6 h-6 rounded-md bg-purple-900/60 border border-purple-500/70 flex items-center justify-center text-[10px] text-purple-200 font-bold">
            C1
          </div>
          <div className="flex flex-col">
            <span className="text-white font-bold">Premium</span>
            <span className="text-[11px] text-accent-purpleLight">Rs. 250 (Rows C-D)</span>
          </div>
        </div>

        {/* VIP */}
        <div className="flex items-center gap-2">
          <div className="w-6 h-6 rounded-md bg-amber-950/70 border border-accent-gold/80 flex items-center justify-center text-[10px] text-accent-goldLight font-bold">
            E1
          </div>
          <div className="flex flex-col">
            <span className="text-white font-bold">VIP Recliner</span>
            <span className="text-[11px] text-accent-gold">Rs. 400 (Row E)</span>
          </div>
        </div>

        {/* Selected */}
        <div className="flex items-center gap-2">
          <div className="w-6 h-6 rounded-md bg-emerald-500 border border-emerald-400 shadow-md flex items-center justify-center text-[10px] text-black font-extrabold">
            ✓
          </div>
          <span className="text-emerald-400 font-bold">Selected</span>
        </div>

        {/* Booked */}
        <div className="flex items-center gap-2">
          <div className="w-6 h-6 rounded-md bg-cinema-darkest/90 border border-red-900/40 opacity-50 flex items-center justify-center text-[10px] text-red-500/80 font-bold cursor-not-allowed">
            ✕
          </div>
          <span className="text-slate-500 font-medium">Sold Out</span>
        </div>
      </div>
    </div>
  );
};

