package com.uws.job.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.uws.common.service.IStudentCommonService;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.rule.IRule;
import com.uws.core.excel.vo.ExcelColumn;
import com.uws.core.excel.vo.ExcelData;
import com.uws.core.util.SpringBeanLocator;
import com.uws.core.util.StringUtils;
import com.uws.domain.job.AgreementModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.job.service.IAgreementService;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;

/**
 * 
* @ClassName: AgreementRule 
* @Description: TODO(对就业信息编号导入的校验) 
* @author 联合永道
* @date 2015-10-12 下午2:38:37 
*
 */
public class AgreementRule implements IRule {
	
	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	private static String yearId;
	private String getString(int site, Map<String, ExcelData> eds, String key) {
		String s = "";
		String keyName = "$" + key + "$" + site;
		if ((eds.get(keyName) != null) && (((ExcelData) eds.get(keyName)).getValue() != null)) {
			s = s + (String) ((ExcelData) eds.get(keyName)).getValue();
		}
		return s.trim();
	}

	@Override
	public void format(ExcelData arg0, ExcelColumn arg1, Map arg2) {

	}


	/**
	 * 描述信息: (学号校验，查看系统中是否有该学生)
	 * @param data
	 * @param column
	 * @param arg2
	 * @throws ExcelException
	 * @see com.uws.core.excel.rule.IRule#validate(com.uws.core.excel.vo.ExcelData, com.uws.core.excel.vo.ExcelColumn, java.util.Map)
	 */
	public void validate(ExcelData data, ExcelColumn column, Map map) throws ExcelException {
		IStudentCommonService studentCommonService = (IStudentCommonService)SpringBeanLocator.getBean("com.uws.common.service.impl.StudentCommonServiceImpl");
		IAgreementService agreementService = (IAgreementService)SpringBeanLocator.getBean("com.uws.job.service.impl.agreementService");
		
		if ("学号".equals(column.getName())){
			String number = data.getValue().toString();
			BigDecimal bd = new BigDecimal(number);
			String certNum = bd.toString();
			StudentInfoModel studentInfo = studentCommonService.queryStudentByStudentNo(certNum);
			if (studentInfo == null) {
				String isText = data.getId().replaceAll("\\$", "");
				throw new ExcelException(isText + "单元格值("
						+ certNum
						+ ")与在系统中没有找到匹配的学号，请修正后重新上传；<br/>");
			}
		}
		
		
		
		
		if ("学年".equals(column.getName())) {
			boolean flag = false;
			String yearValue = data.getValue().toString();
			List<Dic> dicYear = this.dicUtil.getDicInfoList("YEAR");
			for (Dic dic : dicYear){
				if (yearValue.equals(dic.getName())){
					yearId = dic.getId();
					flag = true;
					break;
				}
			}
			if( !flag){
				String isText = data.getId().replaceAll("\\$", "");
				throw new ExcelException(isText + "单元格属性值(" + data.getValue().toString() + ")与在系统中没有找到匹配的学年，请修正后重新上传；<br/>");
			}
		}
		
		if ("就业协议书编号".equals(column.getName())){
			String code = data.getValue().toString();
			//	BigDecimal bd = new BigDecimal(code);
			String agreementCode = code.toString();
			AgreementModel agreement = agreementService.queryAgreementByCode(yearId,agreementCode);
			if (agreement != null && StringUtils.hasText(agreement.getId())) {
				String isText = data.getId().replaceAll("\\$", "");
				throw new ExcelException(isText + "单元格值("
						+ agreementCode
						+ ")在该学年中已存在，请修正后重新上传；<br/>");
			}
		}
		
	}
	
	/**
	 * 描述信息: (把学年处理成数据字典类型)
	 * @param data
	 * @param column
	 * @param arg2
	 * @param eds
	 * @param site
	 * @see com.uws.core.excel.rule.IRule#operation(com.uws.core.excel.vo.ExcelData, com.uws.core.excel.vo.ExcelColumn, java.util.Map, java.util.Map, int)
	 */
	@Override
	public void operation(ExcelData data, ExcelColumn column, Map arg2,
			Map<String, ExcelData> eds, int site) {
		
		if ("employmentYear".equals(column.getName())) {
			List<Dic> dicYear = this.dicUtil.getDicInfoList("YEAR");
			String yearValue = getString(site, eds, "C");
			for (Dic dic : dicYear){
				if (yearValue.equals(dic.getName()) ) {
					data.setValue(dic);
					break;
				} 
			}
		}

	}

}
