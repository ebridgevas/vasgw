/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.dto;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;

/**
 *
 * @author matsaudzaa
 */
@Entity
public class Schedule implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    
    @Basic
    private String msisdn;
    
   
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date entryDate;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date entryDateTimestamp;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date expiryDate;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date expiryDateTimestamp;
    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public Date getEntryDateTimestamp() {
        return entryDateTimestamp;
    }

    public void setEntryDateTimestamp(Date entryDateTimestamp) {
        this.entryDateTimestamp = entryDateTimestamp;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Date getExpiryDateTimestamp() {
        return expiryDateTimestamp;
    }

    public void setExpiryDateTimestamp(Date expiryDateTimestamp) {
        this.expiryDateTimestamp = expiryDateTimestamp;
    }
    
    
    
   
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Schedule)) {
            return false;
        }
        Schedule other = (Schedule) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "zw.co.telecel.akm.millenium.dto.Schedule[ id=" + id + " ]";
    }
    
}
