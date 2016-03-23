import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;


public class displaySequence {

	public String temp;
	
	public void getDisplay(TableItem item, //item of selected table
							final Object[][] info, ////0:Consensus   1:Vector<Integer>(posizioni)
							funzioni f,
							String stream
							){
		final Shell dialog;
		if(item!=null){
			dialog=new Shell(item.getParent().getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL|SWT.BORDER);
			dialog.setText(item.getText(0)+" display occurrence on region: "+item.getText(1));
		}
		else {
			dialog=new Shell(new Shell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL|SWT.BORDER);
			dialog.setText("Display occurrence");
		}
		dialog.setImage(new Image(dialog.getDisplay(), System.getProperty("user.dir")+"/library/dna.jpg"));
		Composite shell=new Composite(dialog,SWT.NONE);
		shell.setBackground(new Color(shell.getDisplay(),0,69,139));
		Integer TSS=null;
		Integer mirna=null;
		int len=0;
		if(stream==null){
			//OCCHIO A STREAM
			stream="";//f.get_temporary_stream(Integer.parseInt(item.getData("id").toString()));
			mirna=Integer.parseInt(f.getLocation(Integer.parseInt(item.getData("id").toString()))[0].toString());
			len=Integer.parseInt(f.getLocation(Integer.parseInt(item.getData("id").toString()))[4].toString());
			//System.out.println(stream.length());
			if(f.getMirnaTSS(Integer.parseInt(item.getData("id").toString()))!=null){
				TSS=(f.getMirnaTSS(Integer.parseInt(item.getData("id").toString())));				
				//System.out.println(TSS+" "+(TSS-Integer.parseInt(item.getText(1).split(" ")[1].toString())));
				if(TSS>Integer.parseInt(item.getText(1).split(" ")[1].toString()) && TSS<Integer.parseInt(item.getText(1).split(" ")[3].toString())){
					TSS=TSS-Integer.parseInt(item.getText(1).split(" ")[1].toString());
				}else TSS=null;
			}
		}
		Composite sx=new Composite(shell,SWT.NONE);
		final StyledText text = new StyledText(sx, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		
		text.setText(stream);
		if(TSS!=null){
			StyleRange range = new StyleRange();
		    range.start = TSS;
		    range.length = 1;
		    range.foreground = shell.getDisplay().getSystemColor(SWT.COLOR_RED);
		    range.fontStyle = SWT.BOLD;
		    text.setStyleRange(range);
		}
		if(item!=null){
			
			if(mirna>=Integer.parseInt(item.getText(1).split(" ")[1].toString()) && mirna<=Integer.parseInt(item.getText(1).split(" ")[3].toString())){
				int start;
				if(item.getText(1).split(" ")[4].toString().equals("[+]")) start=mirna-Integer.parseInt(item.getText(1).split(" ")[1].toString());
				else start=Integer.parseInt(item.getText(1).split(" ")[3].toString())-mirna;
				StyleRange range = new StyleRange();
				range.start = start;
				if((stream.length()-start)>len) range.length = len;
				else range.length = (stream.length()-start);
				range.underline=true;
				range.fontStyle = SWT.BOLD;
				text.setStyleRange(range);
			}
		}else{
				for(int y=0;y<info.length;y++){
					if(info[y][1]!=null){
						for(int i=0;i<((Vector<Integer>)info[y][1]).size();i++){
		    				StyleRange styleRange1 = new StyleRange();
		    				styleRange1.start = ((Vector<Integer>)info[y][1]).elementAt(i);
		    				styleRange1.length = info[y][0].toString().length();
		    				styleRange1.fontStyle = SWT.BOLD;
		    				styleRange1.foreground=shell.getDisplay().getSystemColor(SWT.COLOR_CYAN);;
		    				text.setStyleRange(styleRange1);
		    		}
					}
				}
			}
		Label infoLbl=new Label(sx,SWT.WRAP);
		infoLbl.setText("Display sequence of selected nucleotide."+System.getProperty("line.separator")+"Red element represents the TSS, " +
				"underlined charatchers the pre-miRNA."+System.getProperty("line.separator"));
		Composite dx=new Composite(shell,SWT.NONE);
		
	    Label label = new Label(dx, SWT.CENTER);
	    PaletteData palette = new PaletteData(0xff, 0xff00, 0xff0000);
	    ImageData hueData = new ImageData(300, 300, 24, palette);
	    float hue = 0;
	    for (int x = 0; x < hueData.width; x++) {
	      for (int y = 0; y < hueData.height; y++) {
	        int pixel = palette.getPixel(new RGB(hue, 1f, 1f));
	        hueData.setPixel(x, y, pixel);
	      }
	      hue += 360f / hueData.width;
	    }
	    Image hueImage = new Image(dx.getDisplay(), hueData);
	    label.setImage(hueImage);
	    
	    if(info!=null){
	    	//System.out.println("display: "+info.length);
	    	for(int y=0;y<info.length;y++){
	    		if(info[y][1]!=null){
	    			for(int i=0;i<((Vector<Integer>)info[y][1]).size();i++){
	    				StyleRange styleRange1 = new StyleRange();
	    				styleRange1.start = ((Vector<Integer>)info[y][1]).elementAt(i);
	    				styleRange1.length = info[y][0].toString().length();
	    				styleRange1.fontStyle = SWT.BOLD;
	    				styleRange1.foreground=shell.getDisplay().getSystemColor(SWT.COLOR_CYAN);;
	    				text.setStyleRange(styleRange1);
	    		}
	    		
	    	}
	    }
	    	}
	    
	    Button printItem=new Button(dx,SWT.PUSH);
	    printItem.setText("Print sequence");
	    Button exportItem=new Button(dx,SWT.PUSH);
	    exportItem.setText("Export sequence");
	    
	    /*
	     * LISTENER
	     */
	    
	    shell.addListener(SWT.Close, new Listener() {
	        public void handleEvent(Event event) {
	        	dbAuth a=new dbAuth();
	        	funzioni f=new funzioni(a.getAuth());
	        	f.reset_temporary();
	        	a=null;
	        	f=null;
	        	System.gc();
	        	
	        }
	      });
	    
	    label.addMouseListener(new MouseListener() {
	        public void mouseDown(MouseEvent e) {
	        	RGB rgb = ((Label)e.widget).getImage().getImageData().palette.getRGB(((Label)e.widget).getImage().getImageData().getPixel(e.x, e.y));
	    	    if(info!=null){
	    	    	for(int y=0;y<info.length;y++){
	    	    		if(info[y][1]!=null){
	    	    			for(int i=0;i<((Vector<Integer>)info[y][1]).size();i++){
	    	    				StyleRange styleRange1 = new StyleRange();
	    	    				styleRange1.start = ((Vector<Integer>)info[y][1]).elementAt(i);
	    	    				styleRange1.length = info[y][0].toString().length();
	    	    				styleRange1.fontStyle = SWT.BOLD;
	    	    				styleRange1.foreground = new Color(((Label)e.widget).getDisplay(),rgb);
	    	    				text.setStyleRange(styleRange1);
	    	    		}
	    	    		
	    	    	}
	    	    }
	    	    	}
	          }

	          public void mouseUp(MouseEvent e) {
	            
	          }

	          public void mouseDoubleClick(MouseEvent e) {

	          }
	        });
	    
	    
	    printItem.addListener(SWT.Selection, new Listener(){
       	 public void handleEvent(Event e) {
       		 output o=new output();
       		 o.print(false, text);
       	 }});
	    
	    exportItem.addListener(SWT.Selection, new Listener(){
	       	 public void handleEvent(Event e) {
	       		 output o=new output();
	       		 o.print(true, text);
	       	 }});
	    /*
	     * LAYOUT
	     */
	    GridData gridData=new GridData();
	    gridData.grabExcessHorizontalSpace=true;
	    gridData.grabExcessVerticalSpace=true;
	    gridData.horizontalAlignment=SWT.FILL;
	    gridData.verticalAlignment=SWT.FILL;
	    gridData.widthHint=700;
	    gridData.heightHint=300;
	    
	    shell.setLayout(new GridLayout(2,false));
	    sx.setLayoutData(gridData);
	    dx.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1 , 1));
	    
	    sx.setLayout(new GridLayout(1,true));
	    text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1 , 1));
	    
	    dx.setLayout(new GridLayout(1,true));
	    label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1 , 1));
	    printItem.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1 , 1));
	    exportItem.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1 , 1));

	   
		shell.pack();
		dialog.open();
		dialog.pack();
		
		Display display=shell.getDisplay();
		while (!shell.isDisposed()) {
		if (!display.readAndDispatch()) display.sleep();
	}
		hueImage.dispose();
}
	
	
}//fine class