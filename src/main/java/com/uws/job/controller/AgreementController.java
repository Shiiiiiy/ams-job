
package com.uws.job.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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
import com.uws.domain.job.AgreementModel;
import com.uws.domain.job.AgreementStatisticsClassModel;
import com.uws.domain.job.AgreementStatisticsCollegeModel;
import com.uws.domain.job.AgreementStatisticsMajorModel;
import com.uws.domain.job.AgreementStatisticsModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.job.service.IAgreementService;
import com.uws.job.util.Constants;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.IDicService;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.sys.util.MultipartFileValidator;
import com.uws.util.CheckUtils;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;

/**
 * 
* @ClassName: AgreementController 
* @Description: 就业协议书控制器类 
* 主要实现的功能有 就业协议书编号导入，  就业协议的统计功能以及导出功能
* @author 联合永道
* @date 2015-10-9 下午6:27:33 
*
 */
@Controller
public class AgreementController extends BaseController {
	
	private static Logger log = new LoggerFactory(AgreementController.class);

	@Autowired
	private IAgreementService agreementService;
	@Autowired
	private IBaseDataService baseDataService;
	@Autowired
	private ICompService compService;
	@Autowired
	private IExcelService excelService;
	@Autowired
	private IDicService dicService;
	
	private DicUtil dicUtil = DicFactory.getDicUtil();
	
	private FileUtil fileUtil = FileFactory.getFileUtil();
	/**
	 * 
	 * @Title: initBinder
	 * @Description: (进行日期类型数据处理)
	 * @param binder
	 * @throws
	 */
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
	/**
	 * 查询所有学生的就业协议信息
	 */
	@RequestMapping({"/job/agreement/opt-query/agreementList"})
	public String listAgreement(ModelMap model, HttpServletRequest request, AgreementModel agreement){
		log.info("查询就业协议书列表");
		
		if(agreement.getEmploymentYear() == null){
			agreement.setEmploymentYear(Constants.CURRENT_YEAR);
		}
		
		String collegeId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
		if(CheckUtils.isCurrentOrgEqCollege(collegeId)){
			model.addAttribute("collegeId", collegeId);
			BaseAcademyModel college = new BaseAcademyModel();
			college.setId(collegeId);
			agreement.setCollege(college);
		}
		
    	int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
    	Page page = agreementService.queryAgreementList(pageNo, Page.DEFAULT_PAGE_SIZE, agreement);
    	
    	model.addAttribute("page", page);
    	model.addAttribute("yearList", Constants.YEAR_LIST);
    	model.addAttribute("agreement", agreement);
    	
		List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
		model.addAttribute("collegeList", collegeList);
		
		if(agreement != null ){
    		if(agreement.getCollege() != null && StringUtils.hasText(agreement.getCollege().getId())){
    			log.debug("若已经选择学院，则查询学院下的专业信息.");
    			List<BaseMajorModel> majorList = compService.queryMajorByCollage(agreement.getCollege().getId());
    			model.addAttribute("majorList", majorList);
    		}
			if(agreement.getMajor() != null && StringUtils.hasText(agreement.getMajor().getId()) ){
				log.debug("若已经选择专业，则查询专业下的班级信息.");
				List<BaseClassModel> classList = compService.queryClassByMajor(agreement.getMajor().getId());
				model.addAttribute("classList", classList);
			}
		}
		
		return Constants.MENUKEY_AGREEMENT_INFO + "/listAgreement" ;
	} 
	
	@RequestMapping({"/job/agreement/opt-view/viewAgreement"})
	public String viewAgreement(ModelMap model, HttpServletRequest request ) {
		log.info("查看就业协议信息");
		
		String id = request.getParameter("id");
		AgreementModel agreement = this.agreementService.findAgreementById(id);
		
		model.addAttribute("agreement", agreement);
		return Constants.MENUKEY_AGREEMENT_INFO + "/viewAgreement" ;
	}
	
	@RequestMapping({"/job/agreement/opt-view/viewAgreementApprove"})
	public String viewAgreementApprove(ModelMap model, HttpServletRequest request ) {
		log.info("查看就业协议信息");
		
		String id = request.getParameter("id");
		AgreementModel agreement = this.agreementService.findAgreementById(id);
		
		model.addAttribute("agreement", agreement);
		return Constants.MENUKEY_AGREEMENT_INFO + "/viewAgreementApprove" ;
	}
	
	/**
	 * @Title: editAgreement
	 * @Description: TODO(修改就业协议信息)
	 * @param model
	 * @param request
	 * @return
	 * @throws
	 */
	@RequestMapping({"/job/agreement/opt-edit/editAgreement"})
	public String editAgreement(ModelMap model, HttpServletRequest request ) {
		String id = request.getParameter("id");
		AgreementModel agreement = this.agreementService.findAgreementById(id);
		model.addAttribute("agreement", agreement);
		model.addAttribute("yearList", Constants.YEAR_LIST);
		
		log.info("修改就业协议信息");
		return Constants.MENUKEY_AGREEMENT_INFO + "/editAgreement";
	}
	
	
	/**
	 * @Title: updateAgreement
	 * @Description: TODO(招生就业处可以通过此方法对学生的 学年、就业协议书编号、新就业协议书编号 予以修改，然后保存到数据库中)
	 * @param model
	 * @param agreement
	 * @param fileId
	 * @return
	 * @throws
	 */
	@RequestMapping({ "/job/agreement/opt-update/updateAgreement"})
	public String updateAgreement(ModelMap model, AgreementModel agreement) {
		String id = agreement.getId();
		AgreementModel agreementPo = this.agreementService.findAgreementById(id);
		agreementPo.setEmploymentYear(agreement.getEmploymentYear());
		agreementPo.setAgreementCode(agreement.getAgreementCode());
		agreementPo.setNewAgreementCode(agreement.getNewAgreementCode());
		this.agreementService.update(agreementPo);
		
		log.info("就业协议信息更新成功!");
		return "redirect:/job/agreement/opt-query/agreementList.do";
	}
	
	/**
	 * @Title: deleteAgreement
	 * @Description: TODO(删除就业协议)
	 * @param id
	 * @return
	 * @throws
	 */
	@RequestMapping({ "/job/agreement/opt-del/deleteAgreement" })
	@ResponseBody
	public String deleteAgreement(String id) {
		
		this.agreementService.deleteAgreementById(id);
		log.info("删除操作成功！");
		
		return "success" ;
	}
	
	/**
	 * @Title: importAgreementCode
	 * @Description: TODO(导入所有学生的就业协议编号)
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
	@RequestMapping({"job/aggreement/opt-import/importAgreementCode"})
	public String importAgreementCode(ModelMap model, @RequestParam("file") MultipartFile file, String maxSize, String allowedExt, HttpServletRequest request, HttpSession session) throws Exception {
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
		    return "job/agreement/importAgreementCode";
		}else{
			String tempFileId = this.fileUtil.saveSingleFile(true, file);
			File tempFile = this.fileUtil.getTempRealFile(tempFileId);
			String filePath = tempFile.getAbsolutePath();
			session.setAttribute("filePath", filePath);
			try {
				ImportUtil iu = new ImportUtil();
				 //Excel数据
				List<AgreementModel> list = iu.getDataList(filePath, "importAgreementCode", null, AgreementModel.class);       
				List<Object[]> arrayList = this.agreementService.compareData(list); 
				//Excel与数据库中的数据有重复
				if((arrayList == null) || (arrayList.size() == 0)) {
					this.agreementService.importOriginData(list);
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
			return "job/agreement/importAgreementCode";
		}
	}

	/**
	 * @Title: importData
	 * @Description: (这个方法是对比之后的确认导入数据 )
	 * @param model
	 * @param session
	 * @param compareId
	 * @return
	 * @throws
	 */
	@SuppressWarnings("finally")
	@RequestMapping({"/job/agreement/opt-import/importConfirmData"})
	public String importData(ModelMap model, HttpSession session, @RequestParam("compareId") String compareId) {
		List<Object> errorText = new ArrayList<Object>();
		
		String filePath = session.getAttribute("filePath").toString();
		List<Object[]> arrayList = (List<Object[]>) session.getAttribute("arrayList");
		try {
			this.agreementService.importLastData(arrayList, filePath, compareId);
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
		    return "job/agreement/importAgreementCode";
		}
	}
	
	@RequestMapping({"/job/statistics/opt-query/statisticsAgreementByCollege"})
	public String statisticsAgreement(ModelMap model, HttpServletRequest request, AgreementStatisticsModel agreementStatistics){
		
		log.info("就业协议补办统计");
			
		if(agreementStatistics !=null ){
			if(agreementStatistics.getDicYear() == null){
				agreementStatistics.setDicYear(Constants.CURRENT_YEAR);
			}
			if(agreementStatistics.getRange() == null){
				agreementStatistics.setRange("1");
			}
		}
			
    	
    	List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
    	model.addAttribute("collegeList", collegeList);
    	
    	if(agreementStatistics != null){
	    	if(DataUtil.isNotNull(agreementStatistics.getCollege()) && DataUtil.isNotNull(agreementStatistics.getCollege().getId()) ){
	    		String collegeId = agreementStatistics.getCollege().getId();
	    		List<BaseMajorModel> majorList = compService.queryMajorByCollage(collegeId);
	    		model.addAttribute("majorList", majorList);
	    	}
	    	if(DataUtil.isNotNull(agreementStatistics.getMajor()) && DataUtil.isNotNull(agreementStatistics.getMajor().getId()) ){
	    		String majorId = agreementStatistics.getMajor().getId();
	    		List<BaseClassModel> classList = compService.queryClassByMajor(majorId);
	    		model.addAttribute("classList", classList);
	    	}
    	}
    	Integer pageNo = request.getParameter("pageNo")!=null?Integer.valueOf(request.getParameter("pageNo")):1;
    	
		//返回页面的名字
		String returnUrl = "";
		if(agreementStatistics.getRange() == null || agreementStatistics.getRange().equals("1")) {
			log.info("页面按学院统计");
			returnUrl = "statisticsAgreementByCollege";
			Page page = agreementService.queryAgreementCollegeList(pageNo, Page.DEFAULT_PAGE_SIZE, agreementStatistics);
			model.addAttribute("page", page);
 		}else if(agreementStatistics.getRange().equals("2")) {
 			log.info("页面按专业统计");
 			returnUrl = "statisticsAgreementByMajor";
 			Page page = agreementService.queryAgreementMajorList(pageNo, Page.DEFAULT_PAGE_SIZE, agreementStatistics);
 			model.addAttribute("page", page);
 		}else if(agreementStatistics.getRange().equals("3")) {
 			log.info("页面按班级统计");
 			returnUrl = "statisticsAgreementByClass";
 			Page page = agreementService.queryAgreementClassList(pageNo, Page.DEFAULT_PAGE_SIZE, agreementStatistics);
 			model.addAttribute("page", page);
 		}
		
		model.addAttribute("yearList", dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("agreementStatistics", agreementStatistics);
		
		return Constants.MENUKEY_AGREEMENT_INFO + "/replaceStatistics/" + returnUrl ;
	}
	
	 /***
	 * 导出预处理(即弹出导出页)
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping({"/job/agreement/nsm/exportPrepare"})
	public String exportPrepare(ModelMap model, HttpServletRequest request){
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
	    return "/job/agreement/replaceStatistics/exportAgreementView";
	}
		
    @RequestMapping({"/job/agreement/opt-export/exportAgreementList"})
    public void exportAgreementList(ModelMap model,HttpServletRequest request, HttpServletResponse response, AgreementStatisticsModel agreementStatistics) throws ParseException {
    	log.info("导出就业协议补办统计方法");
    	
        String exportSize=request.getParameter("exportSize");
        String exportPage=request.getParameter("exportPage");
	  	
  		//导出类封装
		HSSFWorkbook wb = new HSSFWorkbook(); 
		
		String fileName = "";
		if(agreementStatistics.getDicYear() != null && StringUtils.hasText(agreementStatistics.getDicYear().getId())){
			String dicYear = agreementStatistics.getDicYear().getId();
			fileName = this.dicService.getDic(dicYear).getName();
		}
		
        String range = agreementStatistics.getRange();
  		
        try{
        	if(range == null || range.equals("1")){
				Page page = agreementService.queryAgreementCollegeList(Integer.parseInt(exportPage) ,Integer.parseInt(exportSize), agreementStatistics);
				
			    List<Map> listMap = new ArrayList<Map>();
			    List<AgreementStatisticsCollegeModel> infoList = (List)page.getResult();
			    for(AgreementStatisticsCollegeModel info : infoList) {
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
			    fileName += "按学院统计就业协议补办表.xls";
				wb = this.excelService.exportData("export_agreement_college.xls", "exportAgreementByCollege", listMap);
			
        	}
        	
        	if(range.equals("2")){
				Page page = agreementService.queryAgreementMajorList(Integer.parseInt(exportPage) ,Integer.parseInt(exportSize), agreementStatistics);
				
			    List<Map> listMap = new ArrayList<Map>();
			    List<AgreementStatisticsMajorModel> infoList = (List)page.getResult();
			    for(AgreementStatisticsMajorModel info : infoList) {
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
			    fileName += "按专业统计就业协议补办表.xls";
				wb = this.excelService.exportData("export_agreement_major.xls", "exportAgreementByMajor", listMap);
			
        	}
		        
        	if(range.equals("3")){
				Page page = agreementService.queryAgreementClassList(Integer.parseInt(exportPage) ,Integer.parseInt(exportSize), agreementStatistics);
				
			    List<Map> listMap = new ArrayList<Map>();
			    List<AgreementStatisticsClassModel> infoList = (List)page.getResult();
			    for(AgreementStatisticsClassModel info : infoList) {
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
			    fileName += "按班级统计就业协议补办表.xls";
				wb = this.excelService.exportData("export_agreement_class.xls", "exportAgreementByClass", listMap);
			
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
    
    /**
     * 
     * @Title: AgreementController.java 
     * @Package com.uws.job.controller 
     * @Description: 校验协议书编号是否重复
     * @author LiuChen 
     * @date 2016-2-24 上午11:22:20
     */
    @RequestMapping(value = {"/job/agreement/opt-query/agreementCodeCheck"},produces = {"text/plain;charset=UTF-8"})
    @ResponseBody
    public String checkCodeRepeat(@RequestParam String id,@RequestParam String agreementCode,@RequestParam String employmentYear)
    {    
    	// AgreementModel agreementModel = agreementService.queryAgreementByCode(employmentYear,agreementCode);
    	// if (agreementModel != null && StringUtils.hasText(agreementModel.getId())){
	    //   return "false";
	    // }
	    // return "true";
	     if (this.agreementService.checkCodeRepeat(id,employmentYear,agreementCode)) {
		       return "false";
		     }
		     return "true";
    }
    
}
			
