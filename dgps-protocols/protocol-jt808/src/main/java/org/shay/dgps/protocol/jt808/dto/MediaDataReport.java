package org.shay.dgps.protocol.jt808.dto;

import org.shay.dgps.annotation.Property;
import org.shay.dgps.annotation.Type;
import org.shay.dgps.enums.DataType;
import org.shay.dgps.message.AbstractMessageBody;
import org.shay.dgps.protocol.jt808.common.MessageId;

/**
 * @author shay
 */
@Type(MessageId.多媒体数据上传)
public class MediaDataReport extends AbstractMessageBody {

    private Integer id;
    private Integer type;
    private Integer format;
    private Integer event;
    private Integer channelId;
    private PositionReport position;
    private byte[] packet;

    @Property(index = 0, type = DataType.DWORD, desc = "多媒体数据ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Property(index = 4, type = DataType.BYTE, desc = "多媒体类型 0：图像；1：音频；2：视频；")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Property(index = 5, type = DataType.BYTE, desc = "多媒体格式编码 0：JPEG；1：TIF；2：MP3；3：WAV；4：WMV；")
    public Integer getFormat() {
        return format;
    }

    public void setFormat(Integer format) {
        this.format = format;
    }

    @Property(index = 6, type = DataType.BYTE, desc = "事件项编码")
    public Integer getEvent() {
        return event;
    }

    public void setEvent(Integer event) {
        this.event = event;
    }

    @Property(index = 7, type = DataType.BYTE, desc = "通道ID")
    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    @Property(index = 8, type = DataType.OBJ, length = 28, desc = "位置信息")
    public PositionReport getPosition() {
        return position;
    }

    public void setPosition(PositionReport position) {
        this.position = position;
    }

    @Property(index = 36, type = DataType.BYTES, desc = "多媒体数据包")
    public byte[] getPacket() {
        return packet;
    }

    public void setPacket(byte[] packet) {
        this.packet = packet;
    }
}