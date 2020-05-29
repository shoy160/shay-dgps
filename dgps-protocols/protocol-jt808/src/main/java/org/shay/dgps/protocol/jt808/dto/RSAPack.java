package org.shay.dgps.protocol.jt808.dto;

import org.shay.dgps.annotation.Property;
import org.shay.dgps.annotation.Type;
import org.shay.dgps.enums.DataType;
import org.shay.dgps.message.AbstractMessageBody;

import static org.shay.dgps.protocol.jt808.common.MessageId.平台RSA公钥;
import static org.shay.dgps.protocol.jt808.common.MessageId.终端RSA公钥;

/**
 * @author shay
 */
@Type({平台RSA公钥, 终端RSA公钥})
public class RSAPack extends AbstractMessageBody {

    private Long e;
    private byte[] n;

    public RSAPack() {
    }

    public RSAPack(Long e, byte[] n) {
        this.e = e;
        this.n = n;
    }

    @Property(index = 0, type = DataType.DWORD, desc = "RSA公钥{e,n}中的e")
    public Long getE() {
        return e;
    }

    public void setE(Long e) {
        this.e = e;
    }

    @Property(index = 4, type = DataType.BYTES, length = 128, desc = "RSA公钥{e,n}中的n")
    public byte[] getN() {
        return n;
    }

    public void setN(byte[] n) {
        this.n = n;
    }
}