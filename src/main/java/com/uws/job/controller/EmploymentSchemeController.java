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

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.uws.common.service.IBaseDataService;
import com.uws.common.service.IStudentCommonService;
import com.uws.common.util.SchoolYearUtil;
import com.uws.comp.service.ICompService;
import com.uws.core.base.BaseController;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.core.excel.service.IExcelService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.job.EmploymentScheme;
import com.uws.domain.job.EmploymentSchemeClassView;
import com.uws.domain.job.EmploymentSchemeCollegeView;
import com.uws.domain.job.EmploymentSchemeMajorView;
import com.uws.domain.job.EmploymentSchemeView;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.job.service.IEmploymentSchemeService;
import com.uws.job.util.Constants;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.IDicService;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.sys.util.MultipartFileValidator;
import com.uws.util.CheckUtils;
import com.uws.util.ProjectSessionUtils;

/**
 * @className EmploymentSchemeController.java
 * @package com.uws.job.controller
 * @description
 * @date 2015-10-10  下午2:59:35
 */
@Controller
public class EmploymentSchemeController extends BaseController {
	
	@Autowired
	private ICompService compService;
	@Autowired
	private IExcelService excelService;
	@Autowired
	private IBaseDataService baseDateService;
	private DicUtil dicUtil = DicFactory.getDicUtil();
	@Autowired
	private IDicService dicService;
	private SessionUtil sessionUtil = SessionFactory.getSession(Constants.MENUKEY_JOB_EMPLOYMENT_SCHEME);
	@Autowired
	private IStudentCommonService studentCommonService;
	private FileUtil fileUtil = FileFactory.getFileUtil();
	@Autowired
	private IEmploymentSchemeService employmentSchemeService;
	@RequestMapping("/job/employmentScheme/opt-query/queryEmploymentSchemeList")
	public String queryEmploymentScheme(ModelMap model,HttpSession session,HttpServletRequest request,HttpServletResponse response,EmploymentScheme employmentSchemeVO){
		String strPageNo = request.getParameter("pageNo");
		int pageNo = DataUtil.isNotNull(strPageNo) ? Integer.parseInt(strPageNo) : 1;
		List<BaseAcademyModel> colleges =  this.baseDateService.listBaseAcademy();
		List<BaseMajorModel> majors = new ArrayList<BaseMajorModel>();
		List<BaseClassModel> classes = new ArrayList<BaseClassModel>();
		String orgId = (String)request.getSession().getAttribute("_teacher_orgId");
		List<Dic> graduateCodeList = this.dicUtil.getDicInfoList("GRADUATE_CODE");
		//判断用户是否为招就处
		boolean isJobOffice = true;
		if(CheckUtils.isCurrentOrgEqCollege(orgId)){
			isJobOffice = false;
		}
		if(DataUtil.isNotNull(employmentSchemeVO.getStudentId())){
			if(!isJobOffice){
				BaseAcademyModel college = new BaseAcademyModel();
				college.setId(orgId);
				employmentSchemeVO.getStudentId().setCollege(college);
			}
		}else{
			if(!isJobOffice){
				StudentInfoModel studentInfoModel = new StudentInfoModel();
				BaseAcademyModel college = new BaseAcademyModel();
				college.setId(orgId);
				studentInfoModel.setCollege(college);
				employmentSchemeVO.setStudentId(studentInfoModel);
			}
		}
		//数据回显 查询专业信息
		if(DataUtil.isNotNull(employmentSchemeVO.getStudentId()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege().getId())){
			majors = this.compService.queryMajorByCollage(employmentSchemeVO.getStudentId().getCollege().getId());
			if(DataUtil.isNotNull(employmentSchemeVO.getStudentId()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getMajor()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getMajor().getId())){
				classes = this.compService.queryClassByMajor(employmentSchemeVO.getStudentId().getMajor().getId());
			}
		}
		Page page = this.employmentSchemeService.queryEmploymentSchemePage(employmentSchemeVO, Page.DEFAULT_PAGE_SIZE, pageNo);
		model.addAttribute("page", page);
		model.addAttribute("majors", majors);
		model.addAttribute("classes", classes);
		model.addAttribute("colleges", colleges);
		model.addAttribute("isJobOffice", isJobOffice);
		model.addAttribute("graduateCodeList", graduateCodeList);
		model.addAttribute("employmentSchemeVO", employmentSchemeVO);
		return Constants.MENUKEY_JOB_EMPLOYMENT_SCHEME + "listEmploymentScheme";
	}
	/**
	 * 添加、修改-跳转页面
	 * @param model
	 * @param request
	 * @param response
	 * @param employmentSchemeVO
	 * @return
	 */
	@RequestMapping("/job/employmentScheme/opt-add/editEmploymentSchemeInfo")
	public String editEmploymentSchemeInfo(ModelMap model,HttpServletRequest request,HttpServletResponse response,EmploymentScheme employmentSchemeVO){
		List<Dic> yesOrNoList = this.dicUtil.getDicInfoList("Y&N");
		List<Dic> graduateCodeList = this.dicUtil.getDicInfoList("GRADUATE_CODE");
		String orgId = (String)request.getSession().getAttribute("_teacher_orgId");
		if(CheckUtils.isCurrentOrgEqCollege(orgId)){
			model.addAttribute("teacherCollegeId", orgId);
		}
		if(DataUtil.isNotNull(employmentSchemeVO) && DataUtil.isNotNull(employmentSchemeVO.getId())){
			employmentSchemeVO = this.employmentSchemeService.queryEmploymentSchemeById(employmentSchemeVO.getId());
			model.addAttribute("employmentSchemeVO", employmentSchemeVO);
		}
		model.addAttribute("yesOrNoList", yesOrNoList);
		model.addAttribute("graduateCodeList", graduateCodeList);
		return Constants.MENUKEY_JOB_EMPLOYMENT_SCHEME + "editEmploymentScheme";
	}
	/**
	 * 添加、修改
	 * @param model
	 * @param request
	 * @param response
	 * @param employmentSchemeVO
	 * @return
	 */
	@RequestMapping("/job/employmentScheme/opt-save/saveEmploymentSchemeInfo")
	public String saveEmploymentSchemeInfo(ModelMap model,HttpServletRequest request,HttpServletResponse response,EmploymentScheme employmentSchemeVO){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyyMMdd");
		if(DataUtil.isNotNull(employmentSchemeVO) && DataUtil.isNotNull(employmentSchemeVO.getId())){
			String[] ignoreProperties = {"id","studentId","delState"};
			EmploymentScheme employmentSchemePO = this.employmentSchemeService.queryEmploymentSchemeById(employmentSchemeVO.getId());
			BeanUtils.copyProperties(employmentSchemeVO, employmentSchemePO, ignoreProperties);
			try {
				if(DataUtil.isNotNull(employmentSchemeVO.getStrReportStart())){
					employmentSchemePO.setReportStartTime(dFormat.parse(employmentSchemeVO.getStrReportStart()));
				}
				if(DataUtil.isNotNull(employmentSchemeVO.getStrGraduateTime())){
					employmentSchemePO.setGraduateTime(sdf.parse(employmentSchemeVO.getStrGraduateTime()));
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			this.employmentSchemeService.updateEmploymentScheme(employmentSchemePO);
		}else{
			try {
				if(DataUtil.isNotNull(employmentSchemeVO.getStrReportStart())){
					employmentSchemeVO.setReportStartTime(dFormat.parse(employmentSchemeVO.getStrReportStart()));
				}
				if(DataUtil.isNotNull(employmentSchemeVO.getStrGraduateTime())){
					employmentSchemeVO.setGraduateTime(sdf.parse(employmentSchemeVO.getStrGraduateTime()));
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			employmentSchemeVO.setDelState(Constants.STATUS_NORMAL_DICS);
			this.employmentSchemeService.saveEmploymentScheme(employmentSchemeVO);
		}
		return "redirect:/job/employmentScheme/opt-query/queryEmploymentSchemeList.do";
	}
	/**
	 * 判断添加的数据是否存在于数据库中
	 * @param model
	 * @param request
	 * @param response
	 * @param employmentSchemeVO
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/job/employmentScheme/opt-query/isExitEmploymentSchemeInfo"},produces={"text/plain;charset=UTF-8"})
	public String isExitEmploymentSchemeInfo(ModelMap model,HttpServletRequest request,HttpServletResponse response,EmploymentScheme employmentSchemeVO){
		if(DataUtil.isNotNull(this.employmentSchemeService.queryEmploymentSchemeByStuId(employmentSchemeVO.getStudentId().getId()))){
			return "true";
		}
		return "false";
	}
	/**
	 * 判断添加的数据是否存在于数据库中
	 * @param model
	 * @param request
	 * @param response
	 * @param employmentSchemeVO
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/job/employmentScheme/opt-del/delEmploymentScheme"},produces={"text/plain;charset=UTF-8"})
	public String delEmploymentScheme(ModelMap model,HttpServletRequest request,HttpServletResponse response,EmploymentScheme employmentSchemeVO){
		EmploymentScheme employmentSchemePO = this.employmentSchemeService.queryEmploymentSchemeById(employmentSchemeVO.getId());
		if(DataUtil.isNotNull(employmentSchemePO)){
			employmentSchemePO.setDelState(Constants.STATUS_DELETED_DICS);
			this.employmentSchemeService.updateEmploymentScheme(employmentSchemePO);
			return "true";
		}
		return "false";
	}
	/**
	 * 查看就业方案页面
	 * @param model
	 * @param request
	 * @param response
	 * @param employmentSchemeVO
	 * @return
	 */
	@RequestMapping(value={"/job/employmentScheme/opt-query/viewEmploymentSchemeInfo"})
	public String viewEmploymentSchemeInfo(ModelMap model,HttpServletRequest request,HttpServletResponse response,EmploymentScheme employmentSchemeVO){
		if(DataUtil.isNotNull(employmentSchemeVO) && DataUtil.isNotNull(employmentSchemeVO.getId())){
			employmentSchemeVO = this.employmentSchemeService.queryEmploymentSchemeById(employmentSchemeVO.getId());
			model.addAttribute("employmentSchemeVO", employmentSchemeVO);
		}
		return Constants.MENUKEY_JOB_EMPLOYMENT_SCHEME + "viewEmploymentScheme";
	}
	/**
	 * 我的就业方案页面
	 * @param model
	 * @param request
	 * @param response
	 * @param employmentSchemeVO
	 * @return
	 */
	@RequestMapping(value={"/job/myEmploymentScheme/opt-query/myViewEmploymentSchemeInfo"})
	public String myViewEmploymentSchemeInfo(ModelMap model,HttpServletRequest request,HttpServletResponse response,EmploymentScheme employmentSchemeVO){
		boolean isGraduateStudent = false;
		//判断学生，并且存在就业方案
		if(ProjectSessionUtils.checkIsStudent(request)){
			employmentSchemeVO = this.employmentSchemeService.queryEmploymentSchemeByStuId(sessionUtil.getCurrentUserId());
			if(DataUtil.isNotNull(employmentSchemeVO)){
				model.addAttribute("employmentSchemeVO", employmentSchemeVO);
				isGraduateStudent = true;
			}
		}
		model.addAttribute("isGraduateStudent", isGraduateStudent);
		return Constants.MENUKEY_JOB_EMPLOYMENT_SCHEME + "myViewEmploymentSchemeInfo";
	}
	/**
	 * 导入页面跳转
	 * @param model
	 * @param request
	 * @param response
	 * @param employmentSchemeVO
	 * @return
	 */
	@RequestMapping("/job/employmentScheme/opt-query/importEmploymentSchemeInit")
	public String importEmploymentSchemeInit(ModelMap model,HttpServletRequest request,HttpServletResponse response,EmploymentScheme employmentSchemeVO){
		return Constants.MENUKEY_JOB_EMPLOYMENT_SCHEME + "importEmploymentScheme";
	}
	/**
	 * 导入  
	 * @param model
	 * @param file
	 * @param maxSize
	 * @param allowedExt
	 * @param request
	 * @param session
	 * @param employmentSchemeVO
	 */
	@RequestMapping("/job/employmentScheme/opt-query/importEmploymentScheme")
	public String importEmploymentScheme(ModelMap model, @RequestParam("file") MultipartFile file,String maxSize, String allowedExt, HttpServletRequest request, HttpSession session,EmploymentScheme employmentSchemeVO){
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
			List<EmploymentScheme> employmentSchemes = iu.getDataList(filePath, "importEmploymentScheme", null, EmploymentScheme.class);
			this.employmentSchemeService.saveOrUpdate(employmentSchemes);
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
		} catch (ParseException e) {
			errorText.add("您填写的日期格式不正确，请更日期格式为 yyyyMMdd");
			model.addAttribute("errorText", errorText);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			errorText.add("模板配置与数据类型不一致,请与系统管理员联系!");
			errorText = errorText.subList(0, errorText.size() > 20 ? 20 : errorText.size());
			model.addAttribute("errorText", errorText);
		}
		model.addAttribute("importFlag", Boolean.valueOf(true));
		return Constants.MENUKEY_JOB_EMPLOYMENT_SCHEME + "importEmploymentScheme";
	}
//	@RequestMapping("/job/employmentScheme/opt-query/importCompareEmploymentScheme")
//	public String importCompareEmploymentScheme(ModelMap model,HttpServletRequest request,HttpServletResponse response,HttpSession session,String compareId){
//		List errorText = new ArrayList();
//		List<EmploymentScheme[]> exitsEmploymentSchemes = (List<EmploymentScheme[]>) session.getAttribute("exitsEmploymentSchemes");
//		String filePath = (String) session.getAttribute("filePath");
//		try {
//			this.employmentSchemeService.importData(exitsEmploymentSchemes,filePath,compareId);
//			session.removeAttribute("exitsEmploymentSchemes");
//		} catch (OfficeXmlFileException e) {
//			e = e;
//			e.printStackTrace();
//		} catch (IOException e) {
//			e = e;
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e = e;
//			e.printStackTrace();
//		} catch (ExcelException e) {
//			e = e;
//			errorText = e.getMessageList();
//			errorText = errorText.subList(0, errorText.size() > 20 ? 20 :  errorText.size());
//			model.addAttribute("errorText", errorText); 
//		} catch (InstantiationException e) {
//			e = e;
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e = e;
//			e.printStackTrace();
//		} catch (Exception e) {
//			e = e;
//			e.printStackTrace();
//		}
//		model.addAttribute("importFlag", Boolean.valueOf(true));
//		return Constants.MENUKEY_JOB_EMPLOYMENT_SCHEME + "importEmploymentScheme";
//	}
	/**
	 * 计算导出页数
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/job/employmentScheme/nsm/exportEmploymentSchemeList")
	public String exportEmploymentSchemeList(ModelMap model,HttpServletRequest request,HttpServletResponse response){
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
		return Constants.MENUKEY_JOB_EMPLOYMENT_SCHEME + "exportEmploymentSchemeList";
	}
	
	/**
	 * 按查询条件导出Excel
	 * @param modelMap
	 * @param request
	 * @param response
	 * @param temporaryWorkStudyModel
	 */
	@RequestMapping("/job/employmentScheme/opt-query/exportEmploymentScheme")
	public void exportEmploymentScheme(ModelMap model, HttpServletRequest request,HttpServletResponse response, EmploymentScheme employmentSchemeVO,String userQuery_exportSize,String userQuery_exportPage){
		int pageSize = userQuery_exportSize != null ? Integer.parseInt(userQuery_exportSize) : 1000;
		int pageNo = userQuery_exportPage != null ? Integer.parseInt(userQuery_exportPage) : 1;
		String orgId = (String)request.getSession().getAttribute("_teacher_orgId");
		//判断用户是否为招就处
		boolean isJobOffice = true;
		if(CheckUtils.isCurrentOrgEqCollege(orgId)){
			isJobOffice = false;
		}
		if(DataUtil.isNotNull(employmentSchemeVO.getStudentId())){
			if(!isJobOffice){
				BaseAcademyModel college = new BaseAcademyModel();
				college.setId(orgId);
				employmentSchemeVO.getStudentId().setCollege(college);
			}
		}else{
			if(!isJobOffice){
				StudentInfoModel studentInfoModel = new StudentInfoModel();
				BaseAcademyModel college = new BaseAcademyModel();
				college.setId(orgId);
				studentInfoModel.setCollege(college);
				employmentSchemeVO.setStudentId(studentInfoModel);
			}
		}
		List<EmploymentScheme> list = (List<EmploymentScheme>) this.employmentSchemeService.queryEmploymentSchemePage(employmentSchemeVO, pageSize, pageNo).getResult();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyyMMdd");
		List exportDataList = new ArrayList();
		for (EmploymentScheme employmentScheme : list) {
			Map<String,Object> map = new HashMap<String, Object>();
			if(DataUtil.isNotNull(employmentScheme.getExamNo())){
				map.put("examNo", employmentScheme.getExamNo());
			}else{
				map.put("examNo", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getSexCode())){
				map.put("sexCode", employmentScheme.getSexCode());
			}else{
				map.put("sexCode", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getPolitical())){
				map.put("political", employmentScheme.getPolitical());
			}else{
				map.put("political", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getNationCode())){
				map.put("nationCode", employmentScheme.getNationCode());
			}else{
				map.put("nationCode", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getSchoolCode())){
				map.put("schoolCode", employmentScheme.getSchoolCode());
			}else{
				map.put("schoolCode", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getSchoolName())){
				map.put("schoolName", employmentScheme.getSchoolName());
			}else{
				map.put("schoolName", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getBrachSchoolName())){
				map.put("brachSchoolName", employmentScheme.getBrachSchoolName());
			}else{
				map.put("brachSchoolName", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getSchoolPrivince())){
				map.put("schoolPrivince", employmentScheme.getSchoolPrivince());
			}else{
				map.put("schoolPrivince", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getEducationCode())){
				map.put("educationCode", employmentScheme.getEducationCode());
			}else{
				map.put("educationCode", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getMajorCode())){
				map.put("majorCode", employmentScheme.getMajorCode());
			}else{
				map.put("majorCode", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getMajorSide())){
				map.put("majorSide", employmentScheme.getMajorSide());
			}else{
				map.put("majorSide", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getTrainTypeCode())){
				map.put("trainTypeCode", employmentScheme.getTrainTypeCode());
			}else{
				map.put("trainTypeCode", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getPlaceCode())){
				map.put("placeCode", employmentScheme.getPlaceCode());
			}else{
				map.put("placeCode", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getSourcePlace())){
				map.put("sourcePlace", employmentScheme.getSourcePlace());
			}else{
				map.put("sourcePlace", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getCityVillage())){
				map.put("cityVillage", employmentScheme.getCityVillage());
			}else{
				map.put("cityVillage", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getGraduatePlan())){
				map.put("graduatePlan", employmentScheme.getGraduatePlan());
			}else{
				map.put("graduatePlan", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getGraduateTime())){
				map.put("strGraduateTime", dateFormat.format(employmentScheme.getGraduateTime()));
			}else{
				map.put("strGraduateTime", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getTeachStudentTypeCode())){
				map.put("teachStudentTypeCode", employmentScheme.getTeachStudentTypeCode());
			}else{
				map.put("teachStudentTypeCode", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getDifficultTypeCode())){
				map.put("difficultTypeCode", employmentScheme.getDifficultTypeCode());
			}else{
				map.put("difficultTypeCode", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getStudentId())){
				if(DataUtil.isNotNull(employmentScheme.getStudentId().getCertificateCode())){
					map.put("stuCode", employmentScheme.getStudentId().getCertificateCode());
				}else{
					map.put("stuCode", null);
				}
				if(DataUtil.isNotNull(employmentScheme.getStudentId().getName())){
					map.put("stuName", employmentScheme.getStudentId().getName());
				}else{
					map.put("stuName", null);
				}
				if(DataUtil.isNotNull(employmentScheme.getStudentId().getMajor())){
					map.put("majorName", employmentScheme.getStudentId().getMajor().getMajorName());
				}else{
					map.put("majorName", null);
				}
				if(DataUtil.isNotNull(employmentScheme.getStudentId().getEnterDate())){
					map.put("enterDate", dateFormat.format(employmentScheme.getStudentId().getEnterDate()));
				}else{
					map.put("enterDate", null);
				}
				if(DataUtil.isNotNull(employmentScheme.getStudentId().getCollege())){
					map.put("collegeName", employmentScheme.getStudentId().getCollege().getName());
				}else{
					map.put("collegeName", null);
				}
				if(DataUtil.isNotNull(employmentScheme.getStudentId().getClassId())){
					map.put("className", employmentScheme.getStudentId().getClassId().getClassName());
				}else{
					map.put("className", null);
				}
				if(DataUtil.isNotNull(employmentScheme.getStudentId())){
					map.put("stuNo", employmentScheme.getStudentId().getId());
				}else{
					map.put("stuNo", null);
				}
				if(DataUtil.isNotNull(employmentScheme.getStudentId().getBrithDate())){
					map.put("birthday", dateFormat.format(employmentScheme.getStudentId().getBrithDate()));
				}else{
					map.put("birthday", null);
				}
				if(DataUtil.isNotNull(employmentScheme.getStudentId().getPhone1())){
					map.put("phone", employmentScheme.getStudentId().getPhone1());
				}else{
					map.put("phone", null);
				}
				if(DataUtil.isNotNull(employmentScheme.getStudentId().getEmail())){
					map.put("email", employmentScheme.getStudentId().getEmail());
				}else{
					map.put("email", null);
				}
				if(DataUtil.isNotNull(employmentScheme.getStudentId().getQq())){
					map.put("qq", employmentScheme.getStudentId().getQq());
				}else{
					map.put("qq", null);
				}
				if(DataUtil.isNotNull(employmentScheme.getStudentId().getHomeAddress())){
					map.put("address", employmentScheme.getStudentId().getHomeAddress());
				}else{
					map.put("address", null);
				}
				if(DataUtil.isNotNull(employmentScheme.getStudentId().getHomeTel())){
					map.put("telephone", employmentScheme.getStudentId().getHomeTel());
				}else{
					map.put("telephone", null);
				}
				if(DataUtil.isNotNull(employmentScheme.getStudentId().getHomePostCode())){
					map.put("addressPost", employmentScheme.getStudentId().getHomePostCode());
				}else{
					map.put("addressPost", null);
				}
			}
			if(DataUtil.isNotNull(employmentScheme.getTrainCompany())){
				map.put("trainCompany", employmentScheme.getTrainCompany());
			}else{
				map.put("trainCompany", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getBeforeFileCompany())){
				map.put("beforeFileCompany", employmentScheme.getBeforeFileCompany());
			}else{
				map.put("beforeFileCompany", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getIsFileSchool())){
				map.put("isFileSchoolText", employmentScheme.getIsFileSchool().getName());
			}else{
				map.put("isFileSchoolText", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getBeforeAccountPolice())){
				map.put("beforeAccountPolice", employmentScheme.getBeforeAccountPolice());
			}else{
				map.put("beforeAccountPolice", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getIsAccountSchool())){
				map.put("isAccountSchoolText", employmentScheme.getIsAccountSchool().getName());
			}else{
				map.put("isAccountSchoolText", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getGraduateCode())){
				map.put("graduateCodeText", employmentScheme.getGraduateCode().getCode());
			}else{
				map.put("graduateCodeText", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getCompanyName())){
				map.put("companyName", employmentScheme.getCompanyName());
			}else{
				map.put("companyName", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getCompanyOrgCode())){
				map.put("companyOrgCode", employmentScheme.getCompanyOrgCode());
			}else{
				map.put("companyOrgCode", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getCompanyCode())){
				map.put("companyCode", employmentScheme.getCompanyCode());
			}else{
				map.put("companyCode", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getCompanyIndustryCode())){
				map.put("companyIndustryCode", employmentScheme.getCompanyIndustryCode());
			}else{
				map.put("companyIndustryCode", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getCompanyPlaceCode())){
				map.put("companyPlaceCode", employmentScheme.getCompanyPlaceCode());
			}else{
				map.put("companyPlaceCode", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getCompanyPlace())){
				map.put("companyPlace", employmentScheme.getCompanyPlace());
			}else{
				map.put("companyPlace", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getWorkTypeCode())){
				map.put("workTypeCode", employmentScheme.getWorkTypeCode());
			}else{
				map.put("workTypeCode", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getCompanyPerson())){
				map.put("companyPerson", employmentScheme.getCompanyPerson());
			}else{
				map.put("companyPerson", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getPersonTelephone())){
				map.put("personTelephone", employmentScheme.getPersonTelephone());
			}else{
				map.put("personTelephone", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getPersonPhone())){
				map.put("personPhone", employmentScheme.getPersonPhone());
			}else{
				map.put("personPhone", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getPersonEmail())){
				map.put("personEmail", employmentScheme.getPersonEmail());
			}else{
				map.put("personEmail", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getPersonFax())){
				map.put("personFax", employmentScheme.getPersonFax());
			}else{
				map.put("personFax", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getCompanyAddress())){
				map.put("companyAddress", employmentScheme.getCompanyAddress());
			}else{
				map.put("companyAddress", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getCompantPost())){
				map.put("compantPost", employmentScheme.getCompantPost());
			}else{
				map.put("compantPost", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getReportTypeCode())){
				map.put("reportTypeCode", employmentScheme.getReportTypeCode());
			}else{
				map.put("reportTypeCode", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getReportCompanyName())){
				map.put("reportCompanyName", employmentScheme.getReportCompanyName());
			}else{
				map.put("reportCompanyName", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getReportCompanyPlaceCode())){
				map.put("reportCompanyPlaceCode", employmentScheme.getReportCompanyPlaceCode());
			}else{
				map.put("reportCompanyPlaceCode", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getReportCompnayPlace())){
				map.put("reportCompnayPlace", employmentScheme.getReportCompnayPlace());
			}else{
				map.put("reportCompnayPlace", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getFileCompanyName())){
				map.put("fileCompanyName", employmentScheme.getFileCompanyName());
			}else{
				map.put("fileCompanyName", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getFileCompanyAddress())){
				map.put("fileCompanyAddress", employmentScheme.getFileCompanyAddress());
			}else{
				map.put("fileCompanyAddress", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getFileCompanyPost())){
				map.put("fileCompanyPost", employmentScheme.getFileCompanyPost());
			}else{
				map.put("fileCompanyPost", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getAccountAddress())){
				map.put("accountAddress", employmentScheme.getAccountAddress());
			}else{
				map.put("accountAddress", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getReportNo())){
				map.put("reportNo", employmentScheme.getReportNo());
			}else{
				map.put("reportNo", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getReportStartTime())){
				map.put("strReportStartTime", dFormat.format(employmentScheme.getReportStartTime()));
			}else{
				map.put("strReportStartTime", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getJobCommentOne())){
				map.put("jobCommentOne", employmentScheme.getJobCommentOne());
			}else{
				map.put("jobCommentOne", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getJobCommentTwo())){
				map.put("jobCommentTwo", employmentScheme.getJobCommentTwo());
			}else{
				map.put("jobCommentTwo", null);
			}
			if(DataUtil.isNotNull(employmentScheme.getJobCommentThree())){
				map.put("jobCommentThree", employmentScheme.getJobCommentThree());
			}else{
				map.put("jobCommentThree", null);
			}
			exportDataList.add(map);
		}
		HSSFWorkbook wb;
		try {
			wb = this.excelService.exportData("export_employment_scheme.xls","exportEmploymentScheme", exportDataList);
			String filename = "就业方案导出表" + ".xls";
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
	 * 统计就业率
	 * @param model
	 * @param request
	 * @param response
	 * @param schoolGoodStudentVO
	 * @return
	 */
	@RequestMapping("/job/statEmploymentScheme/opt-query/statEmploymentSchemeList")
	public String statSchoolGoodStudentPage(ModelMap model,HttpServletRequest request,HttpServletResponse response,EmploymentScheme employmentSchemeVO){
		String statByWay = request.getParameter("statByWay");//统计范围
		String orgId = (String)request.getSession().getAttribute("_teacher_orgId");
		//判断用户是否为招就处
		boolean isJobOffice = true;
		if(CheckUtils.isCurrentOrgEqCollege(orgId)){
			isJobOffice = false;
		}
		if(DataUtil.isNotNull(employmentSchemeVO.getStudentId())){
			if(!isJobOffice){
				BaseAcademyModel college = new BaseAcademyModel();
				college.setId(orgId);
				employmentSchemeVO.getStudentId().setCollege(college);
			}
		}else{
			if(!isJobOffice){
				StudentInfoModel studentInfoModel = new StudentInfoModel();
				BaseAcademyModel college = new BaseAcademyModel();
				college.setId(orgId);
				studentInfoModel.setCollege(college);
				employmentSchemeVO.setStudentId(studentInfoModel);
			}
		}
		//判断统计范围，默认为按学院统计
		if(DataUtil.isNull(statByWay)){
			statByWay = "byCollege";
		}
		//添加默认学年
		if(!DataUtil.isNotNull(employmentSchemeVO.getSchoolYear())){
			Dic nowSchoolYear = SchoolYearUtil.getYearDic();//当前学年
			employmentSchemeVO.setSchoolYear(nowSchoolYear);
		}
		List<Dic> years = this.dicUtil.getDicInfoList("YEAR");//学年
		List<BaseAcademyModel> colleges =  this.baseDateService.listBaseAcademy();//学院
		List<BaseMajorModel> majors = new ArrayList<BaseMajorModel>();
		List<BaseClassModel> classes = new ArrayList<BaseClassModel>();
		Map<String,String> statisticalWays = Constants.getStatisticalWays();
		model.addAttribute("years", years);
		model.addAttribute("statByWay", statByWay);
//		model.addAttribute("schoolYear", schoolYear);
		model.addAttribute("isJobOffice", isJobOffice);
		model.addAttribute("colleges", colleges);
		model.addAttribute("statisticalWays", statisticalWays);
		model.addAttribute("employmentSchemeVO", employmentSchemeVO);
		if(DataUtil.isEquals("byCollege", statByWay)){
			if(DataUtil.isNotNull(employmentSchemeVO.getStudentId()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege().getId())){
				majors = this.compService.queryMajorByCollage(employmentSchemeVO.getStudentId().getCollege().getId());
			}
			List<EmploymentSchemeCollegeView> collegeView = this.employmentSchemeService.statEmploymentSchemeByCollege(employmentSchemeVO);
			model.addAttribute("page", collegeView);
			model.addAttribute("majors", majors);
			return Constants.MENUKEY_JOB_EMPLOYMENT_SCHEME + "statistics/statEmploymentSchemeByCollege";
		}
		if(DataUtil.isEquals("byMajor", statByWay)){//按专业查询时只需要添加专业查询条件
			List<EmploymentSchemeMajorView> majorView = this.employmentSchemeService.statEmploymentSchemeByMajor(employmentSchemeVO);
			if(DataUtil.isNotNull(employmentSchemeVO.getStudentId()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege().getId())){
				majors = this.compService.queryMajorByCollage(employmentSchemeVO.getStudentId().getCollege().getId());
			}
			model.addAttribute("majors", majors);
			model.addAttribute("page", majorView);
			return Constants.MENUKEY_JOB_EMPLOYMENT_SCHEME + "statistics/statEmploymentSchemeByMajor";
		}
		if(DataUtil.isEquals("byClass", statByWay)){//按班级查询时需要添加专业和班级查询条件
			List<EmploymentSchemeClassView> classView = this.employmentSchemeService.statEmploymentSchemeByClass(employmentSchemeVO);
			if(DataUtil.isNotNull(employmentSchemeVO.getStudentId()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege().getId())){
				majors = this.compService.queryMajorByCollage(employmentSchemeVO.getStudentId().getCollege().getId());
				if(DataUtil.isNotNull(employmentSchemeVO.getStudentId()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getMajor()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getMajor().getId())){
					classes = this.compService.queryClassByMajor(employmentSchemeVO.getStudentId().getMajor().getId());
				}
			}
			model.addAttribute("majors", majors);
			model.addAttribute("classes", classes);
			model.addAttribute("page", classView);
			return Constants.MENUKEY_JOB_EMPLOYMENT_SCHEME + "statistics/statEmploymentSchemeByClass";
		}
		return Constants.MENUKEY_JOB_EMPLOYMENT_SCHEME + "statistics/statEmploymentSchemeByCollege";
	}
	/**
	 * 计算导出页数
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/job/statEmploymentScheme/nsm/exportEmploymentSchemePage")
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
		return Constants.MENUKEY_JOB_EMPLOYMENT_SCHEME + "statistics/exportStatEmploymentSchemeList";
	}
	/** 
	 * 导出统计结果
	 * @param model
	 * @param request
	 * @param response
	 * @param employmentSchemeVO
	 * @param userQuery_exportSize
	 * @param userQuery_exportPage
	 */
	@RequestMapping("/job/statEmploymentScheme/opt-query/statEmploymentSchemeListByStat")
	public void exportSchoolGoodStudentPageByStat(ModelMap model, HttpServletRequest request,HttpServletResponse response, EmploymentScheme employmentSchemeVO,String userQuery_exportSize,String userQuery_exportPage){
		String statByWay = request.getParameter("statByWay");//统计范围
		int pageSize = userQuery_exportSize != null ? Integer.parseInt(userQuery_exportSize) : 10000;
		int pageNo = userQuery_exportPage != null ? Integer.parseInt(userQuery_exportPage) : 1;
		String orgId = (String)request.getSession().getAttribute("_teacher_orgId");
		//判断用户是否为招就处
		boolean isJobOffice = true;
		if(CheckUtils.isCurrentOrgEqCollege(orgId)){
			isJobOffice = false;
		}
		if(DataUtil.isNotNull(employmentSchemeVO.getStudentId())){
			if(!isJobOffice){
				BaseAcademyModel college = new BaseAcademyModel();
				college.setId(orgId);
				employmentSchemeVO.getStudentId().setCollege(college);
			}
		}else{
			if(!isJobOffice){
				StudentInfoModel studentInfoModel = new StudentInfoModel();
				BaseAcademyModel college = new BaseAcademyModel();
				college.setId(orgId);
				studentInfoModel.setCollege(college);
				employmentSchemeVO.setStudentId(studentInfoModel);
			}
		}
		String excelName = "";
		String bdpExcelId = "";
		List exportDataList = new ArrayList();
		boolean isYear = true;
		if(DataUtil.isNotNull(employmentSchemeVO.getSchoolYear()) && DataUtil.isNotNull(employmentSchemeVO.getSchoolYear().getId())){
			isYear = false;
		}
		EmploymentSchemeView total = new EmploymentSchemeView();
		if(DataUtil.isEquals("byCollege", statByWay)){//按学院
			List<EmploymentSchemeCollegeView> collegeViews = (List<EmploymentSchemeCollegeView>) this.employmentSchemeService.statEmploymentSchemeByCollegePage(employmentSchemeVO,pageSize,pageNo).getResult();
			excelName = "export_job_stat_college";
			bdpExcelId = "exportStatJobCollege";
			for (int i = 0; i < collegeViews.size(); i++) {
				EmploymentSchemeCollegeView collegeView = collegeViews.get(i);
				Map<String,String> map = this.getData(collegeView,total);
				map.put("index", (i+1)+"");
				if(isYear){
					map.put("schoolYear", collegeView.getSchoolYear().getName());
				}
				map.put("collegeName", collegeView.getCollege().getName());
				exportDataList.add(map);
			}
		}
		if(DataUtil.isEquals("byMajor", statByWay)){//按专业
			List<EmploymentSchemeMajorView> majorViews = (List<EmploymentSchemeMajorView>) this.employmentSchemeService.statEmploymentSchemeByMajorPage(employmentSchemeVO,pageSize,pageNo).getResult();
			excelName = "export_job_stat_major";
			bdpExcelId = "exportStatJobMajor";
			for (int i = 0; i < majorViews.size(); i++) {
				EmploymentSchemeMajorView majorView = majorViews.get(i);
				Map<String,String> map = this.getData(majorView,total);
				map.put("index", (i+1)+"");
				if(isYear){
					map.put("schoolYear", majorView.getSchoolYear().getName());
				}
				map.put("collegeName", majorView.getMajor().getCollage().getName());
				map.put("majorName", majorView.getMajor().getMajorName());
				exportDataList.add(map);
			}
		}
		if(DataUtil.isEquals("byClass", statByWay)){//按班级
			List<EmploymentSchemeClassView> classViews = (List<EmploymentSchemeClassView>) this.employmentSchemeService.statEmploymentSchemeByClassPage(employmentSchemeVO,pageSize,pageNo).getResult();
			excelName = "export_job_stat_class";
			bdpExcelId = "exportStatJobClass";
			for (int i = 0; i < classViews.size(); i++) {
				EmploymentSchemeClassView classView = classViews.get(i);
				Map<String,String> map = this.getData(classView,total);
				map.put("index", (i+1)+"");
				if(isYear){
					map.put("schoolYear", classView.getSchoolYear().getName());
				}
				map.put("collegeName", classView.getClazz().getMajor().getCollage().getName());
				map.put("majorName", classView.getClazz().getMajor().getMajorName());
				map.put("className", classView.getClazz().getClassName());
				exportDataList.add(map);
			}
		}
		if(isYear){
			excelName += "_cond";
			bdpExcelId += "Cond";
		}
		excelName += ".xls";
		HSSFWorkbook wb;
		try {
			wb = this.excelService.exportData(excelName,bdpExcelId, exportDataList);
			HSSFSheet sheet = wb.getSheetAt(0);
			int endLine = sheet.getLastRowNum() + 1;//最后一行
			HSSFRow row = sheet.createRow(endLine);
			HSSFCell cell = row.createCell(0);
			cell.setCellValue("总计：");
			int endColumn = sheet.getRow(0).getLastCellNum()-1;//返回总的列数
			
			HSSFCell goAbroadCell = row.createCell(endColumn--,HSSFCell.CELL_TYPE_STRING);
			goAbroadCell.setCellValue(total.getGoAbroadNum()+"("+(total.getTotalNum()!=0?(String.format("%.2f", Double.valueOf(total.getGoAbroadNum())/Double.valueOf(total.getTotalNum())*100)+"%"):"0.00%")+")");
			
			HSSFCell goHigherSchoolCell = row.createCell(endColumn--,HSSFCell.CELL_TYPE_STRING);
			goHigherSchoolCell.setCellValue(total.getGoHigherSchoolNum()+"("+(total.getTotalNum()!=0?(String.format("%.2f", Double.valueOf(total.getGoHigherSchoolNum())/Double.valueOf(total.getTotalNum())*100)+"%"):"0.00%")+")");
			
			HSSFCell freelanderCell = row.createCell(endColumn--,HSSFCell.CELL_TYPE_STRING);
			freelanderCell.setCellValue(total.getFreelanderNum()+"("+(total.getTotalNum()!=0?(String.format("%.2f", Double.valueOf(total.getFreelanderNum())/Double.valueOf(total.getTotalNum())*100)+"%"):"0.00%")+")");
			
			HSSFCell selfEmployedCell = row.createCell(endColumn--,HSSFCell.CELL_TYPE_STRING);
			selfEmployedCell.setCellValue(total.getSelfEmployedNum()+"("+(total.getTotalNum()!=0?(String.format("%.2f", Double.valueOf(total.getSelfEmployedNum())/Double.valueOf(total.getTotalNum())*100)+"%"):"0.00%")+")");
			
			HSSFCell localBasicProjectCell = row.createCell(endColumn--,HSSFCell.CELL_TYPE_STRING);
			localBasicProjectCell.setCellValue(total.getLocalBasicProjectNum()+"("+(total.getTotalNum()!=0?(String.format("%.2f", Double.valueOf(total.getLocalBasicProjectNum())/Double.valueOf(total.getTotalNum())*100)+"%"):"0.00%")+")");
			
			HSSFCell nationalBasicProjectCell = row.createCell(endColumn--,HSSFCell.CELL_TYPE_STRING);
			nationalBasicProjectCell.setCellValue(total.getNationalBasicProjectNum()+"("+(total.getTotalNum()!=0?(String.format("%.2f", Double.valueOf(total.getNationalBasicProjectNum())/Double.valueOf(total.getTotalNum())*100)+"%"):"0.00%")+")");
			
			HSSFCell applyConscriptsCell = row.createCell(endColumn--,HSSFCell.CELL_TYPE_STRING);
			applyConscriptsCell.setCellValue(total.getApplyConscriptsNum()+"("+(total.getTotalNum()!=0?(String.format("%.2f", Double.valueOf(total.getApplyConscriptsNum())/Double.valueOf(total.getTotalNum())*100)+"%"):"0.00%")+")");
			
			HSSFCell researchAssistantCell = row.createCell(endColumn--,HSSFCell.CELL_TYPE_STRING);
			researchAssistantCell.setCellValue(total.getResearchAssistantNum()+"("+(total.getTotalNum()!=0?(String.format("%.2f", Double.valueOf(total.getResearchAssistantNum())/Double.valueOf(total.getTotalNum())*100)+"%"):"0.00%")+")");
			
			HSSFCell otherCareerCell = row.createCell(endColumn--,HSSFCell.CELL_TYPE_STRING);
			otherCareerCell.setCellValue(total.getOtherCareerNum()+"("+(total.getTotalNum()!=0?(String.format("%.2f", Double.valueOf(total.getOtherCareerNum())/Double.valueOf(total.getTotalNum())*100)+"%"):"0.00%")+")");
			
			HSSFCell laborCareerCell = row.createCell(endColumn--,HSSFCell.CELL_TYPE_STRING);
			laborCareerCell.setCellValue(total.getLaborCareerNum()+"("+(total.getTotalNum()!=0?(String.format("%.2f", Double.valueOf(total.getLaborCareerNum())/Double.valueOf(total.getTotalNum())*100)+"%"):"0.00%")+")");
			
			HSSFCell protocoCareerCell = row.createCell(endColumn--,HSSFCell.CELL_TYPE_STRING);
			protocoCareerCell.setCellValue(total.getProtocoCareerNum()+"("+(total.getTotalNum()!=0?(String.format("%.2f", Double.valueOf(total.getProtocoCareerNum())/Double.valueOf(total.getTotalNum())*100)+"%"):"0.00%")+")");
			
			HSSFCell jobRateNumCell = row.createCell(endColumn--,HSSFCell.CELL_TYPE_STRING);
			jobRateNumCell.setCellValue(total.getTotalNum()!=0?(String.format("%.2f", Double.valueOf(total.getNoJobNum())/Double.valueOf(total.getTotalNum())*100))+"%":"0.00%");
			
			HSSFCell jobNumCell = row.createCell(endColumn--,HSSFCell.CELL_TYPE_STRING);
			jobNumCell.setCellValue(total.getNoJobNum()+"");//为了方便将未就业人数变量存储了就业人数
			
			HSSFCell totalNumCell = row.createCell(endColumn--,HSSFCell.CELL_TYPE_STRING);
			totalNumCell.setCellValue(total.getTotalNum()+"");
			
			String filename = "";
			if(DataUtil.isNotNull(employmentSchemeVO) && DataUtil.isNotNull(employmentSchemeVO.getStudentId())){
				if(DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege()) && DataUtil.isNotNull(employmentSchemeVO.getStudentId().getCollege().getId())){
					filename += this.baseDateService.findAcademyById(employmentSchemeVO.getStudentId().getCollege().getId()).getName();
				}
				if(!isYear){
					filename += this.dicService.getDic(employmentSchemeVO.getSchoolYear().getId()).getCode() + "届";
				}
			}
			filename += "就业率统计表" + ".xls";
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
	 * 导出统计数据时填充数据
	 * @param employmentSchemeView
	 * @return
	 */
	private Map<String,String> getData(EmploymentSchemeView employmentSchemeView,EmploymentSchemeView total){
		Map<String, String> data = new HashMap<String, String>();
		data.put("totalStuNum", employmentSchemeView.getTotalNum()+"");
		total.setTotalNum(total.getTotalNum() + employmentSchemeView.getTotalNum());
		data.put("totalJobNum", (employmentSchemeView.getTotalNum()-employmentSchemeView.getNoJobNum())+"");
		total.setNoJobNum(total.getNoJobNum() + (employmentSchemeView.getTotalNum()-employmentSchemeView.getNoJobNum()));
		data.put("jobRate", employmentSchemeView.getTotalNum()!=0 ? String.format("%.2f", Double.valueOf(employmentSchemeView.getTotalNum()-employmentSchemeView.getNoJobNum())/Double.valueOf(employmentSchemeView.getTotalNum())*100)+"%" : "0.00%");
		data.put("protocoCareerNum", employmentSchemeView.getProtocoCareerNum()+"("+(employmentSchemeView.getTotalNum()!=0 ? String.format("%.2f", Double.valueOf(employmentSchemeView.getProtocoCareerNum())/Double.valueOf(employmentSchemeView.getTotalNum())*100)+"%" : "0.00%")+")");
		total.setProtocoCareerNum(total.getProtocoCareerNum() + employmentSchemeView.getProtocoCareerNum());
		data.put("laborCareerNum", employmentSchemeView.getLaborCareerNum()+"("+(employmentSchemeView.getTotalNum()!=0 ? String.format("%.2f", Double.valueOf(employmentSchemeView.getLaborCareerNum())/Double.valueOf(employmentSchemeView.getTotalNum())*100)+"%" : "0.00%")+")");
		total.setLaborCareerNum(total.getLaborCareerNum() + employmentSchemeView.getLaborCareerNum());
		data.put("otherCareerNum", employmentSchemeView.getOtherCareerNum()+"("+(employmentSchemeView.getTotalNum()!=0 ? String.format("%.2f", Double.valueOf(employmentSchemeView.getOtherCareerNum())/Double.valueOf(employmentSchemeView.getTotalNum())*100)+"%" : "0.00%")+")");
		total.setOtherCareerNum(total.getOtherCareerNum() + employmentSchemeView.getOtherCareerNum());
		data.put("applyConscriptsNum", employmentSchemeView.getApplyConscriptsNum()+"("+(employmentSchemeView.getTotalNum()!=0 ? String.format("%.2f", Double.valueOf(employmentSchemeView.getApplyConscriptsNum())/Double.valueOf(employmentSchemeView.getTotalNum())*100)+"%" : "0.00%")+")");
		total.setApplyConscriptsNum(total.getApplyConscriptsNum() + employmentSchemeView.getApplyConscriptsNum());
		data.put("researchAssistantNum", employmentSchemeView.getResearchAssistantNum()+"("+(employmentSchemeView.getTotalNum()!=0 ? String.format("%.2f", Double.valueOf(employmentSchemeView.getResearchAssistantNum())/Double.valueOf(employmentSchemeView.getTotalNum())*100)+"%" : "0.00%")+")");
		total.setResearchAssistantNum(total.getResearchAssistantNum() + employmentSchemeView.getResearchAssistantNum());
		data.put("nationalBasicProjectNum", employmentSchemeView.getNationalBasicProjectNum()+"("+(employmentSchemeView.getTotalNum()!=0 ? String.format("%.2f", Double.valueOf(employmentSchemeView.getNationalBasicProjectNum())/Double.valueOf(employmentSchemeView.getTotalNum())*100)+"%" : "0.00%")+")");
		total.setNationalBasicProjectNum(total.getNationalBasicProjectNum() + employmentSchemeView.getNationalBasicProjectNum());
		data.put("localBasicProjectNum", employmentSchemeView.getLocalBasicProjectNum()+"("+(employmentSchemeView.getTotalNum()!=0 ? String.format("%.2f", Double.valueOf(employmentSchemeView.getLocalBasicProjectNum())/Double.valueOf(employmentSchemeView.getTotalNum())*100)+"%" : "0.00%")+")");
		total.setLocalBasicProjectNum(total.getLocalBasicProjectNum() + employmentSchemeView.getLocalBasicProjectNum());
		data.put("selfEmployedNum", employmentSchemeView.getSelfEmployedNum()+"("+(employmentSchemeView.getTotalNum()!=0 ? String.format("%.2f", Double.valueOf(employmentSchemeView.getSelfEmployedNum())/Double.valueOf(employmentSchemeView.getTotalNum())*100)+"%" : "0.00%")+")");
		total.setSelfEmployedNum(total.getSelfEmployedNum() + employmentSchemeView.getSelfEmployedNum());
		data.put("freelanderNum", employmentSchemeView.getFreelanderNum()+"("+(employmentSchemeView.getTotalNum()!=0 ? String.format("%.2f", Double.valueOf(employmentSchemeView.getFreelanderNum())/Double.valueOf(employmentSchemeView.getTotalNum())*100)+"%" : "0.00%")+")");
		total.setFreelanderNum(total.getFreelanderNum() + employmentSchemeView.getFreelanderNum());
		data.put("goHigherSchoolNum", employmentSchemeView.getGoHigherSchoolNum()+"("+(employmentSchemeView.getTotalNum()!=0 ? String.format("%.2f", Double.valueOf(employmentSchemeView.getGoHigherSchoolNum())/Double.valueOf(employmentSchemeView.getTotalNum())*100)+"%" : "0.00%")+")");
		total.setGoHigherSchoolNum(total.getGoHigherSchoolNum() + employmentSchemeView.getGoHigherSchoolNum());
		data.put("goAbroadNum", employmentSchemeView.getGoAbroadNum()+"("+(employmentSchemeView.getTotalNum()!=0 ? String.format("%.2f", Double.valueOf(employmentSchemeView.getGoAbroadNum())/Double.valueOf(employmentSchemeView.getTotalNum())*100)+"%" : "0.00%")+")");
		total.setGoAbroadNum(total.getGoAbroadNum() + employmentSchemeView.getGoAbroadNum());
		return data;
	}
	/**
	 * 得到上传文件的大小 2MB
	 * @return
	 */
	private int setMaxSize() {
		return 20971520;
	}
}
