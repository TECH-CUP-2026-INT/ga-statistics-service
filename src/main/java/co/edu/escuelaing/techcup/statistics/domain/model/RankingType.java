package co.edu.escuelaing.techcup.statistics.domain.model;

/**
 * Tipos de ranking publico. Es un concepto de negocio (que tipo de tabla se
 * quiere), por eso vive en domain y no en el DTO de la capa web -- el
 * controller lo usa directamente como parametro de query, sin necesidad de
 * mapeo.
 */
public enum RankingType {
    GOALS,
    WINS,
    FOULS,
    MINUTES
}
