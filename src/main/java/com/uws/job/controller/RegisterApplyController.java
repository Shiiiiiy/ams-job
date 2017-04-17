package com.uws.job.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.common.service.IBaseDataService;
import com.uws.common.service.IStudentCommonService;
import com.uws.comp.service.ICompService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.job.AgreementModel;
import com.uws.domain.job.RegisterModel;
import com.uws.domain.job.RegisterModel;
import com.uws.domain.job.RegisterForm;
import com.uws.domain.job.RegisterModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.job.service.IRegisterService;
import com.uws.job.util.Constants;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.FileFactory;
import com.uws.util.CheckUtils;
import com.uws.util.ProjectSessionUtils;

/**
 * 
* @ClassName: RegisterApplyController 
* @Description: TODO(报到证补办申请) 
* 主要实现的功能有  报到证的补办申请，报到证的审核
* @author 联合永道
* @date 2015-11-3 下午4:50:20 
*
 */
@Controller
public class RegisterApplyController {
	
	@Autowired
	private IRegisterService registerService;
	@Autowired
	private IBaseDataService baseDataService;
	@Autowired
	private ICompService compService;
	
	private static Logger log = new LoggerFactory(RegisterApplyController.class);
	
	/**
	 * 
	 * @Title: listRegisterApply
	 * @Description: TODO(展示报到证补办申请列表)
	 * @param model
	 * @param request
	 * @param register
	 * @return
	 * @throws
	 */
	@RequestMapping({"job/registerApply/opt-query/registerApplyList"})
	public String listRegisterApply(ModelMap model, HttpServletRequest request, RegisterModel register){
		
		log.info("查询报到证补办申请列表");
		
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
    	
		List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
		model.addAttribute("collegeList", collegeList);
		
    	if(register != null){	
    		if(register.getCollege() != null && StringUtils.hasText(register.getCollege().getId())){
    			String collageId = register.getCollege().getId();
    			List<BaseMajorModel> majorList = compService.queryMajorByCollage(collageId);
    			log.debug("若已经选择学院，则查询学院下的专业信息.");
    			model.addAttribute("majorList", majorList);
    		}
    		if(register.getMajor() != null && StringUtils.hasText(register.getMajor().getId()) ){
    			String majorId = register.getMajor().getId();
    			List<BaseClassModel> classList = compService.queryClassByMajor(majorId);
    			log.debug("若已经选择专业，则查询专业下的班级信息.");
    			model.addAttribute("classList", classList);
    		}
    	}
		
		model.addAttribute("yearList", Constants.YEAR_LIST);
		model.addAttribute("statusList", Constants.STATUS_REPLACE_LIST);
		// request传递查询条件
		model.addAttribute("register", register);
		model.addAttribute("passStatus", Constants.DIC_PASS_STATUS);
		model.addAttribute("submitStatus", Constants.DIC_SUBMIT_STATUS);
		return Constants.MENUKEY_REGISTER_INFO + "/listRegisterApply" ;
	} 
	
	/**
	 * @Title: applyRegister
	 * @Description: TODO(点击申请按钮，进入报到证补办申请页面)
	 * @param model
	 * @param request
	 * @return
	 * @throws
	 */
	@RequestMapping({"/job/register/opt-apply/applyRegister"})
	public String applyRegister(ModelMap model, HttpServletRequest request ) {
		String id = request.getParameter("id");
		RegisterModel register = this.registerService.findRegisterById(id);
		
		model.addAttribute("register", register);
		log.info("进入报到证补办申请页面");
		
		return Constants.MENUKEY_REGISTER_INFO + "/applyRegister";
	}
	
	/**
	 * @Title: saveRegisterApply
	 * @Description: (申请报到证补办，添加几个字段之后(如申请补办理由、就业单位名称)，保存到数据库,即保存报到证补办申请信息)
	 * @Description: (同时报到证补办的状态置为已提交)
	 * @param model
	 * @param request
	 * @return
	 * @throws
	 */
	@RequestMapping({ "/job/register/opt-save/saveRegisterApply"})
	public String saveRegister(ModelMap model, HttpServletRequest request, RegisterModel register) {
		String id = register.getId();
		
		RegisterModel registerPo = this.registerService.findRegisterById(id);
		registerPo.setApplyReason(register.getApplyReason());
		registerPo.setHumanDepartment(register.getHumanDepartment());
		registerPo.setEmploymentDepartment(register.getEmploymentDepartment());
		registerPo.setStatus(Constants.DIC_SUBMIT_STATUS);
		this.registerService.update(registerPo);
			
		model.addAttribute("submitStatus", Constants.DIC_SUBMIT_STATUS);
		
		log.info("报到证改派信息提交申请成功!");
		return "redirect:/job/registerApply/opt-query/registerApplyList.do";
	}
	
	/***
	 * 
	 * @Title: listRegisterApprove
	 * @Description: TODO(报到证审核列表)
	 * @param model
	 * @param request
	 * @param register
	 * @return
	 * @throws
	 */
	@RequestMapping({"/job/confirm/opt-query/registerApproveList"})
	public String listRegisterApprove(ModelMap model, HttpServletRequest request, RegisterModel register){
		
		log.info("查询报到证审核列表");
		
		if(register.getEmploymentYear() == null){
			register.setEmploymentYear(Constants.CURRENT_YEAR);
		}
		
    	int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
    	Page page = registerService.queryRegisterApproveList(pageNo, Page.DEFAULT_PAGE_SIZE, register);
    	
    	model.addAttribute("page", page);
    	model.addAttribute("register", register);
    	model.addAttribute("yearList", Constants.YEAR_LIST);
    	
		List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
		model.addAttribute("collegeList", collegeList);
		
    	if(register != null){
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
    	
		model.addAttribute("statusList", Constants.STATUS_REPLACE_LIST);
		model.addAttribute("passStatus", Constants.DIC_PASS_STATUS);
		model.addAttribute("submitStatus", Constants.DIC_SUBMIT_STATUS);
		
		return Constants.MENUKEY_REGISTER_APPROVE + "/listRegisterApprove" ;
	}
	
	
	/**
	 * 
	 * @Title: RegisterApplyController.java 
	 * @Package com.uws.job.controller 
	 * @Description:撤销报到证编号
	 * @author LiuChen 
	 * @date 2016-4-12 下午3:05:40
	 */
	@ResponseBody
	@RequestMapping(value={"/job/register/opt-cancel/cancelRegister"},produces={"text/plain;charset=UTF-8"})
	public String cancelRegister(HttpServletRequest request,HttpServletResponse response,String id){
		RegisterModel registerPo = this.registerService.findRegisterById(id);
		registerPo.setStatus(Constants.DIC_CANCEL_STATUS);
		this.registerService.update(registerPo);
		return "success";
	}
	
	
	/**
	 * @Title: approveSingleRegister
	 * @Description: (进入报到证单个审核页面)
	 * @param model
	 * @param request
	 * @param nationalLoan
	 * @return
	 * @throws
	 */
	@RequestMapping({ "/job/register/opt-approve/approveSingleRegister"})
	public String approveSingleRegister(ModelMap model, HttpServletRequest request) {
		
		String id = request.getParameter("id");
		RegisterModel register = this.registerService.findRegisterById(id);
		model.addAttribute("register", register);
		
		log.info("进入单个审核学生报到证页面");
		return Constants.MENUKEY_REGISTER_APPROVE + "/approveSingleRegister";
	}
	
	/**
	 * @Title: saveRegisterRefuse
	 * @Description: TODO(在单个审核页面，拒绝报到证补办申请之后，进入此方法)
	 * @Description: TODO(只需要更改数据库中的状态即可)
	 * @param model
	 * @param request
	 * @param register
	 * @return
	 * @throws
	 */
	@RequestMapping({ "/job/register/opt-save/refuseSingleRegister"})
	public String saveRegisterRefuse(ModelMap model, HttpServletRequest request, RegisterModel register) {
		String id = request.getParameter("id");
		RegisterModel registerPo = this.registerService.findRegisterById(id);
		registerPo.setStatus(Constants.DIC_REFUSE_STATUS);
		this.registerService.update(registerPo);
		
		log.info("单个报到证审核完成，更新报到证的状态为拒绝，返回列表页面");
		return "redirect:/job/confirm/opt-query/registerApproveList.do";
	}
	
	
	/**
	 * @Title: saveRegisterPass
	 * @Description: TODO(在单个审核页面，通过报到证补办申请之后，进入此方法)
	 * @Description: TODO(需要同时更新数据库中报到证的状态和保存新的报到证书编号)
	 * @param model
	 * @param request
	 * @param register
	 * @return
	 * @throws
	 */
	@RequestMapping({ "/job/register/opt-save/passSingleRegister"})
	public String saveRegisterPass(ModelMap model, HttpServletRequest request, RegisterModel register) {
		String id = request.getParameter("id");
		String newRegisterCode = register.getNewRegisterCode();
		
		RegisterModel registerPo = this.registerService.findRegisterById(id);
		registerPo.setNewRegisterCode(newRegisterCode);
		registerPo.setStatus(Constants.DIC_PASS_STATUS);
		this.registerService.update(registerPo);
		
		log.info("单个报到证审核完成，保存新的报到证书编号，返回列表页面");
		return "redirect:/job/confirm/opt-query/registerApproveList.do";
	}
	
	/**
	 * @Title: approveRegister
	 * @Description: (弹出层批量审核报到证)
	 * @param model
	 * @param request
	 * @return
	 * @throws
	 */
	@RequestMapping({"job/register/nsm/approveRegister"})
	public String approveRegister(ModelMap model, HttpServletRequest request ){
		List<RegisterModel> registers = new ArrayList<RegisterModel>();
		String checkedIds  = request.getParameter("checkdIds");
		if(checkedIds != null && !"".equals(checkedIds)){
			String[] ids = checkedIds.split(",");
			RegisterModel register = new RegisterModel();
			for(String id:ids){
				 if(id!=null && !id.equals("")){
					 register = registerService.findRegisterById(id);
					 registers.add(register);
				 }
			}
		}
		model.addAttribute("registers", registers);
		
		log.info("弹出层批量审核报到证信息");
		return Constants.MENUKEY_REGISTER_APPROVE + "/approveAllRegister" ;
	}
	
	/**
	 * @Title: saveRegister
	 * @Description: (批量新增新的报到证编号)
	 * @Description: (审批通过的学生给予新的报到证编号，保存在数据库中)
	 * @param model
	 * @param request
	 * @param register
	 * @param fileId
	 * @return
	 * @throws
	 */
	@RequestMapping({ "/job/register/opt-save/saveRegister"})
	public String saveRegister(ModelMap model, HttpServletRequest request, RegisterForm registerForm ) {
		
		List<RegisterModel> registers = registerForm.getRegisters();
		for(int i=0; i<registers.size(); i++){
			RegisterModel register = registers.get(i);
			String id = register.getId();
			String code = register.getNewRegisterCode();
			RegisterModel registerPo = this.registerService.findRegisterById(id);
			registerPo.setStatus(Constants.DIC_PASS_STATUS);
			registerPo.setNewRegisterCode(code);
			this.registerService.update(registerPo);
		}
		
		log.info("新的报到证编号授予成功!");
		return "redirect:/job/confirm/opt-query/registerApproveList.do";
	}
	
	/**
	 * @Title: approveBatchRegister
	 * @Description: (批量审核保证证补办，  进行驳回拒绝操作)
	 * @param model
	 * @param request
	 * @param checkedIds
	 * @param isConfirm
	 * @return
	 * @throws
	 */
	@ResponseBody
    @RequestMapping({"/job/register/opt-update/rejectRegister"}) 
    public String approveBatchRegister(ModelMap model, HttpServletRequest request, String checkedIds, String isConfirm) {
    	if(checkedIds != null && !"".equals(checkedIds)){
			String[] ids = checkedIds.split(",");
			RegisterModel register = new RegisterModel();
			for(String id:ids){
				 if(id!=null && !id.equals("")){
					 register = registerService.findRegisterById(id);
 	    		     register.setStatus(Constants.DIC_REFUSE_STATUS);
					 registerService.update(register);
				 }
			}
		}
    	    				
    	return "success";
    }
	
	
}
