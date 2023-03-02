package com.bbw.god.gameuser.special;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 玩家特产出售记录
 *
 * @author suchaobin
 * @date 2020/02/24 15:31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserSpecialSaleRecord extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 6542632013023998293L;
    /**
     * 已经卖过的特产种类id集合
     */
    private List<Integer> saledSpecialList = new ArrayList<>();
    private List<Integer> lastSaleSpecialList = new ArrayList<>();
    private List<Integer> lastBuySpecialList = new ArrayList<>();
    private Integer lastSaleCityId;
    private Integer lastBuyCityId;
    private long lastSaleTime;
    private long lastBuyTime;

    public void updateSaleRecord(Integer lastSaleCityId, List<Integer> lastSaleSpecialList) {
        this.lastSaleCityId = lastSaleCityId;
        this.lastSaleSpecialList = lastSaleSpecialList;
        this.lastSaleTime = DateUtil.toDateTimeLong();
    }

    public void updateBuyRecord(Integer lastBuyCityId, List<Integer> lastBuySpecialList) {
        this.lastBuyCityId = lastBuyCityId;
        this.lastBuySpecialList = lastBuySpecialList;
        this.lastBuyTime = DateUtil.toDateTimeLong();
    }

    public static UserSpecialSaleRecord instanceSaleRecord(long uid, List<Integer> lastSaleSpecialList, Integer lastSaleCityId) {
        UserSpecialSaleRecord uRecord = new UserSpecialSaleRecord();
        uRecord.setGameUserId(uid);
        uRecord.setId(ID.INSTANCE.nextId());
        uRecord.setLastSaleTime(DateUtil.toDateTimeLong());
        uRecord.setLastSaleSpecialList(lastSaleSpecialList);
        uRecord.setLastSaleCityId(lastSaleCityId);
        return uRecord;
    }

    public static UserSpecialSaleRecord instanceBoughtRecord(long uid, List<Integer> lastBuySpecialList, Integer lastBuyCityId) {
        UserSpecialSaleRecord uRecord = new UserSpecialSaleRecord();
        uRecord.setGameUserId(uid);
        uRecord.setId(ID.INSTANCE.nextId());
        uRecord.setLastBuyTime(DateUtil.toDateTimeLong());
        uRecord.setLastBuyCityId(lastBuyCityId);
        uRecord.setLastBuySpecialList(lastBuySpecialList);
        return uRecord;
    }

    public boolean isNewSpecial(Integer specialId) {
        return !this.saledSpecialList.contains(specialId);
    }

    public void addSpecial(Integer specialId) {
        this.saledSpecialList.add(specialId);
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.SPECIAL_SALE_RECORD;
    }
}
