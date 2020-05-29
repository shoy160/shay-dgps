package org.shay.dgps.protocol.jt808.dto;

import org.shay.dgps.annotation.Property;
import org.shay.dgps.annotation.Type;
import org.shay.dgps.enums.DataType;
import org.shay.dgps.message.AbstractMessageBody;
import org.shay.dgps.protocol.jt808.common.MessageId;

@Type(MessageId.人工确认报警消息)
public class WarningMessage extends AbstractMessageBody {

    private Integer serialNumber;
    private Integer type;

    @Property(index = 0, type = DataType.WORD, desc = "消息流水号")
    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * [ 0 ]   1：确认紧急报警；
     * [1-2]   保留
     * [ 3 ]   1：确认危险预警；
     * [4-19]  保留
     * [ 20 ]  1：确认进出区域报警；
     * [ 21 ]  1：确认进出路线报警；
     * [ 22 ]  1：确认路段行驶时间不足/过长报警；
     * [23-26] 保留
     * [ 27 ]  1：确认车辆非法点火报警；
     * [ 28 ]  1：确认车辆非法位移报警；
     * [29-31] 保留
     */
    @Property(index = 2, type = DataType.DWORD, desc = "报警类型")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}