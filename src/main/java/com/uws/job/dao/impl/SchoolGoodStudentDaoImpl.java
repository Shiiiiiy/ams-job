package com.uws.job.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.job.SchoolGoodStudent;
import com.uws.domain.job.SchoolGoodStudentClassView;
import com.uws.domain.job.SchoolGoodStudentCollegeView;
import com.uws.domain.job.SchoolGoodStudentMajorView;
import com.uws.job.dao.ISchoolGoodStudentDao;
import com.uws.job.util.Constants;

/**
 * @className SchoolGoodStudentDaoImpl.java
 * @package com.uws.job.dao.impl
 * @description
 * @author Administrator
 * @date 2015-11-6  下午5:04:49
 */
@Repository
public class SchoolGoodStudentDaoImpl extends BaseDaoImpl implements ISchoolGoodStudentDao {

	/**
	 * 分页查询
	 * @param schoolGoodStudentVO
	 * @param dEFAULT_PAGE_SIZE
	 * @param pageNo
	 * @param queryType  查询方式  1：在未删除的基础数据上查询，2：是在审核通过和撤销的基础数据上查询
	 * @return
	 */
	@Override
	public Page querySchoolGoodStudentPage(SchoolGoodStudent schoolGoodStudentVO, int dEFAULT_PAGE_SIZE, int pageNo,int queryType) {
		String hql = "from SchoolGoodStudent where status.id = :statusId ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("statusId", Constants.STATUS_NORMAL_DICS.getId());
		if(queryType == Constants.schoolGoodStudentTypeByPass){
			hql += "and approveStatus in ('PASS') ";
		}
		if (DataUtil.isNotNull(schoolGoodStudentVO)) {
			if(DataUtil.isNotNull(schoolGoodStudentVO.getSchoolYear()) && DataUtil.isNotNull(schoolGoodStudentVO.getSchoolYear().getId())){
				hql += "and schoolYear.id = :schoolYearId ";
				params.put("schoolYearId", schoolGoodStudentVO.getSchoolYear().getId());
			}
			if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId())){
				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getId())){
					hql += "and studentId.id like :studentId ";
					params.put("studentId", "%"+schoolGoodStudentVO.getStudentId().getId()+"%");
				}
				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getName())){
					hql += "and studentId.name like :studentName ";
					params.put("studentName", "%"+schoolGoodStudentVO.getStudentId().getName()+"%");
				}
				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege().getId())){
					hql += "and studentId.college.id = :collegeId ";
					params.put("collegeId", schoolGoodStudentVO.getStudentId().getCollege().getId());
				}
				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getMajor()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getMajor().getId())){
					hql += "and studentId.major.id = :majorId ";
					params.put("majorId", schoolGoodStudentVO.getStudentId().getMajor().getId());
				}
				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getClassId()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getClassId().getId())){
					hql += "and studentId.classId.id = :classId ";
					params.put("classId", schoolGoodStudentVO.getStudentId().getClassId().getId());
				}
			}
			if(DataUtil.isNotNull(schoolGoodStudentVO.getApproveStatus())){
				hql += "and approveStatus = :approveStatus ";
				params.put("approveStatus", schoolGoodStudentVO.getApproveStatus());
			}
			if(DataUtil.isNotNull(schoolGoodStudentVO.getSubmitStatus()) && DataUtil.isNotNull(schoolGoodStudentVO.getSubmitStatus().getId())){
				hql += "and submitStatus.id = :submitStatusId ";
				params.put("submitStatusId", schoolGoodStudentVO.getSubmitStatus().getId());
			}
		}
		hql += "order by schoolYear.code desc,studentId.college.id,studentId.major.id,studentId.id";
		return this.pagedQuery(hql, params, dEFAULT_PAGE_SIZE, pageNo);
	}
//	/**
//	 * 分页查询审核通过和撤销
//	 * @param schoolGoodStudentVO
//	 * @param dEFAULT_PAGE_SIZE
//	 * @param pageNo
//	 * @return
//	 */
//	@Override
//	public Page querySchoolGoodStudentPageByPass(SchoolGoodStudent schoolGoodStudentVO, int dEFAULT_PAGE_SIZE, int pageNo) {
//		String hql = "from SchoolGoodStudent where status.id = :statusId";
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("statusId", Constants.STATUS_NORMAL_DICS.getId());
//		if (DataUtil.isNotNull(schoolGoodStudentVO)) {
//			if(DataUtil.isNotNull(schoolGoodStudentVO.getSchoolYear()) && DataUtil.isNotNull(schoolGoodStudentVO.getSchoolYear().getId())){
//				hql += "and schoolYear.id = :schoolYearId ";
//				params.put("schoolYearId", schoolGoodStudentVO.getSchoolYear().getId());
//			}
//			if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId())){
//				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getId())){
//					hql += "and studentId.id like :studentId ";
//					params.put("studentId", "%"+schoolGoodStudentVO.getStudentId().getId()+"%");
//				}
//				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getName())){
//					hql += "and studentId.name like :studentName ";
//					params.put("studentName", "%"+schoolGoodStudentVO.getStudentId().getName()+"%");
//				}
//				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege().getId())){
//					hql += "and studentId.college.id = :collegeId ";
//					params.put("collegeId", schoolGoodStudentVO.getStudentId().getCollege().getId());
//				}
//				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getMajor()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getMajor().getId())){
//					hql += "and studentId.major.id = :majorId ";
//					params.put("majorId", schoolGoodStudentVO.getStudentId().getMajor().getId());
//				}
//				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getClassId()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getClassId().getId())){
//					hql += "and studentId.classId.id = :classId ";
//					params.put("classId", schoolGoodStudentVO.getStudentId().getClassId().getId());
//				}
//			}
//			if(DataUtil.isNotNull(schoolGoodStudentVO.getApproveStatus())){
//				hql += "and approveStatus = :approveStatus ";
//				params.put("approveStatus", schoolGoodStudentVO.getApproveStatus());
//			}
//			if(DataUtil.isNotNull(schoolGoodStudentVO.getSubmitStatus()) && DataUtil.isNotNull(schoolGoodStudentVO.getSubmitStatus().getId())){
//				hql += "and submitStatus.id = :submitStatusId ";
//				params.put("submitStatusId", schoolGoodStudentVO.getSubmitStatus().getId());
//			}
//		}
//		hql += "order by schoolYear.code desc,studentId.college.id,studentId.major.id,studentId.id";
//		return this.pagedQuery(hql, params, dEFAULT_PAGE_SIZE, pageNo);
//	}
	/**
	 * 条件查询
	 * @param schoolGoodStudentVO
	 * @return
	 */
	@Override
	public List<SchoolGoodStudent> querySchoolGoodStudentByCond(SchoolGoodStudent schoolGoodStudentVO) {
		String hql = "from SchoolGoodStudent where status.id = :statusId ";//and approveStatus != 'UNDO' ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("statusId", Constants.STATUS_NORMAL_DICS.getId());
		if (DataUtil.isNotNull(schoolGoodStudentVO)) {
			if(DataUtil.isNotNull(schoolGoodStudentVO.getSchoolYear())){
				hql += "and schoolYear.id = :schoolYearId ";
				params.put("schoolYearId", schoolGoodStudentVO.getSchoolYear().getId());
			}
			if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId())){
				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getId())){
					hql += "and studentId.id = :studentId ";
					params.put("studentId", schoolGoodStudentVO.getStudentId().getId());
				}
				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getName())){
					hql += "and studentId.name = :studentName ";
					params.put("studentName", schoolGoodStudentVO.getStudentId().getName());
				}
				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege().getId())){
					hql += "and studentId.college.id = :collegeId ";
					params.put("collegeId", schoolGoodStudentVO.getStudentId().getCollege().getId());
				}
				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getMajor()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getMajor().getId())){
					hql += "and studentId.major.id = :majorId ";
					params.put("majorId", schoolGoodStudentVO.getStudentId().getMajor().getId());
				}
				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getClassId()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getClassId().getId())){
					hql += "and studentId.classId.id = :classId ";
					params.put("classId", schoolGoodStudentVO.getStudentId().getClassId().getId());
				}
			}
			if(DataUtil.isNotNull(schoolGoodStudentVO.getApproveStatus())){
				hql += "and approveStatus = :approveStatus ";
				params.put("approveStatus", schoolGoodStudentVO.getApproveStatus());
			}
			if(DataUtil.isNotNull(schoolGoodStudentVO.getSubmitStatus()) && DataUtil.isNotNull(schoolGoodStudentVO.getSubmitStatus().getId())){
				hql += "and submitStatus.id = :submitStatusId ";
				params.put("submitStatusId", schoolGoodStudentVO.getSubmitStatus().getId());
			}
		}
		return this.query(hql, params);
	}
	
	/**
	 * 根据学生Id的集合查询校优秀毕业生
	 * @param stuIds
	 * @return
	 */
	@Override
	public List<SchoolGoodStudent> querySchoolGoodStudentsByStuId(String stuId) {
		String hql = "from SchoolGoodStudent where status.id = :statusId and studentId.id = :stuId ";
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("statusId", Constants.STATUS_NORMAL_DICS.getId());
		params.put("stuId", stuId);
		hql += "and approveStatus != 'UNDO'";
		return this.query(hql, params);
	}
	/**
	 * 通过学院统计校优人数
	 * @return
	 */
	@Override
	public List<SchoolGoodStudentCollegeView> statSchoolGoodStudentByCollege(SchoolGoodStudent schoolGoodStudentVO) {
		String hql = "from SchoolGoodStudentCollegeView t where 1 = 1 ";
		Map<String,Object> params = new HashMap<String, Object>();
		if(DataUtil.isNotNull(schoolGoodStudentVO)){
			if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege().getId())){
				hql += " and t.college.id = :collegeId";
				params.put("collegeId", schoolGoodStudentVO.getStudentId().getCollege().getId());
			}
			if(DataUtil.isNotNull(schoolGoodStudentVO.getSchoolYear()) && DataUtil.isNotNull(schoolGoodStudentVO.getSchoolYear().getId())){
				hql += " and t.schoolYear.id = :schoolYearId";
				params.put("schoolYearId", schoolGoodStudentVO.getSchoolYear().getId());
			}
		}
		hql += " order by college.id,schoolYear.code desc";
		return this.query(hql, params);
	}
	/**
	 * 按照专业统计
	 * @return
	 */
	@Override
	public List<SchoolGoodStudentMajorView> statSchoolGoodStudentByMajor(SchoolGoodStudent schoolGoodStudentVO) {
		String hql = "from SchoolGoodStudentMajorView t where 1 =1 ";
		Map<String,Object> params = new HashMap<String, Object>();
		if(DataUtil.isNotNull(schoolGoodStudentVO)){
			if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId())){
				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getMajor()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getMajor().getId())){
					hql += " and t.major.id = :majorId";
					params.put("majorId", schoolGoodStudentVO.getStudentId().getMajor().getId());
				}
				
				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege().getId())){
					hql += " and t.major.collage.id = :collegeId";
					params.put("collegeId", schoolGoodStudentVO.getStudentId().getCollege().getId());
				}
			}
			if(DataUtil.isNotNull(schoolGoodStudentVO.getSchoolYear()) && DataUtil.isNotNull(schoolGoodStudentVO.getSchoolYear().getId())){
				hql += " and t.schoolYear.id = :schoolYearId";
				params.put("schoolYearId", schoolGoodStudentVO.getSchoolYear().getId());
			}
		}
		hql += " order by major.collage.id,major.id,schoolYear.code desc";
		return this.query(hql, params);
	}
	/**
	 * 按照班级统计
	 * @return
	 */
	@Override
	public List<SchoolGoodStudentClassView> statSchoolGoodStudentByClass(SchoolGoodStudent schoolGoodStudentVO) {
		String hql = "from SchoolGoodStudentClassView t where 1 = 1 ";
		Map<String,Object> params = new HashMap<String, Object>();
		
		if(DataUtil.isNotNull(schoolGoodStudentVO)){
			if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId())){
				
				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getClassId()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getClassId().getId())){
					hql += " and t.clazz.id = :classId";
					params.put("classId", schoolGoodStudentVO.getStudentId().getClassId().getId());
				}
				
				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getMajor()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getMajor().getId())){
					hql += " and t.clazz.major.id = :majorId";
					params.put("majorId", schoolGoodStudentVO.getStudentId().getMajor().getId());
				}
				
				if(DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege()) && DataUtil.isNotNull(schoolGoodStudentVO.getStudentId().getCollege().getId())){
					hql += " and t.clazz.major.collage.id = :collegeId";
					params.put("collegeId", schoolGoodStudentVO.getStudentId().getCollege().getId());
				}
			}
			if(DataUtil.isNotNull(schoolGoodStudentVO.getSchoolYear()) && DataUtil.isNotNull(schoolGoodStudentVO.getSchoolYear().getId())){
				hql += " and t.schoolYear.id = :schoolYearId";
				params.put("schoolYearId", schoolGoodStudentVO.getSchoolYear().getId());
			}
		}
		hql += " order by clazz.major.collage.id,clazz.major.id,clazz.id,schoolYear.code desc";
		return this.query(hql, params);
	}
	/**
	 * 通过ids数组，查询
	 * @param schoolGoodStudentIds
	 */
	@Override
	public List<SchoolGoodStudent> querySchoolGoodStudentByIds(String[] schoolGoodStudentIds) {
		String hql = "from SchoolGoodStudent where status.id = :statusId and submitStatus.id = :submitStatusId and id in (:schoolGoodStudentIds)";
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("statusId", Constants.STATUS_NORMAL_DICS.getId());
		params.put("submitStatusId", Constants.STATUS_SUBMIT_DICS.getId());
		params.put("schoolGoodStudentIds", schoolGoodStudentIds);
		return this.query(hql, params);
	}
	/**
	 * 批量保存审核结果
	 * @param schoolGoodStudentIds
	 * @param approveStatus
	 */
	@Override
	public void updateApproveStateStudent(String[] schoolGoodStudentIds, String approveStatus,String approveReason) {
		String hql = "update SchoolGoodStudent set approveStatus = :approveStatus,approveReason = :approveReason where id in (:schoolGoodStudentIds)";
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("approveStatus", approveStatus);
		params.put("approveReason", approveReason);
		params.put("schoolGoodStudentIds", schoolGoodStudentIds);
		this.executeHql(hql, params);
	}
}
