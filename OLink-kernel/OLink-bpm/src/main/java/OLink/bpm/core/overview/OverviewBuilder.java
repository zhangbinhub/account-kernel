package OLink.bpm.core.overview;

/**
 * 概览的生成实现类
 * 
 * 2.6版本新增的类
 * 
 * @author keezzm
 * 
 */
public class OverviewBuilder extends AbstractOverviewBuilder {

	public OverviewBuilder() {
		super();
	}

	public static OverviewBuilder getInstance() {
		return new OverviewBuilder();
	}

	public static void main(String[] args) throws Exception {
		new OverviewBuilder().buildOverview(
				"11de-ef9e-c010eee1-860c-e1cadb714510", "appOverview"
						+ System.currentTimeMillis() + ".pdf");
	}
}
