package com.uws.job.dao;

import java.util.List;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.ProvinceGoodStudent;
import com.uws.domain.job.ProvinceGoodStudentClassView;
import com.uws.domain.job.ProvinceGoodStudentCollegeView;
import com.uws.domain.job.ProvinceGoodStudentMajorView;
import com.uws.sys.model.Dic;

public interface IProvinceGoodStudentDao extends IBaseDao
{
    /**
     * 
    * @Title: IProvinceGoodStudentDao.java 
    * @Package com.uws.job.dao 
    * @Description:查询省优秀毕业生列表
    * @author LiuChen 
    * @date 2015-12-2 下午5:02:49
     */
	Page queryProvinceGoodStudentList(int pageNo, int pageSize,ProvinceGoodStudent provinceGoodStudent,Dic nowSchoolYear,String currentStudentId);
    
	/**
	 * 
	* @Title: IProvinceGoodStudentDao.java 
	* @Package com.uws.job.dao 
	* @Description: 根据学生校优秀毕业生查询省优秀毕业生学生
	* @author LiuChen 
	* @date 2015-12-2 下午5:03:14
	 */
	public ProvinceGoodStudent findProvinceGoodStudentBySid(String sid);
    /**
     * 
    * @Title: IProvinceGoodStudentDao.java 
    * @Package com.uws.job.dao 
    * @Description: 查询省优秀毕业生信息列表
    * @author LiuChen 
    * @date 2015-12-2 下午5:03:45
     */
	Page queryApproveProvinceGoodStudentList(int pageNo, int pageSize,ProvinceGoodStudent provinceGoodStudent,Dic yearDic);
    /**
     * 
    * @Title: IProvinceGoodStudentDao.java 
    * @Package com.uws.job.dao 
    * @Description: 通过学院统计省优秀毕业生
    * @author LiuChen 
    * @date 2015-12-2 下午5:04:05
     */
	List<ProvinceGoodStudentCollegeView> queryProvinceGoodStudentByCollege(ProvinceGoodStudent provinceGoodStudent, int pageSize, int pageNo,Dic nowSchoolYear);
    /**
     * 
    * @Title: IProvinceGoodStudentDao.java 
    * @Package com.uws.job.dao 
    * @Description:通过专业统计省优秀毕业生
    * @author LiuChen 
    * @date 2015-12-2 下午5:04:20
     */
	public List<ProvinceGoodStudentMajorView> queryProvinceGoodStudentByByMajor(ProvinceGoodStudent provinceGoodStudent,Dic nowSchoolYear);
    /**
     * 
    * @Title: IProvinceGoodStudentDao.java 
    * @Package com.uws.job.dao 
    * @Description: 通过班级统计省优秀毕业生
    * @author LiuChen 
    * @date 2015-12-2 下午5:04:50
     */
	public List<ProvinceGoodStudentClassView> queryProvinceGoodStudentByClass(ProvinceGoodStudent provinceGoodStudent,Dic nowSchoolYear);
    /**
     * 
    * @Title: IProvinceGoodStudentDao.java 
    * @Package com.uws.job.dao 
    * @Description:查询审核通过的省优秀毕业生
    * @author LiuChen 
    * @date 2015-12-2 下午5:05:06
     */
	Page queryPassProvinceGoodStudentList(ProvinceGoodStudent provinceGoodStudent, int pageSize, int pageNo,Dic yearDic);

}
