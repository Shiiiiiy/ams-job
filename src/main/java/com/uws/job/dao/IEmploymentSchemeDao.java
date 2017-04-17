package com.uws.job.dao;

import java.util.List;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.EmploymentScheme;
import com.uws.domain.job.EmploymentSchemeClassView;
import com.uws.domain.job.EmploymentSchemeCollegeView;
import com.uws.domain.job.EmploymentSchemeMajorView;
import com.uws.sys.model.Dic;

/**
 * @className IEmploymentSchemeDao.java
 * @package com.uws.job.dao.impl
 * @description
 * @date 2015-10-10  下午3:05:55
 */
public interface IEmploymentSchemeDao extends IBaseDao {
	/**
	 * 分页查询
	 * @param employmentScheme
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	public Page queryEmploymentSchemePage(EmploymentScheme employmentScheme, int pageSize, int pageNo);
	/**
	 * 通过学生学号查询就业方案
	 * @param stuNumber
	 * @return 
	 */
	public EmploymentScheme queryEmploymentSchemeByStuId(String stuId);
	/**
	 * 通过查询条件查询EmploymentScheme
	 * @param employmentSchemeVO
	 * @return
	 */
	public List<EmploymentScheme> queryEmploymentSchemeByCond(EmploymentScheme employmentSchemeVO);
	/**
	 * 查询出全部的EmploymentScheme记录总数
	 * @return 
	 */
	public long getCount();
	/**
	 * 按学院统计就业率
	 * @param employmentSchemeVO
	 * @param schoolYear
	 * @return
	 */
	public List<EmploymentSchemeCollegeView> statEmploymentSchemeByCollege(EmploymentScheme employmentSchemeVO);
	/**
	 * 按专业统计就业率
	 * @param employmentSchemeVO
	 * @param schoolYear
	 * @return
	 */
	public List<EmploymentSchemeMajorView> statEmploymentSchemeByMajor(EmploymentScheme employmentSchemeVO);
	/**
	 * 按班级统计就业率
	 * @param employmentSchemeVO
	 * @param schoolYear
	 * @return
	 */
	public List<EmploymentSchemeClassView> statEmploymentSchemeByClass(EmploymentScheme employmentSchemeVO);
	/**
	 * 按学院统计就业率分页
	 * @param employmentSchemeVO
	 * @param schoolYear
	 * @return
	 */
	public Page statEmploymentSchemeByCollegePage(EmploymentScheme employmentSchemeVO,int pageSize,int pageNo);
	/**
	 * 按专业统计就业率分页
	 * @param employmentSchemeVO
	 * @param schoolYear
	 * @return
	 */
	public Page statEmploymentSchemeByMajorPage(EmploymentScheme employmentSchemeVO,int pageSize,int pageNo);
	/**
	 * 按班级统计就业率分页
	 * @param employmentSchemeVO
	 * @param schoolYear
	 * @return
	 */
	public Page statEmploymentSchemeByClassPage(EmploymentScheme employmentSchemeVO,int pageSize,int pageNo);
}
