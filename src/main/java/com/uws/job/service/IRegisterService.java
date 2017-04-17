package com.uws.job.service;

import java.io.IOException;
import java.util.List;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;

import com.uws.core.base.IBaseService;
import com.uws.core.excel.ExcelException;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.AgreementModel;
import com.uws.domain.job.RegisterStatisticsClassModel;
import com.uws.domain.job.RegisterStatisticsCollegeModel;
import com.uws.domain.job.RegisterStatisticsMajorModel;
import com.uws.domain.job.RegisterModel;
import com.uws.domain.job.RegisterModel;
import com.uws.domain.job.RegisterStatisticsModel;
import com.uws.domain.sponsor.OriginLoanModel;
import com.uws.sys.model.Dic;


public interface IRegisterService extends IBaseService {
	
	/**
	 * @return 
	 * 
	 * @Title: findRegisterById
	 * @Description: TODO(通过ID找到就业协议)
	 * @param id
	 * @return
	 * @throws
	 */
	public RegisterModel findRegisterById(String id);
	
	/**
	 * 
	 * @Title: deleteRegisterById
	 * @Description: TODO(删除报到证补办申请)
	 * @param id
	 * @throws
	 */
	public void deleteRegisterById(String id);

	/**
	 * @Title: queryRegisterApproveList
	 * @Description: TODO(报到证补办审核列表)
	 * @param pageNo
	 * @param pageSize
	 * @param register
	 * @return
	 * @throws
	 */
	public Page queryRegisterApproveList(int pageNo, int pageSize, RegisterModel register);
	
	/**
	 * @Title: queryRegisterList
	 * @Description: (查询报到证编号列表)
	 * @param pageNo
	 * @param pageSize
	 * @param register
	 * @return
	 * @throws
	 */
	public Page queryRegisterList(int pageNo, int pageSize, RegisterModel register);
	
	/**
	 * 
	 * @Title: update
	 * @Description: TODO(用于进行状态的更新)
	 * @param register
	 * @throws
	 */
	public void update(RegisterModel register);
	
	/**
	 * 
	 * @Title: importOriginData
	 * @Description: TODO(数据库中没有这些数据直接导入)
	 * @param list
	 * @throws ExcelException
	 * @throws
	 */
	public void importOriginData(List<RegisterModel> list) throws ExcelException;

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
	public List<Object[]> compareData(List<RegisterModel> paramList) throws OfficeXmlFileException, IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException;
	
	/**
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
	 * 
	 * @Title: queryRegisterCollegeList
	 * @Description: TODO(查询各个学院的报到证补办率)
	 * @param pageNo
	 * @param pageSize
	 * @param registerStatistics
	 * @return
	 * @throws
	 */
	public Page queryRegisterCollegeList(int pageNo, int pageSize, RegisterStatisticsModel registerStatistics);
	
	/**
	 * 
	 * @Title: queryRegisterMajorList
	 * @Description: TODO(查询各个专业的报到证补办率)
	 * @param pageNo
	 * @param pageSize
	 * @param registerStatistics
	 * @return
	 * @throws
	 */
	public Page queryRegisterMajorList(int pageNo, int pageSize, RegisterStatisticsModel registerStatistics);
	
	/**
	 * @Title: queryRegisterClassList
	 * @Description: TODO(查询各个班级的报到证补办率)
	 * @param pageNo
	 * @param pageSize
	 * @param registerStatistics
	 * @return
	 * @throws
	 */
	public Page queryRegisterClassList(int pageNo, int pageSize, RegisterStatisticsModel registerStatistics);

	public RegisterModel queryRegisterByCode(String yearId,String registerCode);

	public boolean checkCodeRepeat(String id, String employmentYear,
            String registerCode);

}
