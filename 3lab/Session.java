package model;

import java.time.LocalDateTime;

/** Сеанс фильма с матрицей кресел */
public class Session {
    private final Movie movie;
    private final LocalDateTime dateTime;
    private final Seat[][] seats;

    public Session(Movie movie, LocalDateTime dateTime, int rows, int cols) {
        this.movie = movie;
        this.dateTime = dateTime;
        this.seats = new Seat[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                seats[r][c] = new Seat();
            }
        }
    }

    public Movie getMovie() {
        return movie;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Seat[][] getSeats() {
        return seats;
    }

    /** Вывести план зала: [ ] — свободно, [X] — занято */
    public void printSeatPlan() {
        System.out.println("План зала:");
        for (Seat[] row : seats) {
            for (Seat seat : row) {
                System.out.print(seat.isOccupied() ? "[X]" : "[ ]");
            }
            System.out.println();
        }
    }
}
