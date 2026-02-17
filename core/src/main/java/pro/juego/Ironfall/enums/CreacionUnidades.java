package pro.juego.Ironfall.enums;

import pro.juego.Ironfall.entidades.Arquero;
import pro.juego.Ironfall.entidades.Bestia;
import pro.juego.Ironfall.entidades.Espadachin;
import pro.juego.Ironfall.entidades.Unidad;


public class CreacionUnidades {

    public static Unidad crearUnidad(
            TipoUnidad tipo,
            float x,
            float y,
            int equipo
    ) {

        switch (tipo) {

            case ESPADACHIN:
                return new Espadachin(x, y, equipo);

            case BESTIA:
                return new Bestia(x, y, equipo);

            case ARQUERO:
                return new Arquero(x, y, equipo);

            default:
                throw new RuntimeException(
                        "TipoUnidad no soportado: " + tipo
                );
        }
    }
}
