import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;


public class main {

	private Shell shell;
	public Composite opzioni,risultati,graph;
	public HashMap option;
	final dbAuth a=new dbAuth();
	final funzioni f=new funzioni(a.getAuth());
	public ConcurrentHashMap<Integer,Object> mirna;
	//getValore g=new getValore();
	results r=new results();
	
	 public main(Display display) {
		  
		 	
		 
	        shell = new Shell(display);
	        shell.setText("Display consensus matrix on nucletidic sequences v.1.0");
	       
	        //controllo inserimento db
	        File name = new File(System.getProperty("user.dir")+"/library/out.sys");
   		 	if (name.isFile()) {
   		 	try {
   			 BufferedReader input = new BufferedReader(new FileReader(name));
   			 String text;
   			 
   			 if((text = input.readLine()) == null || text.length() == 0){
   				dbOption o=new dbOption(null, true); //inizializza la prima connessione al database
   				 }else{
   					 //System.out.println(text);
   					 f.reset_temporary();
   				 }
   			 } catch (IOException ioException) {
   			 }
		  		}
	        
	        final Tray tray = display.getSystemTray();
	        Image image = new Image(display, System.getProperty("user.dir")+"/library/dna.jpg");
	        shell.setImage(image);
	       
	        final ToolTip tip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_INFORMATION);
	        if (tray != null) {
	            final TrayItem item = new TrayItem(tray, SWT.NONE);
	            item.setImage(image);
	            item.setToolTipText("SWT miRNA TSS tray");
	            item.addListener(SWT.Show, new Listener() {
	              public void handleEvent(Event event) {
	                System.out.println("show");
	              }
	            });
	            item.addListener(SWT.Hide, new Listener() {
	              public void handleEvent(Event event) {
	                System.out.println("hide");
	              }
	            });
	            item.addListener(SWT.Selection, new Listener() {
	              public void handleEvent(Event event) {
	                System.out.println("selection");
	              }
	            });
	            item.addListener(SWT.DefaultSelection, new Listener() {
	              public void handleEvent(Event event) {
	                System.out.println("default selection");
	                shell.setMaximized(true);
	                shell.setVisible(true);
	              }
	            });
	            

	            final Menu m = new Menu(shell, SWT.BAR);
	            final MenuItem file = new MenuItem(m, SWT.CASCADE);
	            file.setText("&Option");
	            final Menu optionmenu = new Menu(shell, SWT.DROP_DOWN);
	            file.setMenu(optionmenu);
	            final MenuItem dbmenu = new MenuItem(optionmenu, SWT.CASCADE);
	            dbmenu.setText("&Database");
	            final Menu submenu = new Menu(shell, SWT.DROP_DOWN);
	            dbmenu.setMenu(submenu);
	            	final MenuItem dbItem = new MenuItem(submenu, SWT.PUSH);
	            	dbItem.setText("&Access\tCTRL+A");
	            	dbItem.setAccelerator(SWT.CTRL + 'A');
	            	final MenuItem dbImport = new MenuItem(submenu, SWT.PUSH);
	            	dbImport.setText("&Update sequence\tCTRL+I");
	            	dbImport.setAccelerator(SWT.CTRL + 'I');
	            	final MenuItem mirnaUp = new MenuItem(submenu, SWT.PUSH);
	            	mirnaUp.setText("&Update miRNA position\tCTRL+M");
	            	mirnaUp.setAccelerator(SWT.CTRL + 'M');
	            final MenuItem printItem = new MenuItem(optionmenu, SWT.PUSH);
	            printItem.setText("&Print\tCTRL+P");
	            printItem.setAccelerator(SWT.CTRL + 'P');
	            final MenuItem exportItem = new MenuItem(optionmenu, SWT.PUSH);
	            exportItem.setText("&Export\tCTRL+X");
	            exportItem.setAccelerator(SWT.CTRL + 'X');
	            final MenuItem graphItem = new MenuItem(optionmenu, SWT.CASCADE);
	            graphItem.setText("&Graph");
	            final Menu graphmenu = new Menu(shell, SWT.DROP_DOWN);
	            graphItem.setMenu(graphmenu);
	            	final MenuItem graph1 = new MenuItem(graphmenu, SWT.PUSH);
	            	graph1.setText("&BarGraph\tCTRL+B");
	            	final MenuItem graph2 = new MenuItem(graphmenu, SWT.PUSH);
	            	graph2.setText("&PercentGraph\tCTRL+G");
	            final MenuItem separator = new MenuItem(optionmenu, SWT.SEPARATOR);
	            final MenuItem exitItem = new MenuItem(optionmenu, SWT.PUSH);
	            exitItem.setText("E&xit");
	            
	            printItem.setEnabled(false);
				exportItem.setEnabled(false);
	            
	            //pannelli
	            final Composite content=new Composite(shell,SWT.NONE);
	            final Button esegui=new Button(shell,SWT.PUSH);
	            esegui.setText("Run");
	            final StackLayout stack=new StackLayout();
		        opzioni=new Composite(content,SWT.NONE);
		        final option o=new option();
		        opzioni=o.option(opzioni);
		        //risultati=new Composite(content,SWT.NONE);
		        //results r=new results();
		        //risultati=r.results(risultati,null,f);
		        
		        stack.topControl=opzioni;
		        content.setLayout(stack);
		        content.layout();
	            
		        //LISTENER
		        esegui.addListener(SWT.Selection, new Listener(){
		        	 public void handleEvent(Event event) {
		        		 if(esegui.getText().equals("Run")){ 
		        			 f.reset_temporary();
		        			 if(mirna!=null) if(!mirna.isEmpty()) for(int t:mirna.keySet()) mirna.remove(t);
		        			 Object[] data=o.getData();
		        			 if(data==null) return;
		        			 esecuzione e=new esecuzione();
		        			 long esec=System.currentTimeMillis();
		        			 o.setMatchingCount(-1);
		        			 mirna=e.manager(data,f);
		        			 /*
		        			  * mirna<Int,Object> --> <Integer,Object[][]> 
		        			  * 								Object[][] -> 0:Consensus, 1:Vector<Integer>
		        			  */
		        			 //((Vector<Integer>)((HashMap<String,Vector<Integer>>)mirna.get(key)).get(temp))
		        			 esec=System.currentTimeMillis()-esec;
		        			 int hit_count = 0;
		        			 for(Integer i:mirna.keySet()){
		        				 for(String s: ((HashMap<String,Vector<Integer>>)mirna.get(i)).keySet()){
		        					 hit_count += ((Vector<Integer>)((HashMap<String,Vector<Integer>>)mirna.get(i)).get(s)).size();
		        				 }
		        			 }
		        			 System.out.println("Total hit_count: "+hit_count);
		        			 if(mirna!=null){
		        				  risultati=r.results(new Composite(content,SWT.NONE),mirna,f,data,esec);
		        				  //g.valore=r.getItem();
		        				  esegui.setText("Switch to option");
		        				  stack.topControl=risultati;
		        				  risultati.layout();
		        				  printItem.setEnabled(true);
		        				  exportItem.setEnabled(true);
		        			 }
		        			
		     		         
		        			 
		        		 }else{ 
		        			 printItem.setEnabled(false);
	        				 exportItem.setEnabled(false);
		        			 esegui.setText("Run");
		        			 stack.topControl=opzioni;
		        		 }
		        		 
		        		 content.layout(); 
		        	 }
		        });
		        
		        //LAYOUT
		        GridLayout grid = new GridLayout(1,false);
		        shell.setLayout(grid);
		        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		        esegui.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		        
	            shell.setMenuBar(m);
	            center(shell);
	            shell.setMaximized(true);
	            shell.open();

	        
	        /*
	         * LISTENER
	         */
	            
	     /* graphItem.addListener(SWT.Selection, new Listener(){
		        	 public void handleEvent(Event e) {
		        		 graph=new grafico().disegna(new Composite(content,SWT.BORDER), false);
		        		 stack.topControl=graph;
		 		         //content.setLayout(stack);
		 		         graph.layout();
		 		        content.layout();
		        	 }});  
		    */  
	     graph1.addListener(SWT.Selection, new Listener(){
	        	 public void handleEvent(Event e) {
	        		 graph=new grafico().disegna(new Composite(content,SWT.BORDER), false, 2);
	        		 stack.topControl=graph;
	 		         //content.setLayout(stack);
	 		         graph.layout();
	 		        content.layout();
	        	 }});    
	     graph2.addListener(SWT.Selection, new Listener(){
	        	 public void handleEvent(Event e) {
	        		 graph=new grafico().disegna(new Composite(content,SWT.BORDER), false, 1);
	        		 stack.topControl=graph;
	 		         //content.setLayout(stack);
	 		         graph.layout();
	 		        content.layout();
	        	 }});    
	            
	         
	     
	     
	       printItem.addListener(SWT.Selection, new Listener(){
		        	 public void handleEvent(Event e) {
		        		 output o=new output();
		        		 o.print(false, r.getItem());
		        		 //System.out.println(.length);
		        	 }});  
	       
	            
	       exportItem.addListener(SWT.Selection, new Listener(){
	        	 public void handleEvent(Event e) {
	        		 output o=new output();
	        		 o.print(true, r.getItem());
	        		 //System.out.println(.length);
	        	 }});    
	       
	        dbItem.addListener(SWT.Selection, new Listener(){
	        	 public void handleEvent(Event e) {
	        		 File name = new File(System.getProperty("user.dir")+"/library/out.sys");
	        		 if (name.isFile()) {
	        		 try {
	        			 BufferedReader input = new BufferedReader(new FileReader(name));
	        			 String text;
	        			 if((text = input.readLine()) == null){ 
	        				 dbOption o=new dbOption(null, false); }
	        			 else{
	        				 Object[] data=new Object[5];
	        				 data[0]=text;
	        				 int i=1;
	        				 while ((text = input.readLine()) != null){
	        					 	data[i]=text;
	        			 	 		i++;
	        		  			}
	        				 input.close();
	        				 dbOption o=new dbOption(data, false);
	        				 data=null;
	        				 System.gc();
	        			 }
	        		 }	 
	        			 catch (IOException ioException) {
	        		  		}
	        		 	}
	        		 
	        		 
	        	 }
	        });
	        
	        
	        mirnaUp.addListener(SWT.Selection, new Listener(){
	        	 public void handleEvent(Event e) {
	        		 FileDialog file_open = new FileDialog (shell, SWT.OPEN);
		             	String [] filterNames = new String [] {"Txt Files", "All Files (*)"};
		             	String [] filterExtensions = new String [] {"*.txt;", "*.*"};
		             	String filterPath = "/";
		             	String platform = SWT.getPlatform();
		             	if (platform.equals("win32") || platform.equals("wpf")) {
		             		filterNames = new String [] {"Txt Files", "All Files (*.*)"};
		             		filterExtensions = new String [] {"*.txt;", "*.*"};
		             		filterPath = "c:\\";
		             	}
		             	file_open.setFilterNames (filterNames);
		             	file_open.setFilterExtensions (filterExtensions);
		             	file_open.setFilterPath (filterPath);
		             	//file_open.setFileName ("myfile");
		             	String selected=file_open.open();
		             	System.out.println("selected: "+selected);
		             	 if (selected !=null) {
			        		 try {
			        			 FileInputStream fstream = new FileInputStream(selected);
			        	      	 DataInputStream in = new DataInputStream(fstream);
			        	      	 BufferedReader input = new BufferedReader(new InputStreamReader(in));
			        			 String temp;
			        			 while (((temp = input.readLine()) != null) ){
			        					 	f.updateMiRNAPosition(temp.toString().trim());
			        					 	
			        		  			} 
			        			 input.close();
			        				  
			        		 }	 
			        			 catch (IOException ioException) {
			        		  		}
			        		 	}
			        	 
	        	 }});
	        
	        dbImport.addListener(SWT.Selection, new Listener(){
	        	 public void handleEvent(Event e) {
	        		FileDialog file_open = new FileDialog (shell, SWT.OPEN);
	             	String [] filterNames = new String [] {"Txt Files", "All Files (*)"};
	             	String [] filterExtensions = new String [] {"*.txt;", "*.*"};
	             	String filterPath = "/";
	             	String platform = SWT.getPlatform();
	             	if (platform.equals("win32") || platform.equals("wpf")) {
	             		filterNames = new String [] {"Txt Files", "All Files (*.*)"};
	             		filterExtensions = new String [] {"*.txt;", "*.*"};
	             		filterPath = "c:\\";
	             	}
	             	file_open.setFilterNames (filterNames);
	             	file_open.setFilterExtensions (filterExtensions);
	             	file_open.setFilterPath (filterPath);
	             	//file_open.setFileName ("myfile");
	             	String selected=file_open.open();
	             	System.out.println("selected: "+selected);
	        		 if (selected !=null) {
	        		 try {
	        			 FileInputStream fstream = new FileInputStream(selected);
	        	      	 DataInputStream in = new DataInputStream(fstream);
	        	      	 BufferedReader input = new BufferedReader(new InputStreamReader(in));
	        			 String temp;
	        			 String sequence=""; //sequenza
	        			 String info=""; //mirna
	        			 while (((temp = input.readLine()) != null) ){
	        					 	if(temp.toString().contains(">hg19_wgRna")){
	        					 		if(sequence.length()!=0){
	        					 			f.updateDb(sequence, info);
	        					 			sequence="";
	        					 			info=temp.toString().trim();
	        					 		}else info=temp.toString().trim();
	        					 	}else{
	        					 		sequence+=temp.toString().trim();
	        					 	}
	        					 	
	        		  			} 
	        			 f.updateDb(sequence, info);
	        			 input.close();
	        				  
	        		 }	 
	        			 catch (IOException ioException) {
	        		  		}
	        		 	}
	        		 
	        		 
	        	 }
	        });
	        
	        while (!shell.isDisposed()) {
	          if (!display.readAndDispatch()) {
	            display.sleep();
	          }
	        }
	    }
	 }

	
	 public void center(Shell shell) {

	        Rectangle bds = shell.getDisplay().getBounds();
	        Point p = shell.getSize();
	        int nLeft = (bds.width - p.x) / 2;
	        int nTop = (bds.height - p.y) / 2;
	        shell.setBounds(nLeft, nTop, p.x, p.y);
	    }


	    public static void main(String[] args) {
	        Display display = new Display();
	        new main(display);
	        display.dispose();
	    }
	
}
