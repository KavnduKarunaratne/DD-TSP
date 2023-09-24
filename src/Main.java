import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of destinations: ");
        int numberOfDestinations = scanner.nextInt();

        int[][] distances = new int[numberOfDestinations][numberOfDestinations];
        Map<Integer, String> invoiceData = new HashMap<>();
        List<int[]> coordinates = new ArrayList<>();

        // Input distances between destinations
        for (int start = 0; start < numberOfDestinations; start++) {
            for (int end = 0; end < numberOfDestinations; end++) {
                if (start == end) {
                    distances[start][end] = -1;
                }else{
                    System.out.print("Enter distance from destination " + (start + 1) + " to destination " + (end + 1) + ": ");
                    distances[start][end] = scanner.nextInt();
                }
            }
        }

        // Input invoice data for each destination
        for (int start = 0; start < numberOfDestinations; start++) {
            System.out.print("Enter invoice data for destination " + (start + 1) + ": ");
            String data = scanner.next();
            invoiceData.put(start, data);
        }

        // Calculate shortest path and store coordinates
        List<Integer> visited = calculateShortestPath(distances, coordinates);
        displayData(visited, invoiceData, coordinates);

        // Serialize data to a file
        String filename = "delivery_data.ser";
        serializeData(visited, invoiceData, coordinates, distances, filename);

        // Deserialize data from a file
        Map<Integer, String> deserializedData = deserializeData(filename);
        List<int[]> deserializedCoordinates = deserializeCoordinates(filename);
        List<Integer> deserializedRoute = deserializeRoute(filename);
        System.out.println("\nDeserialized Route:");
        for (int destination : deserializedRoute) {
            System.out.println("Destination " + (destination + 1));
        }
        System.out.println("\nDeserialized Invoice Data:");
        for (int destination : deserializedData.keySet()) {
            System.out.println("Destination " + (destination + 1) + ": " + deserializedData.get(destination));
        }
        System.out.println("\nDeserialized Coordinates:");
        for (int i = 0; i < deserializedCoordinates.size(); i++) {
            int[] coordinate = deserializedCoordinates.get(i);
            System.out.println("Destination " + (i + 1) + " - X: " + coordinate[0] + ", Y: " + coordinate[1]);
        }
    }

    public static List<Integer> calculateShortestPath(int[][] distances, List<int[]> coordinates) {
        int numberOfDestinations = distances.length;
        List<Integer> visited = new ArrayList<>();
        int currentDestination = 0; // Start from the first destination

        while (visited.size() < numberOfDestinations) {
            visited.add(currentDestination);
            int nextDestination = findNextDestination(currentDestination, visited, distances);
            coordinates.add(new int[]{currentDestination, nextDestination}); // Store coordinates
            currentDestination = nextDestination;
        }

        return visited;
    }

    public static int findNextDestination(int startingDestination, List<Integer> visited, int[][] distances) {
        int closestDestination = -1;
        int minDistance = Integer.MAX_VALUE;

        for (int destination = 0; destination < distances.length; destination++) {
            if (!visited.contains(destination) && destination != startingDestination) {
                int distance = distances[startingDestination][destination];
                if (distance < minDistance) {
                    minDistance = distance;
                    closestDestination = destination;
                }
            }
        }

        return closestDestination;
    }

    public static void displayData(List<Integer> route, Map<Integer, String> invoiceData, List<int[]> coordinates) {
        System.out.println("\nShortest Delivery Route:");
        for (int i = 0; i < route.size(); i++) {
            int destination = route.get(i);
            int[] coordinate = coordinates.get(i);
            System.out.println("Destination " + (destination + 1) + ": " + invoiceData.get(destination));
            System.out.println("Coordinates - X: " + coordinate[0] + ", Y: " + coordinate[1]);
        }
    }

    public static void serializeData(List<Integer> visited, Map<Integer, String> invoiceData, List<int[]> coordinates, int[][] distances, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(visited);
            oos.writeObject(invoiceData);
            oos.writeObject(coordinates);
            oos.writeObject(distances);
            System.out.println("\nData has been serialized to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Integer> deserializeRoute(String filename) {
        List<Integer> route = new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            route = (List<Integer>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return route;
    }

    public static Map<Integer, String> deserializeData(String filename) {
        Map<Integer, String> data = new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            ois.readObject(); // Skip visited
            data = (Map<Integer, String>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static List<int[]> deserializeCoordinates(String filename) {
        List<int[]> coordinates = new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            ois.readObject(); // Skip visited
            ois.readObject(); // Skip invoiceData
            coordinates = (List<int[]>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return coordinates;
    }

}