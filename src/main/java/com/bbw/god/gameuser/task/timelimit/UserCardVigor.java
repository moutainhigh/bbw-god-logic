package com.bbw.god.gameuser.task.timelimit;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import com.bbw.god.gameuser.card.UserCard;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卡牌精力
 *
 * @author: suhq
 * @date: 2021/8/5 5:57 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserCardVigor extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 卡牌精力记录 卡牌ID:剩余精力值 */
    private Map<String, Integer> cardVigors = new HashMap<>();

    private Date lastUpdate = DateUtil.now();

    public static UserCardVigor getInstance(long uid) {
        UserCardVigor userCardVigor = new UserCardVigor();
        userCardVigor.setId(ID.INSTANCE.nextId());
        userCardVigor.setGameUserId(uid);
        return userCardVigor;
    }


    /**
     * 更新卡牌精力
     *
     * @param ucs
     * @param deductVigor
     */
    public void updateCardVigors(List<UserCard> ucs, int deductVigor) {
        for (UserCard uc : ucs) {
            String key = uc.getBaseId().toString();
            int maxCardVigor = TimeLimitTaskTool.getMaxCardVigor(uc.getBaseId(), uc.getHierarchy());
            Integer vigor = getCardVigors().getOrDefault(key, maxCardVigor);
            vigor -= deductVigor;
            cardVigors.put(uc.getBaseId().toString(), vigor);
        }
        lastUpdate = DateUtil.now();
    }

    /**
     * 添加卡牌，卡牌升阶用
     *
     * @param uc
     * @param addCardVigor
     * @return false 请求者无需持久化到数据库 true 需要
     */
    public boolean addCardVigor(UserCard uc, int addCardVigor) {
        String key = uc.getBaseId().toString();
        Integer vigor = getCardVigors().get(key);
        if (null == vigor) {
            return false;
        }
        vigor += addCardVigor;
        cardVigors.put(key, vigor);
        lastUpdate = DateUtil.now();
        return true;
    }

    /**
     * 精力数据重置
     */
    public void reset() {
        cardVigors = new HashMap<>();
        lastUpdate = DateUtil.now();
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_CARD_VIGOR;
    }

}
