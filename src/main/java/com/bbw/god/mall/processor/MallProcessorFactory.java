package com.bbw.god.mall.processor;

import java.util.List;

import com.bbw.god.mall.store.AbstractStoreProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * 
 * @author suhq
 * @date 2018年12月6日 上午10:57:04
 */
@Service
public class MallProcessorFactory {
	@Autowired
	@Lazy
	private List<AbstractMallProcessor> mallProcessors;

	/**
	 * 根据商品类型获取物品服务实现对象
	 * 
	 * @param mallType
	 * @return
	 */
	public AbstractMallProcessor getMallProcessor(int mallType) {
		return mallProcessors.stream().filter(mp -> mp.isMatch(mallType)).findFirst().orElse(null);
	}

}
