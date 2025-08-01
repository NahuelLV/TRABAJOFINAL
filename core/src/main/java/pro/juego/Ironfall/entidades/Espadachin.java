package pro.juego.Ironfall.entidades;

public class Espadachin extends Unidad {
	public Espadachin(float x, float y, float anchoMapa, int equipo) {
        super("unidades/espadachin.png", x, y, anchoMapa, equipo);
        setEstadisticas();
    }

    private void setEstadisticas() {
        this.setVida(120);         // Un poco más resistente
        this.setDaño(30);          // Más daño que la base
        this.setRangoAtaque(40f);  // Cuerpo a cuerpo más corto
        this.setTiempoEntreAtaques(0.8f); // Ataca más seguido
    }
}
