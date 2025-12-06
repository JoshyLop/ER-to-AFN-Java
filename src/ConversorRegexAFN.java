import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class ConversorRegexAFN {
    private int contadorEstados;
    private String expresionRegular;
    private Set<Estado> todosLosEstados;
    private Set<Transicion> todasLasTransiciones;

    public ConversorRegexAFN(String expresionRegular) {
        this.expresionRegular = expresionRegular;
        this.contadorEstados = 0;
        this.todosLosEstados = new HashSet<>();
        this.todasLasTransiciones = new HashSet<>();
    }

    private Estado crearEstado() {
        Estado e = new Estado(contadorEstados++);
        todosLosEstados.add(e);
        return e;
    }

    private AFN crearAFNSimbolo(String simbolo) {
        Estado inicial = crearEstado();
        Estado estadoFinal = crearEstado();
        
        Transicion t = new Transicion(inicial, estadoFinal, simbolo);
        todasLasTransiciones.add(t);
        
        AFN afn = new AFN(inicial, estadoFinal);
        afn.agregarTransicion(t);
        
        System.out.println("\n  ✓ Paso 1: Crear símbolo '" + simbolo + "'");
        System.out.println("    Estados creados: " + inicial + " -> " + estadoFinal);
        System.out.println("    Transición: " + t);
        
        return afn;
    }

    private AFN unir(AFN afn1, AFN afn2) {
        // Concatenación: conectar final de afn1 con inicial de afn2
        Estado inicial = afn1.getEstadoInicial();
        Estado estadoFinal = afn2.getEstadoFinal();
        
        // Desmarcar aceptación del final de afn1
        afn1.getEstadoFinal().setAceptacion(false);
        afn2.getEstadoFinal().setAceptacion(false);
        
        // Crear transición epsilon entre final de afn1 e inicial de afn2
        Transicion epsilon = new Transicion(afn1.getEstadoFinal(), afn2.getEstadoInicial(), "ε");
        todasLasTransiciones.add(epsilon);
        
        // Crear nuevo AFN que contenga todos los estados y transiciones
        AFN resultado = new AFN(inicial, estadoFinal);
        
        // Agregar todos los estados de afn1
        for (Estado e : afn1.getEstados()) {
            resultado.agregarEstado(e);
        }
        
        // Agregar todos los estados de afn2
        for (Estado e : afn2.getEstados()) {
            resultado.agregarEstado(e);
        }
        
        // Agregar todas las transiciones de afn1
        for (Transicion tr : afn1.getTransiciones()) {
            resultado.agregarTransicion(tr);
        }
        
        // Agregar todas las transiciones de afn2
        for (Transicion tr : afn2.getTransiciones()) {
            resultado.agregarTransicion(tr);
        }
        
        // Agregar la transición epsilon
        resultado.agregarTransicion(epsilon);
        
        System.out.println("    Estado inicial: " + inicial + " -> Estado final: " + estadoFinal);
        System.out.println("    Total estados en AFN: " + resultado.getEstados().size());
        System.out.println("    Total transiciones: " + resultado.getTransiciones().size());
        
        return resultado;
    }

    private AFN alternancia(AFN afn1, AFN afn2) {
        Estado novoInicial = crearEstado();
        Estado novoFinal = crearEstado();
        
        AFN resultado = new AFN(novoInicial, novoFinal);
        
        // Desmarcar aceptación
        afn1.getEstadoFinal().setAceptacion(false);
        afn2.getEstadoFinal().setAceptacion(false);
        
        // Transiciones epsilon del nuevo inicial a los iniciales de ambos AFN
        Transicion e1 = new Transicion(novoInicial, afn1.getEstadoInicial(), "ε");
        Transicion e2 = new Transicion(novoInicial, afn2.getEstadoInicial(), "ε");
        todasLasTransiciones.add(e1);
        todasLasTransiciones.add(e2);
        
        resultado.agregarTransicion(e1);
        resultado.agregarTransicion(e2);
        
        // Transiciones epsilon de los finales al nuevo final
        Transicion e3 = new Transicion(afn1.getEstadoFinal(), novoFinal, "ε");
        Transicion e4 = new Transicion(afn2.getEstadoFinal(), novoFinal, "ε");
        todasLasTransiciones.add(e3);
        todasLasTransiciones.add(e4);
        
        resultado.agregarTransicion(e3);
        resultado.agregarTransicion(e4);
        
        // Agregar todos los estados
        resultado.agregarEstado(novoInicial);
        resultado.agregarEstado(novoFinal);
        
        for (Estado e : afn1.getEstados()) resultado.agregarEstado(e);
        for (Estado e : afn2.getEstados()) resultado.agregarEstado(e);
        
        // Agregar todas las transiciones
        for (Transicion t : afn1.getTransiciones()) resultado.agregarTransicion(t);
        for (Transicion t : afn2.getTransiciones()) resultado.agregarTransicion(t);
        
        System.out.println("  ✓ Alternancia completada");
        System.out.println("    Estado inicial: " + novoInicial + " -> Estado final: " + novoFinal);
        System.out.println("    Total estados: " + resultado.getEstados().size());
        System.out.println("    Total transiciones: " + resultado.getTransiciones().size());
        
        return resultado;
    }

    private AFN kleene(AFN afn) {
        // Crear nuevos estados inicial y final
        Estado novoInicial = crearEstado();
        Estado novoFinal = crearEstado();
        
        AFN resultado = new AFN(novoInicial, novoFinal);
        
        afn.getEstadoFinal().setAceptacion(false);
        
        // Transición epsilon: inicial nuevo -> inicial AFN
        Transicion e1 = new Transicion(novoInicial, afn.getEstadoInicial(), "ε");
        todasLasTransiciones.add(e1);
        resultado.agregarTransicion(e1);
        
        // Transición epsilon: final AFN -> final nuevo
        Transicion e2 = new Transicion(afn.getEstadoFinal(), novoFinal, "ε");
        todasLasTransiciones.add(e2);
        resultado.agregarTransicion(e2);
        
        // Ciclo: final vuelve al inicial (Kleene)
        Transicion e3 = new Transicion(afn.getEstadoFinal(), afn.getEstadoInicial(), "ε");
        todasLasTransiciones.add(e3);
        resultado.agregarTransicion(e3);
        
        // Bypass: inicial nuevo va directo al final nuevo
        Transicion e4 = new Transicion(novoInicial, novoFinal, "ε");
        todasLasTransiciones.add(e4);
        resultado.agregarTransicion(e4);
        
        // Agregar estados
        resultado.agregarEstado(novoInicial);
        resultado.agregarEstado(novoFinal);
        for (Estado e : afn.getEstados()) resultado.agregarEstado(e);
        
        // Agregar transiciones del AFN original
        for (Transicion t : afn.getTransiciones()) resultado.agregarTransicion(t);
        
        System.out.println("  ✓ Kleene (*) completado");
        System.out.println("    Estado inicial: " + novoInicial + " -> Estado final: " + novoFinal);
        System.out.println("    Total estados: " + resultado.getEstados().size());
        System.out.println("    Total transiciones: " + resultado.getTransiciones().size());
        
        return resultado;
    }

    private AFN masUno(AFN afn) {
        // a+ = a.a*
        System.out.println("  ✓ Aplicando operador (+)");
        System.out.println("    Construcción: a+ = a.a*");
        AFN kleeneAFN = kleene(afn);
        return unir(afn, kleeneAFN);
    }

    private AFN opcional(AFN afn) {
        Estado novoInicial = crearEstado();
        Estado novoFinal = crearEstado();
        
        AFN resultado = new AFN(novoInicial, novoFinal);
        
        afn.getEstadoFinal().setAceptacion(false);
        
        // Camino 1: directo (epsilon)
        Transicion e1 = new Transicion(novoInicial, novoFinal, "ε");
        todasLasTransiciones.add(e1);
        resultado.agregarTransicion(e1);
        
        // Camino 2: a través del AFN
        Transicion e2 = new Transicion(novoInicial, afn.getEstadoInicial(), "ε");
        todasLasTransiciones.add(e2);
        resultado.agregarTransicion(e2);
        
        Transicion e3 = new Transicion(afn.getEstadoFinal(), novoFinal, "ε");
        todasLasTransiciones.add(e3);
        resultado.agregarTransicion(e3);
        
        // Agregar estados
        resultado.agregarEstado(novoInicial);
        resultado.agregarEstado(novoFinal);
        for (Estado e : afn.getEstados()) resultado.agregarEstado(e);
        
        // Agregar transiciones del AFN
        for (Transicion t : afn.getTransiciones()) resultado.agregarTransicion(t);
        
        System.out.println("  ✓ Opcional (?) completado");
        System.out.println("    Estado inicial: " + novoInicial + " -> Estado final: " + novoFinal);
        System.out.println("    Total estados: " + resultado.getEstados().size());
        System.out.println("    Total transiciones: " + resultado.getTransiciones().size());
        
        return resultado;
    }

    public AFN convertir() {
        System.out.println("\n>>> Iniciando conversión de: " + expresionRegular);
        String regexProcesada = agregarConcatenacionExplicita(expresionRegular);
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
