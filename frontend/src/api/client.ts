import axios from 'axios';
import {
  Movie,
  Show,
  Seat,
  Booking,
  BookingRequest,
  CancellationResult,
  CouponValidation,
} from '../types';

const API_BASE_URL = import.meta.env.VITE_API_URL || '';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Movies
export const fetchMovies = async (): Promise<Movie[]> => {
  const response = await apiClient.get<Movie[]>('/api/movies');
  return response.data;
};

export const fetchMovieById = async (id: number | string): Promise<Movie> => {
  const response = await apiClient.get<Movie>(`/api/movies/${id}`);
  return response.data;
};

// Shows
export const fetchAllShows = async (): Promise<Show[]> => {
  const response = await apiClient.get<Show[]>('/api/shows');
  return response.data;
};

export const fetchShowById = async (id: number | string): Promise<Show> => {
  const response = await apiClient.get<Show>(`/api/shows/${id}`);
  return response.data;
};

export const fetchShowsByMovieId = async (movieId: number | string): Promise<Show[]> => {
  const response = await apiClient.get<Show[]>(`/api/movies/${movieId}/shows`);
  return response.data;
};

// Seats
export const fetchSeatsByShowId = async (showId: number | string): Promise<Seat[]> => {
  const response = await apiClient.get<Seat[]>(`/api/shows/${showId}/seats`);
  return response.data;
};

// Bookings
export const createBooking = async (payload: BookingRequest): Promise<Booking> => {
  const response = await apiClient.post<Booking>('/api/bookings', payload);
  return response.data;
};

export const fetchBookingById = async (bookingId: string): Promise<Booking> => {
  const response = await apiClient.get<Booking>(`/api/bookings/${bookingId}`);
  return response.data;
};

export const fetchAllBookings = async (): Promise<Booking[]> => {
  const response = await apiClient.get<Booking[]>('/api/bookings');
  return response.data;
};

export const cancelBooking = async (bookingId: string): Promise<CancellationResult> => {
  const response = await apiClient.post<CancellationResult>(`/api/bookings/${bookingId}/cancel`);
  return response.data;
};

// Coupons
export const validateCoupon = async (
  code: string,
  totalAmount: number
): Promise<CouponValidation> => {
  const response = await apiClient.post<CouponValidation>('/api/coupons/validate', {
    code,
    totalAmount,
  });
  return response.data;
};

