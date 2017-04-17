package com.uws.job.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.job.AgreementModel;
import com.uws.domain.job.AgreementStatisticsModel;
import com.uws.job.dao.IAgreementDao;

/**
 * 
* @ClassName: AgreementDaoImpl 
* @Description: (就业协议实现类) 
* @author 联合永道
* @date 2015-10-10 下午5:01:21 
*
 */
@Repository("agreementDao")
public class AgreementDaoImpl extends BaseDaoImpl implements IAgreementDao {
	
    public Page queryAgreementList(Integer pageNo, Integer pageSize, AgreementModel agreement ){
		
	    StringBuffer hql = new StringBuffer("from AgreementModel where 1=1");
	    List<Object> values = new ArrayList<Object>();
	    
	    if(agreement != null){
	    	if(agreement.getEmploymentYear() != null && StringUtils.isNotEmpty(agreement.getEmploymentYear().getId()) ) {
		    	hql.append(" and employmentYear = ? ");
		        values.add(agreement.getEmploymentYear());
		    }
	    	if(agreement.getStatus() != null && StringUtils.isNotEmpty(agreement.getStatus().getId()) ) {
		    	hql.append(" and status = ? ");
		        values.add(agreement.getStatus());
		    }
	    	
	    	if(agreement.getCollege() != null && StringUtils.isNotEmpty(agreement.getCollege().getId())){
	    		hql.append(" and student.college = ? ");
		        values.add(agreement.getCollege());
	    	}
	    	if(agreement.getMajor() != null && StringUtils.isNotEmpty(agreement.getMajor().getId()) ){
	    		hql.append(" and student.major = ? ");
		        values.add(agreement.getMajor());
	    	}
	    	if(agreement.getClassId()!= null && StringUtils.isNotEmpty(agreement.getClassId().getId()) ){
	    		hql.append(" and student.classId = ? ");
		        values.add(agreement.getClassId());
	    	}
	    	
			if(StringUtils.isNotEmpty(agreement.getStuNumber())){
				hql.append(" and student.stuNumber like ? ");
				if(HqlEscapeUtil.IsNeedEscape(agreement.getStuNumber())) {
					values.add("%" + HqlEscapeUtil.escape(agreement.getStuNumber()) + "%");
					hql.append(HqlEscapeUtil.HQL_ESCAPE);
				} else{
					values.add("%" + agreement.getStuNumber() + "%");
				}
	    	}
			if(StringUtils.isNotEmpty(agreement.getStuName())){
				hql.append(" and student.name like ? ");
				if(HqlEscapeUtil.IsNeedEscape(agreement.getStuName())){
					values.add("%" + HqlEscapeUtil.escape(agreement.getStuName()) + "%");
					hql.append(HqlEscapeUtil.HQL_ESCAPE);
				} else{
					values.add("%" + agreement.getStuName() + "%");
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
	
    public Page queryAgreementApproveList(int pageNo, int pageSize, AgreementModel agreement ){
    	
		List<Object> values = new ArrayList<Object>();
	    StringBuffer hql = new StringBuffer("from AgreementModel where 1=1 and status != null ");
	    
	    if( agreement != null){
	    	//通过就业学年查询
	    	if ( agreement.getEmploymentYear() != null && StringUtils.isNotEmpty(agreement.getEmploymentYear().getId()) ) {
		    	hql.append(" and employmentYear = ? ");
		        values.add(agreement.getEmploymentYear());
		    }
	    	
	    	if ( agreement.getStatus() != null && StringUtils.isNotEmpty(agreement.getStatus().getId()) ) {
		    	hql.append(" and status = ? ");
		        values.add(agreement.getStatus());
		    }
    		//通过学院查询
	    	if (agreement.getCollege() != null && StringUtils.isNotEmpty(agreement.getCollege().getId())){
	    		hql.append(" and student.college = ? ");
		        values.add(agreement.getCollege());
	    	}
	    	//查询专业查询
	    	if (agreement.getMajor() != null && StringUtils.isNotEmpty(agreement.getMajor().getId()) ){
	    		hql.append(" and student.major = ? ");
		        values.add(agreement.getMajor());
	    	}
	    	//通过班级查询
	    	if (agreement.getClassId()!= null && StringUtils.isNotEmpty(agreement.getClassId().getId()) ){
	    		hql.append(" and student.classId = ? ");
		        values.add(agreement.getClassId());
	    	}
	    	//通过学号查询
			if (StringUtils.isNotEmpty(agreement.getStuNumber())) {
				hql.append(" and student.stuNumber like ? ");
				if (HqlEscapeUtil.IsNeedEscape(agreement.getStuNumber())) 
				{
					values.add("%" + HqlEscapeUtil.escape(agreement.getStuNumber()) + "%");
					hql.append(HqlEscapeUtil.HQL_ESCAPE);
				} else
					values.add("%" + agreement.getStuNumber() + "%");
			}
			//通过学生的姓名查询
			if (StringUtils.isNotEmpty(agreement.getStuName())) {
				hql.append(" and student.name like ? ");
				if (HqlEscapeUtil.IsNeedEscape(agreement.getStuName())) 
				{
					values.add("%" + HqlEscapeUtil.escape(agreement.getStuName()) + "%");
					hql.append(HqlEscapeUtil.HQL_ESCAPE);
				} else
					values.add("%" + agreement.getStuName() + "%");
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
     * @see com.uws.job.dao.IAgreementDao#countAgreement()
     */
    public long countAgreement() {
		String hql = "select count(*) from AgreementModel where 1 = 1";
		return this.queryCount(hql, new Object[]{});
	}

    /**
     * 
     * @Title: queryAgreementCollegeList
     * @Description: TODO(查询各个学院的就业协议补办率)
     * @param pageNo
     * @param pageSize
     * @param agreementStatistics
     * @return
     * @throws
     */
    public Page queryAgreementCollegeList(int pageNo, int pageSize, AgreementStatisticsModel agreementStatistics){
    	List<Object> values = new ArrayList<Object>();
    	StringBuffer hql = new StringBuffer(" from AgreementStatisticsCollegeModel where 1=1 ");
			
    	if(agreementStatistics != null ){
			if(agreementStatistics.getDicYear() !=null &&  StringUtils.isNotEmpty(agreementStatistics.getDicYear().getId()) ){
				hql.append("and employmentYear = ? ");
				values.add(agreementStatistics.getDicYear());
			}
    	
			if(agreementStatistics.getCollege() != null && StringUtils.isNotEmpty(agreementStatistics.getCollege().getId())){
				hql.append(" and college = ? ");
				values.add(agreementStatistics.getCollege());
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
	 * @Title: queryAgreementMajorList
	 * @Description: TODO(查询各个专业的就业协议补办率)
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
    public Page queryAgreementMajorList(int pageNo, int pageSize, AgreementStatisticsModel agreementStatistics){
    	List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer(" from AgreementStatisticsMajorModel where 1=1 ");
		
		if(agreementStatistics != null ){
			if(agreementStatistics.getDicYear() !=null && StringUtils.isNotEmpty(agreementStatistics.getDicYear().getId() )){
				hql.append("and employmentYear = ? ");
				values.add(agreementStatistics.getDicYear());
			}
			
			if(agreementStatistics.getMajor() != null && StringUtils.isNotEmpty(agreementStatistics.getMajor().getId())){
				hql.append(" and major = ? ");
				values.add(agreementStatistics.getMajor());
			} else if( agreementStatistics.getCollege() !=null && StringUtils.isNotEmpty(agreementStatistics.getCollege().getId()) ) {
				hql.append(" and major.collage = ? ");
				values.add(agreementStatistics.getCollege());
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
     * @Title: queryAgreementClassList
     * @Description: TODO(查询各个班级的就业协议补办率)
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
	public Page queryAgreementClassList(int pageNo, int pageSize, AgreementStatisticsModel agreementStatistics){
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer(" from AgreementStatisticsClassModel where 1=1 ");
			
		if(agreementStatistics != null ){
			if(agreementStatistics.getDicYear() !=null && StringUtils.isNotEmpty(agreementStatistics.getDicYear().getId() )){
				hql.append("and employmentYear = ? ");
				values.add(agreementStatistics.getDicYear());
			} 
			
			if(agreementStatistics.getClassId() !=null && StringUtils.isNotEmpty(agreementStatistics.getClassId().getId() )) {
				hql.append(" and classId = ? ");
				values.add(agreementStatistics.getClassId());
			}else if(agreementStatistics.getMajor() !=null && StringUtils.isNotEmpty(agreementStatistics.getMajor().getId() )) {
				hql.append(" and classId.major= ? ");
				values.add(agreementStatistics.getMajor());
			}else if(agreementStatistics.getCollege() !=null && StringUtils.isNotEmpty(agreementStatistics.getCollege().getId() )) {
				hql.append(" and classId.major.collage= ? ");
				values.add(agreementStatistics.getCollege());
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
    public AgreementModel queryAgreementByCode(String yearId,String agreementCode)
    {
		return (AgreementModel)this.queryUnique("from AgreementModel where employmentYear.id=? and agreementCode=?", new Object[] {yearId,agreementCode});
    }

	@Override
    public boolean checkCodeRepeat(String id, String employmentYear,String agreementCode)
    {
		@SuppressWarnings("unchecked")
        List<AgreementModel> list = query("from AgreementModel a where a.employmentYear.id=? and a.agreementCode=? ", new Object[] {employmentYear,agreementCode});
	     boolean b = false;
	     if ((list != null) && (list.size() > 0)) {
	       for (AgreementModel agreement : list) {
	         if (!agreement.getId().equals(id)) {
	           b = true;
	         }
	       }
	     }
	     return b;
    }
	
	
	
}

