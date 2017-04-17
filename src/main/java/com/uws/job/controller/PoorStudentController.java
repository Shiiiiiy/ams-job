package com.uws.job.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.DecimalFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uws.common.service.IBaseDataService;
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
import com.uws.domain.job.PoorStudent;
import com.uws.domain.job.PoorStudentClassView;
import com.uws.domain.job.PoorStudentCollegeView;
import com.uws.domain.job.PoorStudentMajorView;
import com.uws.domain.sponsor.DifficultStudentInfo;
import com.uws.job.service.IPoorStudentService;
import com.uws.job.util.Constants;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.IDicService;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.util.CheckUtils;
import com.uws.util.ProjectSessionUtils;

/**
 * 双困生管理 PoorStudentController
 * @author liuchen
 *
 */
@Controller
public class PoorStudentController extends BaseController
{   
	@Autowired
	private IPoorStudentService poorStudentService;
	@Autowired
	private IBaseDataService baseDataService;
	@Autowired
	private ICompService compService;
	@Autowired
	private IDicService dicService;
	@Autowired
	private IExcelService excelService;
	
	//数据字典工具类
    private DicUtil dicUtil = DicFactory.getDicUtil();
	// 日志
    private Logger log = new LoggerFactory(PoorStudentController.class);
    //附件工具类
  	private FileUtil fileUtil=FileFactory.getFileUtil();
  	// sessionUtil工具类
  	private SessionUtil sessionUtil = SessionFactory.getSession(Constants.MENUKEY_JOB_POORSTUDENT);
    
       /**
        * 
       * @Title: PoorStudentController.java 
       * @Package com.uws.job.controller 
       * @Description: TODO
       * @author pc  
       * @date 2015-11-5 下午4:51:23
        */
	    @RequestMapping("/job/poorStudent/opt-query/queryPoorStudentInfoList")
		public String listPoorStudentInfo(ModelMap model,HttpServletRequest request,PoorStudent poorStudent){
			log.info("双困生信息维护查询列表");
			String yearId=request.getParameter("difficultStudentInfo.schoolYear.id");
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
				poorStudent.setStrCollege(college);
				model.addAttribute("college", college);
			}
			int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
			Page page = this.poorStudentService.queryPoorStudentInfoList(pageNo,Page.DEFAULT_PAGE_SIZE,poorStudent,yearDic);
			//学院
	    	List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
	    	//专业
	    	List<BaseMajorModel> majorList = null;
	    	//班级
	    	List<BaseClassModel> classList = null;
	    	if(poorStudent!= null && poorStudent.getStrCollege()!=null  && com.uws.core.util.StringUtils.hasText(poorStudent.getStrCollege().getId())){
	    		majorList = compService.queryMajorByCollage(poorStudent.getStrCollege().getId());
	    	}
	    	if(poorStudent!= null && poorStudent.getStrMajor()!=null &&com.uws.core.util.StringUtils.hasText(poorStudent.getStrMajor().getId())){
	    		classList = compService.queryClassByMajor(poorStudent.getStrMajor().getId());
	    	}
	    	model.addAttribute("page", page);
	    	model.addAttribute("collegeList", collegeList);
			model.addAttribute("majorList", majorList);
			model.addAttribute("classList", classList);
	    	model.addAttribute("poorStudent", poorStudent);
	    	model.addAttribute("isList",dicUtil.getDicInfoList("Y&N"));
			model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
			model.addAttribute("statusList",dicUtil.getDicInfoList("POOR_STUDENT_STATUS"));
			model.addAttribute("difficultTypeList",dicUtil.getDicInfoList("DIFFICULT_CATEGORY"));
			return Constants.MENUKEY_JOB_POORSTUDENT+"/poorStudentList";
		}
	    
	    /**
	     * 
	     * @Title: PoorStudentController.java 
	     * @Package com.uws.job.controller 
	     * @Description:新增双困生（毕业的学生中选出来的）
	     * @author LiuChen 
	     * @date 2015-12-25 上午9:56:33
	     */
	    @RequestMapping({"/job/poorStudent/opt-add/addPoorStudent"})
		public String addPoorStudentInfo(ModelMap model,HttpServletRequest request){
			String id = request.getParameter("id");
			if(com.uws.core.util.StringUtils.hasText(id)){
				PoorStudent poorStudent = poorStudentService.getPoorStudentInfoById(id);
				model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(poorStudent.getId()));
				model.addAttribute("poorStudent", poorStudent);
			}else{
				model.addAttribute("poorStudent", new PoorStudent());
				model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(null));
			}
			model.addAttribute("isList",dicUtil.getDicInfoList("Y&N"));
			model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
			model.addAttribute("difficultTypeList",dicUtil.getDicInfoList("DIFFICULT_CATEGORY"));
			model.addAttribute("collegeId", ProjectSessionUtils.getCurrentTeacherOrgId(request));
			return Constants.MENUKEY_JOB_POORSTUDENT+"/addPoorStudent";
		}
	    
	    
	    /**
	     * 
	    * @Title: PoorStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description:新增双困生(来源是资助困难生那边过来的)
	    * @author pc  
	    * @date 2015-11-17 下午2:14:21
	     */
	    @RequestMapping({"/job/poorStudent/opt-add/editPoorStudentInfo"})
		public String editPoorStudentInfo(ModelMap model,HttpServletRequest request){
			String difficultId = request.getParameter("difficultId");
			String id = request.getParameter("id");
			if(com.uws.core.util.StringUtils.hasText(difficultId)){
				DifficultStudentInfo difficultStudentInfo = poorStudentService.getDifficultStudentInfoById(difficultId);
				model.addAttribute("difficultStudentInfo", difficultStudentInfo);
			}else{
				model.addAttribute("difficultStudentInfo", new DifficultStudentInfo());
			}
			if(com.uws.core.util.StringUtils.hasText(id)){
				PoorStudent poorStudent = poorStudentService.getPoorStudentInfoById(id);
				model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(poorStudent.getId()));
				model.addAttribute("poorStudent", poorStudent);
			}else{
				model.addAttribute("poorStudent", new PoorStudent());
				model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(null));
			}
			model.addAttribute("isList",dicUtil.getDicInfoList("Y&N"));
			model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
			model.addAttribute("difficultTypeList",dicUtil.getDicInfoList("DIFFICULT_CATEGORY"));
			if(com.uws.core.util.StringUtils.hasText(difficultId)){
				return Constants.MENUKEY_JOB_POORSTUDENT+"/poorStudentEdit";
			}else{
				return Constants.MENUKEY_JOB_POORSTUDENT+"/addPoorStudent";
			}
		}
	    
	    /**
	     * 
	    * @Title: PoorStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description: 保存双困生，修改
	    * @author pc  
	    * @date 2015-11-17 下午2:14:39
	     */
	    @RequestMapping(value = {"/job/poorStudent/opt-save/savePoorStudentInfo","/job/poorStudent/opt-update/updatePoorStudentInfo"})
		public String savePoorStudentInfo(ModelMap model, HttpServletRequest request,PoorStudent poorStudent,String[] fileId) {
	    	String flag = request.getParameter("flag");
	    	//判断是保存还是提交
	    	if(StringUtils.hasText(flag) && flag.equals("1")){
	    		poorStudent.setStatus(Constants.STATUS_SUBMIT_DIC);
	    	}else{
	    		poorStudent.setStatus(Constants.STATUS_SAVE_DIC);
	    	}
	    	if(com.uws.core.util.StringUtils.hasText(poorStudent.getId())){
				//就业信息修改
	    		PoorStudent poorStudentPo = poorStudentService.getPoorStudentInfoById(poorStudent.getId());
				BeanUtils.copyProperties(poorStudent,poorStudentPo,new String[]{"difficultStudentInfo","createTime"});
				this.poorStudentService.updatePoorInfo(poorStudentPo,fileId);
				log.info("双困生信息修改成功!");
			}else{
				this.poorStudentService.savePoorInfo(poorStudent,fileId);
				log.info("双困生信息新增成功!");
			}
	    	 return "redirect:/job/poorStudent/opt-query/queryPoorStudentInfoList.do";
		}
	    
	    /**
	     * 
	     * @Title: PoorStudentController.java 
	     * @Package com.uws.job.controller 
	     * @Description: 验证是否已添加过学生
	     * @author LiuChen 
	     * @date 2016-1-6 下午12:57:03
	     */
	    @RequestMapping(value = {"/job/poorStudent/opt-query/studentCheck"},produces = { "text/plain;charset=UTF-8"})
	    @ResponseBody
	    public String checkCodeRepeat(@RequestParam String id, @RequestParam String studentId,@RequestParam String schoolYear)
	    {   
		     if (this.poorStudentService.isExistStudent(id,studentId,schoolYear)) {
		    	 return "";
		     }
		     return "true";
	    }
	    
	    
	    /**
	     * 
	    * @Title: PoorStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description:批量新增
	    * @author pc  
	    * @date 2015-11-17 下午2:15:04
	     */
	    @RequestMapping(value = {"/job/poorStudent/nsm/savePoorStudents" },produces = { "text/plain;charset=UTF-8" })
		@ResponseBody
		public String savePoorStudents(ModelMap model, HttpServletRequest request,String ids,String difficultType,String reason) {
			if(StringUtils.hasText(ids)){
				String[] pids = ids.split(",");
				for (String id : pids)
                {   
					PoorStudent poorStudent = new PoorStudent();
					DifficultStudentInfo difficultStudentInfo = poorStudentService.getDifficultStudentInfoById(id);
					difficultStudentInfo.setId(id);
					poorStudent.setDifficultStudentInfo(difficultStudentInfo);
					poorStudent.setStudentInfo(difficultStudentInfo.getStudent());
					poorStudent.setSchoolYear(difficultStudentInfo.getSchoolYear());
					poorStudent.setStatus(Constants.STATUS_SUBMIT_DIC);
					if(StringUtils.hasText(difficultType)){
						Dic diffDic = dicService.getDic(difficultType);
						poorStudent.setDifficultType(diffDic);
                     }
					poorStudent.setReason(reason);
	                this.poorStudentService.savePoorStudentInfo(poorStudent);
                }
			}
			   return "success";
		}
	    
	    /**
	     * 
	    * @Title: PoorStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description:取消申请---删除
	    * @author pc  
	    * @date 2015-11-24 下午2:07:19
	     */
	    @ResponseBody
	    @RequestMapping(value = {"/job/poorStudent/opt-del/deletePoorStudent" },produces = { "text/plain;charset=UTF-8" })
		public String deleteDifficultStudentInfo(ModelMap model, HttpServletRequest request,String id) {
	    	PoorStudent poorStudentPo = poorStudentService.getPoorStudentInfoById(id);
			this.poorStudentService.deleteInfo(poorStudentPo);
			log.info("删除操作成功！");
			return "success";
		}
	    
	    
	    /**
	     * 
	    * @Title: PoorStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description: 双困生查看页面
	    * @author pc  
	    * @date 2015-11-17 下午2:15:30
	     */
	    @RequestMapping({"/job/approvePoorStudent/opt-view/viewPoorStudent"})
		public String viewPoorStudent(ModelMap model,HttpServletRequest request){
			String id = request.getParameter("id");
			if(com.uws.core.util.StringUtils.hasText(id)){
				PoorStudent poorStudent = poorStudentService.getPoorStudentInfoById(id);
				model.addAttribute("poorStudent", poorStudent);
			}else{
				model.addAttribute("poorStudent", new PoorStudent());
			}
			model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(id));//文件查看
			model.addAttribute("isList",dicUtil.getDicInfoList("Y&N"));
			model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
			model.addAttribute("difficultTypeList",dicUtil.getDicInfoList("DIFFICULT_CATEGORY"));
			return Constants.MENUKEY_JOB_POORSTUDENT+"/poorStudentView";
		}
	    
	    
	    /**
	     * 
	    * @Title: PoorStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description: 双困生审核列表
	    * @author pc  
	    * @date 2015-11-17 下午2:15:19
	     */
	    @RequestMapping("/job/approvePoorStudent/opt-query/approvePoorStudentInfoList")
	    public String listApprovePoorStudentInfo(ModelMap model,HttpServletRequest request,PoorStudent poorStudent){
			log.info("双困生审核列表信息");
			int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
			String yearId=request.getParameter("difficultStudentInfo.schoolYear.id");
			Dic yearDic = new Dic();
			if(com.uws.core.util.StringUtils.hasText(yearId)){
				yearDic = dicService.getDic(yearId);
				model.addAttribute("yearDic", yearDic);
			}else if(yearId == null){
				//获取当前学年字典。
				yearDic = SchoolYearUtil.getYearDic();
				model.addAttribute("yearDic", yearDic);
			}
			Page page = this.poorStudentService.queryApprovePoorStudentInfoList(pageNo,Page.DEFAULT_PAGE_SIZE,poorStudent,yearDic);
			//学院
	    	List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
	    	//专业
	    	List<BaseMajorModel> majorList = null;
	    	//班级
	    	List<BaseClassModel> classList = null;
	    	if(poorStudent!= null && poorStudent.getDifficultStudentInfo()!= null && poorStudent.getDifficultStudentInfo().getStudent()!= null && poorStudent.getDifficultStudentInfo().getStudent().getCollege()!=null &&
	    			com.uws.core.util.StringUtils.hasText(poorStudent.getDifficultStudentInfo().getStudent().getCollege().getId())){
	    		majorList = compService.queryMajorByCollage(poorStudent.getDifficultStudentInfo().getStudent().getCollege().getId());
	    	}
	    	if(poorStudent!= null && poorStudent.getDifficultStudentInfo()!= null && poorStudent.getDifficultStudentInfo().getStudent()!= null && poorStudent.getDifficultStudentInfo().getStudent().getMajor()!=null &&
	    			com.uws.core.util.StringUtils.hasText(poorStudent.getDifficultStudentInfo().getStudent().getMajor().getId())){
	    		classList = compService.queryClassByMajor(poorStudent.getDifficultStudentInfo().getStudent().getMajor().getId());
	    	}
	    	model.addAttribute("page", page);
	    	model.addAttribute("collegeList", collegeList);
			model.addAttribute("majorList", majorList);
			model.addAttribute("classList", classList);
	    	model.addAttribute("poorStudent", poorStudent);
	    	model.addAttribute("isList",dicUtil.getDicInfoList("Y&N"));
			model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
			model.addAttribute("processStatusMap", Constants.getApproveProcessStatus());
			model.addAttribute("difficultTypeList",dicUtil.getDicInfoList("DIFFICULT_CATEGORY"));
			return Constants.MENUKEY_JOB_POORSTUDENT+"/approvePoorStudentList";
		}
	    
	    /**
	     * 
	    * @Title: PoorStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description: 双困生审核页面
	    * @author pc  
	    * @date 2015-11-17 下午2:15:30
	     */
	    @RequestMapping({"/job/approvePoorStudent/opt-approve/approvePoorStudent"})
		public String approvePoorStudent(ModelMap model,HttpServletRequest request){
			String difficultId = request.getParameter("difficultId");
			String id = request.getParameter("id");
			//查询困难生
			if(com.uws.core.util.StringUtils.hasText(difficultId)){
				DifficultStudentInfo difficultStudentInfo = poorStudentService.getDifficultStudentInfoById(difficultId);
				model.addAttribute("difficultStudentInfo", difficultStudentInfo);
			}else{
				model.addAttribute("difficultStudentInfo", new DifficultStudentInfo());
			}
			//根据id查询双困生
			if(com.uws.core.util.StringUtils.hasText(id)){
				PoorStudent poorStudent = poorStudentService.getPoorStudentInfoById(id);
				model.addAttribute("poorStudent", poorStudent);
			}else{
				model.addAttribute("poorStudent", new PoorStudent());
			}
			model.addAttribute("isList",dicUtil.getDicInfoList("Y&N"));
			model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
			model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(id));//文件查看
			model.addAttribute("difficultTypeList",dicUtil.getDicInfoList("DIFFICULT_CATEGORY"));
			return Constants.MENUKEY_JOB_POORSTUDENT+"/approvePoorStudentEdit";
		}
	    
	    /**
	     * 
	    * @Title: PoorStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description:保存审核信息
	    * @author pc  
	    * @date 2015-11-17 下午2:15:51
	     */
	    @RequestMapping(value = {"/job/approvePoorStudent/opt-save/saveApprovePoorStudent"})
		public String saveApprovePoorStudent(ModelMap model, HttpServletRequest request,String id,String flag) {
	    	String approveReason = request.getParameter("approveReason");
	    	if(com.uws.core.util.StringUtils.hasText(id)){
	    		//就业信息修改
	    		PoorStudent poorStudentPo = poorStudentService.getPoorStudentInfoById(id);
	    	if(StringUtils.hasText(flag) && flag.equals("1")){
	    		poorStudentPo.setStatus(Constants.STATUS_PASS_DIC);
	    	}else{
	    		poorStudentPo.setStatus(Constants.STATUS_UNPASS_DIC);
	    	}
	    	poorStudentPo.setApproveReason(approveReason);
	    	this.poorStudentService.updatePoorStudentInfo(poorStudentPo);
	    	log.info("双困生审核成功!");
		}
	    	return "redirect:/job/approvePoorStudent/opt-query/approvePoorStudentInfoList.do";
     }
	    
	    /**
	     * 
	    * @Title: PoorStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description: 批量审批页面
	    * @author pc  
	    * @date 2015-11-16 下午6:19:54
	     */
	    @RequestMapping({"/job/approvePoorStudent/opt-query/checkedApproveList"})
		public String checkedApproveList(ModelMap model,String selectedBox,HttpServletRequest request) {
			
			List<PoorStudent> stuList = new ArrayList<PoorStudent>();
			if(selectedBox !=null && selectedBox.indexOf(",") > -1) {
				String[] checkedIds = selectedBox.split(",");
				for(String s : checkedIds) {
					PoorStudent poorStudentPo = poorStudentService.getPoorStudentInfoById(s);
					if(DataUtil.isNotNull(poorStudentPo)) {
						stuList.add(poorStudentPo);
					}
				}
			}else if(DataUtil.isNotNull(selectedBox)){
				PoorStudent poorStudentPo = poorStudentService.getPoorStudentInfoById(selectedBox);
				if(DataUtil.isNotNull(poorStudentPo)) {
					stuList.add(poorStudentPo);
				}
			}
			model.addAttribute("stuList", stuList);
		    model.addAttribute("objectIds", selectedBox);
		    return Constants.MENUKEY_JOB_POORSTUDENT+"/poorStudentMulApprove";
		}    
	    
	    /**
	     * 
	    * @Title: PoorStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description:批量保存
	    * @author pc  
	    * @date 2015-11-16 下午6:49:30
	     */
	    @RequestMapping({"/job/approvePoorStudent/opt-save/saveApprovePoorStudents"})
		public String saveApprovePoorStudents(ModelMap model,HttpServletRequest request,String objectIds,String flag) {
	    	String approveReason = request.getParameter("approveReason");
	    	if(StringUtils.hasText(objectIds)){
	    		String[] ids = objectIds.split(",");
	    		for (String id : ids)
                {
	    			PoorStudent poorStudentPo = poorStudentService.getPoorStudentInfoById(id);
	    			//就业信息修改
	    			if(StringUtils.hasText(flag) && flag.equals("1")){
	    				poorStudentPo.setStatus(Constants.STATUS_PASS_DIC);
	    			}else{
	    				poorStudentPo.setStatus(Constants.STATUS_UNPASS_DIC);
	    			}
	    			poorStudentPo.setApproveReason(approveReason);
	    			this.poorStudentService.updatePoorStudentInfo(poorStudentPo);
                }
		 }
	    	return "redirect:/job/approvePoorStudent/opt-query/approvePoorStudentInfoList.do";
	  }   
	    
	    
	    /**
	        * 
	       * @Title: PoorStudentController.java 
	       * @Package com.uws.job.controller 
	       * @Description: TODO
	       * @author pc  
	       * @date 2015-11-5 下午4:51:23
	        */
		    @RequestMapping("/job/queryPoorStudent/opt-query/queryPoorStudentInfoList")
			public String queryPoorStudentInfoList(ModelMap model,HttpServletRequest request,PoorStudent poorStudent){
				log.info("双困生信息申请通过查询列表");
				String currentStudentId = null;
				String yearId=request.getParameter("difficultStudentInfo.schoolYear.id");
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
					poorStudent.setStrCollege(college);
					model.addAttribute("college", college);
				}
				int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
				Page page = this.poorStudentService.queryPassPoorStudentInfoList(pageNo,Page.DEFAULT_PAGE_SIZE,poorStudent,yearDic,currentStudentId);
				//学院
		    	List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
		    	//专业
		    	List<BaseMajorModel> majorList = null;
		    	//班级
		    	List<BaseClassModel> classList = null;
		    	if(poorStudent!= null && poorStudent.getStrCollege()!=null  && com.uws.core.util.StringUtils.hasText(poorStudent.getStrCollege().getId())){
		    		majorList = compService.queryMajorByCollage(poorStudent.getStrCollege().getId());
		    	}
		    	if(poorStudent!= null && poorStudent.getStrMajor()!=null &&com.uws.core.util.StringUtils.hasText(poorStudent.getStrMajor().getId())){
		    		classList = compService.queryClassByMajor(poorStudent.getStrMajor().getId());
		    	}
		    	model.addAttribute("page", page);
		    	model.addAttribute("collegeList", collegeList);
				model.addAttribute("majorList", majorList);
				model.addAttribute("classList", classList);
		    	model.addAttribute("poorStudent", poorStudent);
		    	model.addAttribute("isList",dicUtil.getDicInfoList("Y&N"));
				model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
				model.addAttribute("statusList",dicUtil.getDicInfoList("POOR_STUDENT_STATUS"));
				model.addAttribute("difficultTypeList",dicUtil.getDicInfoList("DIFFICULT_CATEGORY"));
				return Constants.MENUKEY_JOB_POORSTUDENT+"/selectPoorStudentList";
			}
	   
	    /**
	     * 
	    * @Title: PoorStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description:统计双困生列表
	    * @author pc  
	    * @date 2015-12-2 下午4:36:59
	     */
	    @RequestMapping("/job/countPoorStudent/opt-query/countPoorStudentList")
		public String countProvinceGoodStudentList(ModelMap model,HttpServletRequest request,PoorStudent poorStudent){
			String statByWay = request.getParameter("statByWay");//统计范围
			String yearId=request.getParameter("difficultStudentInfo.schoolYear.id");
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
			model.addAttribute("poorStudent", poorStudent);
			if(DataUtil.isEquals("byCollege", statByWay)){
				List<PoorStudentCollegeView> poorStudentcollegeList = this.poorStudentService.queryPoorStudentByCollege(poorStudent,yearDic);
				model.addAttribute("poorStudentcollegeList", poorStudentcollegeList);
				return Constants.MENUKEY_JOB_COUNT_POORSTUDENT +"countPoorStudentByCollege";
			}
			if(DataUtil.isEquals("byMajor", statByWay)){//按专业查询时只需要添加专业查询条件
				List<PoorStudentMajorView> poorStudentmajorList = this.poorStudentService.queryPoorStudentByByMajor(poorStudent,yearDic);
				if(poorStudent!= null && poorStudent.getDifficultStudentInfo()!= null && poorStudent.getDifficultStudentInfo().getStudent()!= null && poorStudent.getDifficultStudentInfo().getStudent().getCollege()!=null &&
		    			com.uws.core.util.StringUtils.hasText(poorStudent.getDifficultStudentInfo().getStudent().getCollege().getId())){
					majors = compService.queryMajorByCollage(poorStudent.getDifficultStudentInfo().getStudent().getCollege().getId());
		    	}
				model.addAttribute("majors", majors);
				model.addAttribute("poorStudentmajorList", poorStudentmajorList);
				return Constants.MENUKEY_JOB_COUNT_POORSTUDENT + "countPoorStudentByMajor";
			}
			if(DataUtil.isEquals("byClass", statByWay)){//按班级查询时需要添加专业和班级查询条件
				List<PoorStudentClassView> poorStudentclassList = this.poorStudentService.queryPoorStudentByClass(poorStudent,yearDic);
				if(poorStudent!= null && poorStudent.getDifficultStudentInfo()!= null && poorStudent.getDifficultStudentInfo().getStudent()!= null && poorStudent.getDifficultStudentInfo().getStudent().getCollege()!=null &&
		    			com.uws.core.util.StringUtils.hasText(poorStudent.getDifficultStudentInfo().getStudent().getCollege().getId())){
					majors = compService.queryMajorByCollage(poorStudent.getDifficultStudentInfo().getStudent().getCollege().getId());
					if(poorStudent!= null && poorStudent.getDifficultStudentInfo()!= null && poorStudent.getDifficultStudentInfo().getStudent()!= null && poorStudent.getDifficultStudentInfo().getStudent().getMajor()!=null &&
			    			com.uws.core.util.StringUtils.hasText(poorStudent.getDifficultStudentInfo().getStudent().getMajor().getId())){
						classes = compService.queryClassByMajor(poorStudent.getDifficultStudentInfo().getStudent().getMajor().getId());
			    	}
				}
				model.addAttribute("majors", majors);
				model.addAttribute("classes", classes);
				model.addAttribute("poorStudentclassList", poorStudentclassList);
				return Constants.MENUKEY_JOB_COUNT_POORSTUDENT + "countPoorStudentByClass";
			}
			return Constants.MENUKEY_JOB_COUNT_POORSTUDENT + "countPoorStudentByCollege";
		}
	    
	    /**
	     * 
	    * @Title: PoorStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description: 双困生导出页面
	    * @author pc  
	    * @date 2015-11-25 下午5:47:13
	     */
	    @RequestMapping(value="/job/queryPoorStudent/nsm/exportPoorStudentView")
		public String exportPoorStudentList(ModelMap model,HttpServletRequest request){
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
			 return Constants.MENUKEY_JOB_POORSTUDENT+"/exportPoorStudentView";
		}
	    
	    
	    
	    /**
	     * 
	    * @Title: PoorStudentController.java 
	    * @Package com.uws.job.controller 
	    * @Description: 双困生导出方法
	    * @author pc  
	    * @date 2015-11-25 下午5:49:39
	     */
	    @RequestMapping(value="/job/opt-export/exportPoorStudent")
	    public void exportPoorStudent(ModelMap model,HttpServletRequest request,PoorStudent poorStudent,HttpServletResponse response){
			String exportPage=request.getParameter("studentPoQuery_exportPage");
			String exportSize=request.getParameter("studentPoQuery_exportSize");
			String yearId=request.getParameter("difficultStudentInfo.schoolYear.id");
			Dic yearDic = new Dic();
			if(com.uws.core.util.StringUtils.hasText(yearId)){
				yearDic = dicService.getDic(yearId);
				model.addAttribute("yearDic", yearDic);
			}else if(yearId == null){
				//获取当前学年字典。
				yearDic = SchoolYearUtil.getYearDic();
				model.addAttribute("yearDic", yearDic);
			}
			Page page = this.poorStudentService.queryPassPoorStudentInfoList(Integer.parseInt(exportPage),Integer.parseInt(exportSize),poorStudent,yearDic,null);
			List<Map> listMap= new ArrayList<Map>();
			List<PoorStudent> poorList = (List<PoorStudent>) page.getResult();
			//遍历导出的数据，并将数据放入map对象中
			for(PoorStudent p:poorList){
				Map<String, Object> newmap = new HashMap<String, Object>();
				newmap.put("name", p.getDifficultStudentInfo()!=null?(p.getDifficultStudentInfo().getStudent()!=null?p.getDifficultStudentInfo().getStudent().getName():""):"");
				newmap.put("diffLevel", p.getDifficultStudentInfo()!=null?(p.getDifficultStudentInfo().getDifficultLevel()!=null?p.getDifficultStudentInfo().getDifficultLevel().getName():""):"");
				newmap.put("schoolYear", p.getDifficultStudentInfo()!=null?(p.getDifficultStudentInfo().getSchoolYear()!=null?p.getDifficultStudentInfo().getSchoolYear().getName():""):"");
				newmap.put("stuNumber", p.getDifficultStudentInfo()!=null?(p.getDifficultStudentInfo().getStudent()!=null?p.getDifficultStudentInfo().getStudent().getStuNumber():""):"");
				newmap.put("collegeName",p.getDifficultStudentInfo()!=null?(p.getDifficultStudentInfo().getStudent()!=null?(p.getDifficultStudentInfo().getStudent().getCollege()!=null?p.getDifficultStudentInfo().getStudent().getCollege().getName():""):""):"");	
				newmap.put("majorName",p.getDifficultStudentInfo()!=null?(p.getDifficultStudentInfo().getStudent()!=null?(p.getDifficultStudentInfo().getStudent().getMajor()!=null?p.getDifficultStudentInfo().getStudent().getMajor().getMajorName():""):""):"");	
				newmap.put("className",p.getDifficultStudentInfo()!=null?(p.getDifficultStudentInfo().getStudent()!=null?(p.getDifficultStudentInfo().getStudent().getClassId()!=null?p.getDifficultStudentInfo().getStudent().getClassId().getClassName():""):""):"");
				newmap.put("difficultType",p.getDifficultType()!=null?p.getDifficultType().getName():"");
				newmap.put("reason",p.getReason());
				listMap.add(newmap);
			}
			
			try {
				HSSFWorkbook wb=this.excelService.dynamicExportData("export_poorStudent.xls", "exportPoorStudent", listMap);
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
					title=yearsDic.getName()+"学年"+"双困生信息名单";
				}else{
					//获取当前学年字典。
					Calendar cal = Calendar.getInstance();
					int year = cal.get(Calendar.YEAR);
					Dic nowYearDic = dicUtil.getDicInfo("YEAR", String.valueOf(year));
					yearName = nowYearDic.getName();
					title=yearName+"学年"+"双困生信息名单";
				}
				// 总结需要合并单元格的
				HSSFCell headCell = headRow.createCell(0);
				headCell.setCellValue(title);// 跨单元格显示的数据
				headCell.setCellType(HSSFCell.CELL_TYPE_STRING);
	            sheet.addMergedRegion(new CellRangeAddress(0,0, 0,8));
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
	    
	    
	    @RequestMapping(value="/job/countPoorStudent/nsm/exportCountPoorStudent")
		public String exportCountPoorStudentList(ModelMap model,HttpServletRequest request){
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
			 return Constants.MENUKEY_JOB_POORSTUDENT+"/exportPoorStudentView";
		}
	    
	    
	    /**
	     * 
	     * @Title: PoorStudentController.java 
	     * @Package com.uws.job.controller 
	     * @Description:按学院，专业，班级统计结果。
	     * @author LiuChen 
	     * @date 2016-1-6 上午10:55:03
	     */
	    @RequestMapping(value="/job/opt-export/exportCountPoorStudentList")
		public void exportPoorStudentDate(ModelMap model,HttpServletRequest request,PoorStudent poorStudent,HttpServletResponse response){
			//String exportSize=request.getParameter("achievePoQuery_exportSize");
			//String exportPage=request.getParameter("achievePoQuery_exportPage");
			String statByWay = request.getParameter("statByWay");//统计范围
			String yearId=request.getParameter("difficultStudentInfo.schoolYear.id");
			DecimalFormat decformat = new java.text.DecimalFormat("0.00");//精确到2位有效小数
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
			List<Map> listMap= new ArrayList<Map>();
			//excel求和列表信息
			int sumGraduationTotal = 0;//毕业总人数
			int sumPassTotal = 0;  double passTotalDouble = 0; //双困生审核通过人数
			int sumApplyTotal = 0; double applyTotalDouble = 0;   //双困生申请总人数
			String sumProportion = "";   //总比例
			int sumJob = 0;
			int sumFamily = 0;
			int sumJobFamily = 0;
			int sumDisability = 0;
			int sumJobDisability = 0;
			int sumFamilyDisability = 0;
			int sumJobFamilyDisability = 0;
			int totalGraduationTotal = 0;//毕业总人数
			double totalApplyTotal = 0;
			double totalPassTotal = 0;
			int totalPassTotalInt = 0;
			int totalJob = 0;
			int totalFamily = 0;
			int totalJobFamily = 0;
			int totalDisability = 0;
			int totalJobDisability = 0;
			int totalFamilyDisability = 0;
			int totalJobFamilyDisability = 0;
			//导出类封装
			HSSFWorkbook wb = new HSSFWorkbook(); 
			try {
				//按学院导出
				if(DataUtil.isEquals("byCollege", statByWay)){
					List<PoorStudentCollegeView> collegeList = this.poorStudentService.queryPoorStudentByCollege(poorStudent,yearDic);
					for (int i = 0; i < collegeList.size(); i++)
					{   
						PoorStudentCollegeView pc = collegeList.get(i);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("index",i+1);
						map.put("schoolYear",pc.getSchoolYear()!=null?pc.getSchoolYear().getName():"");
						map.put("collegeName",pc.getCollege()!=null?pc.getCollege().getName():"");
						map.put("graduation_total",pc.getGraduation_total());//毕业生总人数
						map.put("approvePassNum",pc.getApprovePassNum());//毕业生总人数
						if(pc.getTotal()!=0)
						{   
							passTotalDouble = pc.getApprovePassNum();
							applyTotalDouble = pc.getTotal();
							map.put("proportion",decformat.format(passTotalDouble*100/applyTotalDouble)+"%");//双困生比例
						}else{
							map.put("proportion","0.00%");//双困生比例
						}
						map.put("job",pc.getJob());
						map.put("family",pc.getFamily());
						map.put("job_family",pc.getJob_family());
						map.put("disability",pc.getDisability());
						map.put("job_disability",pc.getJob_disability());
						map.put("family_disability",pc.getFamily_disability());
						map.put("job_family_disability",pc.getJob_family_disability());
						listMap.add(map);
						sumGraduationTotal = (int)(sumGraduationTotal+pc.getGraduation_total());
						sumApplyTotal =(int)(sumApplyTotal+pc.getTotal());
						sumJob = (int)(sumJob +pc.getJob());
						sumFamily = (int)(sumFamily +pc.getFamily());
						sumJobFamily = (int)(sumJobFamily +pc.getJob_family());
						sumDisability = (int)(sumDisability +pc.getDisability());
						sumJobDisability = (int)(sumJobDisability +pc.getJob_disability());
						sumFamilyDisability = (int)(sumFamilyDisability +pc.getFamily_disability());
						sumJobFamilyDisability = (int)(sumJobFamilyDisability +pc.getJob_family_disability());
						sumPassTotal =(int)(sumPassTotal+pc.getApprovePassNum());
					}
					wb=this.excelService.exportData("export_poorStudent_college.xls","exportPoorStudentByCollege",listMap);
					//添加总数列表 
					HSSFSheet sheet = wb.getSheetAt(0);
					int rowNum = sheet.getLastRowNum();
					HSSFRow row = sheet.createRow(rowNum+1);
					//单元格样式
					HSSFCellStyle styleBold = (HSSFCellStyle) wb.createCellStyle();
					styleBold.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
					styleBold.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
					styleBold.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
					styleBold.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
					styleBold.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
					
					// 总结需要合并单元格的地方
					HSSFCell cell = row.createCell(0);
					cell.setCellValue("合计"); // 跨单元格显示的数据
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);  
					sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(),row.getRowNum(), 0, 2));
					cell.setCellStyle(styleBold);
					
					totalGraduationTotal =totalGraduationTotal + sumGraduationTotal;
					totalPassTotal =totalPassTotal+sumPassTotal;
					totalPassTotalInt =(totalPassTotalInt + sumPassTotal);
					totalApplyTotal = totalApplyTotal+sumApplyTotal;
					sumProportion =decformat.format(totalPassTotal*100/totalApplyTotal)+"%";
					totalJob = totalJob+sumJob;
					totalFamily = totalFamily+sumFamily;
					totalJobFamily = totalJobFamily+sumJobFamily;
					totalDisability = totalDisability+sumDisability;
					totalJobDisability = totalJobDisability+sumJobDisability;
					totalFamilyDisability = totalFamilyDisability+sumFamilyDisability;
					totalJobFamilyDisability = totalJobFamilyDisability+sumJobFamilyDisability;
					// 每列合计需要调用的方法
					generateCountCell(wb,totalGraduationTotal,row,3,styleBold);
					generateCountCell(wb,totalPassTotalInt,row,4,styleBold);
					generateCountCell(wb,sumProportion,row,5,styleBold);
					generateCountCell(wb,totalJob,row,6,styleBold);
					generateCountCell(wb,totalFamily,row,7,styleBold);
					generateCountCell(wb,totalJobFamily,row,8,styleBold);
					generateCountCell(wb,totalDisability,row,9,styleBold);
					generateCountCell(wb,totalJobDisability,row,10,styleBold);
					generateCountCell(wb,totalFamilyDisability,row,11,styleBold);
					generateCountCell(wb,totalJobFamilyDisability,row,12,styleBold);
				}
				
				if(DataUtil.isEquals("byMajor", statByWay)){//按专业查询时只需要添加专业查询条件
					List<PoorStudentMajorView> majorList = this.poorStudentService.queryPoorStudentByByMajor(poorStudent,yearDic);
					for (int i = 0; i < majorList.size(); i++)
					{   
						PoorStudentMajorView pc = majorList.get(i);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("index",i+1);
						map.put("schoolYear",pc.getSchoolYear()!=null?pc.getSchoolYear().getName():"");
						map.put("collegeName",pc.getMajor()!=null?(pc.getMajor().getCollage()!=null?pc.getMajor().getCollage().getName():""):"");
						map.put("majorName",pc.getMajor()!=null?pc.getMajor().getMajorName():"");
						map.put("graduation_total",pc.getGraduation_total());//毕业生总人数
						map.put("approvePassNum",pc.getApprovePassNum());//毕业生总人数
						if(pc.getTotal()!=0)
						{
							passTotalDouble = pc.getApprovePassNum();
							applyTotalDouble = pc.getTotal();
							map.put("proportion",decformat.format(passTotalDouble*100/applyTotalDouble)+"%");//双困生比例
						}else{
							map.put("proportion","0.00%");//双困生比例
						}
						map.put("job",pc.getJob());
						map.put("family",pc.getFamily());
						map.put("job_family",pc.getJob_family());
						map.put("disability",pc.getDisability());
						map.put("job_disability",pc.getJob_disability());
						map.put("family_disability",pc.getFamily_disability());
						map.put("job_family_disability",pc.getJob_family_disability());
						listMap.add(map);
						sumGraduationTotal = (int)(sumGraduationTotal+pc.getGraduation_total());
						sumApplyTotal =(int)(sumApplyTotal+pc.getTotal());
						sumJob = (int)(sumJob +pc.getJob());
						sumFamily = (int)(sumFamily +pc.getFamily());
						sumJobFamily = (int)(sumJobFamily +pc.getJob_family());
						sumDisability = (int)(sumDisability +pc.getDisability());
						sumJobDisability = (int)(sumJobDisability +pc.getJob_disability());
						sumFamilyDisability = (int)(sumFamilyDisability +pc.getFamily_disability());
						sumJobFamilyDisability = (int)(sumJobFamilyDisability +pc.getJob_family_disability());
						sumPassTotal =(int)(sumPassTotal+pc.getApprovePassNum());
					}
					wb=this.excelService.exportData("export_poorStudent_major.xls","exportPoorStudentByMajor",listMap);
					//添加总数列表 
					HSSFSheet sheet = wb.getSheetAt(0);
					int rowNum = sheet.getLastRowNum();
					HSSFRow row = sheet.createRow(rowNum+1);
					
					//单元格样式
					HSSFCellStyle styleBold = (HSSFCellStyle) wb.createCellStyle();
					styleBold.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
					styleBold.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
					styleBold.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
					styleBold.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
					styleBold.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
					
					// 总结需要合并单元格的地方
					HSSFCell cell = row.createCell(0);
					cell.setCellValue("合计"); // 跨单元格显示的数据
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);  
					sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(),row.getRowNum(), 0, 3));
					cell.setCellStyle(styleBold);
					
					totalGraduationTotal =totalGraduationTotal + sumGraduationTotal;
					totalPassTotal =totalPassTotal+sumPassTotal;
					totalPassTotalInt =(totalPassTotalInt + sumPassTotal);
					totalApplyTotal = totalApplyTotal+sumApplyTotal;
					sumProportion =decformat.format(totalPassTotal*100/totalApplyTotal)+"%";
					totalJob = totalJob+sumJob;
					totalFamily = totalFamily+sumFamily;
					totalJobFamily = totalJobFamily+sumJobFamily;
					totalDisability = totalDisability+sumDisability;
					totalJobDisability = totalJobDisability+sumJobDisability;
					totalFamilyDisability = totalFamilyDisability+sumFamilyDisability;
					totalJobFamilyDisability = totalJobFamilyDisability+sumJobFamilyDisability;
					// 每列合计需要调用的方法
					generateCountCell(wb,totalGraduationTotal,row,4,styleBold);
					generateCountCell(wb,totalPassTotalInt,row,5,styleBold);
					generateCountCell(wb,sumProportion,row,6,styleBold);
					generateCountCell(wb,totalJob,row,7,styleBold);
					generateCountCell(wb,totalFamily,row,8,styleBold);
					generateCountCell(wb,totalJobFamily,row,9,styleBold);
					generateCountCell(wb,totalDisability,row,10,styleBold);
					generateCountCell(wb,totalJobDisability,row,11,styleBold);
					generateCountCell(wb,totalFamilyDisability,row,12,styleBold);
					generateCountCell(wb,totalJobFamilyDisability,row,13,styleBold);
				}
				
				if(DataUtil.isEquals("byClass", statByWay)){//按班级查询时需要添加专业和班级查询条件
					List<PoorStudentClassView> classList = this.poorStudentService.queryPoorStudentByClass(poorStudent,yearDic);
					for (int i = 0; i < classList.size(); i++)
					{   
						PoorStudentClassView pc = classList.get(i);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("index",i+1);
						map.put("schoolYear",pc.getSchoolYear()!=null?pc.getSchoolYear().getName():"");
						map.put("collegeName",pc.getClassModel()!=null?(pc.getClassModel().getMajor()!=null?(pc.getClassModel().getMajor().getCollage()!=null?pc.getClassModel().getMajor().getCollage().getName():""):""):"");
						map.put("majorName",pc.getClassModel()!=null?(pc.getClassModel().getMajor()!=null?pc.getClassModel().getMajor().getMajorName():""):"");
						map.put("className",pc.getClassModel()!=null?pc.getClassModel().getClassName():"");//毕业生总人数
						map.put("graduation_total",pc.getGraduation_total());//毕业生总人数
						map.put("approvePassNum",pc.getApprovePassNum());//毕业生总人数
						if(pc.getTotal()!=0)
						{
							passTotalDouble = pc.getApprovePassNum();
							applyTotalDouble = pc.getTotal();
							map.put("proportion",decformat.format(passTotalDouble*100/applyTotalDouble)+"%");//双困生比例
						}else{
							map.put("proportion","0.00%");//双困生比例
						}
						map.put("job",pc.getJob());
						map.put("family",pc.getFamily());
						map.put("job_family",pc.getJob_family());
						map.put("disability",pc.getDisability());
						map.put("job_disability",pc.getJob_disability());
						map.put("family_disability",pc.getFamily_disability());
						map.put("job_family_disability",pc.getJob_family_disability());
						listMap.add(map);
						sumGraduationTotal = (int)(sumGraduationTotal+pc.getGraduation_total());
						sumApplyTotal =(int)(sumApplyTotal+pc.getTotal());
						sumJob = (int)(sumJob +pc.getJob());
						sumFamily = (int)(sumFamily +pc.getFamily());
						sumJobFamily = (int)(sumJobFamily +pc.getJob_family());
						sumDisability = (int)(sumDisability +pc.getDisability());
						sumJobDisability = (int)(sumJobDisability +pc.getJob_disability());
						sumFamilyDisability = (int)(sumFamilyDisability +pc.getFamily_disability());
						sumJobFamilyDisability = (int)(sumJobFamilyDisability +pc.getJob_family_disability());
						sumPassTotal =(int)(sumPassTotal+pc.getApprovePassNum());
					}
					wb=this.excelService.exportData("export_poorStudent_class.xls","exportPoorStudentByClass",listMap);
					//添加总数列表 
					HSSFSheet sheet = wb.getSheetAt(0);
					int rowNum = sheet.getLastRowNum();
					HSSFRow row = sheet.createRow(rowNum+1);
					
					//单元格样式
					HSSFCellStyle styleBold = (HSSFCellStyle) wb.createCellStyle();
					styleBold.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
					styleBold.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
					styleBold.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
					styleBold.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
					styleBold.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
					
					// 总结需要合并单元格的地方
					HSSFCell cell = row.createCell(0);
					cell.setCellValue("合计"); // 跨单元格显示的数据
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);  
					sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(),row.getRowNum(), 0, 3));
					cell.setCellStyle(styleBold);
					
					totalGraduationTotal =totalGraduationTotal + sumGraduationTotal;
					totalPassTotal =totalPassTotal+sumPassTotal;
					totalPassTotalInt =(totalPassTotalInt + sumPassTotal);
					totalApplyTotal = totalApplyTotal+sumApplyTotal;
					sumProportion =decformat.format(totalPassTotal*100/totalApplyTotal)+"%";
					totalJob = totalJob+sumJob;
					totalFamily = totalFamily+sumFamily;
					totalJobFamily = totalJobFamily+sumJobFamily;
					totalDisability = totalDisability+sumDisability;
					totalJobDisability = totalJobDisability+sumJobDisability;
					totalFamilyDisability = totalFamilyDisability+sumFamilyDisability;
					totalJobFamilyDisability = totalJobFamilyDisability+sumJobFamilyDisability;
					// 每列合计需要调用的方法
					generateCountCell(wb,totalGraduationTotal,row,4,styleBold);
					generateCountCell(wb,totalPassTotalInt,row,5,styleBold);
					generateCountCell(wb,sumProportion,row,6,styleBold);
					generateCountCell(wb,totalJob,row,7,styleBold);
					generateCountCell(wb,totalFamily,row,8,styleBold);
					generateCountCell(wb,totalJobFamily,row,9,styleBold);
					generateCountCell(wb,totalDisability,row,10,styleBold);
					generateCountCell(wb,totalJobDisability,row,11,styleBold);
					generateCountCell(wb,totalFamilyDisability,row,12,styleBold);
					generateCountCell(wb,totalJobFamilyDisability,row,13,styleBold);
				}
				
				String filename = "双困生统计导出";
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
	    
	    
	    
	    /**
		 * 
		 * @Title: generateCountCell
		 * @Description: 生成统计表格字段
		 * @throws
		 */
		private void generateCountCell(HSSFWorkbook wb,Object value,HSSFRow  row , int cellNum,HSSFCellStyle styleBold )
		{
			HSSFCell cell = null;
            cell = row.createCell(cellNum);
            cell.setCellValue(value.toString()); // 跨单元格显示的数据
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);  
		}
	    
	    
	    
}