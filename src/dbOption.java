import java.io.BufferedWriter;
import java.io.FileWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class dbOption {

	public Combo nome;
	Object[] auth;
	
		dbOption(Object[] data, boolean init){

		final Shell dialog = new Shell(new Shell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL|SWT.BORDER);
		dialog.setText("Database Access");
		Image s = new Image(Display.getCurrent(),System.getProperty("user.dir")+"/library/user.png");
	    dialog.setImage(s);
	    Composite c=new Composite(dialog,SWT.NONE);
	    
	  
	    Label lnome=new Label(c,SWT.NONE);
		lnome.setText("DB name: ");
		nome=new Combo(c,SWT.BORDER);
		final Button b=new Button(c,SWT.CHECK);
		b.setText("Set as default"); 
		if(data!=null){b.setSelection(true);}
	    
		Label luser=new Label(c,SWT.NONE);
		luser.setText("User: ");
		final Text user=new Text(c,SWT.BORDER);
		if(data!=null){user.setText(data[1].toString());}
		Label lpass=new Label(c,SWT.NONE);
		lpass.setText("Password: ");
		final Text pass=new Text(c,SWT.BORDER);
		if(data!=null){pass.setText(data[2].toString());}
		final Button ch=new Button(c,SWT.CHECK);
		ch.setText("Hide pass"); 
		ch.setSelection(true);
		pass.setEchoChar('*');
		
		Label lconn=new Label(c,SWT.NONE);
		lconn.setText("Hostname: ");
		final Text conn=new Text(c,SWT.BORDER);
		conn.setText("localhost");
		if(data!=null){conn.setText((data[3].toString().split(":"))[0].toString());}
		Label lport=new Label(c,SWT.NONE);
		lport.setText("Port: ");
		final Text port=new Text(c,SWT.BORDER);
		port.setText("3306");
		if(data!=null){port.setText((data[3].toString().split(":"))[1].toString());}
		
		Label ldb=new Label(c,SWT.NONE);
		ldb.setText("Database: ");
		final Text dbname=new Text(c,SWT.BORDER);
		dbname.setText("tss-mirna");
		
		Label f=new Label(c,SWT.NONE);
		Button chiudi=new Button(c,SWT.PUSH);
		chiudi.setText("Close");
		Button salva=new Button(c,SWT.PUSH);
		salva.setText("Save");
	    
		/*
		 * LISTENER
		 */
		Listener saveOnfile=new Listener() {
            public void handleEvent(Event event) {
            	if(b.getSelection()){
            	 try{
            		  // Create file 
            		  FileWriter fstream = new FileWriter(System.getProperty("user.dir")+"/library/out.sys");
            		  BufferedWriter out = new BufferedWriter(fstream);
            		  out.write(dbname.getText()+System.getProperty("line.separator"));
            		  out.write(nome.getItem(nome.getSelectionIndex())+System.getProperty("line.separator"));
            		  out.write(user.getText()+System.getProperty("line.separator"));
            		  out.write(pass.getText()+System.getProperty("line.separator"));
            		  out.write(conn.getText()+":"+port.getText());
            		  //Close the output stream
            		  out.close();
            		  }catch (Exception e){//Catch exception if any
            		  System.err.println("Error: " + e.getMessage());
            		  }
            		   }
            	dialog.close();
            }};
        Listener save=new Listener(){
        	 public void handleEvent(Event event) {
        		 if(dbname.getText().length()>0 || user.getText().length()>0 || 
        				 pass.getText().length()>0 || conn.getText().length()>0 || port.getText().length()>0){
        			 Object[] temp={dbname.getText(),user.getText(),pass.getText(),conn.getText(),port.getText()};
        			 auth=temp;
        			 temp=null;
        			 try{
               		  // Create file 
               		  FileWriter fstream = new FileWriter(System.getProperty("user.dir")+"/library/out.sys");
               		  BufferedWriter out = new BufferedWriter(fstream);
               		  out.write(auth[0]+System.getProperty("line.separator"));
               		  out.write(auth[1]+System.getProperty("line.separator"));
               		  out.write(auth[2]+System.getProperty("line.separator"));
               		  out.write(auth[3]+":"+auth[4]);
               		  //Close the output stream
               		  out.close();
               		  }catch (Exception e){//Catch exception if any
               		  System.err.println("Error: " + e.getMessage());
               		  }
        			 System.gc();
        			 dialog.close();
        		 }
        }} ;   
		
		ch.addListener(SWT.Selection,new Listener() {
            public void handleEvent(Event event) {
            	if(ch.getSelection()){
            		pass.setEchoChar('*');
            	}else{
            		pass.setEchoChar((char)0);
            	}
            	
            }});
		
		
	    /*
	     * LAYOUT
	     */
		
		
		
		GridLayout grid=new GridLayout(1,true);
		dialog.setLayout(grid);
		
		GridLayout gridc=new GridLayout(4,true);
		c.setLayout(gridc);
		lnome.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1 , 1));
		nome.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2 , 1));
		b.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1 , 1));
		luser.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1 , 1));
		user.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 3 , 1));
		lpass.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1 , 1));
		pass.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2 , 1));
		ch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1 , 1));
		lconn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1 , 1));
		conn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1 , 1));
		lport.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1 , 1));
		port.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1 , 1));
		ldb.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1 , 1));
		dbname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1 , 1));
		f.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2 , 1));
		chiudi.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1 , 1));
		salva.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1 , 1));
		
		if(!init){
			funzioni fdb=new funzioni(data);
	    	nome=fdb.lista(nome);
	    	salva.addListener(SWT.Selection, saveOnfile);
	    	if(data[0]!=null && data[0].toString().length()>0){
	    		int find=-1;
	    		for(int i=0;i<nome.getItemCount();i++){
	    			if(nome.getItem(i).equals(data[0])){
	    				find=i;
	    				break;
	    			}
	    		}
	    		nome.select(find);
	    		}
			}else{
				lnome.dispose();
				nome.dispose();
				b.dispose();
				chiudi.dispose();
				salva.addListener(SWT.Selection, save);
			}
		
		
		dialog.open();
		dialog.pack();
		Display display=c.getDisplay();
		while (!c.isDisposed()) {
		if (!display.readAndDispatch()) display.sleep();
		}
		
	}
	
	
	public Object[] getauth(){
		return this.auth;
	}
	
}
