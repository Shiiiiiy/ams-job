package com.uws.job.controller;

import java.util.Map;

import com.uws.common.service.IRewardCommonService;
import com.uws.common.service.IStudentCommonService;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.rule.IRule;
import com.uws.core.excel.vo.ExcelColumn;
import com.uws.core.excel.vo.ExcelData;
import com.uws.core.util.SpringBeanLocator;
import com.uws.domain.orientation.StudentInfoModel;

/**
 * @className SchoolGoodStudentRule.java
 * @package com.uws.job.controller
 * @description
 * @date 2015-12-16  下午5:03:57
 */
public class SchoolGoodStudentRule implements IRule {

	@Override
	public void format(ExcelData arg0, ExcelColumn arg1, Map arg2) {

	}

	@Override
	public void operation(ExcelData arg0, ExcelColumn arg1, Map arg2, Map<String, ExcelData> arg3, int arg4) {

	}

	@Override
	public void validate(ExcelData data, ExcelColumn column, Map arg2) throws ExcelException {
		boolean flag = false; 
		boolean insert = false;
		String value = data.getValue().toString();
		IStudentCommonService studentCommonService = (IStudentCommonService)SpringBeanLocator.getBean("com.uws.common.service.impl.StudentCommonServiceImpl");
		IRewardCommonService rewardCommonService = (IRewardCommonService)SpringBeanLocator.getBean("rewardCommonService");
		if ("stuId".equalsIgnoreCase(column.getTable_column())){
			StudentInfoModel studentInfo = studentCommonService.queryStudentByStudentNo(value);
			boolean checkIsGraduateStudent = studentCommonService.checkIsGraduateStudent(value);
			if(studentInfo==null){
				String isText = data.getId().replaceAll("\\$", "");
				throw new ExcelException(isText + "单元格属性值(" + value + ")在数据库中不存在，请严格按照选项进行选择；<br/>");
			}else{
				if(checkIsGraduateStudent == false){
					String isText = data.getId().replaceAll("\\$", "");
					throw new ExcelException(isText + "单元格属性值(" + value + ")在数据库中不是毕业班的学生，请重新选择毕业生；<br/>");
				}else{
					StudentInfoModel student = new StudentInfoModel();
					student.setId(value);
					if(!rewardCommonService.checkStuGetAwardOrNot(student) && !rewardCommonService.checkStuGetBurseOrNot(student) && !rewardCommonService.checkStuGetCollegeOrNot(student)){//判断是否获得过奖项
						String isText = data.getId().replaceAll("\\$", "");
						throw new ExcelException(isText + "单元格属性值(" + value + ")在数据库中未获得过任何奖学金，不符合校优秀生申请资格，请重新选择毕业生；<br/>");
					}
				}
			}
		}
		if ((insert) && (!flag)){
			String isText = data.getId().replaceAll("\\$", "");
			throw new ExcelException(isText + "单元格属性值(" + data.getValue().toString() + ")在数据库中不存在，请严格按照选项进行选择；<br/>");
		}
	}
	
}
