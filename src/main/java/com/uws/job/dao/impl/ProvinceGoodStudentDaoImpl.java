package com.uws.job.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.job.ProvinceGoodStudent;
import com.uws.domain.job.ProvinceGoodStudentClassView;
import com.uws.domain.job.ProvinceGoodStudentCollegeView;
import com.uws.domain.job.ProvinceGoodStudentMajorView;
import com.uws.job.dao.IProvinceGoodStudentDao;
import com.uws.job.util.Constants;
import com.uws.sys.model.Dic;
@Repository("com.uws.job.dao.impl.ProvinceGoodStudentDaoImpl")
public class ProvinceGoodStudentDaoImpl extends BaseDaoImpl implements IProvinceGoodStudentDao
{   
	/**
	 * 
	* @Description:查询省优秀毕业生列表
	* @author LiuChen  
	* @date 2015-12-2 下午5:05:31
	 */
	@Override
	public Page queryProvinceGoodStudentList(int pageNo, int pageSize,ProvinceGoodStudent provinceGoodStudent,Dic nowSchoolYear,String currentStudentId)
	{
		 List<Object> values = new ArrayList<Object>();
		 StringBuffer hql = new StringBuffer("select p,s from ProvinceGoodStudent p right outer join p.schoolGoodStudent s where 1=1");
		/* hql.append(" and (s.approveStatus=? or (s.approveStatus=? and p.status.id =?) or(s.approveStatus=? and p.status.id =?) or (s.approveStatus=? and p.status.id =?))");
		 values.add("PASS");
		 values.add("UNDO");
		 values.add(Constants.STATUS_APPLY_PASS_DIC.getId());
		 values.add("UNDO");
		 values.add(Constants.STATUS_APPLY_DIC.getId());
		 values.add("UNDO");
		 values.add(Constants.STATUS_APPLY_UNPASS_DIC.getId());*/
		 hql.append(" and(s.approveStatus=? or p.status.id =? or p.status.id =? or p.status.id =? or p.status.id =?)");
		 values.add("PASS");
		 values.add(Constants.STATUS_APPLY_DIC.getId());
		 values.add(Constants.STATUS_APPLY_PASS_DIC.getId());
		 values.add(Constants.STATUS_APPLY_UNPASS_DIC.getId());
		 values.add(Constants.STATUS_APPLY_UNDO_DIC.getId());
		 if(StringUtils.isNotBlank(currentStudentId))
		 {
			 hql.append(" and p.schoolGoodStudent.studentId.id = ?");
			 values.add(currentStudentId);
		 }
		 if(provinceGoodStudent != null)
		 {
			 if(provinceGoodStudent.getSchoolGoodStudent()!=null && provinceGoodStudent.getSchoolGoodStudent().getStudentId()!=null && StringUtils.isNotBlank(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getName()))
			 {
				 hql.append(" and s.studentId.name like ?");
				 values.add("%" + provinceGoodStudent.getSchoolGoodStudent().getStudentId().getName() + "%");
			 }
			 if(provinceGoodStudent.getSchoolGoodStudent()!=null && provinceGoodStudent.getSchoolGoodStudent().getStudentId()!=null && StringUtils.isNotBlank(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getStuNumber()))
			 {
				 hql.append(" and s.studentId.stuNumber like ?");
				 values.add("%" + provinceGoodStudent.getSchoolGoodStudent().getStudentId().getStuNumber() + "%");
			 }
			 if(provinceGoodStudent.getCollege()!=null && StringUtils.isNotBlank(provinceGoodStudent.getCollege().getId()))
			 {
				 hql.append(" and s.studentId.college.id = ?");
				 values.add(provinceGoodStudent.getCollege().getId());
			 }
			 if(provinceGoodStudent.getMajor()!=null && StringUtils.isNotBlank(provinceGoodStudent.getMajor().getId()))
			 {
				 hql.append(" and s.studentId.major.id = ?");
				 values.add(provinceGoodStudent.getMajor().getId());
			 }
			 if(provinceGoodStudent.getClassId()!=null && StringUtils.isNotBlank(provinceGoodStudent.getClassId().getId()))
			 {
				 hql.append(" and s.studentId.classId.id = ?");
				 values.add(provinceGoodStudent.getClassId().getId());
			 }
			 if(provinceGoodStudent.getStatus()!=null && StringUtils.isNotBlank(provinceGoodStudent.getStatus().getId()))
			 {
				 if(provinceGoodStudent.getStatus().getId().equals(Constants.STATUS_UNAPPLY_DIC.getId()))
				 {
					 hql.append(" and s.id not in (select p.schoolGoodStudent.id from ProvinceGoodStudent where 1=1)"); 
				 }else
				 {	 
				     hql.append(" and p.status.id = ?");
				     values.add(provinceGoodStudent.getStatus().getId());
				 }
			 }
		 }
			 if(nowSchoolYear!=null && StringUtils.isNotBlank(nowSchoolYear.getId()))
			 {
				 hql.append(" and s.schoolYear.id = ?");
				 values.add(nowSchoolYear.getId());
			 }
			 
		 hql.append(" order by s desc");
	     if (values.size() == 0) {
		      return pagedQuery(hql.toString(), pageNo, pageSize, new Object[0]);
		 }
		      return pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}
	
	/**
	 * 
	* @Description:根据学生校优秀毕业生查询省优秀毕业生学生
	* @author LiuChen  
	* @date 2015-12-2 下午5:05:49
	 */
	@Override
	public ProvinceGoodStudent findProvinceGoodStudentBySid(String sid)
	{
		ProvinceGoodStudent provinceGoodStudent =(ProvinceGoodStudent) this.queryUnique(" from ProvinceGoodStudent p where 1=1 and p.schoolGoodStudent.id= ?", new Object[] {sid});
		return provinceGoodStudent;
	}
	
	/**
	 * 
	* @Description: 查询省优秀毕业生审核信息列表
	* @author LiuChen  
	* @date 2015-12-2 下午5:06:10
	 */
	@Override
	public Page queryApproveProvinceGoodStudentList(int pageNo, int pageSize,ProvinceGoodStudent provinceGoodStudent,Dic nowSchoolYear)
	{
		 List<Object> values = new ArrayList<Object>();
		 StringBuffer hql = new StringBuffer("from ProvinceGoodStudent p where 1=1");
		// hql.append(" and p.schoolGoodStudent.approveStatus=? ");
		// values.add("PASS");
		 hql.append(" and (p.status.id =? or p.status.id =? or p.status.id =? or p.status.id =?)");
		 values.add(Constants.STATUS_APPLY_DIC.getId());
		 values.add(Constants.STATUS_APPLY_PASS_DIC.getId());
		 values.add(Constants.STATUS_APPLY_UNPASS_DIC.getId());
		 values.add(Constants.STATUS_APPLY_UNDO_DIC.getId());
		 if(provinceGoodStudent != null)
		 {
			 if(provinceGoodStudent.getSchoolGoodStudent()!=null && provinceGoodStudent.getSchoolGoodStudent().getStudentId()!=null && StringUtils.isNotBlank(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getName()))
			 {
				 hql.append(" and p.schoolGoodStudent.studentId.name like ?");
				 values.add("%" + provinceGoodStudent.getSchoolGoodStudent().getStudentId().getName() + "%");
			 }
			 if(provinceGoodStudent.getSchoolGoodStudent()!=null && provinceGoodStudent.getSchoolGoodStudent().getStudentId()!=null && StringUtils.isNotBlank(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getStuNumber()))
			 {
				 hql.append(" and p.schoolGoodStudent.studentId.stuNumber like ?");
				 values.add("%" + provinceGoodStudent.getSchoolGoodStudent().getStudentId().getStuNumber() + "%");
			 }
			 if(provinceGoodStudent.getSchoolGoodStudent()!=null && provinceGoodStudent.getSchoolGoodStudent().getStudentId()!=null && provinceGoodStudent.getSchoolGoodStudent().getStudentId().getCollege()!=null && StringUtils.isNotBlank(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getCollege().getId()))
			 {
				 hql.append(" and p.schoolGoodStudent.studentId.college.id = ?");
				 values.add(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getCollege().getId());
			 }
			 if(provinceGoodStudent.getSchoolGoodStudent()!=null && provinceGoodStudent.getSchoolGoodStudent().getStudentId()!=null && provinceGoodStudent.getSchoolGoodStudent().getStudentId().getMajor()!=null && StringUtils.isNotBlank(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getMajor().getId()))
			 {
				 hql.append(" and p.schoolGoodStudent.studentId.major.id = ?");
				 values.add(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getMajor().getId());
			 }
			 if(provinceGoodStudent.getSchoolGoodStudent()!=null && provinceGoodStudent.getSchoolGoodStudent().getStudentId()!=null && provinceGoodStudent.getSchoolGoodStudent().getStudentId().getClassId()!=null && StringUtils.isNotBlank(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getClassId().getId()))
			 {
				 hql.append(" and p.schoolGoodStudent.studentId.classId.id = ?");
				 values.add(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getClassId().getId());
			 }
			 if(provinceGoodStudent.getStatus()!=null && StringUtils.isNotBlank(provinceGoodStudent.getStatus().getId()))
			 {
			     hql.append(" and p.status.id = ?");
			     values.add(provinceGoodStudent.getStatus().getId());
			 }
			 // 审核状态
			if (provinceGoodStudent.getStatus()!=null && !StringUtils.isEmpty(provinceGoodStudent.getStatus().getName())) 
			{
				{
					 hql.append(" and p.status.code = ?");
					 values.add(provinceGoodStudent.getStatus().getName());
				}
			}
		 }
			 if(nowSchoolYear!=null && StringUtils.isNotBlank(nowSchoolYear.getId()))
			 {
				 hql.append(" and p.schoolGoodStudent.schoolYear.id = ?");
				 values.add(nowSchoolYear.getId());
			 }
		 hql.append(" order by p.status,p.updateTime desc");
	     if (values.size() == 0) {
		      return pagedQuery(hql.toString(), pageNo, pageSize, new Object[0]);
		 }
		      return pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}
	
	/**
	 * 
	* @Description:查询审核通过的省优秀毕业生
	* @author LiuChen  
	* @date 2015-12-2 下午5:08:24
	 */
	@Override
	public Page queryPassProvinceGoodStudentList(ProvinceGoodStudent provinceGoodStudent, int pageSize, int pageNo,Dic yearDic)
	{
		 List<Object> values = new ArrayList<Object>();
		 StringBuffer hql = new StringBuffer("from ProvinceGoodStudent p where 1=1");
		 hql.append(" and p.status.id = ?");
		 values.add(Constants.STATUS_APPLY_PASS_DIC.getId());
		 if(provinceGoodStudent != null)
		 {
			 if(provinceGoodStudent.getSchoolGoodStudent()!=null && provinceGoodStudent.getSchoolGoodStudent().getStudentId()!=null && StringUtils.isNotBlank(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getName()))
			 {
				 hql.append(" and p.schoolGoodStudent.studentId.name like ?");
				 values.add("%" + provinceGoodStudent.getSchoolGoodStudent().getStudentId().getName() + "%");
			 }
			 if(provinceGoodStudent.getSchoolGoodStudent()!=null && provinceGoodStudent.getSchoolGoodStudent().getStudentId()!=null && StringUtils.isNotBlank(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getStuNumber()))
			 {
				 hql.append(" and p.schoolGoodStudent.studentId.stuNumber like ?");
				 values.add("%" + provinceGoodStudent.getSchoolGoodStudent().getStudentId().getStuNumber() + "%");
			 }
			 if(provinceGoodStudent.getCollege()!=null && StringUtils.isNotBlank(provinceGoodStudent.getCollege().getId()))
			 {
				 hql.append(" and p.schoolGoodStudent.studentId.college.id = ?");
				 values.add(provinceGoodStudent.getCollege().getId());
			 }
			 if(provinceGoodStudent.getMajor()!=null && StringUtils.isNotBlank(provinceGoodStudent.getMajor().getId()))
			 {
				 hql.append(" and p.schoolGoodStudent.studentId.major.id = ?");
				 values.add(provinceGoodStudent.getMajor().getId());
			 }
			 if(provinceGoodStudent.getClassId()!=null && StringUtils.isNotBlank(provinceGoodStudent.getClassId().getId()))
			 {
				 hql.append(" and p.schoolGoodStudent.studentId.classId.id = ?");
				 values.add(provinceGoodStudent.getClassId().getId());
			 }
		 }
			 if(yearDic!=null && StringUtils.isNotBlank(yearDic.getId()))
			 {
				 hql.append(" and p.schoolGoodStudent.schoolYear.id = ?");
				 values.add(yearDic.getId());
			 }
		 hql.append(" order by p.schoolGoodStudent.studentId.classId,p.updateTime desc");
	     if (values.size() == 0) {
		      return pagedQuery(hql.toString(), pageNo, pageSize, new Object[0]);
		 }
		      return pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}

    /**
     * 
    * @Description: 通过学院统计省优秀毕业生
    * @author LiuChen  
    * @date 2015-12-2 下午5:08:49
     */
	@Override
	@SuppressWarnings("unchecked")
    public List<ProvinceGoodStudentCollegeView> queryProvinceGoodStudentByCollege(ProvinceGoodStudent provinceGoodStudent, int pageSize, int pageNo,Dic nowSchoolYear)
    {
		String hql = "from ProvinceGoodStudentCollegeView t where 1 = 1 ";
		Map<String,Object> params = new HashMap<String, Object>();
		if(DataUtil.isNotNull(provinceGoodStudent)){
			if(DataUtil.isNotNull(provinceGoodStudent.getSchoolGoodStudent()) && DataUtil.isNotNull(provinceGoodStudent.getSchoolGoodStudent().getStudentId()) && DataUtil.isNotNull(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getCollege()) && DataUtil.isNotNull(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getCollege().getId())){
				hql += " and t.college.id = :collegeId";
				params.put("collegeId", provinceGoodStudent.getSchoolGoodStudent().getStudentId().getCollege().getId());
			}
		}
		if(nowSchoolYear!=null && DataUtil.isNotNull(nowSchoolYear.getId())){
			hql += " and t.schoolYear.id = :schoolYearId";
			params.put("schoolYearId", nowSchoolYear.getId());
		}
		return this.query(hql, params);
    }

    /**
     * 
    * @Description:通过专业统计省优秀毕业生
    * @author LiuChen  
    * @date 2015-12-2 下午5:09:20
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ProvinceGoodStudentMajorView> queryProvinceGoodStudentByByMajor(ProvinceGoodStudent provinceGoodStudent,Dic nowSchoolYear)
    {
		String hql = "from ProvinceGoodStudentMajorView t where 1 =1 ";
		Map<String,Object> params = new HashMap<String, Object>();
		if(DataUtil.isNotNull(provinceGoodStudent)){
			if(provinceGoodStudent.getSchoolGoodStudent()!=null && DataUtil.isNotNull(provinceGoodStudent.getSchoolGoodStudent().getStudentId())){
				if(DataUtil.isNotNull(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getMajor()) && DataUtil.isNotNull(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getMajor().getId())){
					hql += " and t.major.id = :majorId";
					params.put("majorId", provinceGoodStudent.getSchoolGoodStudent().getStudentId().getMajor().getId());
				}
				
				if(DataUtil.isNotNull(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getCollege()) && DataUtil.isNotNull(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getCollege().getId())){
					hql += " and t.major.collage.id = :collegeId";
					params.put("collegeId", provinceGoodStudent.getSchoolGoodStudent().getStudentId().getCollege().getId());
				}
			}
		}
		if(nowSchoolYear!=null && DataUtil.isNotNull(nowSchoolYear.getId())){
			hql += " and t.schoolYear.id = :schoolYearId";
			params.put("schoolYearId", nowSchoolYear.getId());
		}
		return this.query(hql, params);
    }

    /**
     * 
    * @Description:通过班级统计省优秀毕业生
    * @author LiuChen  
    * @date 2015-12-2 下午5:09:31
     */
	@SuppressWarnings("unchecked")
    @Override
    public List<ProvinceGoodStudentClassView> queryProvinceGoodStudentByClass(ProvinceGoodStudent provinceGoodStudent,Dic nowSchoolYear)
    {
		String hql = "from ProvinceGoodStudentClassView t where 1 = 1 ";
		Map<String,Object> params = new HashMap<String, Object>();
		if(DataUtil.isNotNull(provinceGoodStudent)){
			if(provinceGoodStudent.getSchoolGoodStudent()!=null && DataUtil.isNotNull(provinceGoodStudent.getSchoolGoodStudent().getStudentId())){
				
				if(DataUtil.isNotNull(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getClassId()) && DataUtil.isNotNull(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getClassId().getId())){
					hql += " and t.classModel.id = :classId";
					params.put("classId", provinceGoodStudent.getSchoolGoodStudent().getStudentId().getClassId().getId());
				}
				
				if(DataUtil.isNotNull(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getMajor()) && DataUtil.isNotNull(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getMajor().getId())){
					hql += " and t.classModel.major.id = :majorId";
					params.put("majorId", provinceGoodStudent.getSchoolGoodStudent().getStudentId().getMajor().getId());
				}
				
				if(DataUtil.isNotNull(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getCollege()) && DataUtil.isNotNull(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getCollege().getId())){
					hql += " and t.classModel.major.collage.id = :collegeId";
					params.put("collegeId", provinceGoodStudent.getSchoolGoodStudent().getStudentId().getCollege().getId());
				}
			}
		}
		if(nowSchoolYear!=null && DataUtil.isNotNull(nowSchoolYear.getId())){
			hql += " and t.schoolYear.id = :schoolYearId";
			params.put("schoolYearId", nowSchoolYear.getId());
		}
		return this.query(hql, params);
    }

}
