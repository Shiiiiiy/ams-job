package com.uws.job.dao;

import java.util.List;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.job.AgreementModel;
import com.uws.domain.job.RegisterModel;
import com.uws.domain.job.RegisterStatisticsClassModel;
import com.uws.domain.job.RegisterStatisticsCollegeModel;
import com.uws.domain.job.RegisterStatisticsMajorModel;
import com.uws.domain.job.RegisterStatisticsModel;
import com.uws.sys.model.Dic;

public interface IRegisterDao extends IBaseDao {
	
	
	/**
	 * 
	 * @Title: queryRegisterList
	 * @Description: (查询报到证列表)
	 * @param pageNo
	 * @param pageSize
	 * @param register
	 * @return
	 * @throws
	 */
	public Page queryRegisterList(int pageNo, int pageSize, RegisterModel register); 
	
	/**
	 * 
	 * @Title: queryRegisterApproveList
	 * @Description: TODO(查询报到证审核列表)
	 * @param pageNo
	 * @param pageSize
	 * @param register
	 * @return
	 * @throws
	 */
	public Page queryRegisterApproveList(int pageNo, int pageSize, RegisterModel register);

	/**
	 * 
	 * @Title: countRegister
	 * @Description: TODO(统计数据库中报到证的条数 )
	 * @return
	 * @throws
	 */
	public long countRegister();

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
	 * 
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

	public boolean checkCodeRepeat(String id, String employmentYear,String registerCode);
	
	
	
}
