import static java.lang.Math.round;

import java.util.Vector;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;


public class pannello {

	public void visualizza(
			final TableItem item,
			final Object[][] info, //0:Consensus 1: Vector<Integer>(posizioni)
			final funzioni f
			){
		
		int id=Integer.parseInt(item.getData("id").toString());
		Object[] data={
				f.getMirnaAccession(id),
				f.getMirnaLocation(id),
				f.getMirnaType(id),
				f.getMirnaTSS(id),
				f.getMirnaHost(id),
				f.getMirnaLength(id),
				f.getMirnaCluster(id)
		};
		
		final Shell dialog = new Shell(new Shell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL|SWT.BORDER);
		dialog.setText(item.getText(0)+" information on region: "+item.getText(1));
		dialog.setImage(new Image(dialog.getDisplay(), System.getProperty("user.dir")+"/library/dna.jpg"));
		Composite shell=new Composite(dialog,SWT.NONE);
		shell.setBackground(new Color(shell.getDisplay(),0,69,139));
		
		Composite dx=new Composite(shell,SWT.NONE);
		String[] title={"Accession: ","miRNA location: Chr","Type: "
				,"TSS (distance from precursor): ","Host gene: ","miRNA length: ","Cluster: "};
		for(int i=0;i<7;i++){
			StyledText accession=new StyledText(dx,SWT.READ_ONLY);
			accession.setEditable(false);
			accession.setBackground(dialog.getBackground());
			if(data[i]==null){data[i]="-----------";}
			accession.setText(title[i]+data[i]);
			StyleRange styleRange1 = new StyleRange();
			styleRange1.start = 0;
			styleRange1.length = accession.getText().indexOf(":")+1;
			styleRange1.fontStyle = SWT.BOLD;
			accession.setStyleRange(styleRange1);
			accession.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1 , 1));
			if(i==6)accession.setWordWrap(true);
			}
		
		
		Composite sx=new Composite(shell,SWT.NONE);
		CTabFolder tab=new CTabFolder(sx,SWT.BORDER);
		tab.setSimple(false);
		tab.setSelectionForeground(new Color(tab.getDisplay(),255,255,255));
	    tab.setForeground(new Color(tab.getDisplay(),0,0,0));
	    tab.setSelectionBackground(new Color[] {
	    		new Color(tab.getDisplay(),0,139,69),
	    		new Color(tab.getDisplay(),204,231,217)},
	                new int[] { 45 });
	    for(int y=0;y<info.length;y++){
	    	if(info[y][1]!=null){
	    		CTabItem tabItem=new CTabItem(tab,SWT.NONE | SWT.FILL);
	    		tabItem.setText(info[y][0].toString()+" ("+((Vector<Integer>)info[y][1]).size()+")");
	    		Composite compo=new Composite(tab,SWT.NONE);
	    		compo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1 , 1));
	    		tabItem.setControl(compo);
	    		Table table_occ=new Table(compo,SWT.BORDER|SWT.V_SCROLL);
	    	    table_occ.setLinesVisible(true);
			    table_occ.setHeaderVisible(true);
			    table_occ.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1 , 1));
			    String[] alltitles = {"TSS relative position","Absolute position","String position"};
			    
			    TableColumnLayout layout_occ = new TableColumnLayout();
			    compo.setLayout( layout_occ );
			    for (int i = 0; i < alltitles.length; i++) {
			      final TableColumn column = new TableColumn(table_occ, SWT.NONE);
			      switch(i){
			      			case 0:   
			      				column.setAlignment(SWT.CENTER); 
			      				layout_occ.setColumnData( column, new ColumnWeightData( 34 ) );
			      				break;
			      			case 1:   
			      				column.setAlignment(SWT.CENTER);
			      				layout_occ.setColumnData( column, new ColumnWeightData( 39 ) );
			      				 break;
			      			case 2:   
			      				column.setAlignment(SWT.CENTER); 
			      				layout_occ.setColumnData( column, new ColumnWeightData( 25 ) );
			      				break;
			      }
			      column.setText(alltitles[i]);
			    	}
			    for(int i=0;i<((Vector<Integer>)info[y][1]).size();i++){
			    	TableItem itemTemp=new TableItem(table_occ,SWT.NONE);
			    	itemTemp.setText(2,""+((Vector<Integer>)info[y][1]).elementAt(i));
			    	if(item.getText(1).contains("+")){
			    		itemTemp.setText(1, ""+((Integer.parseInt(item.getText(1).split(" ")[1].toString()))+((Vector<Integer>)info[y][1]).elementAt(i)));
			    		if(f.getMirnaTSS(id)!=null) itemTemp.setText(0,""+(Integer.parseInt(itemTemp.getText(1))-(f.getMirnaTSS(id))));
			    		else itemTemp.setText(0,"----");
			    	}else{
			    		itemTemp.setText(1, ""+((Integer.parseInt(item.getText(1).split(" ")[3].toString()))-((Vector<Integer>)info[y][1]).elementAt(i)));
			    		if(f.getMirnaTSS(id)!=null) itemTemp.setText(0,""+(Integer.parseInt(item.getText(1).split(" ")[3].toString())-((Vector<Integer>)info[y][1]).elementAt(i)-(f.getMirnaTSS(id))));
			    		else itemTemp.setText(0,"----");
			    	}
			    	
			    }
	    	}
	    	tab.setSelection(0);
	    }
	    
		 Button visualize=new Button(sx,SWT.PUSH);   
		 visualize.setText("Show occurrence(s) on stream");
		 /*
		  * LISTENER
		  */
		visualize.addListener(SWT.Selection, new Listener(){
		        	 public void handleEvent(Event event) {
		        		// displaySequence d=new displaySequence();
		        		// d.getDisplay(item, info, f, null);
		        	 }});
		
		 /*
		  * LAYOUT
		  */
		shell.setLayout(new GridLayout(2,false));
		GridData griddata_box=new GridData();
		griddata_box.grabExcessHorizontalSpace=true;
		griddata_box.horizontalAlignment=SWT.FILL;
		griddata_box.verticalAlignment=SWT.CENTER;
		griddata_box.widthHint=480;
		griddata_box.heightHint=400;
		dx.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1 , 1));
		sx.setLayoutData(griddata_box);
		
		
		dx.setLayout(new GridLayout(1,true));
		
		sx.setLayout(new GridLayout(1,true));
		tab.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1 , 1));
		visualize.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1 , 1));
		
		
		shell.pack();
		dialog.open();
		dialog.pack();
		Display display=shell.getDisplay();
		while (!shell.isDisposed()) {
		if (!display.readAndDispatch()) display.sleep();
		}
	}
}
