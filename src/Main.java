public class Main {
    public static void main(String[] args) {
        // Ejemplos de expresiones regulares
        String[] ejemplos = {
            "a",           // Símbolo simple
            "ab",          // Concatenación
            "a|b",         // Alternancia
            "a*",          // Kleene
            "a+",          // Una o más
            "a?",          // Opcional
            "(a|b)*",      // Combinado
            "a(b|c)*d"     // Más complejo
        };

        for (String regex : ejemplos) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("EXPRESIÓN REGULAR: " + regex);
            System.out.println("=".repeat(50));

            try {
                ConversorRegexAFN conversor = new ConversorRegexAFN(regex);
                AFN afn = conversor.convertir();
                afn.mostrar();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            System.out.println("\n");
        }
    }
}
