/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author madziwal
 */
@Entity
@Table(name = "REGISTER")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Register.findAll", query = "SELECT r FROM Register r"),
    @NamedQuery(name = "Register.findByMsisdn", query = "SELECT r FROM Register r WHERE r.msisdn = :msisdn"),
    @NamedQuery(name = "Register.findByCosjobid", query = "SELECT r FROM Register r WHERE r.cosjobid = :cosjobid"),
    @NamedQuery(name = "Register.findByRem6jobid", query = "SELECT r FROM Register r WHERE r.rem6jobid = :rem6jobid"),
    @NamedQuery(name = "Register.findByRem7jobid", query = "SELECT r FROM Register r WHERE r.rem7jobid = :rem7jobid"),
    @NamedQuery(name = "Register.findByStatus", query = "SELECT r FROM Register r WHERE r.status = :status")})
public class Register implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "MSISDN")
    private String msisdn;
    @Column(name = "COSJOBID")
    private String cosjobid;
    @Column(name = "REM6JOBID")
    private String rem6jobid;
    @Column(name = "REM7JOBID")
    private String rem7jobid;
    @Column(name = "STATUS")
    private String status;

    public Register() {
    }

    public Register(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getCosjobid() {
        return cosjobid;
    }

    public void setCosjobid(String cosjobid) {
        this.cosjobid = cosjobid;
    }

    public String getRem6jobid() {
        return rem6jobid;
    }

    public void setRem6jobid(String rem6jobid) {
        this.rem6jobid = rem6jobid;
    }

    public String getRem7jobid() {
        return rem7jobid;
    }

    public void setRem7jobid(String rem7jobid) {
        this.rem7jobid = rem7jobid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (msisdn != null ? msisdn.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Register)) {
            return false;
        }
        Register other = (Register) object;
        if ((this.msisdn == null && other.msisdn != null) || (this.msisdn != null && !this.msisdn.equals(other.msisdn))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "zw.co.telecel.model.Register[ msisdn=" + msisdn + " ]";
    }
    
}
