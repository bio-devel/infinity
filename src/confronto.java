import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;



public class confronto {

	public confronto(Shell shell){
		FileDialog file_open = new FileDialog (shell, SWT.OPEN|SWT.MULTI);
	 	String [] filterNames = new String [] {"Txt Files", "All Files (*)"};
	 	String [] filterExtensions = new String [] {"*.xls;", "*.*"};
	 	String filterPath = "/";
	 	file_open.setFilterNames(filterNames);
	 	file_open.setFilterExtensions(filterExtensions);
	 	file_open.setFilterPath(filterPath);
	 	String selected=file_open.open();
	 	Vector<String> lista=new Vector<String>();
	 	 if (selected !=null) {
	 		String[] files=file_open.getFileNames();
	 		String path=selected.substring(0,selected.lastIndexOf("/")+1);
	 		file_open = new FileDialog (shell, SWT.OPEN|SWT.MULTI);
		 	file_open.setFilterNames(filterNames);
		 	file_open.setFilterExtensions(filterExtensions);
		 	file_open.setFilterPath(filterPath);
		 	String secondascelta=file_open.open();
		 	String nome="";
		 	String numero="";
		 	String nome2="";
		 	String numero2="";
		 	boolean uguali=false;
		 	if(secondascelta!=null){
		 		String[] files2=file_open.getFileNames();
		 		String path2=selected.substring(0,secondascelta.lastIndexOf("/")+1);
		 		for(int f=0;f<files.length;f++){
		 			selected=path+files[f];
		 			System.out.println("selected: "+selected);
		 			try {
		 				 InputStream inp = new FileInputStream(selected);
	        			 HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(inp));
	        			 Sheet sheet1 = wb.getSheetAt(0);
		 					for(int g=0;g<files2.length;g++){
		 						 secondascelta=path2+files2[g];
		 						 System.out.println("--"+secondascelta);
		 						 InputStream inp2 = new FileInputStream(secondascelta);
		 						 HSSFWorkbook wb2 = new HSSFWorkbook(new POIFSFileSystem(inp2));
		 						 Sheet sheet2 = wb2.getSheetAt(0);
		 						 int count=0;
		 						 for (Row row : sheet1) {
		 							for (Cell cell : row) {
	 									 if(cell.getCellType()== Cell.CELL_TYPE_STRING){
	 	        			            	 switch(cell.getColumnIndex()){
	 	        			            	 case 0:
	 	        			            		 nome=cell.getRichStringCellValue().getString().trim();
	 	        			            		 break;
	 	        			            	 case 3:
	 	        			            		 numero=cell.getRichStringCellValue().getString().trim();
	 	        			            		 break;
	 	        			            	 case 4:
	 	        			            		 //faccio il controllo tra le celle
	 	        			            		 count=0;
	 	        			            		 for(Row row2 : sheet2){
	 			 									for(Cell cell2 : row2){
	 			 										 switch(cell2.getColumnIndex()){
	 			 	        			            	 case 0:
	 			 	        			            		 nome2=cell2.getRichStringCellValue().getString().trim();
	 			 	        			            		 break;
	 			 	        			            	 case 3:
	 			 	        			            		 numero2=cell2.getRichStringCellValue().getString().trim();
	 			 	        			            		 if(!numero2.equals(numero) && nome.equals(nome2)){
	 			 	        			            			 lista.add(nome2);
	 			 	        			            			System.out.println(nome+" - "+numero+" =! "+nome2+" - "+numero2);
	 			 	        			            		 }
	 			 	        			            		 break;
	 			 	        			            	 }
	 			 									}
	 			 									count++;
	 				 							}
	 	        			            		 break;
	 	        			            	 }}
		 							
		 							 }
		 							
		 						 }
		 						 System.out.println("---count xls2: "+count);
		 					}
		 			}catch(Exception e){e.printStackTrace();}
				 }
		 	}
	 		
	}
	
	System.out.println("numero diversi: "+lista.size());
	for(int y=0;y<lista.size();y++){
		System.out.println(lista.elementAt(y));
	}
	
}
	}
