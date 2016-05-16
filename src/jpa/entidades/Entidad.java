/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.entidades;

import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

/**
 * <b>Entidad</b> abstracta con toda la configuración básica.
 * <br><br>
 * <b>Clase</b> útil para usar como basa común a todas las entidades,
 * facilitando la tarea de realizar clases reutilizables que manejan
 * <b>Entidad</b> en vez de una entidad concreta.
 *
 * @author Gianluigi Pierini
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
public class Entidad implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

//    GETTERS Y SETTERS
    public Long getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

//    METODOS SOBREESCRITOS
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Entidad)) {
            return false;
        }
        Entidad other = (Entidad) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "Entidad{" + "id=" + id + '}';
    }
}
