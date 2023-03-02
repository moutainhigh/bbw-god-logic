package com.bbw.god.mall;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 商城刷新记录
 *
 * @author suhq
 * @date 2018年12月14日 上午9:29:30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserMallRefreshRecord extends UserData implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer mysteriousRefreshTimes;// 神秘刷新次数
    private Date dateTime;// 记录生成时间

    public static UserMallRefreshRecord instance(Long guId) {
        UserMallRefreshRecord umr = new UserMallRefreshRecord();
        umr.setId(ID.INSTANCE.nextId());
        umr.setGameUserId(guId);
        umr.setMysteriousRefreshTimes(1);
        umr.setDateTime(DateUtil.now());
        return umr;
    }

    public void addMysteriousRefreshTimes() {
        this.mysteriousRefreshTimes++;
    }

    /**
     * 该记录是否仍然有效
     *
     * @return
     */
    public boolean ifToday() {
        if (DateUtil.isToday(this.dateTime)) {
            return true;
        }
        return false;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.MALL_SM_REFRESH;
    }
}
