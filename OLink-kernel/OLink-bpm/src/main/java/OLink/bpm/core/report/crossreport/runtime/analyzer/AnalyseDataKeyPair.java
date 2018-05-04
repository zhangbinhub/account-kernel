package OLink.bpm.core.report.crossreport.runtime.analyzer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleDataType;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleMetaData;


/**
 * The analyse data key pair, it usually use in the data key sorting.
 */
public class AnalyseDataKeyPair {
	/**
	 * The data key.
	 */
	String key;
	/**
	 * The analyse dimension.
	 */
	Collection<AnalyseDimension> analyseDimensions;

	/**
	 * Constructor with key and AnalyseDimension.
	 */
	public AnalyseDataKeyPair(String key,
			Collection<AnalyseDimension> analyseDimension) {
		this.key = key;
		this.analyseDimensions = analyseDimension;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the analyseDimension
	 */
	public Collection<AnalyseDimension> getAnalyseDimensions() {
		return analyseDimensions;
	}

	/**
	 * @param analyseDimension
	 *            the analyseDimension to set
	 */
	public void setAnalyseDimensions(
			Collection<AnalyseDimension> analyseDimensions) {
		this.analyseDimensions = analyseDimensions;
	}

	/**
	 * Compare another AnalyseDataKeyPair
	 * 
	 * @param p2
	 *            The another AnalyseDataKeyPair
	 * @return 1 if this bigger than p2, 0 if they are equal, -1 for small than
	 *         p2
	 */
	public int compareTo(AnalyseDataKeyPair p2) {

		String dim1[] = key.split(AnalyseDimension.DIM_DIM_SEPARATOR);
		String dim2[] = p2.getKey().split(AnalyseDimension.DIM_DIM_SEPARATOR);

		if (dim1 == null || dim1.length <= 0)
			return -1;

		if (dim2 == null || dim2.length <= 0)
			return 1;

		String[] keys1 = dim1[0].split(AnalyseDimension.KEY_KEY_SEPARATOR);
		String[] keys2 = dim2[0].split(AnalyseDimension.KEY_KEY_SEPARATOR);
		
     
		Object[] tempDimensions = analyseDimensions.toArray();
	    List<Object> list = Arrays.asList(tempDimensions);
	    Collections.reverse(list);
	    Object[] dimensions = list.toArray();
	    
		for (int i = 0; i < keys1.length; ++i) {
			String key2 = keys1.length ==keys2.length ?keys2[i]:"";
			if (!keys1[i].equals(key2)) {
				ConsoleMetaData metaData = ((AnalyseDimension) dimensions[i])	.getMetaData();

				if (metaData.getDataType().getValue() == ConsoleDataType.Integer.getValue()
						|| metaData.getDataType().getValue() == ConsoleDataType.Numberic.getValue()) {

					return (int) (new Double(keys1[i]).doubleValue() - Double
							.valueOf(keys2[i]).doubleValue());

				} else
					return keys1[i].compareTo(keys2[i]);
			}
		}

		return 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((analyseDimensions == null) ? 0 : analyseDimensions
						.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		AnalyseDataKeyPair other = (AnalyseDataKeyPair) obj;
		
		if (analyseDimensions == null) {
			if (other.analyseDimensions != null)
				return false;
		} else if (!analyseDimensions.equals(other.analyseDimensions))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}
}
