package tables;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery.OrderBy;

public class SelectRow <T> implements Comparable<SelectRow>{

	private OrderBy[] orderBys;		//The orderBys of the SelectQuery
	private RowValue[] values;		//The desired Values of the SelectQuery
	private int currentVal;
	
	/**
	 * Creates a new SelectRow to be inserted into a ResultSet
	 * @param orderBys The OrderBys of the Table
	 * @param rowLength	The eventual length of this Row
	 */
	public SelectRow(OrderBy[] orderBys, int rowLength)
	{
		this.orderBys = orderBys;
		values = new RowValue[rowLength];
	}

	/**
	 * Returns the RowValues of this SelectRow
	 * @return the RowValues of this SelectRow
	 */
	public RowValue[] getValues() {
		return values;
	}
	
	/**
	 * Adds a RowValue to this SelectRow
	 * @param rowVal the RowValue to be added to the Select row
	 */
	public void addValue(RowValue rowVal)
	{
		values[currentVal] = rowVal;
		currentVal++;
	}
	
	/**
	 * Returns the Size of this SelectRow
	 * @return the Size of this SelectRow
	 */
	public int size()
	{
		return values.length;
	}

	/**
	 * Returns a specific RowValue from the SelectRow
	 * @param columnNum The number of the Column who's RowValue you would like
	 * @return The desired RowValue
	 */
	public RowValue getSpecificVal(int columnNum)
	{
		return values[columnNum];
	}
	
	/*
	 * This is where the OrderBys from the SelectQuery are taken care of
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SelectRow row) {
		if(this.equals(row)){
			return 0;
		}
		int compare = 0;
		for(int n = 0; n < orderBys.length; n++){
			String columnName = orderBys[n].getColumnID().getColumnName();
			for(int i = 0; i < row.size(); i++){
				RowValue thisRow = this.getSpecificVal(i);
				RowValue otherRow = row.getSpecificVal(i);
				
				if(thisRow == null && otherRow != null){
					compare = -1;
					break;
				}
				if(thisRow == null && otherRow == null){
					compare = 0;
					break;
				}
				if(thisRow != null && otherRow == null){
					compare = 1;
					break;
				}
				if(thisRow.getColumn().equals(otherRow.getColumn()) && thisRow.getColumn().equals(columnName)){
					compare = thisRow.compareTo(otherRow);
					if(compare != 0){
						if(orderBys[n].isDescending()){
							compare *= -1;
						}
						return compare;
					}
					break;
				}
			}
		}
		
		
		return compare;
	}

	@Override
	public String toString()
	{
		String str = "";
		for(int i = 0; i < this.size(); i++){
			str += this.getSpecificVal(i).toString() + " ";
		}
		return str;
	}
	//@Override
	public boolean equals(SelectRow obj)
	{
		if(this.size() != obj.size()){
			return false;
		}
		for(int i = 0; i < this.size(); i++){
			if(!this.getSpecificVal(i).equals(obj.getSpecificVal(i))){
				return false;
			}
		}
		return true;
	}
	
}
