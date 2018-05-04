package OLink.bpm.core.report.tablecolumn.action;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.report.reportconfig.ejb.ReportConfig;
import OLink.bpm.core.report.reportconfig.ejb.ReportConfigProcess;
import OLink.bpm.core.report.tablecolumn.ejb.TableColumn;
import OLink.bpm.core.report.tablecolumn.ejb.TableColumnProcess;
import OLink.bpm.util.ProcessFactory;

public class TableColumnAction extends BaseAction<TableColumn> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String _reportConfigid;

	private String _type;

	private Collection<TableColumn> _fieldList; // 根据_reportConfigid,_type取得

	private String _isSort;

	private String calculateMode[];

	private String _description[];

	private String _width[];

	private String _queryid;

	private String _orderNo[];

	private String _fontSize[];

	private String _backColor[];

	public String get_queryid() {
		return _queryid;
	}

	public void set_queryid(String _queryid) {
		this._queryid = _queryid;
	}

	public String[] get_description() {
		return _description;
	}

	public void set_description(String[] _description) {
		this._description = _description;
	}

	public String[] get_width() {
		return _width;
	}

	public void set_width(String[] _width) {
		this._width = _width;
	}

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public TableColumnAction() throws Exception {
		super(ProcessFactory.createProcess(TableColumnProcess.class), new TableColumn());
	}

	public String doSave() {
		try {
			DataPackage<TableColumn> data = ((TableColumnProcess) process).getFieldsByReportConfigAndType(_reportConfigid, _type,
					getApplication());
			if (data != null && data.datas != null && data.datas.size() > 0)
				for (Iterator<TableColumn> iter = data.datas.iterator(); iter.hasNext();) {
					TableColumn em = iter.next();
					this.process.doRemove(em.getId());
				}

			ReportConfigProcess rp = (ReportConfigProcess) (ProcessFactory.createProcess(ReportConfigProcess.class));
			ReportConfig rc = (ReportConfig) rp.doView(_reportConfigid);
			if (_selects != null && _selects.length > 0) {
				boolean flag = false;
				for (int i = 0; i < _selects.length; i++) {
					String name = _selects[i];
					TableColumn content = new TableColumn();
					content.setName(name);
					content.setReportConfig(rc);
					content.setType(_type);
					content.setDescription(_description[i]);
					content.setWidth(_width[i]);
					content.setBackColor(_backColor[i]);
					if (name.equals(this._isSort)) {
						content.setSort(true);
						flag = true;
					} else {
						content.setSort(false);
						if (flag)
							content.setCalculateMode(calculateMode[i - 1]);
						else
							content.setCalculateMode(calculateMode[i]);
					}
					if (!content.isSort() && _orderNo[i] != null && _orderNo[i].length() > 0)
						content.setOrderno(Integer.parseInt(_orderNo[i]));

					if (_fontSize[i] != null && _fontSize[i].length() > 0)
						content.setFontSize(Integer.parseInt(_fontSize[i]));

					content.setApplicationid(getApplication());
					this.process.doCreate(content);
				}
			}
			DataPackage<TableColumn> dt = ((TableColumnProcess) process).getFieldsByReportConfigAndType(_reportConfigid, _type,
					getApplication());
			if (dt != null)
				set_fieldList(dt.datas);
			return SUCCESS;
		} catch (ClassNotFoundException e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	public String doEdit() {
		try {
			DataPackage<TableColumn> data = ((TableColumnProcess) process).getFieldsByReportConfigAndType(_reportConfigid, _type,
					getApplication());
			if (data != null)
				set_fieldList(data.datas);
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}

	public String doNew() {
		try {
			DataPackage<TableColumn> data = ((TableColumnProcess) process).getFieldsByReportConfigAndType(_reportConfigid, _type,
					getApplication());
			if (data != null)
				set_fieldList(data.datas);
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}
	}

	public String doList() {
		try {
			DataPackage<TableColumn> data = ((TableColumnProcess) process).getFieldsByReportConfigAndType(_reportConfigid, _type,
					getParams().getParameterAsString("application"));
			setDatas(data);
			if (data != null)
				set_fieldList(data.datas);
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

	}

	public String get_reportConfigid() {
		return _reportConfigid;
	}

	public void set_reportConfigid(String configid) {
		_reportConfigid = configid;
	}

	public String get_type() {
		return _type;
	}

	public void set_type(String _type) {
		this._type = _type;
	}

	public Collection<TableColumn> get_fieldList() {
		return _fieldList;
	}

	public void set_fieldList(Collection<TableColumn> list) {
		_fieldList = list;
	}

	public String get_isSort() {
		return _isSort;
	}

	public void set_isSort(String sort) {
		_isSort = sort;
	}

	public String[] getCalculateMode() {
		return calculateMode;
	}

	public void setCalculateMode(String[] calculateMode) {
		this.calculateMode = calculateMode;
	}

	public String[] get_orderNo() {
		return _orderNo;
	}

	public void set_orderNo(String[] no) {
		_orderNo = no;
	}

	public String[] get_backColor() {
		return _backColor;
	}

	public void set_backColor(String[] color) {
		_backColor = color;
	}

	public String[] get_fontSize() {
		return _fontSize;
	}

	public void set_fontSize(String[] size) {
		_fontSize = size;
	}
}
