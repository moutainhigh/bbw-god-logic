package com.bbw.god.server.monster;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.common.JSONUtil;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.fight.FightResultUtil;
import com.bbw.god.fight.OpponentCardsUtil;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgMonster;
import com.bbw.god.game.config.card.CfgCard;
import com.bbw.god.game.config.city.YgTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 野怪未胜利的怪物
 *
 * @author suhq
 * @date 2018年11月7日 下午4:18:29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ServerMonster extends ServerData {
    private Long guId;
    private String finderName;
    private Boolean beDefeated;
    private Date eacapeTime;
    private String soliders = "";
    private Integer blood;

    private Integer property;
    private String joiners = "";
    private Long beLong = 0L;
    private Boolean joinYouGuai = true;// 加入到友怪
    private String monsterName = null;// 野怪名字
    private Integer headIcon = TreasureEnum.HEAD_ICON_Normal.getValue();// 头像框
    private Integer Head = null;// 头像
    private YeGuaiEnum yeGuaiEnum = null;

    public static ServerMonster fromGu(GameUser gu, RDFightsInfo fightsInfo, int remainBlood, int ygType) {
        ServerMonster sMonster = new ServerMonster();
        sMonster.setId(ID.INSTANCE.nextId());
        sMonster.setEacapeTime(DateUtil.addHours(DateUtil.now(), 2));
        sMonster.setGuId(gu.getId());
        sMonster.setSid(gu.getServerId());
        sMonster.setSoliders(JSONUtil.toJson(fightsInfo));
        sMonster.setBeDefeated(false);
        sMonster.setBlood(remainBlood);
        sMonster.setProperty(ygType);
        sMonster.setFinderName(gu.getRoleInfo().getNickname());
        sMonster.setHeadIcon(fightsInfo.getHeadIcon());
        sMonster.setMonsterName(fightsInfo.getNickname());
        sMonster.setHead(fightsInfo.getHead());
        return sMonster;
    }

    public static ServerMonster fromGuForNewerGuide(long guId, int guCountry, GameUser finder) {
        int ygLevel = Cfg.I.getUniqueConfig(CfgMonster.class).getMonsterDefaultLevel();
        String ygCards = YgTool.getYGCards(ygLevel).getCards();
        List<UserCard> fightCards = OpponentCardsUtil.getOpponentCardsForMonster(ygCards, guCountry, CfgCard.AI_CARDS_NOT_TO_FSDL_1);
        RDFightsInfo oppInfo = new RDFightsInfo(ygLevel, fightCards);
        int ygType = guCountry;
        int remainBlood = FightResultUtil.getBloodByLevel(ygLevel);
        ServerMonster sMonster = new ServerMonster();
        sMonster.setId(ID.INSTANCE.nextId());
        sMonster.setEacapeTime(DateUtil.addHours(DateUtil.now(), 2));
        sMonster.setGuId(finder.getId());
        sMonster.setSid(finder.getServerId());
        sMonster.setSoliders(JSONUtil.toJson(oppInfo));
        sMonster.setBeDefeated(false);
        sMonster.setBlood(remainBlood);
        sMonster.setProperty(ygType);
        sMonster.setFinderName(finder.getRoleInfo().getNickname());
        sMonster.setBeLong(guId);
        return sMonster;
    }

    public void deductBlood(int deductBlood) {
        this.blood -= deductBlood;
    }

    public void updateJoiners(long guId) {
        this.joiners += (guId + ",");
    }

    /**
     * 友怪是否无效
     *
     * @return
     */
    public boolean ifUnvalid() {
        if (beDefeated) {// 被打败
            return true;
        }
        if (blood.intValue() == 0) {// 血量为0
            return true;
        }
        if (eacapeTime.before(DateUtil.now())) {// 逃跑时间早于现在
            return true;
        }
        return false;
    }

    @Override
    public ServerDataType gainDataType() {
        return ServerDataType.MONSTER;
    }
}
