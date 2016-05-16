/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orz.gg.funciones;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author GustavoG
 */
@Entity
public class Cargo implements Serializable {
    
    @Id
    private Long id;
    @Column
    private String nombre;
    @OneToMany(mappedBy = "cargo")
    private List<UsuarioU> usuarioU;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<UsuarioU> getUsuarioU() {
        if(usuarioU == null){
            usuarioU = new ArrayList();
        }
        return usuarioU;
    }

    public void setUsuarioU(List<UsuarioU> usuarioU) {
        this.usuarioU = usuarioU;
    }
    
    
}
