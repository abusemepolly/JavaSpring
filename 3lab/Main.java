package app;

import model.*;
import service.AuthService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final List<Cinema> cinemas = new ArrayList<>();
    private static final List<Movie> movies = new ArrayList<>();

    public static void main(String[] args) {
        initializeTestData();

        System.out.println("ВХОД");
        System.out.println("Админ: admin / admin");
        System.out.println("Пользователь: user / user\n");

        // Основной цикл авторизации
        while (true) {
            System.out.print("Логин: ");
            String login = scanner.nextLine().trim();
            System.out.print("Пароль: ");
            String password = scanner.nextLine().trim();

            String role = AuthService.login(login, password);
            if (role == null) {
                System.out.println("Неверные учетные данные. Попробуйте снова.\n");
                continue;
            }

            if (role.equals("admin")) {
                adminMenu();
            } else {
                userMenu();
            }

            System.out.print("1 - Войти снова, 2 - Выйти: ");
            if (scanner.nextLine().trim().equals("2")) {
                System.out.println("\nВЫХОД\n");
                break;
            }
        }
    }

    private static void adminMenu() {
        while (true) {
            System.out.println("МЕНЮ АДМИНИСТРАТОРА");
            System.out.println("1. Добавить кинотеатр");
            System.out.println("2. Добавить зал");
            System.out.println("3. Добавить фильм");
            System.out.println("4. Добавить сеанс");
            System.out.println("5. Выход");
            System.out.print("Выбор: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    System.out.print("Название кинотеатра: ");
                    cinemas.add(new Cinema(scanner.nextLine().trim()));
                    System.out.println("Кинотеатр добавлен.");
                }
                case "2" -> {
                    Cinema cinema = selectCinema();
                    if (cinema == null) break;
                    System.out.print("Название зала: ");
                    String hallName = scanner.nextLine().trim();
                    System.out.print("Рядов: ");
                    int rows = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Мест в ряду: ");
                    int cols = Integer.parseInt(scanner.nextLine().trim());
                    cinema.addHall(new Hall(hallName, rows, cols));
                    System.out.println("Зал добавлен.");
                }
                case "3" -> {
                    System.out.print("Название фильма: ");
                    String title = scanner.nextLine().trim();
                    System.out.print("Длительность (минут): ");
                    int duration = Integer.parseInt(scanner.nextLine().trim());
                    movies.add(new Movie(title, duration));
                    System.out.println("Фильм добавлен.");
                }
                case "4" -> addSession();
                case "5" -> {
                    System.out.println();
                    return;
                }
                default -> System.out.println("Неверный выбор.");
            }
        }
    }

    private static void addSession() {
        System.out.println("ДОБАВЛЕНИЕ СЕАНСА");
        Cinema cinema = selectCinema();
        if (cinema == null) return;
        Hall hall = selectHall(cinema);
        if (hall == null) return;
        Movie movie = selectMovie();
        if (movie == null) return;

        while (true) {
            try {
                System.out.print("Дата (yyyy-MM-dd или 0 - отмена): ");
                String date = scanner.nextLine().trim();
                if (date.equals("0")) return;
                System.out.print("Время (HH:mm или 0 - отмена): ");
                String time = scanner.nextLine().trim();
                if (time.equals("0")) return;

                // Парсим дату и время вместе через форматтер
                LocalDateTime start = LocalDateTime.parse(date + " " + time, DTF);

                // Проверка пересечений с уже добавленными сеансами
                boolean conflict = hall.getSessions().stream().anyMatch(s -> {
                    LocalDateTime existStart = s.getDateTime();
                    LocalDateTime existEnd = existStart.plusMinutes(s.getMovie().duration());
                    LocalDateTime newEnd = start.plusMinutes(movie.duration());
                    return !(start.isAfter(existEnd) || newEnd.isBefore(existStart));
                });

                if (conflict) {
                    System.out.println("Ошибка: время пересекается с другим сеансом.");
                } else {
                    hall.addSession(movie, start);
                    System.out.println("Сеанс добавлен.");
                    break;
                }
            } catch (Exception e) {
                System.out.println("Неверный формат даты или времени.");
            }
        }
    }

    private static void userMenu() {
        while (true) {
            System.out.println("МЕНЮ ПОЛЬЗОВАТЕЛЯ");
            System.out.println("1. Список фильмов");
            System.out.println("2. Купить билет");
            System.out.println("3. Посмотреть план зала");
            System.out.println("4. Выход");
            System.out.print("Выбор: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> movies.forEach(System.out::println);
                case "2" -> buyTicket();
                case "3" -> {
                    Cinema cin = selectCinema();
                    if (cin == null) break;
                    Hall hall = selectHall(cin);
                    if (hall == null) break;
                    Session sess = selectSession(hall);
                    if (sess == null) break;
                    sess.printSeatPlan();
                }
                case "4" -> {
                    System.out.println();
                    return;
                }
                default -> System.out.println("Неверный ввод.");
            }
        }
    }

    private static void buyTicket() {
        System.out.println("ПОКУПКА БИЛЕТА");
        Movie movie = selectMovie();
        if (movie == null) return;

        var choices = getSessionChoices(movie);
        if (choices.isEmpty()) {
            System.out.println("Нет доступных сеансов.");
            return;
        }

        // Вывод доступных сеансов
        for (int i = 0; i < choices.size(); i++) {
            var sc = choices.get(i);
            System.out.printf("%d. %s, %s, %s%n",
                    i+1,
                    sc.cinema().getName(),
                    sc.hall().getName(),
                    sc.session().getDateTime().format(DTF)
            );
        }

        Session session;
        while (true) {
            System.out.print("Выберите сеанс (0 - отмена): ");
            String line = scanner.nextLine().trim();
            if (line.equals("0")) return;
            int idx = Integer.parseInt(line) - 1;
            if (idx >= 0 && idx < choices.size()) {
                session = choices.get(idx).session();
                break;
            }
            System.out.println("Неверный выбор.");
        }

        session.printSeatPlan();

        // Сколько билетов
        System.out.print("Сколько билетов (0 - отмена): ");
        int count = Integer.parseInt(scanner.nextLine().trim());
        if (count <= 0) return;

        // Покупка
        for (int i = 0; i < count; i++) {
            int row, col;
            while (true) {
                System.out.printf("Ряд (1-%d): ", session.getSeats().length);
                row = Integer.parseInt(scanner.nextLine().trim()) - 1;
                if (row >= 0 && row < session.getSeats().length) break;
                System.out.println("Неверный ряд.");
            }
            while (true) {
                System.out.printf("Место (1-%d): ", session.getSeats()[0].length);
                col = Integer.parseInt(scanner.nextLine().trim()) - 1;
                if (col >= 0 && col < session.getSeats()[0].length) break;
                System.out.println("Неверное место.");
            }
            if (session.getSeats()[row][col].isOccupied()) {
                System.out.println("Уже занято!");
                i--;
            } else {
                session.getSeats()[row][col].occupy();
                System.out.printf("Билет куплен: ряд %d, место %d%n", row+1, col+1);
            }
        }
    }

    // Поиск всех сеансов с свободными местами
    private static List<SessionChoice> getSessionChoices(Movie movie) {
        List<SessionChoice> choices = new ArrayList<>();
        for (Cinema c : cinemas) {
            for (Hall h : c.getHalls()) {
                for (Session s : h.getSessions()) {
                    boolean hasFree = Arrays.stream(s.getSeats())
                                            .flatMap(Arrays::stream)
                                            .anyMatch(seat -> !seat.isOccupied());
                    if (s.getMovie().equals(movie) && hasFree) {
                        choices.add(new SessionChoice(c, h, s));
                    }
                }
            }
        }
        return choices;
    }

    // Меню выбора кинотеатра, зала, сеанса, фильма (аналогично оригиналу)
    private static Cinema selectCinema() { /* … */ }
    private static Hall selectHall(Cinema c) { /* … */ }
    private static Movie selectMovie() { /* … */ }
    private static Session selectSession(Hall h) { /* … */ }

    // Тестовые данные для старта приложения
    private static void initializeTestData() {
        movies.clear();
        movies.add(new Movie("Дюна", 100));
        movies.add(new Movie("Убить Билла 2", 117));

        Cinema cinema = new Cinema("Эпицентр");
        Hall hall = new Hall("Зал 1", 5, 5);
        hall.addSession(movies.get(0), LocalDateTime.of(2025, 6, 15, 14, 0));
        hall.addSession(movies.get(1), LocalDateTime.of(2025, 6, 15, 17, 30));
        cinema.addHall(hall);

        cinemas.clear();
        cinemas.add(cinema);
    }
}
