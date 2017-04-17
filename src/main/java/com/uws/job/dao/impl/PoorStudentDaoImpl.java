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
import com.uws.domain.job.PoorStudent;
import com.uws.domain.job.PoorStudentClassView;
import com.uws.domain.job.PoorStudentCollegeView;
import com.uws.domain.job.PoorStudentMajorView;
import com.uws.domain.sponsor.DifficultStudentInfo;
import com.uws.job.dao.IPoorStudentDao;
import com.uws.job.util.Constants;
import com.uws.sys.model.Dic;
@Repository("com.uws.job.dao.impl.PoorStudentDaoImpl")
public class PoorStudentDaoImpl extends BaseDaoImpl implements IPoorStudentDao
{

	/**
	 * 
	* @Title: PoorStudentDaoImpl.java 
	* @Package com.uws.job.dao.impl 
	* @Description:双困生查询列表
	* @author pc  
	* @date 2015-12-2 下午2:20:02
	 */
	@Override
    public Page queryPoorStudentInfoList(int pageNo, int pageSize,PoorStudent poorStudent,Dic yearDic)
    {
		 List<Object> values = new ArrayList<Object>();
	     StringBuffer hql = new StringBuffer("from PoorStudentDifficultView p where 1=1");
//	    //删除状态为正常
//		 hql.append(" and p.difficultStudentInfo.delStatus.id=? ");
//		 values.add(Constants.STATUS_NORMAL_DICS.getId());
//		 //最终审核通过的状态
//		 hql.append(" and p.difficultStudentInfo.processStatus =?");
//		 values.add("PASS");
		 //按条件查询
		 if(poorStudent != null)
		 {
			 if(poorStudent.getDifficultStudentInfo()!=null && poorStudent.getDifficultStudentInfo().getStudent()!=null && StringUtils.isNotBlank(poorStudent.getDifficultStudentInfo().getStudent().getName()))
			 {   
				 hql.append(" and (p.studentInfo.name like ?)");
				 values.add("%" + poorStudent.getDifficultStudentInfo().getStudent().getName() + "%");
			 }
			 if(poorStudent.getDifficultStudentInfo()!=null && poorStudent.getDifficultStudentInfo().getStudent()!=null && StringUtils.isNotBlank(poorStudent.getDifficultStudentInfo().getStudent().getStuNumber()))
			 {
				 hql.append(" and p.studentInfo.stuNumber like ?");
				 values.add("%" + poorStudent.getDifficultStudentInfo().getStudent().getStuNumber() + "%");
			 }
			 if(poorStudent.getStrCollege()!= null && StringUtils.isNotBlank(poorStudent.getStrCollege().getId()))
			 {   
				 hql.append(" and p.studentInfo.college.id = ?");
				 values.add(poorStudent.getStrCollege().getId());
			 }
			 if(poorStudent.getStrMajor()!=null && StringUtils.isNotBlank(poorStudent.getStrMajor().getId()))
			 {
				 hql.append(" and p.studentInfo.major.id = ?");
				 values.add(poorStudent.getStrMajor().getId());
			 }
			 if(poorStudent.getStrClassId()!=null && StringUtils.isNotBlank(poorStudent.getStrClassId().getId()))
			 {
				 hql.append(" and p.studentInfo.classId.id = ?");
				 values.add(poorStudent.getStrClassId().getId());
			 }
			 if(poorStudent.getStatus()!=null && StringUtils.isNotBlank(poorStudent.getStatus().getId()))
			 {
				 hql.append(" and p.status.id = ?");
				 values.add(poorStudent.getStatus().getId());
			 }
			 if(poorStudent.getDifficultType()!=null && StringUtils.isNotBlank(poorStudent.getDifficultType().getId()))
			 {
				 hql.append(" and p.difficultType.id = ?");
				 values.add(poorStudent.getDifficultType().getId());
			 }
		 }

			 if(yearDic!=null && StringUtils.isNotBlank(yearDic.getId()))
			 {
				 hql.append(" and p.schoolYear.id = ?");
				 values.add(yearDic.getId());
			 }
	     if (values.size() == 0) {
		      return pagedQuery(hql.toString(), pageNo, pageSize, new Object[0]);
		 }
		      return pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
    }
    
	/**
	 * 
	* @Title: PoorStudentDaoImpl.java 
	* @Package com.uws.job.dao.impl
	* @Description:困难生列表查询
	* @author pc  
	* @date 2015-12-2 下午2:20:02
	 */
	@Override
    public List<DifficultStudentInfo> queryDiffStudentInfoList()
    {   
		List<DifficultStudentInfo> list = this.query(" from DifficultStudentInfo where delStatus.id = ? and processStatus= ? order by student.classId ",new Object[] {Constants.STATUS_NORMAL_DICS.getId(),"PASS"});
		if(list != null && list.size() > 0){
			return list;
		}else{
			return new ArrayList<DifficultStudentInfo>();
		}
    }
	
	/**
	 * 
	* @Title: PoorStudentDaoImpl.java 
	* @Package com.uws.job.dao.impl 
	* @Description:双困生审核列表
	* @author pc  
	* @date 2015-12-2 下午2:20:02
	 */
	@Override
	public Page queryApprovePoorStudentInfoList(int pageNo, int pageSize,PoorStudent poorStudent,Dic yearDic)
	{
		 List<Object> values = new ArrayList<Object>();
		 StringBuffer hql = new StringBuffer(" from PoorStudent p where 1=1");
	     //删除状态为正常
		 //hql.append(" and e.delStatus.id=? ");
		 //values.add(Constants.STATUS_NORMAL_DICS.getId());
		 //最终审核通过的状态
		 hql.append(" and (p.status.id =? or p.status.id =? or p.status.id =? )");
		 values.add(Constants.STATUS_SUBMIT_DIC.getId());
		 values.add(Constants.STATUS_PASS_DIC.getId());
		 values.add(Constants.STATUS_UNPASS_DIC.getId());
		 if(poorStudent!=null)
		 {
			 if(poorStudent.getDifficultStudentInfo()!=null && poorStudent.getDifficultStudentInfo().getStudent()!=null && StringUtils.isNotBlank(poorStudent.getDifficultStudentInfo().getStudent().getName()))
			 {
				 hql.append(" and p.studentInfo.name like ?");
				 values.add("%" + poorStudent.getDifficultStudentInfo().getStudent().getName() + "%");
			 }
			 if(poorStudent.getDifficultStudentInfo()!=null && poorStudent.getDifficultStudentInfo().getStudent()!=null && StringUtils.isNotBlank(poorStudent.getDifficultStudentInfo().getStudent().getStuNumber()))
			 {
				 hql.append(" and p.studentInfo.stuNumber like ?");
				 values.add("%" + poorStudent.getDifficultStudentInfo().getStudent().getStuNumber() + "%");
			 }
			 if(poorStudent.getDifficultStudentInfo()!=null && poorStudent.getDifficultStudentInfo().getStudent()!=null && poorStudent.getDifficultStudentInfo().getStudent().getCollege()!=null && StringUtils.isNotBlank(poorStudent.getDifficultStudentInfo().getStudent().getCollege().getId()))
			 {
				 hql.append(" and p.studentInfo.college.id = ?");
				 values.add(poorStudent.getDifficultStudentInfo().getStudent().getCollege().getId());
			 }
			 if(poorStudent.getDifficultStudentInfo()!=null && poorStudent.getDifficultStudentInfo().getStudent()!=null && poorStudent.getDifficultStudentInfo().getStudent().getMajor()!=null && StringUtils.isNotBlank(poorStudent.getDifficultStudentInfo().getStudent().getMajor().getId()))
			 {
				 hql.append(" and p.studentInfo.major.id = ?");
				 values.add(poorStudent.getDifficultStudentInfo().getStudent().getMajor().getId());
			 }
			 if(poorStudent.getDifficultStudentInfo()!=null && poorStudent.getDifficultStudentInfo().getStudent()!=null && poorStudent.getDifficultStudentInfo().getStudent().getClassId()!=null && StringUtils.isNotBlank(poorStudent.getDifficultStudentInfo().getStudent().getClassId().getId()))
			 {
				 hql.append(" and p.studentInfo.classId.id = ?");
				 values.add(poorStudent.getDifficultStudentInfo().getStudent().getClassId().getId());
			 }
			 if(poorStudent.getDifficultStudentInfo()!=null && poorStudent.getDifficultStudentInfo().getSchoolYear()!=null && StringUtils.isNotBlank(poorStudent.getDifficultStudentInfo().getSchoolYear().getId()))
			 {
				 hql.append(" and p.schoolYear.id = ?");
				 values.add(poorStudent.getDifficultStudentInfo().getSchoolYear().getId());
			 }
			 if(poorStudent.getDifficultType()!=null && StringUtils.isNotBlank(poorStudent.getDifficultType().getId()))
			 {
				 hql.append(" and p.difficultType.id = ?");
				 values.add(poorStudent.getDifficultType().getId());
			 }
			 // 审核状态
			if (poorStudent.getStatus()!=null && !StringUtils.isEmpty(poorStudent.getStatus().getName())) 
			{
				{
					 hql.append(" and p.status.code = ?");
					 values.add(poorStudent.getStatus().getName());
				}
			}
		 }
			 if(yearDic!=null && StringUtils.isNotBlank(yearDic.getId()))
			 {
				 hql.append(" and p.schoolYear.id = ?");
				 values.add(yearDic.getId());
			 }
		 hql.append(" order by p.updateTime desc");
	     if (values.size() == 0) {
		      return pagedQuery(hql.toString(), pageNo, pageSize, new Object[0]);
		 }
		      return pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}
	
	/**
	 * 
	* @Title: PoorStudentDaoImpl.java 
	* @Package com.uws.job.dao.impl
	* @Description:双困生查询列表信息审核通过的信息
	* @author pc  
	* @date 2015-12-2 下午2:20:02
	 */
	@Override
	public Page queryPassPoorStudentInfoList(int pageNo, int pageSize,PoorStudent poorStudent,Dic yearDic,String currentStudentId)
	{
		 List<Object> values = new ArrayList<Object>();
		 StringBuffer hql = new StringBuffer(" from PoorStudent p where 1=1");
		 //最终审核通过的状态
		 hql.append(" and p.status.id = ? ");
		 values.add(Constants.STATUS_PASS_DIC.getId());
		 if(StringUtils.isNotBlank(currentStudentId))
		 {
			 hql.append(" and p.studentInfo.id = ?");
			 values.add(currentStudentId);
		 }
		 if(poorStudent!=null)
		 {
			 if(poorStudent.getDifficultStudentInfo()!=null && poorStudent.getDifficultStudentInfo().getStudent()!=null && StringUtils.isNotBlank(poorStudent.getDifficultStudentInfo().getStudent().getName()))
			 {
				 hql.append(" and p.studentInfo.name like ?");
				 values.add("%" + poorStudent.getDifficultStudentInfo().getStudent().getName() + "%");
			 }
			 if(poorStudent.getDifficultStudentInfo()!=null && poorStudent.getDifficultStudentInfo().getStudent()!=null && StringUtils.isNotBlank(poorStudent.getDifficultStudentInfo().getStudent().getStuNumber()))
			 {
				 hql.append(" and p.studentInfo.stuNumber like ?");
				 values.add("%" + poorStudent.getDifficultStudentInfo().getStudent().getStuNumber() + "%");
			 }
			 if(poorStudent.getStrCollege()!= null && StringUtils.isNotBlank(poorStudent.getStrCollege().getId()))
			 {   
				 hql.append(" and p.studentInfo.college.id = ?");
				 values.add(poorStudent.getStrCollege().getId());
			 }
			 if(poorStudent.getStrMajor()!=null && StringUtils.isNotBlank(poorStudent.getStrMajor().getId()))
			 {
				 hql.append(" and p.studentInfo.major.id = ?");
				 values.add(poorStudent.getStrMajor().getId());
			 }
			 if(poorStudent.getStrClassId()!=null && StringUtils.isNotBlank(poorStudent.getStrClassId().getId()))
			 {
				 hql.append(" and p.studentInfo.classId.id = ?");
				 values.add(poorStudent.getStrClassId().getId());
			 }
			 if(poorStudent.getDifficultType()!=null && StringUtils.isNotBlank(poorStudent.getDifficultType().getId()))
			 {
				 hql.append(" and p.difficultType.id = ?");
				 values.add(poorStudent.getDifficultType().getId());
			 }
		 }
			if(yearDic!=null && DataUtil.isNotNull(yearDic.getId())){
				hql.append(" and p.schoolYear.id = ?");
				 values.add(yearDic.getId());
			}
		 hql.append(" order by p.updateTime desc");
	     if (values.size() == 0) {
		      return pagedQuery(hql.toString(), pageNo, pageSize, new Object[0]);
		 }
		      return pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}
	
	/**
	 * 
	* @Title: PoorStudentDaoImpl.java 
	* @Package com.uws.job.dao.impl
	* @Description:根据学院统计查询
	* @author pc  
	* @date 2015-12-2 下午2:25:01
	 */
	@Override
    public List<PoorStudentCollegeView> queryPoorStudentByCollege(PoorStudent poorStudent,Dic yearDic)
    {
		String hql = "from PoorStudentCollegeView t where 1 = 1 ";
		Map<String,Object> params = new HashMap<String, Object>();
		if(DataUtil.isNotNull(poorStudent)){
			if(DataUtil.isNotNull(poorStudent.getDifficultStudentInfo()) && DataUtil.isNotNull(poorStudent.getDifficultStudentInfo().getStudent()) && DataUtil.isNotNull(poorStudent.getDifficultStudentInfo().getStudent().getCollege()) && DataUtil.isNotNull(poorStudent.getDifficultStudentInfo().getStudent().getCollege().getId())){
				hql += " and t.college.id = :collegeId";
				params.put("collegeId", poorStudent.getDifficultStudentInfo().getStudent().getCollege().getId());
			}
		}
		if(yearDic!=null && DataUtil.isNotNull(yearDic.getId())){
			hql += " and t.schoolYear.id = :schoolYearId";
			params.put("schoolYearId", yearDic.getId());
		}
		return this.query(hql, params);
    }
    
	/**
	 * 
	* @Title: PoorStudentDaoImpl.java 
	* @Package com.uws.job.dao.impl
	* @Description:根据专业统计查询
	* @author pc  
	* @date 2015-12-2 下午2:25:01
	 */
	@Override
    public List<PoorStudentMajorView> queryPoorStudentByByMajor(PoorStudent poorStudent,Dic nowSchoolYear)
    {
		String hql = "from PoorStudentMajorView t where 1 =1 ";
		Map<String,Object> params = new HashMap<String, Object>();
		if(DataUtil.isNotNull(poorStudent)){
			if(poorStudent.getDifficultStudentInfo()!=null && DataUtil.isNotNull(poorStudent.getDifficultStudentInfo().getStudent())){
				if(DataUtil.isNotNull(poorStudent.getDifficultStudentInfo().getStudent().getMajor()) && DataUtil.isNotNull(poorStudent.getDifficultStudentInfo().getStudent().getMajor().getId())){
					hql += " and t.major.id = :majorId";
					params.put("majorId", poorStudent.getDifficultStudentInfo().getStudent().getMajor().getId());
				}
				
				if(DataUtil.isNotNull(poorStudent.getDifficultStudentInfo().getStudent().getCollege()) && DataUtil.isNotNull(poorStudent.getDifficultStudentInfo().getStudent().getCollege().getId())){
					hql += " and t.major.collage.id = :collegeId";
					params.put("collegeId", poorStudent.getDifficultStudentInfo().getStudent().getCollege().getId());
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
	* @Title: PoorStudentDaoImpl.java 
	* @Package com.uws.job.dao.impl
	* @Description:根据班级统计查询
	* @author pc  
	* @date 2015-12-2 下午2:25:01
	 */
	@Override
    public List<PoorStudentClassView> queryPoorStudentByClass(PoorStudent poorStudent,Dic nowSchoolYear)
    {
		String hql = "from PoorStudentClassView t where 1 = 1 ";
		Map<String,Object> params = new HashMap<String, Object>();
		if(DataUtil.isNotNull(poorStudent)){
			if(poorStudent.getDifficultStudentInfo()!=null && DataUtil.isNotNull(poorStudent.getDifficultStudentInfo().getStudent())){
				
				if(DataUtil.isNotNull(poorStudent.getDifficultStudentInfo().getStudent().getClassId()) && DataUtil.isNotNull(poorStudent.getDifficultStudentInfo().getStudent().getClassId().getId())){
					hql += " and t.classModel.id = :classId";
					params.put("classId", poorStudent.getDifficultStudentInfo().getStudent().getClassId().getId());
				}
				
				if(DataUtil.isNotNull(poorStudent.getDifficultStudentInfo().getStudent().getMajor()) && DataUtil.isNotNull(poorStudent.getDifficultStudentInfo().getStudent().getMajor().getId())){
					hql += " and t.classModel.major.id = :majorId";
					params.put("majorId", poorStudent.getDifficultStudentInfo().getStudent().getMajor().getId());
				}
				
				if(DataUtil.isNotNull(poorStudent.getDifficultStudentInfo().getStudent().getCollege()) && DataUtil.isNotNull(poorStudent.getDifficultStudentInfo().getStudent().getCollege().getId())){
					hql += " and t.classModel.major.collage.id = :collegeId";
					params.put("collegeId", poorStudent.getDifficultStudentInfo().getStudent().getCollege().getId());
				}
			}
		}
		if(nowSchoolYear!=null && DataUtil.isNotNull(nowSchoolYear.getId())){
			hql += " and t.schoolYear.id = :schoolYearId";
			params.put("schoolYearId", nowSchoolYear.getId());
		}
		return this.query(hql, params);
    }
	
	
	@Override
	public boolean isExistStudent(String id, String studentId, String schoolYear)
	{
		@SuppressWarnings("unchecked")
        List<PoorStudent> list = query("from PoorStudent p where p.studentInfo.id=? and p.schoolYear.id=? ", new Object[] {studentId,schoolYear});
	     boolean b = false;
	     if ((list != null) && (list.size() > 0)) {
	       for (PoorStudent poorStudent : list) {
	         if (!poorStudent.getId().equals(id)) {
	           b = true;
	         }
	       }
	     }
	     return b;
	}
	
    
}
