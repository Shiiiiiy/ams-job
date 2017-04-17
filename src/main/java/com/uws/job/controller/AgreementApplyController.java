
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
import com.uws.comp.service.ICompService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.common.CommonApproveComments;
import com.uws.domain.job.AgreementForm;
import com.uws.domain.job.AgreementModel;
import com.uws.domain.job.SchoolGoodStudent;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.sponsor.NationalLoanModel;
import com.uws.job.service.IAgreementService;
import com.uws.job.util.Constants;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.user.model.User;
import com.uws.util.CheckUtils;
import com.uws.util.ProjectSessionUtils;

/**
 * 
* @ClassName: AgreementApplyController 
* @Description: TODO(就业协议书补办申请) 
* 主要实现的功能  就业协议补办申请， 以及就业协议的审核功能
* @author 联合永道
* @date 2015-10-19 上午10:20:03 
*
 */
@Controller
public class AgreementApplyController {
	
	private static Logger log = new LoggerFactory(AgreementController.class);
	@Autowired
	private IAgreementService agreementService;
	@Autowired
	private IBaseDataService baseDataService;
	@Autowired
	private ICompService compService;
	
	/**
	 * @Title: listApplyAgreement
	 * @Description: TODO(就业协议书补办申请列表)
	 * @param model
	 * @param request
	 * @param agreement
	 * @return
	 * @throws
	 */
	@RequestMapping({"/job/apply/opt-query/agreementApplyList"})
	public String listAgreementApply(ModelMap model, HttpServletRequest request, AgreementModel agreement){
		
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
    	
		List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
		model.addAttribute("collegeList", collegeList);
		
		
    	if(agreement != null){
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
    	
		model.addAttribute("statusList", Constants.STATUS_REPLACE_LIST);
		model.addAttribute("agreement", agreement);
		model.addAttribute("passStatus", Constants.DIC_PASS_STATUS);
		model.addAttribute("submitStatus", Constants.DIC_SUBMIT_STATUS);
		
		return Constants.MENUKEY_AGREEMENT_INFO + "/listAgreementApply" ;
	} 
	
	/**
	 * @Title: applyAgreement
	 * @Description: TODO(点击申请按钮，进入就业协议补办申请页面)
	 * @param model
	 * @param request
	 * @return
	 * @throws
	 */
	@RequestMapping({"/job/agreement/opt-apply/applyAgreement"})
	public String applyAgreement(ModelMap model, HttpServletRequest request ) {
		String id = request.getParameter("id");
		AgreementModel agreement = this.agreementService.findAgreementById(id);
		
		model.addAttribute("agreement", agreement);
		
		log.info("进入就业协议信息补办申请页面");
		return Constants.MENUKEY_AGREEMENT_INFO + "/applyAgreement";
	}
	
	/**
	 * @Title: saveAgreementApply
	 * @Description: (申请就业协议补办，添加几个字段之后(如申请补办理由、就业单位名称)，保存到数据库,即保存就业协议申请信息)
	 * @Description: (同时就业协议补办的状态置为已提交)
	 * @param model
	 * @param request
	 * @param nationalLoan
	 * @return
	 * @throws
	 */
	@RequestMapping({ "/job/agreement/opt-save/saveAgreementApply"})
	public String saveAgreementApply(ModelMap model, HttpServletRequest request, AgreementModel agreement) {
		
		String id = agreement.getId();
		AgreementModel agreementPo = this.agreementService.findAgreementById(id);
		agreementPo.setApplyReason(agreement.getApplyReason());
		agreementPo.setHumanDepartment(agreement.getHumanDepartment());
		agreementPo.setEmploymentDepartment(agreement.getEmploymentDepartment());
		agreementPo.setStatus(Constants.DIC_SUBMIT_STATUS);
		this.agreementService.update(agreementPo);
			
		model.addAttribute("submitStatus", Constants.DIC_SUBMIT_STATUS);
		
		log.info("就业协议补办信息提交申请成功!");
		return "redirect:/job/apply/opt-query/agreementApplyList.do";
		
	}
	
	/**
	 * @Title: approveSingleAgreement
	 * @Description: (进入就业协议单个审核页面)
	 * @param model
	 * @param request
	 * @param nationalLoan
	 * @return
	 * @throws
	 */
	@RequestMapping({ "/job/agreement/opt-approve/approveSingleAgreement"})
	public String approveSingleAgreement(ModelMap model, HttpServletRequest request) {
		
		String id = request.getParameter("id");
		AgreementModel agreement = this.agreementService.findAgreementById(id);
		model.addAttribute("agreement", agreement);
		
		log.info("进入单个审核学生就业协议页面");
		return Constants.MENUKEY_AGREEMENT_APPROVE + "/approveSingleAgreement";
	}
	
	/**
	 * @Title: saveAgreementRefuse
	 * @Description: TODO(在单个审核页面，拒绝就业协议补办申请之后，进入此方法)
	 * @Description: TODO(只需要更改数据库中的状态即可)
	 * @param model
	 * @param request
	 * @param agreement
	 * @return
	 * @throws
	 */
	@RequestMapping({ "/job/agreement/opt-save/refuseSingleAgreement"})
	public String saveAgreementRefuse(ModelMap model, HttpServletRequest request, AgreementModel agreement) {
		String id = request.getParameter("id");
		
		AgreementModel agreementPo = this.agreementService.findAgreementById(id);
		agreementPo.setStatus(Constants.DIC_REFUSE_STATUS);
		this.agreementService.update(agreementPo);
		
		log.info("单个就业协议审核完成，更新就业协议的状态为拒绝，返回列表页面");
		return "redirect:/job/approve/opt-query/agreementApproveList.do";
	}
	
	
	/**
	 * @Title: saveAgreementPass
	 * @Description: TODO(在单个审核页面，通过就业协议补办申请之后，进入此方法)
	 * @Description: TODO(需要同时更新数据库中就业协议的状态和保存新的就业协议书编号)
	 * @param model
	 * @param request
	 * @param agreement
	 * @return
	 * @throws
	 */
	@RequestMapping({ "/job/agreement/opt-save/passSingleAgreement"})
	public String saveAgreementPass(ModelMap model, HttpServletRequest request, AgreementModel agreement) {
		String id = request.getParameter("id");
		String newAgreementCode = agreement.getNewAgreementCode();
		
		AgreementModel agreementPo = this.agreementService.findAgreementById(id);
		agreementPo.setNewAgreementCode(newAgreementCode);
		agreementPo.setStatus(Constants.DIC_PASS_STATUS);
		this.agreementService.update(agreementPo);
		
		log.info("单个就业协议审核完成，保存新的就业协议书编号，返回列表页面");
		return "redirect:/job/approve/opt-query/agreementApproveList.do";
	}
	
	/***
	 * 
	 * @Title: listAgreementApprove
	 * @Description: TODO(就业协议书审核列表)
	 * @param model
	 * @param request
	 * @param agreement
	 * @return
	 * @throws
	 */
	@RequestMapping({"/job/approve/opt-query/agreementApproveList"})
	public String listAgreementApprove(ModelMap model, HttpServletRequest request, AgreementModel agreement){
		
		log.info("就业协议书审核列表");
		
		if(agreement.getEmploymentYear() == null){
			agreement.setEmploymentYear(Constants.CURRENT_YEAR);
		}
		
    	int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
    	Page page = agreementService.queryAgreementApproveList(pageNo, Page.DEFAULT_PAGE_SIZE, agreement);
    	model.addAttribute("page", page);
    	
    	model.addAttribute("yearList", Constants.YEAR_LIST);
    	
		List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
		model.addAttribute("collegeList", collegeList);
		
    	if(agreement != null){
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
    	
		//传递查询条件未申请
		model.addAttribute("statusList", Constants.STATUS_REPLACE_LIST);
		// request传递查询条件
		model.addAttribute("agreement", agreement);
		model.addAttribute("submitStatus", Constants.DIC_SUBMIT_STATUS);
		return Constants.MENUKEY_AGREEMENT_APPROVE + "/listAgreementApprove" ;
	} 
	
	
	/**
	 * 
	 * @Title: AgreementApplyController.java 
	 * @Package com.uws.job.controller 
	 * @Description:撤销就业协议申请
	 * @author LiuChen 
	 * @date 2016-4-12 下午2:47:04
	 */
	@ResponseBody
	@RequestMapping(value={"/job/approve/opt-cancel/cancelAgreement"},produces={"text/plain;charset=UTF-8"})
	public String cancelAgreement(HttpServletRequest request,HttpServletResponse response,String id){
		AgreementModel agreementPo = this.agreementService.findAgreementById(id);
		agreementPo.setStatus(Constants.DIC_CANCEL_STATUS);
		this.agreementService.update(agreementPo);
		return "success";
	}
	
	
	
	/**
	 * @Title: approveAgreement
	 * @Description: (弹出层批量审核就业协议)
	 * @param model
	 * @param request
	 * @return
	 * @throws
	 */
	@RequestMapping({"job/agreement/nsm/approveAgreementInfo"})
	public String approveAgreement(ModelMap model, HttpServletRequest request ){
		List<AgreementModel> agreements = new ArrayList<AgreementModel>();
		String checkedIds  = request.getParameter("checkdIds");
		if(checkedIds != null && !"".equals(checkedIds)){
			String[] ids = checkedIds.split(",");
			AgreementModel agreement = new AgreementModel();
			for(String id:ids){
				 if(id!=null && !id.equals("")){
					 agreement = agreementService.findAgreementById(id);
					 agreement.setStatus(Constants.DIC_PASS_STATUS);
					 agreements.add(agreement);
				 }
			}
		}
		model.addAttribute("agreements", agreements);
		
		log.info("弹出层批量审核就业协议信息");
		return Constants.MENUKEY_AGREEMENT_APPROVE + "/approveAllAgreement" ;
	}
	
	/**
	 * @Title: saveAgreement
	 * @Description: (批量操作，新增新的就业协议书编号)
	 * @Description: (审批通过的学生给予新的就业协议协议书编号，保存在数据库中)
	 * @param model
	 * @param request
	 * @param agreement
	 * @param fileId
	 * @return
	 * @throws
	 */
	@RequestMapping({ "/job/agreement/opt-save/saveAgreement"})
	public String saveAgreement(ModelMap model, HttpServletRequest request, AgreementForm agreementForm) {
		List<AgreementModel> agreements =  agreementForm.getAgreements();
		for(int i=0; i<agreements.size(); i++){
			AgreementModel agreement = agreements.get(i);
			String id = agreement.getId();
			String code = agreement.getNewAgreementCode();
			AgreementModel agreementPo = this.agreementService.findAgreementById(id);
			agreementPo.setStatus(Constants.DIC_PASS_STATUS);
			agreementPo.setNewAgreementCode(code);
			this.agreementService.update(agreementPo);
		}
		
		log.info("新的就业协议书编号授予成功!");
		return "redirect:/job/approve/opt-query/agreementApproveList.do";
	}
	
	/***
	 * 
	 * @Title: approveBatchAgreement
	 * @Description: TODO(批量拒绝就业协议)
	 * @param model
	 * @param request
	 * @param checkedIds
	 * @param isConfirm
	 * @return
	 * @throws
	 */
	@ResponseBody
    @RequestMapping({"/job/agreement/opt-update/rejectAgreement"}) 
    public String approveBatchAgreement(ModelMap model, HttpServletRequest request, String checkedIds) {
    	if(checkedIds != null && !"".equals(checkedIds)){
			String[] ids = checkedIds.split(",");
			AgreementModel agreement = new AgreementModel();
			for(String id:ids){
				 if(id!=null && !id.equals("")){
					 agreement = agreementService.findAgreementById(id);
 	    		     agreement.setStatus(Constants.DIC_REFUSE_STATUS);
					 agreementService.update(agreement);
				 }
			}
		}
    	    				
    	return "success";
    }
}
