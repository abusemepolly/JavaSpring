package model;

/** Фильм с названием и длительностью в минутах */
public record Movie(String title, int duration) {
    @Override
    public String toString() {
        return title + " (" + duration + " мин)";
    }
}
