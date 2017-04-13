package org.tmf.dsmapi.agreement.event;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import org.tmf.dsmapi.agreement.event.AgreementEventEnum;
import org.tmf.dsmapi.agreement.model.Agreement;


@SuppressWarnings("All")
@Entity
@Table(name="EVENT_AGREEMENT")

public class AgreementEvent implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date eventTime;

    @Enumerated(value = EnumType.STRING)
    private AgreementEventEnum eventType;

    @Transient
    private Agreement resource; //check for object

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public AgreementEventEnum getEventType() {
        return eventType;
    }

    public void setEventType(AgreementEventEnum eventType) {
        this.eventType = eventType;
    }


    public Agreement getResource() {
        return resource;
    }

    public void setResource(Agreement resource) {
        this.resource = resource;
    }


    class EventBody {
        private Agreement entity;
        public Agreement getAppointment() {
            return entity;
        }
        public EventBody(Agreement entity) {
            this.entity = entity;
        }
    }


    public EventBody getEvent() {
        return new EventBody(getResource() );
    }

    @Override
    public String toString() {
        return "AgreementEvent{" +
                "id='" + id + '\'' +
                ", eventTime=" + eventTime +
                ", eventType=" + eventType +
                ", resource=" + resource +
                '}';
    }
}