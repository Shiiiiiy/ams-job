package com.uws.job.dao;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.AgreementModel;
import com.uws.domain.job.AgreementStatisticsClassModel;
import com.uws.domain.job.AgreementStatisticsCollegeModel;
import com.uws.domain.job.AgreementStatisticsMajorModel;
import com.uws.domain.job.AgreementStatisticsModel;
import com.uws.sys.model.Dic;

public interface IAgreementDao extends IBaseDao {
	
	/**
	 * @Title: queryAgreementList
	 * @Description: TODO(查询就业协议列表页面)
	 * @param pageNo
	 * @param pageSize
	 * @param agreement
	 * @return
	 * @throws
	 */
	public Page queryAgreementList(Integer pageNo, Integer pageSize, AgreementModel agreement);
	
	/**
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
	 * @Title: countAgreement
	 * @Description: TODO(统计数据库中就业协议的条数)
	 * @return
	 * @throws
	 */
	public long countAgreement();

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
	 * 
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
