package com.bbw.validator.group;

import javax.validation.GroupSequence;

/**
 * 定义校验顺序，如果AddGroup组失败，则UpdateGroup组不会再校验
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-02 15:54
 */
@GroupSequence({ AddGroup.class, UpdateGroup.class })
public interface Group {

}
