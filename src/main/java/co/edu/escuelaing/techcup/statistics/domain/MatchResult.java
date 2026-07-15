package co.edu.escuelaing.techcup.statistics.domain;

/**
 * Resultado obtenido por un jugador (a traves de su equipo) en un partido.
 * Concepto puro de negocio: no tiene ninguna anotacion de persistencia ni
 * de la capa web.
 */
public enum MatchResult {
    WON,
    DRAWN,
    LOST
}
