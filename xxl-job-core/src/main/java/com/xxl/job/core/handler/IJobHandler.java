package com.xxl.job.core.handler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;

/**
 * job handler
 *
 * @author xuxueli 2015-12-19 19:06:38
 */
public abstract class IJobHandler {

	private TriggerParam triggerParam;

	/**
	 * 获取执行任务信息
	 * @author esun
	 * @return
	 */
	public TriggerParam getTriggerParam() {
		return triggerParam;
	}

	public void setTriggerParam(TriggerParam triggerParam) {
		this.triggerParam = triggerParam;
	}

	/** success */
	public static final ReturnT<String> SUCCESS = new ReturnT<String>(200, null);
	/** fail */
	public static final ReturnT<String> FAIL = new ReturnT<String>(500, null);
	/** fail timeout */
	public static final ReturnT<String> FAIL_TIMEOUT = new ReturnT<String>(502, null);


	/**
	 * execute handler, invoked when executor receives a scheduling request
	 *
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public abstract ReturnT<String> execute(String param) throws Exception;


	/**
	 * init handler, invoked when JobThread init
	 */
	public void init() {
		// do something
	}


	/**
	 * destroy handler, invoked when JobThread destroy
	 */
	public void destroy() {
		// do something
	}


}
