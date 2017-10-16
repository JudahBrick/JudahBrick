package tables;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;

public class IndexColumn {

	private ColumnDescription columnDescription;
	private BTree bTree;
	private String indexName;
	
	/**
	 * Creates a new IndexColumn which holds all of the needed information of the column that is indexed
	 * @param columnDescription The ColumnDescription of this column
	 * @param bTree The BTree for this column
	 * @param indexName The name of this index
	 */
	public IndexColumn(ColumnDescription columnDescription, BTree bTree, String indexName)
	{
		this.columnDescription = columnDescription;
		this.bTree = bTree;
		this.indexName = indexName;
	}
	
	/**
	 * Returns the ColumnDescription of this indexed column
	 * @return the ColumnDescription of this indexed column
	 */
	public ColumnDescription getColumnDescription()
	{
		return columnDescription;
	}
	
	/**
	 * Returns the BTree for this indexed column
	 * @return the BTree for this indexed column
	 */
	public BTree getBTree()
	{
		return bTree;
	}

	/**
	 * Returns the name of this index
	 * @return the name of this index
	 */
	public String getIndexName() {
		return indexName;
	}

}
