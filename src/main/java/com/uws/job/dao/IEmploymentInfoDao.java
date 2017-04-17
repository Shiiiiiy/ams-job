package com.uws.job.dao;


import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.EmploymentInfo;
/**
* @ClassName: IEmploymentInfoDao 
* @Description: 就业信息管理模块Dao接口
* @author liuchen
* @date 2015-10-9 下午14:23:08 
*
*/
public interface IEmploymentInfoDao extends IBaseDao{
    
	/**
	 * 
	* @Title: IEmploymentInfoDao.java 
	* @Package com.uws.job.dao 
	* @Description: 就业信息查询列表
	* @author pc  
	* @date 2015-10-9 下午3:05:18
	 */
	Page queryEmploymentInfoList(int pageNo, int pageSize,EmploymentInfo employmenInfo);

	 /**
     * 获取总条数
     * @return
     */
	public long getCount();
    
	/**
	 * 
	* @Title: IEmploymentInfoDao.java 
	* @Package com.uws.job.dao 
	* @Description:导入查询页数
	* @author pc  
	* @date 2015-12-2 下午4:33:52
	 */
	public Page pageQuery(int i, int pageSize);
    
	/**
	 * 
	 * @Title: IEmploymentInfoDao.java 
	 * @Package com.uws.job.dao 
	 * @Description:验证学生重复添加
	 * @author LiuChen 
	 * @date 2015-12-9 下午3:56:56
	 */
	public boolean isExistStudent(String id, String studentId);

	
}