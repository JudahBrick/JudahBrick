package tables;

import java.util.ArrayList;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition.Operator;

public class ConditionColumn <T, V> {

	private ColumnDescription column;
	private Operator operator;
	private T value;
	private BTree bTree;
	
	/**
	 * Creates a new ConditionColumn which holds one of the BTree options from a condition
	 * as well as the value operator and column from the condition
	 * @param columnDescription The ColumnDescription of the column in the condition
	 * @param operator The operator the condition is using
	 * @param value	The value on the right side of the condition statement
	 * @param bTree The BTree for this column
	 */
	ConditionColumn(ColumnDescription columnDescription, Operator operator, T value, BTree bTree)
	{
		column = columnDescription;
		this.operator = operator;
		this.value = value;
		this.bTree = bTree;
	}
	
	/**
	 * Returns the value from the condition
	 * @return The value from the Condition
	 */
	protected T getValue()
	{
		return value;
	}
	
	/**
	 * Returns the column that this ConditionColumn is for
	 * @return The ColumnDescription of this column
	 */
	protected ColumnDescription getColumn()
	{
		return column;
	}
	
	/**
	 * returns the Operator this condition is functining under
	 * @return The operator's string
	 */
	protected String getOperator()
	{
		return operator.toString();
	}

	/**
	 * Returns the BTree for this column
	 * @return The BTree for this column
	 */
	protected BTree getBTree() {
		return bTree;
	}

	/**
	 * Sets the BTree for this column
	 * @param bTree the BTree for this column
	 */
	protected void setBTree(BTree bTree) {
		this.bTree = bTree;
	}
	
	/**
	 * Returns all of the Row values that fit the condition from this column
	 * @return The list of the rows
	 */
	protected  ArrayList<V> getPossibleVals()
	{
		String str = operator.toString();
		
		switch(str){
			case "=": return bTree.get(value);
			
			case "<=": return bTree.lessOrEqual(value);
				
			case ">=": return bTree.greaterOrEqual(value);
				
			case "<": return bTree.getLess(value);
					
			case ">": return bTree.getGreater(value);
				
			case "<>":  ArrayList<V> helper = new ArrayList<>();
				ArrayList<V> notVals = bTree.getAll();
				for(int i = 0; i < notVals.size(); i++){
					if(!notVals.get(i).equals(value)){
						helper.add(notVals.get(i));
					}
				}
				return helper;	
		}
		return null;

	}
}
