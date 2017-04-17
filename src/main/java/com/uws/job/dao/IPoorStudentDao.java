package com.uws.job.dao;

import java.util.List;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.PoorStudent;
import com.uws.domain.job.PoorStudentClassView;
import com.uws.domain.job.PoorStudentCollegeView;
import com.uws.domain.job.PoorStudentMajorView;
import com.uws.domain.sponsor.DifficultStudentInfo;
import com.uws.sys.model.Dic;

public interface IPoorStudentDao extends IBaseDao
{
	/**
	 * 
	* @Title: IPoorStudentDao.java 
	* @Package com.uws.job.service 
	* @Description:双困生查询列表
	* @author pc  
	* @date 2015-12-2 下午2:20:02
	 */
	public Page queryPoorStudentInfoList(int pageNo, int pageSize,PoorStudent poorStudent,Dic yearDic);
    
	/**
	 * 
	* @Title: IPoorStudentDao.java 
	* @Package com.uws.job.service 
	* @Description:双困生查询列表信息
	* @author pc  
	* @date 2015-12-2 下午2:20:02
	 */
	public List<DifficultStudentInfo> queryDiffStudentInfoList();
	/**
	 * 
	* @Title: IPoorStudentDao.java 
	* @Package com.uws.job.service 
	* @Description:查询审核双困生列表
	* @author pc  
	* @date 2015-12-2 下午2:20:02
	 */
	public Page queryApprovePoorStudentInfoList(int pageNo, int pageSize,PoorStudent poorStudent,Dic yearDic);
	/**
	 * 
	* @Title: IPoorStudentDao.java 
	* @Package com.uws.job.service 
	* @Description:根据学院统计查询
	* @author pc  
	* @date 2015-12-2 下午2:20:02
	 */
	public List<PoorStudentCollegeView> queryPoorStudentByCollege(PoorStudent poorStudent,Dic yearDic);
	/**
	 * 
	* @Title: IPoorStudentDao.java 
	* @Package com.uws.job.service 
	* @Description:根据专业统计查询
	* @author pc  
	* @date 2015-12-2 下午2:20:02
	 */
	public List<PoorStudentMajorView> queryPoorStudentByByMajor(PoorStudent poorStudent,Dic yearDic);
	/**
	 * 
	* @Title: IPoorStudentDao.java 
	* @Package com.uws.job.service 
	* @Description:根据班级统计查询
	* @author pc  
	* @date 2015-12-2 下午2:20:02
	 */
	public List<PoorStudentClassView> queryPoorStudentByClass(PoorStudent poorStudent,Dic yearDic);
	/**
	 * 
	* @Title: IPoorStudentDao.java 
	* @Package com.uws.job.service 
	* @Description:双困生查询列表
	* @author pc  
	* @date 2015-12-2 下午2:20:02
	 */
	public Page queryPassPoorStudentInfoList(int pageNo, int pageSize,PoorStudent poorStudent,Dic yearDic,String currentStudentId);
    
	/**
	 * 
	 * @Title: IPoorStudentDao.java 
	 * @Package com.uws.job.dao 
	 * @Description: 验证是否已添加过学生
	 * @author LiuChen 
	 * @date 2016-1-6 下午1:00:11
	 */
	public boolean isExistStudent(String id, String studentId, String schoolYear);
	

}
