package com.bbw.god.gameuser.task;

import com.bbw.god.rd.item.RDItems;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取任务列表（每日、主线）
 *
 * @author suhq
 * @date 2019年3月12日 下午5:24:54
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDTaskList extends RDItems<RDTaskItem> implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long countdownTimes = null;//倒计时
	private Integer unlock = null;
	private Integer curDays = null;

}
