package com.uws.job.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.RecruitModel;
import com.uws.job.dao.IRecruitDao;

/**
 * 
* @ClassName: RecruitDaoImpl 
* @Description: (报到证实现类) 
* @author 联合永道
* @date 2015-11-2 下午2:08:33 
*
 */
@Repository("recruitDao")
public class RecruitDaoImpl extends BaseDaoImpl implements IRecruitDao {
	
	/**
	 * 查询当前学生的本学年的申请列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RecruitModel> applyList(String year, String collegeId) {
		
		List<Object> values = new ArrayList<Object>();
	    StringBuffer hql = new StringBuffer(" from RecruitModel  where year.id = ?  and college.id = ?");
	    values.add(year);
	    values.add(collegeId);
	    
	    return query(hql.toString(), values.toArray());
	}
	
	@Override
	public Page queryRecruitList(int pageNo, int pageSize, RecruitModel recruit) {
		StringBuffer hql = new StringBuffer("from RecruitModel where 1=1");
		List<Object> values = new ArrayList<Object>();
		    
		if(recruit != null){
			if(recruit.getYear() != null && StringUtils.isNotEmpty(recruit.getYear().getId()) ) {
				hql.append(" and year = ? ");
			    values.add(recruit.getYear());
			}
		    if(recruit.getCollege() != null && StringUtils.isNotEmpty(recruit.getCollege().getId())){
				hql.append(" and college = ? ");
				values.add(recruit.getCollege());
			}
		}
		    
		hql.append(" order by year desc ");
		
		if(values.size() == 0){
		    return this.pagedQuery(hql.toString(), pageNo, pageSize);
		} else{
		    return this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
		}
	}
	
    /**
     * 描述信息: TODO (统计就业协议总的人数)
     * @return
     * @see com.uws.job.dao.IRecruitDao#countRecruit()
     */
    public long countRecruit() {
		String hql = "select count(*) from RecruitModel where 1 = 1";
		return this.queryCount(hql, new Object[]{});
	}

  
}

