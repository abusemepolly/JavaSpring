package model;

import java.util.ArrayList;
import java.util.List;

/** Кинотеатр хранит имя и список залов */
public class Cinema {
    private final String name;
    private final List<Hall> halls = new ArrayList<>();

    public Cinema(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Hall> getHalls() {
        return halls;
    }

    /** Добавить новый зал в кинотеатр */
    public void addHall(Hall hall) {
        halls.add(hall);
    }
}
