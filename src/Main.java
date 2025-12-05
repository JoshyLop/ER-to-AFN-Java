import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("===================================================");
        System.out.println("  Convertidor de ER a AFN (Método de Thompson) ");
        System.out.println("===================================================");
        System.out.println("\nOperadores soportados:");
        System.out.println("  | (alternancia)");
        System.out.println("  * (Kleene - cero o más)");
        System.out.println("  + (una o más)");
        System.out.println("  ? (cero o uno)");
        System.out.println("  () (agrupación)");
        
        while (true) {
            System.out.print("\nIngresa una expresión regular (o 'salir' para terminar): ");
            String expresionR = scanner.nextLine().trim();
            
            if (expresionR.equalsIgnoreCase("salir")) {
                System.out.println("¡Hasta luego!");
                break;
            }
            
            if (expresionR.isEmpty()) {
                System.out.println("Por favor ingresa una expresión válida.");
                continue;
            }
            
            System.out.println("EXPRESIÓN REGULAR: " + expresionR);

            try {
                ConversorRegexAFN conversor = new ConversorRegexAFN(expresionR);
                AFN afn = conversor.convertir();
                afn.mostrar();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        
        scanner.close();
    }
}
