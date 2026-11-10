import React from 'react';
import { Film, Shield, Database, Server, Smartphone, Heart } from 'lucide-react';

export const Footer: React.FC = () => {
  return (
    <footer className="bg-cinema-darkest border-t border-cinema-border/80 mt-20 pt-16 pb-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-10 pb-12 border-b border-cinema-border/60">
          {/* Brand Info */}
          <div className="md:col-span-2 space-y-4">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-accent-purpleDark to-accent-purple flex items-center justify-center">
                <Film className="w-5 h-5 text-white" />
              </div>
              <span className="text-2xl font-black tracking-tight text-white">
                CINE<span className="text-accent-gold">BOOK</span>
              </span>
            </div>
            <p className="text-cinema-muted text-sm max-w-md leading-relaxed">
              A full-stack, enterprise-grade cinema booking platform. Built with high-concurrency Java Spring Boot 3, Supabase PostgreSQL, and React with responsive dark cinema aesthetics.
            </p>
            <div className="flex flex-wrap items-center gap-2 pt-2">
              <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-accent-purple/15 text-accent-purpleLight border border-accent-purple/30">
                <Server className="w-3.5 h-3.5" /> Spring Boot 3
              </span>
              <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-500/15 text-emerald-400 border border-emerald-500/30">
                <Database className="w-3.5 h-3.5" /> Supabase PostgreSQL
              </span>
              <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-accent-gold/15 text-accent-goldLight border border-accent-gold/30">
                <Shield className="w-3.5 h-3.5" /> Pessimistic Lock Safe
              </span>
            </div>
          </div>

          {/* Quick Links */}
          <div>
            <h4 className="text-sm font-bold uppercase tracking-wider text-slate-300 mb-4">Navigation</h4>
            <ul className="space-y-2.5 text-sm text-cinema-muted">
              <li><a href="/" className="hover:text-accent-gold transition-colors">Now Showing</a></li>
              <li><a href="/showtimes" className="hover:text-accent-gold transition-colors">Today's Showtimes</a></li>
              <li><a href="/my-bookings" className="hover:text-accent-gold transition-colors">Cancellation & Refunds</a></li>
              <li><a href="/#promotions" className="hover:text-accent-gold transition-colors">Promotional Coupons</a></li>
            </ul>
          </div>

          {/* Pricing Policy */}
          <div>
            <h4 className="text-sm font-bold uppercase tracking-wider text-slate-300 mb-4">Cinema Tiers</h4>
            <ul className="space-y-2.5 text-sm text-cinema-muted">
              <li className="flex justify-between items-center pr-4">
                <span>Regular (Rows A–B)</span>
                <span className="font-bold text-white">Rs. 150</span>
              </li>
              <li className="flex justify-between items-center pr-4">
                <span>Premium (Rows C–D)</span>
                <span className="font-bold text-white">Rs. 250</span>
              </li>
              <li className="flex justify-between items-center pr-4">
                <span>VIP Recliner (Row E)</span>
                <span className="font-bold text-accent-gold">Rs. 400</span>
              </li>
              <li className="pt-2 text-xs text-slate-500">
                *Cancellations receive an instant 80% refund.
              </li>
            </ul>
          </div>
        </div>

        <div className="pt-8 flex flex-col sm:flex-row items-center justify-between text-xs text-cinema-muted gap-4">
          <p>© {new Date().getFullYear()} CineBook Inc. All rights reserved.</p>
          <p className="flex items-center gap-1">
            Engineered with <Heart className="w-3.5 h-3.5 text-rose-500 fill-rose-500" /> for Academic & Production Excellence
          </p>
        </div>
      </div>
    </footer>
  );
};

