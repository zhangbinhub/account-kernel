package OLink.bpm.core.image.repository.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.core.deploy.module.ejb.ModuleProcess;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.image.repository.ejb.ImageRepositoryProcess;
import OLink.bpm.core.image.repository.ejb.ImageRepositoryVO;
import OLink.bpm.base.action.BaseHelper;
import OLink.bpm.util.ProcessFactory;

/**
 * @author Marky
 */
public class ImageRepositoryHelper extends BaseHelper<ImageRepositoryVO> {

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public ImageRepositoryHelper() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(ImageRepositoryProcess.class));
	}

	/**
	 * 根据应用标识查询,返回图片库(ImageRepository)集合
	 * 
	 * @param application
	 *            应用标识
	 * @return 图片库集合
	 */
	public Map<String, String> get_listImage(String application) throws Exception {

		try {
			HashMap<String, String> imagelist = new HashMap<String, String>();
			Collection<String> modulelist = new HashSet<String>();
			ModuleProcess mp = (ModuleProcess) ProcessFactory
					.createProcess(ModuleProcess.class);
			ModuleVO mv = (ModuleVO) mp.doView(getModuleid());
			modulelist.add(mv.getId());
			String applicationid = mv.getApplication().getId();
			ModuleVO temp = null;
			while ((temp = mv.getSuperior()) != null) {
				modulelist.add(temp.getId());
				mv = temp;
			}
			ImageRepositoryProcess sp = (ImageRepositoryProcess) ProcessFactory
					.createProcess(ImageRepositoryProcess.class);

			Iterator<String> it = modulelist.iterator();
			while (it.hasNext()) {
				String mid = it.next();
				Collection<ImageRepositoryVO> col = sp
						.getImageRepositoryByModule(mid, application);
				for (Iterator<ImageRepositoryVO> iter = col.iterator(); iter.hasNext();) {

					ImageRepositoryVO ir = iter.next();
					imagelist.put(ir.getId(), ir.getName());

				}
			}

			Collection<ImageRepositoryVO> imageTemp = sp.getImageRepositoryByApplication(
					applicationid, application);
			for (Iterator<ImageRepositoryVO> iter = imageTemp.iterator(); iter.hasNext();) {
				ImageRepositoryVO ir = iter.next();
				imagelist.put(ir.getId(), ir.getName() != null ? ir.getName()
						: "");
			}
			return imagelist;

		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * 根据图片库主键查询,返回图片库内容.
	 * 
	 * @param styleid
	 *            图片样式主键
	 * @return Image Content
	 * @throws Exception
	 */
	public static String getImageContent(String styleid) throws Exception {
		ImageRepositoryProcess sp = (ImageRepositoryProcess) ProcessFactory
				.createProcess(ImageRepositoryProcess.class);
		ImageRepositoryVO sv = (ImageRepositoryVO) sp.doView(styleid);
		if (sv != null)
			return sv.getContent();
		else
			return null;
	}

	/**
	 * 根据图片主键,返回图片库对象
	 * 
	 * @param styleid
	 *            图片主键
	 * @return ImageRepositoryVO对象
	 * @throws Exception
	 */
	public ImageRepositoryVO doListByImageid(String imageid) throws Exception {
		ImageRepositoryProcess sp = (ImageRepositoryProcess) ProcessFactory
				.createProcess(ImageRepositoryProcess.class);
		ImageRepositoryVO datas = (ImageRepositoryVO) ((sp).doView(imageid));
		if (datas != null) {
			return datas;
		} else {
			return null;
		}
	}

}
