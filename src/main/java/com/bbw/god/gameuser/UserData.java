package com.bbw.god.gameuser;

import lombok.Data;

/**
 * 玩家数据
 * 所有User***的类为该类的子类
 *
 * @author suhq
 * @date 2018年10月26日 下午7:04:01
 */
@Data
public abstract class UserData {
    protected Long id;
    protected Long gameUserId;

    /**
     * 玩家资源数据ID
     *
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置玩家资源数据ID
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 返回区服玩家ID
     *
     * @return
     */
    public Long getGameUserId() {
        return gameUserId;
    }

    public void setGameUserId(Long gameUserId) {
        this.gameUserId = gameUserId;
    }

    /**
     * 玩家资源类型
     *
     * @return
     */
    abstract public UserDataType gainResType();
}
