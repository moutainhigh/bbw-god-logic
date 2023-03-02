package com.bbw.god.gameuser.task.main;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 任务奖励返回
 * 
 * @author suhq
 * @date 2019年3月12日 下午5:24:54
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDMainTaskAwarded extends RDCommon implements Serializable {
	private static final long serialVersionUID = 1L;
	private RDMainTask nextMainTask = null;// 下一个可领取的主线任务状态

}
