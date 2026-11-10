export interface Movie {
  id: number;
  title: string;
  description: string;
  genre: string;
  duration: number; // in minutes
  language: string;
  rating: string;
  posterUrl: string;
  releaseDate: string;
  status: 'NOW_SHOWING' | 'UPCOMING';
}

export interface Show {
  id: number;
  movieId: number;
  movieTitle: string;
  moviePosterUrl: string;
  movieRating: string;
  movieDuration: number;
  showDate: string;
  showTime: string;
  active: boolean;
  availableSeats: number;
  totalSeats: number;
}

export type SeatCategory = 'REGULAR' | 'PREMIUM' | 'VIP';

export interface Seat {
  id: number;
  showId: number;
  seatCode: string; // e.g. "A1", "C4"
  seatType: SeatCategory;
  price: number;
  isBooked: boolean;
}

export interface BookingRequest {
  showId: number;
  seatCodes: string[];
  couponCode?: string;
}

export interface Booking {
  id: number;
  bookingId: string;
  showId: number;
  movieTitle: string;
  moviePosterUrl: string;
  showDate: string;
  showTime: string;
  seatCodes: string[];
  seatCategories: string;
  originalAmount: number;
  discountAmount: number;
  totalAmount: number;
  bookingStatus: 'CONFIRMED' | 'CANCELLED';
  createdAt: string;
  cancelledAt?: string;
}

export interface CancellationResult {
  bookingId: string;
  originalAmount: number;
  refundAmount: number;
  bookingStatus: string;
  cancelledAt: string;
  releasedSeats: string[];
  message: string;
}

export interface CouponValidation {
  code: string;
  valid: boolean;
  discountPercentage: number;
  discountAmount: number;
  finalAmount: number;
  message: string;
}

