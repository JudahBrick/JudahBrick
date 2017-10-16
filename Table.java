package tables;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import org.junit.Test;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnID;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnValuePair;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition.Operator;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery.FunctionInstance;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery.OrderBy;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateIndexQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateTableQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.DeleteQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.InsertQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.UpdateQuery;

//import  Users.yehudabrick.COMPSCI.workspace.DataProject.DBproject.src.BTree.BTree; 

public class Table<V> {

	private ArrayList<ColumnDescription> columns;
	private BTree bTree;
	private ArrayList<IndexColumn> otherIndexes = new ArrayList<IndexColumn>();
	private ArrayList<ColumnDescription> indexedColumns = new ArrayList<>();
	private ArrayList<Row> theTable = new ArrayList<>();
	private ColumnDescription primaryKeyColumn;
	private String tableName;
	private ArrayList<String> columnNames = new ArrayList<>();
	private boolean toQuit = false;
	private ResultSet results;
	
	
	/**
	 * Takes a CreatTableQuery and creates a new table with the specified columns
	 * as well as a new BTree index for the PrimareyKeyColumn
	 * @param newTable  the CreateTableQuery to create the table
	 */
	protected Table(CreateTableQuery newTable)
	{
		columns = new ArrayList<ColumnDescription>();
		setTableName(newTable.getTableName());
		establishPrimaryKey(newTable);
		convertColumns(newTable);
		createIndex(primaryKeyColumn, "mainIndex");
		results = new ResultSet(columns);
		ifWorked("CreateTable");
	}
	
	/**
	 * Changes the ResultSet after each action is done to the table
	 * @param action The action that was just done to the table
	 */
	private void ifWorked(String action)
	{
		SelectRow selectRow = new SelectRow(null, 1);
		RowValue rowVal = new RowValue(action, !toQuit);
		selectRow.addValue(rowVal);
		ArrayList<SelectRow> selectResults = new ArrayList<>();
		selectResults.add(selectRow);
		results.setResults(selectResults);	
	}
	
	/**
	 * Takes the CreatTableQuery and adds each of the columns to the table
	 * @param tableQuery  The CreateTableQuery from the constructor
	 */
	private void convertColumns(CreateTableQuery tableQuery)
	{
		ColumnDescription[] queryColumns = tableQuery.getColumnDescriptions(); 
		for(int i = 0; i < queryColumns.length; i++){
			//Column nextColumn = new Column(column);
			if(columnNames.contains(queryColumns[i].getColumnName())){
				toQuit = true;
				throw new IllegalArgumentException("Can not have two cloumns with the same name");
			}
			columns.add(queryColumns[i]);
			columnNames.add(queryColumns[i].getColumnName());
		}
	}
	/**
	 * Creates a new BTree index with a different column as the keys
	 * @param indexQuery The CreateIndexQuery to create the index
	 */
	protected void createIndex(CreateIndexQuery indexQuery)
	{
		String columnName = indexQuery.getColumnName();
		for(int i = 0; i < columns.size(); i++){
			String tableColumn = columns.get(i).getColumnName();
			if(columnName.equals(tableColumn)){
				String indexName = indexQuery.getIndexName();
				createIndex(columns.get(i), indexName);
				//this.ifWorked("Create Index");
				
			}
		}
		ifWorked("Create Index");
	}
	
	/**
	 * Creates the actual BTree index
	 * @param colDesc	The ColumnDiscription of the column that will be indexed
	 * @param indexName  The name for the new index
	 * @return	True or false if the index is created or not
	 */
	private boolean createIndex(ColumnDescription colDesc, String indexName)
	{
		String dataType = getDataType(colDesc);
		if(colDesc.equals(primaryKeyColumn) && bTree == null){		
			bTree = new BTree(dataType);
			indexedColumns.add(colDesc);
			return true;
		}
		for(int i = 0; i < otherIndexes.size(); i++){
			IndexColumn indexedColumn = otherIndexes.get(i);
			if(indexedColumn.getColumnDescription().equals(colDesc) 
					|| indexedColumn.getIndexName().equals(indexName)){
				throw new IllegalArgumentException("There can only be one index per column, or you named"
						+ "the index the same as another index");
			}
		}
		BTree secondaryIndex = new BTree(dataType);
		fillBTree(colDesc, secondaryIndex);
		IndexColumn newIndex = new IndexColumn(colDesc, secondaryIndex, indexName);
		otherIndexes.add(newIndex);
		indexedColumns.add(colDesc);
		return true;
	}
	
	
	/**
	 * Gets the name of the data type for the BTree keys
	 * @param colDesc the ColumnDescription we are making the BTree for
	 * @return  String the name of the DataType to be used in the BTree
	 */
	private String getDataType(ColumnDescription colDesc)
	{
		String type = colDesc.getColumnType().name();
		String theVal = null;

			switch(type){
		
				case "INT": theVal = "java.lang.Integer";  // the class name for integer wrapper class
					break;
		
				case "DECIMAL": theVal = "java.lang.Double";//  the class name for double or big decimal or wtv
					break;

				case "BOOLEAN":theVal = "java.lang.Boolean";// class name for boolean wrapper class
					break;

				case "VARCHAR":  theVal = "java.lang.String";// class name for string
					break;
			}

		return theVal;
	}

	/**
	 * Adds all of the rows from the table to the new index
	 * @param colDesc The CollumnDescription of the column we are making the index for
	 * @param bTree The newly created BTree
	 */
	private <T> void fillBTree(ColumnDescription colDesc, BTree bTree)
	{
		/*
		 * need to deel with null values in a column
		 */
		for(int i = 0; i < theTable.size(); i++){
			Row row = theTable.get(i);
			T theKey = (T) row.getPrimaryColumnValue(colDesc);
			bTree.add(theKey, row);
		}
	}
	
	
	/**
	 * Adds a new Row into the table and all of its indexes
	 * @param query The InsertQuery we are creating the row from
	 */
	protected <T> void addRow(InsertQuery query)
	{
		Row row = new Row(this, query);
		try{
			if(row.getNeedToQuit()){
				toQuit = true;
				ifWorked("Insert");
				toQuit = false;
				return;
			}
			if(!theTable.isEmpty()){
				testUnique(row);		
			}
			testNull(row);

			theTable.add(row);
			addToBTrees(row);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		ifWorked("Insert");
	}
	
	/**
	 * Tests if this row is unique or not in the necessary unique columns
	 * @param row  The row we are testing
	 */
	private void testUnique(Row row)
	{
		ArrayList<UniqueValue> uniques = new ArrayList<>();
		for(int i = 0; i < row.size(); i++){	
			if(columns.get(i).isUnique()  || columns.get(i).equals(primaryKeyColumn)){// if this column is unique or it is the primary key column
				Object val = row.getSpecificVal(i).getValue();
				
				UniqueValue newUnique = new UniqueValue(i, val);
				uniques.add(newUnique);
			}
		}
		for(int i = 0; i < theTable.size(); i++){
			Row currentRow = theTable.get(i);
			for(int n = 0; n < uniques.size(); n++){
				
				int theColumnNumberWeAreChecking = uniques.get(n).getColumnNumber();
				Object rowValueTestingForUnique = uniques.get(n).getValue();
				Object tableValue = currentRow.getSpecificVal(theColumnNumberWeAreChecking).getValue();
				if(tableValue == null){
					break;
				}
				if(tableValue.equals(rowValueTestingForUnique)){
					
					throw new IllegalArgumentException("All values in the " + columns.get(theColumnNumberWeAreChecking).getColumnName()
							+ " column must be unique.  Can not add the value: " + rowValueTestingForUnique);
				}
			}
		}	
	}

	 
	/**
	 * Tests if the row has any illegal null values
	 * @param row	The row we are testing for
	 */
	private void testNull(Row row)
	{
		for(int i = 0; i < row.size(); i++){
			//not null columns
			if(columns.get(i).isNotNull() || columns.get(i).equals(primaryKeyColumn)){
				if(row.getSpecificVal(i).getValue() == null){
					throw new IllegalArgumentException("The values for the column '" 
				+ columnNames.get(i) + "' must not have null values.");
				}
			}
		}
		
	}
	
	/**
	 * Deals with adding the new row to all of the BTree indexes
	 * @param row	The Row being added
	 */
	private <T> void addToBTrees(Row row)
	{
		/*
		 * need to deal with null values in a column
		 */
		T theKey = (T) row.getPrimaryColumnValue(primaryKeyColumn);
		bTree.add(theKey, row);
		for(int i = 0; i < otherIndexes.size(); i++){
			IndexColumn secondaryIndex = otherIndexes.get(i);
			ColumnDescription colDesc = secondaryIndex.getColumnDescription();
			BTree secondaryBTree = secondaryIndex.getBTree();
			int columnNum = columns.indexOf(colDesc);
			T secondaryKey = (T) row.getSpecificVal(columnNum).getValue();
			secondaryBTree.add(secondaryKey, row);
		}
	}
	
	/**
	 * Returns the column names 
	 * @return ArrayList<String>  The list of column names
	 */
	protected ArrayList<String> getColumnNames()
	{
		return columnNames;
	}
	
	/**
	 * The size of the table measured by the amount of rows it has
	 * @return int the size
	 */
	protected int size()
	{
		return theTable.size();
	}
	
	/**
	 * Takes a DeleteQuery and deletes the desired rows
	 * @param query The DeleteQuery
	 * @return True or false if the deletion worked
	 */
	protected void delete(DeleteQuery query)
	{
		Condition condition = query.getWhereCondition();
		if(condition == null){
			Iterator it = theTable.iterator();
			
			while(it.hasNext()){
				Row row = (Row)it.next();
				deleteRow(row);
				it.remove();
			}
			ifWorked("Delete");
		}
		ArrayList<V> searchList = getSearchTree(condition);
		if(searchList == null){
			searchList = bTree.getAll();
		}
		for(int i = 0; i < searchList.size(); i++){
			Row currentRow = (Row)searchList.get(i);
			if(checkConditionForRow(condition, currentRow)){
				deleteRow(currentRow);
				theTable.remove(currentRow);
			}
		}
		ifWorked("Delete");
	}
	
	/**
	 * Deletes a single row
	 * @param Row The row to be deleted
	 */
	private <K, T> void deleteRow(Row row)
	{
		//theTable.remove(row);
		K theKey = (K) row.getPrimaryColumnValue(primaryKeyColumn);
		bTree.delete(theKey);
		for(int i = 0; i < otherIndexes.size(); i++){
			IndexColumn secondaryIndex = otherIndexes.get(i);
			ColumnDescription colDesc = secondaryIndex.getColumnDescription();
			BTree secondaryBTree = secondaryIndex.getBTree();
			int columnNum = columns.indexOf(colDesc);
			T secondaryKey = (T) row.getSpecificVal(columnNum).getValue();
			secondaryBTree.delete(secondaryKey);
		}
		
	}
	
	/**
	 * Takes an UpdateQuery and updates the rows as per the query's specifications
	 * @param query	The UpdateQuery we are updating from
	 * @return True or false if the action was completed
	 */
	protected void update(UpdateQuery query)
	{
		Condition condition = query.getWhereCondition();
		if(condition == null){
			updateAll(query);
			ifWorked("Update");
			return;
		}
		ArrayList<V> searchList = getSearchTree(condition);
		if(searchList ==null){
			searchList = bTree.getAll();
		}
		for(int i = 0; i < searchList.size(); i++){
			Row currentRow = (Row)searchList.get(i);
			if(checkConditionForRow(condition, currentRow)){
				updateRow(query, currentRow);
			}
		}
		ifWorked("Update");
	}
	
	/**
	 * Updates all rows
	 * @param query	The UpdateQuery we are updating from
	 * @return	True or false if the update was completed
	 */
	private boolean updateAll(UpdateQuery query)
	{
		for(int i = 0; i < theTable.size(); i++){
			Row currentRow = theTable.get(i);
			updateRow(query, currentRow);
		}
		return !toQuit;
	}
	
	
	/**
	 * Updates a Row using the UpdateQuery and the Row we would like to update
	 * @param query the UpdateQuery with the update information
	 * @param row The row to be updated
	 */
	private <T> void updateRow(UpdateQuery query, Row row)
	{
		ColumnValuePair[] valuePairs = query.getColumnValuePairs();
		
		for(int i = 0; i < valuePairs.length; i++){		//loop through the new values
			String updateColName = valuePairs[i].getColumnID().getColumnName();		// the new value's column name
			for(int n = 0; i < columns.size(); n++){		//loop through the columns		
				String  colName = columns.get(n).getColumnName(); 		//the table's column name
				
				if(colName.equalsIgnoreCase(updateColName)){		//if same column name
					T newVal = (T) getConditionDataType(columns.get(n), valuePairs[i].getValue());					
					//valuePairs[i].
					row.setValue(n, newVal);
					break;
				}	
			}
		}
	}
	
	
	/**
	 * Takes a SelectQuery and returns the desired values from the desired rows
	 * @param query	The SelectQuery we are using and selecting from
	 * @return	ArrayList<SelectRow> a List of the desired values from the desired rows
	 */
	protected ArrayList<SelectRow> select(SelectQuery query)
	{
		if(query.getFunctionMap().isEmpty()){
			return valueSelect(query.getWhereCondition(), query.getSelectedColumnNames(), query.getOrderBys(), query.isDistinct());
		}

		if(query.getFunctionMap().size() > 0){
			ArrayList<SelectRow> selectRows = new ArrayList<>();
			SelectRow functionSelectRow = functionSelect(query.getWhereCondition(), query.getFunctionMap(), query.getSelectedColumnNames());
			//TODO make function select method
			selectRows.add(functionSelectRow);
			return selectRows;
		}
		return null;
		
	}
	
	/**
	 * Deals with the SelectQueries that want values from the table
	 * @param condition	The condition from the SelectQuery
	 * @param columnNames The column names of the desired column values
	 * @param orderBys The list of the order in which to return the SelectQuery
	 * @param distinct If the SelectQuery requires each row to be distinct or not
	 * @return The list of the desired rows
	 */
	private <T> ArrayList<SelectRow> valueSelect(Condition condition, ColumnID[] columnNames, OrderBy[] orderBys, boolean distinct)
	{
		ArrayList<SelectRow> selectedRows = new ArrayList<>();
		ArrayList<V> searchList = getSearchTree(condition);
		if(searchList ==null){
			searchList = bTree.getAll();
		}
		for(int i = 0; i < searchList.size(); i++){
			SelectRow selectRow = new SelectRow(orderBys, columnNames.length);
			Row row = (Row) searchList.get(i);
			if(checkConditionForRow(condition, row)){
				for(int n = 0; n < columnNames.length; n++){
					String colName = columnNames[n].getColumnName();
					int colNum = this.columnNames.indexOf(colName);
					RowValue rowVal = row.getSpecificVal(colNum);
					selectRow.addValue(rowVal);
				}
				if(distinct){
					if(selectedRows.contains(selectRow)){
						break;
					}
				}
				selectedRows.add(selectRow);
			}
		}
		if(orderBys.length > 0){
			selectedRows = orderBy(selectedRows);
		}
		
		return selectedRows;
	}
	
	/**
	 * This orders the selected row in the desired fashion as told to us by the SelectQuery
	 * @param selectedRows the list of already selected rows in the incorrect order
	 * @return The list of selected rows in the appropriate order
	 */
	private ArrayList<SelectRow> orderBy(ArrayList<SelectRow> selectedRows)
	{
		ArrayList<SelectRow> helper = new ArrayList<>();
		for(int i = 0; i < selectedRows.size(); i++){
			SelectRow currentRow = selectedRows.get(i);
			
			int helperSize = helper.size();
			for(int n = 0; n < helperSize; n++){
				SelectRow helperListRow = helper.get(n);
				int compare = currentRow.compareTo(helperListRow);
				if(compare > 0){
				}
				
				if(compare < 0){
					helper.add(n, currentRow);
					break;
				}
				if(compare == 0){
					helper.add(n, currentRow);
					break;
				}	
				if(n == helperSize -1){
					helper.add(currentRow);
					break;
				}
			}
			if(helper.isEmpty()){
				helper.add(currentRow);
			}
		}		
		return helper;
	}
	
	//TODO Write the javadoc for this after i decide how to actually write this method
	/**
	 * Delas with the SelectQueries dealing with functions
	 * @param condition The condition for the SelectQuery
	 * @param functionMap The Map Holding the Functions
	 * @param columnIDs The ColumnIDs that will have functions for them
	 * @return The SelectRow holding the information of the Functions
	 */
	private SelectRow functionSelect(Condition condition, Map<ColumnID,FunctionInstance> functionMap, ColumnID[] columnIDs)
	{
		SelectRow selectRow = new SelectRow(null, functionMap.size());
		
		for(int i = 0; i < columnIDs.length; i++){
			ColumnID key = columnIDs[i];
			FunctionInstance currentFunction = functionMap.get(key);
			Number finishedNum = null;
			int columnNum = columnNames.indexOf(key.getColumnName());
			
			ArrayList<V> searchList = getSearchTree(condition);
			if(searchList ==null){
				searchList = bTree.getAll();
			}
			ArrayList<V> helper = new ArrayList<>();
			for(int n = 0; n < searchList.size(); n++){
				Row row = (Row) searchList.get(n);
				if(this.checkConditionForRow(condition, row)){
					helper.add((V)row);
				}
			}
			searchList = helper;
			SelectRow functionsRow = new SelectRow(null, functionMap.size());
			switch (currentFunction.function.toString()){
			
			case "SUM": finishedNum = sumFunction(columnNum, currentFunction, condition, searchList);
			break;
			case "AVG": finishedNum = avgFunction(columnNum, currentFunction, condition, searchList);
			break;
			case "COUNT": finishedNum = countFunction(columnNum, currentFunction, condition, searchList);
			break;
			}

			if(finishedNum != null){
				
				RowValue rowVal = new RowValue(key.getColumnName(), finishedNum);
				selectRow.addValue(rowVal);
			}
		}
		return selectRow;
		
	}
	
	/**
	 * Deals with the Count function
	 * @param columnNum The number of the column in the Table
	 * @param function The function to be done on this column
	 * @param condition The condition for this function
	 * @param searchList The list of rows that may be included in the function
	 * @return The count of the columns
	 */
	private Number countFunction(int columnNum, FunctionInstance function, Condition condition,ArrayList<V> searchList)
	{
		int count = 0;
		ArrayList<V> addedVals = new ArrayList<>();
		for(int i = 0; i < searchList.size(); i++){
			Row row = (Row)searchList.get(i);
			V val = (V)row.getSpecificVal(columnNum).getValue();
			if(function.isDistinct && val != null){
				if(!addedVals.contains(val)){
					count++;
					addedVals.add(val);
				}
			}
			if(!function.isDistinct && val != null){
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Deals with the Sum function
	 * @param columnNum The number of the column in the Table
	 * @param function The function to be done on this column
	 * @param condition The condition for this function
	 * @param searchList The list of rows that may be included in the function
	 * @return The sum of the values in this columns
	 */
	private Number sumFunction(int columnNum, FunctionInstance function, Condition condition,ArrayList<V> searchList)
	{
		ColumnDescription colDesc = columns.get(columnNum);
		String colType = colDesc.getColumnType().name();
		if(!colType.equals("INT")  && !colType.equals("DECIMAL")){
			return null;
		}
		ArrayList<Double> usedInts = new ArrayList<>();
		Double sum = 0.0;
		Number rtn = null;
		for(int i = 0; i < searchList.size(); i++){
			Row row = (Row)searchList.get(i);
			
			Double newVal = null;
			if(colType.equals("INT")){
				Integer castInt = (Integer)row.getSpecificVal(columnNum).getValue();
				newVal = castInt.doubleValue();
			}
			if(colType.equals("DECIMAL")){
				newVal = (Double)row.getSpecificVal(columnNum).getValue();
			}
			if(function.isDistinct){
				if(!usedInts.contains(newVal) && newVal != null){
					sum += newVal;
					usedInts.add(newVal);
				}
			}
			if(!function.isDistinct){
				if(newVal != null){
					sum +=  newVal;
				}
			}
		}	
		rtn = sum;
		if(colType.equals("INT")){
			rtn = sum.intValue();
		}
		return rtn;
	}
	
	/**
	 * Deals with the Average function
	 * @param columnNum The number of the column in the Table
	 * @param function The function to be done on this column
	 * @param condition The condition for this function
	 * @param searchList The list of rows that may be included in the function
	 * @return The average of the values in this columns
	 */
	private Number avgFunction(int columnNum, FunctionInstance function, Condition condition,ArrayList<V> searchList )
	{
		//TODO deal with how to decide if there is no sum or null sums or illegal sums or wtv
		ColumnDescription colDesc = columns.get(columnNum);
		String colType = colDesc.getColumnType().name();
		if(!colType.equals("INT")  && !colType.equals("DECIMAL")){
			return null;
		}
		ArrayList<Double> usedInts = new ArrayList<>();
		Double avg = 0.0;
		int addedNums = 0;
		Number rtn = null;
		
		for(int i = 0; i < searchList.size(); i++){
			Row row = (Row)searchList.get(i);
			Double newVal = null;
			if(colType.equals("INT")){
				Integer castInt = (Integer)row.getSpecificVal(columnNum).getValue();
				newVal = castInt.doubleValue();
			}
			if(colType.equals("DECIMAL")){
				newVal = (Double)row.getSpecificVal(columnNum).getValue();
			}
			if(function.isDistinct && newVal != null){
				if(!usedInts.contains(newVal) && newVal != null){
					avg += newVal;
					usedInts.add(newVal);
					addedNums++;
				}
			}
			if(!function.isDistinct && newVal != null){
				if(newVal != null){
					avg +=  newVal;
					addedNums++;
				}
			}
		}	
		avg = avg / addedNums;
		rtn = avg;
		if(colType.equals("INT")){
			rtn = avg.intValue();
		}
		return rtn;
	}
	/**
	 * Returns the list of possible rows from this condition
	 * @param condition the condition from the SQLQuery
	 * @return The list of Possible rows
	 */
	private ArrayList<V> getSearchTree(Condition condition)
	{
		ArrayList<ConditionColumn> treeOptions = findConditionIndexColumns(condition);
		if(treeOptions.size() > 0){
			for(int i = 0; i < treeOptions.size(); i++){
				if(treeOptions.get(i).getOperator().equals("=")){
					return treeOptions.get(i).getPossibleVals();
				}
				
			}
			return treeOptions.get(0).getPossibleVals();
		}
		return null;
	}
	
	/**
	 * Creates the ConditionColumns for each of the possible indexed columns we may use to search
	 * @param condition The condition from the SQLQuery
	 * @return The List of ConditionColumn options
	 */
	private <T> ArrayList<ConditionColumn> findConditionIndexColumns(Condition condition)
	{
		ArrayList<ConditionColumn> indexedColumnsInCondition = new ArrayList<ConditionColumn>();
		if(condition == null){
			return indexedColumnsInCondition;
		}
		if(condition.getLeftOperand() instanceof ColumnID){
			String columnName = ((ColumnID)condition.getLeftOperand()).getColumnName();
			int columnNumber = columnNames.indexOf(columnName);
			ColumnDescription colDesc = columns.get(columnNumber);
			T conditionVal = getConditionDataType(colDesc, (String)condition.getRightOperand());
			
			if(colDesc.equals(primaryKeyColumn)){		//if the column is that of the primary key
				ConditionColumn conditionColumn = new ConditionColumn(colDesc, condition.getOperator(), conditionVal, bTree);
				indexedColumnsInCondition.add(conditionColumn);
			}
			if(indexedColumns.contains(colDesc)  && !(colDesc.equals(primaryKeyColumn))){
				int treeNumber = indexedColumns.indexOf(colDesc) -1;
				BTree theTree = otherIndexes.get(treeNumber).getBTree();
				ConditionColumn conditionColumn = new ConditionColumn(colDesc, condition.getOperator(), conditionVal, theTree);
				indexedColumnsInCondition.add(conditionColumn);
			}
		}
		if(!(condition.getLeftOperand() instanceof ColumnID)){
			indexedColumnsInCondition.addAll(findConditionIndexColumns((Condition) condition.getRightOperand()));
			indexedColumnsInCondition.addAll(findConditionIndexColumns((Condition) condition.getLeftOperand()));
		}
		return indexedColumnsInCondition;
	}
	
	/**
	 * This checks is specific row fits the Conditions asked for in the SQLQuery
	 * @param condition The condition from the SQLQuery
	 * @param row the Row to be tested
	 * @return True or False if the Row passes the condition
	 */
	private <T> boolean checkConditionForRow(Condition condition, Row row)
	{
		if(condition == null){
			return true;
		}
		if(condition.getLeftOperand() instanceof ColumnID){
			String columnName = ((ColumnID) condition.getLeftOperand()).getColumnName();
			String stringUpdateVal = ((String) condition.getRightOperand());
			T updateVal = null;
			ColumnDescription updateColumn = null;
			for(int i = 0; i < columns.size(); i++){
				if(columns.get(i).getColumnName().equalsIgnoreCase(columnName)){
					updateColumn = columns.get(i);
					updateVal = getConditionDataType(updateColumn, stringUpdateVal);
					break;
				}
			}
			if(updateColumn == null){
				throw new IndexOutOfBoundsException("The column in your condition for update does not exist");
			}
			return goodRow(updateColumn, condition.getOperator(), updateVal, row);
		}
		if(condition.getOperator().toString().equals("AND")){
			boolean leftCondition = checkConditionForRow((Condition)condition.getLeftOperand(), row);
			boolean rightCondition = checkConditionForRow((Condition)condition.getRightOperand(), row);
			return (leftCondition && rightCondition);
		}
		if(condition.getOperator().toString().equals("OR")){
			boolean leftCondition = checkConditionForRow((Condition)condition.getLeftOperand(), row);
			boolean rightCondition = checkConditionForRow((Condition)condition.getRightOperand(), row);
			return (leftCondition || rightCondition);
		}
		return false;
	}
	
	/**
	 * Returns the type of the value input into the condition of the SQLQuery
	 * @param colDesc The column the condition is dealing with
	 * @param value The String value we would like to get the value of
	 * @return the value of the condition
	 */
	private <T> T getConditionDataType(ColumnDescription colDesc, String value)
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
						throw new IllegalArgumentException("The decimal update into the " + colDesc.getColumnName() + " coulmn was too long");	
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
	 * Checks if this row fits the condition from the SQLQuery
	 * @param column the ColumnDescription of the column we are testing for
	 * @param operator the operator the condition is functioning on
	 * @param updateVal the condition value
	 * @param rowthe row we are testing
	 * @return True or false if the Row passes the condition
	 */
	private <T> boolean goodRow(ColumnDescription column, Operator operator, T updateVal, Row row)
	{
		int columnNumber = columns.indexOf(column);
		T rowVal = (T)row.getSpecificVal(columnNumber).getValue();
		String str = operator.toString();
		if(rowVal == null){
			switch(str){
			case "=": return updateVal.equals(rowVal);
			
			case "<=": if(updateVal == null){
				return true;
			}
			return true;
				
			case ">=": if(updateVal == null){
				return true;
			}
			return false;
			
			case "<": if(updateVal == null){
				return false;
			}
			return true;
			
			case ">": return false;
				
			
			case "<>": return !(updateVal.equals(rowVal));
				
			}
		}
		int compare = ((Comparable) rowVal).compareTo(updateVal);			// comppares the update val and the row value
		
		switch(str){
			case "=": return updateVal.equals(rowVal);
			
			case "<=": if(compare <= 0){
				return true;
			}
				break;
			
			case ">=": if(compare >= 0){
				return true;
			}
				break;
			
			case "<": if(compare < 0){
				return true;
			}
				break;
			
			case ">": if(compare > 0){
				return true;
			}
				break;
			
			case "<>": if(compare != 0){
				return true;
			}
				break;
		}
			
		
		return false;
	}
	

	/**
	 * returns the BTree of the indexed promeryKey column of this table
	 * @return the BTree of the indexed promeryKey column of this table
	 */
	protected BTree getBTree()
	{
		return bTree;
	}
	
	/**
	 * Establishes the Primary key column
	 * @param tableQuery the CreateTableQuery that hold the primary key
	 */
	private void establishPrimaryKey(CreateTableQuery tableQuery)
	{
		primaryKeyColumn = tableQuery.getPrimaryKeyColumn();
	}
	
	/**
	 * Returns the ColumnDescription of the Primary key column
	 * @returnthe ColumnDescription of the Primary key column
	 */
	protected ColumnDescription getPrimaryKey()
	{
		return primaryKeyColumn;
	}

	/**
	 * Returns the Table's name
	 * @return the Table's name
	 */
	protected String getTableName() {
		return tableName;
	}

	/**
	 * sets the name for the table
	 * @param tableName the name for the table
	 */
	protected void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	/**
	 * returns a list of the ColumnDescription of this table
	 * @return the  list of the ColumnDescription of this table
	 */
	protected ArrayList<ColumnDescription> getColumns()
	{
		return columns;
	}
	
	/**
	 * if this Table is not supposed to be created or had an error during an action
	 * @return true if the table had an error
	 */
	protected boolean needToQuit()
	{
		return toQuit;
	}

	/**
	 * returns the ResultSet of this table
	 * @returnthe ResultSet of this table
	 */
	protected ResultSet getResults() {
		return results;
	}

	/**
	 * sets the table of the ResultSet of this table
	 * @param results the new results
	 */
	protected void setResults(ArrayList<SelectRow> results) {
		this.results.setResults(results);
	}

}
