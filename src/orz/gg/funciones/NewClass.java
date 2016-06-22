/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orz.gg.funciones;

import com.megagroup.entidades.administracion.funcionales.TipoExpediente;
import com.megagroup.entidades.almacenamiento.UnidadAlmacenamientoNivel3;
import com.megagroup.entidades.funcionales.Expediente;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author GustavoG
 */
public class NewClass {

    public static void main(String[] args) {

//        NewClass.class.getPackage().getClass().
        Properties p = new Properties();
        p.put("url", "jdbc:postgresql://192.168.123.236:5434/mProductos24");
        p.put("user", "postgres");
        p.put("pass", "Mega2014");
        p.put("driver", "org.postgresql.Driver");
//        Properties p = new Properties();
//        p.put("url", "jdbc:postgresql://192.168.123.236:5434/mProductosNueva");
//        p.put("user", "postgres");
//        p.put("pass", "Mega2014");
//        p.put("driver", "org.postgresql.Driver");

        JDBCManager jDBCManager = new JDBCManager();
        JDBCManager.createConection(p);

//        Usuario us = jDBCManager.ejecutarQuery(Usuario.class, "SELECT * FROM Usuario ",null);
//        System.out.println(us);
        
        List<Expediente> us = jDBCManager.ejecutarQuery(Expediente.class, "SELECT * FROM Expediente ", null, -1);
        
        
        for (Expediente u : us) {
            if(u.getUnidadAlmacenamiento3() != null){
                System.out.println(u.getUnidadAlmacenamiento3().getUnidadAlmacenamiento2());
            }
            
        }
//        List<Cargo> us = jDBCManager.ejecutarQuery(Cargo.class, "SELECT u.* FROM CARGO u WHERE u.nombre Like :nombre", new HashMap<String, Object>() {
//            {
//                put("nombre", "%%");
//            }
//        }, -1);
        
//         List<UnidadAlmacenamientoNivel1> us = jDBCManager.ejecutarQuery(UnidadAlmacenamientoNivel1.class,"SELECT * FROM UnidadAlmacenamientoNivel1 ",null, -1);
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
//        for (long i = 9003; i < 9004; i++) {
//
//            Expediente ua1 = new Expediente();
//            ua1.setId(i);
//            ua1.setNombre(i+"");
//            ua1.setEstado("Archivado");
//            ua1.setFechaCreacion(new Date());
//            UnidadAlmacenamientoNivel3 c = jDBCManager.get(UnidadAlmacenamientoNivel3.class, 106L);
//            TipoExpediente tp = jDBCManager.get(TipoExpediente.class, 1L);
//            ua1.setTipoExpediente(tp);
//            ua1.setUnidadAlmacenamiento3(c);
//            jDBCManager.persist(ua1);
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
//        if (us != null) {
//            for (Cargo u : us) {
//                System.out.println(u.getId());
//                System.out.println(u.getNombre());
//                System.out.println(u.getUsuarioU().size());
////                for (UsuarioU u1 : u.getUsuarioU()) {
////                    System.out.println(u1.getNombre());
////                }
//            }
//        }
    }
}
