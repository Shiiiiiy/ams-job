package com.uws.job.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.job.EmploymentScheme;
import com.uws.domain.job.EmploymentSchemeClassView;
import com.uws.domain.job.EmploymentSchemeCollegeView;
import com.uws.domain.job.EmploymentSchemeMajorView;
import com.uws.job.dao.IEmploymentSchemeDao;
import com.uws.job.util.Constants;
import com.uws.sys.model.Dic;


/**
 * @className EmploymentSchemeDaoImpl.java
 * @package com.uws.job.dao.impl
 * @description
 * @author Administrator
 * @date 2015-10-10  下午3:06:57
 */
@Repository("employmentSchemeDao")
public class EmploymentSchemeDaoImpl extends BaseDaoImpl implements IEmploymentSchemeDao {
	/**
	 * 分页查询
	 * @param employmentScheme
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	@Override
	public Page queryEmploymentSchemePage(EmploymentScheme employmentScheme, int pageSize, int pageNo) {
		Map<String,Object> map = this.getHql(employmentScheme);
		String hql = (String) map.get("hql");
		hql += " order by studentId.college.id";
		Map<String,Object> params = (Map<String, Object>) map.get("params");
		return this.pagedQuery(hql, params, pageSize, pageNo);
	}
	/**
	 * 通过学生学号查询就业方案
	 * @param stuNumber
	 */
	@Override
	public EmploymentScheme queryEmploymentSchemeByStuId(String stuId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("stuId", stuId);
		params.put("delStateId", Constants.STATUS_NORMAL_DICS.getId());
		List<EmploymentScheme> employmentSchemes = this.query("from EmploymentScheme where delState.id = :delStateId and studentId.id = :stuId", params);
		if(employmentSchemes == null || employmentSchemes.size() == 0){
			return null;
		}else{
			return employmentSchemes.get(0);
		}
	}
	/**
	 * 通过查询条件查询EmploymentScheme
	 * @param employmentSchemeVO
	 * @return
	 */
	@Override
	public List<EmploymentScheme> queryEmploymentSchemeByCond(EmploymentScheme employmentSchemeVO) {
		Map<String, Object> map = this.getHql(employmentSchemeVO);
		String hql = (String) map.get("hql");
		Map<String,Object> params = (Map<String, Object>) map.get("params");
		return this.query(hql, params);
	}
	/**
	 * 查询出全部的EmploymentScheme记录总数
	 */
	@Override
	public long getCount() {
		String sql = "select count(e.id) from EmploymentScheme e";
		return ((Long)queryUnique(sql, new Object[0])).longValue();
	}
	//拼接Hql语句
	private Map<String,Object> getHql(EmploymentScheme employmentScheme){
		String hql = "from EmploymentScheme where delState.id = :delStateId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("delStateId", Constants.STATUS_NORMAL_DICS.getId());
		if(DataUtil.isNotNull(employmentScheme)){
			if(DataUtil.isNotNull(employmentScheme.getStudentId()) && DataUtil.isNotNull(employmentScheme.getStudentId().getCollege()) && DataUtil.isNotNull(employmentScheme.getStudentId().getCollege().getId())){
				hql += " and studentId.college.id = :collegeId";
				params.put("collegeId", employmentScheme.getStudentId().getCollege().getId());
			}
			if(DataUtil.isNotNull(employmentScheme.getStudentId()) && DataUtil.isNotNull(employmentScheme.getStudentId().getMajor()) && DataUtil.isNotNull(employmentScheme.getStudentId().getMajor().getId())){
				hql += " and studentId.major.id = :majorId";
				params.put("majorId", employmentScheme.getStudentId().getMajor().getId());
			}
			if(DataUtil.isNotNull(employmentScheme.getStudentId()) && DataUtil.isNotNull(employmentScheme.getStudentId().getClassId()) && DataUtil.isNotNull(employmentScheme.getStudentId().getClassId().getId())){
				hql += " and studentId.classId.id = :classId";
				params.put("classId", employmentScheme.getStudentId().getClassId().getId());
			}
			if(DataUtil.isNotNull(employmentScheme.getStudentId()) && DataUtil.isNotNull(employmentScheme.getStudentId().getName())){
				hql += " and studentId.name like :name";
				params.put("name", "%"+employmentScheme.getStudentId().getName()+"%");
			}
			if(DataUtil.isNotNull(employmentScheme.getStudentId()) && DataUtil.isNotNull(employmentScheme.getStudentId().getStuNumber())){
				hql += " and studentId.stuNumber like :stuNumber";
				params.put("stuNumber", "%"+employmentScheme.getStudentId().getStuNumber()+"%");
			}
			if(DataUtil.isNotNull(employmentScheme.getGraduateCode()) && DataUtil.isNotNull(employmentScheme.getGraduateCode().getId())){
				hql += " and graduateCode.id = :graduateCodeId";
				params.put("graduateCodeId", employmentScheme.getGraduateCode().getId());
			}
		}
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("hql", hql);
		map.put("params", params);
		return map;
	}
	/**
	 * 按学院统计就业率
	 * @param employmentSchemeVO
	 * @param schoolYear
	 * @return
	 */
	@Override
	public List<EmploymentSchemeCollegeView> statEmploymentSchemeByCollege(EmploymentScheme employmentSchemeVO) {
		String hql = "from EmploymentSchemeCollegeView where 1=1";
		Map<String,Object> params = new HashMap<String,Object>();
		if (DataUtil.isNotNull(employmentSchemeVO.getStudentId()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege().getId())) {
			hql += " and college.id = :collegeId";
			params.put("collegeId", employmentSchemeVO.getStudentId().getCollege().getId());
		}
		if(DataUtil.isNotNull(employmentSchemeVO.getSchoolYear()) && DataUtil.isNotNull(employmentSchemeVO.getSchoolYear().getId())){
			hql += " and schoolYear.id = :schoolYearId";
			params.put("schoolYearId", employmentSchemeVO.getSchoolYear().getId());
		}
		hql += " order by college.id,schoolYear.code desc";
		return this.query(hql, params);
	}
	/**
	 * 按专业统计就业率
	 * @param employmentSchemeVO
	 * @param schoolYear
	 * @return
	 */
	@Override
	public List<EmploymentSchemeMajorView> statEmploymentSchemeByMajor(EmploymentScheme employmentSchemeVO) {
		String hql = "from EmploymentSchemeMajorView where 1=1";
		Map<String,Object> params = new HashMap<String,Object>();
		if (DataUtil.isNotNull(employmentSchemeVO.getStudentId())) {
			if(DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege().getId())){
				hql += " and major.collage.id = :collegeId";
				params.put("collegeId", employmentSchemeVO.getStudentId().getCollege().getId());
			}
			if(DataUtil.isNotNull(employmentSchemeVO.getStudentId().getMajor()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getMajor().getId())){
				hql += " and major.id = :majorId";
				params.put("majorId", employmentSchemeVO.getStudentId().getMajor().getId());
			}
		}
		if(DataUtil.isNotNull(employmentSchemeVO.getSchoolYear()) && DataUtil.isNotNull(employmentSchemeVO.getSchoolYear().getId())){
			hql += " and schoolYear.id = :schoolYearId";
			params.put("schoolYearId", employmentSchemeVO.getSchoolYear().getId());
		}
		hql += " order by major.collage.id,major.id,schoolYear.code desc";
		return this.query(hql, params);
	}
	/**
	 * 按班级统计就业率
	 * @param employmentSchemeVO
	 * @param schoolYear
	 * @return
	 */
	@Override
	public List<EmploymentSchemeClassView> statEmploymentSchemeByClass(EmploymentScheme employmentSchemeVO) {
		String hql = "from EmploymentSchemeClassView where 1=1";
		Map<String,Object> params = new HashMap<String,Object>();
		if (DataUtil.isNotNull(employmentSchemeVO.getStudentId())) {
			if(DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege().getId())){
				hql += " and clazz.major.collage.id = :collegeId";
				params.put("collegeId", employmentSchemeVO.getStudentId().getCollege().getId());
			}
			if(DataUtil.isNotNull(employmentSchemeVO.getStudentId().getMajor()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getMajor().getId())){
				hql += " and clazz.major.id = :majorId";
				params.put("majorId", employmentSchemeVO.getStudentId().getMajor().getId());
			}
			if(DataUtil.isNotNull(employmentSchemeVO.getStudentId().getClassId()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getClassId().getId())){
				hql += " and clazz.id = :clazzId";
				params.put("clazzId", employmentSchemeVO.getStudentId().getClassId().getId());
			}
		}
		if(DataUtil.isNotNull(employmentSchemeVO.getSchoolYear()) && DataUtil.isNotNull(employmentSchemeVO.getSchoolYear().getId())){
			hql += " and schoolYear.id = :schoolYearId";
			params.put("schoolYearId", employmentSchemeVO.getSchoolYear().getId());
		}
		hql += " order by clazz.major.collage.id,clazz.major.id,clazz.id,schoolYear.code desc";
		return this.query(hql, params);
	}
	public Page statEmploymentSchemeByCollegePage(EmploymentScheme employmentSchemeVO,int pageSize,int pageNo){
		String hql = "from EmploymentSchemeCollegeView where 1=1";
		Map<String,Object> params = new HashMap<String,Object>();
		if (DataUtil.isNotNull(employmentSchemeVO.getStudentId()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege().getId())) {
			hql += " and college.id = :collegeId";
			params.put("collegeId", employmentSchemeVO.getStudentId().getCollege().getId());
		}
		if(DataUtil.isNotNull(employmentSchemeVO.getSchoolYear()) && DataUtil.isNotNull(employmentSchemeVO.getSchoolYear().getId())){
			hql += " and schoolYear.id = :schoolYearId";
			params.put("schoolYearId", employmentSchemeVO.getSchoolYear().getId());
		}
		hql += " order by college.id,schoolYear.code desc";
		return this.pagedQuery(hql, params, pageSize, pageNo);
	}
	/**
	 * 按专业统计就业率分页
	 * @param employmentSchemeVO
	 * @param schoolYear
	 * @return
	 */
	public Page statEmploymentSchemeByMajorPage(EmploymentScheme employmentSchemeVO,int pageSize,int pageNo){
		String hql = "from EmploymentSchemeMajorView where 1=1";
		Map<String,Object> params = new HashMap<String,Object>();
		if (DataUtil.isNotNull(employmentSchemeVO.getStudentId())) {
			if(DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege().getId())){
				hql += " and major.collage.id = :collegeId";
				params.put("collegeId", employmentSchemeVO.getStudentId().getCollege().getId());
			}
			if(DataUtil.isNotNull(employmentSchemeVO.getStudentId().getMajor()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getMajor().getId())){
				hql += " and major.id = :majorId";
				params.put("majorId", employmentSchemeVO.getStudentId().getMajor().getId());
			}
		}
		if(DataUtil.isNotNull(employmentSchemeVO.getSchoolYear()) && DataUtil.isNotNull(employmentSchemeVO.getSchoolYear().getId())){
			hql += " and schoolYear.id = :schoolYearId";
			params.put("schoolYearId", employmentSchemeVO.getSchoolYear().getId());
		}
		hql += " order by major.collage.id,major.id,schoolYear.code desc";
		return this.pagedQuery(hql, params, pageSize, pageNo);
	}
	/**
	 * 按班级统计就业率分页
	 * @param employmentSchemeVO
	 * @param schoolYear
	 * @return
	 */
	public Page statEmploymentSchemeByClassPage(EmploymentScheme employmentSchemeVO,int pageSize,int pageNo){
		String hql = "from EmploymentSchemeClassView where 1=1";
		Map<String,Object> params = new HashMap<String,Object>();
		if (DataUtil.isNotNull(employmentSchemeVO.getStudentId())) {
			if(DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege().getId())){
				hql += " and clazz.major.collage.id = :collegeId";
				params.put("collegeId", employmentSchemeVO.getStudentId().getCollege().getId());
			}
			if(DataUtil.isNotNull(employmentSchemeVO.getStudentId().getMajor()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getMajor().getId())){
				hql += " and clazz.major.id = :majorId";
				params.put("majorId", employmentSchemeVO.getStudentId().getMajor().getId());
			}
			if(DataUtil.isNotNull(employmentSchemeVO.getStudentId().getClassId()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getClassId().getId())){
				hql += " and clazz.id = :clazzId";
				params.put("clazzId", employmentSchemeVO.getStudentId().getClassId().getId());
			}
		}
		if(DataUtil.isNotNull(employmentSchemeVO.getSchoolYear()) && DataUtil.isNotNull(employmentSchemeVO.getSchoolYear().getId())){
			hql += " and schoolYear.id = :schoolYearId";
			params.put("schoolYearId", employmentSchemeVO.getSchoolYear().getId());
		}
		hql += " order by clazz.major.collage.id,clazz.major.id,clazz.id,schoolYear.code desc";
		return this.pagedQuery(hql, params, pageSize, pageNo);
	}
}
