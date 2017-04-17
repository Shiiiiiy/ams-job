package com.uws.job.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.uws.common.service.IBaseDataService;
import com.uws.core.base.BaseController;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.core.excel.service.IExcelService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.core.util.SystemPropertiesUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.job.CompanyInfo;
import com.uws.domain.job.EmploymentScheme;
import com.uws.domain.job.SchoolGoodStudent;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.job.service.ICompanyService;
import com.uws.job.util.Constants;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.sys.util.MultipartFileValidator;
import com.uws.user.model.Org;
import com.uws.user.model.User;
import com.uws.user.service.IOrgService;
import com.uws.util.CheckUtils;

/**
 * @className CompanyController.java
 * @package com.uws.job.controller
 * @description
 * @date 2015-10-21  下午2:49:48
 */
@Controller
public class CompanyController extends BaseController {
	@Autowired
	private IOrgService orgService;
	@Autowired
	private IExcelService excelService;
	@Autowired
	private IBaseDataService baseDateService;
	@Autowired
	private ICompanyService companyService;
	private DicUtil dicUtil = DicFactory.getDicUtil();
	private FileUtil fileUtil = FileFactory.getFileUtil();
	//session
	private SessionUtil sessionUtil = SessionFactory.getSession(Constants.MENUKEY_JOB_COMPANY_INFO);
	
	private static final int QUERY_TYPE_JOB_OFFICE = 1;//查询方式--招就处查询
	private static final int QUERY_TYPE_NOT_JOB_OFFICE = 2;//查询方式--二级学院查询
	private static final int QUERY_TYPE = 3;//查询方式--招就处查询或二级学院查询
	private String companyManagerName = (String) SystemPropertiesUtil.getSystemConfigProperties().get("system.compnay.manager.name");
	/**
	 * 查询列表
	 * @param model
	 * @param request
	 * @param response
	 * @param companyInfoVO
	 * @return
	 */
	@RequestMapping("/job/companyInfo/opt-query/queryCompanyInfoList")
	public String queryEmploymentScheme(ModelMap model,HttpServletRequest request,HttpServletResponse response,CompanyInfo companyInfoVO){
		String strPageNo = request.getParameter("pageNo");
		int pageNo = DataUtil.isNotNull(strPageNo) ? Integer.parseInt(strPageNo) : 1;
		List<BaseAcademyModel> colleges =  this.baseDateService.listBaseAcademy();
		List<BaseAcademyModel> collegesCopy = new ArrayList<BaseAcademyModel>();
		List<Dic> companyProertys = this.dicUtil.getDicInfoList("COMPANY_PROPERTY");
		String collegeId = (String)request.getSession().getAttribute("_teacher_orgId");
		
		boolean  isJobOffice = true;//查询用户是招就处
		int queryType = 0;
		if(CheckUtils.isCurrentOrgEqCollege(collegeId)){
			isJobOffice = false;
			for (int i = 0; i < colleges.size(); i++) {
				if(DataUtil.isEquals(collegeId, colleges.get(i).getId())){
					collegesCopy.add(colleges.get(i));
				}
			}
			model.addAttribute("colleges", collegesCopy);
		}else{
			model.addAttribute("colleges", colleges);
		}
		if(isJobOffice){
			queryType = QUERY_TYPE_JOB_OFFICE;
		}else{
			if(!DataUtil.isNotNull(companyInfoVO.getCollege()) || DataUtil.isNull(companyInfoVO.getCollege().getId())){
				queryType = QUERY_TYPE_NOT_JOB_OFFICE;
			}else{
				queryType = QUERY_TYPE;
			}
		}
		Page page = this.companyService.queryCompanyInfoPage(companyInfoVO, Page.DEFAULT_PAGE_SIZE, pageNo,queryType,collegeId);
		model.addAttribute("page", page);
		model.addAttribute("collegeId", collegeId);
		model.addAttribute("isJobOffice", isJobOffice);
		model.addAttribute("companyInfoVO", companyInfoVO);
		model.addAttribute("companyProertys", companyProertys);
		model.addAttribute("companyManagerName", companyManagerName);
		return Constants.MENUKEY_JOB_COMPANY_INFO + "listCompany";
	}
	/**
	 * 添加跳转页面
	 * @param model
	 * @param request
	 * @param response
	 * @param companyInfoVO
	 * @return
	 */
	@RequestMapping("/job/companyInfo/opt-add/editCompanyInfo")
	public String editCompanyInfo(ModelMap model,HttpServletRequest request,HttpServletResponse response,CompanyInfo companyInfoVO){
		List<Dic> companyProertys = this.dicUtil.getDicInfoList("COMPANY_PROPERTY");
		List<Dic> isSchoolCompanyProtocols = this.dicUtil.getDicInfoList("IS_SCHOOL_COMPANY_PROTOCOL");//
		List<Dic> isBatchWorks = this.dicUtil.getDicInfoList("IS_BATCH_WORK");
		List<BaseAcademyModel> colleges =  this.baseDateService.listBaseAcademy();
		String collegeId = (String)request.getSession().getAttribute("_teacher_orgId");
		boolean  isJobOffice = true;//查询用户是招就处
		if(CheckUtils.isCurrentOrgEqCollege(collegeId)){
			Org college = new Org();
			college.setId(collegeId);
			companyInfoVO.setCollege(college);
			model.addAttribute("companyInfoVO", companyInfoVO);
			isJobOffice = false;
		}
		if(DataUtil.isNotNull(companyInfoVO) && DataUtil.isNotNull(companyInfoVO.getId())){//修改
			companyInfoVO = this.companyService.queryCompanyInfoById(companyInfoVO.getId());
			model.addAttribute("companyInfoVO", companyInfoVO);
		}
		model.addAttribute("colleges", colleges);
		model.addAttribute("isJobOffice", isJobOffice);
		model.addAttribute("isBatchWorks", isBatchWorks);
		model.addAttribute("companyProertys", companyProertys);
		model.addAttribute("isSchoolCompanyProtocols", isSchoolCompanyProtocols);
		model.addAttribute("companyManagerName", companyManagerName);
		return Constants.MENUKEY_JOB_COMPANY_INFO + "editCompany";
	}
	/**
	 * 增加--修改:
	 * 	添加前先排重：按学院、企业名称和企业性质排重；
	 * 	由于前台页面的企业名称和企业性质不能修改，故修改时不存在重复数据
	 * @param model
	 * @param request
	 * @param response
	 * @param companyInfoVO
	 * @return
	 */
	@RequestMapping("/job/companyInfo/opt-save/saveCompanyInfo")
	public String saveCompanyInfo(ModelMap model,HttpServletRequest request,HttpServletResponse response,CompanyInfo companyInfoVO){
		CompanyInfo companyInfoPO = new CompanyInfo();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if(DataUtil.isNotNull(companyInfoVO.getId())){//修改 
			companyInfoPO = this.companyService.queryCompanyInfoById(companyInfoVO.getId());
			BeanUtils.copyProperties(companyInfoVO, companyInfoPO,new String[]{"id","college","companyName","companyProerty","delState","createTime","creator"});
			try {
				companyInfoPO.setUpdateDate(dateFormat.parse(companyInfoVO.getUpdateDateText()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			this.companyService.updateCompanyInfo(companyInfoPO);
		}else{//添加
			BeanUtils.copyProperties(companyInfoVO, companyInfoPO);
			String collegeId = (String)request.getSession().getAttribute("_teacher_orgId");
			if(CheckUtils.isCurrentOrgEqCollege(collegeId)){
				Org college = new Org();
				college.setId(collegeId);
				companyInfoPO.setCollege(college);
			}else{
				if(!DataUtil.isNotNull(companyInfoVO.getCollege().getId())){//如果没有填写所属学院,则认为是招就处添加
					companyInfoPO.getCollege().setId(Constants.JOBOFFICEID);
				}
			}
			try {
				companyInfoPO.setUpdateDate(dateFormat.parse(companyInfoVO.getUpdateDateText()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			companyInfoPO.setDelState(Constants.STATUS_NORMAL_DICS);
			User creator = new User(this.sessionUtil.getCurrentUserId()); 
			companyInfoPO.setCreator(creator);
			this.companyService.saveCompanyInfo(companyInfoPO);
		}
		return "redirect:/job/companyInfo/opt-query/queryCompanyInfoList.do";
	}
	/**
	 * 逻辑删除
	 * @param model
	 * @param request
	 * @param response
	 * @param companyInfoVO
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/job/companyInfo/opt-del/delCompanyInfo"},produces={"text/plain;charset=UTF-8"})
	public String delCompanyInfo(ModelMap model,HttpServletRequest request,HttpServletResponse response,CompanyInfo companyInfoVO){
		if(DataUtil.isNotNull(companyInfoVO) && DataUtil.isNotNull(companyInfoVO.getId())){
			CompanyInfo companyInfoPO = this.companyService.queryCompanyInfoById(companyInfoVO.getId());
			companyInfoPO.setDelState(Constants.STATUS_DELETED_DICS);
			this.companyService.updateCompanyInfo(companyInfoPO);
			return "true";
		}
		return "false";
	}
	/**
	 * 查看
	 * @param model
	 * @param request
	 * @param response
	 * @param companyInfoVO
	 * @return
	 */
	@RequestMapping("/job/companyInfo/opt-query/viewCompanyInfo")
	public String viewCompanyInfo(ModelMap model,HttpServletRequest request,HttpServletResponse response,CompanyInfo companyInfoVO){
		if(DataUtil.isNotNull(companyInfoVO) && DataUtil.isNotNull(companyInfoVO.getId())){
			companyInfoVO = this.companyService.queryCompanyInfoById(companyInfoVO.getId());
			model.addAttribute("companyInfoVO", companyInfoVO);
		}
		model.addAttribute("companyManagerName", companyManagerName);
		return Constants.MENUKEY_JOB_COMPANY_INFO + "viewCompany";
	}
	/**
	 * TODO:将来考虑修改  企业名称和企业性质时 排除重复
	 * 判断是否存在CompanyInfo
	 * @param model
	 * @param request
	 * @param response
	 * @param companyInfoVO
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/job/companyInfo/opt-query/isExitCompanyInfo"},produces={"text/plain;charset=UTF-8"})
	public String isExitCompanyInfo(ModelMap model,HttpServletRequest request,HttpServletResponse response,CompanyInfo companyInfoVO){
		if(!DataUtil.isNotNull(companyInfoVO.getCollege().getId())){//如果没有填写所属学院,则认为是招就处添加
			companyInfoVO.getCollege().setId(Constants.JOBOFFICEID);
		}
		if(this.queryCompanyInfoByCond(companyInfoVO)){
			return "true";
		}else{
			return "false";
		}
	}
	/**
	 * 导入跳转
	 * @param model
	 * @param request
	 * @param response
	 * @param employmentSchemeVO
	 * @return
	 */
	@RequestMapping("/job/companyInfo/opt-query/importCompanyInfoInit")
	public String importEmploymentSchemeInit(ModelMap model,HttpServletRequest request,HttpServletResponse response,EmploymentScheme employmentSchemeVO){
		return Constants.MENUKEY_JOB_COMPANY_INFO + "importCompany";
	}
	/**
	 * 导入
	 * @param model
	 * @param file
	 * @param maxSize
	 * @param allowedExt
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping("/job/companyInfo/opt-query/importCompanyInfo")
	public String importEmploymentScheme(ModelMap model, @RequestParam("file") MultipartFile file,String maxSize, String allowedExt, HttpServletRequest request, HttpSession session){
		List errorText = new ArrayList();
		MultipartFileValidator validator = new MultipartFileValidator();
		if (org.apache.commons.lang.StringUtils.isNotEmpty(allowedExt)) {
			validator.setAllowedExtStr(allowedExt.toLowerCase());
		}
		if (org.apache.commons.lang.StringUtils.isNotEmpty(maxSize))
			validator.setMaxSize(Long.valueOf(maxSize).longValue());
		else {
			validator.setMaxSize(setMaxSize());
		}
		String returnValue = validator.validate(file);
		if (!returnValue.equals("")) {
			errorText.add(returnValue);
			model.addAttribute("errorText", errorText);
			model.addAttribute("importFlag", Boolean.valueOf(true));
		}
		String tempFileId = this.fileUtil.saveSingleFile(true, file);
		File tempFile = this.fileUtil.getTempRealFile(tempFileId);
		String filePath = tempFile.getAbsolutePath();
		session.setAttribute("filePath", filePath);
		ImportUtil iu = new ImportUtil();
		try {
			String collegeId = (String)request.getSession().getAttribute("_teacher_orgId");
			Org teacherOrg = this.orgService.queryOrgById(collegeId);
			List<Org> orgs = this.orgService.queryOrg();
			List<CompanyInfo> companyInfos = iu.getDataList(filePath, "importCompanyInfo", null, CompanyInfo.class);
			boolean isJobOffice = false;//不是招就处教师
			boolean isSaveOrUpdate = true;//是否执行保存或更新
			if(!CheckUtils.isCurrentOrgEqCollege(collegeId)){
				isJobOffice = true;
			}
			for (CompanyInfo companyInfo : companyInfos) {
				if(isJobOffice){//招就处能够给任何学院导入企业信息，如果导入的企业信息中所属学院为空，则将所属学院设置为招就处
					if(DataUtil.isNotNull(companyInfo.getCollegeText())){
						for (Org org : orgs) {
							if(org.getName().startsWith(companyInfo.getCollegeText())){
								companyInfo.setCollege(org);
							}
						}
					}else{
						companyInfo.setCollege(teacherOrg);
					}
				}else{//非招就处的教师只能导入本学院的企业信息
					if(DataUtil.isNotNull(companyInfo.getCollegeText())){
						if(teacherOrg.getName().startsWith(companyInfo.getCollegeText())){
							companyInfo.setCollege(teacherOrg);
						}else{
							isSaveOrUpdate = false;
							errorText.add("您导入的数据中包括不是您学院的企业信息，请将不是您学院的企业信息删除或修改后导入！</br>");
						}
					}else{
						isSaveOrUpdate = false;
						errorText.add("您导入的数据中存在没有填写所属学院的企业信息，请在数据中填写您所在的学院！</br>");
					}
				}
			}
			if(isSaveOrUpdate){
				//如果数据库中存在相同的数据，则将相同的数据更新保存，不存在相同的数据则将数据直接保存
				this.companyService.saveOrUpdateCompanyInfoList(companyInfos, new User(this.sessionUtil.getCurrentUserId()));
			}
		}catch (ExcelException e) {
			errorText = e.getMessageList();
			errorText = errorText.subList(0, errorText.size() > 20 ? 20 : errorText.size());
			model.addAttribute("errorText", errorText);
		} catch (InstantiationException e) {
			e.printStackTrace(); 
		} catch (IOException e) {
			e.printStackTrace(); 
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			errorText.add("模板配置与数据类型不一致,请与系统管理员联系!");
			errorText = errorText.subList(0, errorText.size() > 20 ? 20 : errorText.size());
			model.addAttribute("errorText", errorText);
		}
		model.addAttribute("errorText", errorText);
		model.addAttribute("importFlag", Boolean.valueOf(true));
		return Constants.MENUKEY_JOB_COMPANY_INFO + "importCompany";
	}
	/**
	 * 计算导出页数
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/job/companyInfo/nsm/exportCompanyInfoPage")
	public String exportSchoolGoodStudentPage(ModelMap model,HttpServletRequest request,HttpServletResponse response){
		int exportSize = Integer.valueOf(request.getParameter("exportSize")).intValue();
		int pageTotalCount = Integer.valueOf(request.getParameter("pageTotalCount")).intValue();
		int maxNumber = 0;
		if(pageTotalCount < exportSize){
			maxNumber = 1;
		}else if (pageTotalCount % exportSize == 0)
			maxNumber = pageTotalCount / exportSize;
		else{
			maxNumber = pageTotalCount / exportSize + 1;
		}
		model.addAttribute("exportSize", Integer.valueOf(exportSize));
		model.addAttribute("maxNumber", Integer.valueOf(maxNumber));
		if (maxNumber <= 500)
			model.addAttribute("isMore", "false");
		else {
			model.addAttribute("isMore", "true");
		}
		return Constants.MENUKEY_JOB_COMPANY_INFO+ "exportCompany";
	}
	/**
	 * 按查询条件导出Excel
	 * @param modelMap
	 * @param request
	 * @param response
	 * @param temporaryWorkStudyModel
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/job/companyInfo/nsm/exportCompanyInfo")
	public void exportSchoolGoodStudent(ModelMap model, HttpServletRequest request,HttpServletResponse response, CompanyInfo companyInfoVO,String userQuery_exportSize,String userQuery_exportPage){
		int pageSize = userQuery_exportSize != null ? Integer.parseInt(userQuery_exportSize) : 1000;
		int pageNo = userQuery_exportPage != null ? Integer.parseInt(userQuery_exportPage) : 1;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String collegeId = (String)request.getSession().getAttribute("_teacher_orgId");
		List<BaseAcademyModel> colleges =  this.baseDateService.listBaseAcademy();
		List<BaseAcademyModel> collegesCopy = new ArrayList<BaseAcademyModel>();
//		boolean  isJobOffice = true;//查询用户是招就处
//		if(CheckUtils.isCurrentOrgEqCollege(collegeId)){
//			Org college = new Org();
//			college.setId(collegeId);
//			companyInfoVO.setCollege(college);
//			model.addAttribute("companyInfoVO", companyInfoVO);
//			isJobOffice = false;
//		}
//		if(DataUtil.isNotNull(companyInfoVO) && DataUtil.isNotNull(companyInfoVO.getId())){//修改
//			companyInfoVO = this.companyService.queryCompanyInfoById(companyInfoVO.getId());
			
//		}
		boolean  isJobOffice = true;//查询用户是招就处
		int queryType = 0;
		if(CheckUtils.isCurrentOrgEqCollege(collegeId)){
			isJobOffice = false;
			for (int i = 0; i < colleges.size(); i++) {
				if(DataUtil.isEquals(collegeId, colleges.get(i).getId())){
					collegesCopy.add(colleges.get(i));
				}
			}
			model.addAttribute("colleges", collegesCopy);
		}else{
			model.addAttribute("colleges", colleges);
		}
		if(isJobOffice){
			queryType = QUERY_TYPE_JOB_OFFICE;
		}else{
			if(!DataUtil.isNotNull(companyInfoVO.getCollege()) || DataUtil.isNull(companyInfoVO.getCollege().getId())){
				queryType = QUERY_TYPE_NOT_JOB_OFFICE;
			}else{
				queryType = QUERY_TYPE;
			}
		}
		model.addAttribute("companyInfoVO", companyInfoVO);
		model.addAttribute("companyManagerName", companyManagerName);
		List<CompanyInfo> companyInfos = (List<CompanyInfo>) this.companyService.queryCompanyInfoPage(companyInfoVO, pageSize, pageNo,queryType,collegeId).getResult();
		List exportDataList = new ArrayList();
		for (int i = 0; i < companyInfos.size(); i++) {
			Map<String,Object> map = new HashMap<String, Object>();
			CompanyInfo companyInfo = companyInfos.get(i);
			map.put("index", i+1);
			if(DataUtil.isNotNull(companyInfo.getCollege())){
				if(DataUtil.isEquals(Constants.JOBOFFICEID, companyInfo.getCollege().getId())){
					map.put("collegeText", companyManagerName);
				}else{
					map.put("collegeText", companyInfo.getCollege().getName());
				}
			}
			if(DataUtil.isNotNull(companyInfo.getCompanyName())){
				map.put("companyName", companyInfo.getCompanyName());
			}
			if(DataUtil.isNotNull(companyInfo.getCompanyProerty())){
				map.put("companyProertyText", companyInfo.getCompanyProerty().getName());
			}
			if(DataUtil.isNotNull(companyInfo.getAddress())){
				map.put("address", companyInfo.getAddress());
			}
			if(DataUtil.isNotNull(companyInfo.getContactPerson())){
				map.put("contactPerson", companyInfo.getContactPerson());
			}
			if(DataUtil.isNotNull(companyInfo.getContactPost())){
				map.put("contactPost", companyInfo.getContactPost());
			}
			if(DataUtil.isNotNull(companyInfo.getContactPhone())){
				map.put("contactPhone", companyInfo.getContactPhone());
			}
			if(DataUtil.isNotNull(companyInfo.getPhoneNum())){
				map.put("phoneNum", companyInfo.getPhoneNum());
			}
			if(DataUtil.isNotNull(companyInfo.getNeedWork())){
				map.put("needWork", companyInfo.getNeedWork());
			}
			if(DataUtil.isNotNull(companyInfo.getIsSchoolCompanyProtocol())){
				map.put("isSchoolCompanyProtocolText", companyInfo.getIsSchoolCompanyProtocol().getName());
			}
			if(DataUtil.isNotNull(companyInfo.getIsBatchWork())){
				map.put("isBatchWorkText", companyInfo.getIsBatchWork().getName());
			}
			if(DataUtil.isNotNull(companyInfo.getUpdateDate())){
				map.put("updateDateText", dateFormat.format(companyInfo.getUpdateDate()));
			}
			exportDataList.add(map);
		}
		HSSFWorkbook wb;
		try {
			wb = this.excelService.exportData("export_company_info.xls","exportCompanyInfo", exportDataList);
			String filename = "企业信息导出表.xls";
			response.setContentType("application/x-excel");
			response.setHeader("Content-disposition", "attachment;filename=" + new String(filename.getBytes("GBK"), "iso-8859-1"));
			response.setCharacterEncoding("UTF-8");
			OutputStream ouputStream = response.getOutputStream();
			wb.write(ouputStream);
			ouputStream.flush();
			ouputStream.close();
		} catch (ExcelException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 得到上传文件的大小 2MB
	 * @return
	 */
	private int setMaxSize() {
		return 20971520;
	}
	/**
	 * 条件查询是否存在
	 * @param companyInfo
	 * @return
	 */
	private boolean queryCompanyInfoByCond(CompanyInfo companyInfo){
		List<CompanyInfo> companyInfos = this.companyService.queryCompanyInfoByCond(companyInfo);
		if(companyInfos == null || companyInfos.size() == 0){
			return false;
		}
		return true;
	}
}
