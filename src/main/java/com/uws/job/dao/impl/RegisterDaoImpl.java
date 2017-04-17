package com.uws.job.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.job.AgreementModel;
import com.uws.domain.job.RegisterModel;
import com.uws.domain.job.RegisterStatisticsClassModel;
import com.uws.domain.job.RegisterStatisticsCollegeModel;
import com.uws.domain.job.RegisterStatisticsMajorModel;
import com.uws.domain.job.RegisterStatisticsModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.job.dao.IRegisterDao;
import com.uws.job.util.Constants;
import com.uws.sys.model.Dic;

/**
 * 
* @ClassName: RegisterDaoImpl 
* @Description: (报到证实现类) 
* @author 联合永道
* @date 2015-11-2 下午2:08:33 
*
 */
@Repository("registerDao")
public class RegisterDaoImpl extends BaseDaoImpl implements IRegisterDao {
	
	/**
	 * 描述信息: (查询报到证列表)
	 * @param pageNo
	 * @param pageSize
	 * @param register
	 * @return
	 * @see com.uws.job.dao.IRegisterDao#queryRegisterList(int, int, com.uws.domain.job.RegisterModel)
	 */
	@Override
	public Page queryRegisterList(int pageNo, int pageSize, RegisterModel register) {
		 StringBuffer hql = new StringBuffer("from RegisterModel where 1=1");
		    List<Object> values = new ArrayList<Object>();
		    
		    if(register != null){
		    	if(register.getEmploymentYear() != null && StringUtils.isNotEmpty(register.getEmploymentYear().getId()) ) {
			    	hql.append(" and employmentYear = ? ");
			        values.add(register.getEmploymentYear());
			    }
		    	if(register.getStatus() != null && StringUtils.isNotEmpty(register.getStatus().getId()) ) {
			    	hql.append(" and status = ? ");
			        values.add(register.getStatus());
			    }
		    	
		    	if(register.getCollege() != null && StringUtils.isNotEmpty(register.getCollege().getId())){
		    		hql.append(" and student.college = ? ");
			        values.add(register.getCollege());
		    	}
		    	if(register.getMajor() != null && StringUtils.isNotEmpty(register.getMajor().getId()) ){
		    		hql.append(" and student.major = ? ");
			        values.add(register.getMajor());
		    	}
		    	if(register.getClassId()!= null && StringUtils.isNotEmpty(register.getClassId().getId()) ){
		    		hql.append(" and student.classId = ? ");
			        values.add(register.getClassId());
		    	}
		    	
				if(StringUtils.isNotEmpty(register.getStuNumber())){
					hql.append(" and student.stuNumber like ? ");
					if(HqlEscapeUtil.IsNeedEscape(register.getStuNumber())) {
						values.add("%" + HqlEscapeUtil.escape(register.getStuNumber()) + "%");
						hql.append(HqlEscapeUtil.HQL_ESCAPE);
					} else{
						values.add("%" + register.getStuNumber() + "%");
					}
		    	}
				if(StringUtils.isNotEmpty(register.getStuName())){
					hql.append(" and student.name like ? ");
					if(HqlEscapeUtil.IsNeedEscape(register.getStuName())){
						values.add("%" + HqlEscapeUtil.escape(register.getStuName()) + "%");
						hql.append(HqlEscapeUtil.HQL_ESCAPE);
					} else{
						values.add("%" + register.getStuName() + "%");
					}
				}
		    }
		    
			hql.append(" order by student.stuNumber ");
		
		    if(values.size() == 0){
		    	return this.pagedQuery(hql.toString(), pageNo, pageSize);
		    } else{
		    	return this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
		    }
	}
	
	
    public Page queryRegisterApproveList(int pageNo, int pageSize, RegisterModel register ){
    	
		List<Object> values = new ArrayList<Object>();
	    StringBuffer hql = new StringBuffer("from RegisterModel  where 1=1 and status != null ");
	    
	    if(register != null){
	    	//通过就业学年查询
	    	if ( register.getEmploymentYear() != null && StringUtils.isNotEmpty(register.getEmploymentYear().getId()) ) {
		    	hql.append(" and employmentYear = ? ");
		        values.add(register.getEmploymentYear());
		    }
	    	
	    	if ( register.getStatus() != null && StringUtils.isNotEmpty(register.getStatus().getId()) ) {
		    	hql.append(" and status = ? ");
		        values.add(register.getStatus());
		    }
	    	
    		//通过学院查询
	    	if ( register.getCollege() != null && StringUtils.isNotEmpty( register.getCollege().getId())){
	    		hql.append(" and student.college = ? ");
		        values.add( register.getCollege());
	    	}
	    	//查询专业查询
	    	if ( register.getMajor() != null && StringUtils.isNotEmpty( register.getMajor().getId()) ){
	    		hql.append(" and student.major = ? ");
		        values.add( register.getMajor());
	    	}
	    	//通过班级查询
	    	if ( register.getClassId()!= null && StringUtils.isNotEmpty( register.getClassId().getId()) ){
	    		hql.append(" and student.classId = ? ");
		        values.add( register.getClassId());
	    	}
	    	//通过学号查询
			if (StringUtils.isNotEmpty(register.getStuNumber())) {
				hql.append(" and student.stuNumber like ? ");
				if (HqlEscapeUtil.IsNeedEscape(register.getStuNumber())) {
					values.add("%" + HqlEscapeUtil.escape( register.getStuNumber()) + "%");
					hql.append(HqlEscapeUtil.HQL_ESCAPE);
				} else{
					values.add("%" + register.getStuNumber() + "%");
				}
			}
			//通过学生的姓名查询
			if(StringUtils.isNotEmpty(register.getStuName())){
				hql.append(" and student.name like ? ");
				if(HqlEscapeUtil.IsNeedEscape( register.getStuName())){
					values.add("%" + HqlEscapeUtil.escape( register.getStuName()) + "%");
					hql.append(HqlEscapeUtil.HQL_ESCAPE);
				}else{
					values.add("%" + register.getStuName() + "%");
				}
			}
	    }
	   
        hql.append(" order by student ");
        
        if (values.size() == 0)
            return this.pagedQuery(hql.toString(), pageNo, pageSize);
        else
            return this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
    }
    
    /**
     * 描述信息: TODO (统计就业协议总的人数)
     * @return
     * @see com.uws.job.dao.IRegisterDao#countRegister()
     */
    public long countRegister() {
		String hql = "select count(*) from RegisterModel where 1 = 1";
		return this.queryCount(hql, new Object[]{});
	}

    /**
     * 
     * @Title: queryRegisterCollegeList
     * @Description: TODO(查询各个学院的报到证补办率)
     * @param pageNo
     * @param pageSize
     * @param registerStatistics
     * @return
     * @throws
     */
    public Page queryRegisterCollegeList(int pageNo, int pageSize, RegisterStatisticsModel registerStatistics){
    	List<Object> values = new ArrayList<Object>();
    	StringBuffer hql = new StringBuffer(" from RegisterStatisticsCollegeModel where 1=1 ");
			
    	if(registerStatistics != null ){
    		if ( registerStatistics.getDicYear() != null && StringUtils.isNotEmpty(registerStatistics.getDicYear().getId()) ) {
				hql.append("and employmentYear = ? ");
				values.add(registerStatistics.getDicYear());
			}
    	
			if(registerStatistics.getCollege() != null && StringUtils.isNotEmpty(registerStatistics.getCollege().getId())){
				hql.append(" and college = ? ");
				values.add(registerStatistics.getCollege());
			}
		}
    	
    	hql.append(" order by college asc ");
    	
	    if (values.size() == 0){
	    	return this.pagedQuery(hql.toString(), pageNo, pageSize);
	    } else{
	    	return this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	    }
	        
    }
    
	/**
	 * 
	 * @Title: queryRegisterMajorList
	 * @Description: TODO(查询各个专业的报到证补办率)
	 * @param pageNo
	 * @param pageSize
	 * @param yearId
	 * @param range
	 * @param collegeId
	 * @param majorId
	 * @param classId
	 * @return
	 * @throws
	 */
    public Page queryRegisterMajorList(int pageNo, int pageSize, RegisterStatisticsModel registerStatistics){
    	List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer(" from RegisterStatisticsMajorModel where 1=1 ");
		
		if(registerStatistics != null ){
			if ( registerStatistics.getDicYear() != null && StringUtils.isNotEmpty(registerStatistics.getDicYear().getId()) ) {
				hql.append("and employmentYear = ? ");
				values.add(registerStatistics.getDicYear());
			}
			
			if(registerStatistics.getMajor() != null && StringUtils.isNotEmpty(registerStatistics.getMajor().getId())){
				hql.append(" and major = ? ");
				values.add(registerStatistics.getMajor());
			} else if( registerStatistics.getCollege() !=null && StringUtils.isNotEmpty(registerStatistics.getCollege().getId()) ) {
				hql.append(" and major.collage = ? ");
				values.add(registerStatistics.getCollege());
			}
		}
			
		hql.append(" order by major asc ");
	
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		} else {
			return this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
		}
    	
    }
    
    /**
     * 
     * @Title: queryRegisterClassList
     * @Description: TODO(查询各个班级的报到证补办率)
     * @param pageNo
     * @param pageSize
     * @param yearId
     * @param range
     * @param collegeId
     * @param majorId
     * @param classId
     * @return
     * @throws
     */
	public Page queryRegisterClassList(int pageNo, int pageSize, RegisterStatisticsModel registerStatistics){
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer(" from RegisterStatisticsClassModel where 1=1 ");
			
		if(registerStatistics != null ){
			if ( registerStatistics.getDicYear() != null && StringUtils.isNotEmpty(registerStatistics.getDicYear().getId()) ) {
				hql.append("and employmentYear = ? ");
				values.add(registerStatistics.getDicYear());
			} 
			
			if(registerStatistics.getClassId() !=null && StringUtils.isNotEmpty(registerStatistics.getClassId().getId() )) {
				hql.append(" and classId = ? ");
				values.add(registerStatistics.getClassId());
			}else if(registerStatistics.getMajor() !=null && StringUtils.isNotEmpty(registerStatistics.getMajor().getId() )) {
				hql.append(" and classId.major= ? ");
				values.add(registerStatistics.getMajor());
			}else if(registerStatistics.getCollege() !=null && StringUtils.isNotEmpty(registerStatistics.getCollege().getId() )) {
				hql.append(" and classId.major.collage= ? ");
				values.add(registerStatistics.getCollege());
			}
		}	
		
		hql.append(" order by classId asc ");
		
	    if (values.size() == 0){
	    	return this.pagedQuery(hql.toString(), pageNo, pageSize);
	    }else {
        	return this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
        }
            
	}


	@Override
    public RegisterModel queryRegisterByCode(String yearId,String registerCode)
    {
		return (RegisterModel)this.queryUnique("from RegisterModel where employmentYear.id=? and registerCode=?", new Object[] {yearId,registerCode});
    }


	@Override
    public boolean checkCodeRepeat(String id, String employmentYear,String registerCode)
    {
		@SuppressWarnings("unchecked")
        List<RegisterModel> list = query("from RegisterModel r where r.employmentYear.id=? and r.registerCode=? ", new Object[] {employmentYear,registerCode});
	     boolean b = false;
	     if ((list != null) && (list.size() > 0)) {
	       for (RegisterModel register : list) {
	         if (!register.getId().equals(id)) {
	           b = true;
	         }
	       }
	     }
	     return b;
    }
	
	
	
	
	
	
}

