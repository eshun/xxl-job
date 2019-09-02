package com.xxl.job.console.dao;

import com.xxl.job.console.model.JobInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JobInfoDaoTest {
	
	@Resource
	private JobInfoDao jobInfoDao;
	
	@Test
	public void pageList(){
		List<JobInfo> list = jobInfoDao.pageList(0, 20, 0, -1, null, null);
		int list_count = jobInfoDao.pageListCount(0, 20, 0, -1, null, null);
		
		System.out.println(list);
		System.out.println(list_count);

		List<JobInfo> list2 = jobInfoDao.getJobsByActuator(1);
	}
	
	@Test
	public void save_load(){
		JobInfo info = new JobInfo();
		info.setActuatorId(1);
		info.setJobCron("jobCron");
		info.setJobDesc("desc");
		info.setAuthor("setAuthor");
		info.setAlarmEmail("setAlarmEmail");
		info.setExecutorParam("setExecutorParam");
		info.setExecutorBlockStrategy("setExecutorBlockStrategy");
		info.setChildJobId("1");

		int count = jobInfoDao.save(info);

		JobInfo info2 = jobInfoDao.loadById(info.getId());
		info2.setJobCron("jobCron2");
		info2.setJobDesc("desc2");
		info2.setAuthor("setAuthor2");
		info2.setAlarmEmail("setAlarmEmail2");
		info2.setExecutorParam("setExecutorParam2");
		info2.setExecutorBlockStrategy("setExecutorBlockStrategy2");
		info2.setChildJobId("1");

		int item2 = jobInfoDao.update(info2);

		jobInfoDao.delete(info2.getId());

		List<JobInfo> list2 = jobInfoDao.getJobsByActuator(1L);

		int ret3 = jobInfoDao.findAllCount();

	}

}
