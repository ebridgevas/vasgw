/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.dto;

import java.io.Serializable;
import java.util.Date;
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
public class ERRORLOG implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date logDate;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date logDateTimestamp;
    
    private String eventType;
    
    private String comment;
    
    private String msisdn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    public Date getLogDateTimestamp() {
        return logDateTimestamp;
    }

    public void setLogDateTimestamp(Date logDateTimestamp) {
        this.logDateTimestamp = logDateTimestamp;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
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
        if (!(object instanceof ERRORLOG)) {
            return false;
        }
        ERRORLOG other = (ERRORLOG) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "zw.co.telecel.akm.millenium.dto.ERRORLOG[ id=" + id + " ]";
    }
    
}
