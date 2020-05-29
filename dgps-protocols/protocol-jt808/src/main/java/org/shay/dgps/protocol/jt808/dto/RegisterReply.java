package org.shay.dgps.protocol.jt808.dto;

import org.shay.dgps.annotation.Property;
import org.shay.dgps.annotation.Type;
import org.shay.dgps.enums.DataType;
import org.shay.dgps.message.AbstractMessageBody;
import org.shay.dgps.protocol.jt808.common.MessageId;

/**
 * @author shay
 */
@Type(MessageId.终端注册应答)
public class RegisterReply extends AbstractMessageBody {

    /**
     * 0：成功
     */
    public static final int Success = 0;
    /**
     * 1：车辆已被注册
     */
    public static final int AlreadyRegisteredVehicle = 1;
    /**
     * 2：数据库中无该车辆
     */
    public static final int NotFoundVehicle = 2;
    /**
     * 3：终端已被注册
     */
    public static final int AlreadyRegisteredTerminal = 3;
    /**
     * 4：数据库中无该终端
     */
    public static final int NotFoundTerminal = 4;

    private Integer serialNumber;
    private Integer resultCode;
    private String token;

    public RegisterReply() {
    }

    public RegisterReply(Integer serialNumber, Integer resultCode, String token) {
        this.serialNumber = serialNumber;
        this.resultCode = resultCode;
        this.token = token;
    }

    /**
     * 对应的终端注册消息的流水号
     */
    @Property(index = 0, type = DataType.WORD, desc = "应答流水号")
    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * 0-4
     */
    @Property(index = 2, type = DataType.BYTE, desc = "结果")
    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * 成功后才有该字段
     */
    @Property(index = 3, type = DataType.STRING, desc = "鉴权码")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}