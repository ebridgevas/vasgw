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
public class BundleDto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;

    @Basic
    private Double amount;
    
    @Basic
    private String mobileNumber;
    
    @Basic
    private String bundleType;
   
    @Basic
    private String transactionType;
    
    @Basic
    private Double duration;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date expiryDate;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date expiryDateTimestamp;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date transactionDate;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date transactionDateTimestamp;
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BundleDto)) {
            return false;
        }
        BundleDto other = (BundleDto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }
    
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getBundleType() {
        return bundleType;
    }

    public void setBundleType(String bundleType) {
        this.bundleType = bundleType;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Date getTransactionDateTimestamp() {
        return transactionDateTimestamp;
    }

    public void setTransactionDateTimestamp(Date transactionDateTimestamp) {
        this.transactionDateTimestamp = transactionDateTimestamp;
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

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    
    

    @Override
    public String toString() {
        return "com.ebridge.sdp.vas.dto.BundleDto[ id=" + id + " ]";
    }
    
}
