package com.bbw.god.gameuser.helpabout.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description: 客户端 菜单-帮助 按钮点击事件
 * @author: suchaobin
 * @createTime: 2019-11-21 13:44
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class EPReadMenuHelp extends BaseEventParam {

    public EPReadMenuHelp(BaseEventParam bep) {
        setValues(bep);
    }
}
