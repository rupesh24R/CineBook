import React, { createContext, useContext, useState, useMemo } from 'react';
import { Movie, Show, Seat, CouponValidation } from '../types';

interface BookingContextType {
  selectedMovie: Movie | null;
  selectedShow: Show | null;
  selectedSeats: Seat[];
  couponCode: string;
  appliedCoupon: CouponValidation | null;
  setSelectedMovie: (movie: Movie | null) => void;
  setSelectedShow: (show: Show | null) => void;
  toggleSeat: (seat: Seat) => void;
  clearSelectedSeats: () => void;
  setCouponCode: (code: string) => void;
  setAppliedCoupon: (coupon: CouponValidation | null) => void;
  resetBookingFlow: () => void;
  originalTotal: number;
  discountAmount: number;
  finalTotal: number;
  seatCategoriesText: string;
}

const BookingContext = createContext<BookingContextType | undefined>(undefined);

export const BookingProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [selectedMovie, setSelectedMovie] = useState<Movie | null>(null);
  const [selectedShow, setSelectedShow] = useState<Show | null>(null);
  const [selectedSeats, setSelectedSeats] = useState<Seat[]>([]);
  const [couponCode, setCouponCode] = useState<string>('');
  const [appliedCoupon, setAppliedCoupon] = useState<CouponValidation | null>(null);

  const toggleSeat = (seat: Seat) => {
    if (seat.isBooked) return;

    setSelectedSeats((prev) => {
      const exists = prev.some((s) => s.id === seat.id);
      if (exists) {
        return prev.filter((s) => s.id !== seat.id);
      } else {
        // Enforce maximum 10 seats per booking
        if (prev.length >= 10) {
          alert('Maximum 10 seats can be booked in a single transaction.');
          return prev;
        }
        return [...prev, seat];
      }
    });
  };

  const clearSelectedSeats = () => {
    setSelectedSeats([]);
    setAppliedCoupon(null);
  };

  const resetBookingFlow = () => {
    setSelectedMovie(null);
    setSelectedShow(null);
    setSelectedSeats([]);
    setCouponCode('');
    setAppliedCoupon(null);
  };

  const originalTotal = useMemo(() => {
    return selectedSeats.reduce((acc, seat) => acc + Number(seat.price || 0), 0);
  }, [selectedSeats]);

  const discountAmount = useMemo(() => {
    if (!appliedCoupon || !appliedCoupon.valid) return 0;
    const pct = Number(appliedCoupon.discountPercentage || 0);
    return (originalTotal * pct) / 100;
  }, [originalTotal, appliedCoupon]);

  const finalTotal = useMemo(() => {
    const total = originalTotal - discountAmount;
    return total > 0 ? total : 0;
  }, [originalTotal, discountAmount]);

  const seatCategoriesText = useMemo(() => {
    const categories = Array.from(new Set(selectedSeats.map((s) => s.seatType)));
    return categories.length > 0 ? categories.join(', ') : 'None';
  }, [selectedSeats]);

  return (
    <BookingContext.Provider
      value={{
        selectedMovie,
        selectedShow,
        selectedSeats,
        couponCode,
        appliedCoupon,
        setSelectedMovie,
        setSelectedShow,
        toggleSeat,
        clearSelectedSeats,
        setCouponCode,
        setAppliedCoupon,
        resetBookingFlow,
        originalTotal,
        discountAmount,
        finalTotal,
        seatCategoriesText,
      }}
    >
      {children}
    </BookingContext.Provider>
  );
};

export const useBooking = (): BookingContextType => {
  const context = useContext(BookingContext);
  if (!context) {
    throw new Error('useBooking must be used within a BookingProvider');
  }
  return context;
};

