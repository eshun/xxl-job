package com.xxl.job.console.dao;

import com.xxl.job.console.model.JobInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * job info
 * @author xuxueli 2016-1-12 18:03:45
 */
@Mapper
public interface JobInfoDao {

	public List<JobInfo> pageList(@Param("offset") int offset,
                                  @Param("pageSize") int pageSize,
                                  @Param("actuatorId") long actuatorId,
                                  @Param("triggerStatus") int triggerStatus,
                                  @Param("jobDesc") String jobDesc,
                                  @Param("author") String author);
	public int pageListCount(@Param("offset") int offset,
							 @Param("pageSize") int pageSize,
							 @Param("actuatorId") long actuatorId,
							 @Param("triggerStatus") int triggerStatus,
							 @Param("jobDesc") String jobDesc,
							 @Param("author") String author);
	
	public int insert(JobInfo info);

	public JobInfo loadById(@Param("id") long id);
	
	public int update(JobInfo jobInfo);
	
	public int delete(@Param("id") long id);

	public List<JobInfo> getJobsByActuator(@Param("actuatorId") long actuatorId);

	public int findAllCount();

	public List<JobInfo> scheduleJobQuery(@Param("maxNextTime") long maxNextTime);

	public int scheduleUpdate(JobInfo jobInfo);


}
