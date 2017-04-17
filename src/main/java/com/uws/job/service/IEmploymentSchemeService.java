package com.uws.job.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;

import com.uws.core.base.IBaseService;
import com.uws.core.excel.ExcelException;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.EmploymentScheme;
import com.uws.domain.job.EmploymentSchemeClassView;
import com.uws.domain.job.EmploymentSchemeCollegeView;
import com.uws.domain.job.EmploymentSchemeMajorView;
import com.uws.sys.model.Dic;

/**
 * @className IEmploymentSchemeService.java
 * @package com.uws.job.service
 * @description
 * @date 2015-10-10  下午3:01:16
 */
public interface IEmploymentSchemeService extends IBaseService {
	/**
	 * 分页查询
	 * @param employmentScheme
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	public Page queryEmploymentSchemePage(EmploymentScheme employmentScheme,int pageSize,int pageNo);
	/**
	 * 添加EmploymentScheme
	 * @param employmentScheme
	 */
	public void saveEmploymentScheme(EmploymentScheme employmentScheme);
	/**
	 * 通过ID查询EmploymentScheme
	 * @param id
	 */
	public EmploymentScheme queryEmploymentSchemeById(String id);
	/**
	 * 修改EmploymentScheme
	 * @param employmentSchemePO
	 */
	public void updateEmploymentScheme(EmploymentScheme employmentSchemePO);
	/**
	 * 通过学生学号查询EmploymentScheme
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
	 * 查询employmentSchemes集合中的数据存在数据库中的数据
	 * @param employmentSchemes 
	 * @return 存在于数据库中的数据
	 */
	public List<EmploymentScheme[]> compareData(List<EmploymentScheme> employmentSchemes);
	/**
	 * 用户确定导入Excel在系统中存在的数据
	 * @param compareId 
	 * @param filePath 
	 * @param exitsEmploymentSchemes 
	 * @throws Exception 
	 * @throws ClassNotFoundException 
	 * @throws InstantiationException 
	 * @throws ExcelException 
	 * @throws IllegalAccessException 
	 * @throws IOException 
	 * @throws OfficeXmlFileException 
	 */
	public void importData(List<EmploymentScheme[]> exitsEmploymentSchemes, String filePath, String compareId) throws OfficeXmlFileException, IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException, Exception;
	/**
	 * 对导入的数据修改或保存
	 * @param employmentSchemes
	 */
	public void saveOrUpdate(List<EmploymentScheme> employmentSchemes) throws ParseException;
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
