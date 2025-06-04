Celem projektu było wykonanie prostej aplikacji desktopowej napisanej w JavaFX, która pozwala użytkownikowi wczytać plik JPG, wykonać na nim wybrane operacje graficzne, a następnie zapisać wynik. Wszystkie operacje przetwarzania obrazu wykonywane są wielowątkowo, co przyspiesza pracę z większymi plikami.

## Główne komponenty

1. **HelloApplication** (klasa startowa) 
   - Uruchamia aplikację i wyświetla główne okno. 
   - Loguje moment uruchomienia i zamknięcia aplikacji. 
   - Ładuje układ graficzny z pliku FXML oraz stosuje arkusz stylów CSS. 
   - Ustawia tytuł okna („Edytor obrazów - JavaFX”) oraz jego wymiary. 

2. **hello-view\.fxml** (układ GUI) 
   - Bazuje na kontenerze typu BorderPane, dzieląc okno na cztery sekcje: górny pasek (top), panel boczny (left), główną przestrzeń roboczą (center) i stopkę (bottom). 
   - W górnym pasku wyświetlane jest logo oraz tytuł aplikacji. 
   - Panel boczny zawiera przyciski do wczytywania, skalowania, zapisywania i obracania obrazu, a także rozwijaną listę dostępnych operacji (negatyw, progowanie, konturowanie) wraz z przyciskiem wykonania wybranej akcji. 
   - W centralnej części znajdują się dwa widoki obrazów (ImageView): po lewej oryginalny obraz, po prawej obraz po przetworzeniu. 
   - Stopka zawiera informacje o autorze (imię, nazwisko i numer indeksu).

3. **HelloController** (kontroler GUI) 
   - Obsługuje wszystkie zdarzenia użytkownika związane z przyciskami i kontrolkami. 
   - Po wczytaniu pliku JPG wyświetla go w widoku oryginalnym i odblokowuje przyciski do dalszych operacji. Loguje nazwę wczytanego pliku oraz czas wczytywania. 
   - Pozwala użytkownikowi wybrać jedną z dostępnych operacji graficznych: 
     * **Negatyw** – wywołuje funkcję generującą negatyw obrazu.
     * **Progowanie** – otwiera okno dialogowe, w którym użytkownik wprowadza wartość progu (0–255), a następnie przekształca obraz w binarny (czarno-biały).
     * **Konturowanie** – stosuje prostą metodę wykrywania krawędzi na podstawie różnicy wartości jasności sąsiadujących pikseli.
   - Po wyborze operacji wynikowy obraz trafia do widoku po prawej stronie, a czas wykonania jest zapisywany w logach. 
   - Pozwala na obracanie obrazu w lewo lub prawo o 90° – każda z tych akcji jest od razu widoczna i zapisywana w logach. 
   - Obsługuje skalowanie obrazu: otwiera okno dialogowe z polami na wpisanie szerokości i wysokości (w zakresie 1–3000 pikseli). Użytkownik może także przywrócić oryginalne wymiary. Po zatwierdzeniu wprowadzonych wartości obraz zostaje przeskalowany i wyświetlony, a czas operacji zapisany. 
   - Umożliwia zapisanie przetworzonego obrazu do katalogu „Pictures” w katalogu domowym użytkownika. Przed zapisem sprawdza, czy użytkownik podał nazwę o długości od 3 do 100 znaków, czy katalog docelowy istnieje (w razie potrzeby tworzy go) oraz czy plik o takiej nazwie nie istnieje już w miejscu docelowym. Po udanym zapisie wyświetla komunikat i zapisuje informację o operacji w logach.

4. **ImageProcessor** (moduł przetwarzania obrazu) 
   - Zawiera metody wykonujące podstawowe algorytmy operujące na pikselach obrazów: generowanie negatywu, progowanie, wykrywanie krawędzi, skalowanie oraz obrót. 
   - W przypadku negatywu, progowania i konturowania działanie jest podzielone na cztery wątki, które równolegle przetwarzają kolejne poziome pasy obrazu. Dzięki temu można wykorzystać wiele rdzeni procesora i zminimalizować czas oczekiwania. 
   - Dla każdej z tych operacji mierzy się czas wykonania (w milisekundach) i zapisuje w pliku logu. 
   - Skalowanie obrazu jest realizowane prostą metodą „nearest neighbor” (każdy piksel nowego obrazu pobierany jest z najbliższego odpowiadającego piksela oryginalnego obrazu). 
   - Obrót o 90° w lewo lub w prawo polega na odpowiednim przepisaniu współrzędnych pikseli z oryginału do nowego bufora.

5. **LoggerService** (serwis logowania) 
   - Zapewnia mechanizm zapisu zdarzeń i ewentualnych błędów do pliku tekstowego „log.txt” w katalogu głównym aplikacji (jeśli plik nie istnieje, zostanie utworzony). 
   - Metoda podstawowa przyjmuje nazwę operacji, poziom logu (np. INFO) oraz czas trwania (milisekundy) i formatuje wpis z bieżącą datą i godziną. 
   - W przypadku błędów (wyjątki podczas przetwarzania obrazu lub zapisu pliku) wywoływana jest osobna metoda, która zapisuje komunikat typu ERROR wraz z treścią wyjątku. 
   - Dzięki temu w każdej chwili można prześledzić historię działań aplikacji, czasy wykonania kolejnych operacji oraz ewentualne problemy. 

## Wygląd aplikacji

![image](https://github.com/user-attachments/assets/e8f80060-e745-4ac0-9f76-e79b7e4a4fc0)


## Funkcjonalności

1. **Wczytywanie obrazu** 
    - Użytkownik klika przycisk „Wczytaj obraz” i wybiera plik JPG. 
    - Aplikacja sprawdza rozszerzenie, wczytuje obraz do pamięci, wyświetla go w oknie oraz odblokowuje pozostałe przyciski. 
    - Czas wczytywania jest mierzony i zapisywany w logu.

2. **Wybór operacji i przetwarzanie obrazu** 
   - Użytkownik wybiera z listy jedną z operacji:
       * **Negatyw** – zmienia każdy piksel na jego kolor odwrotny (np. czerwony na cyjan, zielony na magentę, niebieski na żółty).
       * **Progowanie** – użytkownik wpisuje w dialogu wartość progu; każdy piksel z jasnością powyżej progu staje się biały, a poniżej – czarny.
       * **Konturowanie** – algorytm porównuje jasność wybranego piksela z jasnościami jego sąsiadów w prawo i w dół; jeśli różnica przekracza ustalony próg, pixel staje się biały, inaczej czarny.
   - Wykonanie operacji dzieje się równolegle na czterech wątkach, każdy obsługuje fragment obrazu. 
   - Wynikowa grafika jest wyświetlana po prawej stronie, a czas przetwarzania jest zapisywany.

4. **Skalowanie obrazu**
   - Kliknięcie przycisku „Skaluj obraz” otwiera okno dialogowe z dwoma polami: szerokość i wysokość (zakres 1–3000 pikseli).
   - Użytkownik może również przywrócić oryginalne wymiary, klikając przycisk „Przywróć oryginalny rozmiar”.
   - Po zatwierdzeniu (kliknięciu „OK”) wywoływana jest funkcja skalująca, a zmieniony obraz jest wyświetlany w widoku przetworzonym.

5. **Obrót obrazu**
   - Dwa przyciski: „Obróć w lewo” i „Obróć w prawo”.
   - Każdy obrót o 90° przerysowuje piksele odpowiednio do nowego bufora.
   - Nowy obraz jest wyświetlany, a czas operacji rejestrowany w logach.

6. **Zapis obrazu**
   - Kliknięcie przycisku „Zapisz obraz” otwiera pole tekstowe, w którym użytkownik wpisuje nazwę pliku (3–100 znaków).
   - Aplikacja tworzy folder „Pictures” w katalogu domowym użytkownika (jeśli go nie ma) i sprawdza, czy nie istnieje już plik o podanej nazwie.
   - Następnie zapisuje wynikowy obraz jako plik JPG z białym tłem (konwertuje do RGB) i wyświetla komunikat potwierdzający powodzenie.
   - Informacja o zapisie (nazwa pliku, czas zapisu) trafia do pliku logu.

## Uruchomienie i wymagania

* **Wersja Javy:** 11 lub nowsza (zalecane 17+).
* **Biblioteka JavaFX:** wersja 11+. Należy dołączyć moduły javafx.controls i javafx.fxml.
* **Środowisko IDE:** IntelliJ IDEA, Eclipse lub inny edytor z poprawnie skonfigurowanym JavaFX.

**Kroki uruchomienia:**

1. Sklonuj repozytorium do lokalnego katalogu.
2. Upewnij się, że masz zainstalowaną odpowiednią wersję JavaFX i że w konfiguracji uruchamiania podajesz ścieżkę do biblioteki (np. w VM options:
   `--module-path /ścieżka/do/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml`).
3. Uruchom klasę `HelloApplication`.
4. Po uruchomieniu okna aplikacji możesz rozpocząć pracę: wczytywać obrazy, wykonywać na nich operacje, skalować, obracać oraz zapisywać wyniki.
5. W katalogu projektu pojawi się plik `log.txt`, w którym rejestrowane są wszystkie zdarzenia (uruchomienie, wczytanie pliku, wykonanie operacji, zapis, błędy).

**Przykładowe wpisy w logach**

  * **Uruchomienie aplikacji**
    `[2025-06-04 14:10:00] [INFO] Operacja: Aplikacja uruchomiona, Czas: 0 ms`
  
  * **Wczytanie obrazu**
    `[2025-06-04 14:10:15] [INFO] Operacja: Wczytano obraz: example.jpg, Czas: 120 ms`
  
  * **Negatyw**
    `[2025-06-04 14:10:25] [INFO] Operacja: Negatyw, Czas: 85 ms`
  
  * **Progowanie**
    `[2025-06-04 14:10:40] [INFO] Operacja: Progowanie, Czas: 100 ms`
  
  * **Błąd podczas zapisu**
    `[2025-06-04 14:11:00] [ERROR] Operacja: Błąd zapisu obrazu, Błąd: Nazwa pliku jest nieprawidłowa`

## Podsumowanie

Projekt jest przykładem praktycznego zastosowania JavaFX do tworzenia interfejsu użytkownika oraz wielowątkowego przetwarzania obrazów. Umożliwia on wczytywanie plików JPG, wykonywanie podstawowych operacji graficznych (negatyw, progowanie, wykrywanie krawędzi), skalowanie, obrót oraz zapis wyników. Całość wyposażona jest w prosty serwis logowania, który rejestruje wszystkie działania i ewentualne błędy. Aplikacja może służyć jako podstawa do dalszej rozbudowy.
