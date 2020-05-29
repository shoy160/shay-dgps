package org.shay.dgps.protocol.jt808;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.shay.dgps.annotation.Endpoint;
import org.shay.dgps.annotation.Mapping;
import org.shay.dgps.enums.MessageManager;
import org.shay.dgps.message.SyncFuture;
import org.shay.dgps.protocol.jt808.common.MessageId;
import org.shay.dgps.protocol.jt808.dto.*;
import org.shay.dgps.protocol.jt808.dto.basics.Jt808Message;
import org.shay.dgps.session.Session;
import org.shay.dgps.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author shay
 */
@Endpoint
@Component
public class Jt808Endpoint {
    private static final Logger logger = LoggerFactory.getLogger(Jt808Endpoint.class.getSimpleName());

    private SessionManager sessionManager = SessionManager.getInstance();

    private MessageManager messageManager = MessageManager.INSTANCE;

    public Object send(String mobileNumber, String hexMessage) {

        if (!hexMessage.startsWith("7e")) {
            hexMessage = "7e" + hexMessage + "7e";
        }
        ByteBuf msg = Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(hexMessage));
        Session session = SessionManager.getInstance().getByMobileNumber(mobileNumber);

        session.getChannel().writeAndFlush(msg);

        SyncFuture receive = messageManager.receive(mobileNumber);
        try {
            return receive.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            messageManager.remove(mobileNumber);
            e.printStackTrace();
        }
        return null;
    }

    public Object send(Jt808Message message) {
        return send(message, true);
    }

    public Object send(Jt808Message message, boolean hasReplyFlowIdId) {
        String mobileNumber = message.getMobileNumber();
        Session session = sessionManager.getByMobileNumber(mobileNumber);
        message.setSerialNumber(session.currentFlowId());

        session.getChannel().writeAndFlush(message);

        String key = mobileNumber + (hasReplyFlowIdId ? message.getSerialNumber() : "");
        SyncFuture receive = messageManager.receive(key);
        try {
            return receive.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            messageManager.remove(key);
            e.printStackTrace();
        }
        return null;
    }


    @Mapping(types = MessageId.终端通用应答, desc = "终端通用应答")
    public void 终端通用应答(Jt808Message<CommonResult> message) {
        CommonResult body = message.getBody();
        String mobileNumber = message.getMobileNumber();
        Integer replyId = body.getReplyId();
        messageManager.put(mobileNumber + replyId, message);
    }

    @Mapping(types = MessageId.查询终端参数应答, desc = "查询终端参数应答")
    public void 查询终端参数应答(Jt808Message<ParameterSettingReply> message) {
        ParameterSettingReply body = message.getBody();
        String mobileNumber = message.getMobileNumber();
        Integer replyId = message.getSerialNumber();
        messageManager.put(mobileNumber + replyId, message);
    }

    @Mapping(types = MessageId.查询终端属性应答, desc = "查询终端属性应答")
    public void 查询终端属性应答(Jt808Message<TerminalAttributeReply> message) {
        TerminalAttributeReply body = message.getBody();
        String mobileNumber = message.getMobileNumber();
        messageManager.put(mobileNumber, message);
    }

    @Mapping(types = {MessageId.位置信息查询应答, MessageId.车辆控制应答}, desc = "位置信息查询应答/车辆控制应答")
    public void 位置信息查询应答(Jt808Message<PositionReply> message) {
        PositionReply body = message.getBody();
        String mobileNumber = message.getMobileNumber();
        Integer replyId = message.getSerialNumber();
        messageManager.put(mobileNumber + replyId, message);
    }

    @Mapping(types = MessageId.终端RSA公钥, desc = "终端RSA公钥")
    public void 终端RSA公钥(Jt808Message<RSAPack> message) {
        RSAPack body = message.getBody();
        String mobileNumber = message.getMobileNumber();
        messageManager.put(mobileNumber, message);
    }

    @Mapping(types = MessageId.摄像头立即拍摄命令应答, desc = "摄像头立即拍摄命令应答")
    public void 摄像头立即拍摄命令应答(Jt808Message<CameraShotReply> message) {
        CameraShotReply body = message.getBody();
        String mobileNumber = message.getMobileNumber();
        Integer replyId = message.getSerialNumber();
        messageManager.put(mobileNumber + replyId, message);
    }

    @Mapping(types = MessageId.存储多媒体数据检索应答, desc = "存储多媒体数据检索应答")
    public void 存储多媒体数据检索应答(Jt808Message<MediaDataQueryReply> message, Session session) {
        MediaDataQueryReply body = message.getBody();
        String mobileNumber = message.getMobileNumber();
        Integer replyId = message.getSerialNumber();
        messageManager.put(mobileNumber + replyId, message);
    }
    //=============================================================

    @Mapping(types = MessageId.终端心跳, desc = "终端心跳")
    public Jt808Message heartBeat(Jt808Message message, Session session) {
        CommonResult result = new CommonResult(MessageId.终端心跳, message.getSerialNumber(), CommonResult.Success);
        return new Jt808Message(MessageId.平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = MessageId.补传分包请求, desc = "补传分包请求")
    public Jt808Message 补传分包请求(Jt808Message<RepairPackRequest> message, Session session) {
        RepairPackRequest body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(MessageId.补传分包请求, message.getSerialNumber(), CommonResult.Success);
        return new Jt808Message(MessageId.平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = MessageId.终端注册, desc = "终端注册")
    public Jt808Message<RegisterReply> register(Jt808Message<Register> message, Session session) {
        Register body = message.getBody();
        //TODO
        sessionManager.put(Session.buildId(session.getChannel()), session);

        RegisterReply result = new RegisterReply(message.getSerialNumber(), RegisterReply.Success, "test_token");
        return new Jt808Message(MessageId.终端注册应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = MessageId.终端注销, desc = "终端注销")
    public Jt808Message 终端注销(Jt808Message message, Session session) {
        //TODO
        CommonResult result = new CommonResult(MessageId.终端注销, message.getSerialNumber(), CommonResult.Success);
        return new Jt808Message(MessageId.平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = MessageId.终端鉴权, desc = "终端鉴权")
    public Jt808Message authentication(Jt808Message<Authentication> message, Session session) {
        Authentication body = message.getBody();
        //TODO
        session.setTerminalId(message.getMobileNumber());
        sessionManager.put(Session.buildId(session.getChannel()), session);
        CommonResult result = new CommonResult(MessageId.终端鉴权, message.getSerialNumber(), CommonResult.Success);
        return new Jt808Message(MessageId.平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = MessageId.终端升级结果通知, desc = "终端升级结果通知")
    public Jt808Message 终端升级结果通知(Jt808Message<TerminalUpgradeNotify> message, Session session) {
        TerminalUpgradeNotify body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(MessageId.终端升级结果通知, message.getSerialNumber(), CommonResult.Success);
        return new Jt808Message(MessageId.平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = MessageId.位置信息汇报, desc = "位置信息汇报")
    public Jt808Message 位置信息汇报(Jt808Message<PositionReport> message, Session session) {
        PositionReport body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(MessageId.位置信息汇报, message.getSerialNumber(), CommonResult.Success);
        return new Jt808Message(MessageId.平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = MessageId.人工确认报警消息, desc = "人工确认报警消息")
    public Jt808Message 人工确认报警消息(Jt808Message<WarningMessage> message, Session session) {
        WarningMessage body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(MessageId.位置信息汇报, message.getSerialNumber(), CommonResult.Success);
        return new Jt808Message(MessageId.平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = MessageId.事件报告, desc = "事件报告")
    public Jt808Message 事件报告(Jt808Message<EventReport> message, Session session) {
        EventReport body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(MessageId.事件报告, message.getSerialNumber(), CommonResult.Success);
        return new Jt808Message(MessageId.平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = MessageId.提问应答, desc = "提问应答")
    public Jt808Message 提问应答(Jt808Message<QuestionMessageReply> message, Session session) {
        QuestionMessageReply body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(MessageId.提问应答, message.getSerialNumber(), CommonResult.Success);
        return new Jt808Message(MessageId.平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = MessageId.信息点播_取消, desc = "信息点播/取消")
    public Jt808Message 信息点播取消(Jt808Message<MessageSubOperate> message, Session session) {
        MessageSubOperate body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(MessageId.信息点播_取消, message.getSerialNumber(), CommonResult.Success);
        return new Jt808Message(MessageId.平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = MessageId.电话回拨, desc = "电话回拨")
    public Jt808Message 电话回拨(Jt808Message<CallPhone> message, Session session) {
        CallPhone body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(MessageId.电话回拨, message.getSerialNumber(), CommonResult.Success);
        return new Jt808Message(MessageId.平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = MessageId.行驶记录仪数据上传, desc = "行驶记录仪数据上传")
    public Jt808Message 行驶记录仪数据上传(Jt808Message message, Session session) {
        CommonResult result = new CommonResult(MessageId.行驶记录仪数据上传, message.getSerialNumber(), CommonResult.Success);
        return new Jt808Message(MessageId.平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = MessageId.行驶记录仪参数下传命令, desc = "行驶记录仪参数下达命令")
    public Jt808Message 行驶记录仪参数下传命令(Jt808Message message, Session session) {
        //TODO
        CommonResult result = new CommonResult(MessageId.行驶记录仪参数下传命令, message.getSerialNumber(), CommonResult.Success);
        return new Jt808Message(MessageId.平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = MessageId.电子运单上报, desc = "电子运单上报")
    public Jt808Message 电子运单上报(Jt808Message message, Session session) {
        //TODO
        CommonResult result = new CommonResult(MessageId.电子运单上报, message.getSerialNumber(), CommonResult.Success);
        return new Jt808Message(MessageId.平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = MessageId.驾驶员身份信息采集上报, desc = "驾驶员身份信息采集上报")
    public Jt808Message 驾驶员身份信息采集上报(Jt808Message<DriverIdentityInfo> message, Session session) {
        DriverIdentityInfo body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(MessageId.驾驶员身份信息采集上报, message.getSerialNumber(), CommonResult.Success);
        return new Jt808Message(MessageId.平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = MessageId.定位数据批量上传, desc = "定位数据批量上传")
    public Jt808Message 定位数据批量上传(Jt808Message<PositionReportBatch> message, Session session) {
        PositionReportBatch body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(MessageId.定位数据批量上传, message.getSerialNumber(), CommonResult.Success);
        return new Jt808Message(MessageId.平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = MessageId.CAN总线数据上传, desc = "定位数据批量上传")
    public Jt808Message CAN总线数据上传(Jt808Message<CANBusReport> message, Session session) {
        CANBusReport body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(MessageId.CAN总线数据上传, message.getSerialNumber(), CommonResult.Success);
        return new Jt808Message(MessageId.平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = MessageId.多媒体事件信息上传, desc = "多媒体事件信息上传")
    public Jt808Message 多媒体事件信息上传(Jt808Message<MediaEventReport> message, Session session) {
        MediaEventReport body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(MessageId.多媒体事件信息上传, message.getSerialNumber(), CommonResult.Success);
        return new Jt808Message(MessageId.平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = MessageId.多媒体数据上传, desc = "多媒体数据上传")
    public Jt808Message 多媒体数据上传(Jt808Message<MediaDataReport> message, Session session) throws IOException {
        MediaDataReport body = message.getBody();

        byte[] packet = body.getPacket();
        FileOutputStream fos = new FileOutputStream("D://test.jpg");
        fos.write(packet);
        fos.close();

        MediaDataReportReply result = new MediaDataReportReply();
        result.setMediaId(body.getId());
        return new Jt808Message(MessageId.平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = MessageId.数据上行透传, desc = "数据上行透传")
    public Jt808Message passthrough(Jt808Message<PassthroughPack> message, Session session) {
        PassthroughPack body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(MessageId.数据上行透传, message.getSerialNumber(), CommonResult.Success);
        return new Jt808Message(MessageId.平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

    @Mapping(types = MessageId.数据压缩上报, desc = "数据压缩上报")
    public Jt808Message gzipPack(Jt808Message<GZIPPack> message, Session session) {
        GZIPPack body = message.getBody();
        //TODO
        CommonResult result = new CommonResult(MessageId.数据压缩上报, message.getSerialNumber(), CommonResult.Success);
        return new Jt808Message(MessageId.平台通用应答, session.currentFlowId(), message.getMobileNumber(), result);
    }

}