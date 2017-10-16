package tables;

public class UniqueValue {

	private int columnNumber;
	private Object value;
	
	/**
	 * Creates a new UniqueValue which is used for testing if a row is unique
	 * @param columnNumber The number of the column in the table
	 * @param value the value to be tested for
	 */
	public UniqueValue(int columnNumber, Object value)
	{
		this.columnNumber = columnNumber;
		this.value = value;
	}
	
	/**
	 * Returns the ColumnNumber
	 * @return the ColumnNumber
	 */
	public int getColumnNumber()
	{
		return columnNumber;
	}
	
	/**
	 * returns the value
	 * @return the value
	 */
	public Object getValue()
	{
		return value;
	}

}
