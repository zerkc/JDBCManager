/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orz.gg.funciones;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author GustavoG
 */
public class ReflexionMethodAndField {

    private final Map<String, Field> fields = new HashMap();
    private final Map<String, Method> methods = new HashMap();

    public ReflexionMethodAndField(Object instance) {
        this(instance.getClass());
    }

    public ReflexionMethodAndField(Class objectClass) {
//        System.out.println(objectClass.getName());
        methods(objectClass);
        fields(objectClass);
    }

    private void methods(Class objectClass) {
        Method[] metodos = objectClass.getMethods();
        for (Method metodo : metodos) {
            methods.put(metodo.getName().toLowerCase(), metodo);
        }
    }

    private void fields(Class objectClass) {
        Field[] flds = objectClass.getDeclaredFields();
        for (Field fld : flds) {
            fields.put(fld.getName().toLowerCase(), fld);
        }
        if (objectClass.getSuperclass() != null) {
            fields(objectClass.getSuperclass());
        }
    }

    public Field getField(String name) {
       
        return fields.get(name.toLowerCase());
    }

    public Method getMethod(String name) {
//         System.out.println(name + "=" +methods.get(name.toLowerCase()));
        return methods.get(name.toLowerCase());
    }

}
