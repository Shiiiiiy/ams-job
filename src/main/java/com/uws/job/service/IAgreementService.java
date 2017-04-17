package com.uws.job.service;

import java.io.IOException;
import java.util.List;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;

import com.uws.core.base.IBaseService;
import com.uws.core.excel.ExcelException;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.job.AgreementModel;
import com.uws.domain.job.AgreementStatisticsClassModel;
import com.uws.domain.job.AgreementStatisticsCollegeModel;
import com.uws.domain.job.AgreementStatisticsMajorModel;
import com.uws.domain.job.AgreementStatisticsModel;
import com.uws.sys.model.Dic;


public interface IAgreementService extends IBaseService {
	
	/**
	 * 
	 * @Title: findAgreementById
	 * @Description: TODO(通过ID找到就业协议)
	 * @param id
	 * @return
	 * @throws
	 */
	public AgreementModel findAgreementById(String id);
	
	 /**
     * 
     * @Title: compareData
     * @Description: 比较数据重复
     * @param paramList
     * @return
     * @throws OfficeXmlFileException
     * @throws IOException
     * @throws IllegalAccessException
     * @throws ExcelException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws
     */
	public List<Object[]> compareData(List<AgreementModel> paramList) throws OfficeXmlFileException, IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException;
	
	/**
	 * 
	 * @Title: importOriginData
	 * @Description: TODO(数据库中没有这些数据直接导入)
	 * @param list
	 * @throws ExcelException
	 * @throws
	 */
	public void importOriginData(List<AgreementModel> list) throws ExcelException;
	
	/**
	 * 
	 * @Title: importLastData
	 * @Description: TODO(有重复数据，对比之后，更新数据成为最终数据)
	 * @param list
	 * @param filePath
	 * @param compareId
	 * @throws OfficeXmlFileException
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws ExcelException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws Exception
	 * @throws
	 */
	public void importLastData(List<Object[]> list, String filePath, String compareId) throws OfficeXmlFileException, IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException,Exception;
	
	/**
	 * @Title: queryAgreementList
	 * @Description: TODO(查询就业协议书列表)
	 * @param pageNo
	 * @param pageSize
	 * @param agreement
	 * @return
	 * @throws
	 */
	public Page queryAgreementList(int pageNo, int pageSize, AgreementModel agreement );
	
	public void update(AgreementModel agreement);
	/**
	 * 
	 * @Title: update
	 * @Description: TODO(修改就业协议的信息)
	 * @param agreement
	 * @throws
	 */
	public void updateInfos(AgreementModel agreement, String[] fileId);
	
	/**
	 * 
	 * @Title: deleteAgreementById
	 * @Description: TODO(删除就业协议信息)
	 * @param id
	 * @throws
	 */
	public void deleteAgreementById(String id);

	/**
	 * 
	 * @Title: queryAgreementApproveList
	 * @Description: (查询就业协议审核列表)
	 * @param pageNo
	 * @param pageSize
	 * @param agreement
	 * @return
	 * @throws
	 */
	public Page queryAgreementApproveList(int pageNo, int pageSize, AgreementModel agreement);
	
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
	public Page queryAgreementCollegeList(int pageNo, int pageSize, AgreementStatisticsModel agreementStatistics);
	
	/**
	 * 
	 * @Title: queryAgreementMajorList
	 * @Description: TODO(查询各个专业的就业协议补办率)
	 * @param pageNo
	 * @param pageSize
	 * @param agreementStatistics
	 * @return
	 * @throws
	 */
	public Page queryAgreementMajorList(int pageNo, int pageSize, AgreementStatisticsModel agreementStatistics);
	
	/**
	 * @Title: queryAgreementClassList
	 * @Description: TODO(查询各个班级的就业协议补办率)
	 * @param pageNo
	 * @param pageSize
	 * @param agreementStatistics
	 * @return
	 * @throws
	 */
	public Page queryAgreementClassList(int pageNo, int pageSize, AgreementStatisticsModel agreementStatistics);

	public AgreementModel queryAgreementByCode(String yearId,String agreementCode);

	public boolean checkCodeRepeat(String id, String employmentYear,String agreementCode);


}
