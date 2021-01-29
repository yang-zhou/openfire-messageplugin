package org.igniterealtime.openfire.messageplugin;

import java.io.Serializable;

public class ChatMessageVo implements Serializable{
	private static final long serialVersionUID = -1119357051430195384L;
	
	private String fullFromJid;
	private String fullToJid;
	private String bareFromJid;
	private String bareToJid;
	private String body;
	private String xml;
	
	public ChatMessageVo() {
		super();
	}
	
	public ChatMessageVo(String fullFromJid, String fullToJid, String bareFromJid, String bareToJid, String body, String xml) {
		super();
		this.fullFromJid = fullFromJid;
		this.fullToJid = fullToJid;
		this.bareFromJid = bareFromJid;
		this.bareToJid = bareToJid;
		this.body = body;
		this.xml = xml;
	}
	public String getFullFromJid() {
		return fullFromJid;
	}
	public void setFullFromJid(String fullFromJid) {
		this.fullFromJid = fullFromJid;
	}
	public String getFullToJid() {
		return fullToJid;
	}
	public void setFullToJid(String fullToJid) {
		this.fullToJid = fullToJid;
	}
	public String getBareFromJid() {
		return bareFromJid;
	}
	public void setBareFromJid(String bareFromJid) {
		this.bareFromJid = bareFromJid;
	}
	public String getBareToJid() {
		return bareToJid;
	}
	public void setBareToJid(String bareToJid) {
		this.bareToJid = bareToJid;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml;
	}
}
