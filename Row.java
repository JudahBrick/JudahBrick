package tables;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnValuePair;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.InsertQuery;

import java.lang.IllegalArgumentException;


public class Row<T> implements Comparable{

	ArrayList<ColumnDescription> columns;	// the columns of the table
	RowValue[] values;		// The objects the values are being held in	
	Table table;	//The table frome which this row belongs
	boolean needToQuit;		// true if the row is illegal in some way
	//ColumnDescription primaryKey;	// The primary key colum for this table
	/**
	 * Creates a new Row to be put into a Table
	 * @param table The Table that is creating this Row
	 * @param query The InsertQuery we are creating from
	 */
	public Row(Table table, InsertQuery query) {
		this.table = table;
		columns = table.getColumns();	
		needToQuit = false;
		values = new RowValue[columns.size()];
		//primaryKey = table.getPrimaryKey();
		addValues(query);
	}
	
	/**
	 * Creates a new Row to be inserted into a Table
	 * @param query The InsertQuery we are using to create the new Row
	 */
	private <T> void addValues(InsertQuery query)
	{
		
		try{
			ColumnValuePair[] valuePairs = query.getColumnValuePairs();
			
			for(int i = 0; i < valuePairs.length; i++){		//loop through the new values
				String newValColName = valuePairs[i].getColumnID().getColumnName();		// the new value's column name
				for(int n = 0; i < columns.size(); n++){		//loop through the columns		
					String  colName = columns.get(n).getColumnName(); 		//the table's column name
					
					if(colName.equalsIgnoreCase(newValColName)){		//if same column name
						T newVal = (T) getDataType(columns.get(n), valuePairs[i].getValue());					
						//valuePairs[i].
						RowValue theActualValue = new RowValue (newValColName, newVal);
						values[n] = theActualValue;	
						break;
					}	
				}
			}
			//System.out.println("size of the row  " + values.);
			for(int i = 0; i < values.length; i++){
				if(values[i] == null){
					notRightAmmount();
					break;
				}
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			needToQuit = true;
		}
	}

	/**
	 * Returns the value the row has in the desired column, to use as a key in the BTree
	 * @param indexedColumn The column who's value we are looking for
	 * @return The value of the desired column
	 */
	public <T> T getPrimaryColumnValue(ColumnDescription indexedColumn)
	{
		T thePrimaryVal = null;
		for(int i = 0; i < values.length; i++){
			if(values[i].getColumn().equals(indexedColumn.getColumnName())){
				thePrimaryVal = (T) values[i].getValue();
				break;
			}
		}
		return thePrimaryVal;
	}
	
	/**
	 * Get the value for this column from the strings in the InsertQuery
	 * @param colDesc The column who's value is stored in the String
	 * @param value The string holding the desired value
	 * @return The value to be put in the row
	 */
	private <T> T getDataType(ColumnDescription colDesc, String value)
	{
		Scanner scanner = new Scanner(value);
		String type = colDesc.getColumnType().name();
		T theVal = null;

			switch(type){
		
				case "INT": Integer intVal = scanner.nextInt();	
					theVal = (T) intVal;
					break;
		
				case "DECIMAL": Double decimalVal = scanner.nextDouble();	
					theVal = (T) decimalVal;
					String str = decimalVal.toString();
					if(str.length() > colDesc.getFractionLength() + colDesc.getFractionLength()){
						throw new IllegalArgumentException("The decimal inserted into the " + colDesc.getColumnName() + " coulmn was too long");
						
					}
					break;

				case "BOOLEAN": Boolean booleanVal = scanner.nextBoolean();	
					theVal = (T) booleanVal;
					break;

				case "VARCHAR": if(!value.startsWith("'") || !value.endsWith("'")){
									throw new IllegalArgumentException("The " + colDesc.getColumnName() + " column must only be words");
								}
								if(value != null){
									String newStringVal = value.substring(1, value.length()-1);	
									theVal = (T) newStringVal;					 
									if(newStringVal.length() > colDesc.getVarCharLength() && colDesc.getVarCharLength() != 0){
										throw new IllegalArgumentException("The word(s) put into the '" + colDesc.getColumnName() + "' column"
												+ " are not allowed to be that long");											
									}
								}
					break;
			}
	
		return theVal;
	}
	
	/**
	 * Deals with missing values in the InsertQuery and inserts the default value for that column if there is one
	 */
	private <T> void notRightAmmount()
	{
		//ArrayList<Boolean> done = new ArrayList<>();
		for(int i = 0; i < columns.size(); i++){
			if(values[i] == null){		//if there was no value added from the insert query this slot should return null
				T fixedVal = fixNull(columns.get(i));
				RowValue fixedRowVal = new RowValue(columns.get(i).getColumnName(), fixedVal);
				values[i] = fixedRowVal;
			}
		}
	}
	
	/**
	 * Fixes a null value in the the InsertQuery
	 * @param colDesc The column that did not have a value inserted from the InsertQuery
	 * @return The default value or null
	 */
	private <T> T fixNull(ColumnDescription colDesc)
	{
		/*
		 * check dataType (not sure where in this method this needs to be done
		 * find out if column is unique
		 * if so check if isNotNull is true or false and add null if allowed
		 * if it is not unique check if it has default and then add default
		 * 
		 */
		T fixedVal = null;
		if(colDesc.isUnique()){		//if it is unique
			if(colDesc.isNotNull()){
				needToQuit = true;
				throw new IllegalArgumentException("The column " + colDesc.getColumnName() 
				+ " must have a unique value that is not null");
			}
			if(colDesc.getHasDefault()){
				fixedVal = getDataType(colDesc, colDesc.getDefaultValue());
			}
		}
		if(colDesc.getHasDefault()){
			fixedVal = getDataType(colDesc, colDesc.getDefaultValue());
		}
		
		return fixedVal;
	}
	
	/**
	 * Used to update a row and set a specific value
	 * @param columnNum	The column number of the desired column to change
	 * @param updateVal The new value to be placed in the row
	 */
	public void setValue(int columnNum, T updateVal)
	{
		values[columnNum].setValue(updateVal);
	}
	
	/**
	 * Retrieves the RowValue of  certain column
	 * @param columnNum The column number of the desired column
	 * @return The RowValue of the column
	 */
	public  RowValue getSpecificVal(int columnNum)
	{
		return  values[columnNum];
	}
	
	/**
	 * This is to know if the row should not be added due to some issue
	 * @return True if the row is an illegal row of somesort
	 */
	public boolean getNeedToQuit()
	{
		return needToQuit;
	}

	@Override
	public String toString()
	{
		
		String str = "";
		for(int i = 0; i< values.length; i++){
			str += values[i].toString() + "    ";
		}
		return str;
	}
	
	/**
	 * The size of the row
	 * @return The size of the row
	 */
	public int size()
	{
		return values.length;
	}

	@Override
	public int compareTo(Object o) {
		
		return 0;
	}
	
	//TODO why wont it let me override this?
	public boolean equals(Row row)
	{
		if(row.size() != this.size()){
			return false;
		}
		for(int i = 0; i < row.size(); i++){
			if(!this.getSpecificVal(i).getValue().equals(row.getSpecificVal(i).getValue())){
				return false;
			}
		}
		return true;
	}
}
