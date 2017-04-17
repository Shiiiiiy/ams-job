package com.uws.job.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.job.CompanyInfo;
import com.uws.job.dao.ICompanyDao;
import com.uws.job.util.Constants;

/**
 * @className CompanyDaoImpl.java
 * @package com.uws.job.dao.impl
 * @description
 * @date 2015-10-21  下午2:46:00
 */
@Repository
public class CompanyDaoImpl extends BaseDaoImpl implements ICompanyDao {
	/**
	 * 分页查询CompanyInfo
	 * @param companyInfoVO
	 * @param dEFAULT_PAGE_SIZE
	 * @param pageNo
	 * @param queryType
	 * @return
	 */
	@Override
	public Page queryCompanyInfoPage(CompanyInfo companyInfoVO, int dEFAULT_PAGE_SIZE, int pageNo,int queryType,String collegeId) {
		
		String hql = "from CompanyInfo where delState.id = :delStateId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("delStateId", Constants.STATUS_NORMAL_DICS.getId());
		if(DataUtil.isNotNull(companyInfoVO)){
			if(DataUtil.isNotNull(companyInfoVO.getCollege()) && DataUtil.isNotNull(companyInfoVO.getCollege().getId())){
				if(queryType == 1 || queryType == 3){
					hql += " and college.id = :collegeId";
					params.put("collegeId", companyInfoVO.getCollege().getId());
				}
			}
			if(queryType == 2){
				hql += " and college.id in (:collegeIds)";
				String[] collegeIds = {collegeId,Constants.JOBOFFICEID};
				params.put("collegeIds", collegeIds);
			}
			if(DataUtil.isNotNull(companyInfoVO.getCompanyProerty()) && DataUtil.isNotNull(companyInfoVO.getCompanyProerty().getId())){
				hql += " and companyProerty.id = :companyProertyId";
				params.put("companyProertyId", companyInfoVO.getCompanyProerty().getId());
			}
			if(DataUtil.isNotNull(companyInfoVO.getCompanyName())){
				hql += " and companyName like :companyName";
				params.put("companyName", "%"+companyInfoVO.getCompanyName()+"%");
			}
		}
		hql += " order by college.id,updateDate desc";
		return this.pagedQuery(hql, params, dEFAULT_PAGE_SIZE, pageNo);
	}
	/**
	 * 通过公司组织机构代码查询CompanyInfo
	 * @param compantCode
	 * @return
	 */
	@Override
	public List<CompanyInfo> queryCompanyInfoByCond(CompanyInfo companyInfo) {
		String hql = "from CompanyInfo where delState.id = :delStateId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("delStateId", Constants.STATUS_NORMAL_DICS.getId());
		if(DataUtil.isNotNull(companyInfo)){
			if(DataUtil.isNotNull(companyInfo.getId())){
				hql += " and id != :id";
				params.put("id", companyInfo.getId());
			}
			if(DataUtil.isNotNull(companyInfo.getCollege()) && DataUtil.isNotNull(companyInfo.getCollege().getId())){
				hql += " and college.id = :collegeId";
				params.put("collegeId", companyInfo.getCollege().getId());
			}
			if(DataUtil.isNotNull(companyInfo.getCompanyName())){
				hql += " and companyName = :companyName";
				params.put("companyName", companyInfo.getCompanyName());
			}
			if(DataUtil.isNotNull(companyInfo.getCompanyProerty()) && DataUtil.isNotNull(companyInfo.getCompanyProerty().getId())){
				hql += " and companyProerty.id = :companyProertyId";
				params.put("companyProertyId", companyInfo.getCompanyProerty().getId());
			}
		}
		return this.query(hql, params);
	}
	
}
