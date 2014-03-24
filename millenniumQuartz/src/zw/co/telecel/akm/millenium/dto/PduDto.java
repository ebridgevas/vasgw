/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.telecel.akm.millenium.dto;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 12/15/12
 * Time: 6:29 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class PduDto implements Serializable {

    @Id
    private String uuid;

    @Basic
    private String channel;

    @Temporal(TIMESTAMP)
    private Date pduDate;

    @Basic
    private String pduType;

    @Basic
    private String sourceId;

    @Basic
    private String destinationId;

    @Basic
    private String shortMessage;

    @Basic
    private String debugString;

    public PduDto() {
    }

    public PduDto(String uuid,
                  String channel,
                  Date pduDate,
                  String pduType,
                  String sourceId,
                  String destinationId,
                  String shortMessage,
                  String debugString) {

        this.uuid = uuid;
        this.channel = channel;
        this.pduDate = pduDate;
        this.pduType = pduType;
        this.sourceId = sourceId;
        this.destinationId = destinationId;
        this.shortMessage = shortMessage;
        this.debugString = debugString;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Date getPduDate() {
        return pduDate;
    }

    public void setPduDate(Date pduDate) {
        this.pduDate = pduDate;
    }

    public String getPduType() {
        return pduType;
    }

    public void setPduType(String pduType) {
        this.pduType = pduType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public void setShortMessage(String shortMessage) {
        this.shortMessage = shortMessage;
    }

    public String getDebugString() {
        return debugString;
    }

    public void setDebugString(String debugString) {
        this.debugString = debugString;
    }

}