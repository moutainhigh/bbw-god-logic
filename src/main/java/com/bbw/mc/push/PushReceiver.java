package com.bbw.mc.push;

import com.bbw.mc.Msg;
import com.bbw.mc.MsgType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 推送消息
 * 
 * @author suhq
 * @date 2020-02-27 21:02:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PushReceiver {
	private String token;
	private Integer cId;
}
