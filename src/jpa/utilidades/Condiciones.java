package com.megagroup.jpa.utilidades;

/**
 * <b>Enum</b> con la lista de Condiciones soportadas por <b>QueryBuilder</b>.
 *
 * @author Gianluigi Pierini
 */
//    CONDICIONES
public enum Condiciones {

    /**
     *
     */
    MAYOR(">"),

    /**
     *
     */
    MENOR("<"),

    /**
     *
     */
    MAYOR_IGUAL(">="),

    /**
     *
     */
    MENOR_IGUAL("<="),

    /**
     *
     */
    IGUAL_CS("="),
    
    DIFERENTE("!="),

    /**
     *
     */
    ES_NULO("IS NULL") {
                @Override
                public StringBuilder getCondicion(String atributoEntidad, String nombreParametro) {
                    return getCondicionSinParametro(atributoEntidad);
                }
            },

    /**
     *
     */
    NO_ES_NULO("IS NOT NULL") {
                @Override
                public StringBuilder getCondicion(String atributoEntidad, String nombreParametro) {
                    return getCondicionSinParametro(atributoEntidad);
                }
            },

    /**
     *
     */
    IGUAL_CI("LIKE") {
                @Override
                public StringBuilder getCondicion(String atributoEntidad, String nombreParametro) {
                    StringBuilder condicion = new StringBuilder();
                    return condicion.append("LOWER(").append(atributoEntidad)
                    .append(") LIKE LOWER(:").append(nombreParametro).append(")");
                }
            },

    /**
     *
     */
    IGUAL_CI_AC("LIKE") {
                @Override
                public StringBuilder getCondicion(String atributoEntidad, String nombreParametro) {
                    return IGUAL_CI.getCondicion(atributoEntidad, nombreParametro);
                }
            },

    /**
     *
     */
    CONTAINS("IN") {
                @Override
                public StringBuilder getCondicion(String atributoEntidad, String nombreParametro) {
                    StringBuilder condicion = new StringBuilder();
                    return condicion.append(atributoEntidad).append(" ").append(comparacion)
                    .append(" :").append(nombreParametro).append("");
                }
            };
    /**
     *
     */
        protected final String comparacion;

    private Condiciones(String comparacion) {
        this.comparacion = comparacion;
    }

    /**
     *
     * @param atributoEntidad
     * @param nombreParametro
     * @return
     */
    public StringBuilder getCondicion(String atributoEntidad, String nombreParametro) {
        StringBuilder condicion = new StringBuilder();
        return condicion.append(atributoEntidad).append(" ")
                .append(comparacion).append(" :").append(nombreParametro);
    }

    /**
     *
     * @param atributoEntidad
     * @return
     */
    protected StringBuilder getCondicionSinParametro(String atributoEntidad) {
        StringBuilder condicion = new StringBuilder();
        return condicion.append(atributoEntidad).append(" ")
                .append(comparacion);
    }
}
