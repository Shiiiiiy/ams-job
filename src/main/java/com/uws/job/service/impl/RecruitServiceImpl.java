package com.uws.job.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.common.service.IBaseDataService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.RecruitModel;
import com.uws.job.dao.IRecruitDao;
import com.uws.job.service.IRecruitService;

@Service("recruitService")
public class RecruitServiceImpl implements IRecruitService {
	
	@Autowired
	private IRecruitDao recruitDao;
	@Autowired
	private IBaseDataService baseDataService;
	
	public RecruitModel findRecruitById(String id) {
		if(StringUtils.isNotEmpty(id))
			return (RecruitModel) this.recruitDao.get(RecruitModel.class, id);
		return  null;
	}

	public void deleteRecruitById(String id) {
		if(StringUtils.isNotEmpty(id))
			this.recruitDao.deleteById(RecruitModel.class, id);
	}

	@Override
	public void update(RecruitModel recruit) {
		this.recruitDao.update(recruit);
	}
	
	/**
	 * 验证同一学年是否上报过招聘会信息
	 */
	@Override
	public String isApply( String collegeId, String year) 
	{
		List<RecruitModel> recruits = recruitDao.applyList(year, collegeId);
		int len = null == recruits ? 0 : recruits.size();
		if(len >0 ) 
			return "true";
		return "false";
   }
	
	
	
	/**
	 * 描述信息: TODO (查询所有学生的报到证信息)
	 * @param pageNo
	 * @param pageSize
	 * @param recruit
	 * @return
	 * @see com.uws.job.service.IRecruitService#queryRecruitList(int, int, com.uws.domain.job.RecruitModel)
	 */
	public Page queryRecruitList(int pageNo, int pageSize, RecruitModel recruit) {
		return recruitDao.queryRecruitList(pageNo, pageSize, recruit);
	}

	@Override
	public void save(RecruitModel recruit) {
		this.recruitDao.save(recruit);
	}
	
}
	

