package com.bbw.god.random.config;

import java.io.Serializable;

import lombok.Data;

/**保底n次内【至少】【至少】【至少】被抽中一次。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-06 21:29
 */
@Data
public class SelectorAtLeastRule implements Serializable {
	private static final long serialVersionUID = 297029076577195962L;
	//-------------------------
	private int times;//次数[1-n]，n次内【至少】被抽中一次。
	private transient int presentValue;//当前累积的概率值。运行时需要。

}
