package org.shay.dgps.protocol.jt808.dto;

import org.shay.dgps.annotation.Property;
import org.shay.dgps.annotation.Type;
import org.shay.dgps.enums.DataType;
import org.shay.dgps.message.AbstractMessageBody;
import org.shay.dgps.protocol.jt808.common.MessageId;

import java.util.List;

/**
 * @author shay
 */
@Type(MessageId.定位数据批量上传)
public class PositionReportBatch extends AbstractMessageBody {

    private Integer total;
    private Integer type;
    private List<Item> list;

    @Property(index = 0, type = DataType.WORD, desc = "数据项个数")
    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    @Property(index = 2, type = DataType.BYTE, desc = "位置数据类型 0：正常位置批量汇报，1：盲区补报")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Property(index = 3, type = DataType.LIST, desc = "位置汇报数据项")
    public List<Item> getList() {
        return list;
    }

    public void setList(List<Item> list) {
        this.list = list;
    }

    public static class Item {

        private Integer length;
        private PositionReport position;

        @Property(index = 0, type = DataType.WORD, desc = "位置汇报数据体长度")
        public Integer getLength() {
            return length;
        }

        public void setLength(Integer length) {
            this.length = length;
        }

        @Property(index = 2, type = DataType.OBJ, lengthName = "length", desc = "位置汇报数据项")
        public PositionReport getPosition() {
            return position;
        }

        public void setPosition(PositionReport position) {
            this.position = position;
        }
    }
}