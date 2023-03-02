package com.bbw.god.server.guild;

import com.bbw.common.Rst;
import com.bbw.common.SensitiveWordUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import com.bbw.god.server.guild.service.GuildEightDiagramsTaskService;
import com.bbw.god.server.guild.service.GuildInfoService;
import com.bbw.god.server.guild.service.GuildShopService;
import com.bbw.god.server.guild.service.GuildUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 行会入口
* @author lwb  
* @date 2019年5月14日  
* @version 1.0  
*/
@RestController
public class GuildCtrl extends AbstractController{
	@Autowired
	private GuildInfoService guildInfoService;
	@Autowired
	private GuildShopService guildShopService;
	@Autowired
	private GuildEightDiagramsTaskService guildEightDiagramsTaskService;
	@Autowired
	private GuildUserService guildUserService;
	
	/**
	 * 检验玩家是否已加入行会
	 * 
	 * @return
	 */
	@GetMapping(CR.Guild.GUILD_HAVE_GUILD)
	public RDSuccess haveUserGuild() {
		RDCommon rd=new RDCommon();
		if (!guildUserService.hasGuild(getUserId())) {
			throw new ExceptionForClientTip("guild.has.join");
		}
		return new RDSuccess();
	}

	/**
	 * 获取行会列表
	 * 
	 * @param page     当前页
	 * @param pageSize 页面大小
	 * @param key      名称关键字检索
	 * @return
	 */
	@GetMapping(CR.Guild.GUILD_LIST)
	public GuildRD list(Integer page,Integer pageSize,String key) {
		page= null==page?1:page;
		pageSize= null==pageSize?10:pageSize;
		return guildInfoService.list(getUserId(),getServerId(), page, pageSize, key);
	}

	/**
	 * 行会创建
	 * 
	 * @param name
	 * @return
	 */
	@GetMapping(CR.Guild.GUILD_CREATE)
	public GuildRD create(String name) {
		name = name.trim();
		if (name.length() > GuildConstant.NAME_LENGTH) {
			throw new ExceptionForClientTip("guild.content.toolong");
		}
		LoginPlayer user = getUser();
		if (SensitiveWordUtil.isNotPass(name, user.getChannelId(), user.getOpenId())) {
			throw new ExceptionForClientTip("guild.fail.name");
		}
		guildInfoService.create(getUserId(), name);
		return new GuildRD();
	}
	
	@GetMapping(CR.Guild.GUILD_JOIN)
	public GuildRD join(Long id) {
		return guildInfoService.join(getUserId(), getServerId(), id);
	}
	
	@GetMapping(CR.Guild.GUILD_INFO)
	public GuildRD info() {
		return guildInfoService.info(getUserId(), getServerId());
	}

	/**
	 * 改名
	 * 
	 * @param name
	 * @return
	 */
	@GetMapping(CR.Guild.GUILD_RENAME)
	public GuildRD rename(String name) {
		if (name.length() > GuildConstant.NAME_LENGTH) {
			throw new ExceptionForClientTip("guild.content.toolong");
		}
		LoginPlayer user = getUser();
		if (SensitiveWordUtil.isNotPass(name, user.getChannelId(), user.getOpenId())) {
			throw new ExceptionForClientTip("guild.fail.name");
		}
		return guildInfoService.rename(getUserId(), getServerId(), name);
	}
	
	/**
	 * 弹劾队长
	 * 
	 * @return
	 */
	@GetMapping(CR.Guild.GUILD_IMPEACH_BOS)
	public GuildRD impeachBoss() {
		return guildInfoService.impeach(getUserId(), getServerId());
	}
	
	@GetMapping(CR.Guild.GUILD_LIST_EXAMIE)
	public GuildRD listExamie() {
		return guildInfoService.listExamine(getUserId(), getServerId());
	}

	@GetMapping(CR.Guild.GUILD_MEMBER_OPTION)
	public GuildRD memberOption(Integer option, Long examineid) {
		switch (GuidMemberEnum.fromVal(option)) {
			case DISMISS:
				//踢出成员
				guildInfoService.expulsion(getUserId(), getServerId(), examineid);
				break;
			case TRANSFER:
				//转让队长
				guildInfoService.transfer(getUserId(), getServerId(), examineid);
				break;
			case ACCEPT:
				//审核成员
				guildInfoService.memberAccept(getUserId(),getServerId(), examineid);
				break;
			case SET_VICEBOSS:
				//设置副队
				guildInfoService.setViceBossId(getUserId(), examineid, getServerId());
				break;
			case DEMOTION:
				//降级为会员
				guildInfoService.setNormalMember(getUserId(), examineid, getServerId());
			case REFUSE:
				//拒绝申请
				guildInfoService.memberRefuse(getUserId(),getServerId(), examineid);
				break;
			default :
		}
		return new GuildRD();
	}
	
	@GetMapping(CR.Guild.GUILD_WRITE_WORDS)
	public GuildRD wirteWords(String content){
		LoginPlayer user = getUser();
		if (SensitiveWordUtil.isNotPass(content, user.getChannelId(), user.getOpenId())) {
			throw new ExceptionForClientTip("guild.fail.name");
		}
		guildInfoService.writeWord(getGameUser(), content);
		return new GuildRD();
	}
	
	@GetMapping(CR.Guild.GUILD_EXIT)
	public GuildRD exit() {
		guildInfoService.exit(getUserId(), getServerId(),true);
		return new GuildRD();
	}
	@GetMapping(CR.Guild.GUILD_READ_WORDS)
	public GuildRD readWords(Long wordId) {
		return guildInfoService.readWords(getUserId(), getServerId(), wordId);
	}

	@GetMapping(CR.Guild.GUILD_LIST_MEMBER)
	public GuildRD listMember() {
		return guildInfoService.listMember(getGameUser());
	}
	
	@GetMapping(CR.Guild.GUILD_ED_INFO_TASK)
	public GuildRD EdInfoTask() {
		return guildEightDiagramsTaskService.list(getServerId(),getUserId());
	}
	
	@GetMapping(CR.Guild.GUILD_ED_OPTION_TASK)
	public GuildRD EdOptionTask(int taskId,int option) {
		if (taskId==0) {
			return guildEightDiagramsTaskService.opendBox(getUserId(), getServerId());
		}
		return guildEightDiagramsTaskService.taskOption(getUserId(), taskId, option);
	}

	@GetMapping(CR.Guild.GUILD_ED_HELP_TASK)
	public Rst EdHelpTask() {
		 guildEightDiagramsTaskService.TaskHelp(getUserId());
		 return Rst.businessOK();
	}
	
	@GetMapping(CR.Guild.GUILD_ED_REFREASH_TASK)
	public GuildRD EdRefreshTask() {
		return guildEightDiagramsTaskService.taskRefresh(getUserId());
	}
	
	@GetMapping(CR.Guild.GUILD_LIST_SHOP)
	public GuildRD listShop() {
		return guildShopService.list(getUserId());
	}
	
	@GetMapping(CR.Guild.GUILD_BUY_SHOP)
	public RDCommon buyShop(Integer propId) {
		return guildShopService.buyProduce(getUserId(), propId);
	}
	
	@GetMapping(CR.Guild.GUILD_ACCEPTED_TASK_INFO)
	public GuildRD getAcceptedTaskInfo() {
		return guildEightDiagramsTaskService.getAcceptedTaskInfo(getUserId());
	}
}
