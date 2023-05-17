import java.util.*;

public class GeneticAlgorithm {

    //liczba punktów
    private int numberOfCities;

    //tablica przechowująca współrzędne punktów
    private double[][] cities;

    //rozmiar populacji
    private int populationSize;

    //maksymalna liczba pokoleń
    private int maxGenerations;

    //prawdopodobieństwo mutacji
    private double mutationProbability;

    //punkt startowy
    private int startingCity;

    //lista przechowująca najlepszą trasę
    private ArrayList<Integer> bestRoute;

    //najlepsza znaleziona trasa
    private double bestDistance;

    public GeneticAlgorithm(int numberOfCities, double[][] cities, int populationSize, int maxGenerations, double mutationProbability, int startingCity) {
        this.numberOfCities = numberOfCities;
        this.cities = cities;
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.mutationProbability = mutationProbability;
        this.startingCity = startingCity;
        bestRoute = new ArrayList<>();
        bestDistance = Double.POSITIVE_INFINITY;
    }

    //metoda inicjująca populację początkową
    private ArrayList<int[]> initializePopulation() {
        ArrayList<int[]> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            int[] route = getRandomRoute();
            population.add(route);
        }
        return population;
    }

    //metoda losująca losową trasę
    private int[] getRandomRoute() {
        int[] route = new int[numberOfCities];
        ArrayList<Integer> citiesList = new ArrayList<>();

        for (int i = 0; i < numberOfCities; i++) {
            if (i != startingCity) {
                citiesList.add(i);
            }
        }

        Collections.shuffle(citiesList);
        route[0] = startingCity;
        for (int i = 1; i < numberOfCities; i++) {
            route[i] = citiesList.get(i - 1);
        }
        return route;
    }

    //metoda obliczająca odległość między dwoma punktami
    private double getDistance(int city1, int city2) {
        double x1 = cities[city1][0];
        double y1 = cities[city1][1];
        double x2 = cities[city2][0];
        double y2 = cities[city2][1];
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    //metoda obliczająca długość trasy
    private double getRouteLength(int[] route) {
        double length = 0;
        for (int i = 0; i < numberOfCities - 1; i++) {
            length += getDistance(route[i], route[i + 1]);
        }
        length += getDistance(route[numberOfCities - 1], route[0]);
        return length;
    }

    //metoda selekcji turniejowej
    private int[] tournamentSelection(ArrayList<int[]> population) {
        int[] bestRoute = null;
        double bestDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < 2; i++) {
            int index = (int) (Math.random() * populationSize);
            int[] route = population.get(index);
            double distance = getRouteLength(route);
            if (bestRoute == null || distance < bestDistance) {
                bestRoute = route;
                bestDistance = distance;
            }
        }

        return bestRoute;
    }

    //metoda krzyżowania
    private int[] crossover(int[] parent1, int[] parent2) {
        int[] child = new int[numberOfCities];
        int startPos = (int) (Math.random() * numberOfCities);
        int endPos = (int) (Math.random() * numberOfCities);
        if (startPos > endPos) {
            int temp = startPos;
            startPos = endPos;
            endPos = temp;
        }
        for (int i = startPos; i <= endPos; i++) {
            child[i] = parent1[i];
        }
        int j = 0;
        for (int i = 0; i < numberOfCities; i++) {
            if (j == startPos) {
                j = endPos + 1;
            }
            if (!containsCity(child, parent2[i])) {
                child[j] = parent2[i];
                j++;
            }
        }
        return child;
    }

    //metoda sprawdzająca, czy miasto jest już na trasie
    private boolean containsCity(int[] route, int city) {
        for (int i = 0; i < numberOfCities; i++) {
            if (route[i] == city) {
                return true;
            }
        }
        return false;
    }

    //metoda mutacji
    private void mutate(int[] route) {
        for (int i = 0; i < numberOfCities; i++) {
            if (Math.random() < mutationProbability) {
                int j = (int) (Math.random() * numberOfCities);
                int temp = route[i];
                route[i] = route[j];
                route[j] = temp;
            }
        }
    }

    //metoda tworząca nową populację
    private ArrayList<int[]> createNewPopulation(ArrayList<int[]> population) {
        ArrayList<int[]> newPopulation = new ArrayList<>();
        while (newPopulation.size() < populationSize) {
            int[] parent1 = tournamentSelection(population);
            int[] parent2 = tournamentSelection(population);
            int[] child = crossover(parent1, parent2);
            mutate(child);
            newPopulation.add(child);
        }
        return newPopulation;
    }

    //metoda rozwiązująca problem komiwojażera metodą genetyczną
    public void solve() {
        ArrayList<int[]> population = initializePopulation();
        for (int i = 0; i < maxGenerations; i++) {
            for (int[] route : population) {
                double distance = getRouteLength(route);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestRoute.clear();
                    for (int city : route) {
                        bestRoute.add(city);
                    }
                }
            }
            population = createNewPopulation(population);
        }
        System.out.println("Najlepsza trasa: " + bestRoute.toString() + startingCity);
        System.out.println("Długość trasy: " + bestDistance);
    }

    public static void main(String[] args) {
        // Wczytanie danych wejściowych (liczby punktów i ich współrzędnych)
        Scanner scanner = new Scanner(System.in);
        System.out.print("Podaj liczbę punktów: ");
        int numPoints = scanner.nextInt();
        double[][] points = new double[numPoints][2];
        System.out.println("Podaj współrzędne punktów:");
        for (int i = 0; i < numPoints; i++) {
            System.out.print("Punkt " + (i) + ": ");
            points[i][0] = scanner.nextInt();
            points[i][1] = scanner.nextInt();
        }
        System.out.print("Podaj punkt startowy (0-" + (numPoints - 1) + "): ");
        int startPoint = scanner.nextInt();

        // Uruchomienie algorytmu genetycznego
      //  int populationSize = 50; int numGenerations = 100; double mutationRate = 0.02;
        GeneticAlgorithm ga = new GeneticAlgorithm(numPoints,points,8,15,0.05, startPoint);
        ga.solve();
    }
}


