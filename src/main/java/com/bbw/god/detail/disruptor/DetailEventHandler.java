package com.bbw.god.detail.disruptor;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.db.entity.InsUserDetailEntity;
import com.bbw.god.db.pool.DetailDataDAO;
import com.bbw.god.detail.DetailData;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;

/**
 * 明细处理器
 *
 * @author suhq
 * @date 2019年3月13日 下午3:06:15
 */
@Slf4j
public final class DetailEventHandler {
	private static int ringBufferSize = 1024 * 8;// 环形队列长度，必须是2的N次方，缓冲1W条
	private static DetailEventHandler hander = new DetailEventHandler();
	private RingBuffer<DetailEvent> ringBuffer;

	final EventHandler<DetailEvent> linkHandler = new EventHandler<DetailEvent>() {
		// 事件包装后最后将会有disruptor回收
		@Override
		public void onEvent(final DetailEvent event, final long sequence, final boolean endOfBatch) throws Exception {
			InsUserDetailEntity insEntity = null;
			try {
				DetailData detailData = event.getDetailData();
				// log.info(detailData.toString());
				insEntity = InsUserDetailEntity.fromDetailData(detailData);
				DetailDataDAO pdd = SpringContextUtil.getBean(DetailDataDAO.class, insEntity.getSid());
				pdd.dbInsertInsUserDetailEntity(insEntity);
			} catch (Exception e) {
				if (null == insEntity) {
					log.error("明细数据保存失败！\n" + event.getDetailData());
				} else {
					log.error("明细数据保存失败！\n" + event.getDetailData() + "\n" + insEntity);
				}
				log.error(e.getMessage(), e);
			}
		}
	};

	private DetailEventHandler() {
		// 定义Disruptor，基于单生产者，阻塞策略
		Disruptor<DetailEvent> disruptor = new Disruptor<DetailEvent>(DetailEvent.EVENT_FACTORY, ringBufferSize, Executors.defaultThreadFactory(), ProducerType.SINGLE, new BlockingWaitStrategy());

		disruptor.handleEventsWith(linkHandler);
		ringBuffer = disruptor.start();
	}

	public static DetailEventHandler getInstance() {
		return hander;
	}

	/**
	 * 记录明细
	 * 
	 * @param detailData
	 */
	public void log(DetailData detailData) {
		long seq = ringBuffer.next();
		try {
			DetailEvent event = ringBuffer.get(seq);
			event.setDetailData(detailData);
		} finally {
			ringBuffer.publish(seq);
		}
	}

}
