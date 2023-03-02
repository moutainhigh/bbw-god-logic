package com.bbw.god.server.fst;

import com.bbw.common.DateUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-07-06
 */
@Data
public class FstVideoLog implements Serializable {
	private Long id;
	//是否是挑战者
	private boolean isAttack;
	//对手ID
	private Long oppo;
	//是否赢了
	private boolean win=false;
	//排名变动
	private Integer rank=0;
	private List<Log> logs=new ArrayList<>();
	private Date logDate;
	
	public static FstVideoLog getInstance(boolean isAttack,long oppo){
		FstVideoLog log=new FstVideoLog();
		log.setAttack(isAttack);
		log.setOppo(oppo);
		log.setLogDate(new Date());
		log.setId(DateUtil.toDateTimeLong());
		return log;
	}
	
	public void addLog(boolean isFirst,String url,boolean win){
		Log log=new Log();
		log.setFirst(isFirst);
		log.setUrl(url);
		log.setWin(win);
		logs.add(log);
		this.win=win;
		if (logs.size()>=2){
			this.win=logs.stream().filter(p->p.isWin()).count()>=2;
		}
	}
	
	public boolean ifDone(){
		if (logs.size()<2){
			return false;
		}else if (logs.size()==2){
			long count = logs.stream().filter(p -> p.isWin()).count();
			return count==0 || count==2;
		}
		return true;
	}
	
	public boolean ifValid(){
		if (logDate!=null){
			return DateUtil.millisecondsInterval(DateUtil.now(),DateUtil.addDays(logDate,2))<0;
		}
		return true;
	}
	@Data
	public static class Log implements Serializable{
		private boolean first;
		private String url;
		private boolean win;
	}
}
