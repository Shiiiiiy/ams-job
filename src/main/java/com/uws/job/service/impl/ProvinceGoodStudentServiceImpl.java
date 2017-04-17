package com.uws.job.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.ProvinceGoodStudent;
import com.uws.domain.job.ProvinceGoodStudentClassView;
import com.uws.domain.job.ProvinceGoodStudentCollegeView;
import com.uws.domain.job.ProvinceGoodStudentMajorView;
import com.uws.job.dao.IProvinceGoodStudentDao;
import com.uws.job.service.IProvinceGoodStudentService;
import com.uws.sys.model.Dic;
/**
 * 省优秀毕业生接口实现类
 * @author pc
 *
 */
@Service("com.uws.job.service.impl.ProvinceGoodStudentServiceImpl")
public class ProvinceGoodStudentServiceImpl extends BaseServiceImpl implements IProvinceGoodStudentService
{   
	@Autowired
	private IProvinceGoodStudentDao provinceGoodStudentDao;

    /**
     * 
    * @Description:查询省优秀毕业生列表信息
    * @author LiuChen  
    * @date 2015-12-2 下午4:57:13
     */
	@Override
	public Page queryProvinceGoodStudentList(int pageNo, int pageSize,ProvinceGoodStudent provinceGoodStudent,Dic nowSchoolYear,String currentStudentId)
	{
	    return this.provinceGoodStudentDao.queryProvinceGoodStudentList(pageNo,pageSize,provinceGoodStudent,nowSchoolYear,currentStudentId);
	}
	

    /**
     * 
    * @Description:通过主键id查询省优秀毕业生信息
    * @author LiuChen  
    * @date 2015-12-2 下午4:57:59
     */
	@Override
	public ProvinceGoodStudent findProvinceGoodStudentById(String id)
	{
	    ProvinceGoodStudent ProvinceGoodStudent =(ProvinceGoodStudent) this.provinceGoodStudentDao.get(ProvinceGoodStudent.class, id);
	    return ProvinceGoodStudent;
	}
	
	/**
	 * 
	* @Description:保存省优秀毕业生
	* @author LiuChen  
	* @date 2015-12-2 下午4:58:23
	 */
	@Override
	public void save(ProvinceGoodStudent provinceGoodStudent)
	{
	    this.provinceGoodStudentDao.save(provinceGoodStudent);
	}
	
	/**
	 * 
	* @Description:修改省优秀毕业生
	* @author LiuChen  
	* @date 2015-12-2 下午4:59:14
	 */
	@Override
	public void update(ProvinceGoodStudent provinceGoodStudentPo)
	{
	    this.provinceGoodStudentDao.update(provinceGoodStudentPo);
	}
	
	/**
	* 
	* @Description:删除省优秀毕业生
	* @author LiuChen  
	* @date 2015-12-2 下午4:59:32
	 */
	@Override
	public void delete(ProvinceGoodStudent provinceGoodStudentPo)
	{
	    this.provinceGoodStudentDao.delete(provinceGoodStudentPo);
	}
	
	/**
	 * 
	* @Description:根据学生校优秀毕业生查询省优秀毕业生学生
	* @author LiuChen  
	* @date 2015-12-2 下午4:59:56
	 */
	@Override
	public ProvinceGoodStudent findProvinceGoodStudentBySid(String sid)
	{
	    return this.provinceGoodStudentDao.findProvinceGoodStudentBySid(sid);
	}
	
	/**
	 * 
	* @Description: 查询省优秀毕业生信息列表
	* @author LiuChen  
	* @date 2015-12-2 下午5:00:30
	 */
	@Override
	public Page queryApproveProvinceGoodStudentList(int pageNo, int pageSize,ProvinceGoodStudent provinceGoodStudent,Dic yearDic)
	{
	    return this.provinceGoodStudentDao.queryApproveProvinceGoodStudentList(pageNo, pageSize, provinceGoodStudent,yearDic);
	}
    
	/**
	 * 
	* @Description:通过学院统计省优秀毕业生
	* @author LiuChen  
	* @date 2015-12-2 下午5:00:52
	 */
	@Override
    public List<ProvinceGoodStudentCollegeView> queryProvinceGoodStudentByCollege(ProvinceGoodStudent provinceGoodStudent, int pageSize, int pageNo,Dic nowSchoolYear)
    {   
	    return this.provinceGoodStudentDao.queryProvinceGoodStudentByCollege(provinceGoodStudent,pageSize,pageNo,nowSchoolYear);
    }
    
	/**
	 * 
	* @Description:通过专业统计省优秀毕业生
	* @author LiuChen  
	* @date 2015-12-2 下午5:01:15
	 */
	@Override
    public List<ProvinceGoodStudentMajorView> queryProvinceGoodStudentByByMajor(ProvinceGoodStudent provinceGoodStudent,Dic nowSchoolYear)
    {
		return this.provinceGoodStudentDao.queryProvinceGoodStudentByByMajor(provinceGoodStudent,nowSchoolYear);
    }
    
	/**
	 * 
	* @Description:通过班级统计省优秀毕业生
	* @author LiuChen  
	* @date 2015-12-2 下午5:01:52
	 */
	@Override
    public List<ProvinceGoodStudentClassView> queryProvinceGoodStudentByClass(ProvinceGoodStudent provinceGoodStudent,Dic nowSchoolYear)
    {
		return this.provinceGoodStudentDao.queryProvinceGoodStudentByClass(provinceGoodStudent,nowSchoolYear);
    }
	
	/**
	 * 
	* @Description: 查询审核通过的省优秀毕业生
	* @author LiuChen  
	* @date 2015-12-2 下午5:02:17
	 */
	@Override
	public Page queryPassProvinceGoodStudentList(int pageNo, int pageSize,ProvinceGoodStudent provinceGoodStudent, Dic yearDic)
	{
	    return this.provinceGoodStudentDao.queryPassProvinceGoodStudentList(provinceGoodStudent,pageSize,pageNo,yearDic);
	}

}
