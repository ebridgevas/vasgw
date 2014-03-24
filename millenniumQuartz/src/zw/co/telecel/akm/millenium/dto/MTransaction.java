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
public class MTransaction implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    
    @Basic
    private String msisdn;
    
    @Basic
    private String transactionType;
    
    @Basic
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date transactionDate;
    
    @Basic
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date transactionDateTimestamp;
    
    @Basic
    private Double amount;

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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
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
        if (!(object instanceof MTransaction)) {
            return false;
        }
        MTransaction other = (MTransaction) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "zw.co.telecel.akm.millenium.dto.International[ id=" + id + " ]";
    }
    
}
