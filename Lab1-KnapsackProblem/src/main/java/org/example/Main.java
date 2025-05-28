package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Podaj liczbę rodzajów przedmiotów (n): ");
        int n = scanner.nextInt();

        System.out.print("Podaj ziarno losowania (seed): ");
        int seed = scanner.nextInt();

        System.out.print("Podaj pojemność plecaka (capacity): ");
        int capacity = scanner.nextInt();

        int lowerBound = 1;
        int upperBound = 10;

        // Tworzenie instancji problemu
        Problem problem = new Problem(n, seed, lowerBound, upperBound);


        // Rozwiązywanie problemu algorytmem Dantziga
        Result result = problem.solve(capacity);
        System.out.println(result);
    }
}