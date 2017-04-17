package com.uws.job.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.common.dao.IBaseDataDao;
import com.uws.common.dao.IEvaluationCommonDao;
import com.uws.common.dao.IRewardCommonDao;
import com.uws.common.dao.IStudentCommonDao;
import com.uws.common.util.JsonUtils;
import com.uws.common.util.SchoolYearUtil;
import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.job.SchoolGoodStudent;
import com.uws.domain.job.SchoolGoodStudentClassView;
import com.uws.domain.job.SchoolGoodStudentCollegeView;
import com.uws.domain.job.SchoolGoodStudentMajorView;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.reward.AwardInfo;
import com.uws.domain.reward.AwardType;
import com.uws.domain.reward.CollegeAwardInfo;
import com.uws.domain.reward.CountryBurseInfo;
import com.uws.domain.reward.StudentApplyInfo;
import com.uws.job.dao.ISchoolGoodStudentDao;
import com.uws.job.service.ISchoolGoodStudentService;
import com.uws.job.util.Constants;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.user.model.User;

/**
 * @className SchoolGoodStudentServiceImpl.java
 * @package com.uws.job.service.impl
 * @description
 * @date 2015-11-6  下午5:06:57
 */
@Service
public class SchoolGoodStudentServiceImpl extends BaseServiceImpl implements ISchoolGoodStudentService {
	@Autowired
	private ISchoolGoodStudentDao schoolGoodStudentDao;
	@Autowired
	private IStudentCommonDao studentCommonDao;
	@Autowired
	private IRewardCommonDao rewardCommonDao;
	@Autowired
	public IEvaluationCommonDao evaluationCommonDao;
	@Autowired
	public IBaseDataDao baseDataDao;
	
	private DicUtil dicUtil = DicFactory.getDicUtil();
	/**
	 * 分页查询
	 * @param schoolGoodStudentVO
	 * @param dEFAULT_PAGE_SIZE
	 * @param pageNo
	 * @param queryType  查询方式  1：在未删除的基础数据上查询，2：是在审核通过和撤销的基础数据上查询
	 * @return
	 */
	@Override
	public Page querySchoolGoodStudentPage(SchoolGoodStudent schoolGoodStudentVO, int dEFAULT_PAGE_SIZE,int pageNo,int queryType) {
		return this.schoolGoodStudentDao.querySchoolGoodStudentPage(schoolGoodStudentVO,dEFAULT_PAGE_SIZE,pageNo,queryType);
	}
	/**
	 * 添加
	 * @param schoolGoodStudentPO
	 */
	@Override
	public void saveSchoolGoodStudent(SchoolGoodStudent schoolGoodStudentPO) {
		this.schoolGoodStudentDao.save(schoolGoodStudentPO);
	}
	/**
	 * 通过ID查询
	 * @param id
	 * @return
	 */
	@Override
	public SchoolGoodStudent querySchoolGoodStudentById(String id) {
		return (SchoolGoodStudent) this.schoolGoodStudentDao.get(SchoolGoodStudent.class, id);
	}
	/**
	 * 修改
	 * @param schoolGoodStudentPO
	 */
	@Override
	public void updateSchoolGoodStudent(SchoolGoodStudent schoolGoodStudentPO) {
		this.schoolGoodStudentDao.update(schoolGoodStudentPO);
	}
	/**
	 * 条件查询
	 * @param schoolGoodStudentVO
	 * @return
	 */
	@Override
	public List<SchoolGoodStudent> querySchoolGoodStudentByCond(SchoolGoodStudent schoolGoodStudentVO) {
		return this.schoolGoodStudentDao.querySchoolGoodStudentByCond(schoolGoodStudentVO);
	}
	/**
	 * 保存或更新，如数据库中不存在则添加，如数据库中存在则更新
	 * @param schoolGoodStudents
	 * @param schoolYear
	 * @param creator
	 */
	@Override
	public String saveOrUpdate(List<SchoolGoodStudent> schoolGoodStudents,Dic schoolYear,User creator){
		String exits = "";
		boolean isExits = false;
		SimpleDateFormat sdfyear = new SimpleDateFormat("yyyy");
		for (SchoolGoodStudent schoolGoodStudent : schoolGoodStudents) {
			List<SchoolGoodStudent> exitsSchoolGoodStudents = this.schoolGoodStudentDao.querySchoolGoodStudentsByStuId(schoolGoodStudent.getStuId());
			//判断已经存在数据是否已经提交，如果已经提交则提示用户，并且数据不会保存到数据库中
			if(exitsSchoolGoodStudents != null && exitsSchoolGoodStudents.size() > 0){
				if(DataUtil.isEquals("SUBMIT", exitsSchoolGoodStudents.get(0).getSubmitStatus().getCode())){
//					if(!DataUtil.isEquals("UNDO", exitsSchoolGoodStudents.get(0).getApproveStatus())){
						isExits = true;
						exits += schoolGoodStudent.getStuId() + ",";
//					}
				}
			}
		}
		if(!isExits){
			for (SchoolGoodStudent schoolGoodStudent : schoolGoodStudents) {
				List<SchoolGoodStudent> exitsSchoolGoodStudents = this.schoolGoodStudentDao.querySchoolGoodStudentsByStuId(schoolGoodStudent.getStuId());
				if(exitsSchoolGoodStudents != null && exitsSchoolGoodStudents.size() > 0){//已经存在且是未提交状态的则覆盖更新
					BeanUtils.copyProperties(schoolGoodStudent, exitsSchoolGoodStudents.get(0), new String[]{"id","studentId","creator","createTime","schoolYear","status","submitStatus","approveStatus","performance","honor","approveReason","classNumber"});
					this.schoolGoodStudentDao.update(exitsSchoolGoodStudents.get(0));
				}else{//数据库中不存在该数据，直接添加
					StudentInfoModel studentInfoModel = (StudentInfoModel) this.studentCommonDao.get(StudentInfoModel.class, schoolGoodStudent.getStuId());
					schoolGoodStudent.setStudentId(studentInfoModel);//设置学生
					schoolGoodStudent.setStatus(Constants.STATUS_NORMAL_DICS);//正常状态
					schoolGoodStudent.setSubmitStatus(Constants.STATUS_SAVE_DICS);//保存
					schoolGoodStudent.setSchoolYear(studentInfoModel.getClassId().getGraduatedYearDic());//毕业学年
					schoolGoodStudent.setClassNumber(this.baseDataDao.countStudentByClass(studentInfoModel.getClassId().getId()));//班级人数
					schoolGoodStudent.setApproveStatus("SAVED");//已保存
					schoolGoodStudent.setCreator(creator);//创建人
					schoolGoodStudent.setHonor(this.formatAward(this.getStuAllAward(studentInfoModel)));//奖助信息
					String startSchoolYear = sdfyear.format(studentInfoModel.getEnterDate());//入学年份
					String endSchoolYear = studentInfoModel.getClassId().getGraduatedYearDic().getCode();//毕业年份
					String allEvaluationInfos = this.getAllEvaluationInfos(startSchoolYear, endSchoolYear, studentInfoModel);
					schoolGoodStudent.setPerformance(this.formatEvaluationInfos(allEvaluationInfos));//测评信息
					this.schoolGoodStudentDao.save(schoolGoodStudent);
				}
			}
		}
		if(isExits){
			return exits.substring(0, exits.length()-1);
		}
		return null;
	}
	/**
	 * 通过学院统计校优人数
	 * @return
	 */
	@Override
	public List<SchoolGoodStudentCollegeView> statSchoolGoodStudentByCollege(SchoolGoodStudent schoolGoodStudentVO) {
		return this.schoolGoodStudentDao.statSchoolGoodStudentByCollege(schoolGoodStudentVO);
	}
	/**
	 * 按照专业统计
	 * @return
	 */
	@Override
	public List<SchoolGoodStudentMajorView> statSchoolGoodStudentByMajor(SchoolGoodStudent schoolGoodStudentVO) {
		return this.schoolGoodStudentDao.statSchoolGoodStudentByMajor(schoolGoodStudentVO);
	}
	/**
	 * 按照班级统计
	 * @return
	 */
	@Override
	public List<SchoolGoodStudentClassView> statSchoolGoodStudentByClass(SchoolGoodStudent schoolGoodStudentVO) {
		return this.schoolGoodStudentDao.statSchoolGoodStudentByClass(schoolGoodStudentVO);
	}
	/**
	 * 获取某个学生的全部奖助信息
	 * @param student
	 * @return
	 */
	@Override
	public String getStuAllAward(StudentInfoModel student) {
		StringBuffer awardbf = new StringBuffer("[");
		List<CountryBurseInfo> listBurse= this.rewardCommonDao.getStuBurseList(student);//国家奖助
		StudentApplyInfo stuApply = new StudentApplyInfo();
		stuApply.setStudentId(student);
		List<StudentApplyInfo> listAwardInfo = this.rewardCommonDao.getStuInfoList(stuApply);//学生评奖评优信息
		List<CollegeAwardInfo> listcollegeAwardInfo = this.rewardCommonDao.getStuCollegeAwardList(student);//校内奖励
		List<StudentApplyInfo> listAward = new ArrayList<StudentApplyInfo>();
		if(listAwardInfo!=null &&listAwardInfo.size()>0){
			for(int i=0;i<listAwardInfo.size();i++){
				if(listAwardInfo.get(i).getAwardTypeId()!=null){
					AwardType awardType = rewardCommonDao.getAwardTypeById(listAwardInfo.get(i).getAwardTypeId().getId());
					StudentApplyInfo studentApplyInfo = new StudentApplyInfo();
					BeanUtils.copyProperties(listAwardInfo.get(i), studentApplyInfo);
					if(DataUtil.isNotNull(awardType.getSecondAwardName())) {
						studentApplyInfo.setId(awardType.getAwardInfoId().getAwardName() + awardType.getSecondAwardName().getName());//存放在id中,方便自己页面获取
					}else{
						AwardInfo awardInfo = this.rewardCommonDao.getAwardInfoById(awardType.getAwardInfoId().getId());
						studentApplyInfo.setId(awardInfo.getAwardName());
					}
					listAward.add(studentApplyInfo);
				}
			}
		}
		for (int i = 0; i < listBurse.size(); i++) {
			awardbf.append("{");
			awardbf.append("\"schoolYear\":");
			awardbf.append("\""+listBurse.get(i).getSchoolYear().getName()+"\",");
			awardbf.append("\"awardName\":");
			if(DataUtil.isNotNull(listBurse.get(i).getHelpGrade()) && DataUtil.isEquals(listBurse.get(i).getHelpGrade().getDicCategory().getCode(), "AID_LEVELS")){//判断国家助学金所在档数
				awardbf.append("\""+listBurse.get(i).getBurseName().getName()+"（"+listBurse.get(i).getHelpGrade().getName()+"）"+"\"");
			}else{
				awardbf.append("\""+listBurse.get(i).getBurseName().getName()+"\"");
			}
			awardbf.append("}");
			if( i != (listBurse.size()-1) || listAward.size() > 0 || listcollegeAwardInfo.size() > 0){
				awardbf.append(",");
			}
		}
		for (int i = 0; i < listAward.size(); i++) {
			awardbf.append("{");
			awardbf.append("\"schoolYear\":");
			awardbf.append("\"" + listAward.get(i).getAwardTypeId().getSchoolYear().getName() + "\",");
			awardbf.append("\"awardName\":");
			awardbf.append("\"" + listAward.get(i).getId() + "\"");
			awardbf.append("}");
			if( i != (listAward.size()-1) || listcollegeAwardInfo.size() > 0){
				awardbf.append(",");
			}
		}
		for(int i = 0; i < listcollegeAwardInfo.size(); i++){
			awardbf.append("{");
			awardbf.append("\"schoolYear\":");
			awardbf.append("\"" + listcollegeAwardInfo.get(i).getSchoolYear().getName() + "\",");
			awardbf.append("\"awardName\":");
			awardbf.append("\"" + listcollegeAwardInfo.get(i).getAwardName() + "\"");
			awardbf.append("}");
			if( i != (listcollegeAwardInfo.size()-1)){
				awardbf.append(",");
			}
		}
		awardbf.append("]");
		this.formatAward(awardbf.toString());
		return awardbf.toString();
	}
	/**
	 * 获取某个学生在校期间全部的综合测评信息
	 * @param startSchoolYear
	 * @param endSchoolYear
	 * @param studentInfoModelPO
	 * @return
	 */
	@Override
	public String getAllEvaluationInfos(String startSchoolYear, String endSchoolYear, StudentInfoModel studentInfoModel) {
		StringBuffer performance = new StringBuffer("{");
		for (int i = Integer.parseInt(startSchoolYear); i <= Integer.parseInt(endSchoolYear); i++) {
			Dic schoolYear = SchoolYearUtil.yearToSchool(i+"");
			if(!DataUtil.isNotNull(schoolYear)){
				continue;
			}
			Map<String,String> evaluationInfos = this.evaluationCommonDao.queryEvaluationScore(schoolYear.getId(), studentInfoModel);
			if(evaluationInfos.size() == 0){//某学年该学生没有综合测评成绩
				continue;
			}
			performance.append("\""+schoolYear.getName())
				.append("\":{\"德育分\":\"").append(DataUtil.isNotNull(evaluationInfos.get("dycpScore"))?evaluationInfos.get("dycpScore"):0)
				.append("\",\"能力分\":\"").append(DataUtil.isNotNull(evaluationInfos.get("nlcpScore"))?evaluationInfos.get("nlcpScore"):0)
				.append("\",\"文体分\":\"").append(DataUtil.isNotNull(evaluationInfos.get("wtcpScore"))?evaluationInfos.get("wtcpScore"):0)
				.append("\",\"测评总分\":\"").append(DataUtil.isNotNull(evaluationInfos.get("sumScore"))?evaluationInfos.get("sumScore"):0)
				.append("\"},");
		}
		int end = performance.lastIndexOf(",");
		if(end > -1){
			return performance.substring(0, end) + "}";
		}else{
			return performance.append("}").toString();
		}
	}
	/**
	 * 通过ids数组，查询
	 * @param schoolGoodStudentIds
	 */
	@Override
	public List<SchoolGoodStudent> querySchoolGoodStudentByIds(String[] schoolGoodStudentIds) {
		return this.schoolGoodStudentDao.querySchoolGoodStudentByIds(schoolGoodStudentIds);
	}
	/**
	 * 批量保存审核结果
	 * @param schoolGoodStudentIds
	 * @param approveStatus
	 */
	@Override
	public void updateApproveStateStudent(String[] schoolGoodStudentIds, String approveStatus,String approveReason) {
		this.schoolGoodStudentDao.updateApproveStateStudent(schoolGoodStudentIds,approveStatus,approveReason);
	}
	/**
	 * 格式化综合信息，接收JSON串
	 * @param allEvaluationInfos
	 * @return
	 */
	public String formatEvaluationInfos(String allEvaluationInfos){
		Map<String,JSONObject> evaluationInfos = JsonUtils.string2JsonObject(allEvaluationInfos);
		StringBuffer evaluationbf = new StringBuffer();
		for (String schoolYearStr : evaluationInfos.keySet()) {
			evaluationbf.append(schoolYearStr);
			evaluationbf.append("\r\n");
			Map<String,JSONObject> evaluations = evaluationInfos.get(schoolYearStr);
			int i = 0;
			for (String e : evaluations.keySet()) {
				evaluationbf.append(e);
				evaluationbf.append(" ：");
				evaluationbf.append(evaluations.get(e));
				evaluationbf.append("\t\t");
				i++;
				if(i % 4 == 0){
					evaluationbf.append("\r\n");
				}
			}
		}
		return evaluationbf.toString();
	}
	/**
	 * 格式化奖助信息
	 * @param allAaward
	 * @return
	 */
	private String formatAward(String allAaward){
		StringBuffer awardbf = new StringBuffer();
		Object[] awards = JsonUtils.jsonArray2JavaArray(allAaward);
		for (int i = 0; i < awards.length; i++) {
			JSONObject jsonObject = (JSONObject) awards[i];
			awardbf.append(jsonObject.get("schoolYear")).append(" ： ").append(jsonObject.get("awardName")).append("\r\n");
		}
		return awardbf.toString();
	}
}
