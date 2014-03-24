/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.dto;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;

/**
 *
 * @author matsaudzaa
 */
@Entity
public class Register implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String msisdn;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dateOfPurchase;
    
    @Basic
    private String status;
    @Basic
    private String cosJobId;
    @Basic
    private String rem6JobId;
    @Basic
    private String rem7JobId;
    @Basic
    private String bundleType;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date expiryDate;

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    
    

    public String getBundleType() {
        return bundleType;
    }

    public void setBundleType(String bundleType) {
        this.bundleType = bundleType;
    }
    
    

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCosJobId() {
        return cosJobId;
    }

    public void setCosJobId(String cosJobId) {
        this.cosJobId = cosJobId;
    }

    public String getRem6JobId() {
        return rem6JobId;
    }

    public void setRem6JobId(String rem6JobId) {
        this.rem6JobId = rem6JobId;
    }

    public String getRem7JobId() {
        return rem7JobId;
    }

    public void setRem7JobId(String rem7JobId) {
        this.rem7JobId = rem7JobId;
    }

    public Date getDateOfPurchase() {
        return dateOfPurchase;
    }

    public void setDateOfPurchase(Date dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
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
        return "zw.co.telecel.akm.millenium.dto.Register[ msisdn=" + msisdn + " ]";
    }
    
}
