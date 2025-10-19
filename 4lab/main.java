import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Library lib = new Library();

        System.out.println("\n0) Library is empty:");
        print(lib.getAllBooks());

        System.out.println("\n1) addBook(Book)");
        // Демонстрационные данные (другие авторы/книги)
        System.out.println(lib.addBook(new Book("Мастер и Маргарита", "Михаил Булгаков", 1967)));
        System.out.println(lib.addBook(new Book("Евгений Онегин", "Александр Пушкин", 1833)));
        System.out.println(lib.addBook(new Book("Мёртвые души", "Николай Гоголь", 1842)));
        System.out.println(lib.addBook(new Book("Чайка", "Антон Чехов", 1896)));
        // Попытка добавить дубликат
        System.out.println(lib.addBook(new Book("Мастер и Маргарита", "Михаил Булгаков", 1967)));

        System.out.println("\n1.1) All books (sorted by year):");
        print(lib.getAllBooksSortedByYear());

        System.out.println("\n2) removeBook(Book)");
        System.out.println("removeBook('Чайка', 'Антон Чехов', 1896)");
        System.out.println(lib.removeBook(new Book("Чайка", "Антон Чехов", 1896)));

        System.out.println("\n2.1) All books after removal:");
        print(lib.getAllBooks());

        System.out.println("\n3) Unique authors:");
        printStrings(lib.getUniqueAuthors());

        System.out.println("\n4) Author statistics:");
        lib.getAuthorStatistics().forEach((a, c) -> System.out.println(a + ": " + c));

        System.out.println("\n5) findBooksByAuthor('Михаил Булгаков'):");
        print(lib.findBooksByAuthor("Михаил Булгаков"));

        System.out.println("\n6) findBooksByYear(1842):");
        print(lib.findBooksByYear(1842));
    }

    // Утилиты печати
    private static void print(List<Book> books) { books.forEach(System.out::println); }
    private static void printStrings(Collection<String> items) { items.forEach(System.out::println); }
}

final class Book {
    // Инкапсулированные неизменяемые поля
    private final String title;
    private final String author;
    private final int year;

    public Book(String title, String author, int year) {
        this.title = Objects.requireNonNull(title);
        this.author = Objects.requireNonNull(author);
        this.year = year;
    }

    // Геттеры
    public String getTitle()  { return title; }
    public String getAuthor() { return author; }
    public int getYear()      { return year; }

    @Override
    public String toString() {
        return String.format("%s (%s, %d)", title, author, year);
    }

    // Равенство по всем полям (для contains/remove)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book other = (Book) o;
        return year == other.year
                && title.equals(other.title)
                && author.equals(other.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, year);
    }
}

class Library {
    // Единственный источник истины — список книг
    private final List<Book> books = new ArrayList<>();

    // Добавление без дублей; true — добавлено, false — уже есть
    public boolean addBook(Book book) {
        if (books.contains(book)) return false;
        return books.add(book);
    }

    // Удаление; true — удалено, false — не найдено
    public boolean removeBook(Book book) {
        return books.remove(book);
    }

    // Поиск по автору (регистронезависимо)
    public List<Book> findBooksByAuthor(String author) {
        String needle = author.toLowerCase(Locale.ROOT);
        return books.stream()
                .filter(b -> b.getAuthor().toLowerCase(Locale.ROOT).equals(needle))
                .collect(Collectors.toList());
    }

    // Поиск по году
    public List<Book> findBooksByYear(int year) {
        return books.stream()
                .filter(b -> b.getYear() == year)
                .collect(Collectors.toList());
    }

    // Все книги (копия)
    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    // Все книги, отсортированные по году
    public List<Book> getAllBooksSortedByYear() {
        return books.stream()
                .sorted(Comparator.comparingInt(Book::getYear))
                .collect(Collectors.toList());
    }

    // Уникальные авторы (в порядке добавления)
    public Set<String> getUniqueAuthors() {
        return books.stream()
                .map(Book::getAuthor)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    // Статистика автор -> количество
    public Map<String, Long> getAuthorStatistics() {
        return books.stream()
                .collect(Collectors.groupingBy(Book::getAuthor, LinkedHashMap::new, Collectors.counting()));
    }
}
