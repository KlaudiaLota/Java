Celem projektu była implementacja rozwiązania **problemu plecakowego z powtarzaniem** (ang. *Unbounded Knapsack Problem*). W tym celu opracowano aplikację konsolową w języku **Java**, która umożliwia użytkownikowi wygenerowanie instancji problemu na podstawie zadanych parametrów oraz jego rozwiązanie przy użyciu **algorytmu zachłannego wg Dantziga (1957)**.

Po uruchomieniu programu użytkownik proszony jest o wprowadzenie trzech wartości:

* liczby rodzajów przedmiotów (*n*),
* ziarna losowania (*seed*),
* pojemności plecaka (*capacity*).

Na ich podstawie tworzona jest losowa instancja problemu plecakowego – każdy przedmiot ma przypisaną losową wagę i wartość z zakresu od 1 do 10.

Po wygenerowaniu danych, aplikacja przystępuje do rozwiązywania problemu. W tym celu stosowany jest algorytm zachłanny, który:

1. Sortuje wszystkie przedmioty malejąco według stosunku wartości do wagi.
2. Dodaje kolejne przedmioty (w możliwie największej ilości) do plecaka, dopóki pozwala na to jego pojemność.
3. Zwraca przybliżone rozwiązanie z informacją o wybranych przedmiotach, ich liczbie, sumarycznej wadze i wartości.

Przykładowy wynik działania programu:

![Zrzut ekranu 2025-06-02 203948](https://github.com/user-attachments/assets/e2dddc1b-6bbf-466f-bfc1-1011c9a74ac8)

## Testowanie algorytmu

![image](https://github.com/user-attachments/assets/a9f155ad-7816-4750-9b21-26a8342cda93)

Testy weryfikują poprawność działania algorytmu plecakowego w różnych scenariuszach:

1. **Przynajmniej jeden przedmiot powinien zostać wybrany**

  Jeśli istnieje przedmiot, który mieści się w plecaku, wynik nie powinien być pusty:
  ```java
  assertFalse(itemCounts.isEmpty());
  ```

2. **Poprawna liczba wygenerowanych przedmiotów**
  
  Dla zadanej liczby `n` powinno powstać dokładnie `n` przedmiotów:
  ```java
  assertEquals(10, items.size());
  ```

3. **Przedmioty za duże na plecak – wynik pusty**
  
  W przypadku, gdy wszystkie przedmioty są zbyt ciężkie, algorytm nie powinien nic dodać:
  ```java
  Result result = problem.solve(1);
  assertTrue(itemCounts.isEmpty());
  ```

4. **Wartość i waga mieszczą się w zadanym zakresie**
   
  Test sprawdzający, czy generowane dane są w zakresie `[1, 10]`:
  ```java
  assertTrue(item.getWeight() >= 1 && item.getWeight() <= 10);
  ```

5. **Sprawdzenie poprawności działania na konkretnym przykładzie**
   
  Dla znanego zestawu danych weryfikowane są oczekiwane wyniki:
  ```java
  assertEquals(expectedValue, result.getTotalValue());
  assertEquals(expectedWeight, result.getTotalWeight());
  assertEquals(expectedItemCounts, result.getItemCounts());
  ```

6. **Całkowita waga nie przekracza pojemności plecaka**
   
  Sprawdzamy, czy rozwiązanie nie łamie ograniczeń plecaka:
  ```java
  assertTrue(result.getTotalWeight() <= capacity);
  ```


Aplikacja poprawnie rozwiązuje problem plecakowy z powtarzaniem w sposób przybliżony, oferując prosty i przejrzysty interfejs konsolowy. Dzięki testom jednostkowym możliwa jest szybka weryfikacja poprawności implementacji w różnych przypadkach brzegowych.
