package OLink.bpm.core.overview;

import eWAP.itext.text.Table;

/**
 * 概览的pdf表格生成接口
 * 
 * 2.6版本新增的类
 * 
 * @author keezzm
 *
 */
public interface IOverview {

	Table buildOverview(String applicationId) throws Exception;
}
