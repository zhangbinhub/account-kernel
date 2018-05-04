package OLink.bpm.core.user.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;

public class OnlineUsers {
	private static Map<String, WebUser> _users = new HashMap<String, WebUser>();

	@SuppressWarnings("unchecked")
	public static void add(String key, WebUser user) {
		if (key == null || user == null) {
			return;
		}
		if(_users.size()>0){
			Iterator<Entry<String, WebUser>> iter = _users.entrySet().iterator();
			while(iter.hasNext()){
				Entry entry=(Entry) iter.next();
				String thekey=(String) entry.getKey();
				WebUser theUser=(WebUser) entry.getValue();
				if(!thekey.equals(key) && theUser.getId().equals(user.getId())){
					//重复登录时，删掉旧的
					_users.remove(thekey);
				}
			}
		}
		_users.put(key, user);
	}

	public static void remove(String key) {
		if (key == null) {
			return;
		}
		_users.remove(key);

	}

	public static int getUsersCount() {
		return (_users != null ? _users.size() : 0);
	}

	public static DataPackage<WebUser> doQuery(ParamsTable params) {
		DataPackage<WebUser> datas = new DataPackage<WebUser>();
		ArrayList<WebUser> result = new ArrayList<WebUser>(_users.values());

		datas.rowCount = result.size();
		int page, lines;
		try {
			page = Integer.parseInt(params.getParameterAsString("_currpage"));
		} catch (Exception ex) {
			page = 1;
		}
		try {
			lines = Integer.parseInt(params.getParameterAsString("_pagelines"));
		} catch (Exception ex) {
			lines = Integer.MAX_VALUE;
		}
		datas.pageNo = page;
		datas.linesPerPage = lines;
		// 分页
		try {
			datas.setDatas(result.subList((page - 1) * lines,
					(datas.rowCount > (page) * lines ? (page) * lines
							: datas.rowCount)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datas;
	}

}
