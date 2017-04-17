package com.uws.job.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.rule.IRule;
import com.uws.core.excel.vo.ExcelColumn;
import com.uws.core.excel.vo.ExcelData;
import com.uws.core.util.DataUtil;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.user.model.Org;
import com.uws.user.service.IOrgService;

public class CompanyInfoRule implements IRule {

	private DicUtil dicUtil;
	private IOrgService orgService;
	private List<Org> colleges;
	private List<Dic> companyProertys;
	private List<Dic> isSchoolCompanyProtocols;
	private List<Dic> isBatchWorks;
	
	public CompanyInfoRule() {
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		orgService = (IOrgService) wac.getBean("orgService");
		this.dicUtil = DicFactory.getDicUtil();
		this.colleges = this.orgService.queryOrg();
		this.companyProertys = this.dicUtil.getDicInfoList("COMPANY_PROPERTY");
		this.isSchoolCompanyProtocols = this.dicUtil.getDicInfoList("IS_SCHOOL_COMPANY_PROTOCOL");
		this.isBatchWorks = this.dicUtil.getDicInfoList("IS_BATCH_WORK");
	}

	@Override
	public void format(ExcelData excelData, ExcelColumn excelColumn, Map map) {
		
	}

	@Override
	public void operation(ExcelData exceldata, ExcelColumn excelcolumn, Map map, Map<String, ExcelData> map1, int i) {
		if("companyProertyValue".equals(excelcolumn.getName())){
			String companyProertyValue = getString(i, map1, "D");
			for (Dic companyProerty : companyProertys) {
				if(companyProerty.getName().equals(companyProertyValue)){
					exceldata.setValue(companyProerty);
					break;
				}
			}
		}
		if("isSchoolCompanyProtocolValue".equals(excelcolumn.getName())){
			String isSchoolCompanyProtocolValue = getString(i, map1, "K");
			for (Dic isSchoolCompanyProtocol : isSchoolCompanyProtocols) {
				if(isSchoolCompanyProtocol.getName().equals(isSchoolCompanyProtocolValue)){
					exceldata.setValue(isSchoolCompanyProtocol);
					break;
				}
			}
		}
		if("isBatchWorkValue".equals(excelcolumn.getName())){
			String isBatchWorkValue = getString(i, map1, "L");
			for (Dic isBatchWork : isBatchWorks) {
				if(isBatchWork.getName().equals(isBatchWorkValue)){
					exceldata.setValue(isBatchWork);
					break;
				}
			}
		}
	}
	@Override
	public void validate(ExcelData exceldata, ExcelColumn excelcolumn, Map map) throws ExcelException {
		boolean flag = false;
		boolean isExitColumn = false;
		String value = exceldata.getValue().toString();
		if("collegeText".equalsIgnoreCase(excelcolumn.getTable_column())){
			isExitColumn = true;
			for (Org college : colleges) {
				if(DataUtil.isNull(value) || college.getName().startsWith(value)){//允许所属学院为空
					flag = true;
					break;
				}
			}
		}
		if("companyProertyText".equalsIgnoreCase(excelcolumn.getTable_column())){
			isExitColumn = true;
			for (Dic companyProerty : companyProertys) {
				if(value.equals(companyProerty.getName())){
					flag = true;
					break;
				}
			}
		}
		if("isSchoolCompanyProtocolText".equalsIgnoreCase(excelcolumn.getTable_column())){
			isExitColumn = true;
			for (Dic isSchoolCompanyProtocol : isSchoolCompanyProtocols) {
				if(value.equals(isSchoolCompanyProtocol.getName())){
					flag = true;
					break;
				}
			}
		}
		if("isBatchWorkText".equalsIgnoreCase(excelcolumn.getTable_column())){
			isExitColumn = true;
			for (Dic isBatchWork : isBatchWorks) {
				if(value.equals(isBatchWork.getName())){
					flag = true;
					break;
				}
			}
		}
		if("contactPhone".equalsIgnoreCase(excelcolumn.getTable_column())){
			Pattern phonePattern = Pattern.compile("^0[0-9]{2,3}\\-?([2-9][0-9]{6,8})+(\\-[0-9]{1,4})?$");
			if(DataUtil.isNotNull(value)){
				if(!phonePattern.matcher(value).matches()){
					throw new ExcelException("单元格属性值("+exceldata.getId().replaceAll("\\$", "")+")格式不正确：区号-号码-分机号（分机号可不填）</br>");
				}
			}
		}
		if("phoneNum".equalsIgnoreCase(excelcolumn.getTable_column())){
			Pattern telPattern = Pattern.compile("^13[0123456789]{1}[0-9]{8}|15[012356789]{1}[0-9]{8}|18[012356789]{1}[0-9]{8}$");
			if(DataUtil.isNotNull(value)){
				if(!telPattern.matcher(value).matches()){
					throw new ExcelException("单元格属性值("+exceldata.getId().replaceAll("\\$", "")+")\u624B\u673A\u683C\u5F0F\u4E0D\u6B63\u786E</br>");
				}
			}
		}
		if("updateDateText".equalsIgnoreCase(excelcolumn.getTable_column())){
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				dateFormat.parse(value);
			} catch (ParseException e) {
				throw new ExcelException("单元格属性值("+exceldata.getId().replaceAll("\\$", "")+")日期格式不正确</br>");
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
