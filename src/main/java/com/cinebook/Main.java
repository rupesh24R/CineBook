package com.cinebook;

import com.cinebook.app.CineApp;

/**
 * CineBook - Console-Based Movie Ticket Booking System
 *
 * Application entry point.
 * Demonstrates: Object instantiation and application bootstrapping.
 */
public class Main {
    /**
     * Main method running CineBook.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        CineApp app = new CineApp();
        app.start();
    }
}

