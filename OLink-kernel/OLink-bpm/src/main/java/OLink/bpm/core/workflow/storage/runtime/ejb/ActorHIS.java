package OLink.bpm.core.workflow.storage.runtime.ejb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.role.ejb.RoleProcess;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;
import eWAP.core.Tools;

import OLink.bpm.core.user.action.WebUser;


public class ActorHIS extends ValueObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -511742656217844826L;

	private String name;

	private String actorid;
	
	private String agentid;
	
	private String agentname;

	private int type;

	private Date processtime; // 审批日期

	private String attitude; // 审批意见

	public ActorHIS() {

	}

	/**
	 * 构造方法
	 * 
	 * @param user
	 *            web用户
	 * @throws Exception
	 */
	public ActorHIS(WebUser user) throws Exception {
		this.id = Tools.getSequence();
		this.name = user.getName();
		this.actorid = user.getId();
		this.type = Type.TYPE_USER;
	}

	/**
	 * 获取角色标识
	 * 
	 * @return 角色标识
	 * @hibernate.property column="ACTORID"
	 */
	public String getActorid() {
		return actorid;
	}

	/**
	 * 设置角色标识
	 * 
	 * @param actorid
	 *            角色标识
	 */
	public void setActorid(String actorid) {
		this.actorid = actorid;
	}

	/**
	 * 获取名称
	 * 
	 * @return 名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置名称
	 * 
	 * @param name
	 *            名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public int getType() {
		return type;
	}

	/**
	 * 设置类型
	 * 
	 * @param type
	 *            类型
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	public String getAgentid() {
		return agentid;
	}

	public void setAgentid(String agentid) {
		this.agentid = agentid;
	}

	public String getAgentname() {
		return agentname;
	}

	public void setAgentname(String agentname) {
		this.agentname = agentname;
	}

	/**
	 * 获取处理日期
	 * 
	 * @return
	 */
	public Date getProcesstime() {
		return processtime;
	}

	/**
	 * 设置处理日期
	 * 
	 * @param processtime
	 */
	public void setProcesstime(Date processtime) {
		this.processtime = processtime;
	}

	/**
	 * 获取处理意见
	 * 
	 * @return
	 */
	public String getAttitude() {
		return attitude;
	}

	/**
	 * 设置处理意见
	 * 
	 * @param attitude
	 */
	public void setAttitude(String attitude) {
		this.attitude = attitude;
	}

	/**
	 * 获取所有的审批用户的集合,集合里存放(UserVO的对象)
	 * 
	 * @return 审批用户的集合
	 * @throws Exception
	 */
	public List<UserVO> getAllUser() throws Exception {
		List<UserVO> userList = new ArrayList<UserVO>();

		switch (getType()) {
		case Type.TYPE_USER:
			UserProcess userProcess = (UserProcess) ProcessFactory
					.createProcess(UserProcess.class);
			UserVO user = (UserVO) userProcess.doView(getActorid());
			userList.add(user);
			break;
		case Type.TYPE_ROLE:
			RoleProcess roleProcess = (RoleProcess) ProcessFactory
					.createProcess(RoleProcess.class);
			RoleVO role = (RoleVO) roleProcess.doView(getActorid());
			for (Iterator<UserVO> iterator = role.getUsers().iterator(); iterator
					.hasNext();) {
				UserVO roleUser = iterator.next();
				if (roleUser.getDomainid().equals(getDomainid())) {
					userList.add(roleUser);
				}
			}
			break;
		default:
			break;
		}

		return userList;
	}
}
