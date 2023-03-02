package com.bbw.god.game.config;

/**
 * <pre>
 * 有些实体数据经常需要分类读取，为了避免每次读取遍历分类，
 * 在第一次加载时或者yml修改时（重新）准备分类数据，如商城物品、任务、卡牌、法宝...
 * </pre>
 * 
 * @author suhq
 * @date 2019-07-29 11:38:40
 */
public interface CfgPrepareListInterface {
	public void prepare();
}
