package com.bbw.god.game.config;

/**
 * <pre>
 * 基础实体数据继承该类，如区服、活动、卡牌、法宝、特产... 
 * 继承该类的数据类，可通过Cfg.I.get(Serializable id, Class<T> clazz)获取特定数据，无需遍历
 * </pre>
 * 
 * @author suhq
 * @date 2019-07-19 15:13:17
 */
public interface CfgEntityInterface extends CfgInterface {
}
