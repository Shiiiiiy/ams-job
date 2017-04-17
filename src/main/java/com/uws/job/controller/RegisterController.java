package com.uws.job.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.uws.common.service.IBaseDataService;
import com.uws.common.service.IStudentCommonService;
import com.uws.comp.service.ICompService;
import com.uws.core.base.BaseController;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.core.excel.service.IExcelService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.job.RegisterStatisticsClassModel;
import com.uws.domain.job.RegisterStatisticsCollegeModel;
import com.uws.domain.job.RegisterStatisticsMajorModel;
import com.uws.domain.job.RegisterModel;
import com.uws.domain.job.RegisterStatisticsClassModel;
import com.uws.domain.job.RegisterStatisticsCollegeModel;
import com.uws.domain.job.RegisterStatisticsMajorModel;
import com.uws.domain.job.RegisterModel;
import com.uws.domain.job.RegisterStatisticsModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.job.service.IRegisterService;
import com.uws.job.util.Constants;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
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
 * 
* @ClassName: RegisterController 
* @Description: 报到证管理控制器类 
* 主要实现了报到证编号的导入， 报到证改派的统计功能 以及导出功能
* @author 联合永道
* @date 2015-10-9 下午6:27:33 
*
 */
@Controller
public class RegisterController extends BaseController {
	
	private static Logger log = new LoggerFactory(RegisterController.class);
	@Autowired
	private IRegisterService registerService;
	@Autowired
	private IBaseDataService baseDataService;
	@Autowired
	private ICompService compService;
	@Autowired
	private IExcelService excelService;
	@Autowired
	private IDicService dicService;
	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	
	private FileUtil fileUtil = FileFactory.getFileUtil();
	
	@RequestMapping({"job/register/opt-query/registerList"})
	public String listRegister(ModelMap model, HttpServletRequest request, RegisterModel register){
		log.info("报到证查询列表");
		
		if(register.getEmploymentYear() == null){
			register.setEmploymentYear(Constants.CURRENT_YEAR);
		}
		
		String collegeId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
		if(CheckUtils.isCurrentOrgEqCollege(collegeId)){
			model.addAttribute("collegeId", collegeId);
			BaseAcademyModel college = new BaseAcademyModel();
			college.setId(collegeId);
			register.setCollege(college);
		}
		
    	int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
    	Page page = registerService.queryRegisterList(pageNo, Page.DEFAULT_PAGE_SIZE, register);
    	
    	model.addAttribute("page", page);
    	model.addAttribute("yearList", Constants.YEAR_LIST);
    	model.addAttribute("register", register);
    	
		List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
		model.addAttribute("collegeList", collegeList);
		
		if(register != null ){
    		if(register.getCollege() != null && StringUtils.hasText(register.getCollege().getId())){
    			log.debug("若已经选择学院，则查询学院下的专业信息.");
    			List<BaseMajorModel> majorList = compService.queryMajorByCollage(register.getCollege().getId());
    			model.addAttribute("majorList", majorList);
    		}
			if(register.getMajor() != null && StringUtils.hasText(register.getMajor().getId()) ){
				log.debug("若已经选择专业，则查询专业下的班级信息.");
				List<BaseClassModel> classList = compService.queryClassByMajor(register.getMajor().getId());
				model.addAttribute("classList", classList);
			}
		}
		return Constants.MENUKEY_REGISTER_INFO + "/listRegister" ;
	} 
	
	@RequestMapping({"/job/register/opt-view/viewRegister"})
	public String viewRegister(ModelMap model, HttpServletRequest request ) {
		log.info("查看报到证信息");
		
		String id = request.getParameter("id");
		RegisterModel register = this.registerService.findRegisterById(id);
		
		model.addAttribute("register", register);
		return Constants.MENUKEY_REGISTER_INFO + "/viewRegister" ;
	}
	
	@RequestMapping({"/job/register/opt-view/viewRegisterApprove"})
	public String viewRegisterApprove(ModelMap model, HttpServletRequest request ) {
		log.info("查看报到证审核信息");
		
		String id = request.getParameter("id");
		RegisterModel register = this.registerService.findRegisterById(id);
		
		model.addAttribute("register", register);
		return Constants.MENUKEY_REGISTER_INFO + "/viewRegisterApprove" ;
	}
	
	/**
	 * @Title: editRegister
	 * @Description: TODO(此方法，用于进入报到证信息的修改页面)
	 * @param model
	 * @param request
	 * @return
	 * @throws
	 */
	@RequestMapping({"/job/register/opt-edit/editRegister"})
	public String editRegister(ModelMap model, HttpServletRequest request ) {
		String id = request.getParameter("id");
		RegisterModel register = this.registerService.findRegisterById(id);
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(id));
		model.addAttribute("register", register);
		model.addAttribute("yearList", Constants.YEAR_LIST);
		
		log.info("修改报到证信息");
		return Constants.MENUKEY_REGISTER_INFO + "/editRegister";
	}
	
	
	/**
	 * @Title: updateRegister
	 * @Description: TODO(导入报到证编号之后，如果信息需要变更，可以通过此方法  进行修改 )
	 * 可以修改 学年、报到证编号、新报到证编号
	 * @param model
	 * @param register
	 * @return
	 * @throws
	 */
	@RequestMapping({ "/job/register/opt-update/updateRegister"})
	public String updateRegister(ModelMap model, RegisterModel register) {
		String id = register.getId();
		RegisterModel registerPo = this.registerService.findRegisterById(id);
		registerPo.setEmploymentYear(register.getEmploymentYear());
		registerPo.setRegisterCode(register.getRegisterCode());
		registerPo.setNewRegisterCode(register.getNewRegisterCode());
		this.registerService.update(registerPo);
		
		log.info("报到证信息更新成功!");
		
		return "redirect:/job/register/opt-query/registerList.do";
	}
	
	/**
	 * @Title: deleteRegister
	 * @Description: TODO(删除报到证)
	 * @param id
	 * @return
	 * @throws
	 */
	@RequestMapping({ "/job/register/opt-del/deleteRegister" })
	@ResponseBody
	public String deleteRegister(String id) {
		
		this.registerService.deleteRegisterById(id);
		log.info("删除操作成功！");
		
		return "success" ;
	}
	
	/**
	 * @Title: importRegisterCode
	 * @Description: (导入所有学生的报到证编号)
	 * @param model
	 * @param file
	 * @param maxSize
	 * @param allowedExt
	 * @param request
	 * @param session
	 * @return
	 * @throws Exception
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping({"job/register/opt-import/importRegisterCode"})
	public String importRegisterCode(ModelMap model, @RequestParam("file") MultipartFile file, String maxSize, String allowedExt, HttpServletRequest request, HttpSession session) throws Exception {
		List errorText = new ArrayList();
		String errorTemp = "";
		MultipartFileValidator validator = new MultipartFileValidator();
		if(DataUtil.isNotNull(allowedExt)) {
			validator.setAllowedExtStr(allowedExt.toLowerCase());
		}
		if(DataUtil.isNotNull(maxSize)) {
			validator.setMaxSize(Long.valueOf(maxSize).longValue());
		}else{
			validator.setMaxSize(20971520);
		}
		String returnValue = validator.validate(file);
		if(!returnValue.equals("")) {
			errorTemp = returnValue;
			errorText.add(errorTemp);
			model.addAttribute("errorText", errorText.size()==0 ? null : errorText);
		    model.addAttribute("importFlag", Boolean.valueOf(true));
		    return "job/register/importRegisterCode";
		}else{
			String tempFileId = this.fileUtil.saveSingleFile(true, file);
			File tempFile = this.fileUtil.getTempRealFile(tempFileId);
			String filePath = tempFile.getAbsolutePath();
			session.setAttribute("filePath", filePath);
			try {
				ImportUtil iu = new ImportUtil();
				 //Excel数据
				List<RegisterModel> list = iu.getDataList(filePath, "importRegisterCode", null, RegisterModel.class);       
				List<Object[]> arrayList = this.registerService.compareData(list); 
				//Excel与数据库中的数据有重复
				if((arrayList == null) || (arrayList.size() == 0)) {
					this.registerService.importOriginData(list);
				} else {
					session.setAttribute("arrayList", arrayList);
					
					Page page = new Page();
					page.setPageSize(Page.DEFAULT_PAGE_SIZE);
					page.setResult(arrayList);
					page.setStart(0L);
					page.setTotalCount(arrayList.size());
					model.addAttribute("page", page);
				}
			} catch (OfficeXmlFileException e) {
				e.printStackTrace();
				errorText.add("模板配置与数据类型不一致,请与系统管理员联系!");
			} catch (IOException e) {
				e.printStackTrace();
				errorTemp = "IOException" + e.getMessage();
				errorText.add(errorTemp);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				errorTemp = "IllegalAccessException" + e.getMessage();
				errorText.add(errorTemp);
			} catch (ExcelException e) {
				e.printStackTrace();
				errorTemp = e.getMessage();
				errorText.add(errorTemp);
			} catch (InstantiationException e) {
				e.printStackTrace();
				errorTemp = "InstantiationException" + e.getMessage();
				errorText.add(errorTemp);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			model.addAttribute("importFlag", Boolean.valueOf(true));
			model.addAttribute("errorText", errorText.size()==0 ? null : errorText);
			return "job/register/importRegisterCode";
		}
	}

	
	/**
	 * 
	 * @Title: importData
	 * @Description: (这个方法是对比之后的确认导入数据 )
	 * @param model
	 * @param session
	 * @param compareId
	 * @return
	 * @throws
	 */
	@SuppressWarnings("finally")
	@RequestMapping({"/job/register/opt-import/importConfirmData"})
	public String importData(ModelMap model, HttpSession session, @RequestParam("compareId") String compareId) {
		
		List<Object> errorText = new ArrayList<Object>();
		
		String filePath = session.getAttribute("filePath").toString();
		List<Object[]> arrayList = (List<Object[]>) session.getAttribute("arrayList");
		try {
			this.registerService.importLastData(arrayList, filePath, compareId);
		} catch (ExcelException e) {
			errorText.add(0, e.getMessage());
		    errorText = errorText.subList(0, errorText.size() > 20 ? 20 : errorText.size());
		    model.addAttribute("errorText", errorText.size()==0 ? null : errorText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (OfficeXmlFileException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally{
			model.addAttribute("importFlag", Boolean.valueOf(true));
		    return "job/register/importRegisterCode";
		}
	}
	
	@RequestMapping({"/job/replace/opt-query/statisticsRegisterByCollege"})
	public String statisticsRegister(ModelMap model, HttpServletRequest request, RegisterStatisticsModel registerStatistics){
		
		log.info("报到证改派率统计");
		
		if(registerStatistics !=null ){
			if(registerStatistics.getDicYear() == null ){
				registerStatistics.setDicYear(Constants.CURRENT_YEAR);
			}
			if(registerStatistics.getRange() == null){
				registerStatistics.setRange("1");
			}
		}
    	
    	List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
    	model.addAttribute("collegeList", collegeList);
    	
    	if(registerStatistics != null){
	    	if(DataUtil.isNotNull(registerStatistics.getCollege()) && DataUtil.isNotNull(registerStatistics.getCollege().getId()) ){
	    		String collegeId = registerStatistics.getCollege().getId();
	    		List<BaseMajorModel> majorList = compService.queryMajorByCollage(collegeId);
	    		model.addAttribute("majorList", majorList);
	    	}
	    	if(DataUtil.isNotNull(registerStatistics.getMajor()) && DataUtil.isNotNull(registerStatistics.getMajor().getId()) ){
	    		String majorId = registerStatistics.getMajor().getId();
	    		List<BaseClassModel> classList = compService.queryClassByMajor(majorId);
	    		model.addAttribute("classList", classList);
	    	}
    	}
    	Integer pageNo = request.getParameter("pageNo")!=null?Integer.valueOf(request.getParameter("pageNo")):1;
		Page page = new Page();
    	
		//返回页面的名字
		String returnUrl = "";
		if(registerStatistics.getRange() == null || registerStatistics.getRange().equals("1")) {
			log.info("页面按学院统计");
			returnUrl = "statisticsRegisterByCollege";
			page = registerService.queryRegisterCollegeList(pageNo, Page.DEFAULT_PAGE_SIZE, registerStatistics);
 		}else if(registerStatistics.getRange().equals("2")) {
 			log.info("页面按专业统计");
 			returnUrl = "statisticsRegisterByMajor";
 			page = registerService.queryRegisterMajorList(pageNo, Page.DEFAULT_PAGE_SIZE, registerStatistics);
 		}else {
 			log.info("页面按班级统计");
 			returnUrl = "statisticsRegisterByClass";
 			page = registerService.queryRegisterClassList(pageNo, Page.DEFAULT_PAGE_SIZE, registerStatistics);
 		}
		
		model.addAttribute("page", page);
		
		model.addAttribute("yearList", dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("registerStatistics", registerStatistics);
		
		return Constants.MENUKEY_REGISTER_INFO + "/replaceStatistics/" + returnUrl ;
		
	}
	
	 /***
		 * 导出预处理(即弹出导出页)
		 * @param model
		 * @param request
		 * @return
		 */
	@RequestMapping({"/job/register/nsm/exportRegisterList"})
	public String exportRegisterList(ModelMap model, HttpServletRequest request){
		int exportSize = Integer.valueOf(request.getParameter("exportSize")).intValue();
		String pageTotal = request.getParameter("pageTotalCount");
		int pageTotalCount = 0;
		if(pageTotal != null && !pageTotal.equals("")){
			pageTotalCount = Integer.valueOf(request.getParameter("pageTotalCount")).intValue();
		}
	    int maxNumber = 0;
	    if (pageTotalCount < exportSize)
	    	maxNumber = 1;
	    else if (pageTotalCount % exportSize == 0)
	    	maxNumber = pageTotalCount / exportSize;
	    else {
	    	maxNumber = pageTotalCount / exportSize + 1;
	    }
	    model.addAttribute("exportSize", Integer.valueOf(exportSize));
	    model.addAttribute("maxNumber", Integer.valueOf(maxNumber));
	    if (maxNumber <= 500)
	    	model.addAttribute("isMore", "false");
	    else {
	    	model.addAttribute("isMore", "true");
	    }
	    return "/job/register/replaceStatistics/exportRegisterView";
	}
			
    @RequestMapping({"/job/register/opt-export/exportRegisterList"})
    public void exportReportProgress(ModelMap model,HttpServletRequest request, HttpServletResponse response, RegisterStatisticsModel registerStatistics ) throws ParseException {
    	log.info("导出报到证改派excel表");
	    	
        String exportSize=request.getParameter("exportSize");
        String exportPage=request.getParameter("exportPage");
	  	
  		//导出类封装
		HSSFWorkbook wb = new HSSFWorkbook(); 
		
		String fileName = "";
		if(registerStatistics.getDicYear() != null && StringUtils.hasText(registerStatistics.getDicYear().getId())){
			String dicYear = registerStatistics.getDicYear().getId();
			fileName = this.dicService.getDic(dicYear).getName();
		}
        String range = registerStatistics.getRange();
  		
        try{
        	if(range == null || range.equals("1")){
				Page page = registerService.queryRegisterCollegeList(Integer.parseInt(exportPage) ,Integer.parseInt(exportSize), registerStatistics);
				
			    List<Map> listMap = new ArrayList<Map>();
			    List<RegisterStatisticsCollegeModel> infoList = (List)page.getResult();
			    for(RegisterStatisticsCollegeModel info : infoList) {
			    	Map<String, Object> map = new HashMap<String, Object>();
			    	
			    	map.put("employmentYear", info.getEmploymentYear() !=null ? info.getEmploymentYear().getName() : "" );
			    	map.put("college",  info.getCollege() != null ? info.getCollege().getName() : "");
			    	map.put("pending", info.getSubmit());
			    	map.put("pass", info.getPass());
			    	map.put("refuse", info.getRefuse()); 
			    	map.put("total", info.getTotal());
			    	
			    	if(info.getTotal() != 0){
			    		double percentage = ((double)info.getPass()) / ((double)info.getTotal());
		    			NumberFormat nf = NumberFormat.getPercentInstance(); 
			    		nf.setMinimumFractionDigits(2);// 小数点后保留几位
			    		String str = nf.format(percentage);
			    		map.put("percentage", str);
			    	} else{
			    		map.put("percentage", "0.00%");
			    	}
			    	
			    	listMap.add(map);
				}
			    fileName += "按学院统计报到证改派表.xls";
				wb = this.excelService.exportData("export_register_college.xls", "exportRegisterByCollege", listMap);
			
        	}
        	
        	if(range.equals("2")){
				Page page = registerService.queryRegisterMajorList(Integer.parseInt(exportPage) ,Integer.parseInt(exportSize), registerStatistics);
				
			    List<Map> listMap = new ArrayList<Map>();
			    List<RegisterStatisticsMajorModel> infoList = (List)page.getResult();
			    for(RegisterStatisticsMajorModel info : infoList) {
			    	Map<String, Object> map = new HashMap<String, Object>();
			    	
			    	map.put("employmentYear", info.getEmploymentYear() !=null ? info.getEmploymentYear().getName() : "" );
			    	map.put("college",  info.getMajor() != null && info.getMajor().getCollage() != null ? info.getMajor().getCollage().getName() : "");
			    	map.put("major",  info.getMajor() != null ? info.getMajor().getMajorName() : "");
			    	map.put("pending", info.getSubmit());
			    	map.put("pass", info.getPass());
			    	map.put("refuse", info.getRefuse()); 
			    	map.put("total", info.getTotal());
			    	
			    	if(info.getTotal() != 0){
			    		double percentage = ((double)info.getPass()) / ((double)info.getTotal());
		    			NumberFormat nf = NumberFormat.getPercentInstance(); 
			    		nf.setMinimumFractionDigits(2);// 小数点后保留几位
			    		String str = nf.format(percentage);
			    		map.put("percentage", str);
			    	} else{
			    		map.put("percentage", "0.00%");
			    	}
			    	
			    	listMap.add(map);
				}
			    fileName += "按专业统计报到证改派表.xls";
				wb = this.excelService.exportData("export_register_major.xls", "exportRegisterByMajor", listMap);
			
        	}
		        
        	if(range.equals("3")){
				Page page = registerService.queryRegisterClassList(Integer.parseInt(exportPage) ,Integer.parseInt(exportSize), registerStatistics);
				
			    List<Map> listMap = new ArrayList<Map>();
			    List<RegisterStatisticsClassModel> infoList = (List)page.getResult();
			    for(RegisterStatisticsClassModel info : infoList) {
			    	Map<String, Object> map = new HashMap<String, Object>();
			    	
			    	map.put("employmentYear", info.getEmploymentYear() !=null ? info.getEmploymentYear().getName() : "" );
			    	map.put("college", info.getClassId() != null && info.getClassId().getMajor() !=null && info.getClassId().getMajor().getCollage() != null ? info.getClassId().getMajor().getCollage().getName() : "");
			    	map.put("major", info.getClassId() != null && info.getClassId().getMajor() !=null ? info.getClassId().getMajor().getMajorName() : "");
			    	map.put("class", info.getClassId() != null ? info.getClassId().getClassName() : "");
			    	map.put("pending", info.getSubmit());
			    	map.put("pass", info.getPass());
			    	map.put("refuse", info.getRefuse()); 
			    	map.put("total", info.getTotal());
			    	
			    	if(info.getTotal() != 0){
			    		double percentage = ((double)info.getPass()) / ((double)info.getTotal());
		    			NumberFormat nf = NumberFormat.getPercentInstance(); 
			    		nf.setMinimumFractionDigits(2);// 小数点后保留几位
			    		String str = nf.format(percentage);
			    		map.put("percentage", str);
			    	} else{
			    		map.put("percentage", "0.00%");
			    	}
			    	
			    	listMap.add(map);
				}
			    fileName += "按班级统计报到证改派表.xls";
				wb = this.excelService.exportData("export_register_class.xls", "exportRegisterByClass", listMap);
			
        	}
        	
		    response.setContentType("application/x-excel");
		    response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("GBK"), "iso-8859-1"));
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
    
    
    @RequestMapping(value = {"/job/register/opt-query/registerCodeCheck"},produces = {"text/plain;charset=UTF-8"})
    @ResponseBody
    public String checkCodeRepeat(@RequestParam String id,@RequestParam String registerCode,@RequestParam String employmentYear)
    {    
	     if (this.registerService.checkCodeRepeat(id,employmentYear,registerCode)) {
		       return "false";
		     }
		     return "true";
    }

	
}
