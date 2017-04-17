package com.uws.job.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.uws.common.service.IBaseDataService;
import com.uws.comp.service.ICompService;
import com.uws.core.base.BaseController;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.job.EmploymentInfo;
import com.uws.job.service.IEmploymentInfoService;
import com.uws.job.util.Constants;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sys.model.SysConfig;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.ISysConfigService;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.sys.util.MultipartFileValidator;
import com.uws.util.CheckUtils;
import com.uws.util.ProjectSessionUtils;

/**
 * 就业信息维护管理EmploymentInfoController
 * @author liuchen
 *
 */
@Controller
public class EmploymentInfoController extends BaseController{
	
	@Autowired
	private IEmploymentInfoService employmentInfoSevice;
	@Autowired
	private IBaseDataService baseDataService;
	@Autowired
	private ICompService compService;
	@Autowired
	private ISysConfigService sysConfigService;
	// 日志
    private Logger log = new LoggerFactory(EmploymentInfoController.class);
    //数据字典工具类
    private DicUtil dicUtil = DicFactory.getDicUtil();
    //上传文件工具类
    private FileUtil fileUtil = FileFactory.getFileUtil();
    
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
    
    /**
     * 
    * @Title: EmploymentInfoController.java 
    * @Package com.uws.job.controller 
    * @Description:就业信息维护列表查询
    * @author pc  
    * @date 2015-10-9 下午2:55:40
     */               
    @RequestMapping("/job/employment/opt-query/queryEmploymentInfoList")
	public String listEmploymentInfo(ModelMap model,HttpServletRequest request,EmploymentInfo employmenInfo){
		log.info("就业信息维护查询列表");
		String collegeId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
		BaseAcademyModel college = this.baseDataService.findAcademyById(collegeId);
		if(CheckUtils.isCurrentOrgEqCollege(collegeId)){
			model.addAttribute("collegeStatus", "false");
			employmenInfo.setStrCollege(college);
			model.addAttribute("college", college);
		}
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		Page page = this.employmentInfoSevice.queryEmploymentInfoList(pageNo,Page.DEFAULT_PAGE_SIZE, employmenInfo);
		//学院
    	List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
    	//专业
    	List<BaseMajorModel> majorList = null;
    	//班级
    	List<BaseClassModel> classList = null;
    	if(employmenInfo!= null && employmenInfo.getStrCollege()!= null && com.uws.core.util.StringUtils.hasText(employmenInfo.getStrCollege().getId())){
    		majorList = compService.queryMajorByCollage(employmenInfo.getStrCollege().getId());
    	}
    	if(employmenInfo!= null && employmenInfo.getStrMajor()!= null && com.uws.core.util.StringUtils.hasText(employmenInfo.getStrMajor().getId())){
    		classList = compService.queryClassByMajor(employmenInfo.getStrMajor().getId());
    	}
    	model.addAttribute("page", page);
    	model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
    	model.addAttribute("employmenInfo", employmenInfo);
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("educationList",dicUtil.getDicInfoList("EDUCATION"));
		model.addAttribute("genderList",dicUtil.getDicInfoList("GENDER"));
		model.addAttribute("normalList",dicUtil.getDicInfoList("NORMAL_TYPE"));
		model.addAttribute("cultureList",dicUtil.getDicInfoList("CULTURE_TYPE"));
		model.addAttribute("recruitList",dicUtil.getDicInfoList("RECRUIT_STUDENT_TYPE"));
		model.addAttribute("difficultTypeList",dicUtil.getDicInfoList("DIFFICULT_CATEGORY_JOB"));
		return Constants.MENUKEY_JOB_EMPLOYMENT+"/employmentInfoList";
	}
    
    /**
     * 
    * @Title: EmploymentInfoController.java 
    * @Package com.uws.job.controller 
    * @Description: 就业信息维护页面
    * @author pc  
    * @date 2015-11-25 上午10:43:47
     */
    @RequestMapping({"/job/employment/opt-add/editEmploymentInfo","/job/employment/opt-update/editEmploymentInfo" })
	public String editEmploymentInfo(ModelMap model,HttpServletRequest request){
		String id = request.getParameter("id");
		if(com.uws.core.util.StringUtils.hasText(id)){
			EmploymentInfo employmentInfo = this.employmentInfoSevice.findEmploymentInfoById(id);
			model.addAttribute("employmentInfo", employmentInfo);
			log.info("修改就业信息");
		}else{
			model.addAttribute("employmentInfo", new EmploymentInfo());
			log.info("新增就业信息");
		}
		model.addAttribute("collegeId", ProjectSessionUtils.getCurrentTeacherOrgId(request));
		model.addAttribute("educationList",dicUtil.getDicInfoList("EDUCATION"));
		model.addAttribute("genderList",dicUtil.getDicInfoList("GENDER"));
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("isList",dicUtil.getDicInfoList("Y&N"));
		model.addAttribute("normalList",dicUtil.getDicInfoList("NORMAL_TYPE"));
		model.addAttribute("cultureList",dicUtil.getDicInfoList("CULTURE_TYPE"));
		model.addAttribute("recruitList",dicUtil.getDicInfoList("RECRUIT_STUDENT_TYPE"));
		model.addAttribute("difficultTypeList",dicUtil.getDicInfoList("DIFFICULT_CATEGORY_JOB"));
		return Constants.MENUKEY_JOB_EMPLOYMENT+"/employmentInfoEdit";
	}
    
    /**
     * 
    * @Title: EmploymentInfoController.java 
    * @Package com.uws.job.controller 
    * @Description:就业信息保存
    * @author pc  
    * @date 2015-11-25 上午10:44:15
     */
    @RequestMapping(value = {"/job/employment/opt-save/saveEmploymentInfo","/job/employment/opt-update/updateEmploymentInfo"})
	public String saveEmploymentInfo(ModelMap model, HttpServletRequest request,EmploymentInfo employmentInfo) {
    	if(com.uws.core.util.StringUtils.hasText(employmentInfo.getId())){
			//就业信息修改
    		EmploymentInfo employmentInfoPo = this.employmentInfoSevice.findEmploymentInfoById(employmentInfo.getId());
			BeanUtils.copyProperties(employmentInfo,employmentInfoPo,new String[]{"createTime","college","major","classId"});
			this.employmentInfoSevice.updateEmploymentInfo(employmentInfoPo);
			log.info("就业信息修改成功!");
		}else{
			this.employmentInfoSevice.saveEmploymentInfo(employmentInfo);
			log.info("就业信息新增成功!");
		}
    	 return "redirect:/job/employment/opt-query/queryEmploymentInfoList.do";
	}
	
    /**
     * 
    * @Title: EmploymentInfoController.java 
    * @Package com.uws.job.controller 
    * @Description: 删除
    * @author pc  
    * @date 2015-11-25 上午10:44:30
     */
    @RequestMapping(value = {"/job/employment/opt-del/deleteEmploymentInfo" },produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String deleteEmploymentInfo(ModelMap model, HttpServletRequest request) {
		String id = request.getParameter("id");
		EmploymentInfo employmentInfo = this.employmentInfoSevice.findEmploymentInfoById(id);
		this.employmentInfoSevice.deleteEmploymentInfo(employmentInfo);
		log.info("删除操作成功！");
		return "success";
	}
    
    /**
     * 
     * @Title: EmploymentInfoController.java 
     * @Package com.uws.job.controller 
     * @Description: 验证学生不能重复添加
     * @author LiuChen 
     * @date 2015-12-9 下午3:53:50
     */
    @RequestMapping(value = {"/job/employment/opt-query/studentCheck"},produces = { "text/plain;charset=UTF-8"})
    @ResponseBody
    public String checkCodeRepeat(@RequestParam String id, @RequestParam String studentId)
    {   
	     if (this.employmentInfoSevice.isExistStudent(id,studentId)) {
	       return "true";
	     }
	     return "false";
    }
    
    
    /**
     * 
    * @Title: EmploymentInfoController.java 
    * @Package com.uws.job.controller 
    * @Description: 就业信息查看
    * @author pc  
    * @date 2015-11-25 上午10:47:45
     */
    @RequestMapping({"/job/employment/opt-view/viewEmploymentInfo" })
   	public String viewEmploymentInfo(ModelMap model,HttpServletRequest request){
   		String id = request.getParameter("id");
   		if(com.uws.core.util.StringUtils.hasText(id)){
   			EmploymentInfo employmentInfo = this.employmentInfoSevice.findEmploymentInfoById(id);
   			model.addAttribute("employmentInfo", employmentInfo);
   			model.addAttribute("native",dicUtil.getDicInfo("NATIVE",employmentInfo.getStudent().getSourceLand()));
   			log.info("就业信息查看");
   		}else{
   			model.addAttribute("employmentInfo", new EmploymentInfo());
   			log.info("就业信息查看");
   		}
   		model.addAttribute("educationList",dicUtil.getDicInfoList("EDUCATION"));
   		model.addAttribute("genderList",dicUtil.getDicInfoList("GENDER"));
   		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
   		model.addAttribute("isList",dicUtil.getDicInfoList("Y&N"));
   		model.addAttribute("normalList",dicUtil.getDicInfoList("NORMAL_TYPE"));
   		model.addAttribute("cultureList",dicUtil.getDicInfoList("CULTURE_TYPE"));
   		model.addAttribute("recruitList",dicUtil.getDicInfoList("RECRUIT_STUDENT_TYPE"));
   		model.addAttribute("difficultTypeList",dicUtil.getDicInfoList("DIFFICULT_CATEGORY_JOB"));
   		return Constants.MENUKEY_JOB_EMPLOYMENT+"/employmentInfoView";
   	}
    
    
	/**
	 * 执行导入
	 * @param model
	 * @param file
	 * @param maxSize
	 * @param allowedExt
	 * @param request
	 * @return
	 * @throws exception 
	 * @throws Exception 
	 */
	@RequestMapping(value="/job/employment/opt-import/importEmploymentInfo")
	public String importCourseInfo(ModelMap model, @RequestParam("file") MultipartFile file, 
		   String maxSize, String allowedExt, HttpServletRequest request, HttpSession session) throws Exception{

	     List errorText = new ArrayList();
	     String errorTemp = "";
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
	       errorTemp = returnValue;
	       errorText.add(returnValue);
	       model.addAttribute("errorText", errorText);
	       model.addAttribute("importFlag", Boolean.valueOf(true));
	       return Constants.MENUKEY_JOB_EMPLOYMENT+"/importInfo";
	     }
	 
	     String tempFileId = this.fileUtil.saveSingleFile(true, file);
	     File tempFile = this.fileUtil.getTempRealFile(tempFileId);
	     String filePath = tempFile.getAbsolutePath();
	 
	     session.setAttribute("filePath", filePath);
	     try
	     {
	       ImportUtil iu = new ImportUtil();
	       List list = iu.getDataList(filePath, "importEmployment", null, EmploymentInfo.class);
	       //比较数据是否重复
	       List arrayList = this.employmentInfoSevice.compareData(list);
	       if ((arrayList == null) || (arrayList.size() == 0))
	       {
	         this.employmentInfoSevice.importData(list,request);
	       }
	       else {
	         session.setAttribute("arrayList", arrayList);
	         List subList = null;
	         if (arrayList.size() >= Page.DEFAULT_PAGE_SIZE)
	           subList = arrayList.subList(0, Page.DEFAULT_PAGE_SIZE);
	         else
	           subList = arrayList;
	         Page page = new Page();
	         page.setPageSize(Page.DEFAULT_PAGE_SIZE);
	         page.setResult(subList);
	         page.setStart(0L);
	         page.setTotalCount(arrayList.size());
	         model.addAttribute("page", page);
	       }
	     }
	     catch (OfficeXmlFileException e) {
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
	     return Constants.MENUKEY_JOB_EMPLOYMENT+"/importInfo";
	}
	
		 /**
		 * 设置最大
		 * @return
		 */
		private int setMaxSize(){
			return 20971520;//20M
		}
		
		
	   /**
	    * 重复数据的分页
	    * @param model
	    * @param request
	    * @param session
	    * @param pageNo
	    * @return
	    */
	   @RequestMapping(value={"/job/employment/opt-query/employmentPageQuery"}, produces={"text/plain;charset=UTF-8"})
	   @ResponseBody
	   public String comparePageQuery(ModelMap model, HttpServletRequest request, HttpSession session, @RequestParam(value="pageNo", required=true) String pageNo)
	   {
	     List arrayList = (List)session.getAttribute("arrayList");
	     List<Object[]> subList = null;
	     int pageno = Integer.parseInt(pageNo); int length = arrayList.size();
	     if (arrayList.size() >= Page.DEFAULT_PAGE_SIZE * pageno)
	       subList = arrayList.subList(Page.DEFAULT_PAGE_SIZE * (pageno - 1), Page.DEFAULT_PAGE_SIZE * pageno);
	     else
	       subList = arrayList.subList(Page.DEFAULT_PAGE_SIZE * (pageno - 1), length);
	     JSONArray array = new JSONArray();
	     JSONObject obj = null; JSONObject json = new JSONObject();
	     for (Object[] employmentInfoArray : subList) {
	       EmploymentInfo employmentInfo = (EmploymentInfo)employmentInfoArray[0];
	       EmploymentInfo employmentExcel = (EmploymentInfo)employmentInfoArray[1];
	       obj = new JSONObject();
	       obj.put("id", employmentInfo.getId());
	       obj.put("name", employmentInfo.getStudent()!=null?employmentInfo.getStudent().getName():"");
	       obj.put("stuNumber", employmentInfo.getStudent()!=null?employmentInfo.getStudent().getStuNumber():"");
	       obj.put("difficultType", employmentInfo.getDifficultType()!=null?employmentInfo.getDifficultType().getName():"");
	       obj.put("excelName", employmentExcel.getStudent()!=null?employmentInfo.getStudent().getName():"");
	       obj.put("excelStuNumber", employmentExcel.getStudent()!=null?employmentInfo.getStudent().getStuNumber():"");
	       obj.put("excelDifficultType", employmentExcel.getDifficultType()!=null?employmentInfo.getDifficultType().getName():"");
	       array.add(obj);
	     }
	     json.put("result", array);
	     obj = new JSONObject();
	     obj.put("totalPageCount", Integer.valueOf(length % Page.DEFAULT_PAGE_SIZE == 0 ? length / Page.DEFAULT_PAGE_SIZE : length / Page.DEFAULT_PAGE_SIZE + 1));
	     obj.put("previousPageNo", Integer.valueOf(pageno - 1));
	     obj.put("nextPageNo", Integer.valueOf(pageno + 1));
	     obj.put("currentPageNo", Integer.valueOf(pageno));
	     obj.put("pageSize", Integer.valueOf(Page.DEFAULT_PAGE_SIZE));
	     obj.put("totalCount", Integer.valueOf(length));
	     json.put("page", obj);
	     return json.toString();
	   }
	
	 
	 
	   /**
		 * 对比导入数据
		 * @param model
		 * @param session
		 * @param compareId
		 * @return
	     * @throws Exception 
		 */
		@RequestMapping({"/job/employment/opt-query/compareData"})
		public String importData(ModelMap model, HttpSession session, @RequestParam("compareId") String compareId) throws Exception{
			List errorText = new ArrayList();
		    String filePath = session.getAttribute("filePath").toString();
		    List arrayList = (List)session.getAttribute("arrayList");
		    try {
		       this.employmentInfoSevice.importData(arrayList, filePath, compareId);
		     }
		    catch (ExcelException e) {
		      errorText = e.getMessageList();
		 
		      errorText = errorText.subList(0, errorText.size() > 20 ? 20 : errorText.size());
		      model.addAttribute("errorText", errorText);
		    } catch (OfficeXmlFileException e) {
		       e.printStackTrace();
		    } catch (IOException e) {
		       e.printStackTrace();
		    } catch (IllegalAccessException e) {
		       e.printStackTrace();
		    } catch (InstantiationException e) {
		       e.printStackTrace();
		    } catch (ClassNotFoundException e) {
		       e.printStackTrace(); } finally {
		    }
		    model.addAttribute("importFlag", Boolean.valueOf(true));
		    return Constants.MENUKEY_JOB_EMPLOYMENT+"/importInfo";
		}
		
		
		/**
		 * 
		* @Title: EmploymentInfoController.java 
		* @Package com.uws.job.controller 
		* @Description: 测评网址页面
		* @author pc  
		* @date 2015-10-15 下午3:49:11
		 */
		@RequestMapping({ "/job/evaluation/opt-update/editEvaluation" })
		public String editSysConfig(ModelMap model, HttpServletRequest request)
		{    
			//综合测评网站
			SysConfig webUrl = this.sysConfigService.getSysConfig(com.uws.common.util.Constants.EVALUATE_WEB_URL_CODE);
			 model.addAttribute("webUrl",webUrl);
			 return Constants.MENUKEY_JOB_EMPLOYMENT+"/editWebUrl";
		}
		

}
