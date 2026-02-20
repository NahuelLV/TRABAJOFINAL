package pro.juego.Ironfall.entidades;

import pro.juego.Ironfall.enums.TipoUnidad;

public class IAEnemiga {

    private Estatua estatua;
    private float timerDecision = 0f;
    private float intervaloDecision = 2.5f;

    public IAEnemiga(Estatua estatua) {
        this.estatua = estatua;
    }

    public void update(float delta) {
        timerDecision += delta;

        if (timerDecision < intervaloDecision) return;

        timerDecision = 0f;
        decidirProduccion();
    }

    private void decidirProduccion() {

        // IA simple por ahora
        estatua.intentarProducir(TipoUnidad.ESPADACHIN);

    }
}