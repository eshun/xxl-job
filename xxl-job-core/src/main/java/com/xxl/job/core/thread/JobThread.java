package com.xxl.job.core.thread;

import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.JobHandleParamInfo;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ReflectionUtil;
import com.xxl.job.core.util.ShardingUtil;
import com.xxl.job.core.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;


/**
 * handler thread
 * @author xuxueli 2016-1-16 19:52:47
 */
public class JobThread extends Thread {
	private static Logger logger = LoggerFactory.getLogger(JobThread.class);

	private long jobId;
	private IJobHandler handler;
	private LinkedBlockingQueue<TriggerParam> triggerQueue;
	private Set<Long> triggerLogIdSet;        // avoid repeat trigger for the same TRIGGER_LOG_ID

	private volatile boolean toStop = false;
	private String stopReason;

	private boolean running = false;    // if running job
	private int idleTimes = 0;            // idel times


	public JobThread(long jobId, IJobHandler handler) {
		this.jobId = jobId;
		this.handler = handler;
		this.triggerQueue = new LinkedBlockingQueue<TriggerParam>();
		this.triggerLogIdSet = Collections.synchronizedSet(new HashSet<Long>());
	}

	public IJobHandler getHandler() {
		return handler;
	}

	/**
	 * new trigger to queue
	 *
	 * @param triggerParam
	 * @return
	 */
	public ReturnT<String> pushTriggerQueue(TriggerParam triggerParam) {
		// avoid repeat
		if (triggerLogIdSet.contains(triggerParam.getLogId())) {
			logger.info(">>>>>>>>>>> repeate trigger job, logId:{}", triggerParam.getLogId());
			return new ReturnT<String>(ReturnT.FAIL_CODE, "repeate trigger job, logId:" + triggerParam.getLogId());
		}

		triggerLogIdSet.add(triggerParam.getLogId());
		triggerQueue.add(triggerParam);
		return ReturnT.SUCCESS;
	}

	/**
	 * kill job thread
	 *
	 * @param stopReason
	 */
	public void toStop(String stopReason) {
		/**
		 * Thread.interrupt只支持终止线程的阻塞状态(wait、join、sleep)，
		 * 在阻塞出抛出InterruptedException异常,但是并不会终止运行的线程本身；
		 * 所以需要注意，此处彻底销毁本线程，需要通过共享变量方式；
		 */
		this.toStop = true;
		this.stopReason = stopReason;
	}

	/**
	 * is running job
	 *
	 * @return
	 */
	public boolean isRunningOrHasQueue() {
		return running || triggerQueue.size() > 0;
	}

	@Override
	public void run() {

		// init
		try {
			handler.init();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}

		// execute
		while (!toStop) {
			running = false;
			idleTimes++;

			TriggerParam triggerParam = null;
			Object executeResult = null;
			try {
				// to check toStop signal, we need cycle, so wo cannot use queue.take(), instand of poll(timeout)
				triggerParam = triggerQueue.poll(3L, TimeUnit.SECONDS);
				if (triggerParam != null) {
					running = true;
					idleTimes = 0;
					triggerLogIdSet.remove(triggerParam.getLogId());

					// log filename, like "logPath/yyyy-MM-dd/9999.log"
					String logFileName = XxlJobFileAppender.makeLogFileName(new Date(triggerParam.getLogDateTim()), triggerParam.getLogId());
					XxlJobFileAppender.contextHolder.set(logFileName);
					ShardingUtil.setShardingVo(new ShardingUtil.ShardingVO(triggerParam.getBroadcastIndex(), triggerParam.getBroadcastTotal()));

					// execute
					XxlJobLogger.log("<br>----------- xxl-job job execute start -----------<br>----------- Param:" + triggerParam.getExecutorParams());

					if (triggerParam.getExecutorTimeout() > 0) {
						// limit timeout
						Thread futureThread = null;
						try {
							final TriggerParam triggerParamTmp = triggerParam;
							FutureTask<Object> futureTask = new FutureTask<Object>(new Callable<Object>() {
								@Override
								public Object call() throws Exception {
									return execute(handler, triggerParamTmp);
								}
							});
							futureThread = new Thread(futureTask);
							futureThread.start();

							executeResult = futureTask.get(triggerParam.getExecutorTimeout(), TimeUnit.SECONDS);
						} catch (TimeoutException e) {

							XxlJobLogger.log("<br>----------- xxl-job job execute timeout");
							XxlJobLogger.log(e);

							executeResult = ReturnT.failTimeout("job execute timeout ");
						} finally {
							futureThread.interrupt();
						}
					} else {
						// just execute
						executeResult = execute(handler, triggerParam);
					}

					if (executeResult == null) {
						executeResult = ReturnT.fail("");
					} else {
						ReturnT<Object> result = (ReturnT<Object>) executeResult;
						result.setMsg(
								(result != null && result.getMsg() != null && result.getMsg().length() > 50000)
										? result.getMsg().substring(0, 50000).concat("...")
										: result.getMsg());
						result.setContent(null);
						executeResult = result;
					}
					XxlJobLogger.log("<br>----------- xxl-job job execute end(finish) -----------<br>----------- ReturnT:" + executeResult);

				} else {
					if (idleTimes > 30) {
						XxlJobExecutor.removeJobThread(jobId, "excutor idel times over limit.");
					}
				}
			} catch (Throwable e) {
				if (toStop) {
					XxlJobLogger.log("<br>----------- JobThread toStop, stopReason:" + stopReason);
				}

				StringWriter stringWriter = new StringWriter();
				e.printStackTrace(new PrintWriter(stringWriter));
				String errorMsg = stringWriter.toString();
				executeResult = new ReturnT<String>(ReturnT.FAIL_CODE, errorMsg);

				XxlJobLogger.log("<br>----------- JobThread Exception:" + errorMsg + "<br>----------- xxl-job job execute end(error) -----------");
			} finally {
				if (triggerParam != null) {
					// callback handler info
					if (!toStop) {
						// commonm
						TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTim(), executeResult));
					} else {
						// is killed
						ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, stopReason + " [job running，killed]");
						TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTim(), stopResult));
					}
				}
			}
		}

		// callback trigger request in queue
		while (triggerQueue != null && triggerQueue.size() > 0) {
			TriggerParam triggerParam = triggerQueue.poll();
			if (triggerParam != null) {
				// is killed
				ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, stopReason + " [job not executed, in the job queue, killed.]");
				TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTim(), stopResult));
			}
		}

		// destroy
		try {
			handler.destroy();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}

		logger.info(">>>>>>>>>>> xxl-job JobThread stoped, hashCode:{}", Thread.currentThread());
	}

	public static Object execute(IJobHandler handler, TriggerParam triggerParam) {
		try {
			Class<?> c = handler.getClass();
			Method method = c.getMethod(triggerParam.getExecutorMethod());
			Class<?> clazz = method.getReturnType();
			Object retObject = null;

			if (clazz == void.class || clazz == Void.class) {
				method.invoke(handler);
			} else {
				Object args=getArgs(triggerParam.getExecutorParams());
				retObject = method.invoke(handler, args);
			}
			if (retObject == null) {
				return ReturnT.SUCCESS;
			} else {
				return new ReturnT(retObject);
			}
		} catch (NoSuchMethodException e) {
			return ReturnT.fail(e.getMessage());
		} catch (IllegalAccessException e) {
			return ReturnT.fail(e.getMessage());
		} catch (IllegalArgumentException e) {
			return ReturnT.fail(e.getMessage());
		} catch (InvocationTargetException e) {
			return ReturnT.fail(e.getMessage());
		}
	}

	private static Object getArgs(List<JobHandleParamInfo> jobHandleParamInfos) {
		if (jobHandleParamInfos != null) {
			if (jobHandleParamInfos.size() > 1) {
				Object[] objects = new Object[jobHandleParamInfos.size()];

				return objects;
			} else {
				Object object = getArg(jobHandleParamInfos.get(0));

				return object;
			}
		} else {
			return null;
		}
	}
	private static Object getArg(JobHandleParamInfo jobHandleParamInfo) {
		String className = jobHandleParamInfo.getClassName();
		String value = jobHandleParamInfo.getJobValue();

		if (VerifyUtil.isNullOrEmpty(className)) {
			return value;
		} else if ("string".equals(className.toLowerCase())) {
			return value;
		} else if ("boolean".equals(className.toLowerCase())) {
			return Boolean.valueOf(value);
		} else if ("int".equals(className.toLowerCase())) {
			return Integer.valueOf(value);
		} else if ("byte".equals(className.toLowerCase())) {
			return value.getBytes();
		} else if ("double".equals(className.toLowerCase())) {
			return Double.valueOf(value);
		} else if ("long".equals(className.toLowerCase())) {
			return Long.valueOf(value);
		} else if ("float".equals(className.toLowerCase())) {
			return Float.valueOf(value);
		} else if ("short".equals(className.toLowerCase())) {
			return Short.valueOf(value);
		} else if ("Array".equals(className.toLowerCase())) {
			List<JobHandleParamInfo> childs = jobHandleParamInfo.getChildren();
			if (childs != null) {
				int l = 0;
				Object[] objects = new Object[childs.size()];
				for (JobHandleParamInfo child : childs) {
					objects[l] = getArg(child);
					l++;
				}
				return objects;
			}
			return Long.valueOf(value);
			//}else if(){//Collection Map

		} else {
			try {
				Class<?> clazz = ReflectionUtil.loadClassByName(className);
				//ReflectionUtil.getBeanPublicSetterMethod()

			} catch (Exception e) {

			}
		}
		return value;
	}
}
