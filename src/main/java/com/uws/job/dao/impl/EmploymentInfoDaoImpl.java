package com.uws.job.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;


import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.EmploymentInfo;
import com.uws.job.dao.IEmploymentInfoDao;
/**
* @ClassName: EmploymentInfoDaoImpl 
* @Description: 就业信息管理模块Dao实现类
* @author liuchen
* @date 2015-10-9 下午14:24:08 
*
*/
@Repository("com.uws.job.dao.impl.EmploymentInfoDaoImpl")
public class EmploymentInfoDaoImpl extends BaseDaoImpl implements IEmploymentInfoDao{
   
	/**
     * 
    * @Title: EmploymentInfoDaoImpl.java 
    * @Package com.uws.job.dao.impl;
    * @Description:就业信息维护查询实现Dao实现类
    * @author 
    * @date 2015-10-9 下午3:10:39
     */
	@Override
    public Page queryEmploymentInfoList(int pageNo, int pageSize,EmploymentInfo employmenInfo)
    {
		 List<Object> values = new ArrayList<Object>();
	     StringBuffer hql = new StringBuffer("from EmploymentInfo e where 1=1");
	     if(employmenInfo!=null)
	     {
	    	 if(employmenInfo.getStudent()!=null && StringUtils.isNotBlank(employmenInfo.getStudent().getName()))
	    	 {
	    		 hql.append(" and e.student.name like ?");
				 values.add("%" + employmenInfo.getStudent().getName() + "%");
	    	 }
	    	 if(employmenInfo.getStudent()!=null && StringUtils.isNotBlank(employmenInfo.getStudent().getStuNumber()))
	    	 {
	    		 hql.append(" and e.student.stuNumber like ?");
				 values.add("%" + employmenInfo.getStudent().getStuNumber() + "%");
	    	 }
	    	 if(employmenInfo.getStrCollege()!=null && StringUtils.isNotBlank(employmenInfo.getStrCollege().getId()))
	    	 {
	    		 hql.append(" and e.student.college.id = ?");
				 values.add(employmenInfo.getStrCollege().getId());
	    	 }
	    	 if(employmenInfo.getStrMajor()!=null && StringUtils.isNotBlank(employmenInfo.getStrMajor().getId()))
	    	 {
	    		 hql.append(" and e.student.major.id = ?");
				 values.add(employmenInfo.getStrMajor().getId());
	    	 }
	    	 if(employmenInfo.getStrClassId()!=null && StringUtils.isNotBlank(employmenInfo.getStrClassId().getId()))
	    	 {
	    		 hql.append(" and e.student.classId.id = ?");
				 values.add(employmenInfo.getStrClassId().getId());
	    	 }
	    	 if(employmenInfo.getDifficultType()!=null && StringUtils.isNotBlank(employmenInfo.getDifficultType().getId()))
	    	 {
	    		 hql.append(" and e.difficultType.id = ?");
				 values.add(employmenInfo.getDifficultType().getId());
	    	 }
	     }
		 hql.append(" order by e.updateTime desc");
	     if (values.size() == 0) {
		      return pagedQuery(hql.toString(), pageNo, pageSize, new Object[0]);
		 }
		      return pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
    }
	
	/**
	 * 总条数
	 */
	@Override
	public long getCount() {
		String sql = " select count(e.id) from EmploymentInfo e where 1=1 ";
		return ((Long)queryUnique(sql, new Object[0])).longValue();
	}
   
	
	@Override
	public Page pageQuery(int i, int pageSize) {
		String sql = "select e from EmploymentInfo e where 1=1 ";
		return pagedQuery(sql, i, pageSize, new Object[0]);
	}
	
	/**
	 * 
	 * @Description:验证重复添加
	 * @author LiuChen  
	 * @date 2015-12-9 下午3:56:08
	 */
	@Override
	public boolean isExistStudent(String id, String studentId)
	{
		@SuppressWarnings("unchecked")
        List<EmploymentInfo> list = query("from EmploymentInfo e where e.student.id=? ", new Object[] {studentId});
	     boolean b = false;
	     if ((list != null) && (list.size() > 0)) {
	       for (EmploymentInfo employmentInfo : list) {
	         if (!employmentInfo.getId().equals(id)) {
	           b = true;
	         }
	       }
	     }
	     return b;
	}
	
}
