package com.uws.job.service;

import java.io.IOException;
import java.util.List;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;

import com.uws.core.base.IBaseService;
import com.uws.core.excel.ExcelException;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.CompanyInfo;
import com.uws.user.model.User;

/**
 * 
 * @className ICompanyService.java
 * @package com.uws.job.service
 * @description
 * @date 2015-10-21  下午3:15:55
 */
public interface ICompanyService extends IBaseService {
	/**
	 * 分页查询CompanyInfo
	 * @param companyInfoVO
	 * @param dEFAULT_PAGE_SIZE
	 * @param pageNo
	 * @param queryType
	 * @return
	 */
	public Page queryCompanyInfoPage(CompanyInfo companyInfoVO, int dEFAULT_PAGE_SIZE, int pageNo,int queryType,String collegeId);
	/**
	 * 通过ID查询CompanyInfo
	 * @param id
	 * @return
	 */
	public CompanyInfo queryCompanyInfoById(String id);
	/**
	 * 更新CompanyInfo
	 * @param companyInfoPO
	 */
	public void updateCompanyInfo(CompanyInfo companyInfoPO);
	/**
	 * 添加CompanyInfo
	 * @param companyInfoPO
	 */
	public void saveCompanyInfo(CompanyInfo companyInfoPO);
	/**
	 * 通过公司组织机构代码查询CompanyInfo
	 * @param compantCode
	 * @return
	 */
	public List<CompanyInfo> queryCompanyInfoByCond(CompanyInfo companyInfo);
//	/**
//	 * 查找companyInfos数据是否在数据库中已经存在
//	 * @param companyInfos
//	 * @return
//	 */
//	public List<CompanyInfo[]> compareData(List<CompanyInfo> companyInfos,String orgId);
//	/**
//	 * 导入重复数据
//	 * @param exitCompanyinfos
//	 * @param filePath
//	 * @param compareId
//	 * @throws Exception 
//	 * @throws ClassNotFoundException 
//	 * @throws InstantiationException 
//	 * @throws ExcelException 
//	 * @throws IllegalAccessException 
//	 * @throws IOException 
//	 * @throws OfficeXmlFileException 
//	 */
//	public void importData(List<CompanyInfo[]> exitCompanyinfos, String filePath, String compareId, String collegeId, String userId) throws OfficeXmlFileException, IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException, Exception;
	/**
	 * 批量导入保存更新
	 * @param companyInfos
	 */
	public void saveOrUpdateCompanyInfoList(List<CompanyInfo> companyInfos,User creator);

}
