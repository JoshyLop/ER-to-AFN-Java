public class Transicion {
    private Estado desde;
    private Estado hasta;
    private String simbolo;

    public Transicion(Estado desde, Estado hasta, String simbolo) {
        this.desde = desde;
        this.hasta = hasta;
        this.simbolo = simbolo;
    }

    public Estado getDesde() {
        return desde;
    }

    public Estado getHasta() {
        return hasta;
    }

    public String getSimbolo() {
        return simbolo;
    }

    @Override
    public String toString() {
        String simboloMostrado = simbolo.equals("ε") ? "e" : simbolo;
        return desde + " --[" + simboloMostrado + "]--> " + hasta;
    }
}
