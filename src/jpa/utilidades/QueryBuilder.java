package com.megagroup.jpa.utilidades;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <b>Clase</b> utilitaria para la creación de queries dinámicos.
 *
 * @author Gianluigi Pierini
 * @param <T> <b>Clase</b> que sera devuelta al ejecutar el query
 */
public class QueryBuilder<T> implements Serializable {

    private StringBuilder query;
    private String sentenciaFinal = "";
    private Map<String, Object> listaParametros = new HashMap<>();
    private int numeroparametros;
    private String queryEjecutable;
    private JPAManager jpaManager = new JPAManager();

//    CONSTRUCTORES
    /**
     *
     */
    public QueryBuilder() {
    }

    /**
     *
     * @param query
     */
    public QueryBuilder(String query) {
        this.query = new StringBuilder(query);
    }

    /**
     *
     * @param query
     * @param sentenciaFinal
     */
    public QueryBuilder(String query, String sentenciaFinal) {
        this(query);
        this.sentenciaFinal = sentenciaFinal;
    }

//    METODOS PUBLICOS
    /**
     *
     * @param numeroResultados
     * @return
     */
    public List<T> ejecutarQuery(int numeroResultados) {
        prepararEjecucion();
        return jpaManager.ejecutarQuery(queryEjecutable, listaParametros, numeroResultados);
    }

    /**
     *
     * @param numeroResultados
     * @param posicionInicial
     * @return
     */
    public List<T> ejecutarQuery(int numeroResultados, int posicionInicial) {
        prepararEjecucion();
        return (List<T>) jpaManager.ejecutarQuery(queryEjecutable, listaParametros, numeroResultados, posicionInicial);
    }

    /**
     *
     * @return
     */
    public T ejecutarQuery() {
        prepararEjecucion();
        return (T) jpaManager.ejecutarQuery(queryEjecutable, listaParametros);
    }

    /**
     *
     * @param posicionInicial
     * @return
     */
    public T ejecutarQuery(Integer posicionInicial) {
        prepararEjecucion();
        return (T) jpaManager.ejecutarQuery(queryEjecutable, posicionInicial, listaParametros);
    }

    /**
     *
     * @param sentenciaFinal
     * @return
     */
    public QueryBuilder<T> agregarSentenciaFinal(String sentenciaFinal) {
        this.sentenciaFinal = sentenciaFinal;
        return this;
    }

    /**
     *
     * @param query
     * @return
     */
    public QueryBuilder<T> agregarQuery(String query) {
        this.query = new StringBuilder(query);
        sentenciaFinal = "";
        numeroparametros = 0;
        listaParametros = new HashMap<>();
        return this;
    }

    /**
     *
     * @param condicion
     * @param esObligatorio
     * @return
     */
    public QueryBuilder<T> agregarCondicionGeneral(String condicion, boolean esObligatorio) {
        agregarCondicion(condicion, esObligatorio);
        return this;
    }

    /**
     *
     * @param cadena
     * @return
     */
    public QueryBuilder<T> agregarCadena(String cadena) {
        query.append(cadena);
        return this;
    }

    /**
     *
     * @param condicion
     * @param valorComparar
     * @param atributoEntidad
     * @param esObligatorio
     * @param excluirNulo
     * @return
     */
    public QueryBuilder<T> agregarCondicion(Condiciones condicion, Object valorComparar,
            String atributoEntidad, boolean esObligatorio, boolean excluirNulo) {

        switch (condicion) {
            case ES_NULO:
            case NO_ES_NULO:
                crearCondicionSinParametros(Condiciones.ES_NULO, atributoEntidad, esObligatorio);
                return this;
        }
        if (valorComparar == null) {

        }
        if (valorComparar instanceof String) {
            if (condicion.equals(Condiciones.IGUAL_CI_AC)) {
                crearCondicionString(condicion, (String) "%" + valorComparar + "%",
                        atributoEntidad, excluirNulo, "%%", esObligatorio);
            } else {
                crearCondicionString(condicion, (String) valorComparar,
                        atributoEntidad, excluirNulo, "", esObligatorio);
            }

        } else if (valorComparar instanceof Number) {
            crearCondicionLong(condicion, ((Number) valorComparar).longValue(),
                    atributoEntidad, excluirNulo, esObligatorio);

        } else if (valorComparar instanceof Date) {
            crearCondicionFecha(condicion, (Date) valorComparar,
                    atributoEntidad, excluirNulo, esObligatorio);
        } else if (valorComparar != null && valorComparar instanceof Object) {
            crearCondicion(condicion, valorComparar, atributoEntidad, esObligatorio);
        }
        return this;
    }
//    METODOS PRIVADOS

    private void prepararEjecucion() {
        query.append(" ").append(sentenciaFinal);
//        CREA EL QUERY Y ELIMINA SENTENCIAS INVALIDAD
        queryEjecutable = query.toString().replaceAll("WHERE \\(\\) AND", "WHERE").replaceAll("WHERE \\(\\) OR", "WHERE")
                .replaceAll("WHERE \\(\\)", "").replaceAll("\\(\\)", "");
    }

    private void crearCondicionFecha(Condiciones condicion, Date valorComparar,
            String atributoEntidad, boolean excluirNulo, boolean esObligatorio) {
        if (!excluirNulo || valorComparar != null) {
            java.sql.Date fechaJPA = new java.sql.Date(valorComparar.getTime());
            crearCondicion(condicion, fechaJPA, atributoEntidad, esObligatorio);
        }
    }

    private void crearCondicionString(Condiciones condicion, String valorComparar,
            String atributoEntidad, boolean excluirNulo, String textoNulo, boolean esObligatorio) {

        if (!excluirNulo || (valorComparar != null && !textoNulo.equals(valorComparar))) {
            crearCondicion(condicion, valorComparar, atributoEntidad, esObligatorio);
        }
    }

    private void crearCondicionLong(Condiciones condicion, long valorComparar,
            String atributoEntidad, boolean excluirNulo, boolean esObligatorio) {

        if (!excluirNulo || valorComparar != 0) {
            crearCondicion(condicion, valorComparar, atributoEntidad, esObligatorio);
        }
    }

    private void crearCondicion(Condiciones condicion, Object valorComparar,
            String atributoEntidad, boolean esObligatorio) {
        registrarCondicion(valorComparar,
                condicion.getCondicion(atributoEntidad, "p" + (++numeroparametros)), esObligatorio);
    }

    private void registrarCondicion(Object valorComparar, StringBuilder condicion, boolean esObligatorio) {
        listaParametros.put("p" + numeroparametros, valorComparar);
        agregarCondicion(condicion.toString(), esObligatorio);
    }

    private void crearCondicionSinParametros(Condiciones condicion,
            String atributoEntidad, boolean esObligatorio) {

        agregarCondicion(condicion.getCondicion(atributoEntidad, "p" + (++numeroparametros)).toString(),
                esObligatorio);
    }

    private StringBuilder agregarCondicion(String condicion, boolean esObligatorio) {
        if (query.toString().contains("WHERE")) {
            if (!query.substring(query.length() - 1, query.length()).equals("(")) {
                query.append((esObligatorio) ? " AND " : " OR ");
            }
            query.append(condicion);
        } else {
            query.append(" WHERE ").append(condicion);
        }
        return query;
    }

//    GETTERS Y SETTERS
    /**
     *
     * @param jpaManager
     */
    public void setJpaManager(JPAManager jpaManager) {
        this.jpaManager = jpaManager;
    }

    /**
     *
     * @return
     */
    public StringBuilder getQuery() {
        return query;
    }

    /**
     *
     * @param query
     */
    public void setQuery(StringBuilder query) {
        this.query = query;
    }

    public Map<String, Object> getListaParametros() {
        return listaParametros;
    }

//    METODOS HEREDADOS
    @Override
    public String toString() {
        return query.toString();
    }
}
