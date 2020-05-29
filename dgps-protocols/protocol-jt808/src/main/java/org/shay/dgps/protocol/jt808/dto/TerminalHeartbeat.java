package org.shay.dgps.protocol.jt808.dto;

import org.shay.dgps.annotation.Type;
import org.shay.dgps.message.AbstractMessageBody;
import org.shay.dgps.protocol.jt808.common.MessageId;

@Type(MessageId.终端心跳)
public class TerminalHeartbeat extends AbstractMessageBody {

}