package com.uws.job.service;

import java.io.IOException;
import java.util.List;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;

import com.uws.core.base.IBaseService;
import com.uws.core.excel.ExcelException;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.RecruitModel;

public interface IRecruitService extends IBaseService {
	
	/**
	 * @return 
	 * 
	 * @Title: findRecruitById
	 * @Description: TODO(通过ID找到就业协议)
	 * @param id
	 * @return
	 * @throws
	 */
	public RecruitModel findRecruitById(String id);
	
	/**
	 * 
	 * @Title: deleteRecruitById
	 * @Description: TODO(删除报到证补办申请)
	 * @param id
	 * @throws
	 */
	public void deleteRecruitById(String id);

	/**
	 * @Title: update
	 * @Description: TODO(用于进行状态的更新)
	 * @param register
	 * @throws
	 */
	public void update(RecruitModel register);
	
	/**
	 * @Title: queryRecruitList
	 * @Description: TODO()
	 * @param pageNo
	 * @param pageSize
	 * @param recruit
	 * @return
	 * @throws
	 */
	public Page queryRecruitList(int pageNo, int pageSize, RecruitModel recruit);

	public void save(RecruitModel recruit);

	/**
	 * @Title: isApply
	 * @Description: TODO(判断本学年是否申请过招聘会信息)
	 * @param id
	 * @param year
	 * @return
	 * @throws
	 */
	String isApply(String collegeId, String year);
	
}
