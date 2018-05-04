package OLink.bpm.core.report.crossreport.runtime.action;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleRow;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleData;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleMetaData;

/**
 * The analyze table data calculator, It get the result according one set of the
 * console data & what the calculation method in analyze table.
 * 
 */
public class Calculator {

	/**
	 * Compute the result according set of the console data & what the
	 * calculation method in analyze table
	 * 
	 * @param datas
	 *            The set of the console data.
	 * @param method
	 *            The calculation method
	 * @param metaData
	 *            The target field.
	 * @return The numeric result.
	 */
	public double compute(Collection<ConsoleRow> datas, CalculationMethod method, ConsoleMetaData metaData) {
		if (method == CalculationMethod.SUM)
			return sum(datas, metaData);
		else if (method == CalculationMethod.COUNT)
			return count(datas, metaData);
		else if (method == CalculationMethod.AVERAGE)
			return avg(datas, metaData);
		return 0;
	}

	/**
	 * Sum all the data
	 * 
	 * @param datas
	 *            The data
	 * @param metaData
	 *            The target field
	 * @return The total amount
	 */
	public double sum(Collection<ConsoleRow> datas, ConsoleMetaData metaData) {
		if (datas == null)
			return 0;

		double totalValue = 0;

		for (Iterator<ConsoleRow> iterator = datas.iterator(); iterator.hasNext();) {
			ConsoleRow row = iterator.next();
			ConsoleData consoleData = row.getData(metaData);
			double tmpValue = 0;
			if (consoleData != null){
				tmpValue = consoleData.getNumericValue();
				if (tmpValue == 0){
					try{
						tmpValue = new Double(consoleData.getStringValue());
					} catch (Exception e){

					}
				}
			}
			totalValue += tmpValue;
			//totalValue += (consoleData != null) ? consoleData.getNumericValue() : 0;
		}

		return totalValue;
	}

	/**
	 * count all the data
	 * 
	 * @param datas
	 *            The data
	 * @param metaData
	 *            The target field
	 * @return The total amount
	 */
	public int count(Collection<ConsoleRow> datas, ConsoleMetaData metaData) {
		if (datas == null)
			return 0;

		int totalValue = 0;

		for (Iterator<ConsoleRow> iterator = datas.iterator(); iterator.hasNext();) {
			ConsoleRow row = iterator.next();
			ConsoleData consoleData = row.getData(metaData);
			if (consoleData != null) {
				totalValue += 1;
			}
		}

		return totalValue;
	}

	/**
	 * avg all the data
	 * 
	 * @param datas
	 *            The data
	 * @param metaData
	 *            The target field
	 * @return The total amount
	 */
	public double avg(Collection<ConsoleRow> datas, ConsoleMetaData metaData) {
		if (datas == null)
			return 0;

		double totalValue = 0;
		int countValue = 0;

		for (Iterator<ConsoleRow> iterator = datas.iterator(); iterator.hasNext();) {
			ConsoleRow row = iterator.next();
			ConsoleData consoleData = row.getData(metaData);
			double tmpValue = 0;
			if (consoleData != null){
				tmpValue = consoleData.getNumericValue();
				if (tmpValue == 0){
					try{
						tmpValue = new Double(consoleData.getStringValue());
					} catch (Exception e){

					}
				}
				totalValue += tmpValue;
				countValue++;
			}
			
			/*if (consoleData != null) {
				totalValue += consoleData.getNumericValue();
				countValue++;
			}*/
		}

		if (countValue == 0)
			countValue = 1;

		return totalValue / countValue;
	}

	/**
	 * get max value
	 * 
	 * @param datas
	 *            The data
	 * @param method
	 *            The target field
	 * @return The total amount
	 */
	public ConsoleData max(Collection<ConsoleRow> datas, ConsoleMetaData metaData) {

		ConsoleData maxConsoleData = null;
		int i = 1;
		for (Iterator<ConsoleRow> iterator = datas.iterator(); iterator.hasNext(); i++) {
			ConsoleRow row = iterator.next();
			ConsoleData consoleData = row.getData(metaData);

			if (i == 1)
				maxConsoleData = consoleData;

			if (consoleData != null && (consoleData.compareTo(maxConsoleData) == 1)) {
				maxConsoleData = consoleData;
			}
		}
		return maxConsoleData;
	}

	/**
	 * get min value
	 * 
	 * @param datas
	 *            The data
	 * @param method
	 *            The target field
	 * @return The total amount
	 */
	public ConsoleData min(Collection<ConsoleRow> datas, ConsoleMetaData metaData) {

		ConsoleData minConsoleData = null;
		int i = 1;
		for (Iterator<ConsoleRow> iterator = datas.iterator(); iterator.hasNext(); i++) {
			ConsoleRow row = iterator.next();
			ConsoleData consoleData = row.getData(metaData);

			if (i == 1)
				minConsoleData = consoleData;

			if (consoleData != null && (consoleData.compareTo(minConsoleData) == -1)) {
				minConsoleData = consoleData;
			}
		}
		return minConsoleData;
	}
}
