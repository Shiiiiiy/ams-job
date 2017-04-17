package com.uws.job.service;

import java.util.List;

import com.uws.core.base.IBaseService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.job.PoorStudent;
import com.uws.domain.job.PoorStudentClassView;
import com.uws.domain.job.PoorStudentCollegeView;
import com.uws.domain.job.PoorStudentMajorView;
import com.uws.domain.sponsor.DifficultStudentInfo;
import com.uws.sys.model.Dic;
/**
* 
* @ClassName: IEmploymentInfoService 
* @Description: 双困生管理模块接口
* @author liuchen
* @date 2015-10-15 下午17:21:08 
*
*/
public interface IPoorStudentService extends IBaseService
{
    /**
     * 
    * @Title: IPoorStudentService.java 
    * @Package com.uws.job.service 
    * @Description: 困难生信息维护列表
    * @author pc  
    * @date 2015-11-16 下午2:22:48
     */
	public Page queryPoorStudentInfoList(int pageNo, int pageSize,PoorStudent poorStudent,Dic yearDic);
    
	/**
	 * 
	* @Title: IPoorStudentService.java 
	* @Package com.uws.job.service 
	* @Description: 查询困难生对象
	* @author pc  
	* @date 2015-11-16 下午2:23:11
	 */
	public List<DifficultStudentInfo> queryDiffStudentInfoList();
    
	/**
	 * 
	* @Title: IPoorStudentService.java 
	* @Package com.uws.job.service 
	* @Description:根据id查询困难生
	* @author pc  
	* @date 2015-11-16 下午2:23:45
	 */
	public DifficultStudentInfo getDifficultStudentInfoById(String id);
    
	/**
	 * 
	* @Title: IPoorStudentService.java 
	* @Package com.uws.job.service 
	* @Description:根据id查询双困生
	* @author pc  
	* @date 2015-11-16 下午2:24:05
	 */
	public PoorStudent getPoorStudentInfoById(String id);
    
	/**
	 * 
	* @Title: IPoorStudentService.java 
	* @Package com.uws.job.service 
	* @Description:修改双困生
	* @author pc  
	* @date 2015-11-16 下午2:24:25
	 */
	public void updatePoorInfo(PoorStudent poorStudentPo,String fileId[]);
    
	/**
	 * 
	* @Title: IPoorStudentService.java 
	* @Package com.uws.job.service 
	* @Description:保存双困生
	* @author pc  
	* @date 2015-11-16 下午2:24:43
	 */
	public void savePoorInfo(PoorStudent poorStudent,String fileId[]);
    
	/**
	 * 
	* @Title: IPoorStudentService.java 
	* @Package com.uws.job.service 
	* @Description:查询双困生审核列表
	* @author pc  
	* @date 2015-11-16 下午2:25:01
	 */
	public Page queryApprovePoorStudentInfoList(int pageNo,int dEFAULT_PAGE_SIZE, PoorStudent poorStudent,Dic yearDic);
    
	/**
	 * 
	* @Title: IPoorStudentService.java 
	* @Package com.uws.job.service 
	* @Description:按学院统计
	* @author pc  
	* @date 2015-12-2 下午4:37:41
	 */
	public List<PoorStudentCollegeView> queryPoorStudentByCollege(PoorStudent poorStudent,Dic yearDic);
    
	/**
	 * 
	* @Title: IPoorStudentService.java 
	* @Package com.uws.job.service 
	* @Description: 按专业统计
	* @author pc  
	* @date 2015-12-2 下午4:37:57
	 */
	public List<PoorStudentMajorView> queryPoorStudentByByMajor(PoorStudent poorStudent,Dic yearDic);
    
	/**
	 * 
	* @Title: IPoorStudentService.java 
	* @Package com.uws.job.service 
	* @Description:按班级统计
	* @author liuchen
	* @date 2015-12-2 下午4:38:12
	 */
	public List<PoorStudentClassView> queryPoorStudentByClass(PoorStudent poorStudent,Dic yearDic);
    
	/**
	 * 
	* @Title: IPoorStudentService.java 
	* @Package com.uws.job.service 
	* @Description: 删除双困生
	* @author pc  
	* @date 2015-12-2 下午4:38:30
	 */
	public void deleteInfo(PoorStudent poorStudentPo);
    /**
     * 
    * @Title: IPoorStudentService.java 
    * @Package com.uws.job.service 
    * @Description: 双困生审核通过查询文件
    * @author pc  
    * @date 2015-12-2 下午4:39:04
     */
	public Page queryPassPoorStudentInfoList(int pageNo, int pageSize,PoorStudent poorStudent,Dic yearDic,String currentStudentId);
    
	/**
	 * 
	 * @Title: IPoorStudentService.java 
	 * @Package com.uws.job.service 
	 * @Description: 保存
	 * @author LiuChen 
	 * @date 2015-12-17 下午4:18:06
	 */
	public void savePoorStudentInfo(PoorStudent poorStudent);
    
	/**
	 * 
	 * @Title: IPoorStudentService.java 
	 * @Package com.uws.job.service 
	 * @Description: T修改
	 * @author LiuChen 
	 * @date 2015-12-17 下午4:21:50
	 */
	public void updatePoorStudentInfo(PoorStudent poorStudentPo);
	
    /**
     * 
     * @Title: IPoorStudentService.java 
     * @Package com.uws.job.service 
     * @Description:验证是否添加过学生
     * @author LiuChen 
     * @date 2016-1-6 下午12:57:40
     */
	public boolean isExistStudent(String id, String studentId,String schoolYear);

}
