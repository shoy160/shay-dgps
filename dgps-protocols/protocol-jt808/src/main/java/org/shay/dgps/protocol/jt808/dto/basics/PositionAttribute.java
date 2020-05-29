package org.shay.dgps.protocol.jt808.dto.basics;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.shay.dgps.annotation.Property;
import org.shay.dgps.enums.DataType;
import org.shay.dgps.message.AbstractMessageBody;

/**
 * 位置附加信息项
 *
 * @author shay
 */
public class PositionAttribute extends AbstractMessageBody {

    private Integer id;
    private Integer length;
    private byte[] bytesValue;
    private Object value;

    @Property(index = 0, type = DataType.BYTE, desc = "参数ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Property(index = 1, type = DataType.BYTE, desc = "参数长度")
    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    @Property(index = 2, type = DataType.BYTES, lengthName = "length", desc = "参数值")
    public byte[] getBytesValue() {
        return bytesValue;
    }

    public void setBytesValue(byte[] bytesValue) {
        this.bytesValue = bytesValue;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}