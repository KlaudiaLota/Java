To repozytorium zostało stworzone w ramach pracy laboratoryjnej. Programy zostały napisane w języku Java. Poniżej znajduje się krótki opis zawartości poszczególnych katalogów.

# [Lab1 - KnapsackProblem](./Lab1-KnapsackProblem)

Program implementuje rozwiązanie problemu plecakowego w języku Java jako aplikację konsolową. Użytkownik podaje liczbę przedmiotów, pojemność plecaka oraz ziarno (seed), na podstawie których generowane są losowe przedmioty o losowej wadze i wartości w zadanym przedziale. Po wprowadzeniu danych uruchamiany jest algorytm zachłanny (algorytm Dantziga), który sortuje przedmioty według stosunku wartości do wagi i dodaje je do plecaka, maksymalizując jego wartość przy zachowaniu ograniczenia wagowego. Program zawiera również zestaw testów jednostkowych, które sprawdzają poprawność działania algorytmu w różnych scenariuszach, takich jak: generowanie danych, poprawność granic wag i wartości, zachowanie algorytmu przy zbyt małej pojemności oraz poprawność wyniku dla zdefiniowanych instancji.

# [Lab2 - Multithreading](./Lab2-Multithreading)

Aplikacja desktopowa w JavaFX umożliwia wczytanie pliku JPG oraz wykonanie na nim wybranych operacji graficznych, takich jak negatyw, progowanie i konturowanie. Przetwarzanie obrazu realizowane jest wielowątkowo, co przyspiesza działanie aplikacji dla większych plików. Program pozwala także na skalowanie i obracanie obrazu oraz zapisanie zmodyfikowanego pliku do katalogu użytkownika, a wszystkie operacje są logowane wraz z informacją o czasie ich wykonania i ewentualnych błędach. Interfejs użytkownika opiera się na pliku FXML i oferuje intuicyjne elementy sterujące, dzięki czemu korzystanie z aplikacji jest wygodne i czytelne.
