package com.bbw.god.gm;

import com.bbw.common.DateUtil;
import com.bbw.common.Rst;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.activityrank.server.ServerActivityRank;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.CfgChannelEntity;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.service.CfgChannelService;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.server.ServerService;
import com.bbw.god.server.ServerUserService;
import com.bbw.god.server.flx.FlxService;
import com.bbw.god.server.maou.bossmaou.ServerBossMaouService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 补偿相关接口
 *
 * @author suhq
 * @date 2019年4月12日 上午11:55:43
 */
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMMakeUpCtrl extends AbstractController {
    @Autowired
    private ServerService serverService;
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private CfgChannelService cfgChannelService;
    @Autowired
    private MailService mailService;
    @Autowired
    private ActivityRankService activityRankService;
    @Autowired
    private FlxService flxService;
    @Autowired
    private ServerBossMaouService serverBossMaouService;

    /**
     * 区服组补偿
     *
     * @param serverGroup
     * @return
     */
    @GetMapping("server!makeUpServerGroup")
    public Rst makeUpServerGroup(int serverGroup, @Valid CPMail mail) {
        List<CfgServerEntity> groupServers = ServerTool.getGroupServers(serverGroup);
        for (CfgServerEntity server : groupServers) {
            Set<Long> uids = this.serverUserService.getUidsInDays(server.getMergeSid(), 10);
            this.mailService.sendAwardMail(mail.getTitle(), mail.getContent(), uids, mail.getAwards());
            log.info("{}补偿完毕，补偿内容：{}", LogUtil.getLogServerPart(server), mail.toString());
        }
        return Rst.businessOK();
    }

    /**
     * 区服补偿
     *
     * @param serverName
     * @return
     */
    @GetMapping("server!makeUpServer")
    public Rst makeUpServer(String serverName, @Valid CPMail mail) {
        CfgServerEntity server = ServerTool.getServer(serverName);
        Set<Long> uids = this.serverUserService.getUidsInDays(server.getMergeSid(), 30);
        this.mailService.sendAwardMail(mail.getTitle(), mail.getContent(), uids, mail.getAwards());
        log.info("{}补偿完毕，补偿内容：{}", LogUtil.getLogServerPart(server), mail.toString());
        return Rst.businessOK();
    }

    /**
     * 平台补偿
     *
     * @param platCode
     * @param mail
     * @return
     */
    @GetMapping("server!makeUpChannel")
    public Rst makeUpChannel(String platCode, @Valid CPMail mail) {
        Optional<CfgChannelEntity> channel = this.cfgChannelService.getByPlatCode(platCode);
        if (!channel.isPresent()) {
            return Rst.businessFAIL("无效的渠道");
        }

        List<CfgServerEntity> groupServers = ServerTool.getGroupServers(channel.get().getServerGroup());
        for (CfgServerEntity server : groupServers) {
            Set<Long> uids = this.serverUserService.getUidsInDays(server.getMergeSid(), channel.get().getId(), 30);
            this.mailService.sendAwardMail(mail.getTitle(), mail.getContent(), uids, mail.getAwards());
            log.info("{}{}补偿完毕，补偿内容：{}", LogUtil.getLogServerPart(server), channel.get().getName(), mail.toString());
        }
        return Rst.businessOK();
    }

    /**
     * 玩家补偿
     *
     * @param serverName
     * @param nickname
     * @param mail
     * @return
     */
    @GetMapping("server!makeUpUser")
    public Rst makeUpUser(String serverName, String nickname, @Valid CPMail mail) {
        CfgServerEntity server = ServerTool.getServer(serverName);
        Optional<Long> uid = this.serverUserService.getUidByNickName(server.getMergeSid(), nickname);
        if (!uid.isPresent()) {
            return Rst.businessFAIL("该账号不存在");
        }
        this.mailService.sendAwardMail(mail.getTitle(), mail.getContent(), uid.get(), mail.getAwards());
        log.info("{}{}补偿完毕，补偿内容：{}", LogUtil.getLogServerPart(server), nickname, mail.toString());
        return Rst.businessOK();
    }

    /**
     * 冲榜奖励补发
     *
     * @param serverName
     * @param rankType
     * @param week
     * @return
     */
    @GetMapping("server!makeUpActivityRank")
    public Rst makeUpActivityRank(String serverName, int rankType, int week) {
        CfgServerEntity server = ServerTool.getServer(serverName);
        List<ServerActivityRank> sars = this.serverService.getServerDatas(server.getMergeSid(), ServerActivityRank.class);
        Optional<ServerActivityRank> sar = sars.stream().filter(item -> item.getType() == rankType && item.getOpenWeek() == week).findFirst();
        if (!sar.isPresent()) {
            return Rst.businessFAIL("无效的榜单");
        }
        this.activityRankService.sendRankerAwardsByType(sar.get());
        return Rst.businessOK();
    }

    /**
     * 冲榜奖励补发
     *
     * @param serverNames
     * @param endDate
     * @return
     */
    @GetMapping("server!makeUpActivityRankByEndDate")
    public Rst makeUpActivityRankByEndDate(String serverNames, int endDate) {
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        List<Integer> sidList = servers.stream().map(CfgServerEntity::getMergeSid).distinct().collect(Collectors.toList());
        servers = sidList.stream().map(ServerTool::getServer).collect(Collectors.toList());
        for (CfgServerEntity server : servers) {
            List<ServerActivityRank> sars = this.serverService.getServerDatas(server.getMergeSid(), ServerActivityRank.class);
            Optional<ServerActivityRank> sar = sars.stream().filter(item -> DateUtil.toDateInt(item.gainEnd()) == endDate).findFirst();
            if (!sar.isPresent()) {
                continue;
            }
            this.activityRankService.sendRankerAwardsByType(sar.get());
        }
        return Rst.businessOK();
    }

    @RequestMapping("server!sendFlxMailAward")
    public Rst makeUpFlxMailAward(int dateInt) {
        log.info("开始发送{}日福临轩补偿", dateInt);
        this.flxService.sendFlxMailAward(dateInt);
        Rst rst = Rst.businessOK();
        rst.put("日期", String.valueOf(dateInt));
        rst.put("结果", "福临轩奖励补发完毕");
        log.info("福临轩{}补偿发送完毕", dateInt);
        return rst;
    }

    @RequestMapping("server!sendMaouAwardJob")
    public Rst sendMaouAwardJob(String serverNames, String makeupDate) {
        Date date = DateUtil.fromDateTimeString(makeupDate);
        int dateInt = DateUtil.toDateInt(date);
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        String successServers = "";
        String failServers = "";
        for (CfgServerEntity server : servers) {
            log.info("开始发送[{}]日[{}]魔王补偿", dateInt, server.getName());
            try {
                this.serverBossMaouService.sendMaouAwards(date, server);
                successServers += server.getName() + ";";
                log.info("[{}]日[{}]魔王补偿发送完毕！", dateInt, server.getName());
            } catch (Exception e) {
                e.printStackTrace();
                failServers += server.getName() + ";";
            }
        }
        Rst rst = Rst.businessOK();
        rst.put("日期", String.valueOf(dateInt));
        rst.put("发放成功", successServers);
        rst.put("发放失败", failServers);
        return rst;
    }

}
