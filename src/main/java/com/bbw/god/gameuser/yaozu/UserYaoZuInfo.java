package com.bbw.god.gameuser.yaozu;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.card.CardGroupWay;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 玩家妖族信息
 *  注：在玩家通关妖族后所有妖族信息会被删除
 *
 * @author fzj
 * @date 2021/9/6 14:06
 */
@Data
public class UserYaoZuInfo extends UserCfgObj implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer yaoZuId;
    /** 类别 100 野猪妖 200 狗大仙 300 琵琶精 400 稚鸡精 500 妖狐仙 */
    private Integer yaoZuType;
    /** 位置 */
    private Integer position;
    /** 进度 0表示未攻打镜像 1表示已经打赢镜像 2表示已经打败本体 */
    private Integer progress = 0;
    /** 镜像卡组 */
    private List<Integer> mirroringCards = new ArrayList<>();
    /** 镜像符册 */
    private Integer mirroringFuCe = 0;
    /** 本体卡组 */
    private List<Integer> ontologyCards = new ArrayList<>();
    /** 本体符册 */
    private Integer ontologyFuCe = 0;


    public static UserYaoZuInfo getInstance(long uid, int yaoZuId, int pos) {
        UserYaoZuInfo userYaoZuInfo = new UserYaoZuInfo();
        userYaoZuInfo.setId(ID.INSTANCE.nextId());
        userYaoZuInfo.setGameUserId(uid);
        userYaoZuInfo.setBaseId(yaoZuId);
        CfgYaoZuEntity cfgYaoZuEntity = YaoZuTool.getYaoZu(yaoZuId);
        userYaoZuInfo.setName(cfgYaoZuEntity.getName());
        userYaoZuInfo.setYaoZuType(cfgYaoZuEntity.getYaoZuType());
        userYaoZuInfo.setPosition(pos);
        return userYaoZuInfo;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_YAOZU_INFO;
    }

    /**
     * 根据类型判断卡组
     * @param type
     * @return
     */
    public List<Integer> gainAttackCardsByType(int type){
        if (type== YaoZuCardsEnum.MIRRORING_CARDS.getType()) {
           return getMirroringCards();
        }
        return getOntologyCards();
    }

    /**
     * 设置攻击卡组
     *
     * @param type
     * @param yaoZuInfo
     * @param cardIds
     * @return
     */
    public UserYaoZuInfo setAttackCard(int type, UserYaoZuInfo yaoZuInfo, List<Integer> cardIds) {
        if (YaoZuCardsEnum.MIRRORING_CARDS.getType() == type) {
            yaoZuInfo.setMirroringCards(cardIds);
        } else {
            yaoZuInfo.setOntologyCards(cardIds);
        }
        return yaoZuInfo;
    }

    /**
     * 设置战斗符册
     *
     * @param cardGroupWay
     * @param fuCeId
     */
    public void setAttackFuCe(CardGroupWay cardGroupWay, Integer fuCeId) {
        if (CardGroupWay.YAO_ZU_MIRRORING == cardGroupWay) {
            this.mirroringFuCe = fuCeId;
        } else {
            this.ontologyFuCe = fuCeId;
        }
    }

}
