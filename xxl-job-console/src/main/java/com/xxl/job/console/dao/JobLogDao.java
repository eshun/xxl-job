package com.xxl.job.console.dao;

import com.xxl.job.console.model.JobLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * job log
 * @author xuxueli 2016-1-12 18:03:06
 */
@Mapper
public interface JobLogDao {

	// exist jobId not use jobGroup, not exist use jobGroup
	public List<JobLog> pageList(@Param("offset") int offset,
                                 @Param("pageSize") int pageSize,
                                 @Param("actuatorId") long actuatorId,
                                 @Param("jobId") long jobId,
                                 @Param("triggerTimeStart") Date triggerTimeStart,
                                 @Param("triggerTimeEnd") Date triggerTimeEnd,
                                 @Param("logStatus") int logStatus);
	public int pageListCount(@Param("offset") int offset,
							 @Param("pageSize") int pageSize,
							 @Param("actuatorId") long actuatorId,
							 @Param("jobId") long jobId,
							 @Param("triggerTimeStart") Date triggerTimeStart,
							 @Param("triggerTimeEnd") Date triggerTimeEnd,
							 @Param("logStatus") int logStatus);
	
	public JobLog load(@Param("id") long id);

	public int save(JobLog jobLog);

	public int updateTriggerInfo(JobLog jobLog);

	public int updateHandleInfo(JobLog jobLog);
	
	public int delete(@Param("jobId") long jobId);

	public int triggerCountByHandleCode(@Param("handleCode") int handleCode);

	public List<Map<String, Object>> triggerCountByDay(@Param("from") Date from,
													   @Param("to") Date to);

	public int clearLog(@Param("actuatorId") long actuatorId,
						@Param("jobId") long jobId,
						@Param("clearBeforeTime") Date clearBeforeTime,
						@Param("clearBeforeNum") int clearBeforeNum);

	public List<Long> findFailJobLogIds(@Param("pageSize") int pageSize);

	public int updateAlarmStatus(@Param("logId") long logId,
								 @Param("oldAlarmStatus") int oldAlarmStatus,
								 @Param("newAlarmStatus") int newAlarmStatus);

}
