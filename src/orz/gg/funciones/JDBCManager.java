/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orz.gg.funciones;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author GustavoG
 */
public class JDBCManager {

    //varibales privadas
    private static String linkBD;
    private static String userBD;
    private static String passBD;
    private static String driverBD;
    private static Connection connection;

    /**
     * Para la construccion de la conexion a base de datos
     *
     * @param p Propediades de la conexion a base de datos las propiedades son:
     * <ul>
     * <li><strong>url:</strong> Url de la conexion para hacer la conexion a la
     * base de datos <br>
     * <code>
     * p.put("url", "jdbc:postgresql://IP/HOST:PUERTO/NOMBRE_DE_LA_BASE_DE_DATOS");
     * </code>
     * </li>
     * <li><strong>user:</strong> Usuario para iniciar sesion en la base de
     * datos <br>
     * <code>
     * p.put("user", "postgres");
     * </code>
     * </li>
     * <li><strong>pass:</strong> contraseña para iniciar sesion (OPCIONAL) <br>
     * <code>
     * p.put("pass", "123456");
     * </code>
     * </li>
     * <li><strong>driver:</strong> Driver de conexion del manejador de base de
     * datos <br>
     * <code>
     * p.put("driver", "org.postgresql.Driver");
     * </code>
     * </li>
     * </ul>
     */
    public static void createConection(Properties p) {
        if (p == null) {
            throw new NullPointerException("Properties of conection is Null");
        }
        linkBD = p.getProperty("url");
        userBD = p.getProperty("user");
        passBD = p.getProperty("pass");
        driverBD = p.getProperty("driver");
        JDBCManager.createConection(linkBD, userBD, passBD, driverBD);

    }

    /**
     * Para la construccion de la conexion a base de datos
     *
     * @param linkBD Url de la conexion para hacer la conexion a la base de
     * datos <br>
     * <code>
     * p.put("url", "jdbc:postgresql://IP/HOST:PUERTO/NOMBRE_DE_LA_BASE_DE_DATOS");
     * </code>
     * @param userBD Usuario para iniciar sesion en la base de datos <br>
     * <code>
     * p.put("user", "postgres");
     * </code>
     * @param passBD contraseña para iniciar sesion (OPCIONAL) <br>
     * <code>
     * p.put("pass", "123456");
     * </code>
     * @param driverBD Driver de conexion del manejador de base de datos <br>
     * <code>
     * p.put("driver", "org.postgresql.Driver");
     * </code>
     */
    public static void createConection(String linkBD, String userBD, String passBD, String driverBD) {
        try {
            if (linkBD == null || userBD == null || driverBD == null) {
                throw new NullPointerException("arguments is null");
            }
            JDBCManager.linkBD = linkBD;
            JDBCManager.userBD = userBD;
            JDBCManager.passBD = passBD;
            JDBCManager.driverBD = driverBD;
            Class.forName(driverBD);
            connection = DriverManager.getConnection(linkBD, userBD, passBD);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Persistir entidad
     *
     * @param entity objeto que se va a guardar en la base de datos
     * @return devuelve <strong>true</strong> en caso de guardar la entidad si
     * ocurre un error devuelve <strong>false</strong>
     */
    public boolean persist(Object entity) {
        try {
            PreparedStatement pm = connection.prepareStatement(getQueryInsert(entity.getClass()), Statement.RETURN_GENERATED_KEYS);
            setValueQuery(pm, entity);
            System.out.println(pm.toString());
            pm.executeUpdate();
            try (ResultSet generatedKeys = pm.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Field f = getId(getAllField(entity.getClass()));
                    setValueReflexion(entity, f, generatedKeys.getLong(1));
                    return true;
                } else {
                    throw new SQLException("Creating entity failed, no ID obtained.");
                }

            }
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     *
     * @param <T>
     * @param entity
     * @return
     */
    public <T> T merge(T entity) {
        try {
            Object o = getValueReflexion(entity, getId(getAllField(entity.getClass())).getName());
            if (o == null) {
                persist(entity);
                return entity;
            }
            PreparedStatement pm = connection.prepareStatement(getQueryUpdate(entity.getClass()) + " WHERE " + getNameInDB(getId(getAllField(entity.getClass())).getName()) + "=?");
            int c = setValueQuery(pm, entity);
            pm.setObject(c, o);
            if (pm.execute()) {
                return (T) get(entity.getClass(), getValueReflexion(entity, getId(getAllField(entity.getClass())).getName()));
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return entity;
    }

    /**
     *
     * @param entity
     * @param idObject
     */
    public void delete(Class entity, Object idObject) {
        try {
            if (idObject == null) {
                throw new NullPointerException("idObject no puede ser nulo");
            }
            if (entity == null) {
                throw new NullPointerException("Class entity no puede ser nulo");
            }
            PreparedStatement pm = connection.prepareStatement("DELETE FROM " + getNameInDB(entity.getSimpleName()) + " WHERE " + getNameInDB(getId(getAllField(entity)).getName()) + "=?");
            pm.setObject(1, idObject);
            pm.execute();

        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param <T>
     * @param classObject
     * @param idObject
     * @return
     */
    public <T> T get(Class<T> classObject, Object idObject) {
        return get(classObject, idObject, true);
    }

    /**
     *
     * @param <T>
     * @param classObject
     * @param idObject
     * @param relacion
     * @return
     */
    public <T> T get(Class<T> classObject, Object idObject, boolean relacion) {
        try {
            PreparedStatement pm = connection.prepareStatement(getQuerySelect(classObject) + "WHERE " + getNameInDB(getId(getAllField(classObject)).getName()) + "=? LIMIT 1");
            pm.setObject(1, idObject);
            ResultSet rs = pm.executeQuery();
            if (rs.next()) {
                return setNewObject(classObject, rs, relacion);
            }
        } catch (SQLException ex) {
//            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     *
     * @param <T>
     * @param classObject
     * @param query
     * @param condition
     * @return
     */
    public <T> T ejecutarQuery(Class<T> classObject, String query, Map condition) {
        List<T> l = ejecutarQuery(classObject, query, condition, 1);
        if (l != null && !l.isEmpty()) {
            return l.get(0);
        }
        return null;
    }

    /**
     *
     * @param <T>
     * @param classObject
     * @param query
     * @param condition
     * @param limit
     * @return
     */
    public <T> T ejecutarQuery(Class classObject, String query, Map condition, int limit) {
        return ejecutarQuery(classObject, query, condition, limit, 0);
    }

    public <T> T ejecutarQuery(Class classObject, String query, Map condition, int limit, boolean relacion) {
        return ejecutarQuery(classObject, query, condition, limit, 0, relacion);
    }

    /**
     *
     * @param <T>
     * @param classObject
     * @param query
     * @param condition
     * @param limit
     * @param inicio
     * @return
     */
    public <T> T ejecutarQuery(Class classObject, String query, Map condition, int limit, int inicio) {
        return ejecutarQuery(classObject, query, condition, limit, inicio, true);
    }

    public <T> T ejecutarQuery(Class classObject, String query, Map condition, int limit, int inicio, boolean relacion) {
        String DetecCondicion = query;
        List<String> condiciones = new ArrayList();
        List objs = new ArrayList();
        while (DetecCondicion.contains(":")) {
            DetecCondicion = DetecCondicion.substring(DetecCondicion.indexOf(":"));
            if (DetecCondicion.contains(" ")) {

                if (DetecCondicion.indexOf(" ") == (DetecCondicion.indexOf(")") + 1)) {
                    condiciones.add(DetecCondicion.substring(1, DetecCondicion.indexOf(")")));
                    DetecCondicion = DetecCondicion.substring(DetecCondicion.indexOf(")"), DetecCondicion.length());
                } else {
                    condiciones.add(DetecCondicion.substring(1, DetecCondicion.indexOf(" ")));
                    DetecCondicion = DetecCondicion.substring(DetecCondicion.indexOf(" "), DetecCondicion.length());
                }

            } else {
                condiciones.add(DetecCondicion.substring(1, DetecCondicion.length()).replace(")", ""));
                break;
            }
        }

        for (String condicione : condiciones) {
            query = query.replace(":" + condicione, "?");
        }

        try {
            PreparedStatement pm = connection.prepareStatement(getNameInDB(query));
            if (limit > 0) {
                pm = connection.prepareStatement(limit(getNameInDB(query), limit, inicio));
            }
            for (int i = 0; i < condiciones.size(); i++) {
                pm.setObject(i + 1, condition.get(condiciones.get(i)));
            }
//            System.out.println(pm.toString());
            ResultSet rs = pm.executeQuery();
            while (rs.next()) {
                Object Ob = setNewObject(classObject, rs, relacion);
                if (Ob != null) {
                    objs.add(Ob);
                }
            }
            return (T) objs;
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     *
     * @param c
     * @return
     */
    public String getQueryInsert(Class c) {
        List<Field> fields = getAllField(c);
        String f = "(";
        String v = "VALUES(";
        for (Field field : fields) {
            if (isContAnno(field, Column.class) || isContAnno(field, Id.class)) {
                f = f.concat(field.getName() + ",");
                v = v.concat("?,");
            } else if (isContAnno(field, ManyToOne.class)) {
                f = f.concat(field.getName() + "_id,");
                v = v.concat("?,");
            }
        }
        f = f.substring(0, f.length() - 1) + ")";
        v = v.substring(0, v.length() - 1) + ")";

        return "Insert into " + getNameInDB(c.getSimpleName()) + " " + getNameInDB(f) + " " + getNameInDB(v);
    }

    /**
     *
     * @param c
     * @return
     */
    public String getQueryUpdate(Class c) {
        List<Field> fields = getAllField(c);
        String f = "";
        for (Field field : fields) {
            if (isContAnno(field, Column.class) || isContAnno(field, Id.class)) {
                f = f.concat(getNameInDB(field.getName() + "=?,"));
            } else if (isContAnno(field, ManyToOne.class)) {
                f = f.concat(getNameInDB(field.getName() + "_id=?,"));
            }
        }
        f = f.substring(0, f.length() - 1) + "";

        return "Update " + getNameInDB(c.getSimpleName()) + " SET " + f + " ";
    }

    /**
     *
     * @param c
     * @return
     */
    public String getQuerySelect(Class c) {
        List<Field> fields = getAllField(c);
        String f = "(";
        for (Field field : fields) {
            if (isContAnno(field, Column.class) || isContAnno(field, Id.class)) {
                f = f.concat(getNameInDB(field.getName() + ","));
            } else if (isContAnno(field, ManyToOne.class)) {
                f = f.concat(getNameInDB(field.getName() + "_id,"));
            }
        }
        f = f.substring(0, f.length() - 1) + ")";

        return "SELECT * FROM " + getNameInDB(c.getSimpleName()) + " ";
    }

    /**
     *
     * @param ps
     * @param instance
     * @return
     * @throws SQLException
     */
    private int setValueQuery(PreparedStatement ps, Object instance) throws SQLException {
        if (instance == null) {
            return 1;
        }
        List<Field> fields = getAllField(instance.getClass());
        int c = 1;
        for (Field field : fields) {
            if (isContAnno(field, Column.class) || isContAnno(field, Id.class)) {
                Object ob = getValueReflexion(instance, field.getName());
                if (ob instanceof Collection) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(bos);
                        oos.writeObject(ob);
                    } catch (IOException ex) {
                        Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    ps.setObject(c++, bos.toByteArray());
                } else if (ob instanceof Date) {
                    java.sql.Date dt = new java.sql.Date(((Date) ob).getTime());
                    ps.setObject(c++, dt);
                } else {
                    ps.setObject(c++, ob);
                }
            } else if (isContAnno(field, ManyToOne.class)) {
                Object o = getValueReflexion(instance, field.getName());
                if (o != null) {
                    Field f = getId(getAllField(o.getClass()));
                    System.out.println(getValueReflexion(o, f.getName()));
                    ps.setObject(c++, getValueReflexion(o, f.getName()));
                } else {
                    ps.setObject(c++, null);
                }
            }
        }
        return c;
    }

    /**
     *
     * @param fields
     * @return
     */
    private Field getId(List<Field> fields) {
        for (Field field : fields) {
            if (isId(field)) {
                return field;
            }
        }
        throw new NullPointerException("Object no contains ID Column");
    }

    /**
     *
     * @param f
     * @return
     */
    private boolean isId(Field f) {
        return isContAnno(f, Id.class);
    }

    /**
     *
     * @param c
     * @return
     */
    public List<Field> getAllField(Class c) {
        return getAllField(c, new ArrayList());
    }

    /**
     *
     * @param c
     * @return
     */
    public Map<String, Field> getAllFieldMap(Class c) {
        return getAllFieldMap(c, new HashMap());
    }

    /**
     *
     * @param c
     * @param f
     * @return
     */
    private List<Field> getAllField(Class c, List f) {
        f.addAll(Arrays.asList(c.getDeclaredFields()));
        if (!c.getSuperclass().getSimpleName().contains("Object")) {
            getAllField(c.getSuperclass(), f);
        }
        return f;
    }

    /**
     *
     * @param c
     * @param f
     * @return
     */
    private Map<String, Field> getAllFieldMap(Class c, Map f) {
        Field[] lista = c.getDeclaredFields();
        for (Field field : lista) {
            f.put(field.getName().toLowerCase(), field);
        }
        if (c.getSuperclass() != null) {
            getAllFieldMap(c.getSuperclass(), f);
        }
        return f;
    }

    /**
     *
     * @param f
     * @param a
     * @return
     */
    private boolean isContAnno(Field f, Class a) {
        return f.getAnnotation(a) != null;
    }

    /**
     *
     * @param instance
     * @param fieldName
     * @return
     */
    private Object getValueReflexion(Object instance, String fieldName) {
        try {
            Method method = getMethodClass1(instance.getClass(), "get" + capitalize(fieldName));
            if (method == null) {
                method = getMethodClass1(instance.getClass(), "is" + capitalize(fieldName));
//                System.out.println(method);
            }
            if (method == null) {

                return null;
            }

            return method.invoke(instance);
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     *
     * @param instance
     * @param fieldName
     * @param value
     * @param relacion
     * @param rf
     */
    private void setValueReflexion(Object instance, Field fieldName, Object value, boolean relacion) {
        try {
            if (value != null) {
                fieldName.setAccessible(true);
                fieldName.set(instance, value);
            }
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException ex) {
            if (fieldName.getName().equalsIgnoreCase("id") && !(value instanceof Long)) {
                setValueReflexion(instance, fieldName, Long.parseLong(value + ""), relacion);
            } else {
//                System.out.println(ex);
            }
        }
    }

    /**
     *
     * @param instance
     * @param fieldName
     * @param value
     */
    private void setValueReflexion(Object instance, Field fieldName, Object value) {
        setValueReflexion(instance, fieldName, value, true);
    }

    /**
     *
     * @param <T>
     * @param object
     * @param rs
     * @return
     */
    private <T> T setNewObject(Class object, ResultSet rs) {
        return setNewObject(object, rs, true);
    }

    /**
     *
     * @param <T>
     * @param object
     * @param rs
     * @param relacion
     * @return
     */
    private <T> T setNewObject(Class object, final ResultSet rs, boolean relacion) {
        try {

            ResultSetMetaData mt = rs.getMetaData();
            if (mt.getColumnCount() == 1) {

                Object o = rs.getObject(mt.getColumnLabel(1));
                if (o != null) {
                    switch (object.getSimpleName()) {
                        case "String":
                        case "Integer":
                        case "Boolean":
                        case "Long":
                        case "Double":
                        case "Float":
                            return (T) o;
                    }
                }
            }

            final Object ob = object.getConstructor().newInstance();

            List<Field> fil = getAllField(object);
            for (final Field field : fil) {
                try {
                    if (field.getAnnotation(Id.class) != null || field.getAnnotation(Column.class) != null) {

                        setValueReflexion(ob, field, rs.getObject(getNameInDB(field.getName())), relacion);
                    } else if ((field.getAnnotation(ManyToOne.class) != null || field.getAnnotation(OneToOne.class) != null) && relacion) {
                        boolean t = true;
                        if (field.getAnnotation(OneToOne.class) != null) {
                            OneToOne o = field.getAnnotation(OneToOne.class);
                            if (!o.mappedBy().isEmpty()) {
                                t = false;
                            }
                        }
                        if (t) {
                            try {
                                field.setAccessible(true);
                                Object o = get(field.getType(), rs.getObject(getNameInDB(field.getName() + "_id")), false);
                                if (o != null) {
                                    setValueReflexion(ob, field, o, false);
                                }
                            } catch (SQLException ex) {
//                                Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                } catch (Exception ex) {
                }
            }

//            for (int i = 0; i < mt.getColumnCount(); i++) {
//                setValueReflexion(ob, mt.getColumnLabel(i + 1), rs.getObject(mt.getColumnLabel(i + 1)), relacion);
//            }
//            if (relacion) {
//                OneToMany(ob);
//            }
            return (T) ob;
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(JDBCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     *
     * @param Instance
     */
    public void OneToMany(final Object Instance) {
        final List<Field> filas = getAllField(Instance.getClass());
        for (Field fila : filas) {
            if (fila.getAnnotation(OneToMany.class) != null) {
                java.lang.reflect.Type genericSuper = fila.getGenericType();
                if (!(genericSuper instanceof Class)) {
                    ParameterizedType generic = (ParameterizedType) genericSuper;
                    java.lang.reflect.Type[] reified = generic.getActualTypeArguments();
                    setValueReflexion(Instance, fila, ejecutarQuery(((Class) reified[0]), "SELECT * FROM " + getNameInDB(((Class) reified[0]).getSimpleName()) + " WHERE " + getNameInDB(getNameClassRelation(((Class) reified[0]), Instance.getClass())) + "=:id", new HashMap() {
                        {
                            put("id", getValueReflexion(Instance, getId(filas).getName()));
                        }
                    }, -1), false);
                }

            }
        }
    }

    /**
     *
     * @param ob
     * @param file
     * @return
     */
    public String getNameClassRelation(Class ob, Class file) {
        List<Field> filas = getAllField(ob);
        for (Field fila : filas) {
            if (fila.getType().getSimpleName().equals(file.getSimpleName())) {
                return fila.getName() + "_id";
            }
        }
        return null;
    }

    /**
     *
     * @param word
     * @return
     */
    private String capitalize(String word) {
        return String.valueOf(word.charAt(0)).toUpperCase() + word.substring(1);
    }

    /**
     *
     * @param word
     * @return
     */
    private String unCapitalize(String word) {
        return String.valueOf(word.charAt(0)).toLowerCase() + word.substring(1);
    }

    /**
     *
     * @param classObject
     * @param name
     * @return
     */
    private String getMethodClass(Class classObject, String name) {
        Method[] methods = classObject.getMethods();

        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase(name)) {
                return method.getName();
            }
        }

        return null;
    }

    /**
     *
     * @param classObject
     * @param name
     * @return
     */
    private Method getMethodClass1(Class classObject, String name, Class... c) {

        try {
            Method m = classObject.getMethod(name, c);
            if (m != null) {
                return m;
            }
        } catch (NoSuchMethodException | SecurityException ex) {

        }
//        System.out.println(name);
        Method[] methods = classObject.getMethods();
        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase(name)) {
//                System.out.println("Metodo encontrado "+method.getName());
                return method;
            }
        }

        return null;
    }

    /**
     *
     * @param classObject
     * @param name
     * @return
     */
    private Field getFieldClass(Class classObject, String name) {
        try {
            Field m = classObject.getDeclaredField(name);
            if (m != null) {
                return m;
            }
        } catch (NoSuchFieldException | SecurityException ex) {
        }
        System.out.println(name);
        List<Field> methods = getAllField(classObject);
        for (Field method : methods) {
//            System.out.println(method.getName() + " = "+name );
            if (method.getName().equalsIgnoreCase(name)) {
                return method;
            }
        }

        return null;
    }

    /**
     *
     * @param query
     * @param limit
     * @param offset
     * @return
     */
    private String limit(String query, int limit, int offset) {
        if (driverBD.toLowerCase().contains("mysql".toLowerCase())) {
            return query + String.format(" limit %d offset %d ", limit, offset);
        }

        if (driverBD.toLowerCase().contains("postgresql".toLowerCase())) {
            return query + String.format(" limit %d offset %d", limit, offset);
        }

        if (driverBD.toLowerCase().contains("sqlserver".toLowerCase())) {
            String order = " ";
            if (!query.toLowerCase().contains("order by")) {
                order = " ORDER BY 1 DESC ";
            }
            return query + order + String.format(" OFFSET %d ROWS FETCH NEXT %d ROWS ONLY", offset, limit);
        }

        return "";
    }

    /**
     *
     * @param name
     * @return
     */
    private String getNameInDB(String name) {
        if (driverBD == null) {
            return name;
        }
        if (driverBD.toLowerCase().contains("mysql".toLowerCase())) {
            return name.toUpperCase();
        }

        if (driverBD.toLowerCase().contains("postgresql".toLowerCase())) {
            return name.toLowerCase();
        }

        if (driverBD.toLowerCase().contains("sqlserver".toLowerCase())) {
            return name.toUpperCase();
        }
        return name;
    }

}
