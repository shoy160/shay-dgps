package org.shay.dgps.protocol.jt808.dto;

import org.shay.dgps.annotation.Property;
import org.shay.dgps.annotation.Type;
import org.shay.dgps.enums.DataType;
import org.shay.dgps.message.AbstractMessageBody;
import org.shay.dgps.protocol.jt808.common.MessageId;

@Type(MessageId.临时位置跟踪控制)
public class TemporaryMonitor extends AbstractMessageBody {

    private Integer interval;
    private Integer validityPeriod;

    @Property(index = 0, type = DataType.WORD, desc = "时间间隔（秒）")
    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    @Property(index = 2, type = DataType.DWORD, desc = "有效期（秒）")
    public Integer getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(Integer validityPeriod) {
        this.validityPeriod = validityPeriod;
    }
}