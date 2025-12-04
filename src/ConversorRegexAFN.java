import java.util.Stack;

public class ConversorRegexAFN {
    private int contadorEstados;
    private String regex;

    public ConversorRegexAFN(String regex) {
        this.regex = regex;
        this.contadorEstados = 0;
    }

    private Estado crearEstado() {
        return new Estado(contadorEstados++);
    }

    private AFN crearAFNSimbolo(String simbolo) {
        Estado inicial = crearEstado();
        Estado final = crearEstado();
        AFN afn = new AFN(inicial, final);
        
        afn.agregarTransicion(new Transicion(inicial, final, simbolo));
        System.out.println("  ✓ Símbolo '" + simbolo + "': " + inicial + " -> " + final);
        
        return afn;
    }

    private AFN unir(AFN afn1, AFN afn2) {
        // Concatenación: conectar final de afn1 con inicial de afn2
        Estado inicial = afn1.getEstadoInicial();
        Estado final = afn2.getEstadoFinal();
        
        afn1.getEstadoFinal().setAceptacion(false);
        
        for (Transicion t : afn2.getTransiciones()) {
            afn1.agregarTransicion(t);
        }
        
        afn1.agregarTransicion(new Transicion(
            afn1.getEstadoFinal(), 
            afn2.getEstadoInicial(), 
            "ε"
        ));
        
        afn1.agregarEstado(afn2.getEstadoFinal());
        
        System.out.println("  ✓ Concatenación: q" + inicial.getId() + " ... q" + final.getId());
        
        return new AFN(inicial, final);
    }

    private AFN alternancia(AFN afn1, AFN afn2) {
        // Crear nuevos estados inicial y final
        Estado novoInicial = crearEstado();
        Estado novoFinal = crearEstado();
        
        AFN resultado = new AFN(novoInicial, novoFinal);
        
        // Conectar inicial con ambos AFN
        resultado.agregarTransicion(new Transicion(novoInicial, afn1.getEstadoInicial(), "ε"));
        resultado.agregarTransicion(new Transicion(novoInicial, afn2.getEstadoInicial(), "ε"));
        
        // Conectar finales de ambos con el nuevo final
        afn1.getEstadoFinal().setAceptacion(false);
        afn2.getEstadoFinal().setAceptacion(false);
        
        resultado.agregarTransicion(new Transicion(afn1.getEstadoFinal(), novoFinal, "ε"));
        resultado.agregarTransicion(new Transicion(afn2.getEstadoFinal(), novoFinal, "ε"));
        
        // Agregar todos los estados y transiciones
        for (Estado e : afn1.getEstados()) resultado.agregarEstado(e);
        for (Estado e : afn2.getEstados()) resultado.agregarEstado(e);
        for (Transicion t : afn1.getTransiciones()) resultado.agregarTransicion(t);
        for (Transicion t : afn2.getTransiciones()) resultado.agregarTransicion(t);
        
        System.out.println("  ✓ Alternancia (|): q" + novoInicial.getId() + " ... q" + novoFinal.getId());
        
        return resultado;
    }

    private AFN kleene(AFN afn) {
        // Crear nuevos estados inicial y final
        Estado novoInicial = crearEstado();
        Estado novoFinal = crearEstado();
        
        AFN resultado = new AFN(novoInicial, novoFinal);
        
        // Conectar inicial con el AFN
        resultado.agregarTransicion(new Transicion(novoInicial, afn.getEstadoInicial(), "ε"));
        
        // Conectar final del AFN con el nuevo final
        afn.getEstadoFinal().setAceptacion(false);
        resultado.agregarTransicion(new Transicion(afn.getEstadoFinal(), novoFinal, "ε"));
        
        // Ciclo: final vuelve al inicial (Kleene)
        resultado.agregarTransicion(new Transicion(afn.getEstadoFinal(), afn.getEstadoInicial(), "ε"));
        
        // Bypass: inicial va directo al final
        resultado.agregarTransicion(new Transicion(novoInicial, novoFinal, "ε"));
        
        // Agregar todos los estados y transiciones del AFN original
        for (Estado e : afn.getEstados()) resultado.agregarEstado(e);
        for (Transicion t : afn.getTransiciones()) resultado.agregarTransicion(t);
        
        System.out.println("  ✓ Kleene (*): q" + novoInicial.getId() + " ... q" + novoFinal.getId());
        
        return resultado;
    }

    private AFN masUno(AFN afn) {
        // a+ = a.a*
        System.out.println("  ✓ Más uno (+): creando a+");
        AFN kleeneAFN = kleene(afn);
        return unir(afn, kleeneAFN);
    }

    private AFN opcional(AFN afn) {
        // a? = ε | a
        Estado novoInicial = crearEstado();
        Estado novoFinal = crearEstado();
        
        AFN resultado = new AFN(novoInicial, novoFinal);
        
        // Camino 1: directo (epsilon)
        resultado.agregarTransicion(new Transicion(novoInicial, novoFinal, "ε"));
        
        // Camino 2: a través del AFN
        resultado.agregarTransicion(new Transicion(novoInicial, afn.getEstadoInicial(), "ε"));
        afn.getEstadoFinal().setAceptacion(false);
        resultado.agregarTransicion(new Transicion(afn.getEstadoFinal(), novoFinal, "ε"));
        
        for (Estado e : afn.getEstados()) resultado.agregarEstado(e);
        for (Transicion t : afn.getTransiciones()) resultado.agregarTransicion(t);
        
        System.out.println("  ✓ Opcional (?): q" + novoInicial.getId() + " ... q" + novoFinal.getId());
        
        return resultado;
    }

    public AFN convertir() {
        System.out.println("\n>>> Iniciando conversión de: " + regex);
        String regexProcesada = agregarConcatenacionExplicita(regex);
        System.out.println(">>> Expresión procesada: " + regexProcesada);
        
        Stack<AFN> pila = new Stack<>();
        Stack<Character> operadores = new Stack<>();

        int i = 0;
        while (i < regexProcesada.length()) {
            char c = regexProcesada.charAt(i);

            if (c == '(') {
                operadores.push(c);
            } else if (c == ')') {
                while (!operadores.isEmpty() && operadores.peek() != '(') {
                    char op = operadores.pop();
                    procesarOperador(op, pila);
                }
                operadores.pop(); // Saca el '('
            } else if (c == '|') {
                while (!operadores.isEmpty() && operadores.peek() != '(' && operadores.peek() != '.') {
                    char op = operadores.pop();
                    procesarOperador(op, pila);
                }
                operadores.push(c);
            } else if (c == '*' || c == '+' || c == '?') {
                procesarOperador(c, pila);
            } else if (c == '.') {
                while (!operadores.isEmpty() && operadores.peek() == '.') {
                    char op = operadores.pop();
                    procesarOperador(op, pila);
                }
                operadores.push(c);
            } else {
                // Es un símbolo
                pila.push(crearAFNSimbolo(String.valueOf(c)));
            }

            i++;
        }

        while (!operadores.isEmpty()) {
            char op = operadores.pop();
            if (op != '(') {
                procesarOperador(op, pila);
            }
        }

        AFN resultado = pila.pop();
        System.out.println(">>> Conversión completada\n");
        return resultado;
    }

    private void procesarOperador(char operador, Stack<AFN> pila) {
        if (operador == '*') {
            AFN afn = pila.pop();
            pila.push(kleene(afn));
        } else if (operador == '+') {
            AFN afn = pila.pop();
            pila.push(masUno(afn));
        } else if (operador == '?') {
            AFN afn = pila.pop();
            pila.push(opcional(afn));
        } else if (operador == '.') {
            AFN afn2 = pila.pop();
            AFN afn1 = pila.pop();
            pila.push(unir(afn1, afn2));
        } else if (operador == '|') {
            AFN afn2 = pila.pop();
            AFN afn1 = pila.pop();
            pila.push(alternancia(afn1, afn2));
        }
    }

    private String agregarConcatenacionExplicita(String regex) {
        StringBuilder resultado = new StringBuilder();
        
        for (int i = 0; i < regex.length(); i++) {
            char actual = regex.charAt(i);
            resultado.append(actual);

            if (i < regex.length() - 1) {
                char siguiente = regex.charAt(i + 1);
                
                boolean necesitaConcatenacion = false;

                if ((Character.isLetterOrDigit(actual) || actual == ')' || actual == '*' || actual == '+' || actual == '?') &&
                    (Character.isLetterOrDigit(siguiente) || siguiente == '(')) {
                    necesitaConcatenacion = true;
                }

                if (actual == ')' && siguiente == '(') {
                    necesitaConcatenacion = true;
                }

                if (necesitaConcatenacion && siguiente != '|') {
                    resultado.append('.');
                }
            }
        }

        return resultado.toString();
    }
}
