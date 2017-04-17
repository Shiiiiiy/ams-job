package com.uws.job.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;

import com.uws.core.base.IBaseService;
import com.uws.core.excel.ExcelException;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.EmploymentInfo;

/**
* 
* @ClassName: IEmploymentInfoService 
* @Description: 就业信息管理模块接口
* @author liuchen
* @date 2015-10-9 下午14:21:08 
*
*/
public interface IEmploymentInfoService extends IBaseService{
    /**
     * 
    * @Title: IEmploymentInfoService.java 
    * @Package com.uws.job.service 
    * @Description:
    * @author 
    * @date 2015-10-9 下午3:00:39
     */
	public Page queryEmploymentInfoList(int pageNo, int pageSize, EmploymentInfo employmenInfo);
    
	/**
	 * 
	* @Title: IEmploymentInfoService.java 
	* @Package com.uws.job.service 
	* @Description: 保存就业生信息
	* @author pc  
	* @date 2015-10-10 下午3:44:12
	 */
	public void saveEmploymentInfo(EmploymentInfo employmentInfo);
    
	/**
	 * 
	* @Title: IEmploymentInfoService.java 
	* @Package com.uws.job.service 
	* @Description: 根据id获取就业生信息
	* @author pc  
	* @date 2015-10-10 下午4:37:17
	 */
	public EmploymentInfo findEmploymentInfoById(String id);
    
	/**
	 * 
	* @Title: IEmploymentInfoService.java 
	* @Package com.uws.job.service 
	* @Description: 删除就业信息
	* @author pc  
	* @date 2015-12-2 下午4:33:05
	 */
	public void deleteEmploymentInfo(EmploymentInfo employmentInfo);
    
	/**
	 * 
	* @Title: IEmploymentInfoService.java 
	* @Package com.uws.job.service 
	* @Description:修改就业信息
	* @author pc  
	* @date 2015-10-13 下午5:27:56
	 */
	public void updateEmploymentInfo(EmploymentInfo employmentInfoPo);
	
	
	/**
	 * 比较数据
	 * @param list
	 * @return
	 */
	public List<Object[]> compareData(List<EmploymentInfo> paramList) throws OfficeXmlFileException, IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException;
    
	/**
	 * 导入数据
	 * @param paramList
	 */
    public void importData(List<EmploymentInfo> paramList,HttpServletRequest request);
	
	public void importData(List<Object[]> paramList, String paramString1, String paramString2)
	    throws ExcelException, OfficeXmlFileException, IOException, IllegalAccessException, InstantiationException, ClassNotFoundException, Exception;

	public boolean isExistStudent(String id, String studentId);
    

}
