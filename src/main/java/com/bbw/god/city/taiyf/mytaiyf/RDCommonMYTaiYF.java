package com.bbw.god.city.taiyf.mytaiyf;

import com.bbw.god.rd.RDCommon;
import lombok.Data;

/**
 * 梦魇太一府捐献
 *
 * @author lzc
 * @date 2021-03-19 09:01:58
 */
@Data
public class RDCommonMYTaiYF extends RDCommon {
    public Integer isConvert = 0;// 是否可兑换 0:不能兑换，1：可兑换
    public Integer convertLevel = 0;// 可兑换等级
}
