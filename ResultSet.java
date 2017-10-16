package tables;

import java.util.ArrayList;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;

public class ResultSet{

	
	private ArrayList<SelectRow> table; 
	private ArrayList<ColumnDescription> columns;
	
	/**
	 * Creates a new ResultSet from the given columns of a Table
	 * @param columns The columns from the Table we are creating this ResultSet for
	 */
	public ResultSet(ArrayList<ColumnDescription> columns)
	{
		table = new ArrayList<>();
		this.columns = columns;
	}
	
	/**
	 * Replaces the ResultSet's Table values with the new ones for this specific query
	 * @param resultSetValues The list of SelectRows required in the ResultSet for this qury
	 */
	public void setResults(ArrayList<SelectRow> resultSetValues)
	{
		table = resultSetValues;
	}
	
	/**
	 * Returns the Table of this ResultSet
	 * @return the Table of this ResultSet
	 */
	public ArrayList<SelectRow> getResults()
	{
		return table;
	}
	
	/**
	 * Returns the list of columns in this ResultSet
	 * @returnthe list of columns in this ResultSet
	 */
	public ArrayList<ColumnDescription> getColumns()
	{
		return columns;
	}
	
}
