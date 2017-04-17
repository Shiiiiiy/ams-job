package com.uws.job.util;

import com.uws.sys.model.Dic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.uws.common.util.SchoolYearUtil;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;

/**
 *就业管理通用常量
 * @author liuchen
 *
 */
public class Constants {
	
	/**
	 * 数据字典工具类
	 */
	private static DicUtil dicUtil=DicFactory.getDicUtil();
	
	/**
	 *就业信息管理维护返回页面公共路径
	 */
	public static final String MENUKEY_JOB_EMPLOYMENT = "/job/employment";
	
	/**
	 * 就业方案管理维护返回页面公共路径
	 */
	public static final String MENUKEY_JOB_EMPLOYMENT_SCHEME = "/job/scheme/";
	
	/**
	 * 就业协议书管理返回页面公共路径
	 */
	public static final String MENUKEY_AGREEMENT_INFO = "/job/agreement";
	
	/**
	 * 报到证管理返回页面公共路径
	 */
	public static final String MENUKEY_REGISTER_INFO = "/job/register";
	
	/**
	 * 就业协议书审核返回页面公共路径
	 */
	public static final String MENUKEY_AGREEMENT_APPROVE = "/job/agreement/approve";
	
	/**
	 * 报到证审核返回页面公共路径
	 */
	public static final String MENUKEY_REGISTER_APPROVE = "/job/register/approve";
	
	/**
	 * 企业信息库管理公共路径
	 */
	public static final String MENUKEY_JOB_COMPANY_INFO = "/job/company/";
	
	/**
	 * 招聘会信息返回页面公共路径
	 */
	public static final String MENUKEY_RECRUIT_INFO = "/job/recruit";
	
	/**
	 * 校优秀毕业生评选公共路径
	 */
	public static final String MENUKEY_JOB_SCHOOL_GOOD_STUDENT = "/job/schoolGoodStudent/";
	/**
	 * 省优秀毕业生评选公共路径
	 */
	public static final String MENUKEY_JOB_PROVINCE_GOOD_STUDENT = "/job/province/";
	
	/**
	 * 省优秀毕业生统计公共路径
	 */
	public static final String MENUKEY_JOB_COUNT_PROVINCE_GOOD_STUDENT = "/job/province/count/";
	/**
	 *双困生管理维护返回页面公共路径
	 */
	public static final String MENUKEY_JOB_POORSTUDENT = "/job/poorStudent";
	
	/**
	 *双困生统计返回页面公共路径
	 */
	public static final String MENUKEY_JOB_COUNT_POORSTUDENT = "/job/poorStudent/count/";
	
	/**
	 * 【系统数据字典_逻辑删除_正常状态】
	 */
	public static final Dic STATUS_NORMAL_DICS=dicUtil.getDicInfo("STATUS_NORMAL_DELETED","NORMAL");
	
	/**
	 * 【系统数据字典_逻辑删除_删除状态】
	 */
	public static final Dic STATUS_DELETED_DICS=dicUtil.getDicInfo("STATUS_NORMAL_DELETED","DELETED");
	
	/**
	 * 【系统数据字典_双困生状态_保存_提交_审核通过_审核不通过】
	 */
	public static final Dic STATUS_SAVE_DIC=dicUtil.getDicInfo("POOR_STUDENT_STATUS","SAVE");
	public static final Dic STATUS_SUBMIT_DIC=dicUtil.getDicInfo("POOR_STUDENT_STATUS","SUBMIT");
	public static final Dic STATUS_PASS_DIC=dicUtil.getDicInfo("POOR_STUDENT_STATUS","PASS");
	public static final Dic STATUS_UNPASS_DIC=dicUtil.getDicInfo("POOR_STUDENT_STATUS","UNPASS");
	
	/**
	 * 【省优秀毕业生申请状态】
	 */
	public static final Dic STATUS_APPLY_DIC=dicUtil.getDicInfo("APPLY_TYPE","APPLY");
	public static final Dic STATUS_UNAPPLY_DIC=dicUtil.getDicInfo("APPLY_TYPE","UN_APPLY");
	public static final Dic STATUS_APPLY_PASS_DIC=dicUtil.getDicInfo("APPLY_TYPE","APPLY_PASS");
	public static final Dic STATUS_APPLY_UNPASS_DIC=dicUtil.getDicInfo("APPLY_TYPE","APPLY_UNPASS");
	public static final Dic STATUS_APPLY_UNDO_DIC=dicUtil.getDicInfo("APPLY_TYPE","UN_DO");
	
	
	/**
	 * 通过数据字典工具类拿到学年列表
	 */
	public static final  List<Dic> YEAR_LIST = dicUtil.getDicInfoList("YEAR");
	
	/**
	 * 获取当前学年
	 */
	public static final Dic CURRENT_YEAR =  SchoolYearUtil.getYearDic();
	
	/**
	 * 【系统数据字典_补办状态_状态列表】
	 */
	public static final List<Dic> STATUS_REPLACE_LIST= dicUtil.getDicInfoList("STATUS_REPLACE");
	
	/**
	 * 【系统数据字典_补办状态_已提交】
	 */
	public static final Dic DIC_SUBMIT_STATUS = dicUtil.getDicInfo("STATUS_REPLACE","SUBMIT");
	
	/**
	 * 【系统数据字典_补办状态_已通过】
	 */
	public static final Dic DIC_PASS_STATUS = dicUtil.getDicInfo("STATUS_REPLACE","PASS");
	
	/**
	 * 【系统数据字典_补办状态_已拒绝 】
	 */
	public static final Dic DIC_REFUSE_STATUS = dicUtil.getDicInfo("STATUS_REPLACE","REFUSE");
	
	
	/**
	 * 【系统数据字典_补办状态_已撤销】
	 */
	public static final Dic DIC_CANCEL_STATUS = dicUtil.getDicInfo("STATUS_REPLACE","CANCEL");
	
	
	/**
	 * 【系统数据字典_保存状态_保存】
	 */
	public static final Dic STATUS_SAVE_DICS=dicUtil.getDicInfo("STATUS","SAVE");
	
	/**
	 * 【系统数据字典_保存状态_提交】
	 */
	public static final Dic STATUS_SUBMIT_DICS=dicUtil.getDicInfo("STATUS","SUBMIT");
	
	/**
	 * 【统计范围】
	 * @return
	 */
	public static Map<String,String> getStatisticalWays(){
		Map<String,String> statisticalWays = new LinkedHashMap<String, String>();
		statisticalWays.put("byCollege", "学院");
		statisticalWays.put("byMajor", "专业");
		statisticalWays.put("byClass", "班级");
		return statisticalWays;
	}
	/**
	 * 招就处ID
	 */
	public static final String JOBOFFICEID = "05";
	
	/**
	 * 
	 * @Title: Constants.java 
	 * @Package com.uws.job.util 
	 * @Description: 双困生审核状态
	 * @author LiuChen 
	 * @date 2015-12-8 下午3:01:46
	 */
	public static Map<String,String> getApproveProcessStatus()
	{
		Map<String,String> statusMap = new HashMap<String,String>();
		statusMap.put("SUBMIT", "待审核");
		statusMap.put("PASS", "审核通过");
		statusMap.put("UNPASS", "审核拒绝");
		return statusMap;
	}
	
	
	public static Map<String,String> getApproveProvinceProcessStatus()
	{
		Map<String,String> statusMap = new HashMap<String,String>();
		statusMap.put("APPLY", "待审核");
		statusMap.put("UN_DO", "已撤销");
		statusMap.put("APPLY_UNPASS", "审核拒绝");
		statusMap.put("APPLY_PASS", "审核通过");
		return statusMap;
	}
	/**
	 * 校优查询列表状态查询条件
	 * @return
	 */
	public static Map<String,String> getSchoolGoodStudentByPassMap(){
		Map<String,String> approveMap = new HashMap<String, String>();
		approveMap.put("PASS", "审核通过");
		approveMap.put("UNDO", "已撤销");
		return approveMap;
	}
	/**
	 * 校优申请查询列表状态查询条件
	 * @return
	 */
	public static Map<String,String> getApproveMap(){
		Map<String,String> approveMap = new HashMap<String, String>();
		approveMap.put("SAVED", "已保存");
		approveMap.put("APPROVEING", "审核中");
		approveMap.put("PASS", "审核通过");
		approveMap.put("REJECT", "审核拒绝");
		approveMap.put("UNDO", "已撤销");
		return approveMap;
	}
	/**
	 * 校优审核列表状态查询条件
	 * @return
	 */
	public static Map<String,String> getApproveProcessStatusBySchoolGood(){
		Map<String,String> approveMap = new HashMap<String, String>();
		approveMap.put("APPROVEING", "待审核");
		approveMap.put("PASS", "审核通过");
		approveMap.put("REJECT", "审核拒绝");
		approveMap.put("UNDO", "已撤销");
		return approveMap;
	}
	/**
	 * 校优分页查询方式
	 */
	public static int schoolGoodStudentTypeByAll = 1;
	public static int schoolGoodStudentTypeByPass = 2;
	/**
	 * 将String类型的数据转换成Date类型
	 */
	public static final Date convertStringToDate(String time){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dateTime=null;
		try {
			dateTime = sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateTime;
	}
}
