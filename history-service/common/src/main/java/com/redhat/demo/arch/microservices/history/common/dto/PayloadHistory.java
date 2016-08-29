/*
 *
 */
package com.redhat.demo.arch.microservices.history.common.dto;

import java.util.Date;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoMessage;

@ProtoDoc("@Indexed")
public class PayloadHistory {

    private String payload;
    private Date timestamp;

    /**
     * @return the payload
     */
    @ProtoField(number = 1, required = true)
    @ProtoDoc("@IndexedField(index = true, store = true)")
    public String getPayload() {
        return payload;
    }

    /**
     * @param payload
     *            the payload to set
     */
    public void setPayload(String payload) {
        this.payload = payload;
    }

    /**
     * @return the timestamp
     */
    @ProtoField(number = 2, required = true)
    @ProtoDoc("@IndexedField(index = true, store = true)")
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp
     *            the timestamp to set
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result)
                + ((payload == null) ? 0 : payload.hashCode());
        result = (prime * result)
                + ((timestamp == null) ? 0 : timestamp.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PayloadHistory other = (PayloadHistory) obj;
        if (payload == null) {
            if (other.payload != null) {
                return false;
            }
        } else if (!payload.equals(other.payload)) {
            return false;
        }
        if (timestamp == null) {
            if (other.timestamp != null) {
                return false;
            }
        } else if (!timestamp.equals(other.timestamp)) {
            return false;
        }
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PayloadHistory [\n    payload=");
        builder.append(payload);
        builder.append(", \n    timestamp=");
        builder.append(timestamp);
        builder.append("\n]");
        return builder.toString();
    }

}
