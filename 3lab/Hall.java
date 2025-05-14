package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Зал с определённым количеством рядов и мест, хранит сеансы */
public class Hall {
    private final String name;
    private final int rows;
    private final int cols;
    private final List<Session> sessions = new ArrayList<>();

    public Hall(String name, int rows, int cols) {
        this.name = name;
        this.rows = rows;
        this.cols = cols;
    }

    public String getName() {
        return name;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    /** Создать новый сеанс в этом зале */
    public void addSession(Movie movie, LocalDateTime dateTime) {
        sessions.add(new Session(movie, dateTime, rows, cols));
    }
}
