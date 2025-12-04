public class Estado {
    private int id;
    private boolean esAceptacion;

    public Estado(int id) {
        this.id = id;
        this.esAceptacion = false;
    }

    public int getId() {
        return id;
    }

    public boolean esAceptacion() {
        return esAceptacion;
    }

    public void setAceptacion(boolean aceptacion) {
        this.esAceptacion = aceptacion;
    }

    @Override
    public String toString() {
        return "q" + id + (esAceptacion ? " (aceptación)" : "");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Estado estado = (Estado) obj;
        return id == estado.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
