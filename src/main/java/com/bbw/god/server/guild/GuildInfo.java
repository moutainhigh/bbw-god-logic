package com.bbw.god.server.guild;

import com.bbw.god.game.config.CfgGuild.Level;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 行会信息
 *
 * @author lwb
 * @version 1.0
 * @date 2019年5月14日
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GuildInfo extends ServerData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String guildName;//行会名称
    private String bossName;//会长昵称
    private Long bossId;//会长ID
    private Long viceBossId;//副会长Id
    private Integer lv = 1;//等级
    private Long leaveDay = 0L;//会长离开天数
    private Integer limitPeople = 3;//人数上限
    private Integer peopleProgress = 1;//现有人数
    private Integer status = 0;//状态-1 已满 0 可申请 1 已申请
    private Integer examineNum = 0;//待审核人数
    private String expProgres = null;
    private Integer exp = 0;//当前经验 去除了起始经验后的
    private Integer baseExp = 0;//当前等级起始经验
    private Integer targetExp = 190;//升级所需经验
    private Integer maxBox = 3;//初始为3
    private List<Integer> eightDiagrams = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0));
    private Integer eightDiagramsBuildDate;//八卦任务生成时间
    private Integer maxJoinNum = GuildConstant.MAX_JOIN;//每日最大加入人数
    private Integer limitJoinDate;//上限重置时间
    private List<Long> examineUids = new ArrayList<Long>();//待审核玩家 Id
    private List<Long> members = new ArrayList<Long>();
    private List<GuildWords> words = new ArrayList<>();
    private Date LastPushHelpTime;

    @Data
    public static class GuildWords {
        private Long uid;
        private String sender;
        private String content;
        private Date writeDate = new Date();
        private Integer status = 1;//0 正常 1 新留言
    }

    public boolean addExpUpLevel(int addExp) {
        exp += addExp;
        boolean upLevel = false;
        if ((exp + baseExp) >= targetExp) {
            // 升级
            lv++;
            Level level = GuildTools.getLevel(lv);
            exp = exp + baseExp - level.getBaseExp();
            limitPeople = level.getLimitPeople();
            targetExp = level.getTargetExp();
            baseExp = level.getBaseExp();
            maxBox = level.getOpenBoxTimes();
            status = limitPeople > peopleProgress ? GuildConstant.MEMBER_CAN_JOIN : GuildConstant.MEMBER_FILL;
            upLevel = true;
        }
        return upLevel;
    }

    @Override
    public ServerDataType gainDataType() {

        return ServerDataType.Guild_Info;
    }

}
