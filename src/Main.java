import java.util.Scanner;

public class Main {
    
    static String crearLinea(int cantidad) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cantidad; i++) {
            sb.append("=");
        }
        return sb.toString();
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║  Convertidor de ER a AFN (Thompson)   ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("\nOperadores soportados:");
        System.out.println("  | (alternancia)");
        System.out.println("  * (Kleene - cero o más)");
        System.out.println("  + (una o más)");
        System.out.println("  ? (cero o uno)");
        System.out.println("  () (agrupación)");
        System.out.println("\nEjemplos: a, ab, a|b, a*, (a|b)*, a(b|c)*d");
        
        while (true) {
            System.out.print("\nIngresa una expresión regular (o 'salir' para terminar): ");
            String regex = scanner.nextLine().trim();
            
            if (regex.equalsIgnoreCase("salir")) {
                System.out.println("¡Hasta luego!");
                break;
            }
            
            if (regex.isEmpty()) {
                System.out.println("Por favor ingresa una expresión válida.");
                continue;
            }
            
            System.out.println("\n" + crearLinea(50));
            System.out.println("EXPRESIÓN REGULAR: " + regex);
            System.out.println(crearLinea(50));

            try {
                ConversorRegexAFN conversor = new ConversorRegexAFN(regex);
                AFN afn = conversor.convertir();
                afn.mostrar();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        
        scanner.close();
    }
}
