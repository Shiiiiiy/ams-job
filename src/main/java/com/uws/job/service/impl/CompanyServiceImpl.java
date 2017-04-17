package com.uws.job.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.CompanyInfo;
import com.uws.job.dao.ICompanyDao;
import com.uws.job.service.ICompanyService;
import com.uws.job.util.Constants;
import com.uws.user.model.User;

/**
 * @className CompanyServiceImpl.java
 * @package com.uws.job.service.impl
 * @description
 * @date 2015-10-21  下午2:48:10
 */
@Service
public class CompanyServiceImpl extends BaseServiceImpl implements ICompanyService {

	@Autowired
	private ICompanyDao companyDao;
	
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
		return this.companyDao.queryCompanyInfoPage(companyInfoVO, dEFAULT_PAGE_SIZE, pageNo,queryType,collegeId);
	}
	/**
	 * 通过ID查询CompanyInfo
	 * @param id
	 * @return
	 */
	@Override
	public CompanyInfo queryCompanyInfoById(String id) {
		return (CompanyInfo) this.companyDao.get(CompanyInfo.class, id);
	}
	/**
	 * 更新CompanyInfo
	 * @param companyInfoPO
	 */
	@Override
	public void updateCompanyInfo(CompanyInfo companyInfoPO) {
		this.companyDao.update(companyInfoPO);
	}
	/**
	 * 添加CompanyInfo
	 * @param companyInfoPO
	 */
	@Override
	public void saveCompanyInfo(CompanyInfo companyInfoPO) {
		this.companyDao.save(companyInfoPO);
	}
	public List<CompanyInfo> queryCompanyInfoByCond(CompanyInfo companyInfo){
		return this.companyDao.queryCompanyInfoByCond(companyInfo);
	}
//	/**
//	 * 查找companyInfos数据是否在数据库中已经存在
//	 * @param companyInfos
//	 * @return
//	 */
//	@Override
//	public List<CompanyInfo[]> compareData(List<CompanyInfo> companyInfos, String collegeId) {
////		List<CompanyInfo> exitsCompanyInfo = this.companyDao.queryCompanyInfoCode(null, collegeId);
//		List<CompanyInfo[]> exitsCompanyInfos = new ArrayList<CompanyInfo[]>();
////		for (CompanyInfo e : exitsCompanyInfo) {
////			for (CompanyInfo c : companyInfos) {
////				if(c.getCompanyCode().equals(e.getCompanyCode())){
////					exitsCompanyInfos.add(new CompanyInfo[]{e,c});
////				}
////			}
////		}
//		return null;
//	}
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
//	@Override
//	public void importData(List<CompanyInfo[]> exitCompanyinfos, String filePath, String compareId, String collegeId, String userId) throws OfficeXmlFileException, IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException, Exception {
//		Map<String, CompanyInfo> map = new HashMap<String, CompanyInfo>();
//		for (CompanyInfo[] companyInfos : exitCompanyinfos) {
////			map.put(companyInfos[0].getCompanyCode(), companyInfos[0]);
//		}
//		ImportUtil iu = new ImportUtil();
//		List<CompanyInfo> excelCompanyInfos = iu.getDataList(filePath, "importCompanyInfo", null, CompanyInfo.class);
//		for (CompanyInfo excelCompanyInfo : excelCompanyInfos) {
////			if(map.containsKey(excelCompanyInfo.getCompanyCode())){//判断是否在数据库中存在
////				if(!compareId.contains(excelCompanyInfo.getCompanyCode())){//存在compareId中的compareInfo将会被忽略
////					CompanyInfo companyInfoPO = map.get(excelCompanyInfo.getCompanyCode());
////					BeanUtils.copyProperties(excelCompanyInfo,companyInfoPO ,new String[]{"id","college","schoolYear","delState","createTime","creator"});
////					this.companyDao.update(companyInfoPO);
////				}
////			}else{
//				Org college = new Org();
////				college.setId(collegeId);
////				excelCompanyInfo.setCollege(college);
////				excelCompanyInfo.setSchoolYear(SchoolYearUtil.getYearDic());
////				excelCompanyInfo.setDelState(Constants.STATUS_NORMAL_DICS);
////				excelCompanyInfo.setCreator(new User(userId));
////				this.companyDao.save(excelCompanyInfo);
////			}
//		}
//	}
	/**
	 * 批量导入保存更新
	 * @param companyInfos
	 */
	@Override
	public void saveOrUpdateCompanyInfoList(List<CompanyInfo> companyInfos,User creator) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		for (CompanyInfo companyInfo : companyInfos) {
			List<CompanyInfo> exits = this.companyDao.queryCompanyInfoByCond(companyInfo);
			if (exits == null || exits.size() == 0) {//不存在重复数据
				companyInfo.setDelState(Constants.STATUS_NORMAL_DICS);
				companyInfo.setCreator(creator);
				try {//此异常已在rule中处理了，再此无须再处理
					companyInfo.setUpdateDate(dateFormat.parse(companyInfo.getUpdateDateText()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				this.companyDao.save(companyInfo);
			}else{
				CompanyInfo companyInfoPO = exits.get(0);
				BeanUtils.copyProperties(companyInfo, companyInfoPO,new String[]{"id","college","companyName","companyProerty","delState","createTime","creator"});
				try {
					companyInfoPO.setUpdateDate(dateFormat.parse(companyInfo.getUpdateDateText()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				this.companyDao.update(companyInfoPO);
			}
		}
	}
}
