package com.uws.job.dao;

import java.util.List;
import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.SchoolGoodStudent;
import com.uws.domain.job.SchoolGoodStudentClassView;
import com.uws.domain.job.SchoolGoodStudentCollegeView;
import com.uws.domain.job.SchoolGoodStudentMajorView;

/**
 * @className ISchoolGoodStudentDao.java
 * @package com.uws.job.dao
 * @description
 * @date 2015-11-6  下午5:04:49
 */
public interface ISchoolGoodStudentDao extends IBaseDao {
	/**
	 * 分页查询
	 * @param schoolGoodStudentVO
	 * @param dEFAULT_PAGE_SIZE
	 * @param pageNo
	 * @param queryType  查询方式  1：在未删除的基础数据上查询，2：是在审核通过和撤销的基础数据上查询
	 * @return
	 */
	public Page querySchoolGoodStudentPage(SchoolGoodStudent schoolGoodStudentVO,int dEFAULT_PAGE_SIZE, int pageNo,int queryType);
	
	/**
	 * 条件查询
	 * @param schoolGoodStudentVO
	 * @return
	 */
	public List<SchoolGoodStudent> querySchoolGoodStudentByCond(SchoolGoodStudent schoolGoodStudentVO);
	
	/**
	 * 根据学生Id的集合查询校优秀毕业生
	 * @param stuIds
	 * @return
	 */
	public List<SchoolGoodStudent> querySchoolGoodStudentsByStuId(String stuId);

	/**
	 * 通过学院统计校优人数
	 * @param pageNo 
	 * @param dEFAULT_PAGE_SIZE 
	 * @return
	 */
	public List<SchoolGoodStudentCollegeView> statSchoolGoodStudentByCollege(SchoolGoodStudent schoolGoodStudentVO);
	
	/**
	 * 按照专业统计
	 * @return
	 */
	public List<SchoolGoodStudentMajorView> statSchoolGoodStudentByMajor(SchoolGoodStudent schoolGoodStudentVO);
	
	/**
	 * 按照班级统计
	 * @return
	 */
	public List<SchoolGoodStudentClassView> statSchoolGoodStudentByClass(SchoolGoodStudent schoolGoodStudentVO);
	
	/**
	 * 通过ids数组，查询
	 * @param schoolGoodStudentIds
	 */
	public List<SchoolGoodStudent> querySchoolGoodStudentByIds(String[] schoolGoodStudentIds);
	
	/**
	 * 批量保存审核结果
	 * @param schoolGoodStudentIds
	 * @param approveStatus
	 */
	public void updateApproveStateStudent(String[] schoolGoodStudentIds, String approveStatus,String approveReason);
}
