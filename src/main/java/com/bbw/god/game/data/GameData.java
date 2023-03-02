package com.bbw.god.game.data;

import lombok.Getter;
import lombok.Setter;

/**
 * 所有的区服数据集成该类
 *
 * @author suhq
 * @date 2018年11月28日 下午2:40:59
 */
@Getter
@Setter
public abstract class GameData {
    protected Long id;// 数据ID

    /**
     * 资源类型的字符串
     *
     * @return
     */
    abstract public GameDataType gainDataType();
}
