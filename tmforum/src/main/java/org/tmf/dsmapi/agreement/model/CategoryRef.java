package org.tmf.dsmapi.agreement.model;

//import javax.xml.bind.annotation.XmlAccessType;
//import javax.xml.bind.annotation.XmlAccessorType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.CollectionTable;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embedded;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ElementCollection;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

//@XmlAccessorType(XmlAccessType.FIELD)
@Entity(name = "CategoryRef")
@Table(name = "CATEGORY_REF")
@Inheritance(strategy = InheritanceType.JOINED)
public class CategoryRef implements Serializable {

    protected  String id;

    protected String href;

    protected String name;

    protected String version;


    public CategoryRef() {
    }

    public CategoryRef(String id, String href, String name, String version) {
        this.id = id;
        this.href = href;
        this.name = name;
        this.version = version;
    }

    /**
     * Return the ID of the object
     * @return
     *
     * allowed object is
     * {@link String}
     */

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     *
     * allowed object is
     * {@link String}
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Return the HREF of the object
     * @return
     *
     * allowed object is
     * {@link String}
     */
    @Basic
    @Column(name = "HREF", length = 255)
    public String getHref() {
        return href;
    }

    /**
     * Set an HREF string for the object
     * @param href
     *
     * allowed object is
     * {@link String}
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * Return the Name of the object
     * @return
     *
     * allowed object is
     * {@link String}
     */
    @Basic
    @Column(name = "NAME_")
    public String getName() {
        return name;
    }

    /**
     * Set the name of object
     * @param name
     *
     * allowed object is
     * {@link String}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the Version of the object
     * @return
     *
     * allowed object is
     * {@link String}
     */
    @Basic
    @Column(name = "VERSION", length = 255)
    public String getVersion() {
        return version;
    }

    /**
     * Set the version of the object
     * @param version
     *
     * allowed object is
     * {@link String}
     */
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "CategoryRef{" +
                "id='" + id + '\'' +
                ", href='" + href + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}