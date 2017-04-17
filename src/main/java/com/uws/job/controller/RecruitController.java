package com.uws.job.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.common.service.IBaseDataService;
import com.uws.core.base.BaseController;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.job.RecruitModel;
import com.uws.job.service.IRecruitService;
import com.uws.job.util.Constants;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.util.CheckUtils;
import com.uws.util.ProjectSessionUtils;

/**
* @ClassName: RecruitController 
* @Description: TODO (招聘会情况控制器类)
* 主要实现的功能有 学院上报年度招聘会情况，招就办统计招聘会情况
* @author 联合永道
* @date 2016-1-4 下午3:08:59 
*
 */
@Controller
public class RecruitController extends BaseController {
	
	private static Logger log = new LoggerFactory(RecruitController.class);
	@Autowired
	private IRecruitService recruitService;
	@Autowired
	private IBaseDataService baseDataService;
	
	/**
	 * @Title: editRecruit
	 * @Description: TODO(此方法，用于进入招聘会情况的修改页面)
	 * @param model
	 * @param request
	 * @return
	 * @throws
	 */
	@RequestMapping({"/job/recruit/opt-add/addRecruit", "/job/recruit/opt-edit/editRecruit"})
	public String editRecruit(ModelMap model, HttpServletRequest request ) {
		String id = request.getParameter("id");
		if(id != null && !id.equals("")){
			RecruitModel recruit = this.recruitService.findRecruitById(id);
			model.addAttribute("recruit", recruit);
		}
		
		model.addAttribute("yearList", Constants.YEAR_LIST);
		String orgId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
		List<BaseAcademyModel> collegeList =  new ArrayList<BaseAcademyModel>();
		boolean bol = CheckUtils.isCurrentOrgEqCollege(orgId);
		if(bol)
			collegeList.add(baseDataService.findAcademyById(orgId));
		else
			collegeList = baseDataService.listBaseAcademy();
		
    	model.addAttribute("flag", bol);
    	model.addAttribute("collegeList", collegeList);
		return Constants.MENUKEY_RECRUIT_INFO + "/editRecruit";
	}
	
	/**
	 * 
	 * @Title: checkCodeRepeat
	 * @return
	 * @throws
	 */
	@ResponseBody
	@RequestMapping({"/job/recruit/opt-query/checkExistis"})
    public String checkApplyRepeat(HttpServletRequest request, @RequestParam String year, @RequestParam String collegeId){ 
    	return recruitService.isApply(collegeId, year);
    }
	
	/**
	 * @Title: updateRecruit
	 * @Description: TODO(修改数据库中的数据 )
	 * @param model
	 * @param recruit
	 * @return
	 * @throws
	 */
	@RequestMapping({ "/job/recruit/opt-update/updateRecruit"})
	public String updateRecruit(HttpServletRequest request, ModelMap model, RecruitModel recruit) {
		String id = recruit.getId();
		if(id != null && !id.equals("")){
			RecruitModel recruitPo = this.recruitService.findRecruitById(id);
			BeanUtils.copyProperties(recruit, recruitPo, new String[]{ "college","year", "createTime"});
			this.recruitService.update(recruitPo);
		}else {
			this.recruitService.save(recruit);
		}
		log.info("招聘会情况更新成功!");
		return "redirect:/job/recruit/opt-query/recruitList.do";
	}
	
	/**
	 * @Title: deleteRecruit
	 * @Description: TODO(删除)
	 * @param id
	 * @return
	 * @throws
	 */
	@RequestMapping({ "/job/recruit/opt-del/deleteRecruit" })
	@ResponseBody
	public String deleteRecruit(String id) {
		this.recruitService.deleteRecruitById(id);
		log.info("删除操作成功！");
		return "success" ;
	}
	
	@RequestMapping({"/job/recruit/opt-query/recruitList"})
	public String listRecruit(ModelMap model, HttpServletRequest request, RecruitModel recruit){
		log.info("查询招聘会信息列表列表");
		
		if(recruit.getYear() == null){
			recruit.setYear(Constants.CURRENT_YEAR);
		}
		
		String collegeId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
		if(CheckUtils.isCurrentOrgEqCollege(collegeId)){
			model.addAttribute("collegeId", collegeId);
			BaseAcademyModel college = new BaseAcademyModel();
			college.setId(collegeId);
			recruit.setCollege(college);
		}
		
    	int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
    	Page page = recruitService.queryRecruitList(pageNo, Page.DEFAULT_PAGE_SIZE, recruit);
    	
    	model.addAttribute("page", page);
    	model.addAttribute("recruit", recruit);
    	model.addAttribute("yearList", Constants.YEAR_LIST);
    	
		List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
		model.addAttribute("collegeList", collegeList);
		
		return Constants.MENUKEY_RECRUIT_INFO + "/listRecruit" ;
	} 
	
	@RequestMapping({"/job/recruitment/opt-query/recruitList"})
	public String listRecruitment(ModelMap model, HttpServletRequest request, RecruitModel recruit){
		log.info("查询招聘会信息统计列表");
		
		if(recruit.getYear() == null){
			recruit.setYear(Constants.CURRENT_YEAR);
		}
		
    	int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
    	Page page = recruitService.queryRecruitList(pageNo, Page.DEFAULT_PAGE_SIZE, recruit);
    	
    	model.addAttribute("page", page);
    	model.addAttribute("recruit", recruit);
    	model.addAttribute("yearList", Constants.YEAR_LIST);
    	
		return Constants.MENUKEY_RECRUIT_INFO + "/listRecruitment" ;
	} 
	
}
