package org.shay.dgps.protocol.jt808.dto.basics;

import org.shay.dgps.annotation.Property;
import org.shay.dgps.enums.DataType;
import org.shay.dgps.message.AbstractMessageBody;

/**
 * @author shay
 */
public class MapFence extends AbstractMessageBody {

    private Long id;

    public MapFence() {
    }

    public MapFence(Long id) {
        this.id = id;
    }

    @Property(index = 0, type = DataType.DWORD, desc = "区域ID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}