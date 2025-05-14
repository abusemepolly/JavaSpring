package model;

/** Одно кресло: свободно или занято */
public class Seat {
    private boolean occupied;

    public boolean isOccupied() {
        return occupied;
    }

    /** Отметить как занятое */
    public void occupy() {
        occupied = true;
    }
}
