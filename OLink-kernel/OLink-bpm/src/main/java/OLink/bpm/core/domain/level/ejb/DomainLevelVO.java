package OLink.bpm.core.domain.level.ejb;

import java.util.Collection;
import java.util.HashSet;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.domain.ejb.DomainVO;

public class DomainLevelVO extends ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5467597682667795207L;

	private String id;

	private String name;

	private Collection<DomainVO> domains;

	private String description;

	/**
	 * 等级:0,1,2,3
	 */
	private String level;

	private int userCount;

	private int mobileUserCount;

	private double price;

	/**
	 * 获取域的等级(等级:0,1,2,3)
	 * 
	 * @return 域的等级
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * 获取使用平台的用户数量
	 * 
	 * @return 用户数量
	 */
	public int getUserCount() {
		return userCount;
	}

	/**
	 * 获取用手机使用平台用户数量
	 * 
	 * @return 用户数量
	 */
	public int getMobileUserCount() {
		return mobileUserCount;
	}

	/**
	 * 获取价格
	 * 
	 * @return 价格
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * 设置域级别
	 * 
	 * @param level
	 *            域级别
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * 设置使用平台的用户数量
	 * 
	 * @param userCount
	 *            使用平台的用户数量
	 */
	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}

	/**
	 * 设置手机使用平台用户数量
	 * 
	 * @param mobileUserCount
	 *            手机使用平台用户数量
	 */
	public void setMobileUserCount(int mobileUserCount) {
		this.mobileUserCount = mobileUserCount;
	}

	/**
	 * 设置价格
	 * 
	 * @param price
	 *            价格
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * 获取本对象的标识
	 * 
	 * @return 对象的标识
	 */
	public String getId() {
		return id;
	}

	/**
	 * 获取本对象的名称
	 * 
	 * @return 对象的名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置对象的标识
	 * 
	 * @return 对象的标识
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 设置对象的名称
	 * 
	 * @param name
	 *            对象的名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取域集合
	 * 
	 * @return 域集合
	 */
	public Collection<DomainVO> getDomains() {
		if (domains == null)
			domains = new HashSet<DomainVO>();
		return domains;
	}

	/**
	 * 设置域集合
	 * 
	 * @param domains
	 *            域集合
	 */
	public void setDomains(Collection<DomainVO> domains) {
		this.domains = domains;
	}

	/**
	 * 获取描述
	 * 
	 * @return 描述
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置描述
	 * 
	 * @param description
	 *            描述
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
