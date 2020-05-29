package org.shay.dgps.protocol.jt808.dto;

import org.shay.dgps.annotation.Property;
import org.shay.dgps.annotation.Type;
import org.shay.dgps.enums.DataType;
import org.shay.dgps.message.AbstractMessageBody;
import org.shay.dgps.protocol.jt808.common.MessageId;

/**
 * @author shay
 */
@Type(MessageId.提问应答)
public class QuestionMessageReply extends AbstractMessageBody {

    private Integer serialNumber;
    private Integer answerId;

    @Property(index = 0, type = DataType.WORD, desc = "应答流水号")
    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Property(index = 2, type = DataType.BYTE, desc = "答案ID")
    public Integer getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Integer answerId) {
        this.answerId = answerId;
    }
}