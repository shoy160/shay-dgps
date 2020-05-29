package org.shay.dgps.protocol.jt808.dto;

import org.shay.dgps.annotation.Property;
import org.shay.dgps.annotation.Type;
import org.shay.dgps.enums.DataType;
import org.shay.dgps.message.AbstractMessageBody;
import org.shay.dgps.protocol.jt808.common.MessageId;

/**
 * @author shay
 */
@Type(MessageId.事件报告)
public class EventReport extends AbstractMessageBody {

    private Integer eventId;

    @Property(index = 0, type = DataType.BYTE, desc = "事件ID")
    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }
}