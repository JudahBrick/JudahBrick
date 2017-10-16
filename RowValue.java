package tables;

public class RowValue<T> implements Comparable<RowValue>{

	private String columnName;	//The column this is 
	private T value;	// The value in this column in this row
	
	/**
	 * Creates a new RowValue to be placed in a Row within a Table
	 * @param columnName The name of the column in the table/Row
	 * @param value The value to be placed in this column
	 */
	public RowValue(String columnName, Object value)
	{
		this.columnName = columnName;
		this.value = (T) value;
	}

	/**
	 * Retrieves the value
	 * @return The value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Retrieves the column name
	 * @return The column name
	 */
	public String getColumn() {
		return columnName;
	}	
	
	/**
	 * Sets the value
	 * @param val The new value you would like to set the value to
	 */
	public void setValue(T val)
	{
		this.value = val;
	}
	
	@Override
	public int compareTo(RowValue obj) {
		if(!(obj instanceof RowValue)){
			throw new IllegalArgumentException("Both RowValue Objects must be of type RowValue");
		}
		if(this.getValue() == null && obj.getValue() != null){
			return -1;
		}
		if(this.getValue() == null && obj.getValue() == null){
			return 0;
		}
		if(this.getValue() != null && obj.getValue() == null){
			return 1;
		}
		if(this.equals(obj)){
			return 0;
		}
		return ((Comparable) this.getValue()).compareTo(obj.getValue());
	}
		
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof RowValue)){
			return false;
		}
		RowValue other = (RowValue) obj;
		if(this.getColumn().equals(other.getColumn()) && this.getValue().equals(other.getValue())){
			return true;
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		if(columnName != null && value != null){
			return  columnName + ": "+ value.toString();
		}
		if(columnName != null && value == null)
		{
			return columnName + ": has a null value in this row";
		}
		else{
			return null;
		}
	}

	
}
