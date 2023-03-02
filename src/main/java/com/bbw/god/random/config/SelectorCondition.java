package com.bbw.god.random.config;

import com.bbw.common.ListUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <pre>
 * 卡牌选择条件 condition
 * 以"$"开头的为动态参数，需要程序从外部传入。
 *  star:星级;1|2|3|4|5
 *  getWay:获取途径;0|1|2|3
 *  type:属性; -1代表不限制，金木水火土取值依次为 |10|20|30|40|50
 *  group:组合ID，-1代表不限制
 *  requestSize:要求的数量；
 *  include 白名单，配置卡牌名称，以英文逗号,分隔。没有配置就是允许所有；如果有配置，则只能从这个名单中获取。
 *  exclude 黑名单，配置卡牌名称，以英文逗号,分隔。
 *  include，exclude允许配置灵石，名称分别为 1星灵石|2星灵石|3星灵石|4星灵石|5星灵石
 *  </pre>
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-06 21:22
 */
@Data
public class SelectorCondition implements Serializable {
    private static final long serialVersionUID = 297019476577195962L;
    private int star = RandomKeys.NO_LIMIT;
    @Deprecated
    private List<Integer> getWay;//获取途径0|1|2|3
    private String type = RandomKeys.NO_LIMIT_STRING;//属性;允许传参。 -1代表不限制，金木水火土取值依次为 |10|20|30|40|50
    private int group = RandomKeys.NO_LIMIT;//组合ID，-1代表不限制
    private List<String> include;//白名单，配置卡牌名称，以英文逗号,分隔。没有配置就是允许所有；如果有配置，则只能从这个名单中获取。允许传参。
    private List<String> exclude;//黑名单，配置卡牌名称，以英文逗号,分隔。允许传参。

    /**
     * 需要设置属性参数
     *
     * @return
     */
    public boolean needTypeParam() {
        if (null == this.type) {
            return false;
        }
        return this.type.startsWith(RandomKeys.PARAM_PREFIX);
    }

    /**
     * 需要设置白名单
     *
     * @return
     */
    public boolean needIncludeParam() {
        if (ListUtil.isEmpty(this.include)) {
            return false;
        }
        return this.include.stream().anyMatch(tmp -> tmp.startsWith(RandomKeys.PARAM_PREFIX));
//		return include.get(0).startsWith(RandomKeys.PARAM_PREFIX);
    }

    /**
     * 需要设置黑名单
     *
     * @return
     */
    public boolean needExcludeParam() {
        if (ListUtil.isEmpty(this.exclude)) {
            return false;
        }
        return this.exclude.stream().anyMatch(tmp -> tmp.startsWith(RandomKeys.PARAM_PREFIX));
//		return exclude.get(0).startsWith(RandomKeys.PARAM_PREFIX);
    }
}
