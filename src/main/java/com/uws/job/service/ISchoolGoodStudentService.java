package com.uws.job.service;

import java.util.List;
import com.uws.core.base.IBaseService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.SchoolGoodStudent;
import com.uws.domain.job.SchoolGoodStudentClassView;
import com.uws.domain.job.SchoolGoodStudentCollegeView;
import com.uws.domain.job.SchoolGoodStudentMajorView;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.sys.model.Dic;
import com.uws.user.model.User;

/**
 * @className ISchoolGoodStudentService.java
 * @package com.uws.job.service
 * @description
 * @date 2015-11-6  下午5:06:57
 */
public interface ISchoolGoodStudentService extends IBaseService {
	
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
	 * 添加
	 * @param schoolGoodStudentPO
	 */
	public void saveSchoolGoodStudent(SchoolGoodStudent schoolGoodStudentPO);
	
	/**
	 * 通过ID查询
	 * @param id
	 * @return
	 */
	public SchoolGoodStudent querySchoolGoodStudentById(String id);
	
	/**
	 * 修改
	 * @param schoolGoodStudentPO
	 */
	public void updateSchoolGoodStudent(SchoolGoodStudent schoolGoodStudentPO);
	
	/**
	 * 条件查询
	 * @param schoolGoodStudentVO
	 * @return
	 */
	public List<SchoolGoodStudent> querySchoolGoodStudentByCond(SchoolGoodStudent schoolGoodStudentVO);
	
	/**
	 * 通过学院统计校优人数
	 * @param schoolGoodStudentVO 
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
	 * 保存或更新，如数据库中不存在则添加，如数据库中存在则更新
	 * @param schoolGoodStudents
	 * @param schoolYear
	 * @param creator
	 */
	public String saveOrUpdate(List<SchoolGoodStudent> schoolGoodStudents,Dic schoolYear,User creator);
	
	/**
	 * 获取某个学生的全部奖助信息
	 * @param student
	 * @return
	 */
	public String getStuAllAward(StudentInfoModel student);
	
	/**
	 * 获取某个学生在校期间全部的综合测评信息
	 * @param startSchoolYear
	 * @param endSchoolYear
	 * @param studentInfoModelPO
	 * @return
	 */
	public String getAllEvaluationInfos(String startSchoolYear, String endSchoolYear,StudentInfoModel studentInfoModel);
	
	/**
	 * 格式化综合信息，接收JSON串
	 * @param allEvaluationInfos
	 * @return
	 */
	public String formatEvaluationInfos(String allEvaluationInfos);
	
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
