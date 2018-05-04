package OLink.bpm.core.report.query.action;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.deploy.module.ejb.ModuleProcess;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceProcess;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.report.query.ejb.Parameter;
import OLink.bpm.core.report.query.ejb.ParameterProcess;
import OLink.bpm.core.report.query.ejb.QueryProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.report.query.ejb.Query;
import eWAP.core.Tools;

public class QueryAction extends BaseAction<Query> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String _dts;
	
	private String _searchForm;

	private String _moduleid;

	private String _paramsDefaultValue[];
	
	private String _paramsName[];

	private Collection<Parameter> _reprotparams;

	public Collection<Parameter> get_reprotparams() throws Exception {
		if (getContent().getId() != null) {
			ParameterProcess process = (ParameterProcess) ProcessFactory
					.createProcess(ParameterProcess.class);
			_reprotparams = process.getParamtersByQuery(getContent().getId(),
					getApplication());
		}
		return _reprotparams;
	}

	public String[] get_paramsDefaultValue() {
		return _paramsDefaultValue;
	}

	public void set_paramsDefaultValue(String[] defaultValue) {
		_paramsDefaultValue = defaultValue;
	}

	public String[] get_paramsName() {
		return _paramsName;
	}

	public void set_paramsName(String[] name) {
		_paramsName = name;
	}

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public QueryAction() throws Exception {
		super(ProcessFactory.createProcess(QueryProcess.class), new Query());
	}

	public String get_moduleid() {
		Query qy = (Query) this.getContent();
		if (qy.getModule() != null) {
			return qy.getModule().getId();
		} else {
			return this._moduleid;
		}

	}

	public void set_moduleid(String _moduleid) {
		this._moduleid = _moduleid;
	}

	public String doSave() {
		try {
			Query qy = (Query) getContent();
			DataSourceProcess dp = (DataSourceProcess) (ProcessFactory
					.createProcess(DataSourceProcess.class));
			FormProcess fp = (FormProcess) (ProcessFactory
					.createProcess(FormProcess.class));
			Form form = (Form) fp.doView(_searchForm);
			DataSource dts = (DataSource) dp.doView(_dts);
			qy.setDataSource(dts);
			qy.setSearchForm(form);

			if (get_moduleid() != null && get_moduleid().trim().length() > 0) {
				ModuleProcess mp = (ModuleProcess) ProcessFactory
						.createProcess(ModuleProcess.class);
				ModuleVO mv = (ModuleVO) mp.doView(this.get_moduleid());
				qy.setModule(mv);
			}

			if (qy.getId() != null && qy.getId().trim().length() > 0
					&& _paramsName != null && _paramsName.length > 0) {
				ParameterProcess parprocess = (ParameterProcess) ProcessFactory
						.createProcess(ParameterProcess.class);

				Collection<Parameter> coll = parprocess.getParamtersByQuery(getContent()
						.getId(), getApplication());
				for (Iterator<Parameter> iter = coll.iterator(); iter.hasNext();) {
					Parameter pa = iter.next();
					parprocess.doRemove(pa.getId());
				}
				Collection<Parameter> pars = new HashSet<Parameter>();
				QueryHelper helper = new QueryHelper();
				Collection<String> params = helper.getParametersBySQL(qy
						.getQueryString());
				for (int i = 0; i < _paramsName.length; i++) {
					if (params.contains(_paramsName[i])) {
						Parameter paramter = new Parameter();
						paramter.setId(Tools.getSequence());
						paramter.setName(_paramsName[i]);
						paramter.setDefaultValue(_paramsDefaultValue[i]);
						paramter.setQuery(qy);
						paramter.setApplicationid(getApplication());
						parprocess.doUpdate(paramter);
						pars.add(paramter);
					}
				}
				qy.setParamters(pars);
			}
			qy.setApplicationid(getApplication());

			setContent(qy);
			return super.doSave();
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	public String get_dts() {
		Query qy = (Query) getContent();
		return (qy != null && qy.getDataSource() != null) ? qy.getDataSource()
				.getId() : null;
	}

	public void set_dts(String _dts) {
		this._dts = _dts;
	}

	public String get_searchForm() {
		Query vo = (Query) getContent();
		return (vo != null && vo.getSearchForm() != null) ? vo.getSearchForm()
				.getId() : null;
	}

	public void set_searchForm(String form) {
		_searchForm = form;
	}

}
