package com.uws.job.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.common.service.IBaseDataService;
import com.uws.common.service.IRewardCommonService;
import com.uws.common.service.IStudentCommonService;
import com.uws.common.util.SchoolYearUtil;
import com.uws.comp.service.ICompService;
import com.uws.core.base.BaseController;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.service.IExcelService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.job.PoorStudentCollegeView;
import com.uws.domain.job.ProvinceGoodStudent;
import com.uws.domain.job.ProvinceGoodStudentClassView;
import com.uws.domain.job.ProvinceGoodStudentCollegeView;
import com.uws.domain.job.ProvinceGoodStudentMajorView;
import com.uws.domain.job.SchoolGoodStudent;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.reward.AwardInfo;
import com.uws.domain.reward.AwardType;
import com.uws.domain.reward.CountryBurseInfo;
import com.uws.domain.reward.StudentApplyInfo;
import com.uws.job.service.IProvinceGoodStudentService;
import com.uws.job.service.ISchoolGoodStudentService;
import com.uws.job.util.Constants;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.IDicService;
import com.uws.sys.service.impl.DicFactory;
import com.uws.util.CheckUtils;
import com.uws.util.ProjectSessionUtils;

/**
 * 省优秀毕业生Controller  ProvinceGoodStudentController
 * @author pc
 *
 */
@Controller
public class ProvinceGoodStudentController extends BaseController
{   
	@Autowired
	private IProvinceGoodStudentService provinceGoodStudentService;
	@Autowired
	private ISchoolGoodStudentService schoolGoodStudentService;
	@Autowired
	private IBaseDataService baseDataService;
	@Autowired
	private ICompService compService;
	@Autowired
	private IDicService dicService;
	@Autowired
	private IExcelService excelService;
	//学生公共接口
	@Autowired
	private IStudentCommonService studentCommonService;
	//奖助接口
	@Autowired
	private IRewardCommonService rewardCommonService;
	//数据字典工具类
    private DicUtil dicUtil = DicFactory.getDicUtil();
	// 日志
    private Logger log = new LoggerFactory(ProvinceGoodStudentController.class);
    // sessionUtil工具类
  	private SessionUtil sessionUtil = SessionFactory.getSession(Constants.MENUKEY_JOB_PROVINCE_GOOD_STUDENT);
    
    /**
     * 
    * @Title: ProvinceGoodStudentController.java 
    * @Package com.uws.job.controller 
    * @Description: 省优秀毕业生查询列表
    * @author pc  
    * @date 2015-11-17 下午2:22:51
     */
    @RequestMapping("/job/provinceGoodStudent/opt-query/queryProvinceGoodStudentList")
	public String listProvinceGoodStudent(ModelMap model,HttpServletRequest request,ProvinceGoodStudent provinceGoodStudent){
		log.info("省优秀毕业生查询列表");
		String currentStudentId = null;
		String yearId=request.getParameter("schoolGoodStudent.schoolYear.id");
		Dic yearDic = new Dic();
		if(com.uws.core.util.StringUtils.hasText(yearId)){
			yearDic = dicService.getDic(yearId);
			model.addAttribute("yearDic", yearDic);
		}else if(yearId == null){
			//获取当前学年字典。
			yearDic = SchoolYearUtil.getYearDic();
			model.addAttribute("yearDic", yearDic);
		}
		//判断登录人是否是学生
		if(ProjectSessionUtils.checkIsStudent(request))
		{   
			currentStudentId = sessionUtil.getCurrentUserId();
		}
		String collegeId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
		BaseAcademyModel college = this.baseDataService.findAcademyById(collegeId);
		if(CheckUtils.isCurrentOrgEqCollege(collegeId)){
			model.addAttribute("collegeStatus", "false");
			provinceGoodStudent.setCollege(college);
			model.addAttribute("college", college);
		}
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		Page page = this.provinceGoodStudentService.queryProvinceGoodStudentList(pageNo,Page.DEFAULT_PAGE_SIZE,provinceGoodStudent,yearDic,currentStudentId);
		//学院
    	List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
    	//专业
    	List<BaseMajorModel> majorList = null;
    	//班级
    	List<BaseClassModel> classList = null;
    	if(provinceGoodStudent!= null && provinceGoodStudent.getCollege()!=null  && com.uws.core.util.StringUtils.hasText(provinceGoodStudent.getCollege().getId())){
    		majorList = compService.queryMajorByCollage(provinceGoodStudent.getCollege().getId());
    	}
    	if(provinceGoodStudent!= null && provinceGoodStudent.getMajor()!=null &&com.uws.core.util.StringUtils.hasText(provinceGoodStudent.getMajor().getId())){
    		classList = compService.queryClassByMajor(provinceGoodStudent.getMajor().getId());
    	}
    	model.addAttribute("page", page);
    	model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
    	model.addAttribute("provinceGoodStudent", provinceGoodStudent);
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("statusList",dicUtil.getDicInfoList("APPLY_TYPE"));
		return Constants.MENUKEY_JOB_PROVINCE_GOOD_STUDENT+"provinceGoodStudentList";
	}
	
	/**'
	 * 
	* @Title: ProvinceGoodStudentController.java 
	* @Package com.uws.job.controller 
	* @Description:省优秀毕业生新增，修改页面
	* @author pc  
	* @date 2015-11-17 下午2:27:00
	 */
	@RequestMapping({"/job/provinceGoodStudent/opt-add/editProvinceGoodStudent"})
	public String editProvinceGoodStudent(ModelMap model,HttpServletRequest request,String id,String schoolId){
		if(com.uws.core.util.StringUtils.hasText(schoolId)){
			SchoolGoodStudent schoolGoodStudent = schoolGoodStudentService.querySchoolGoodStudentById(schoolId);
			model.addAttribute("schoolGoodStudent", schoolGoodStudent);
			model.addAttribute("native",dicUtil.getDicInfo("NATIVE",schoolGoodStudent.getStudentId().getSourceLand()));
		}else{
			model.addAttribute("schoolGoodStudent",new SchoolGoodStudent());
		}
		if(com.uws.core.util.StringUtils.hasText(id)){
			ProvinceGoodStudent provinceGoodStudent = provinceGoodStudentService.findProvinceGoodStudentById(id);
			model.addAttribute("provinceGoodStudent", provinceGoodStudent);
		}else{
			model.addAttribute("provinceGoodStudent",new ProvinceGoodStudent());
		}
		model.addAttribute("genderList",dicUtil.getDicInfoList("GENDER"));
		return Constants.MENUKEY_JOB_PROVINCE_GOOD_STUDENT+"provinceGoodStudentEdit";
	}
	
	/**
	 * 
	* @Title: ProvinceGoodStudentController.java 
	* @Package com.uws.job.controller 
	* @Description: 保存省优秀毕业生
	* @author pc  
	* @date 2015-11-18 下午2:50:48
	 */
	@RequestMapping(value = {"/job/provinceGoodStudent/opt-save/saveProvinceGoodStudent"})
	public String saveProvinceGoodStudent(ModelMap model, HttpServletRequest request,ProvinceGoodStudent provinceGoodStudent) {
    	if(com.uws.core.util.StringUtils.hasText(provinceGoodStudent.getId())){
			//就业信息修改
    		ProvinceGoodStudent provinceGoodStudentPo = provinceGoodStudentService.findProvinceGoodStudentById(provinceGoodStudent.getId());
			BeanUtils.copyProperties(provinceGoodStudent,provinceGoodStudentPo,new String[]{"schoolGoodStudent","createTime"});
			this.provinceGoodStudentService.update(provinceGoodStudentPo);
		}else{
			provinceGoodStudent.setStatus(Constants.STATUS_APPLY_DIC);
			this.provinceGoodStudentService.save(provinceGoodStudent);
		}
    	 return "redirect:/job/provinceGoodStudent/opt-query/queryProvinceGoodStudentList.do";
	}
	
	
	/**
	 * 根据id删除困难生信息
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = {"/job/provinceGoodStudent/opt-del/deleteProvinceGoodStudent" },produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String deleteProvinceGoodStudent(ModelMap model, HttpServletRequest request) {
		String id = request.getParameter("id");
		ProvinceGoodStudent provinceGoodStudentPo = provinceGoodStudentService.findProvinceGoodStudentById(id);
		provinceGoodStudentPo.setStatus(Constants.STATUS_APPLY_UNDO_DIC);
		this.provinceGoodStudentService.update(provinceGoodStudentPo);
		//this.provinceGoodStudentService.delete(provinceGoodStudentPo);
		log.info("撤销操作成功！");
		return "success";
	}
	
	
	 /**
     * 
    * @Title: ProvinceGoodStudentController.java 
    * @Package com.uws.job.controller 
    * @Description: 省优秀毕业生审核页面
    * @author pc  
    * @date 2015-11-18 下午2:26:46
     */
    @RequestMapping({"/job/provinceGoodStudent/opt-view/provinceGoodStudentView"})
	public String provinceGoodStudentView(ModelMap model,HttpServletRequest request,String id){
    	String sId = request.getParameter("sId");
    	if(com.uws.core.util.StringUtils.hasText(sId)){
    		SchoolGoodStudent schoolGoodStudent = schoolGoodStudentService.querySchoolGoodStudentById(sId);
    		model.addAttribute("schoolGoodStudent", schoolGoodStudent);
    		model.addAttribute("native",dicUtil.getDicInfo("NATIVE",schoolGoodStudent.getStudentId().getSourceLand()));
    	}
		if(com.uws.core.util.StringUtils.hasText(id)){
			ProvinceGoodStudent provinceGoodStudent = provinceGoodStudentService.findProvinceGoodStudentById(id);
			model.addAttribute("provinceGoodStudent", provinceGoodStudent);
		}else{
			model.addAttribute("provinceGoodStudent",new ProvinceGoodStudent());
		}
		model.addAttribute("genderList",dicUtil.getDicInfoList("GENDER"));
		return Constants.MENUKEY_JOB_PROVINCE_GOOD_STUDENT+"provinceGoodStudentView";
	}
	
	
	
	/**
	 * 
	* @Title: ProvinceGoodStudentController.java 
	* @Package com.uws.job.controller 
	* @Description:批量保存
	* @author pc  
	* @date 2015-11-18 下午2:50:31
	 */
	@ResponseBody
	@RequestMapping(value = {"/job/provinceGoodStudent/opt-save/saveProvinceGoodStudents" },produces = { "text/plain;charset=UTF-8" })
	public String saveProvinceGoodStudents(ModelMap model, HttpServletRequest request,String sids) {
		if(StringUtils.hasText(sids)){
			String[] pids = sids.split(",");
			for (String sid : pids)
            {   
				
				ProvinceGoodStudent provinceGoodStudentPo = this.provinceGoodStudentService.findProvinceGoodStudentBySid(sid);
				if(provinceGoodStudentPo!=null && StringUtils.hasText(provinceGoodStudentPo.getId()))
				{
					this.provinceGoodStudentService.update(provinceGoodStudentPo);
				}else
				{   
					ProvinceGoodStudent provinceGoodStudent = new ProvinceGoodStudent();
					SchoolGoodStudent schoolGoodStudent = new SchoolGoodStudent();
					schoolGoodStudent.setId(sid);
					provinceGoodStudent.setSchoolGoodStudent(schoolGoodStudent);
					provinceGoodStudent.setStatus(Constants.STATUS_APPLY_DIC);
					this.provinceGoodStudentService.save(provinceGoodStudent);
				}
            }
		}
		   return "success";
	}
	
	/**
	 * 
	* @Title: ProvinceGoodStudentController.java 
	* @Package com.uws.job.controller 
	* @Description:批量删除
	* @author pc  
	* @date 2015-11-18 上午11:00:16
	 */
	@RequestMapping(value = {"/job/provinceGoodStudent/opt-del/deleteProvinceGoodStudents" },produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String deleteProvinceGoodStudents(ModelMap model, HttpServletRequest request,String sids) {
		if(StringUtils.hasText(sids))
		{
			String[] pids = sids.split(",");
			for (String sid : pids)
            {
				ProvinceGoodStudent provinceGoodStudentPo = this.provinceGoodStudentService.findProvinceGoodStudentBySid(sid);
				if(provinceGoodStudentPo!=null && StringUtils.hasText(provinceGoodStudentPo.getId()))
				{
					this.provinceGoodStudentService.delete(provinceGoodStudentPo);
				}
			}  
        }
		log.info("删除操作成功！");
		return "success";
	}
	
		/**
		 * 
		* @Title: ProvinceGoodStudentController.java 
		* @Package com.uws.job.controller 
		* @Description:省优秀毕业生审核
		* @author pc  
		* @date 2015-11-18 上午11:19:17
		*/
	    @RequestMapping("/job/approveProvinceGoodStudent/opt-query/queryApproveProvinceGoodStudentList")
		public String queryApproveProvinceGoodStudentList(ModelMap model,HttpServletRequest request,ProvinceGoodStudent provinceGoodStudent){
			log.info("省优秀毕业生审核列表");
			int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
			String yearId=request.getParameter("schoolGoodStudent.schoolYear.id");
			Dic yearDic = new Dic();
			if(com.uws.core.util.StringUtils.hasText(yearId)){
				yearDic = dicService.getDic(yearId);
				model.addAttribute("yearDic", yearDic);
			}else if(yearId == null){
				//获取当前学年字典。
				yearDic = SchoolYearUtil.getYearDic();
				model.addAttribute("yearDic", yearDic);
			}
			Page page = this.provinceGoodStudentService.queryApproveProvinceGoodStudentList(pageNo,Page.DEFAULT_PAGE_SIZE,provinceGoodStudent,yearDic);
			//学院
	    	List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
	    	//专业
	    	List<BaseMajorModel> majorList = null;
	    	//班级
	    	List<BaseClassModel> classList = null;
	    	if(provinceGoodStudent!= null && provinceGoodStudent.getSchoolGoodStudent()!= null && provinceGoodStudent.getSchoolGoodStudent().getStudentId()!= null && provinceGoodStudent.getSchoolGoodStudent().getStudentId().getCollege()!=null &&
	    			com.uws.core.util.StringUtils.hasText(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getCollege().getId())){
	    		majorList = compService.queryMajorByCollage(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getCollege().getId());
	    	}
	    	if(provinceGoodStudent!= null && provinceGoodStudent.getSchoolGoodStudent()!= null && provinceGoodStudent.getSchoolGoodStudent().getStudentId()!= null && provinceGoodStudent.getSchoolGoodStudent().getStudentId().getMajor()!=null &&
	    			com.uws.core.util.StringUtils.hasText(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getMajor().getId())){
	    		classList = compService.queryClassByMajor(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getMajor().getId());
	    	}
	    	model.addAttribute("page", page);
	    	model.addAttribute("collegeList", collegeList);
			model.addAttribute("majorList", majorList);
			model.addAttribute("classList", classList);
	    	model.addAttribute("provinceGoodStudent", provinceGoodStudent);
			model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
			model.addAttribute("applyList", dicUtil.getDicInfoList("APPLY_TYPE"));
			model.addAttribute("processStatusMap", Constants.getApproveProvinceProcessStatus());
			return Constants.MENUKEY_JOB_PROVINCE_GOOD_STUDENT+"approveProvinceGoodStudentList";
		}
	    
	    
	    /**
	     * 
	    * @Title: ProvinceGoodStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description: 省优秀毕业生审核页面
	    * @author pc  
	    * @date 2015-11-18 下午2:26:46
	     */
	    @RequestMapping({"/job/approveProvinceGoodStudent/opt-add/approveProvinceGoodStudent","/job/approveProvinceGoodStudent/opt-view/approveProvinceGoodStudentView"})
		public String editaproveProvinceGoodStudent(ModelMap model,HttpServletRequest request,String id,String flag){
			if(com.uws.core.util.StringUtils.hasText(id)){
				ProvinceGoodStudent provinceGoodStudent = provinceGoodStudentService.findProvinceGoodStudentById(id);
				model.addAttribute("native",dicUtil.getDicInfo("NATIVE",provinceGoodStudent.getSchoolGoodStudent().getStudentId().getSourceLand()));
				model.addAttribute("provinceGoodStudent", provinceGoodStudent);
			}else{
				model.addAttribute("provinceGoodStudent",new ProvinceGoodStudent());
			}
			model.addAttribute("genderList",dicUtil.getDicInfoList("GENDER"));
			model.addAttribute("flag",flag);
			return Constants.MENUKEY_JOB_PROVINCE_GOOD_STUDENT+"approveProvinceGoodStudentEdit";
		}
	    
	    
	   /**
	    * 
	   * @Title: ProvinceGoodStudentController.java 
	   * @Package com.uws.job.controller 
	   * @Description: 省优秀毕业生审核保存
	   * @author pc  
	   * @date 2015-11-18 下午2:50:03
	    */
	    @RequestMapping(value = {"/job/approveProvinceGoodStudent/opt-save/saveApproveProvinceGoodStudent"})
		public String saveApproveProvinceGoodStudent(ModelMap model, HttpServletRequest request,String id,String flag) {
	    	String approveReason = request.getParameter("approveReason");
	    	if(com.uws.core.util.StringUtils.hasText(id)){
	    		//就业信息修改
	    		ProvinceGoodStudent provinceGoodStudent = provinceGoodStudentService.findProvinceGoodStudentById(id);
	    	if(StringUtils.hasText(flag) && flag.equals("1")){
	    		provinceGoodStudent.setStatus(Constants.STATUS_APPLY_PASS_DIC);
	    	}else{
	    		provinceGoodStudent.setStatus(Constants.STATUS_APPLY_UNPASS_DIC);
	    	}
	    	provinceGoodStudent.setApproveReason(approveReason);
	    	this.provinceGoodStudentService.update(provinceGoodStudent);
	    	log.info("省优秀毕业生审核成功!");
		}
	    	return "redirect:/job/approveProvinceGoodStudent/opt-query/queryApproveProvinceGoodStudentList.do";
    }
	    
	    
	    /**
	     * 
	    * @Title: ProvinceGoodStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description:批量审核页面
	    * @author pc  
	    * @date 2015-11-18 下午3:41:43
	     */
	    @RequestMapping({"/job/approveProvinceGoodStudent/opt-query/checkedApproveList"})
		public String checkedApproveList(ModelMap model,String selectedBox,HttpServletRequest request) {
			
			List<ProvinceGoodStudent> stuList = new ArrayList<ProvinceGoodStudent>();
			if(selectedBox !=null && selectedBox.indexOf(",") > -1) {
				String[] checkedIds = selectedBox.split(",");
				for(String s : checkedIds) {
					ProvinceGoodStudent provinceGoodStudentPo = provinceGoodStudentService.findProvinceGoodStudentById(s);
					if(DataUtil.isNotNull(provinceGoodStudentPo)) {
						stuList.add(provinceGoodStudentPo);
					}
				}
			}else if(DataUtil.isNotNull(selectedBox)){
				ProvinceGoodStudent provinceGoodStudentPo = provinceGoodStudentService.findProvinceGoodStudentById(selectedBox);
				if(DataUtil.isNotNull(provinceGoodStudentPo)) {
					stuList.add(provinceGoodStudentPo);
				}
			}
			model.addAttribute("stuList", stuList);
		    model.addAttribute("objectIds", selectedBox);
		    return Constants.MENUKEY_JOB_PROVINCE_GOOD_STUDENT+"provinceGoodStudentMulApprove";
		}    
	    
	   /**
	    * 
	   * @Title: ProvinceGoodStudentController.java 
	   * @Package com.uws.job.controller 
	   * @Description:批量审核保存
	   * @author pc  V_JOB_PROVINCE_GOOD_STUDENT_CLASS
	   * @date 2015-11-18 下午3:41:22
	    */
	    @RequestMapping({"/job/approveProvinceGoodStudents/opt-save/saveApproveProvinceGoodStudents"})
		public String saveApproveProvinceGoodStudents(ModelMap model,HttpServletRequest request,String objectIds,String flag) {
	    	String approveReason = request.getParameter("approveReason");
	    	if(StringUtils.hasText(objectIds)){
	    		String[] ids = objectIds.split(",");
	    		for (String id : ids)
                {
	    			ProvinceGoodStudent provinceGoodStudentPo = provinceGoodStudentService.findProvinceGoodStudentById(id);
	    			//就业信息修改
	    			if(StringUtils.hasText(flag) && flag.equals("1")){
	    				provinceGoodStudentPo.setStatus(Constants.STATUS_APPLY_PASS_DIC);
	    			}else{
	    				provinceGoodStudentPo.setStatus(Constants.STATUS_APPLY_UNPASS_DIC);
	    			}
	    			provinceGoodStudentPo.setApproveReason(approveReason);
	    			this.provinceGoodStudentService.update(provinceGoodStudentPo);
                }
		 }
	    	return "redirect:/job/approveProvinceGoodStudent/opt-query/queryApproveProvinceGoodStudentList.do";
	  } 
	    
	    /**
	     * 
	    * @Title: ProvinceGoodStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description:省优秀毕业生查询
	    * @author pc  
	    * @date 2015-11-19 下午4:32:53
	     */
	    @RequestMapping("/job/queryProvinceGoodStudent/opt-query/queryProvinceGoodStudentList")
		public String queryPassProvinceGoodStudentList(ModelMap model,HttpServletRequest request,ProvinceGoodStudent provinceGoodStudent){
			log.info("省优秀毕业生查询列表");
			String yearId=request.getParameter("schoolGoodStudent.schoolYear.id");
			Dic yearDic = new Dic();
			if(com.uws.core.util.StringUtils.hasText(yearId)){
				yearDic = dicService.getDic(yearId);
				model.addAttribute("yearDic", yearDic);
			}else if(yearId == null){
				//获取当前学年字典。
				yearDic = SchoolYearUtil.getYearDic();
				model.addAttribute("yearDic", yearDic);
			}
			String collegeId= ProjectSessionUtils.getCurrentTeacherOrgId(request);
			BaseAcademyModel college = this.baseDataService.findAcademyById(collegeId);
			if(CheckUtils.isCurrentOrgEqCollege(collegeId)){
				model.addAttribute("collegeStatus", "false");
				provinceGoodStudent.setCollege(college);
				model.addAttribute("college", college);
			}
			int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
			Page page = this.provinceGoodStudentService.queryPassProvinceGoodStudentList(pageNo,Page.DEFAULT_PAGE_SIZE,provinceGoodStudent,yearDic);
			//学院
	    	List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
	    	//专业
	    	List<BaseMajorModel> majorList = null;
	    	//班级
	    	List<BaseClassModel> classList = null;
	    	if(provinceGoodStudent!= null && provinceGoodStudent.getCollege()!=null  && com.uws.core.util.StringUtils.hasText(provinceGoodStudent.getCollege().getId())){
	    		majorList = compService.queryMajorByCollage(provinceGoodStudent.getCollege().getId());
	    	}
	    	if(provinceGoodStudent!= null && provinceGoodStudent.getMajor()!=null &&com.uws.core.util.StringUtils.hasText(provinceGoodStudent.getMajor().getId())){
	    		classList = compService.queryClassByMajor(provinceGoodStudent.getMajor().getId());
	    	}
	    	model.addAttribute("page", page);
	    	model.addAttribute("collegeList", collegeList);
			model.addAttribute("majorList", majorList);
			model.addAttribute("classList", classList);
	    	model.addAttribute("provinceGoodStudent", provinceGoodStudent);
			model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
			return Constants.MENUKEY_JOB_PROVINCE_GOOD_STUDENT+"selectProvinceGoodStudentList";
		}  
	    
	    /**
	     * 
	    * @Title: ProvinceGoodStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description:打印省优秀毕业生登记表，并获取学生在校获奖信息
	    * @author pc  
	    * @date 2015-11-19 下午5:01:03
	     */
	     @RequestMapping("/job/queryProvinceGoodStudent/opt-print/nsm/printProvinceGoodStudent")
		  public String printStudent(ModelMap model,HttpServletRequest request,String id)
		  {
			  if (StringUtils.hasText(id)) 
			  {
				    ProvinceGoodStudent provinceGoodStudent = provinceGoodStudentService.findProvinceGoodStudentById(id);
				    StudentInfoModel studentInfo = studentCommonService.queryStudentById(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getId());
			 		List<CountryBurseInfo> listBurse= rewardCommonService.getStuBurseList(studentInfo);//奖学金信息
			 		List<StudentApplyInfo> listAward=rewardCommonService.getStuAwardList(studentInfo);
			 		if(listAward!=null &&listAward.size()>0){
			 			for(int i=0;i<listAward.size();i++){
			 				if(listAward.get(i).getAwardTypeId()!=null){
			 					AwardType awardType = rewardCommonService.getAwardTypeById(listAward.get(i).getAwardTypeId().getId());
			 					if(DataUtil.isNotNull(awardType.getSecondAwardName())) {
			 						listAward.get(i).setId(awardType.getSecondAwardName().getName());//存放在id中,方便自己页面获取
			 					}else{
			 						AwardInfo awardInfo = this.rewardCommonService.getAwardInfoById(awardType.getAwardInfoId().getId());
			 						listAward.get(i).setId(awardInfo.getAwardName());//存放在id中,方便自己页面获取
			 					}
			 				}
			 				
			 			}
			 		}
				    model.addAttribute("provinceGoodStudent", provinceGoodStudent);
				    model.addAttribute("listBurse", listBurse);
			 		model.addAttribute("listAward", listAward);
			 		model.addAttribute("native",dicUtil.getDicInfo("NATIVE",provinceGoodStudent.getSchoolGoodStudent().getStudentId().getSourceLand()));
			  }
			  return Constants.MENUKEY_JOB_PROVINCE_GOOD_STUDENT+"printProvinceGoodStudent";
		  }
	     
	    /**
	     * 
	    * @Title: ProvinceGoodStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description:学生信息完善
	    * @author pc  
	    * @date 2015-11-20 下午3:56:11
	     */
	    @RequestMapping({"/job/queryProvinceGoodStudent/opt-save/writeProvinceGoodStudent"})
	 	public String provinceGoodStudentInfoEdit(ModelMap model,HttpServletRequest request,String id){
	 		if(com.uws.core.util.StringUtils.hasText(id)){
	 			ProvinceGoodStudent provinceGoodStudent = provinceGoodStudentService.findProvinceGoodStudentById(id);
	 			model.addAttribute("provinceGoodStudent", provinceGoodStudent);
	 		}else{
	 			model.addAttribute("provinceGoodStudent",new ProvinceGoodStudent());
	 		}
	 		return Constants.MENUKEY_JOB_PROVINCE_GOOD_STUDENT+"writeProvinceGoodStudent";
	 	}
	    
	    /**
	     * 
	    * @Title: ProvinceGoodStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description:保存完善的信息
	    * @author pc  
	    * @date 2015-11-20 下午4:58:01
	     */
	    @RequestMapping({"/job/queryProvinceGoodStudent/opt-update/saveprovinceGoodStudentInfo"})
	 	public String saveprovinceGoodStudentInfo(ModelMap model,HttpServletRequest request,String id,String flag){
	    	String resume = request.getParameter("resume");//个人简历
	    	String mainStory = request.getParameter("mainStory");  //主要事迹
	 		if(com.uws.core.util.StringUtils.hasText(id)){
	 			ProvinceGoodStudent provinceGoodStudent = provinceGoodStudentService.findProvinceGoodStudentById(id);
	 			provinceGoodStudent.setResume(resume);
	 			provinceGoodStudent.setMainStory(mainStory);
	 			if(StringUtils.hasText(flag) && flag.equals("1")){
	 				provinceGoodStudent.setFlag("1");//提交
	 			}else{
	 				provinceGoodStudent.setFlag("0");//保存
	 			}
	 			this.provinceGoodStudentService.update(provinceGoodStudent);
	 			model.addAttribute("provinceGoodStudent", provinceGoodStudent);
	 		}
	 		return "redirect:/job/provinceGoodStudent/opt-query/queryProvinceGoodStudentList.do";
	 		
	 	}
	     
	    
	    /**
	     * 
	    * @Title: ProvinceGoodStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description:省优秀毕业生统计页面
	    * @author pc  
	    * @date 2015-11-19 上午10:43:54
	     */
	    @RequestMapping("/job/countProvinceGoodStudent/opt-query/countProvinceGoodStudentList")
		public String countProvinceGoodStudentList(ModelMap model,HttpServletRequest request,HttpServletResponse response,ProvinceGoodStudent provinceGoodStudent){
			String statByWay = request.getParameter("statByWay");//统计范围
			String yearId=request.getParameter("schoolGoodStudent.schoolYear.id");
			int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
			if(DataUtil.isNull(statByWay)){
				statByWay = "byCollege";
			}
			Dic yearDic = new Dic();
			if(com.uws.core.util.StringUtils.hasText(yearId)){
				yearDic = dicService.getDic(yearId);
				model.addAttribute("yearDic", yearDic);
			}else if(yearId == null){
				//获取当前学年字典。
				yearDic = SchoolYearUtil.getYearDic();
				model.addAttribute("yearDic", yearDic);
			}
			List<Dic> years = this.dicUtil.getDicInfoList("YEAR");//学年
			List<BaseAcademyModel> colleges =  this.baseDataService.listBaseAcademy();//学院
			List<BaseMajorModel> majors = new ArrayList<BaseMajorModel>();
			List<BaseClassModel> classes = new ArrayList<BaseClassModel>();
			Map<String,String> statisticalWays = Constants.getStatisticalWays();
			model.addAttribute("years", years);
			model.addAttribute("statByWay", statByWay);
			model.addAttribute("colleges", colleges);
			model.addAttribute("nowSchoolYear",yearDic);
			model.addAttribute("statisticalWays", statisticalWays);
			model.addAttribute("provinceGoodStudent", provinceGoodStudent);
			if(DataUtil.isEquals("byCollege", statByWay)){
				List<ProvinceGoodStudentCollegeView> provinceGoodStudentCollegeList = this.provinceGoodStudentService.queryProvinceGoodStudentByCollege(provinceGoodStudent,Page.DEFAULT_PAGE_SIZE,pageNo,yearDic);
				model.addAttribute("provinceGoodStudentCollegeList", provinceGoodStudentCollegeList);
				return Constants.MENUKEY_JOB_COUNT_PROVINCE_GOOD_STUDENT +"countProvinceGoodStudentByCollege";
			}
			if(DataUtil.isEquals("byMajor", statByWay)){//按专业查询时只需要添加专业查询条件
				List<ProvinceGoodStudentMajorView> provinceGoodStudentmajorList = this.provinceGoodStudentService.queryProvinceGoodStudentByByMajor(provinceGoodStudent,yearDic);
				if(provinceGoodStudent!= null && provinceGoodStudent.getSchoolGoodStudent()!= null && provinceGoodStudent.getSchoolGoodStudent().getStudentId()!= null && provinceGoodStudent.getSchoolGoodStudent().getStudentId().getCollege()!=null &&
		    			com.uws.core.util.StringUtils.hasText(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getCollege().getId())){
					majors = compService.queryMajorByCollage(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getCollege().getId());
		    	}
				model.addAttribute("provinceGoodStudentmajorList", provinceGoodStudentmajorList);
				model.addAttribute("majors", majors);
				return Constants.MENUKEY_JOB_COUNT_PROVINCE_GOOD_STUDENT + "countProvinceGoodStudentByMajor";
			}
			if(DataUtil.isEquals("byClass", statByWay)){//按班级查询时需要添加专业和班级查询条件
				List<ProvinceGoodStudentClassView> provinceGoodStudentclassList = this.provinceGoodStudentService.queryProvinceGoodStudentByClass(provinceGoodStudent,yearDic);
				if(provinceGoodStudent!= null && provinceGoodStudent.getSchoolGoodStudent()!= null && provinceGoodStudent.getSchoolGoodStudent().getStudentId()!= null && provinceGoodStudent.getSchoolGoodStudent().getStudentId().getCollege()!=null &&
		    			    com.uws.core.util.StringUtils.hasText(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getCollege().getId())){
					    majors = compService.queryMajorByCollage(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getCollege().getId());
					if(provinceGoodStudent!= null && provinceGoodStudent.getSchoolGoodStudent()!= null && provinceGoodStudent.getSchoolGoodStudent().getStudentId()!= null && provinceGoodStudent.getSchoolGoodStudent().getStudentId().getMajor()!=null &&
			    			com.uws.core.util.StringUtils.hasText(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getMajor().getId())){
						classes = compService.queryClassByMajor(provinceGoodStudent.getSchoolGoodStudent().getStudentId().getMajor().getId());
			    	}
				}
				model.addAttribute("majors", majors);
				model.addAttribute("classes", classes);
				model.addAttribute("provinceGoodStudentclassList", provinceGoodStudentclassList);
				return Constants.MENUKEY_JOB_COUNT_PROVINCE_GOOD_STUDENT + "countProvinceGoodStudentByClass";
			}
			return Constants.MENUKEY_JOB_COUNT_PROVINCE_GOOD_STUDENT + "countProvinceGoodStudentByCollege";
		}
	    
	    /**
	     * 
	    * @Title: ProvinceGoodStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description:省优秀毕业生导出页面
	    * @author pc  
	    * @date 2015-11-23 上午10:43:10
	     */
	    @RequestMapping(value="/job/queryProvinceGoodStudent/nsm/exportProvinceGoodStudentView")
		public String exportProvinceGoodStudentList(ModelMap model,HttpServletRequest request){
			int exportSize=Integer.valueOf(request.getParameter("exportSize")).intValue();
			int pageTotalCount=Integer.valueOf(request.getParameter("pageTotalCount")).intValue();
			int maxNumber=0;
			if(pageTotalCount<exportSize){
				maxNumber=1;
			}else if(pageTotalCount % exportSize == 0){
				maxNumber=pageTotalCount / exportSize;
			}else{
				maxNumber=pageTotalCount / exportSize + 1;
			}
			model.addAttribute("exportSize",Integer.valueOf(exportSize));
			model.addAttribute("maxNumber",Integer.valueOf(maxNumber));
			//为了能将导入的数据效率高，判断每次导入数据500条
			if(maxNumber<500){
				model.addAttribute("isMore", "false");
			}else{
				model.addAttribute("isMore", "true");
			}
			 return Constants.MENUKEY_JOB_PROVINCE_GOOD_STUDENT+"/exportProvinceGoodStudentView";
		}
	    
	    
	    /**
	     * 
	    * @Title: ProvinceGoodStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description: 省优秀毕业生导出方法
	    * @author pc  
	    * @date 2015-11-23 上午10:43:31
	     */
	    @RequestMapping(value="/job/opt-export/exportProvinceGoodStudent")
	    public void exportProvinceGoodStudent(ModelMap model,HttpServletRequest request,ProvinceGoodStudent provinceGoodStudent,HttpServletResponse response){
			String exportPage=request.getParameter("studentPoQuery_exportPage");
			String exportSize=request.getParameter("studentPoQuery_exportSize");
			String yearId=request.getParameter("schoolGoodStudent.schoolYear.id");
			Dic yearDic = new Dic();
			if(com.uws.core.util.StringUtils.hasText(yearId)){
				yearDic = dicService.getDic(yearId);
				model.addAttribute("yearDic", yearDic);
			}else if(yearId == null){
				//获取当前学年字典。
				yearDic = SchoolYearUtil.getYearDic();
				model.addAttribute("yearDic", yearDic);
			}
			Page page = this.provinceGoodStudentService.queryPassProvinceGoodStudentList(Integer.parseInt(exportPage),Integer.parseInt(exportSize),provinceGoodStudent,yearDic);
			List<Map> listMap= new ArrayList<Map>();
			List<ProvinceGoodStudent> proList = (List<ProvinceGoodStudent>) page.getResult();
			//遍历导出的数据，并将数据放入map对象中
			for(ProvinceGoodStudent p:proList){
				Map<String, Object> newmap = new HashMap<String, Object>();
				newmap.put("collegeName",p.getSchoolGoodStudent()!=null?(p.getSchoolGoodStudent().getStudentId()!=null?(p.getSchoolGoodStudent().getStudentId().getCollege()!=null?p.getSchoolGoodStudent().getStudentId().getCollege().getName():""):""):"");	
				newmap.put("majorName",p.getSchoolGoodStudent()!=null?(p.getSchoolGoodStudent().getStudentId()!=null?(p.getSchoolGoodStudent().getStudentId().getMajor()!=null?p.getSchoolGoodStudent().getStudentId().getMajor().getMajorName():""):""):"");	
				newmap.put("className",p.getSchoolGoodStudent()!=null?(p.getSchoolGoodStudent().getStudentId()!=null?(p.getSchoolGoodStudent().getStudentId().getClassId()!=null?p.getSchoolGoodStudent().getStudentId().getClassId().getClassName():""):""):"");	
				newmap.put("name", p.getSchoolGoodStudent()!=null?(p.getSchoolGoodStudent().getStudentId()!=null?p.getSchoolGoodStudent().getStudentId().getName():""):"");
				newmap.put("gender", p.getSchoolGoodStudent()!=null?(p.getSchoolGoodStudent().getStudentId()!=null?(p.getSchoolGoodStudent().getStudentId().getGenderDic()!=null?p.getSchoolGoodStudent().getStudentId().getGenderDic().getName():""):""):"");
				newmap.put("studentType", p.getSchoolGoodStudent()!=null?(p.getSchoolGoodStudent().getStudentId()!=null?p.getSchoolGoodStudent().getStudentId().getStudentType():""):"");
				Dic sourceLand = dicUtil.getDicInfo("NATIVE",p.getSchoolGoodStudent().getStudentId().getSourceLand());
				newmap.put("sourceLand",sourceLand.getName());
				newmap.put("sourceLandCode", p.getSchoolGoodStudent()!=null?(p.getSchoolGoodStudent().getStudentId()!=null?p.getSchoolGoodStudent().getStudentId().getSourceLand():""):"");
				newmap.put("politicalDic", p.getSchoolGoodStudent()!=null?(p.getSchoolGoodStudent().getStudentId()!=null?(p.getSchoolGoodStudent().getStudentId().getPoliticalDic()!=null?p.getSchoolGoodStudent().getStudentId().getPoliticalDic().getName():""):""):"");
				newmap.put("certificateCode", p.getSchoolGoodStudent()!=null?(p.getSchoolGoodStudent().getStudentId()!=null?p.getSchoolGoodStudent().getStudentId().getCertificateCode():""):"");
                newmap.put("post",p.getSchoolGoodStudent()!=null?p.getSchoolGoodStudent().getPost():"");
                listMap.add(newmap);
			}
			
			try {
				HSSFWorkbook wb=this.excelService.dynamicExportData("export_provinceGoodStudent.xls", "exportProvinceGoodStudent", listMap);
				//添加总数列表 
				HSSFSheet sheet = wb.getSheetAt(0);
				HSSFRow headRow = null;
				headRow = sheet.createRow(0);
				String title = null;
				String yearName = null;
				//单元格样式
				HSSFCellStyle styleBold = (HSSFCellStyle) wb.createCellStyle();
				styleBold.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
				styleBold.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
				styleBold.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
				styleBold.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
				styleBold.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
				HSSFFont f = wb.createFont(); f.setFontHeightInPoints((short) 20);
				//字号
				f.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
				//加粗 
				styleBold.setFont(f); 
				//如果没有选择学年，默认导出当前学年
				if(com.uws.core.util.StringUtils.hasText(yearId)){
					Dic yearsDic = dicService.getDic(yearId);
					title=yearsDic.getName()+"学年"+"省优秀毕业生信息名单";
				}else{
					//获取当前学年字典。
					Calendar cal = Calendar.getInstance();
					int year = cal.get(Calendar.YEAR);
					Dic nowYearDic = dicUtil.getDicInfo("YEAR", String.valueOf(year));
					yearName = nowYearDic.getName();
					title=yearName+"学年"+"省优秀毕业生信息名单";
				}
				// 总结需要合并单元格的
				HSSFCell headCell = headRow.createCell(0);
				headCell.setCellValue(title);// 跨单元格显示的数据
				headCell.setCellType(HSSFCell.CELL_TYPE_STRING);
	            sheet.addMergedRegion(new CellRangeAddress(0,0, 0,9));
	            //设置合并单元格单元格的高度
	            sheet.getRow(0).setHeightInPoints(30);
	            headCell.setCellStyle(styleBold);
				String filename = "省优秀毕业生第"+exportPage+"页.xls";
				response.setContentType("application/x-excel");     
				response.setHeader("Content-disposition", "attachment;filename=" +new String (filename.getBytes("GBK"),"iso-8859-1"));
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

}
