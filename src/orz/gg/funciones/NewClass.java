/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orz.gg.funciones;

import com.megagroup.entidades.funcionales.Expediente;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import jpa.utilidades.JPAManager;

/**
 *
 * @author GustavoG
 */
public class NewClass {

    public static void main(String[] args) {

//        NewClass.class.getPackage().getClass().
        Properties p = new Properties();
        p.put("url", "jdbc:mysql://localhost:3306/JDBCManager");
        p.put("user", "root");
        p.put("pass", "");
        p.put("driver", "com.mysql.jdbc.Driver");
//        Properties p = new Properties();
//        p.put("url", "jdbc:postgresql://192.168.123.236:5434/mProductosNueva");
//        p.put("user", "postgres");
//        p.put("pass", "Mega2014");
//        p.put("driver", "org.postgresql.Driver");

        JDBCManager jDBCManager = new JDBCManager();
        JDBCManager.createConection(p);

//        List<Expediente> us = jDBCManager.ejecutarQuery(Expediente.class, "SELECT u.* FROM Expediente u WHERE u.nombre Like :nombre", new HashMap<String, Object>() {
//            {
//                put("nombre", "%%");
//            }
//        }, 1);

        List<Cargo> us = jDBCManager.ejecutarQuery(Cargo.class, "SELECT u.* FROM CARGO u WHERE u.nombre Like :nombre", new HashMap<String, Object>() {
            {
                put("nombre", "%%");
            }
        }, -1);
        
//         List<Cargo> us = new JPAManager().ejecutarQuery("SELECT u FROM Cargo u WHERE u.nombre Like :nombre", new HashMap<String, Object>() {
//            {
//                put("nombre", "%%");
//            }
//        }, -1);
//        Usuario usuario = new Usuario();
//        usuario.setId(1L);
//        usuario.setNombre("GustavoG");
//        usuario.setContrasena(Cifrador.cifrar("hola"));
//        jDBCManager.persist(usuario);
//        
//        usuario = jDBCManager.get(Usuario.class, 1L);
//        System.out.println(usuario);
//        
//        jDBCManager.delete(Usuario.class,1L);
//        usuario = jDBCManager.get(Usuario.class, 1L);
//        System.out.println(usuario);
//
//        for (int i = 8000; i < 16000; i++) {
//
//            Cargo c = new Cargo();
//            c.setId(6L);
//            UsuarioU usuario = new UsuarioU();
//            usuario.setNombre(i+"");
//            usuario.setPassword(i+"");
//            usuario.setCargo(c);
//            jDBCManager.persist(usuario);
//        }

//        if (us != null) {
//            for (Cargo u : us) {
//                System.out.println(u.getId());
//                System.out.println(u.getNombre());
////                System.out.println(u.getDocumentosAsociados().size());
////                for (UsuarioU u1 : u.getUsuarioU()) {
////                    System.out.println(u1.getNombre());
////                }
//            }
//        }
        if (us != null) {
            for (Cargo u : us) {
                System.out.println(u.getId());
                System.out.println(u.getNombre());
                System.out.println(u.getUsuarioU().size());
//                for (UsuarioU u1 : u.getUsuarioU()) {
//                    System.out.println(u1.getNombre());
//                }
            }
        }
    }
}
