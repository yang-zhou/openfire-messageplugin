package org.igniterealtime.openfire.messageplugin;

import org.dom4j.Element;
import org.igniterealtime.openfire.messageplugin.utils.MarshalUtils;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

public class MessageFilter implements PacketInterceptor {
	private static final Logger Log = LoggerFactory.getLogger(MessageFilter.class);

	@Override
	public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed)
			throws PacketRejectedException {
		Packet packetCopy = packet.createCopy();
		if (packetCopy instanceof Message) {
			Message message = (Message) packetCopy;
			if (Message.Type.chat == message.getType() && incoming && processed) {
				// 一对一聊天
				try {
					ChatMessageType type = valiedMessage(message);
					switch (type) {
					case MESSAGE:
						// 序列化数据
						ChatMessageVo vo = buildChatMessageVoFromMessage(message);
						String voMsg = MarshalUtils.marshalJsonAsString(vo);
						MessageQueueBuffer.getSingleInstance().getBufferQueue().put(voMsg);
						break;
					default:
						break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else if (Message.Type.groupchat == message.getType() && incoming && processed) {
				// 群聊模式 @TODO
			}
		}
	}

	/**
	 * 校验消息，排除像正在输入、暂停输入之类的packet
	 * 
	 * @return
	 */
	private ChatMessageType valiedMessage(Message message) {
		ChatMessageType type = null;
		Element composingElement = message.getChildElement("composing", "http://jabber.org/protocol/chatstates");
		Element pausedElement = message.getChildElement("paused", "http://jabber.org/protocol/chatstates");
		Element inacticeElement = message.getChildElement("inactive", "http://jabber.org/protocol/chatstates");
		Element receivedElement = message.getChildElement("received", "urn:xmpp:receipts");
		Element goneElement = message.getChildElement("gone", "http://jabber.org/protocol/chatstates");
		Element readElement = message.getChildElement("read", "urn:xmpp:readreceipts");
		if (composingElement != null) {
			// 正在输入...
			type = ChatMessageType.COMPSING;
		} else if (pausedElement != null) {
			// 暂停输入...
			type = ChatMessageType.PAUSED;
		} else if (inacticeElement != null) {
			// 用户离线
			type = ChatMessageType.INACTIVE;
		} else if (receivedElement != null) {
			type = ChatMessageType.RECEIVED;
		} else if (goneElement != null) {
			type = ChatMessageType.GONE;
		} else if(readElement != null) {
			type = ChatMessageType.READ;
		} else {
			// 消息
			type = ChatMessageType.MESSAGE;
		}
		return type;
	}

	/**
	 * 从message对象构建一个传输使用的对象
	 * 
	 * @param message
	 * @return
	 */
	private ChatMessageVo buildChatMessageVoFromMessage(Message message) {
		String bareFromJid = message.getFrom().toBareJID();
		String bareToJid = message.getTo().toBareJID();
		String fullFromJid = message.getFrom().toString();
		String fullToJid = message.getTo().toString();
		String body = message.getBody();
		String xml = message.toXML();
		ChatMessageVo vo = new ChatMessageVo(fullFromJid, fullToJid, bareFromJid, bareToJid, body, xml);
		return vo;
	}

	private enum ChatMessageType {
		// 正在输入
		COMPSING,
		// 暂停输入
		PAUSED,
		// 用户离线
		INACTIVE,
		// 回执确认
		RECEIVED,
		// 已读
		READ,
		// 离开
		GONE,
		// 真实消息
		MESSAGE;
	}
}
