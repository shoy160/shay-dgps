package org.shay.dgps.protocol.jt808.dto;

import org.shay.dgps.annotation.Property;
import org.shay.dgps.annotation.Type;
import org.shay.dgps.enums.DataType;
import org.shay.dgps.message.AbstractMessageBody;
import org.shay.dgps.protocol.jt808.common.MessageId;

@Type({MessageId.平台通用应答, MessageId.终端通用应答})
public class CommonResult extends AbstractMessageBody {

    public static final int Success = 0;
    public static final int Fial = 1;
    public static final int MessageError = 2;
    public static final int NotSupport = 3;
    public static final int AlarmConfirmation = 4;

    private Integer flowId;

    private Integer replyId;

    private Integer resultCode;

    public CommonResult() {
    }

    public CommonResult(Integer replyId, Integer flowId, Integer resultCode) {
        this.replyId = replyId;
        this.flowId = flowId;
        this.resultCode = resultCode;
    }

    @Property(index = 0, type = DataType.WORD, desc = "应答流水号")
    public Integer getFlowId() {
        return flowId;
    }

    public void setFlowId(Integer flowId) {
        this.flowId = flowId;
    }

    @Property(index = 2, type = DataType.WORD, desc = "应答ID")
    public Integer getReplyId() {
        return replyId;
    }

    public void setReplyId(Integer replyId) {
        this.replyId = replyId;
    }

    @Property(index = 4, type = DataType.BYTE, desc = "结果（响应码）")
    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

}