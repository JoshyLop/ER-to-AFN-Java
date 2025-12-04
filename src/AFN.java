import java.util.HashSet;
import java.util.Set;

public class AFN {
    private Estado estadoInicial;
    private Estado estadoFinal;
    private Set<Estado> estados;
    private Set<Transicion> transiciones;

    public AFN(Estado inicial, Estado final) {
        this.estadoInicial = inicial;
        this.estadoFinal = final;
        this.estados = new HashSet<>();
        this.transiciones = new HashSet<>();
        
        estados.add(inicial);
        estados.add(final);
        final.setAceptacion(true);
    }

    public Estado getEstadoInicial() {
        return estadoInicial;
    }

    public Estado getEstadoFinal() {
        return estadoFinal;
    }

    public Set<Estado> getEstados() {
        return estados;
    }

    public Set<Transicion> getTransiciones() {
        return transiciones;
    }

    public void agregarEstado(Estado estado) {
        estados.add(estado);
    }

    public void agregarTransicion(Transicion transicion) {
        transiciones.add(transicion);
        estados.add(transicion.getDesde());
        estados.add(transicion.getHasta());
    }

    public void mostrar() {
        System.out.println("\n=== AFN ===");
        System.out.println("Estados: " + estados.size());
        for (Estado e : estados) {
            System.out.println("  " + e);
        }
        System.out.println("\nTransiciones: " + transiciones.size());
        for (Transicion t : transiciones) {
            System.out.println("  " + t);
        }
        System.out.println("Estado inicial: " + estadoInicial);
        System.out.println("Estado final: " + estadoFinal);
        System.out.println("================\n");
    }
}
