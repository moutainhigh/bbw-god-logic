package com.bbw.god.gameuser.task.fshelper;

import com.bbw.god.gameuser.chamberofcommerce.CocTask;
import com.bbw.god.gameuser.task.RDTaskItem;
import com.bbw.god.rd.RDSuccess;
import com.bbw.god.server.guild.GuildTask;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/** 
* @author 作者 ：lwb
* @version 创建时间：2020年2月14日 下午5:31:24 
* 类说明 
*/
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDFsHelper extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<RDTaskItem> tasks = null;
	private CocTask cocTask = null;
	private GuildTask guildTask = null;
	private Integer cocTaskTimes = 0;
	private Integer guildTaskTimes = 0;
	private List<RDTaskItem> godTrainingTask = null;
	private List<RDTaskItem> businessGangTask = null;
	/** 商帮任务剩余领取次数 */
	private Integer remainAvailableNum;
}
