package com.uws.job.dao;

import java.util.List;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.RecruitModel;

public interface IRecruitDao extends IBaseDao {
	
	/**
	 * 
	 * @Title: countRecruit
	 * @Description: TODO(统计数据库中报到证的条数 )
	 * @return
	 * @throws
	 */
	public long countRecruit();

	
	public Page queryRecruitList(int pageNo, int pageSize, RecruitModel recruit);

	/**
	 * @Title: applyList
	 * @Description: TODO(查看本学年是否上报过招聘会信息)
	 * @param year
	 * @return
	 * @throws
	 */
	List<RecruitModel> applyList(String year, String collegeId);

}
