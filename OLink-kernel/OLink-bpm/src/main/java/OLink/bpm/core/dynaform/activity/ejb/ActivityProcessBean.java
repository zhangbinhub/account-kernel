package OLink.bpm.core.dynaform.activity.ejb;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.util.StringUtil;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import eWAP.core.Tools;

import OLink.bpm.core.dynaform.activity.dao.ActivityDAO;

public class ActivityProcessBean extends AbstractDesignTimeProcessBean<Activity> implements ActivityProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3819063451738688655L;

	//@SuppressWarnings("unchecked")
	protected IDesignTimeDAO<Activity> getDAO() throws Exception {
		return (ActivityDAO) DAOFactory.getDefaultDAO(Activity.class.getName());
	}

	public void changeOrder(String id, ActivityParent parent, String flag) {
		if (StringUtil.isBlank(id) || StringUtil.isBlank(flag)) {
			return;
		}

		Set<Activity> activitySet = parent.getActivitys();
		Activity[] activityArray = activitySet.toArray(new Activity[activitySet.size()]);
		activitySet.clear();
		for (int i = 0; i < activityArray.length; i++) {
			Activity activity = activityArray[i];
			if (id.equals(activity.getId())) {

				if (flag.equalsIgnoreCase("previous")) {
					if (i != 0) {
						Activity previousActivity = activityArray[i - 1];
						swap(activity, previousActivity);
					}
				} else if (flag.equalsIgnoreCase("next")) {
					if (i < activityArray.length - 1) {
						Activity nextActivity = activityArray[i + 1];
						swap(activity, nextActivity);
					}
				}
			}
			activitySet.add(activity);
		}
	}

	private void swap(Activity activity, Activity anActivity) {
		int tempOrderNo = activity.getOrderno();
		activity.setOrderno(anActivity.getOrderno());
		anActivity.setOrderno(tempOrderNo);
	}

	public int getActivityMaxOrderNo(Collection<Activity> activityList) {
		int maxOrderNo = 0;
		for (Iterator<Activity> iterator = activityList.iterator(); iterator.hasNext();) {
			Activity activity = iterator.next();
			if (activity.getOrderno() > maxOrderNo) {
				maxOrderNo = activity.getOrderno();
			}
		}

		return maxOrderNo;
	}

	public void doRemove(ActivityParent parent, String[] pks) throws Exception {
		Set<Activity> activitySet = parent.getActivitys();
		Set<Activity> destActivitySet = new TreeSet<Activity>();

		for (Iterator<Activity> iterator = activitySet.iterator(); iterator.hasNext();) {
			Activity activity = iterator.next();
			boolean flag = false;
			for (int i = 0; i < pks.length; i++) {
				if (pks[i].equals(activity.getId())) {
					flag = true;
					break;
				}
			}

			if (!flag) {
				destActivitySet.add(activity);
			}
			flag = false;
		}
		parent.setActivitys(destActivitySet);
	}

	public void doUpdate(ActivityParent parent, ValueObject vo) throws Exception {
		Activity activity = (Activity) vo;

		Collection<Activity> origActivitySet = parent.getActivitys();
		int maxOrderNo = getActivityMaxOrderNo(origActivitySet);
		if (StringUtil.isBlank(activity.getId())) {
			activity.setId(Tools.getSequence());
			activity.setOrderno(maxOrderNo + 1);
		}

		Set<Activity> destActivitySet = new TreeSet<Activity>();
		destActivitySet.add(activity);
		for (Iterator<Activity> iterator = origActivitySet.iterator(); iterator.hasNext();) {
			Activity origActivity = iterator.next();
			if (!activity.getId().equals(origActivity.getId())) {
				destActivitySet.add(origActivity);
			}
		}
		parent.setActivitys(destActivitySet);
	}
}
