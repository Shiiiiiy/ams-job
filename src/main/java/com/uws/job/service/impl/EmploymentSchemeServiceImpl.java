package com.uws.job.service.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uws.common.dao.IStudentCommonDao;
import com.uws.core.base.BaseServiceImpl;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.job.EmploymentScheme;
import com.uws.domain.job.EmploymentSchemeClassView;
import com.uws.domain.job.EmploymentSchemeCollegeView;
import com.uws.domain.job.EmploymentSchemeMajorView;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.job.dao.IEmploymentSchemeDao;
import com.uws.job.service.IEmploymentSchemeService;
import com.uws.job.util.Constants;
import com.uws.sys.model.Dic;

/**
 * @className EmploymentSchemeServiceImpl.java
 * @package com.uws.job.service.impl
 * @description
 * @author Administrator
 * @date 2015-10-10  下午3:07:41
 */
@Service("employmentSchemeService")
public class EmploymentSchemeServiceImpl extends BaseServiceImpl implements IEmploymentSchemeService {
	@Autowired
	private IEmploymentSchemeDao employmentSchemeDao;
	@Autowired
	private IStudentCommonDao studentCommonDao;
	private int pageSize = 1000;//分页查询
	/**
	 * 分页查询
	 * @param employmentScheme
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	@Override
	public Page queryEmploymentSchemePage(EmploymentScheme employmentScheme, int pageSize, int pageNo) {
		return this.employmentSchemeDao.queryEmploymentSchemePage(employmentScheme, pageSize, pageNo);
	}
	/**
	 * 添加
	 * @param employmentScheme
	 */
	@Override
	public void saveEmploymentScheme(EmploymentScheme employmentScheme) {
		this.employmentSchemeDao.save(employmentScheme);
	}
	/**
	 * 通过ID查询EmploymentScheme
	 * @param id
	 */
	@Override
	public EmploymentScheme queryEmploymentSchemeById(String id) {
		return (EmploymentScheme) this.employmentSchemeDao.get(EmploymentScheme.class, id);
	}
	/**
	 * 修改EmploymentScheme
	 * @param employmentSchemePO
	 */
	@Override
	public void updateEmploymentScheme(EmploymentScheme employmentSchemePO) {
		this.employmentSchemeDao.update(employmentSchemePO);
	}
	/**
	 * 通过学生学号查询就业方案
	 * @param stuNumber
	 */
	@Override
	public EmploymentScheme queryEmploymentSchemeByStuId(String stuId) {
		return this.employmentSchemeDao.queryEmploymentSchemeByStuId(stuId);
	}
	/**
	 * 通过查询条件查询EmploymentScheme--已废弃
	 * @param employmentSchemeVO
	 * @return
	 */
	@Override
	public List<EmploymentScheme> queryEmploymentSchemeByCond(EmploymentScheme employmentSchemeVO) {
		return this.employmentSchemeDao.queryEmploymentSchemeByCond(employmentSchemeVO);
	}
	/**
	 * 查询employmentSchemes集合中的数据存在数据库中的数据
	 * @param employmentSchemes 
	 * @return 存在于数据库中的数据和Excel中的数据
	 */
	@Override
	public List<EmploymentScheme[]> compareData(List<EmploymentScheme> employmentSchemes) {
		long count = this.employmentSchemeDao.getCount();
		List<EmploymentScheme[]> exitsEmploymentSchemes = new ArrayList<EmploymentScheme[]>();
		EmploymentScheme[] employmentSchemeArr = null;
		for (int i = 0; i < count / this.pageSize + 1; i++) {
			Page page = this.employmentSchemeDao.queryEmploymentSchemePage(null, 1000, i+1);
			for (EmploymentScheme employmentScheme : employmentSchemes) {
				List<EmploymentScheme> result = (List<EmploymentScheme>) page.getResult();
				for (EmploymentScheme e :result) {
					if(employmentScheme.getStuNo().equals(e.getStudentId().getStuNumber())){
						employmentSchemeArr = new EmploymentScheme[]{e,employmentScheme};
						exitsEmploymentSchemes.add(employmentSchemeArr);
						break;
					}
				}
			}
		}
		return exitsEmploymentSchemes;
	}
	/**
	 * 用户确定导入Excel在系统中存在的数据
	 * @param compareId 忽略的学生学号
	 * @param filePath 
	 * @param exitsEmploymentSchemes 
	 * @throws Exception 
	 * @throws ClassNotFoundException 
	 * @throws InstantiationException 
	 * @throws ExcelException 
	 * @throws IllegalAccessException 
	 * @throws IOException 
	 * @throws OfficeXmlFileException 
	 */
	@Override
	public void importData(List<EmploymentScheme[]> exitsEmploymentSchemes,String filePath, String compareId) throws OfficeXmlFileException, IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException, Exception {
		Map<String, EmploymentScheme> map = new HashMap<String, EmploymentScheme>();
		for (EmploymentScheme[] employmentSchemes : exitsEmploymentSchemes) {
			map.put(employmentSchemes[0].getStudentId().getStuNumber(), employmentSchemes[0]);
		}
		ImportUtil iu = new ImportUtil();
		List<EmploymentScheme> employmentSchemes = iu.getDataList(filePath, "importEmploymentScheme", null, EmploymentScheme.class);
		
		for (EmploymentScheme employmentScheme : employmentSchemes) {
			if(map.containsKey(employmentScheme.getStuNo())){//判断导入的数据是否在数据库中存在
				if(!compareId.contains(employmentScheme.getStuNo())){//判断用户是否忽略
					EmploymentScheme e = map.get(employmentScheme.getStuNo());
					BeanUtils.copyProperties(employmentScheme, e, new String[]{"id","studentId","delState"});
					this.employmentSchemeDao.update(e);
				}
			}else{
				StudentInfoModel studentInfoModel = this.studentCommonDao.queryStudentByStudentNo(employmentScheme.getStuNo());
				employmentScheme.setStudentId(studentInfoModel);
//				employmentScheme.setDelState(Constants.STATUS_NORMAL_DICS);
				this.employmentSchemeDao.save(employmentScheme);
			}
		}
	}
	/**
	 * 对导入的数据修改或保存
	 * @param employmentSchemes
	 */
	@Override
	public void saveOrUpdate(List<EmploymentScheme> employmentSchemes) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyyMMdd");
		for (EmploymentScheme employmentScheme : employmentSchemes) {
			String stuId = employmentScheme.getStuNo();
			EmploymentScheme es = this.employmentSchemeDao.queryEmploymentSchemeByStuId(stuId);
			if(DataUtil.isNotNull(es)){//修改
				BeanUtils.copyProperties(employmentScheme, es,new String[]{"id","studentId","delState"});
				if (DataUtil.isNotNull(employmentScheme.getStrGraduateTime())) {
					es.setGraduateTime(dateFormat.parse(employmentScheme.getStrGraduateTime()));
				}
				if(DataUtil.isNotNull(employmentScheme.getStrReportStart())){
					es.setReportStartTime(dateFormat.parse(employmentScheme.getStrReportStart()));
				}
			}else{//增加
				StudentInfoModel studentInfoModel = new StudentInfoModel();
				studentInfoModel.setId(employmentScheme.getStuNo());
				employmentScheme.setStudentId(studentInfoModel);
				if (DataUtil.isNotNull(employmentScheme.getStrGraduateTime())) {
					employmentScheme.setGraduateTime(dateFormat.parse(employmentScheme.getStrGraduateTime()));
				}
				if(DataUtil.isNotNull(employmentScheme.getStrReportStart())){
					employmentScheme.setReportStartTime(dFormat.parse(employmentScheme.getStrReportStart()));
				}
				employmentScheme.setDelState(Constants.STATUS_NORMAL_DICS);
				this.employmentSchemeDao.save(employmentScheme);
			}
		}
	}
	/**
	 * 按学院统计就业率
	 * @param employmentSchemeVO
	 * @param schoolYear
	 * @return
	 */
	@Override
	public List<EmploymentSchemeCollegeView> statEmploymentSchemeByCollege(EmploymentScheme employmentSchemeVO) {
		return this.employmentSchemeDao.statEmploymentSchemeByCollege(employmentSchemeVO);
	}
	/**
	 * 按专业统计就业率
	 * @param employmentSchemeVO
	 * @param schoolYear
	 * @return
	 */
	@Override
	public List<EmploymentSchemeMajorView> statEmploymentSchemeByMajor(EmploymentScheme employmentSchemeVO) {
		return this.employmentSchemeDao.statEmploymentSchemeByMajor(employmentSchemeVO);
	}
	/**
	 * 按班级统计就业率
	 * @param employmentSchemeVO
	 * @param schoolYear
	 * @return
	 */
	@Override
	public List<EmploymentSchemeClassView> statEmploymentSchemeByClass(EmploymentScheme employmentSchemeVO) {
		return this.employmentSchemeDao.statEmploymentSchemeByClass(employmentSchemeVO);
	}
	/**
	 * 按学院统计就业率分页
	 * @param employmentSchemeVO
	 * @param schoolYear
	 * @return
	 */
	public Page statEmploymentSchemeByCollegePage(EmploymentScheme employmentSchemeVO,int pageSize,int pageNo){
		return this.employmentSchemeDao.statEmploymentSchemeByCollegePage(employmentSchemeVO, pageSize, pageNo);
	}
	/**
	 * 按专业统计就业率分页
	 * @param employmentSchemeVO
	 * @param schoolYear
	 * @return
	 */
	public Page statEmploymentSchemeByMajorPage(EmploymentScheme employmentSchemeVO,int pageSize,int pageNo){
		return this.employmentSchemeDao.statEmploymentSchemeByMajorPage(employmentSchemeVO, pageSize, pageNo);
	}
	/**
	 * 按班级统计就业率分页
	 * @param employmentSchemeVO
	 * @param schoolYear
	 * @return
	 */
	public Page statEmploymentSchemeByClassPage(EmploymentScheme employmentSchemeVO,int pageSize,int pageNo){
		return this.employmentSchemeDao.statEmploymentSchemeByClassPage(employmentSchemeVO, pageSize, pageNo);
	}
}
