package th.co.toyota.bw0.api.repository.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.st3.api.model.SystemInfo;
import th.co.toyota.st3.api.model.SystemInfoId;
import th.co.toyota.st3.api.model.SystemInfoId_;
import th.co.toyota.st3.api.model.SystemInfo_;

import com.google.common.base.Strings;

@Repository
public class CBW03060Repository implements IBW03060Repository {
	@NotNull
	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager em;

	final Logger logger = LoggerFactory.getLogger(CBW03060Repository.class);

	@Override
	public List<SystemInfo> querySystemMasterInfo(SystemInfoId sysInfoId) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SystemInfo> cq = cb.createQuery(SystemInfo.class);
		Root<SystemInfo> sys = cq.from(SystemInfo.class);

		cq.select(sys);
		cq.distinct(true);

		List<Predicate> predicates = new ArrayList<Predicate>();
		predicates.add(cb.equal(
				cb.upper(sys.get(SystemInfo_.id).get(SystemInfoId_.category)),
				sysInfoId.getCategory().toUpperCase()));

		if (!Strings.isNullOrEmpty(sysInfoId.getSubCategory())) {
			predicates.add(cb.equal(cb.upper(sys.get(SystemInfo_.id).get(
					SystemInfoId_.subCategory)), sysInfoId.getSubCategory()
					.toUpperCase()));
		}
		if (!Strings.isNullOrEmpty(sysInfoId.getCode())) {
			if (!sysInfoId.getCode().contains("*")) {
				predicates
						.add(cb.equal(cb.upper(sys.get(SystemInfo_.id).get(
								SystemInfoId_.code)), sysInfoId.getCode()
								.toUpperCase()));
			} else {
				predicates.add(cb.like(cb.upper(sys.get(SystemInfo_.id).get(
						SystemInfoId_.code)),
						sysInfoId.getCode().replace("*", "%").toUpperCase()));
			}

		}

		predicates.add(cb.equal(sys.get(SystemInfo_.status),
				AppConstants.ACTIVE));

		if ((predicates != null) && (predicates.size() > 0)) {
			cq.where(predicates.toArray(new Predicate[] {}));
		}

		cq.orderBy(cb.asc(sys.get(SystemInfo_.id).get(SystemInfoId_.category)),
				cb.asc(sys.get(SystemInfo_.id).get(SystemInfoId_.subCategory)),
				cb.asc(sys.get(SystemInfo_.id).get(SystemInfoId_.code)));

		return em.createQuery(cq).getResultList();

	}

	@Override
	public SystemInfo findSystemMasterInfo(SystemInfoId infoId) {
		try {
			SystemInfo sys = em.find(SystemInfo.class, infoId);
			if (sys != null) {
				if (!sys.getStatus().equals(AppConstants.ACTIVE)) {
					sys = null;
				}
			}
			return sys;
		} catch (NoResultException e) {
			return null;
		}
	}

	// TODO Used by log monitoring to retrieve data for combobox. This should be
	// in common repository.
	@Override
	public List<SystemInfo> querySystemMasterCodeValue(String category,
			String subCategory) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SystemInfo> cq = cb.createQuery(SystemInfo.class);
		Root<SystemInfo> sys = cq.from(SystemInfo.class);

		cq.select(sys);
		cq.where(cb.equal(sys.get(SystemInfo_.id).get(SystemInfoId_.category),
				category), cb.equal(
				sys.get(SystemInfo_.id).get(SystemInfoId_.subCategory),
				subCategory), cb.equal(sys.get(SystemInfo_.status),
				AppConstants.ACTIVE));

		cq.orderBy(cb.asc(sys.get(SystemInfo_.id).get(SystemInfoId_.category)),
				cb.asc(sys.get(SystemInfo_.id).get(SystemInfoId_.subCategory)),
				cb.asc(sys.get(SystemInfo_.id).get(SystemInfoId_.code)));

		return em.createQuery(cq).getResultList();

	}
	
	@Override
	public List<SystemInfo> querySystemMasterCodeValue(String category,
			String subCategory, boolean isSortValue, String sortMethod) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SystemInfo> cq = cb.createQuery(SystemInfo.class);
		Root<SystemInfo> sys = cq.from(SystemInfo.class);

		cq.select(sys);
		cq.where(cb.equal(sys.get(SystemInfo_.id).get(SystemInfoId_.category),
				category), cb.equal(
				sys.get(SystemInfo_.id).get(SystemInfoId_.subCategory),
				subCategory), cb.equal(sys.get(SystemInfo_.status),
				AppConstants.ACTIVE));

		if(isSortValue) {
			if(sortMethod.equals(AppConstants.SORT_METHOD_ASCENDING)) {
				cq.orderBy(cb.asc(sys.get(SystemInfo_.value)));
			}else{
				cq.orderBy(cb.desc(sys.get(SystemInfo_.value)));
			}
		}
		

		return em.createQuery(cq).getResultList();

	}

	@Override
	public List<SystemInfo> searchSystemMaster(String category,
			String subCategory, String remark) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SystemInfo> cq = cb.createQuery(SystemInfo.class);
		Root<SystemInfo> root = cq.from(SystemInfo.class);

		cq.select(root);

		List<Predicate> preds = new ArrayList<Predicate>();

		preds.add(cb.equal(
				root.get(SystemInfo_.id).get(SystemInfoId_.category), category));
		preds.add(cb.equal(
				root.get(SystemInfo_.id).get(SystemInfoId_.subCategory),
				subCategory));
		preds.add(cb.equal(root.get(SystemInfo_.remark), remark));

		preds.add(cb.equal(root.get(SystemInfo_.status),
				AppConstants.ACTIVE));

		cq.where(preds.toArray(new Predicate[] {}));

		return em.createQuery(cq).getResultList();
	}

//	@Override
//	public String findSystemMasterValue(String category, String subCategory,
//			String code) {
//		SystemInfoId infoId = new SystemInfoId();
//		infoId.setCategory(category);
//		infoId.setSubCategory(subCategory);
//		infoId.setCode(code);
//
//		SystemInfo sys = em.find(SystemInfo.class, infoId);
//		String str = "";
//		if (sys != null) {
//			if (sys.getStatus().equals(AppConstants.ACTIVE)) {
//				str = sys.getValue();
//			}
//		}
//		return str;
//	}
	
	@Override
	public String findSystemMasterValue(Connection conn, String category, String subCategory, String code){
		boolean closeConnection = true;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String value = "";
        try{
			if(conn==null){
				SessionImpl session = (SessionImpl)(em.getDelegate());
				conn = session.getJdbcConnectionAccess().obtainConnection();
			}else{
				closeConnection = false;
			}
			
        	StringBuilder sql = new StringBuilder();
			sql.append(" SELECT VALUE ");
			sql.append(" FROM TB_M_SYSTEM ");
			sql.append(" WHERE CATEGORY = '"+category+"' ");
			sql.append(" AND SUB_CATEGORY = '"+subCategory+"' ");
			sql.append(" AND CD = '"+code+"' ");
			sql.append(" AND STATUS = 'Y' ");
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				value = rs.getString("VALUE");
			}
        }catch(Exception e){
        	e.printStackTrace();
        } finally {
        	try{
				if (rs !=null) {
					rs.close();					
		            rs = null;
		        }
				
				if (ps !=null) {
		            ps.close();
		            ps = null;
		        }
				
				if(conn!=null && !conn.isClosed() && closeConnection){
					conn.close();
					conn = null;
				}
        	} catch (SQLException e) {
				e.printStackTrace();
			}
		}
        return value;
	}
}
