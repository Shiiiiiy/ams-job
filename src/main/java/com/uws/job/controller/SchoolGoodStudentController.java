package com.uws.job.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import com.uws.common.service.ICommonApproveService;
import com.uws.common.service.IRewardCommonService;
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
import com.uws.domain.common.CommonApproveComments;
import com.uws.domain.job.SchoolGoodStudent;
import com.uws.domain.job.SchoolGoodStudentClassView;
import com.uws.domain.job.SchoolGoodStudentCollegeView;
import com.uws.domain.job.SchoolGoodStudentMajorView;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.job.service.ISchoolGoodStudentService;
import com.uws.job.util.Constants;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.IDicService;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.sys.util.MultipartFileValidator;
import com.uws.user.model.User;
import com.uws.util.CheckUtils;

/**
 * @className SchoolGoodStudentController.java
 * @package com.uws.job.controller
 * @description
 * @date 2015-11-6  下午5:03:01
 */
@Controller
public class SchoolGoodStudentController extends BaseController {
	@Autowired
	private IDicService dicService;
	@Autowired
	private ICompService compService;
	@Autowired
	private IExcelService excelService;
	@Autowired
	private IBaseDataService baseDateService;
	
	private DicUtil dicUtil = DicFactory.getDicUtil();
	@Autowired
	private ICommonApproveService commonApproveService;
	
	private FileUtil fileUtil = FileFactory.getFileUtil();
	@Autowired
	private ISchoolGoodStudentService schoolGoodStudentService;
	@Autowired
	private IStudentCommonService studentCommonService;
	@Autowired
	private IRewardCommonService rewardCommonService;
	
	private SessionUtil sessionUtil = SessionFactory.getSession(Constants.MENUKEY_JOB_SCHOOL_GOOD_STUDENT);
	/**
	 * 校优秀生查询列表页面显示审核通过
	 * @param model
	 * @param request
	 * @param response
	 * @param schoolGoodStudentVO
	 * @return
	 */
	@RequestMapping("/job/querySchoolGoodStudent/opt-query/querySchoolGoodStudentPage")
	public String querySchoolGoodStudentByPass(ModelMap model,HttpServletRequest request,HttpServletResponse response,SchoolGoodStudent schoolGoodStudentVO){
		String strPageNo = request.getParameter("pageNo");
		int pageNo = DataUtil.isNotNull(strPageNo) ? Integer.parseInt(strPageNo) : 1;
		List<BaseAcademyModel> colleges =  this.baseDateService.listBaseAcademy();
		List<BaseMajorModel> majors = new ArrayList<BaseMajorModel>();
		List<BaseClassModel> classes = new ArrayList<BaseClassModel>();
		List<Dic> years = this.dicUtil.getDicInfoList("YEAR");//学年
		String orgId = (String)request.getSession().getAttribute("_teacher_orgId");
		//判断用户是否为招就处
		boolean isJobOffice = true;
		if(CheckUtils.isCurrentOrgEqCollege(orgId)){
			isJobOffice = false;
		}
		if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId())){
			if(!isJobOffice){
				BaseAcademyModel college = new BaseAcademyModel();
				college.setId(orgId);
				schoolGoodStudentVO.getStudentId().setCollege(college);
			}
		}else{
			if(!isJobOffice){
				StudentInfoModel studentInfoModel = new StudentInfoModel();
				BaseAcademyModel college = new BaseAcademyModel();
				college.setId(orgId);
				studentInfoModel.setCollege(college);
				schoolGoodStudentVO.setStudentId(studentInfoModel);
			}
		}
		if(!DataUtil.isNotNull(schoolGoodStudentVO.getSchoolYear())){//添加默认学年
			schoolGoodStudentVO.setSchoolYear(SchoolYearUtil.getYearDic());
		}
		//数据回显 查询专业信息
		if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege().getId())){
			majors = this.compService.queryMajorByCollage(schoolGoodStudentVO.getStudentId().getCollege().getId());
			if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getMajor()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getMajor().getId())){
				classes = this.compService.queryClassByMajor(schoolGoodStudentVO.getStudentId().getMajor().getId());
			}
		}
		Page page = this.schoolGoodStudentService.querySchoolGoodStudentPage(schoolGoodStudentVO,Page.DEFAULT_PAGE_SIZE,pageNo,Constants.schoolGoodStudentTypeByPass);
		model.addAttribute("colleges", colleges);
		model.addAttribute("majors", majors);
		model.addAttribute("classes", classes);
		model.addAttribute("years", years);
		model.addAttribute("page", page);
		model.addAttribute("schoolGoodStudentVO", schoolGoodStudentVO);
		model.addAttribute("approveMap", Constants.getSchoolGoodStudentByPassMap());
		model.addAttribute("isJobOffice", isJobOffice);
		return Constants.MENUKEY_JOB_SCHOOL_GOOD_STUDENT+ "querySchoolGoodStudentPage";
	}
	/**
	 * 校优秀生申请列表页面
	 * @param request
	 * @param response
	 * @param schoolGoodStudentVO
	 * @return
	 */
	@RequestMapping("/job/schoolGoodStudent/opt-query/querySchoolGoodStudentPage")
	public String querySchoolGoodStudentPage(ModelMap model,HttpServletRequest request,HttpServletResponse response,SchoolGoodStudent schoolGoodStudentVO){
		String strPageNo = request.getParameter("pageNo");
		int pageNo = DataUtil.isNotNull(strPageNo) ? Integer.parseInt(strPageNo) : 1;
		List<BaseAcademyModel> colleges =  this.baseDateService.listBaseAcademy();
		List<BaseMajorModel> majors = new ArrayList<BaseMajorModel>();
		List<BaseClassModel> classes = new ArrayList<BaseClassModel>();
		List<Dic> years = this.dicUtil.getDicInfoList("YEAR");//学年
		String orgId = (String)request.getSession().getAttribute("_teacher_orgId");
		//判断用户是否为招就处
		boolean isJobOffice = true;
		if(CheckUtils.isCurrentOrgEqCollege(orgId)){
			isJobOffice = false;
		}
		if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId())){
			if(!isJobOffice){
				BaseAcademyModel college = new BaseAcademyModel();
				college.setId(orgId);
				schoolGoodStudentVO.getStudentId().setCollege(college);
			}
		}else{
			if(!isJobOffice){
				StudentInfoModel studentInfoModel = new StudentInfoModel();
				BaseAcademyModel college = new BaseAcademyModel();
				college.setId(orgId);
				studentInfoModel.setCollege(college);
				schoolGoodStudentVO.setStudentId(studentInfoModel);
			}
		}
		if(!DataUtil.isNotNull(schoolGoodStudentVO.getSchoolYear())){//添加默认学年
			schoolGoodStudentVO.setSchoolYear(SchoolYearUtil.getYearDic());
		}
		//数据回显 查询专业信息
		if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege().getId())){
			majors = this.compService.queryMajorByCollage(schoolGoodStudentVO.getStudentId().getCollege().getId());
			if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getMajor()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getMajor().getId())){
				classes = this.compService.queryClassByMajor(schoolGoodStudentVO.getStudentId().getMajor().getId());
			}
		}
		Page page = this.schoolGoodStudentService.querySchoolGoodStudentPage(schoolGoodStudentVO,Page.DEFAULT_PAGE_SIZE,pageNo,Constants.schoolGoodStudentTypeByAll);
		model.addAttribute("colleges", colleges);
		model.addAttribute("majors", majors);
		model.addAttribute("classes", classes);
		model.addAttribute("years", years);
		model.addAttribute("page", page);
		model.addAttribute("schoolGoodStudentVO", schoolGoodStudentVO);
		model.addAttribute("approveMap", Constants.getApproveMap());
		model.addAttribute("isJobOffice", isJobOffice);
		return Constants.MENUKEY_JOB_SCHOOL_GOOD_STUDENT + "listSchoolGoodStudent";
	}
	/**
	 * 添加、修改、审核页面跳转
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({"/job/schoolGoodStudent/opt-add/querySchoolGoodStudent","/job/schoolGoodStudent/opt-update/querySchoolGoodStudent","/job/approveSchoolGoodStudent/opt-query/querySchoolGoodStudent"})
	public String querySchoolGoodStudent(ModelMap model,HttpServletRequest request,HttpServletResponse response,SchoolGoodStudent schoolGoodStudentVO){
		String isApprove = request.getParameter("isApprove");
		String orgId = (String)request.getSession().getAttribute("_teacher_orgId");
		if(CheckUtils.isCurrentOrgEqCollege(orgId)){
			model.addAttribute("teacherCollegeId", orgId);
		}
		if(DataUtil.isNotNull(schoolGoodStudentVO) && DataUtil.isNotNull(schoolGoodStudentVO.getId())){
			SchoolGoodStudent schoolGoodStudent = this.schoolGoodStudentService.querySchoolGoodStudentById(schoolGoodStudentVO.getId());
			model.addAttribute("schoolGoodStudentVO", schoolGoodStudent);
		}
		if(DataUtil.isNotNull(schoolGoodStudentVO) && DataUtil.isNotNull(isApprove) && DataUtil.isEquals(isApprove, "true")){
			return Constants.MENUKEY_JOB_SCHOOL_GOOD_STUDENT + "approve/approveSchoolGoodStudent";
		}
		return Constants.MENUKEY_JOB_SCHOOL_GOOD_STUDENT + "editSchoolGoodStudent";
	}
	/**
	 * 为选中的人添加数据
	 * @param request
	 * @param response
	 * @param schoolGoodStudentVO
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/job/schoolGoodStudent/opt-query/queryAddStudentInfo"},produces={"text/plain;charset=UTF-8"})
	public String queryAddStudentInfo(HttpServletRequest request,HttpServletResponse response,SchoolGoodStudent schoolGoodStudentVO){
		SchoolGoodStudent schoolGoodStudent = new SchoolGoodStudent();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfyear = new SimpleDateFormat("yyyy");
		StringBuffer json = new StringBuffer();
		if(DataUtil.isNotNull(schoolGoodStudentVO) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getId())){
			//获取学生信息
			StudentInfoModel studentInfoModelPO = this.studentCommonService.queryStudentById(schoolGoodStudentVO.getStudentId().getId());
			schoolGoodStudent.setStudentId(studentInfoModelPO);
			//获取奖助信息
			String honor = this.schoolGoodStudentService.getStuAllAward(studentInfoModelPO);
			//获取综合测评信息   从学生入学的学年开始算起，一直算到添加时的当前日期的学年
			String startSchoolYear = sdfyear.format(studentInfoModelPO.getEnterDate());//入学年份
			String endSchoolYear = studentInfoModelPO.getClassId().getGraduatedYearDic().getCode();//毕业年份
			String performance = this.schoolGoodStudentService.getAllEvaluationInfos(startSchoolYear,endSchoolYear,studentInfoModelPO);
			json.append("{\"studentId\":\"").append(schoolGoodStudent.getStudentId().getId()).append("\",")
				.append("\"studentName\":\"").append(schoolGoodStudent.getStudentId().getName()).append("\",")
				.append("\"className\":\"").append(schoolGoodStudent.getStudentId().getClassId().getClassName()).append("\",")
				.append("\"majorName\":\"").append(schoolGoodStudent.getStudentId().getMajor().getMajorName()).append("\",")
				.append("\"collegeName\":\"").append(schoolGoodStudent.getStudentId().getCollege().getName()).append("\",")
				.append("\"studentNational\":\"").append(schoolGoodStudent.getStudentId().getNational()).append("\",")
				.append("\"studentGenderDic\":\"").append(schoolGoodStudent.getStudentId().getGenderDic().getName()).append("\",")
				.append("\"studentBirthday\":\"").append(sdf.format(schoolGoodStudent.getStudentId().getBrithDate())).append("\",")
				.append("\"studentPoliticalDic\":\"").append(((schoolGoodStudent.getStudentId().getPoliticalDic() != null)?(schoolGoodStudent.getStudentId().getPoliticalDic().getName()):"")).append("\",")
				.append("\"classNumber\":\"").append(this.baseDateService.countStudentByClass(studentInfoModelPO.getClassId().getId()).toString()).append("\",")
				.append("\"honor\":").append(honor).append(",")
				.append("\"performance\":").append(performance).append("}");
		}
		return json.toString();
	}
	/**
	 * 添加、修改 
	 * @param request
	 * @param response
	 * @param schoolGoodStudentVO
	 * @return
	 */
	@RequestMapping("/job/schoolGoodStudent/opt-save/saveSchoolGoodStudent")
	public String saveSchoolGoodStudent(HttpServletRequest request,HttpServletResponse response,SchoolGoodStudent schoolGoodStudentVO){
		String isSubmit = request.getParameter("isSubmit");
		SchoolGoodStudent schoolGoodStudentPO = null;
		if(DataUtil.isNotNull(schoolGoodStudentVO) && DataUtil.isNotNull(schoolGoodStudentVO.getId())){//修改
			schoolGoodStudentPO = this.schoolGoodStudentService.querySchoolGoodStudentById(schoolGoodStudentVO.getId());
			BeanUtils.copyProperties(schoolGoodStudentVO, schoolGoodStudentPO, new String[]{"id","studentId","creator","createTime","schoolYear","status","submitStatus","approveStatus","performance","honor","approveReason","classNumber"});
		}else{//添加
			schoolGoodStudentPO = new SchoolGoodStudent();
			BeanUtils.copyProperties(schoolGoodStudentVO, schoolGoodStudentPO);
			schoolGoodStudentPO.setStatus(Constants.STATUS_NORMAL_DICS);//正常状态
			schoolGoodStudentPO.setSchoolYear(this.studentCommonService.queryStudentById(schoolGoodStudentPO.getStudentId().getId()).getClassId().getGraduatedYearDic());//毕业学年
			schoolGoodStudentPO.setCreator(new User(this.sessionUtil.getCurrentUserId()));//创建人
			this.schoolGoodStudentService.saveSchoolGoodStudent(schoolGoodStudentPO);
		}
		
		if (DataUtil.isNotNull(isSubmit) && DataUtil.isEquals(isSubmit, "0")) {//保存
			schoolGoodStudentPO.setSubmitStatus(Constants.STATUS_SAVE_DICS);
			schoolGoodStudentPO.setApproveStatus("SAVED");//已保存
		}
		
		if (DataUtil.isNotNull(isSubmit) && DataUtil.isEquals(isSubmit, "1")){//提交
			schoolGoodStudentPO.setSubmitStatus(Constants.STATUS_SUBMIT_DICS);
			schoolGoodStudentPO.setApproveStatus("APPROVEING");//审核中
		}
		this.schoolGoodStudentService.updateSchoolGoodStudent(schoolGoodStudentPO);
		return "redirect:/job/schoolGoodStudent/opt-query/querySchoolGoodStudentPage.do";
	}
	/**
	 * 批量提交
	 * @param request
	 * @param response
	 * @param schoolGoodStudentVO
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/job/schoolGoodStudent/opt-save/batchSaveStudentInfo"},produces={"text/plain;charset=UTF-8"})
	public String batchSaveStudentInfo(HttpServletRequest request,HttpServletResponse response,String ids){
		String[] schoolGoodStudentIds = ids.split(",");
		for (String id : schoolGoodStudentIds) {
			SchoolGoodStudent schoolGoodStudentPO = this.schoolGoodStudentService.querySchoolGoodStudentById(id);
			schoolGoodStudentPO.setSubmitStatus(Constants.STATUS_SUBMIT_DICS);
			schoolGoodStudentPO.setApproveStatus("APPROVEING");//审核中
			this.schoolGoodStudentService.updateSchoolGoodStudent(schoolGoodStudentPO);
		}
		return "success";
	}
	/**
	 * 撤销校优秀毕业生
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/job/approveSchoolGoodStudent/opt-cancel/undoSchoolGoodStudent"},produces={"text/plain;charset=UTF-8"})
	public String undoSchoolGoodStudent(HttpServletRequest request,HttpServletResponse response,String id){
		SchoolGoodStudent schoolGoodStudentPO = this.schoolGoodStudentService.querySchoolGoodStudentById(id);
		schoolGoodStudentPO.setApproveStatus("UNDO");//撤销校优秀毕业生
		String approveOpinion = "撤销";
		User approver = new User(this.sessionUtil.getCurrentUserId());
		CommonApproveComments approveComments = new CommonApproveComments(id,approver,approveOpinion,null, new Date(), null);
		this.commonApproveService.saveApproveComments(approveComments);
		this.schoolGoodStudentService.updateSchoolGoodStudent(schoolGoodStudentPO);
		return "success";
	}
	/**
	 * 逻辑删除
	 * @param request
	 * @param response
	 * @param schoolGoodStudentVO
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/job/schoolGoodStudent/opt-del/delSchoolGoodStudent"},produces={"text/plain;charset=UTF-8"})
	public String delSchoolGoodStudent(HttpServletRequest request,HttpServletResponse response,SchoolGoodStudent schoolGoodStudentVO){
		if(DataUtil.isNotNull(schoolGoodStudentVO) && DataUtil.isNotNull(schoolGoodStudentVO.getId())){
			SchoolGoodStudent schoolGoodStudentPO = this.schoolGoodStudentService.querySchoolGoodStudentById(schoolGoodStudentVO.getId());
			schoolGoodStudentPO.setStatus(Constants.STATUS_DELETED_DICS);
			this.schoolGoodStudentService.updateSchoolGoodStudent(schoolGoodStudentPO);
			return "success";
		}
		return "failure";
	}
	/**
	 * 排重：通过学生ID排重,即一个学生只能成为一次优秀毕业生
	 * @param request
	 * @param response
	 * @param schoolGoodStudentVO
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/job/schoolGoodStudent/opt-query/querySchoolGoodStudentByStuId"},produces={"text/plain;charset=UTF-8"})
	public String querySchoolGoodStudentByStuId(HttpServletRequest request,HttpServletResponse response,SchoolGoodStudent schoolGoodStudentVO){
		if(DataUtil.isNotNull(schoolGoodStudentVO) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getId())){
			List<SchoolGoodStudent> schoolGoodStudents = this.schoolGoodStudentService.querySchoolGoodStudentByCond(schoolGoodStudentVO);
			if(schoolGoodStudents != null && schoolGoodStudents.size() > 0){//存在重复数据
				return "success";
			}
		}
		return "failure";
	}
	/**
	 * 查看页面跳转
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/job/schoolGoodStudent/view/viewSchoolGoodStudent")
	public String viewSchoolGoodStudent(ModelMap model,HttpServletRequest request,HttpServletResponse response,SchoolGoodStudent schoolGoodStudentVO){
		if(DataUtil.isNotNull(schoolGoodStudentVO) && DataUtil.isNotNull(schoolGoodStudentVO.getId())){
			SchoolGoodStudent schoolGoodStudent = this.schoolGoodStudentService.querySchoolGoodStudentById(schoolGoodStudentVO.getId());
			model.addAttribute("schoolGoodStudentVO", schoolGoodStudent);
		}
		return Constants.MENUKEY_JOB_SCHOOL_GOOD_STUDENT + "viewSchoolGoodStudent";
	}
	/**
	 * 打印
	 * @param model
	 * @param request
	 * @param response
	 * @param schoolGoodStudentVO
	 * @return
	 */
	@RequestMapping("/job/querySchoolGoodStudent/opt-print/nsm/printSchoolGoodStudent")
	public String printSchoolGoodStudent(ModelMap model,HttpServletRequest request,HttpServletResponse response,SchoolGoodStudent schoolGoodStudentVO){
		if(DataUtil.isNotNull(schoolGoodStudentVO.getId())){
			schoolGoodStudentVO = this.schoolGoodStudentService.querySchoolGoodStudentById(schoolGoodStudentVO.getId());
			if(DataUtil.isNotNull(schoolGoodStudentVO.getPerformance())){
				schoolGoodStudentVO.setPerformance(schoolGoodStudentVO.getPerformance().replaceAll("\r\n", "<br>").replaceAll("\t", "&emsp;"));
			}
			if(DataUtil.isNotNull(schoolGoodStudentVO.getHonor())){
				schoolGoodStudentVO.setHonor(schoolGoodStudentVO.getHonor().replaceAll("\r\n", "<br>").replaceAll("\t", "&emsp;"));
			}
			model.addAttribute("schoolGoodStudentVO", schoolGoodStudentVO);
		}
		return Constants.MENUKEY_JOB_SCHOOL_GOOD_STUDENT + "printSchoolGoodStudent"; 
	}
	/**
	 * 导入页面跳转
	 * @param model
	 * @param request
	 * @param response
	 * @param schoolGoodStudentVO
	 * @return
	 */
	@RequestMapping("/job/schoolGoodStudent/opt-query/importSchoolGoodStudentInit")
	public String importSchoolGoodStudentInit(ModelMap model,HttpServletRequest request,HttpServletResponse response,SchoolGoodStudent schoolGoodStudentVO){
		return Constants.MENUKEY_JOB_SCHOOL_GOOD_STUDENT + "importSchoolGoodStudent"; 
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
	@RequestMapping("/job/schoolGoodStudent/opt-query/importSchoolGoodStudent")
	public String importSchoolGoodStudent(ModelMap model, @RequestParam("file") MultipartFile file,String maxSize, String allowedExt, HttpServletRequest request, HttpSession session){
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
			List<SchoolGoodStudent> schoolGoodStudents = iu.getDataList(filePath, "importSchoolGoodStudent", null, SchoolGoodStudent.class);
			String exit = this.schoolGoodStudentService.saveOrUpdate(schoolGoodStudents,SchoolYearUtil.getYearDic(),new User(this.sessionUtil.getCurrentUserId()));
			if(DataUtil.isNotNull(exit)){
				errorText.add("您导入的数据中已经存在数据库中并且已经处于提交状态，请将其删除后重新提交，以下为冲突数据：<br>");
				String[] exits = exit.split(",");
				for (String e : exits) {
					errorText.add(e);
				}
				model.addAttribute("errorText", errorText);
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
		model.addAttribute("importFlag", Boolean.valueOf(true));
		return Constants.MENUKEY_JOB_SCHOOL_GOOD_STUDENT + "importSchoolGoodStudent";
	}
	/**
	 * 返回查询列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/job/schoolGoodStudent/opt-query/backUp")
	public String backUp(HttpServletRequest request,HttpServletResponse response,HttpSession session){
		session.removeAttribute("exitSchoolGoodStudents");
		return "redirect:/job/schoolGoodStudent/opt-query/querySchoolGoodStudentPage.do";
	}
	/**
	 * 计算导出页数
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/job/schoolGoodStudent/nsm/exportSchoolGoodStudentPage")
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
		return Constants.MENUKEY_JOB_SCHOOL_GOOD_STUDENT + "exportSchoolGoodStudent";
	}
	/**
	 * 按查询条件导出Excel
	 * @param modelMap
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/job/opt-export/exportSchoolGoodStudentPage")
	public void exportSchoolGoodStudent(ModelMap model, HttpServletRequest request,HttpServletResponse response, SchoolGoodStudent schoolGoodStudentVO,String userQuery_exportSize,String userQuery_exportPage){
		int pageSize = userQuery_exportSize != null ? Integer.parseInt(userQuery_exportSize) : 25000;
		int pageNo = userQuery_exportPage != null ? Integer.parseInt(userQuery_exportPage) : 1;
		String orgId = (String)request.getSession().getAttribute("_teacher_orgId");
		//判断用户是否为招就处
		boolean isJobOffice = true;
		if(CheckUtils.isCurrentOrgEqCollege(orgId)){
			isJobOffice = false;
		}
		if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId())){
			if(!isJobOffice){
				BaseAcademyModel college = new BaseAcademyModel();
				college.setId(orgId);
				schoolGoodStudentVO.getStudentId().setCollege(college);
			}
		}else{
			if(!isJobOffice){
				StudentInfoModel studentInfoModel = new StudentInfoModel();
				BaseAcademyModel college = new BaseAcademyModel();
				college.setId(orgId);
				studentInfoModel.setCollege(college);
				schoolGoodStudentVO.setStudentId(studentInfoModel);
			}
		}
//		schoolGoodStudentVO.setApproveStatus("PASS");
		List<SchoolGoodStudent> schoolGoodStudents = (List<SchoolGoodStudent>) this.schoolGoodStudentService.querySchoolGoodStudentPage(schoolGoodStudentVO, pageSize, pageNo,Constants.schoolGoodStudentTypeByPass).getResult();
		List exportDataList = new ArrayList();
		for (int i = 0; i < schoolGoodStudents.size(); i++) {
			Map<String,Object> map = new HashMap<String, Object>();
			SchoolGoodStudent schoolGoodStudent = schoolGoodStudents.get(i);
			if(DataUtil.isNotNull(schoolGoodStudent.getStudentId())){
				map.put("stuName", schoolGoodStudent.getStudentId().getId());
				if(DataUtil.isNotNull(schoolGoodStudent.getStudentId().getCollege()) && DataUtil.isNotNull(schoolGoodStudent.getStudentId().getCollege().getName())){
					map.put("collegeName", schoolGoodStudent.getStudentId().getCollege().getName());
				}
				if(DataUtil.isNotNull(schoolGoodStudent.getStudentId().getMajor()) && DataUtil.isNotNull(schoolGoodStudent.getStudentId().getMajor().getMajorName())){
					map.put("majorName", schoolGoodStudent.getStudentId().getMajor().getMajorName());
				}
				if(DataUtil.isNotNull(schoolGoodStudent.getStudentId().getClassId()) && DataUtil.isNotNull(schoolGoodStudent.getStudentId().getClassId().getClassName())){
					map.put("className", schoolGoodStudent.getStudentId().getClassId().getClassName());
				}
				if(DataUtil.isNotNull(schoolGoodStudent.getStudentId().getName())){
					map.put("studentName", schoolGoodStudent.getStudentId().getName());
				}
				if(DataUtil.isNotNull(schoolGoodStudent.getStudentId().getGenderDic())){
					map.put("sex", schoolGoodStudent.getStudentId().getGenderDic().getName());
				}
				if(DataUtil.isNotNull(schoolGoodStudent.getStudentId().getPoliticalDic())){
					map.put("political", schoolGoodStudent.getStudentId().getPoliticalDic().getName());
				}
				if(DataUtil.isNotNull(schoolGoodStudent.getPost())){
					map.put("post", schoolGoodStudent.getPost());
				}
				if(DataUtil.isNotNull(schoolGoodStudent.getAvgScore())){
					map.put("avgScore", String.format("%.2f",schoolGoodStudent.getAvgScore()));
				}
				if(DataUtil.isNotNull(schoolGoodStudent.getClassSort())){
					map.put("classSort", schoolGoodStudent.getClassSort());
				}
				if(DataUtil.isNotNull(schoolGoodStudent.getComputerLevel())){
					map.put("computerLevel", schoolGoodStudent.getComputerLevel());
				}
				if(DataUtil.isNotNull(schoolGoodStudent.getEnglishLevel())){
					map.put("englishLevel", schoolGoodStudent.getEnglishLevel());
				}
				if(DataUtil.isNotNull(schoolGoodStudent.getApproveStatus())){
					map.put("approveStatus", Constants.getSchoolGoodStudentByPassMap().get(schoolGoodStudent.getApproveStatus()));//审核状态
				}
			}
			exportDataList.add(map);
		}
		HSSFWorkbook wb;
		try {
			wb = this.excelService.exportData("export_school_good_student.xls","exportSchoolGoodStudent", exportDataList);
			String filename = "";
			if(DataUtil.isNotNull(schoolGoodStudentVO) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId())){
				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege().getId())){
					filename += this.baseDateService.findAcademyById(schoolGoodStudentVO.getStudentId().getCollege().getId()).getName();
				}
				if(DataUtil.isNotNull(schoolGoodStudentVO.getSchoolYear()) && DataUtil.isNotNull(schoolGoodStudentVO.getSchoolYear().getId())){
					filename += this.dicService.getDic(schoolGoodStudentVO.getSchoolYear().getId()).getCode() + "届";
				}
			}
			filename += "校级优秀毕业生汇总表" + ".xls";
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
	 * 审核列表页面
	 * @param model
	 * @param request
	 * @param response
	 * @param schoolGoodStudentVO
	 * @return
	 */
	@RequestMapping("/job/approveSchoolGoodStudent/opt-query/approveSchoolGoodStudentPage")
	public String approveSchoolGoodStudentPage(ModelMap model,HttpServletRequest request,HttpServletResponse response,SchoolGoodStudent schoolGoodStudentVO){
		String strPageNo = request.getParameter("pageNo");
		int pageNo = DataUtil.isNotNull(strPageNo) ? Integer.parseInt(strPageNo) : 1;
		List<BaseAcademyModel> colleges =  this.baseDateService.listBaseAcademy();
		List<BaseMajorModel> majors = new ArrayList<BaseMajorModel>();
		List<BaseClassModel> classes = new ArrayList<BaseClassModel>();
		List<Dic> years = this.dicUtil.getDicInfoList("YEAR");//学年
		//数据回显 查询专业信息
		if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege().getId())){
			majors = this.compService.queryMajorByCollage(schoolGoodStudentVO.getStudentId().getCollege().getId());
			if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getMajor()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getMajor().getId())){
				classes = this.compService.queryClassByMajor(schoolGoodStudentVO.getStudentId().getMajor().getId());
			}
		}
		if(!DataUtil.isNotNull(schoolGoodStudentVO.getSchoolYear())){//添加默认学年
			schoolGoodStudentVO.setSchoolYear(SchoolYearUtil.getYearDic());
		}
		schoolGoodStudentVO.setSubmitStatus(Constants.STATUS_SUBMIT_DICS);
		Page page = this.schoolGoodStudentService.querySchoolGoodStudentPage(schoolGoodStudentVO,Page.DEFAULT_PAGE_SIZE,pageNo,Constants.schoolGoodStudentTypeByAll);
		model.addAttribute("colleges", colleges);
		model.addAttribute("majors", majors);
		model.addAttribute("classes", classes);
		model.addAttribute("years", years);
		model.addAttribute("page", page);
		model.addAttribute("schoolGoodStudentVO", schoolGoodStudentVO);
		model.addAttribute("approveMap", Constants.getApproveProcessStatusBySchoolGood());
		return Constants.MENUKEY_JOB_SCHOOL_GOOD_STUDENT + "approve/approveSchoolGoodStudentPage";
	}
	/**
	 * 批量审批跳转页面
	 * @param model
	 * @param request
	 * @param response
	 * @param schoolGoodStudentVO
	 * @return
	 */
	@RequestMapping("/job/schoolGoodStudent/view/approveSchoolGoodStudentList")
	public String approveSchoolGoodStudentList(ModelMap model,HttpServletRequest request,HttpServletResponse response,String[] ids){
		List<SchoolGoodStudent> schoolGoodStudentList = this.schoolGoodStudentService.querySchoolGoodStudentByIds(ids);
		model.addAttribute("schoolGoodStudentList", schoolGoodStudentList);
		return Constants.MENUKEY_JOB_SCHOOL_GOOD_STUDENT + "approve/approveSchoolGoodStudentList";
	}
	/**
	 * 批量审批保存
	 * @param model
	 * @param request
	 * @param response
	 * @param schoolGoodStudentVO
	 * @return
	 */
	@RequestMapping("/job/approveSchoolGoodStudent/opt-save/saveBatchSchoolGoodStudent")
	public String saveBatchSchoolGoodStudent(ModelMap model,HttpServletRequest request,HttpServletResponse response,String[] ids,String approveStatus,String approveReason){
		this.schoolGoodStudentService.updateApproveStateStudent(ids,approveStatus,approveReason);
		String approveOpinion = "";
		if(DataUtil.isEquals(approveStatus, "PASS")){
			approveOpinion = "通过";
		}else{
			approveOpinion = "拒绝";
		}
		User approver = new User(this.sessionUtil.getCurrentUserId());
		for (String id : ids) {
			CommonApproveComments approveComments = new CommonApproveComments(id,approver,approveOpinion,approveReason, new Date(), null);
			this.commonApproveService.saveApproveComments(approveComments);
		}
		return "redirect:/job/approveSchoolGoodStudent/opt-query/approveSchoolGoodStudentPage.do";
	}
	/**
	 * 保存单个审批结果
	 * @param model
	 * @param request
	 * @param response
	 * @param schoolGoodStudentVO
	 * @return
	 */
	@RequestMapping("/job/approveSchoolGoodStudent/opt-save/saveApproveSchoolGoodStudent")
	public String saveApproveSchoolGoodStudent(ModelMap model,HttpServletRequest request,HttpServletResponse response,SchoolGoodStudent schoolGoodStudentVO){
		if (DataUtil.isNotNull(schoolGoodStudentVO) && DataUtil.isNotNull(schoolGoodStudentVO.getId()) && DataUtil.isNotNull(schoolGoodStudentVO.getApproveStatus())) {
			
			SchoolGoodStudent schoolGoodStudentPO = this.schoolGoodStudentService.querySchoolGoodStudentById(schoolGoodStudentVO.getId());
			schoolGoodStudentPO.setApproveStatus(schoolGoodStudentVO.getApproveStatus());
			schoolGoodStudentPO.setApproveReason(schoolGoodStudentVO.getApproveReason());
			this.schoolGoodStudentService.updateSchoolGoodStudent(schoolGoodStudentPO);//保存审核结果
			//
			String approveOpinion = "";
			if(DataUtil.isEquals(schoolGoodStudentVO.getApproveStatus(), "PASS")){
				approveOpinion = "通过";
			}else{
				approveOpinion = "拒绝";
			}
			CommonApproveComments approveComments = new CommonApproveComments(schoolGoodStudentPO.getId(),new User(this.sessionUtil.getCurrentUserId()),approveOpinion, schoolGoodStudentVO.getApproveReason(), new Date(), null);
			this.commonApproveService.saveApproveComments(approveComments);
		}
		return "redirect:/job/approveSchoolGoodStudent/opt-query/approveSchoolGoodStudentPage.do";
	}
	/**
	 * 统计优秀毕业生
	 * @param model
	 * @param request
	 * @param response
	 * @param schoolGoodStudentVO
	 * @return
	 */
	@RequestMapping("/job/statSchoolGoodStudent/opt-query/statSchoolGoodStudentPage")
	public String statSchoolGoodStudentPage(ModelMap model,HttpServletRequest request,HttpServletResponse response,SchoolGoodStudent schoolGoodStudentVO){
		String statByWay = request.getParameter("statByWay");//统计范围
		String strPageNo = request.getParameter("pageNo");
		int pageNo = DataUtil.isNotNull(strPageNo) ? Integer.parseInt(strPageNo) : 1;
		String orgId = (String)request.getSession().getAttribute("_teacher_orgId");
		//判断用户是否为招就处
		boolean isJobOffice = true;
		if(CheckUtils.isCurrentOrgEqCollege(orgId)){
			isJobOffice = false;
		}
		if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId())){
			if(!isJobOffice){
				BaseAcademyModel college = new BaseAcademyModel();
				college.setId(orgId);
				schoolGoodStudentVO.getStudentId().setCollege(college);
			}
		}else{
			if(!isJobOffice){
				StudentInfoModel studentInfoModel = new StudentInfoModel();
				BaseAcademyModel college = new BaseAcademyModel();
				college.setId(orgId);
				studentInfoModel.setCollege(college);
				schoolGoodStudentVO.setStudentId(studentInfoModel);
			}
		}
		//判断统计范围，默认为按学院统计
		if(DataUtil.isNull(statByWay)){
			statByWay = "byCollege";
		}
		if(!DataUtil.isNotNull(schoolGoodStudentVO.getSchoolYear())){
			Dic nowSchoolYear = SchoolYearUtil.getYearDic();//当前学年
			schoolGoodStudentVO.setSchoolYear(nowSchoolYear);
			model.addAttribute("nowSchoolYear", nowSchoolYear);
		}
		List<Dic> years = this.dicUtil.getDicInfoList("YEAR");//学年
		List<BaseAcademyModel> colleges =  this.baseDateService.listBaseAcademy();//学院
		List<BaseMajorModel> majors = new ArrayList<BaseMajorModel>();
		List<BaseClassModel> classes = new ArrayList<BaseClassModel>();
		Map<String,String> statisticalWays = Constants.getStatisticalWays();
		model.addAttribute("years", years);
		model.addAttribute("statByWay", statByWay);
		model.addAttribute("isJobOffice", isJobOffice);
		model.addAttribute("colleges", colleges);
		model.addAttribute("statisticalWays", statisticalWays);
		model.addAttribute("schoolGoodStudentVO", schoolGoodStudentVO);
		if(DataUtil.isEquals("byCollege", statByWay)){
			if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege().getId())){
				majors = this.compService.queryMajorByCollage(schoolGoodStudentVO.getStudentId().getCollege().getId());
			}
			List<SchoolGoodStudentCollegeView> collegeView = this.schoolGoodStudentService.statSchoolGoodStudentByCollege(schoolGoodStudentVO);
			model.addAttribute("page", collegeView);
			model.addAttribute("majors", majors);
			return Constants.MENUKEY_JOB_SCHOOL_GOOD_STUDENT + "statistics/statSchoolGoodStudentByCollege";
		}
		if(DataUtil.isEquals("byMajor", statByWay)){//按专业查询时只需要添加专业查询条件
			List<SchoolGoodStudentMajorView> majorView = this.schoolGoodStudentService.statSchoolGoodStudentByMajor(schoolGoodStudentVO);
			if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege().getId())){
				majors = this.compService.queryMajorByCollage(schoolGoodStudentVO.getStudentId().getCollege().getId());
			}
			model.addAttribute("majors", majors);
			model.addAttribute("page", majorView);
			return Constants.MENUKEY_JOB_SCHOOL_GOOD_STUDENT + "statistics/statSchoolGoodStudentByMajor";
		}
		if(DataUtil.isEquals("byClass", statByWay)){//按班级查询时需要添加专业和班级查询条件
			List<SchoolGoodStudentClassView> classView = this.schoolGoodStudentService.statSchoolGoodStudentByClass(schoolGoodStudentVO);
			if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege().getId())){
				majors = this.compService.queryMajorByCollage(schoolGoodStudentVO.getStudentId().getCollege().getId());
				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getMajor()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getMajor().getId())){
					classes = this.compService.queryClassByMajor(schoolGoodStudentVO.getStudentId().getMajor().getId());
				}
			}
			model.addAttribute("majors", majors);
			model.addAttribute("classes", classes);
			model.addAttribute("page", classView);
			return Constants.MENUKEY_JOB_SCHOOL_GOOD_STUDENT + "statistics/statSchoolGoodStudentByClass";
		}
		return Constants.MENUKEY_JOB_SCHOOL_GOOD_STUDENT + "statistics/statSchoolGoodStudentByCollege";
	}
	/**
	 * 查询评奖评优的毕业生
	 * @param model
	 * @param request
	 * @param student
	 * @param selectedStudentId
	 * @param formId
	 * @param queryFlag
	 * @return
	 */
	@RequestMapping(value={"/job/schoolGoodStudent/nsm/queryAwardGraduateStudent"})
	public String queryAwardGraduateStudent(ModelMap model, HttpServletRequest request,StudentInfoModel student,String selectedStudentId,String formId,String queryFlag){
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		
		Page page =  this.rewardCommonService.getStuAllAwardPage(student,pageNo,5);
		model.addAttribute("page", page);
		Collection<StudentInfoModel> list = page.getResult();
		for( StudentInfoModel stu : list ){
			stu.setStudentInfo(
					new StringBuffer()
					.append("{id:'").append(stu.getId()).append("',")
					.append("name:'").append(stu.getName()).append("',")
					.append("bankCode:'").append(stu.getBankCode()).append("',")
					.append("className:'").append(stu.getClassId().getClassName()).append("',")
					.append("classId:'").append(stu.getClassId().getId()).append("',")
					.append("majorId:'").append(stu.getMajor().getId()).append("',")
					.append("majorName:'").append(stu.getMajor().getMajorName()).append("',")
					.append("genderId:'").append(stu.getGenderDic().getId()).append("',")
					.append("genderName:'").append(stu.getGenderDic().getName()).append("',")
					.append("sourceLandId:'").append(stu.getSourceLand()).append("',")
					.append("sourceLandName:'").append(stu.getSourceLand()).append("',")
					.append("nativeId:'").append(null == stu.getNativeDic() ? "" : stu.getNativeDic().getId()).append("',")
					.append("nativeName:'").append(null == stu.getNativeDic() ? "" : stu.getNativeDic().getName()).append("',")
					.append("birthDay:'").append(stu.getBrithDate()).append("',")
					.append("collegeId:'").append(stu.getCollege().getId()).append("',")
					.append("collegeName:'").append(stu.getCollege().getName()).append("'}")
					.toString()
			);
		}
		model.addAttribute("selectedId", selectedStudentId);
		model.addAttribute("formId", formId);
		model.addAttribute("queryFlag", queryFlag);
		model.addAttribute("student", student);
		return Constants.MENUKEY_JOB_SCHOOL_GOOD_STUDENT + "comp/awardGraduateStudentTable";
	}
	/**
	 * 得到上传文件的大小 2MB
	 * @return
	 */
	private int setMaxSize() {
		return 20971520;
	}
}
