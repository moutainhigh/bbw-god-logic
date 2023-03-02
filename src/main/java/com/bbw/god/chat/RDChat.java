package com.bbw.god.chat;

import java.io.Serializable;

import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;

import lombok.Data;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年11月21日 上午10:46:52 
* 类说明 
*/
@Data
public class RDChat extends RDSuccess implements Serializable{
	private static final long serialVersionUID = 1L;
	public String txt;//检验完后的文本
	
	public static RDChat returnTxt(String txt) {
		RDChat rdChat=new RDChat();
		rdChat.setTxt(txt);
		return rdChat;
	}
}
