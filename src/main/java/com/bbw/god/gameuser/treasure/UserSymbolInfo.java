package com.bbw.god.gameuser.treasure;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author suchaobin
 * @description 玩家符箓数据
 * @date 2020/2/21 14:17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserSymbolInfo extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 已获得的符箓id集合 */
    private List<Integer> awardedSymbolList = new ArrayList<>();

    public UserSymbolInfo(Long uid) {
        this.id = ID.INSTANCE.nextId();
        this.gameUserId = uid;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.SYMBOL_INFO;
    }
}
