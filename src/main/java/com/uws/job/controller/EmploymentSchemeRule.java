package com.uws.job.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.uws.common.service.IStudentCommonService;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.rule.IRule;
import com.uws.core.excel.vo.ExcelColumn;
import com.uws.core.excel.vo.ExcelData;
import com.uws.core.util.DataUtil;
import com.uws.core.util.SpringBeanLocator;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;

/**
 * @className EmploymentSchemeRule.java
 * @package com.uws.job.controller
 * @description
 * @author lizj
 * @date 2015-10-15  上午11:11:04
 */
public class EmploymentSchemeRule implements IRule {
	
	private DicUtil dicUtil;
	
	private List<Dic> graduateCodeDics;
	
	List<Dic> yesOrNoDics;
	
	public EmploymentSchemeRule() {
		this.dicUtil = DicFactory.getDicUtil();
		this.graduateCodeDics = dicUtil.getDicInfoList("GRADUATE_CODE");
		this.yesOrNoDics = dicUtil.getDicInfoList("Y&N");
	}

	@Override
	public void format(ExcelData exceldata, ExcelColumn excelcolumn, Map map) {
		
	}

	@Override
	public void operation(ExcelData exceldata, ExcelColumn excelcolumn, Map map, Map<String, ExcelData> map1, int i) {
		if("isFileSchoolValue".equals(excelcolumn.getName())){
			for (Dic yesOrNoDic : yesOrNoDics) {
				String yesOrNoValue = getString(i, map1, "AJ");
				if(yesOrNoDic.getName().equals(yesOrNoValue)){
					exceldata.setValue(yesOrNoDic);
					break;
				}
			}
		}
		if("isAccountSchoolValue".equals(excelcolumn.getName())){
			for (Dic yesOrNoDic : yesOrNoDics) {
				String yesOrNoValue = getString(i, map1, "AL");
				if(yesOrNoDic.getName().equals(yesOrNoValue)){
					exceldata.setValue(yesOrNoDic);
					break;
				}
			}
		}
		if("graduateCodeValue".equals(excelcolumn.getName())){
			for (Dic graduateCode : graduateCodeDics) {
				String graduateCodeValue = getString(i, map1, "AM");
				if(graduateCode.getCode().equalsIgnoreCase(graduateCodeValue)){
					exceldata.setValue(graduateCode);
					break;
				}
			}
		}
	}

	@Override
	public void validate(ExcelData exceldata, ExcelColumn excelcolumn, Map map) throws ExcelException {
		//判断导入的学生是否存在
		if("stuNo".equalsIgnoreCase(excelcolumn.getTable_column())){
			IStudentCommonService studentCommonService = (IStudentCommonService)SpringBeanLocator.getBean("com.uws.common.service.impl.StudentCommonServiceImpl");
			String stuNo = exceldata.getValue().toString();
			BigDecimal bd = new BigDecimal(stuNo);
			String stuNum = bd.toString();
			StudentInfoModel studentInfoModel = studentCommonService.queryStudentByStudentNo(stuNum);
			if(!DataUtil.isNotNull(studentInfoModel)){
				String isText = exceldata.getId().replaceAll("\\$", "");
				throw new ExcelException(isText + "单元格值("+ stuNum + ")与在系统中没有找到匹配的学号，请修正后重新上传；<br/>");
			}else{
				boolean checkIsGraduateStudent = studentCommonService.checkIsGraduateStudent(stuNum);
				if(!checkIsGraduateStudent){
					String isText = exceldata.getId().replaceAll("\\$", "");
					throw new ExcelException(isText + "单元格属性值(" + stuNum + ")在数据库中不是毕业班的学生，请重新选择毕业生；<br/>");
				}
			}
		}
		boolean flag = false;
		boolean isExitColumn = false;
		String value = exceldata.getValue().toString();
		if("graduateCodeText".equalsIgnoreCase(excelcolumn.getTable_column())){
			isExitColumn = true;
			for (Dic graduateCode : graduateCodeDics) {
				if(value.equals(graduateCode.getCode())){
					flag = true;
					break;
				}
			}
		}
		if("isFileSchoolText".equalsIgnoreCase(excelcolumn.getTable_column())){
			isExitColumn = true;
			for (Dic yesOrNoDic : yesOrNoDics) {
				if(value.equals(yesOrNoDic.getName())){
					flag = true;
					break;
				}
			}
		}
		if("isAccountSchoolText".equalsIgnoreCase(excelcolumn.getTable_column())){
			isExitColumn = true;
			for (Dic yesOrNoDic : yesOrNoDics) {
				if(value.equals(yesOrNoDic.getName())){
					flag = true;
					break;
				}
			}
		}
		if(isExitColumn && !flag){
			String isText = exceldata.getId().replaceAll("\\$", "");
			throw new ExcelException((new StringBuilder(String.valueOf(isText))).append("\u5355\u5143\u683C\u5C5E\u6027\u503C(").append(exceldata.getValue().toString()).append(")\u7CFB\u7EDF\u4E2D\u672A\u5B9A\u4E49\u6B64\u6570\u636E\uFF0C\u8BF7\u68C0\u67E5\u5BFC\u5165\u6570\u636E\u7684\u6B63\u786E\u6027\u6216\u8054\u7CFB\u7BA1\u7406\u5458\uFF01<br/>").toString());
		} else {
			return;
		}
	}
	
	private String getString(int site, Map eds, String key){
		String s = "";
		String keyName = (new StringBuilder("$")).append(key).append("$").append(site).toString();
		if(eds.get(keyName) != null && ((ExcelData)eds.get(keyName)).getValue() != null)
			s = (new StringBuilder(String.valueOf(s))).append((String)((ExcelData)eds.get(keyName)).getValue()).toString();
		return s.trim();
	}
}
