package com.uws.job.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;


import com.uws.common.service.IStudentCommonService;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.rule.IRule;
import com.uws.core.excel.vo.ExcelColumn;
import com.uws.core.excel.vo.ExcelData;
import com.uws.core.util.SpringBeanLocator;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;

public class EmploymentRule implements IRule{
	//数据字典工具类
	 private DicUtil dicUtil = DicFactory.getDicUtil();
	 List<Dic> educationList = dicUtil.getDicInfoList("EDUCATION");
	 List<Dic> isList = dicUtil.getDicInfoList("Y&N");
	 List<Dic> normalList = dicUtil.getDicInfoList("NORMAL_TYPE");
	 List<Dic> cultureList = dicUtil.getDicInfoList("CULTURE_TYPE");
	 List<Dic> recruitList = dicUtil.getDicInfoList("RECRUIT_STUDENT_TYPE");
	 List<Dic> difficultTypeList = dicUtil.getDicInfoList("DIFFICULT_CATEGORY_JOB");
	 
	 @InitBinder
	    protected void initBinder(WebDataBinder binder) {
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
	        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	    }
	 
	@Override
	public void format(ExcelData arg0, ExcelColumn arg1, Map arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void operation(ExcelData data, ExcelColumn cloumn, Map map,
			Map<String, ExcelData> eds, int site) {
		if("education".equals(cloumn.getName())){
			String untinValue = getString(site, eds, "O");
			for (Dic dic : this.educationList)
				if (untinValue.equals(dic.getName())) {
					data.setValue(dic);
					break;
		        }
		}
		
	
		if("normalType".equals(cloumn.getName())){
			String untinValue = getString(site, eds, "Q");
			for (Dic dic : this.normalList)
				if (untinValue.equals(dic.getName())) {
					data.setValue(dic);
					break;
		        }
		}
		
		if("cultureType".equals(cloumn.getName())){
			String untinValue = getString(site, eds, "S");
			for (Dic dic : this.cultureList)
				if (untinValue.equals(dic.getName())) {
					data.setValue(dic);
					break;
		        }
		}
		
		if("recruitStudentType".equals(cloumn.getName())){
			String untinValue = getString(site, eds, "R");
			for (Dic dic : this.recruitList)
				if (untinValue.equals(dic.getName())) {
					data.setValue(dic);
					break;
		        }
		}
		
		if("difficultType".equals(cloumn.getName())){
			String untinValue = getString(site, eds, "T");
			for (Dic dic : this.difficultTypeList)
				if (untinValue.equals(dic.getName())) {
					data.setValue(dic);
					break;
				}
		}
	}

	private String getString(int site, Map<String, ExcelData> eds, String key) {
		String s = "";
	    String keyName = "$" + key + "$" + site;
	    if ((eds.get(keyName) != null) && (((ExcelData)eds.get(keyName)).getValue() != null)){
	    	s = s + (String)((ExcelData)eds.get(keyName)).getValue();
	    }
	    return s.trim();
	}

	@Override
	public void validate(ExcelData data, ExcelColumn column, Map arg2)
			throws ExcelException {
		String value = data.getValue().toString();
		boolean flag = false; boolean insert = false;
		if ("educationText".equalsIgnoreCase(column.getTable_column())){
			insert = true;
			for (Dic dic : this.educationList){
				if (value.equals(dic.getName())){
					flag = true;
					break;
				}
	       }
		}
		
		
		
		if ("normalTypeText".equalsIgnoreCase(column.getTable_column())){
			insert = true;
			for (Dic dic : this.normalList){
				if (value.equals(dic.getName())){
					flag = true;
					break;
				}
	       }
		}
		
		if ("recruitStudentTypeText".equalsIgnoreCase(column.getTable_column())){
			insert = true;
			for (Dic dic : this.recruitList){
				if (value.equals(dic.getName())){
					flag = true;
					break;
				}
	       }
		}
		if ("cultureTypeText".equalsIgnoreCase(column.getTable_column())){
			insert = true;
			for (Dic dic : this.cultureList){
				if (value.equals(dic.getName())){
					flag = true;
					break;
				}
			}
		}
		if ("difficultTypeText".equalsIgnoreCase(column.getTable_column())){
			insert = true;
			for (Dic dic : this.difficultTypeList){
				if (value.equals(dic.getName())){
					flag = true;
					break;
				}
			}
		}
		IStudentCommonService studentCommonService = (IStudentCommonService)SpringBeanLocator.getBean("com.uws.common.service.impl.StudentCommonServiceImpl");
		if ("stuNumber".equalsIgnoreCase(column.getTable_column())){
			insert = true;
			BigDecimal bd = new BigDecimal(value);
			String certNum = bd.toString();
			StudentInfoModel studentInfo = studentCommonService.queryStudentByStudentNo(certNum);
			boolean checkIsGraduateStudent = studentCommonService.checkIsGraduateStudent(certNum);
			if(studentInfo==null)
			{
				flag = false;
				if ((insert) && (!flag)){
					String isText = data.getId().replaceAll("\\$", "");
					throw new ExcelException(isText + "单元格属性值(" + certNum + ")在数据库中不存在，请严格按照选项进行选择；<br/>");
				}
			}else
			{
				flag = true;
				if(checkIsGraduateStudent == false)
				{
					String isText = data.getId().replaceAll("\\$", "");
					throw new ExcelException(isText + "单元格属性值(" + certNum + ")在数据库中不是毕业班的学生，请重新选择毕业生；<br/>");
				}
			}
		}
		
		if ((insert) && (!flag)){
			String isText = data.getId().replaceAll("\\$", "");
			throw new ExcelException(isText + "单元格属性值(" + data.getValue().toString() + ")在数据库中不存在，请严格按照选项进行选择；<br/>");
		}
		
	   }
	}
