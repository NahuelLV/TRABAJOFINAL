package pro.juego.Ironfall.enums;

public enum TipoUnidad {

    ESPADACHIN(100, 2f),
    BESTIA(250, 3.5f),
    ARQUERO(150, 2.5f);

    private final int costo;
    private final float tiempoProduccion;

    TipoUnidad(int costo, float tiempoProduccion) {
        this.costo = costo;
        this.tiempoProduccion = tiempoProduccion;
    }

    public int getCosto() {
        return costo;
    }

    public float getTiempoProduccion() {
        return tiempoProduccion;
    }
}
