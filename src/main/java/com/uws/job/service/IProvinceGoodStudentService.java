package com.uws.job.service;

import java.util.List;

import com.uws.core.base.IBaseService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.ProvinceGoodStudent;
import com.uws.domain.job.ProvinceGoodStudentClassView;
import com.uws.domain.job.ProvinceGoodStudentCollegeView;
import com.uws.domain.job.ProvinceGoodStudentMajorView;
import com.uws.sys.model.Dic;
/**
 * 省优秀毕业生接口
 * @author pc
 *
 */
public interface IProvinceGoodStudentService extends IBaseService
{
    /**
     * 
    * @Title: IProvinceGoodStudentService.java 
    * @Package com.uws.job.service 
    * @Description:查询省优秀毕业生列表
    * @author pc  
    * @date 2015-11-17 下午2:24:01
     */
	public Page queryProvinceGoodStudentList(int pageNo, int pageSize,ProvinceGoodStudent provinceGoodStudent,Dic nowSchoolYear,String currentStudentId);
    
	/**
	 * 
	* @Title: IProvinceGoodStudentService.java 
	* @Package com.uws.job.service 
	* @Description: 根据id查询省优秀毕业生对象
	* @author pc  
	* @date 2015-11-17 下午2:24:24
	 */
	public ProvinceGoodStudent findProvinceGoodStudentById(String id);
    
	/**
	 * 
	* @Title: IProvinceGoodStudentService.java 
	* @Package com.uws.job.service 
	* @Description:修改方法
	* @author pc  
	* @date 2015-11-18 上午11:26:56
	 */
	public void update(ProvinceGoodStudent provinceGoodStudentPo);
    
	/**
	 * 
	* @Title: IProvinceGoodStudentService.java 
	* @Package com.uws.job.service 
	* @Description: 保存
	* @author pc  
	* @date 2015-11-18 上午11:28:44
	 */
	public void save(ProvinceGoodStudent provinceGoodStudent);
    
	/**
	 * 
	* @Title: IProvinceGoodStudentService.java 
	* @Package com.uws.job.service 
	* @Description: 删除
	* @author刘晨
	* @date 2015-11-18 上午11:28:56
	 */
	public void delete(ProvinceGoodStudent provinceGoodStudentPo);
	
	/**
	 * 
	* @Title: IProvinceGoodStudentService.java 
	* @Package com.uws.job.service 
	* @Description:根据校优秀毕业生查询对象
	* @author pc  
	* @date 2015-11-18 上午11:29:22
	 */
	public ProvinceGoodStudent findProvinceGoodStudentBySid(String sid);
    
	/**
	 * 
	* @Title: IProvinceGoodStudentService.java 
	* @Package com.uws.job.service 
	* @Description:查询审核列表
	* @author pc  
	* @date 2015-11-18 上午11:29:45
	 */
	public Page queryApproveProvinceGoodStudentList(int pageNo,int pageSize, ProvinceGoodStudent provinceGoodStudent,Dic yearDic);
	
    /**
     * 
    * @Title: IProvinceGoodStudentService.java 
    * @Package com.uws.job.service 
    * @Description:按学院统计
    * @author pc  
    * @date 2015-11-18 下午6:24:52
     */
	public List<ProvinceGoodStudentCollegeView> queryProvinceGoodStudentByCollege(ProvinceGoodStudent provinceGoodStudent, int pageSize,int pageNo,Dic nowSchoolYear);
    
	/**
	 * 
	* @Title: IProvinceGoodStudentService.java 
	* @Package com.uws.job.service 
	* @Description: 按专业统计查询
	* @author pc  
	* @date 2015-11-18 下午6:25:08
	 */
	public List<ProvinceGoodStudentMajorView> queryProvinceGoodStudentByByMajor(ProvinceGoodStudent provinceGoodStudent,Dic nowSchoolYear);
    
	/**
	 * 
	* @Title: IProvinceGoodStudentService.java 
	* @Package com.uws.job.service 
	* @Description: 按班级统计查询
	* @author pc  
	* @date 2015-11-18 下午6:25:23
	 */
	public List<ProvinceGoodStudentClassView> queryProvinceGoodStudentByClass(ProvinceGoodStudent provinceGoodStudent,Dic nowSchoolYear);
    
	/**
	 * 
	* @Title: IProvinceGoodStudentService.java 
	* @Package com.uws.job.service 
	* @Description:省优秀毕业生审核通过查询
	* @author pc  
	* @date 2015-11-19 下午4:35:25
	 */
	public Page queryPassProvinceGoodStudentList(int pageNo,int pageSize, ProvinceGoodStudent provinceGoodStudent,Dic yearDic);

}
