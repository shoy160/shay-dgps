package org.shay.dgps.protocol.jt808.dto;

import org.shay.dgps.annotation.Property;
import org.shay.dgps.annotation.Type;
import org.shay.dgps.enums.DataType;
import org.shay.dgps.message.AbstractMessageBody;
import org.shay.dgps.protocol.jt808.common.MessageId;

/**
 * @author shay
 */
@Type(MessageId.终端鉴权)
public class Authentication extends AbstractMessageBody {

    /**
     * 终端重连后上报鉴权码
     */
    private String token;

    @Property(index = 0, type = DataType.STRING, desc = "鉴权码")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}