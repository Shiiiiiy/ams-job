package com.uws.job.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.common.service.IStudentCommonService;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.job.AgreementModel;
import com.uws.domain.job.AgreementStatisticsClassModel;
import com.uws.domain.job.AgreementStatisticsCollegeModel;
import com.uws.domain.job.AgreementStatisticsMajorModel;
import com.uws.domain.job.AgreementStatisticsModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.job.dao.IAgreementDao;
import com.uws.job.service.IAgreementService;
import com.uws.job.util.Constants;
import com.uws.sys.model.Dic;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.FileFactory;

@Service("com.uws.job.service.impl.agreementService")
public class AgreementServiceImpl implements IAgreementService {
	
	@Autowired
	private IAgreementDao agreementDao;
	@Autowired
	private IStudentCommonService studentCommonService;
	//附件工具类
	private FileUtil fileUtil=FileFactory.getFileUtil();
	/**
	 * @Title: findAgreementById
	 * @Description: TODO(通过Id查找就业协议)
	 * @param id
	 * @return
	 * @throws
	 */
	public AgreementModel findAgreementById(String id) {
		if(StringUtils.isNotEmpty(id))
			return (AgreementModel)agreementDao.get(AgreementModel.class, id);
		return null;
	}
	

	public void deleteAgreementById(String id) {
		if(id != null)
			this.agreementDao.deleteById(AgreementModel.class, id);
	}

	public void updateInfos(AgreementModel agreement, String[] fileId) {
		agreementDao.update(agreement);
		//上传的附件进行处理
		if (ArrayUtils.isEmpty(fileId)) {
			return;
		}
		for (String id : fileId){
			this.fileUtil.updateFormalFileTempTag(id, agreement.getId());
		}
       
	}
	
	/**
	 * 描述信息: TODO (更改就业协议状态)
	 * @param agreement
	 * @see com.uws.job.service.IAgreementService#update(com.uws.domain.job.AgreementModel)
	 */
	public void update(AgreementModel agreement) {
		agreementDao.update(agreement);
	}
	
	/**
	 * 描述信息: TODO (查询就业协议列表)
	 * @param pageNo
	 * @param pageSize
	 * @param agreement
	 * @return
	 * @see com.uws.job.service.IAgreementService#queryAgreementList(int, int, com.uws.domain.job.AgreementModel)
	 */
	@Override
	public Page queryAgreementList(int pageNo, int pageSize, AgreementModel agreement) {
		return agreementDao.queryAgreementList(pageNo, pageSize, agreement);
	}
	
	/**
	 * 描述信息: TODO (查询就业协议审核列表)
	 * @param pageNo
	 * @param pageSize
	 * @param agreement
	 * @return
	 * @see com.uws.job.service.IAgreementService#queryAgreementApproveList(int, int, com.uws.domain.job.AgreementModel)
	 */
	@Override
	public Page queryAgreementApproveList(int pageNo, int pageSize, AgreementModel agreement) {
		return agreementDao.queryAgreementApproveList(pageNo, pageSize, agreement);
	}
	
	/**
	 * 描述信息: TODO (导入初始的数据)
	 * @param list
	 * @throws ExcelException
	 * @see com.uws.job.service.IAgreementService#importOriginData(java.util.List)
	 */
	public void importOriginData(List<AgreementModel> list) throws ExcelException{
		for(AgreementModel agreement : list) {
			StudentInfoModel student = this.studentCommonService.queryStudentByStudentNo(agreement.getStuNumber());
			if (student != null){
				agreement.setStudent(student);
			} else {
				throw new ExcelException("数据库中，没有该生的学号信息") ;
			}
			this.agreementDao.save(agreement);
		}
	}
	
	/**
	 * @Title: compareData
	 * @Description: (导入之前的数据对比和数据库中已有的数据进行对比)
	 * @param list
	 * @return  返回重复的数据列
	 * @throws OfficeXmlFileException
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws ExcelException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws
	 */
	public List<Object[]> compareData(List<AgreementModel> list) throws OfficeXmlFileException, IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException {
		List<Object[]> compareList = new ArrayList<Object[]>();
		Object[] array = (Object[])null;
		long count = this.agreementDao.countAgreement();
		if(count != 0L) {
			for(int i = 0; i < count / 10 + 1L; i++) {
				Page page = this.agreementDao.queryAgreementList(i+1, 10, new AgreementModel());
				List<AgreementModel> infoList = (List<AgreementModel>)(page.getResult());
				for(AgreementModel info : infoList) {
					for(AgreementModel xls : list) {
						if((info.getStudent().getStuNumber()).equals(xls.getStuNumber())){
							array = new Object[]{info,xls};
							compareList.add(array);
							break;
						}
					}
				}
			}
		}
		return compareList;
	}
	
	/**
	 * 描述信息: TODO (导入最终的数据)
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
	 * @see com.uws.job.service.IAgreementService#importLastData(java.util.List, java.lang.String, java.lang.String)
	 */
	@Override
	public void importLastData(List<Object[]> list, String filePath, String compareId) throws OfficeXmlFileException, IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException, Exception {
		
		Map<String, AgreementModel> map = new HashMap<String, AgreementModel>();
		for(Object[] array : list) {
			AgreementModel info = (AgreementModel)array[0];
			map.put(info.getStudent().getStuNumber(), info);
		}
		ImportUtil iu = new ImportUtil();
		//Excel数据
		List<AgreementModel> infoList = iu.getDataList(filePath, "importAgreementCode", null, AgreementModel.class);     
		
		for(AgreementModel xls : infoList) {
			String flag = xls.getStuNumber();
			if(!map.containsKey(flag)) {
				StudentInfoModel student = this.studentCommonService.queryStudentByStudentNo(xls.getStuNumber());
				xls.setStudent(student);
				this.agreementDao.save(xls);
			} else{
				AgreementModel infoPo = (AgreementModel) map.get(flag);  
				//已有数据需要更新的记录
				if((StringUtils.isBlank(compareId)) || (!compareId.contains(infoPo.getId()))) {
					infoPo.setEmploymentYear(xls.getEmploymentYear());
					infoPo.setAgreementCode(xls.getAgreementCode());
					this.agreementDao.update(infoPo);
				}
			}
		}
		
	}

	/**
	 * 
	 * @Title: queryAgreementCollegeList
	 * 描述信息: 统计各个学院的就业协议补办率
	 * @param pageNo
	 * @param pageSize
	 * @param agreementStatistics
	 * @return
	 * @throws
	 */
	public Page queryAgreementCollegeList(int pageNo, int pageSize, AgreementStatisticsModel agreementStatistics) {
		return agreementDao.queryAgreementCollegeList(pageNo, pageSize, agreementStatistics);
	}
	
	/**
	  * 描述信息: 统计各个专业的就业协议补办率
	 * @param pageNo
	 * @param pageSize
	 * @param agreementStatistics
	 * @return
	 * @see com.uws.job.service.IAgreementService#queryAgreementMajorList(int, int, com.uws.domain.job.AgreementStatisticsModel)
	 */
	public Page queryAgreementMajorList(int pageNo, int pageSize, AgreementStatisticsModel agreementStatistics) {
		return agreementDao.queryAgreementMajorList(pageNo, pageSize, agreementStatistics);
	}	
		
	/**
	 * 描述信息: 统计各个班级的就业协议补办率
	 * @param pageNo
	 * @param pageSize
	 * @param agreementStatistics
	 * @return
	 * @see com.uws.job.service.IAgreementService#queryAgreementClassList(int, int, com.uws.domain.job.AgreementStatisticsModel)
	 */
	public Page queryAgreementClassList(int pageNo, int pageSize, AgreementStatisticsModel agreementStatistics) {
		return agreementDao.queryAgreementClassList(pageNo, pageSize, agreementStatistics);
	}
	
	@Override
	public AgreementModel queryAgreementByCode(String yearId,String agreementCode)
	{
	    return this.agreementDao.queryAgreementByCode(yearId,agreementCode);
	}
	
	@Override
	public boolean checkCodeRepeat(String id, String employmentYear,String agreementCode)
	{
	    return this.agreementDao.checkCodeRepeat(id,employmentYear,agreementCode);
	}

}
