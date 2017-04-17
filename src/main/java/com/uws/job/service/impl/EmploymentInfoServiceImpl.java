package com.uws.job.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.common.dao.IStudentCommonDao;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.job.EmploymentInfo;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.job.dao.IEmploymentInfoDao;
import com.uws.job.service.IEmploymentInfoService;
import com.uws.job.util.Constants;

/**
* @ClassName: EmploymentInfoServiceImpl 
* @Description: 就业信息管理模块接口实现类
* @author liuchen
* @date 2015-10-9 下午14:22:08 
*
*/
@Service("com.uws.job.service.impl.EmploymentInfoServiceImpl")
public class EmploymentInfoServiceImpl implements IEmploymentInfoService{
	@Autowired
	private IEmploymentInfoDao employmentInfoDao;
	//条数
	private int pageSize = 1000;
	//学生基本信息
	@Autowired
	private IStudentCommonDao studentCommonDao;
	
	/**
	 * 
	* @Title: EmploymentInfoServiceImpl.java 
	* @Package com.uws.job.service.impl;
	* @Description: 就业信息查询列表
	* @author pc  
	* @date 2015-10-9 下午3:05:18
	 */
	public Page queryEmploymentInfoList(int pageNo, int pageSize, EmploymentInfo employmenInfo)
	{
	   return this.employmentInfoDao.queryEmploymentInfoList(pageNo,pageSize,employmenInfo);
	}
	
	
	/**
	 * 
	* @Title: EmploymentInfoServiceImpl.java 
	* @Package com.uws.job.service.impl;
	* @Description: 保存就业信息
	* @author pc  
	* @date 2015-10-9 下午3:05:18
	 */
	@Override
	public void saveEmploymentInfo(EmploymentInfo employmentInfo)
	{
	   this.employmentInfoDao.save(employmentInfo);
	}
	
	/**
	 * 
	* @Title: EmploymentInfoServiceImpl.java 
	* @Package com.uws.job.service.impl;
	* @Description: 保存就业信根据就业信息id查询对象
	* @author pc  
	* @date 2015-10-9 下午3:05:18
	 */
	@Override
	public EmploymentInfo findEmploymentInfoById(String id)
	{   
		EmploymentInfo employmentInfo =(EmploymentInfo) this.employmentInfoDao.get(EmploymentInfo.class, id);
	    return employmentInfo;
	}
	
	/**
	 * 
	* @Title: EmploymentInfoServiceImpl.java 
	* @Package com.uws.job.service.impl;
	* @Description: 删除就业信息
	* @author pc  
	* @date 2015-10-9 下午3:05:18
	 */
	@Override
	public void deleteEmploymentInfo(EmploymentInfo employmentInfo)
	{
	    this.employmentInfoDao.delete(employmentInfo);
	}
	
	
	
	/**
	 * 
	* @Title: EmploymentInfoServiceImpl.java 
	* @Package com.uws.job.service.impl;
	* @Description: 跟新就业信息
	* @author pc  
	* @date 2015-10-9 下午3:05:18
	 */
	@Override
	public void updateEmploymentInfo(EmploymentInfo employmentInfoPo)
	{
	    this.employmentInfoDao.update(employmentInfoPo);
	}
	
	/**
	 * 导入就业信息
	 */
	@Override
	public void importData(List<EmploymentInfo> list,HttpServletRequest request){
		for (EmploymentInfo employmentInfo : list){
			 String number = employmentInfo.getStuNumber();
			 BigDecimal bd = new BigDecimal(number);
			 String stuNumber = bd.toString();
			 StudentInfoModel studentInfo = studentCommonDao.queryStudentByStudentNo(stuNumber);
			 if(employmentInfo!=null && StringUtils.isNotBlank(employmentInfo.getEnterTime())){
				 employmentInfo.setEnterDate(Constants.convertStringToDate(employmentInfo.getEnterTime()));
			 }
			 if(employmentInfo!=null && StringUtils.isNotBlank(employmentInfo.getGraduateTime())){
				 employmentInfo.setGraduateDate(Constants.convertStringToDate(employmentInfo.getGraduateTime()));
			 }
			 employmentInfo.setStudent(studentInfo);
			 this.employmentInfoDao.save(employmentInfo);
		}
	}

	/**
	 * 比较导入的数据是否重复
	 */
	@Override
	public List<Object[]> compareData(List<EmploymentInfo> list) throws OfficeXmlFileException, IOException, IllegalAccessException,
		  ExcelException, InstantiationException, ClassNotFoundException {
		 List compareList = new ArrayList();
	     Object[] array = (Object[])null;
	     long count = this.employmentInfoDao.getCount();
	     if (count != 0L) {
	       for (int i = 0; i < count / this.pageSize + 1L; i++) {
	         Page page = this.employmentInfoDao.pageQuery(i + 1, this.pageSize);
	         List<EmploymentInfo> employmentList = (List)page.getResult();
	         for (EmploymentInfo employmentInfo : employmentList) {
	           for (EmploymentInfo employmentInfoExcel : list){
	        	   BigDecimal bd = new BigDecimal(employmentInfoExcel.getStuNumber());
		           String str = bd.toString();
	             if ((employmentInfo.getStudent() !=null && employmentInfo.getStudent().getStuNumber() !=null && employmentInfo.getStudent().getStuNumber().equals(str))) {
	            	   String number = employmentInfoExcel.getStuNumber();
	       			   BigDecimal bds = new BigDecimal(number);
	       			   String stuNumber = bds.toString();
	            	   StudentInfoModel studentInfo = studentCommonDao.queryStudentByStudentNo(stuNumber);
	            	   employmentInfoExcel.setStudent(studentInfo);
	            	 array = new Object[] {employmentInfo,employmentInfoExcel};
	               compareList.add(array);
	            }
	        }
	      }
	     }
	  }
	     return compareList;
	}
    

    /**
     * 
     * @Description:导入数据对比
     * @author LiuChen  
     * @date 2015-12-9 下午3:57:33
     */
	@Override
	public void importData(List<Object[]> list, String filePath,String compareId) throws Exception {
		 Map map = new HashMap();
	     for (Object[] array : list) {
	    	 EmploymentInfo employmentInfo = (EmploymentInfo)array[0];
	       map.put(employmentInfo.getStudent().getStuNumber(), employmentInfo);
	    }
	     ImportUtil iu = new ImportUtil();
	     List<EmploymentInfo> listEmployment = iu.getDataList(filePath, "importEmployment", null, EmploymentInfo.class);
	     for (EmploymentInfo employmentInfo : listEmployment) {
	       BigDecimal bds = new BigDecimal(employmentInfo.getStuNumber());
    	   String str = bds.toString();
	       if (!map.containsKey(str)) {
	    	  StudentInfoModel studentInfoModel = new StudentInfoModel();
	    	  studentInfoModel.setId(str);
	    	  employmentInfo.setStudent(studentInfoModel);
	         this.employmentInfoDao.save(employmentInfo);
	      } else {
	    	  EmploymentInfo employmentInfoPo = (EmploymentInfo)map.get(str);
	         if ((StringUtils.isBlank(compareId)) || (!compareId.contains(employmentInfoPo.getId()))) {
	        	 employmentInfo.setId(employmentInfoPo.getId());
	        	 StudentInfoModel studentInfo = studentCommonDao.queryStudentByStudentNo(str);
	        	 employmentInfo.setStudent(studentInfo);
	        	 if(employmentInfo!=null && StringUtils.isNotBlank(employmentInfo.getEnterTime())){
					 employmentInfo.setEnterDate(Constants.convertStringToDate(employmentInfo.getEnterTime()));
				 }
				 if(employmentInfo!=null && StringUtils.isNotBlank(employmentInfo.getGraduateTime())){
					 employmentInfo.setGraduateDate(Constants.convertStringToDate(employmentInfo.getGraduateTime()));
				 }
	           this.employmentInfoDao.update(employmentInfo);
	        }
	      }
	    }
	  }
	
	/**
	 * 
	 * @Description:验证学生重复添加
	 * @author LiuChen  
	 * @date 2015-12-9 下午3:57:13
	 */
	@Override
	public boolean isExistStudent(String id, String studentId)
	{
	    return employmentInfoDao.isExistStudent(id,studentId);
	}

}
