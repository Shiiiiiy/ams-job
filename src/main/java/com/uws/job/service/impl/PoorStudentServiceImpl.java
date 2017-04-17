package com.uws.job.service.impl;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.PoorStudent;
import com.uws.domain.job.PoorStudentClassView;
import com.uws.domain.job.PoorStudentCollegeView;
import com.uws.domain.job.PoorStudentMajorView;
import com.uws.domain.sponsor.DifficultStudentInfo;
import com.uws.job.dao.IPoorStudentDao;
import com.uws.job.service.IPoorStudentService;
import com.uws.sys.model.Dic;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.FileFactory;
@Service("com.uws.job.service.impl.PoorStudentServiceImpl")
public class PoorStudentServiceImpl extends BaseServiceImpl implements IPoorStudentService
{   
	@Autowired
	private IPoorStudentDao poorStudentDao;
	//附件工具类
	private FileUtil fileUtil=FileFactory.getFileUtil();
	
	 /**
     * 
    * @Title: IPoorStudentService.java 
    * @Package com.uws.job.service 
    * @Description: 困难生信息维护列表
    * @author pc  
    * @date 2015-11-16 下午2:22:48
     */
	@Override
	public Page queryPoorStudentInfoList(int pageNo, int pageSize,PoorStudent poorStudent,Dic yearDic)
	{
	    return this.poorStudentDao.queryPoorStudentInfoList(pageNo,pageSize,poorStudent,yearDic);
	}
	
	/**
	 * 
	* @Title: IPoorStudentService.java 
	* @Package com.uws.job.service 
	* @Description: 查询困难生对象
	* @author pc  
	* @date 2015-11-16 下午2:23:11
	 */
	@Override
	public List<DifficultStudentInfo> queryDiffStudentInfoList()
	{
	    return this.poorStudentDao.queryDiffStudentInfoList();
	}
	
	/**
	 * 
	* @Title: IPoorStudentService.java 
	* @Package com.uws.job.service 
	* @Description:根据id查询困难生
	* @author pc  
	* @date 2015-11-16 下午2:23:45
	 */
	@Override
	public DifficultStudentInfo getDifficultStudentInfoById(String id)
	{   
		DifficultStudentInfo difficultStudentInfo =(DifficultStudentInfo) this.poorStudentDao.get(DifficultStudentInfo.class, id);
	    return difficultStudentInfo;
	}
	
	/**
	 * 
	* @Title: IPoorStudentService.java 
	* @Package com.uws.job.service 
	* @Description:根据id查询双困生
	* @author pc  
	* @date 2015-11-16 下午2:24:05
	 */
	@Override
	public PoorStudent getPoorStudentInfoById(String id)
	{
		PoorStudent poorStudent =(PoorStudent) this.poorStudentDao.get(PoorStudent.class, id);
	    return poorStudent;
	}
	
	/**
	 * 
	* @Title: IPoorStudentService.java 
	* @Package com.uws.job.service 
	* @Description:保存双困生
	* @author pc  
	* @date 2015-11-16 下午2:24:43
	 */
	@Override
	public void savePoorInfo(PoorStudent poorStudent,String fileId[])
	{
	    this.poorStudentDao.save(poorStudent);
	    // 上传的附件进行处理
 		if (!ArrayUtils.isEmpty(fileId))
 		{
 			for (String id : fileId)
 				this.fileUtil.updateFormalFileTempTag(id,poorStudent.getId());
 		}
	}
	
	/**
	 * 
	* @Title: IPoorStudentService.java 
	* @Package com.uws.job.service 
	* @Description:修改双困生
	* @author pc  
	* @date 2015-11-16 下午2:24:25
	 */
	@Override
	public void updatePoorInfo(PoorStudent poorStudentPo,String fileId[])
	{
	    this.poorStudentDao.update(poorStudentPo);
	    //上传的附件进行处理
  		if (ArrayUtils.isEmpty(fileId))
  		       fileId = new String[0];
  		     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(poorStudentPo.getId());
  		     for (UploadFileRef ufr : list) {
  		       if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId()))
  		         this.fileUtil.deleteFormalFile(ufr);
  		    }
  		     for (String id : fileId){
  		       this.fileUtil.updateFormalFileTempTag(id, poorStudentPo.getId());
  		  }
	}
	
	/**
	 * 
	* @Title: IPoorStudentService.java 
	* @Package com.uws.job.service 
	* @Description:查询双困生审核列表
	* @author pc  
	* @date 2015-11-16 下午2:25:01
	 */
	@Override
	public Page queryApprovePoorStudentInfoList(int pageNo,int pageSize, PoorStudent poorStudent,Dic yearDic)
	{
	    return this.poorStudentDao.queryApprovePoorStudentInfoList(pageNo,pageSize,poorStudent,yearDic);
	}
	/**
	 * 
	* @Title: IPoorStudentService.java 
	* @Package com.uws.job.service 
	* @Description:根据学院统计查询
	* @author pc  
	* @date 2015-12-2 下午2:25:01
	 */
	@Override
    public List<PoorStudentCollegeView> queryPoorStudentByCollege(PoorStudent poorStudent,Dic yearDic)
    {
	    return this.poorStudentDao.queryPoorStudentByCollege(poorStudent,yearDic);
    }
    
	/**
	 * 
	* @Title: IPoorStudentService.java 
	* @Package com.uws.job.service 
	* @Description:根据专业统计查询
	* @author pc  
	* @date 2015-12-2 下午2:25:22
	 */
	@Override
    public List<PoorStudentMajorView> queryPoorStudentByByMajor(PoorStudent poorStudent,Dic yearDic)
    {
	    return this.poorStudentDao.queryPoorStudentByByMajor(poorStudent,yearDic);
    }
    
	/**
	 * 
	* @Title: IPoorStudentService.java 
	* @Package com.uws.job.service 
	* @Description:根据班级统计查询
	* @author pc  
	* @date 2015-12-2 下午2:26:02
	 */
	@Override
    public List<PoorStudentClassView> queryPoorStudentByClass(PoorStudent poorStudent,Dic yearDic)
    {
	    return this.poorStudentDao.queryPoorStudentByClass(poorStudent,yearDic);
    }
	
	/**
	 * 
	* @Title: IPoorStudentService.java 
	* @Package com.uws.job.service 
	* @Description:删除双困生
	* @author pc  
	* @date 2015-12-2 下午2:26:02
	 */
	@Override
	public void deleteInfo(PoorStudent poorStudentPo)
	{
	    this.poorStudentDao.delete(poorStudentPo);
	}
	
	/**
	 * 
	* @Title: IPoorStudentService.java 
	* @Package com.uws.job.service 
	* @Description:查询双困生列表
	* @author pc  
	* @date 2015-12-2 下午2:26:02
	 */
	@Override
	public Page queryPassPoorStudentInfoList(int pageNo, int pageSize,PoorStudent poorStudent,Dic yearDic,String currentStudentId)
	{
	    return this.poorStudentDao.queryPassPoorStudentInfoList(pageNo,pageSize,poorStudent,yearDic,currentStudentId);
	}
	
	/**
	 * 
	 * @Description: 保存信息
	 * @author LiuChen  
	 * @date 2015-12-17 下午4:41:04
	 */
	@Override
	public void savePoorStudentInfo(PoorStudent poorStudent)
	{
	    this.poorStudentDao.save(poorStudent);
	}
	
	/**
	 * 
	 * @Description:新增信息
	 * @author LiuChen  
	 * @date 2015-12-17 下午4:41:18
	 */
	@Override
	public void updatePoorStudentInfo(PoorStudent poorStudentPo)
	{
	    this.poorStudentDao.update(poorStudentPo);
	}
	
	/**
	 * 
	 * @Description:验证是否已添加过学生
	 * @author LiuChen  
	 * @date 2016-1-6 下午12:59:41
	 */
	@Override
	public boolean isExistStudent(String id, String studentId,String schoolYear)
	{
	    return this.poorStudentDao.isExistStudent(id,studentId,schoolYear);
	}

}
