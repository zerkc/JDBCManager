package jpa.utilidades;

import com.megagroup.jpa.entidades.Entidad;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.JpaHelper;

/**
 * <b>Clase</b> de Apoyo que implementa una serie de métodos para ejecutar
 * acciones en la <b>Base de Datos</b> a través de <b>JPA</b>.
 *
 * @author Gianluigi Pierini
 */
public class JPAManager {

    private static final Logger logger = Logger.getLogger(JPAManager.class.getName());

    /**
     *
     */
    public static final String unidadPersistencia = "mProductosPU";
    private EntityManager entityManager;
    private EntityTransaction entityTransaction;

//    INICIALIZADORES
    /**
     *
     */
    public JPAManager() {
    }

    /**
     *
     */
    @PostConstruct
    public static void inicializarEntityManager() {
        Map properties = new HashMap();
        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_OR_EXTEND);
//create-or-extend only works on the database
        properties.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_DATABASE_GENERATION);
//this causes DDL generation to occur on refreshMetadata rather than wait until an em is obtained
        properties.put(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "true");
        JpaHelper.getEntityManagerFactory(obtenerEntityManager()).refreshMetadata(properties);
    }

    /**
     * Genera una nueva instancia de EntityManager.
     *
     * @return nueva instancia de EntityManager
     */
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    public static EntityManager obtenerEntityManager() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(unidadPersistencia);
        return entityManagerFactory.createEntityManager();
    }

    /**
     *
     */
    public void iniciarTransaccion() {
        borrarCache();
        entityTransaction = obtenerTransaccion();
        entityTransaction.begin();
    }

    /**
     *
     * @param commit
     */
    public void finalizarTransaccion(Boolean commit) {
        borrarCache();
        if (commit) {
            entityTransaction.commit();
        } else {
            entityTransaction.rollback();
        }
    }

    /**
     *
     * @return
     */
    public EntityTransaction obtenerTransaccion() {
        borrarCache();
        entityManager = obtenerEntityManager();
        return entityManager.getTransaction();
    }

    /**
     *
     */
    public void borrarCache() {
        Persistence.createEntityManagerFactory(unidadPersistencia).getCache().evictAll();
    }

    /**
     *
     * @param query
     * @param parametros
     * @param numeroResultados
     * @return
     */
    public List ejecutarQuery(String query, Map<String, Object> parametros, int numeroResultados) {
        verificarNumeroResultado(numeroResultados);
        return ejecutarQuery(query, parametros, numeroResultados, false);
    }

    /**
     *
     * @param query
     * @param parametros
     * @return
     */
    public Object ejecutarQuery(String query, Map<String, Object> parametros) {
        return ejecutarQuery(query, parametros, 1, false);
    }

    /**
     *
     * @param query
     * @param posicionInicial
     * @param parametros
     * @return
     */
    public Object ejecutarQuery(String query, int posicionInicial, Map<String, Object> parametros) {
        return ejecutarQuery(query, parametros, 1, false, posicionInicial);
    }

    /**
     *
     * @param query
     * @param parametros
     * @param numeroResultados
     * @param posicionInicial
     * @return
     */
    public Object ejecutarQuery(String query, Map<String, Object> parametros, int numeroResultados, int posicionInicial) {
        return ejecutarQuery(query, parametros, numeroResultados, false, posicionInicial);
    }

    /**
     *
     * @param nombreQuery
     * @param parametros
     * @param numeroResultados
     * @return
     */
    public List ejecutarNamedQuery(String nombreQuery, Map<String, Object> parametros, int numeroResultados) {
        verificarNumeroResultado(numeroResultados);
        return ejecutarQuery(nombreQuery, parametros, numeroResultados, true);
    }

    /**
     *
     * @param nombreQuery
     * @param parametros
     * @param numeroResultados
     * @param posicionInicial
     * @return
     */
    public List ejecutarNamedQuery(String nombreQuery, Map<String, Object> parametros, int numeroResultados, int posicionInicial) {
        verificarNumeroResultado(numeroResultados);
        return ejecutarQuery(nombreQuery, parametros, numeroResultados, true, posicionInicial);
    }

    /**
     *
     * @param nombreQuery
     * @param parametros
     * @return
     */
    public Object ejecutarNamedQuery(String nombreQuery, Map<String, Object> parametros) {
        return ejecutarQuery(nombreQuery, parametros, 1, true);
    }

    /**
     *
     * @param query
     * @param parametros
     * @param numeroResultados
     * @param entityManager
     * @return
     */
    public List ejecutarQuery(Query query, Map<String, Object> parametros, int numeroResultados, EntityManager entityManager) {
        verificarNumeroResultado(numeroResultados);
        return ejecutarQueryGeneral(query, entityManager, parametros, numeroResultados);
    }

    /**
     *
     * @param query
     * @param entityManager
     * @param parametros
     * @return
     */
    public Object ejecutarQuery(Query query, EntityManager entityManager, Map<String, Object> parametros) {
        return ejecutarQueryGeneral(query, entityManager, parametros, 1);
    }

    /**
     *
     * @param query
     * @param entityManager
     * @param parametros
     * @param posicionInicial
     * @return
     */
    public Object ejecutarQuery(Query query, EntityManager entityManager, Map<String, Object> parametros, Integer posicionInicial) {
        return ejecutarQueryGeneral(query, entityManager, parametros, 1);
    }

    /**
     *
     * @param query
     * @param entityManager
     * @param parametros
     * @param cantidadResultados
     * @param posicionInicial
     * @return
     */
    public Object ejecutarQuery(Query query, EntityManager entityManager, Map<String, Object> parametros, Integer cantidadResultados, Integer posicionInicial) {
        return ejecutarQueryGeneral(query, entityManager, parametros, cantidadResultados, posicionInicial);
    }

    /**
     *
     * @param entidad
     * @param usaTransaccion
     * @return
     */
    public boolean persistirEntidad(Object entidad, Boolean usaTransaccion) {
        borrarCache();
        if (usaTransaccion) {
            entityManager.persist(entidad);
            return true;
        } else {
            return operacionCUD(entidad, Operaciones.INSERTAR, null, null);
        }
    }

    /**
     * Actualiza un Registro en la Base de Datos
     *
     * @param entidad Instancia de una Entidad de JPA
     * @param usaTransaccion TRUE si usa la transacción activa por el
     * JPAManager. False si inicia una nueva transacción y luego la cierra
     * @return TRUE si la operación termina Exitosamente
     */
    public Object actualizarEntidad(Object entidad, Boolean usaTransaccion) {
       borrarCache();
        if (usaTransaccion) {
            
            return entityManager.merge(entidad);
        } else {
            return operacionCUD(entidad, Operaciones.INSERTAR, null, null);
        }
    }

    /**
     * Elimina un Registro en la Base de Datos
     *
     * @param entidad Instancia de una Entidad de JPA
     * @param idEntidad ID que del Registro
     * @param usaTransaccion
     * @return TRUE si la operación termina Exitosamente
     */
    public boolean eliminarEntidad(Object entidad, Object idEntidad, Boolean usaTransaccion) {
        borrarCache();
        if (usaTransaccion) {
            entityManager.remove(entityManager.getReference(entidad.getClass(), idEntidad));
            return true;
        } else {
            return operacionCUD(entidad, Operaciones.ELIMINAR, entidad.getClass(), idEntidad);
        }
    }

    /**
     *
     * @param entidad
     * @param idEntidad
     * @param usaTransaccion
     * @return
     */
    public boolean eliminarEntidad(Class entidad, Object idEntidad, Boolean usaTransaccion) {
        borrarCache();
        if (usaTransaccion) {
            entityManager.remove(entityManager.getReference(entidad, idEntidad));
            return true;
        } else {
            return operacionCUD(entidad, Operaciones.ELIMINAR, entidad, idEntidad);
        }
    }
    
    public boolean eliminarEntidades(Object entidad){
        
        if (entidad instanceof List && !((List)entidad).isEmpty()) {
            return operacionCUD(entidad, Operaciones.ELIMINAR,((List) entidad).get(0).getClass(), null);
        }else{
            return false;
        }
    }

    /**
     * Ingresa un nuevo Registro en la Base de Datos
     *
     * @param entidad Instancia de una Entidad de JPA
     * @return TRUE si la operación termina Exitosamente
     */
    public boolean persistirEntidad(Object entidad) {
        return operacionCUD(entidad, Operaciones.INSERTAR, null, null);
    }

    /**
     * Actualiza un Registro en la Base de Datos
     *
     * @param entidad Instancia de una Entidad de JPA
     * @return TRUE si la operación termina Exitosamente
     */
    public boolean actualizarEntidad(Object entidad) {
        return operacionCUD(entidad, Operaciones.ACTUALIZAR, null, null);
    }
    
    public <E>E actualizarEntidadYObtener(Object entidad) {
        E t = null;
         EntityManager emLocal = JPAManager.obtenerEntityManager();
        EntityTransaction transactionLocal = emLocal.getTransaction();
        try {
            transactionLocal.begin();
                    if (entidad instanceof List) {
                        for (Object obj : (List) entidad) {
                            t = (E) emLocal.merge(obj);
                        }
                    } else {
                        t = (E) emLocal.merge(entidad);
                    }
                  
              
            transactionLocal.commit();
            return t;
        } catch (javax.persistence.PersistenceException e) {
            logger.log(Level.SEVERE, "ERROR AL INTENTAR HACER UNA OPERACION CUD:", e);
            return null;
        } finally {
            emLocal.close();
        }
       
    }

    /**
     * Elimina un Registro en la Base de Datos
     *
     * @param entidad Instancia de una Entidad de JPA
     * @param idEntidad ID que del Registro
     * @return TRUE si la operación termina Exitosamente
     */
    public boolean eliminarEntidad(Object entidad, Object idEntidad) {
       return operacionCUD(entidad, Operaciones.ELIMINAR, entidad.getClass(), idEntidad);
    }

    /**
     *
     * @param claseEntidad
     * @param idEntidad
     * @return
     */
    public boolean eliminarEntidad(Class claseEntidad, Object idEntidad) {
        return operacionCUD(null, Operaciones.ELIMINAR, claseEntidad, idEntidad);
    }

    /**
     * Obtiene una Entidad por su ID y Clase
     *
     * @param claseEntidad Clase de la Entidad a Buscar
     * @param idEntidad ID de la Entidad
     * @return Entidad
     */
    public Object obtenerEntidad(Class claseEntidad, Number idEntidad) {
        borrarCache();
        EntityManager entityManager = JPAManager.obtenerEntityManager();
        try {
            return entityManager.find(claseEntidad, idEntidad);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "ERROR AL INTENTAR RECUPERAR LA ENTIDAD:", e);
        } finally {
            entityManager.close();
        }
        return null;
    }

    private boolean operacionCUD(Object entidad, Operaciones operaciones, Class claseEntidad, Object idEntidad) {
        borrarCache();
        EntityManager emLocal = JPAManager.obtenerEntityManager();
        EntityTransaction transactionLocal = emLocal.getTransaction();
        try {
            transactionLocal.begin();
            switch (operaciones) {
                case INSERTAR:
                    if (entidad instanceof List) {
                        for (Object obj : (List) entidad) {
                            emLocal.persist(obj);
                        }
                    } else {
                        emLocal.persist(entidad);
                    }
                    break;
                case ACTUALIZAR:
                    if (entidad instanceof List) {
                        for (Object obj : (List) entidad) {
                            emLocal.merge(obj);
                        }
                    } else {
                        emLocal.merge(entidad);
                    }
                    break;
                case ELIMINAR:
                    if (entidad instanceof List) {
                        for (Object obj : (List) entidad) {
                            emLocal.remove(emLocal.getReference(claseEntidad, ((Entidad) obj).getId()));
                        }
                    } else {
                        emLocal.remove(emLocal.getReference(claseEntidad, idEntidad));
                    }
                    break;
            }
            transactionLocal.commit();
            return true;
        } catch (javax.persistence.PersistenceException e) {
            logger.log(Level.SEVERE, "ERROR AL INTENTAR HACER UNA OPERACION CUD:", e);
            return false;
        } finally {
            emLocal.close();
        }
    }

    private <T> T ejecutarQueryGeneral(Query query, EntityManager entityManager, Map<String, Object> parametros, int numeroResultados) {
        return ejecutarQueryGeneral(query, entityManager, parametros, numeroResultados, -1);
    }

    private <T> T ejecutarQueryGeneral(Query query, EntityManager entityManager, Map<String, Object> parametros, int numeroResultados, int posicionInicial) {
        borrarCache();
        if (parametros != null && !parametros.isEmpty()) {
            for (String parametro : parametros.keySet()) {
                query.setParameter(parametro, parametros.get(parametro));
            }
        }
        if (numeroResultados > 0) {
            query.setMaxResults(numeroResultados);
        }

        if (posicionInicial > 0) {
            query.setFirstResult(posicionInicial);
        }

        if (numeroResultados > 1 || numeroResultados <= 0) {
            return (T) query.getResultList();
        } else {
            try {
                return (T) query.getSingleResult();
            } catch (NoResultException e) {
                return null;
            } finally {
                entityManager.close();
            }
        }
    }

    private <T> T ejecutarQuery(String query, Map<String, Object> parametros, int numeroResultados, boolean esNamedQuery) {
        return ejecutarQuery(query, parametros, numeroResultados, esNamedQuery, -1);
    }

    private <T> T ejecutarQuery(String query, Map<String, Object> parametros, int numeroResultados, boolean esNamedQuery, int posicionInicial) {
        borrarCache();
        EntityManager emLocal = obtenerEntityManager();
        Query queryEjecutable;
        if (esNamedQuery) {
            queryEjecutable = emLocal.createNamedQuery(query);
        } else {
            queryEjecutable = emLocal.createQuery(query);
        }
        queryEjecutable.setHint("eclipselink.read-only", "true");
        queryEjecutable.setHint("eclipselink.query-results-cache", "true");
        return ejecutarQueryGeneral(queryEjecutable, emLocal, parametros, numeroResultados, posicionInicial);
    }

    private void verificarNumeroResultado(int numeroResultado) {
        if (numeroResultado == 1) {
            throw new IllegalArgumentException("El número de resultados debe ser un valor diferente de 1,"
                    + " si necesita obtener un único resultado debe usar otra versión del método");
        }
    }

    /**
     * Enum que lista las operaciones de JPA posibles para la clase JPAManager
     */
    private enum Operaciones {

        /**
         * Insertar un nuevo registro en la Base de Datos
         */
        INSERTAR,
        /**
         * Actualiza un Registro en la Base de Datos
         */
        ACTUALIZAR,
        /**
         * Elimina u Registro en la Base de Datos
         */
        ELIMINAR
    }
}
