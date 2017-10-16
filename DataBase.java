package tables;

import java.util.ArrayList;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateIndexQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateTableQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.DeleteQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.InsertQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SQLParser;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SQLQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.UpdateQuery;
import net.sf.jsqlparser.JSQLParserException;

public class DataBase {
	
	private ArrayList<Table> tables;		//The list of Tables in this DataBase
	private SQLParser parser;
	
	public DataBase()
	{
		tables = new ArrayList<>();
		parser = new SQLParser();
	}
	
	/**
	 * Processes the SQL query and does what the SQL query says
	 * @param SQL The SQL String
	 * @return The ResultSet of the action
	 * @throws JSQLParserException
	 */
	public ResultSet execute(String SQL) throws JSQLParserException
	{
		try{
			//TODO actually return ResultSets and deal with where to do that
			SQLQuery query = parser.parse(SQL);
			if(query instanceof CreateTableQuery){	
				return createTable((CreateTableQuery)query);
			}
			if(query instanceof InsertQuery){
				return insertTable((InsertQuery)query);
			}
			if(query instanceof UpdateQuery){
				return updateTable((UpdateQuery)query);
			}
			if(query instanceof CreateIndexQuery){
				return createIndex((CreateIndexQuery) query);
			}
			if(query instanceof DeleteQuery){
				return delete((DeleteQuery)query);
			}
			if(query instanceof SelectQuery){
				return this.select((SelectQuery)query);
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		return null;
		
	}
	
	/**
	 * Deals with creating a table
	 * @param query the SQLQuery we are using
	 * @return The resultSet of the table after the action is done
	 */
	private ResultSet createTable(CreateTableQuery query)
	{
		Table newestTable = new Table(query);
		for(int i = 0; i < tables.size(); i++){
			if(tables.get(i).getTableName().equals(newestTable.getTableName())){
				throw new IllegalArgumentException("Can not create 2 tables with the same name");
			}
		}
		if(!newestTable.needToQuit()){
			tables.add(newestTable);
		}
		return newestTable.getResults();
	}
	
	/**
	 * Deals with inserting rows into a table
	 * @param query the SQLQuery we are using
	 * @return The resultSet of the table after the action is done
	 */
	private ResultSet insertTable(InsertQuery query)
	{
		for(int i = 0; i < tables.size(); i++){
			Table table = tables.get(i);
			if(table.getTableName().equals(query.getTableName())){
				table.addRow(query);
				return table.getResults();
			}
		}
		throw new IllegalArgumentException("The table you attempted to select from does not exsist");
	}
	
	/**
	 * Deals with creating an index to table
	 * @param query the SQLQuery we are using
	 * @return The resultSet of the table after the action is done
	 */
	private ResultSet createIndex(CreateIndexQuery query)
	{
		for(int i = 0; i < tables.size(); i++){
			Table table = tables.get(i);
			if(table.getTableName().equals(query.getTableName())){
				table.createIndex(query);
				return table.getResults();
			}
		}
		throw new IllegalArgumentException("The table you attempted to select from does not exsist");
	}
	
	/**
	 * Deals with updating a table
	 * @param query the SQLQuery we are using
	 * @return The resultSet of the table after the action is done
	 */
	private ResultSet updateTable(UpdateQuery query)
	{
		for(int i = 0; i < tables.size(); i++){
			Table table = tables.get(i);
			if(table.getTableName().equals(query.getTableName())){
				table.update(query);
				return table.getResults();
			}
		}
		throw new IllegalArgumentException("The table you attempted to select from does not exsist");
	}
	
	/**
	 * Deals with deleting rows of a table
	 * @param query the SQLQuery we are using
	 * @return The resultSet of the table after the action is done
	 */
	private ResultSet delete(DeleteQuery query)
	{
		for(int i = 0; i < tables.size(); i++){
			Table table = tables.get(i);
			if(table.getTableName().equals(query.getTableName())){
				table.delete(query);
				return table.getResults();
			}
		}
		throw new IllegalArgumentException("The table you attempted to select from does not exsist");
	}
	
	/**
	 * selects the desired values from the SQL query
	 * @param query the SQLQuery we are using
	 * @return The resultSet of the table after the action is done
	 */
	private ResultSet select(SelectQuery query)
	{
		String[] tableNames = query.getFromTableNames();
		for(int n = 0; n < tableNames.length; n++){
			for(int i = 0; i < tables.size(); i++){
				Table table = tables.get(i);
				if(table.getTableName().equals(tableNames[n])){	 
					 table.getResults().setResults(table.select(query));
					 return table.getResults();
				}
			}
		}
		throw new IllegalArgumentException("The table you attempted to select from does not exsist");
	}
	
	/**
	 * for Testing purposes.  Returns the first table of the DataBase
	 * @return
	 */
	protected Table getTable()
	{
		return tables.get(0);
	}

}
