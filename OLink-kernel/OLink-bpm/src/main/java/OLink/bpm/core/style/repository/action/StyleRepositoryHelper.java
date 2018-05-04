package OLink.bpm.core.style.repository.action;

import java.util.Collection;

import OLink.bpm.base.action.BaseHelper;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.core.style.repository.ejb.StyleRepositoryProcess;
import OLink.bpm.core.style.repository.ejb.StyleRepositoryVO;
import OLink.bpm.util.ProcessFactory;

public class StyleRepositoryHelper extends BaseHelper<StyleRepositoryVO> {
	/**
	 * StyleRepositoryHelper构造函数
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @see BaseHelper#BaseHelper(IDesignTimeProcess)
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public StyleRepositoryHelper() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(StyleRepositoryProcess.class));
	}


	/**
	 * 根据应用标识,返回相应样式库(StyleRepository)集合
	 * 
	 * @param application
	 *            应用标识
	 * @return 样式库(StyleRepository)集合
	 * @throws Exception
	 */
	public Collection<StyleRepositoryVO> get_listStyleByApp(String application) throws Exception {
		StyleRepositoryProcess styleprocess = (StyleRepositoryProcess) ProcessFactory
				.createProcess(StyleRepositoryProcess.class);
		return styleprocess.getStyleRepositoryByApplication(application);
	}

	/**
	 * 根据样式主键查找,返回样式库内容
	 * 
	 * @param styleid
	 *            样式主键
	 * @return 样式库内容
	 * @throws Exception
	 */
	public static String getStyleContent(String styleid) throws Exception {
		StyleRepositoryProcess sp = (StyleRepositoryProcess) ProcessFactory
				.createProcess(StyleRepositoryProcess.class);
		StyleRepositoryVO sv = (StyleRepositoryVO) sp.doView(styleid);
		if (sv != null)
			return sv.getContent();
		else
			return null;
	}

}
