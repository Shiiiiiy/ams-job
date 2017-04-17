package com.uws.job.dao;

import java.util.List;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.CompanyInfo;

/**
 * 
 * @className ICompanyDao.java
 * @package com.uws.job.dao
 * @description
 * @date 2015-10-21  下午2:45:15
 */
public interface ICompanyDao extends IBaseDao {
	/**
	 * 分页查询CompanyInfo
	 * @param companyInfoVO
	 * @param dEFAULT_PAGE_SIZE
	 * @param pageNo
	 * @param queryType
	 * @return
	 */
	Page queryCompanyInfoPage(CompanyInfo companyInfoVO, int dEFAULT_PAGE_SIZE, int pageNo,int queryType,String collegeId);

	/**
	 * 查询
	 * @param companyInfo
	 * @return
	 */
	public List<CompanyInfo> queryCompanyInfoByCond(CompanyInfo companyInfo);
}
