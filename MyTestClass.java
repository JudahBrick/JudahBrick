package tables;

import java.util.ArrayList;

import org.junit.Test;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateIndexQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateTableQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.DeleteQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.InsertQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SQLParser;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.UpdateQuery;
import net.sf.jsqlparser.JSQLParserException;

public class MyTestClass {

	
	private static final char[] Delete = null;
	private static SQLParser parser;
	private static DataBase myDataBase = new DataBase();
	
	public static void main(String[] args) throws JSQLParserException
	{
		parser = new SQLParser();
		createTableTest();
		testCreateIndex();
		insertTest(makeLi4());
		testSelect();
		testUpdate();
		testDelete();
	}


	public static void createTableTest() throws JSQLParserException
	{

		String query = "CREATE TABLE YCStudent"
			+ "("
			+ " BannerID int,"
			+ " SSNum int UNIQUE,"
			+ " FirstName varchar(255),"
			+ " LastName varchar(255) NOT NULL,"
			+ " GPA decimal(1,2) DEFAULT 0.00,"
			+ " CurrentStudent boolean DEFAULT true,"
			+ " Class varchar(255),"
			+ " PRIMARY KEY (BannerID)"
			+ ");";
		CreateTableQuery result = (CreateTableQuery)parser.parse(query);
		System.out.println(myDataBase.execute(query).getResults());
		
		
		System.out.println();
		System.out.println("Print out the table's columns and their values");
		System.out.println();
		Table myTable = myDataBase.getTable();
		ArrayList<ColumnDescription> tableColumns = myTable.getColumns();
		for(int i = 0; i < tableColumns.size(); i++){
			System.out.println(tableColumns.get(i).toString());
			System.out.println("Default value:   " + tableColumns.get(i).getDefaultValue());
			System.out.println("Is Unique:   " + tableColumns.get(i).isUnique());
			System.out.println("Is not null:   " + tableColumns.get(i).isNotNull());
			System.out.println("get has default:   " + tableColumns.get(i).getHasDefault());
			System.out.println("get type:   " + tableColumns.get(i).getColumnType().name());
			System.out.println();
		}
		System.out.println();
		System.out.println("The table's primery key column");
		System.out.println(myTable.getPrimaryKey().getColumnName());
		

	}
	
	private static <V> void insertTest(ArrayList<String> inserts) throws JSQLParserException
	{
		System.out.println();
		System.out.println("I will print out each SQL query and those with erros should have an error print right after it"
				+ "\n  and those rows should not apear when printing out all of the rows");
		System.out.println();
		for(int i = 0; i < inserts.size(); i ++){
			System.out.println();
			System.out.println(inserts.get(i));
			myDataBase.execute(inserts.get(i));
			
			
		}
		System.out.println();
		System.out.println();
		System.out.println("Print out all the values of the table");
		Table myTable = myDataBase.getTable();
		ArrayList<V> keys = myTable.getBTree().getAll();
		for(int n = 0; n < keys.size(); n++){
			System.out.println("		" + keys.get(n));
			System.out.println();
	    }   
		
		

	}
	/**
	 * @author Aaron Shakibpanah wrote these row insertions
	 * @return
	 */
	private static ArrayList<String> makeLi4()
	{

	ArrayList<String> qrys = new ArrayList<String>();

	//qrys.add("INSERT INTO YCStudent (LastName) VALUES ('Tim');");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA, currentstudent) VALUES ('Shimon', 'Cohen' , 'Senior', 800012345, 1678354, 2.00, false);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA, currentstudent) VALUES ('Shimon', 'Cohen' , 'Senior', 800012345, 1678354, 2.00, false);");
	
	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA, currentstudent) VALUES ('Yehuda', 'Gale' , 'Senior', 1, 22, 9.0, false);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA, currentstudent) VALUES ('Noah', 'Frankel' , 'Junior', 2, 3423798, 3.0, true);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Nir', 'Frankel' , 'Freshman', 3, 4322435, 6.0);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Avi', 'Greenman' , 'null', 4, 43243, 4.0);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Meir', 'Alneck' , 'null', 5, 345465, 4.0);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA, currentstudent) VALUES ('Yehuda', 'Brick' , 'Sophmore', 800188082, 1678354, 4.00, true);");
	
	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA, currentstudent) VALUES (4567, 'Cohen' , 'Senior', 800012345, 1678354, 2.00, false);");
	
	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Steve', 'Jobs' , 'Senior', 6, 45437545, 4.0);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, BannerID, SSNum, GPA) VALUES ('Micah', 'Shippel' , 7, 83723247, 4.0);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Yehuda', 'Bigowski' , 'Sophmore', 8, 432474383, 4.0);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA, currentstudent) VALUES ('Yehuda', 'Brick' , 'Sophmore', 800188082, 1678354, 4.00, true);");
	
	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA, currentstudent) VALUES ('Rick', 'Scott' , 'Senior', 9, 9335078, 2.0, true);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA, currentstudent) VALUES ('John', 'Appleseed' , 'Sophmore', 10, 9475743, 4.0, false);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Jamie', 'Benson' , 'null', 11, 8347947, 4.0);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Shalom', 'Mamon' , 'null', 12, 9957349, 4.0);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Meir', 'Shemesh' , 'null', 13, 904575, 4.0);");

	qrys.add("INSERT INTO YCStudent (FirstName) VALUES ('Tim');");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Aaron','Shakibpanah', 'Sophmore',14, 800454, 3.8);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Yitzie', 'Schienman' , 'Sophmore', 15, 43543, 4.0);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Yakov', 'Stern' , 'Senior', 16, 25343425, 2.6);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Barack', 'Obama' , 'Sophmore', 17, 545665422, 4.0);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Moladoris', 'Frankel' , 'Freshman', 26, 4322436, 6.2);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Donald', 'Trump' , 'Junior', 18, 2345234, 3.7);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('George', 'Bush' , 'Senior',19, 2543525, 3.0);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Yair', 'Lapid' , 'null', 20, 4334534, 2.8);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Matan', 'Nomdar' , 'Freshman', 21, 24352345, 4.0);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Samuel', 'Tafara' , 'null', 22, 565465, 2.9);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Jonathan', 'Singer' , 'Junior', 23, 354234255, 3.4);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Yudi', 'Meltzer' , 'Junior', 24, 7654723, 1.6);");

	qrys.add("INSERT INTO YCStudent (FirstName, LastName, Class, BannerID, SSNum, GPA) VALUES ('Yuudi', 'Meltzer' , 'Senior', 25, 7654724, 1.6);");
	
	return qrys;

	}	
	
	@Test
	public static void testCreateIndex() throws JSQLParserException
    {
		System.out.println();
		System.out.println("Create new index for the SSNum column");
		String query = "CREATE INDEX SSNum_Index on YCStudent (SSNum);";
		System.out.println(myDataBase.execute(query).getResults());
    }
	
    @Test
    public static <V> void testUpdate() throws JSQLParserException
    {
    	System.out.println();
    	System.out.println("Now I will Update the table ");
    	String query = "UPDATE YCStudent SET GPA=3.0,Class='Super Senior' WHERE BannerID>10 AND Class<>'Senior';";
    	System.out.println(query);
    	System.out.println(myDataBase.execute(query).getResults());
		query = "UPDATE YCStudent SET GPA=6.0,Class='Super Sophmore' WHERE BannerID<10;";
		System.out.println(query);
    	System.out.println(myDataBase.execute(query).getResults());
		
		query = "UPDATE YCStudent SET GPA=9.0, Class='Super Freshman' WHERE BannerID=10;";
		System.out.println(query);
    	System.out.println(myDataBase.execute(query).getResults());

		System.out.println();
		ArrayList<Row> table = myDataBase.getTable().getBTree().getAll();
		for(int i = 0; i < table.size(); i++){
			System.out.println("		" + table.get(i));
			System.out.println();
	    }   
    }
    
    @Test
    public static <V> void testDelete() throws JSQLParserException
    {
    	String query = "DELETE FROM YCStudent WHERE GPA>6.5;";
    	System.out.println(query);
    	System.out.println(myDataBase.execute(query).getResults());
    	ArrayList<Row> table = myDataBase.getTable().getBTree().getAll();
		for(int i = 0; i < table.size(); i++){
			System.out.println("		" + table.get(i));
			System.out.println();
	    }   
    	
		query = "DELETE FROM YCStudent WHERE Class='Senior' AND GPA<3.5;";
		System.out.println(query);
		System.out.println(myDataBase.execute(query).getResults());
    	table = myDataBase.getTable().getBTree().getAll();
		for(int i = 0; i < table.size(); i++){
			System.out.println("		" + table.get(i));
			System.out.println();
	    }   
		
    }  
    
    @Test
    public static void testSelect() throws JSQLParserException
    {
    	System.out.println();
    	System.out.println("I will now print out various select queries and then the results");
    	ArrayList<String> selects = fillSelect();
    	for(int n = 0; n < selects.size(); n++){
    		String query = selects.get(n);
    		System.out.println();
        	System.out.println(query);
        	
        	ResultSet selectResult = myDataBase.execute(query);
    		ArrayList<SelectRow> selectValues = selectResult.getResults();
    		for(int i = 0; i < selectValues.size(); i++){
    			SelectRow row = selectValues.get(i);
    			System.out.println(row);	
    		}
    		
    	}
    	
    	
		
		

    }
    
    private static ArrayList<String> fillSelect()
    {
    	ArrayList<String> selects = new ArrayList<>();
    	

    	selects.add("SELECT COUNT(BannerID), AVG(GPA) FROM YCStudent WHERE BannerID<4;");
    	selects.add("SELECT SUM(BannerID), AVG(GPA) FROM YCStudent WHERE BannerID<4;");
    	selects.add("SELECT SSNum, FirstName FROM YCStudent WHERE FirstName>'Bob';");
    	selects.add("SELECT BannerID, FirstName, GPA FROM YCStudent WHERE BannerID<=1 ORDER BY BannerID ASC, GPA DESC, FirstName ASC;");
    	selects.add("SELECT GPA, FirstName, BannerID FROM YCStudent WHERE BannerID>=10 ORDER BY GPA ASC, FirstName DESC, BannerID ASC;");
    	selects.add("SELECT BannerID, FirstName FROM YCStudent WHERE BannerID<13;");
    	//selects.add();
    	
    	
    	return selects;
    }
    
}

