package br.dev.viniciusleonel.backend_challenge.utils;

public class NumberUtils {

    private NumberUtils() {
        // Construtor privado para evitar instanciação
    }

    public static boolean isPrime(int number) {
        if (number <= 1) return false;
        if (number == 2) return true;
        if (number % 2 == 0) return false;
        for (int i = 3; i <= Math.sqrt(number); i += 2) {
            if (number % i == 0) return false;
        }
        return true;
    }
}

