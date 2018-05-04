package OLink.bpm.core.dynaform.document.dql;

import java.net.URL;

import antlr.Tool;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// JavaCodeGenerator.

		// ANTLR.compiler.
		URL url = Test.class.getClassLoader().getResource("");

		Tool
				.main(new String[] {
						"-o",
						"C:/Java/workspace/gb4.0/src/OLink/bpm/core/dynaform/document/dql",
						url.getPath()
								+ "OLink/bpm/core/dynaform/document/dql/dql.g"});
	}

}
