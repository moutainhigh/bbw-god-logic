package com.bbw.god.server;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 所有的区服数据集成该类
 *
 * @author suhq
 * @date 2018年11月28日 下午2:40:59
 */
@Getter
@Setter
@ToString
public abstract class ServerData {
    protected Integer sid;// 区服ID
    protected Long id;// 数据ID

    /**
     * 是否是循环数据
     *
     * @return
     */
    public boolean isLoopData() {
        return false;
    }

    public String getLoopKey() {
        return "";
    }

    /**
     * 资源类型的字符串
     *
     * @return
     */
    abstract public ServerDataType gainDataType();
}
