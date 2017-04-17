package com.uws.job.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.common.service.IStudentCommonService;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.AgreementModel;
import com.uws.domain.job.RegisterStatisticsClassModel;
import com.uws.domain.job.RegisterStatisticsCollegeModel;
import com.uws.domain.job.RegisterStatisticsMajorModel;
import com.uws.domain.job.RegisterModel;
import com.uws.domain.job.RegisterModel;
import com.uws.domain.job.RegisterStatisticsModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.job.dao.IRegisterDao;
import com.uws.job.service.IRegisterService;
import com.uws.job.util.Constants;
import com.uws.sys.model.Dic;

@Service("com.uws.job.service.impl.registerService")
public class RegisterServiceImpl implements IRegisterService {
	
	@Autowired
	private IRegisterDao registerDao;
	@Autowired
	private IStudentCommonService studentCommonService;
	
	public RegisterModel findRegisterById(String id) {
		if(StringUtils.isNotEmpty(id))
			return (RegisterModel) this.registerDao.get(RegisterModel.class, id);
		return  null;
	}

	public void deleteRegisterById(String id) {
		if(StringUtils.isNotEmpty(id))
			this.registerDao.deleteById(RegisterModel.class, id);
	}

	/**
	 * 描述信息: TODO (查询报到证审核列表)
	 * @param pageNo
	 * @param pageSize
	 * @param register
	 * @return
	 * @see com.uws.job.service.IRegisterService#queryRegisterApproveList(int, int, com.uws.domain.job.RegisterModel)
	 */
	@Override
	public Page queryRegisterApproveList(int pageNo, int pageSize, RegisterModel register) {
		return registerDao.queryRegisterApproveList(pageNo, pageSize, register);
	}

	@Override
	public void update(RegisterModel register) {
		this.registerDao.update(register);
	}
	
	@Override
	public void importOriginData(List<RegisterModel> list) throws ExcelException {
		for(RegisterModel register : list) {
			StudentInfoModel student = this.studentCommonService.queryStudentByStudentNo(register.getStuNumber());
			register.setStudent(student);
			this.registerDao.save(register);
		}
	}

	@Override
	public List<Object[]> compareData(List<RegisterModel> list) throws OfficeXmlFileException, IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException {
		List<Object[]> compareList = new ArrayList<Object[]>();
		Object[] array = (Object[])null;
		long count = this.registerDao.countRegister();
		if(count != 0L) {
			for(int i = 0; i < count / 10 + 1L; i++) {
				Page page = this.registerDao.queryRegisterList(i+1, 10, new RegisterModel());
				List<RegisterModel> infoList = (List<RegisterModel>)(page.getResult());
				for(RegisterModel info : infoList) {
					for(RegisterModel xls : list) {
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

	@Override
	public void importLastData(List<Object[]> list, String filePath, String compareId) throws OfficeXmlFileException, IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException, Exception {
		Map<String, RegisterModel> map = new HashMap<String, RegisterModel>();
		for(Object[] array : list) {
			RegisterModel info = (RegisterModel)array[0];
			map.put(info.getStudent().getStuNumber(), info);
		}
		ImportUtil iu = new ImportUtil();
		//Excel数据
		List<RegisterModel> infoList = iu.getDataList(filePath, "importRegisterCode", null, RegisterModel.class);     
		
		for(RegisterModel xls : infoList) {
			String flag = xls.getStuNumber();
			if(!map.containsKey(flag)) {
				StudentInfoModel student = this.studentCommonService.queryStudentByStudentNo(xls.getStuNumber());
				xls.setStudent(student);
				this.registerDao.save(xls);
			} else{
				RegisterModel infoPo = (RegisterModel) map.get(flag);  
				//已有数据需要更新的记录
				if((StringUtils.isBlank(compareId)) || (!compareId.contains(infoPo.getId()))) {
					infoPo.setEmploymentYear(xls.getEmploymentYear());
					infoPo.setRegisterCode(xls.getRegisterCode());
					this.registerDao.update(infoPo);
				}
			}
		}
	}

	
	/**
	 * 描述信息: TODO (查询所有学生的报到证信息)
	 * @param pageNo
	 * @param pageSize
	 * @param register
	 * @return
	 * @see com.uws.job.service.IRegisterService#queryRegisterList(int, int, com.uws.domain.job.RegisterModel)
	 */
	public Page queryRegisterList(int pageNo, int pageSize, RegisterModel register) {
		return registerDao.queryRegisterList(pageNo, pageSize, register);
	}

	/**
	 * 
	 * @Title: queryRegisterCollegeList
	 * 描述信息: 统计各个学院的报到证补办率
	 * @param pageNo
	 * @param pageSize
	 * @param registerStatistics
	 * @return
	 * @throws
	 */
	public Page queryRegisterCollegeList(int pageNo, int pageSize, RegisterStatisticsModel registerStatistics) {
		return registerDao.queryRegisterCollegeList(pageNo, pageSize, registerStatistics);
	}
	
	/**
	  * 描述信息: 统计各个专业的报到证补办率
	 * @param pageNo
	 * @param pageSize
	 * @param registerStatistics
	 * @return
	 * @see com.uws.job.service.IRegisterService#queryRegisterMajorList(int, int, com.uws.domain.job.RegisterStatisticsModel)
	 */
	public Page queryRegisterMajorList(int pageNo, int pageSize, RegisterStatisticsModel registerStatistics) {
		return registerDao.queryRegisterMajorList(pageNo, pageSize, registerStatistics);
	}	
		
	/**
	 * 描述信息: 统计各个班级的报到证补办率
	 * @param pageNo
	 * @param pageSize
	 * @param registerStatistics
	 * @return
	 * @see com.uws.job.service.IRegisterService#queryRegisterClassList(int, int, com.uws.domain.job.RegisterStatisticsModel)
	 */
	public Page queryRegisterClassList(int pageNo, int pageSize, RegisterStatisticsModel registerStatistics) {
		return registerDao.queryRegisterClassList(pageNo, pageSize, registerStatistics);
	}
	
	@Override
	public RegisterModel queryRegisterByCode(String yearId,String registerCode)
	{
	    return this.registerDao.queryRegisterByCode(yearId,registerCode);
	}

	@Override
    public boolean checkCodeRepeat(String id, String employmentYear,String registerCode)
    {
	    return this.registerDao.checkCodeRepeat(id,employmentYear,registerCode);
    }

}
	

