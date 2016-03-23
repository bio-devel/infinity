import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.awt.Font;

import org.csstudio.swt.widgets.datadefinition.ColorMap;
import org.csstudio.swt.widgets.datadefinition.ColorMap.PredefinedColorMap;
import org.csstudio.swt.widgets.figures.IntensityGraphFigure;
import org.csstudio.swt.widgets.figures.IntensityGraphFigure.IROIListener;
import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider;
import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.figures.ToolbarArmedXYGraph;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.Trace.PointStyle;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.swt.xygraph.linearscale.AbstractScale.LabelSide;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;




public class grafico {

	static dbAuth a=new dbAuth();
	static Object[] auth=a.getAuth();
	static funzioni f=new funzioni(auth);
	private static final int DataHeight = 939;
	private static final int resolution = 1;
	//private static final int DataWidth = 65000;
	private int PlotWidth;
	private int upstream;
	private int downstream;
	private int num_window = 1;
	private int upWindow = 0;
	private int downWindow = 0;
	private static final int minValue= 0;
	private static final String trace2_name = "miRNA";
	private Integer type = 2;
	private double media = 0;
	private HashMap<Integer,Integer> media_ponderata = new HashMap<Integer,Integer>();
	private static  DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	
	public Composite disegna(Composite content, Boolean esiste, int typeGraph){
		/*
		 * type=0 -> grafico intensità
		 */
		type = typeGraph;
		//Composite shell=new Composite(content,SWT.NONE);
		
		//GridLayout grid=new GridLayout(1,true);
		//content.setLayout(grid);
//		for(int y = 0; y<200;y++){
//			media_ponderata.put(y, 0);
//		}
		
		final Shell shell = new Shell(content.getDisplay());
		shell.setMaximized(true);
		
		//new confronto(shell);
		
		if(!esiste){
			FileDialog file_open = new FileDialog (shell, SWT.OPEN|SWT.MULTI);
         	String [] filterNames = new String [] {"Txt Files", "All Files (*)"};
         	String [] filterExtensions = new String [] {"*.xls;", "*.*"};
         	String filterPath = "/";
         	String platform = SWT.getPlatform();
         	if (platform.equals("win32") || platform.equals("wpf")) {
         		filterNames = new String [] {"Txt Files", "All Files (*.*)"};
         		filterExtensions = new String [] {"*.xls;", "*.*"};
         		filterPath = "C:\\Users\\VM-Luca\\workspace\\miRNA\\Risultati_miRNA";
         	}
         	file_open.setFilterNames (filterNames);
         	file_open.setFilterExtensions (filterExtensions);
         	file_open.setFilterPath (filterPath);
         	String selected=file_open.open();
         
         	 if (selected !=null) {
         		System.out.println("selected: "+selected);
         		String path=selected.substring(0,selected.lastIndexOf(System.getProperty("file.separator"))+1);
         		//System.out.println("path: "+path);
         		short[] simuData = new short[((int)(PlotWidth/resolution)) * DataHeight*2];
	 			   // for(int y=0;y<simuData.length;y++) simuData[y]=0;
 			    double[] plotData= new double[PlotWidth/resolution];
 			    int p;
					//  for(int y=0;y<plotData.length;y++) plotData[y]=0;; 
 			    HashMap<String,Object> dati=new HashMap<String,Object>();
       		 	HashMap<Object,Object> hit=new HashMap<Object,Object>();
         		String[] files=file_open.getFileNames();
         		int max=0;
         		int nm=-1;
         		String mirna="";
			    String temp="";
			    int c=0;
			    int count=0;
			    int temp_media = 0;
			    int key = 0;
			    int totaleHit = 0;
			    double jvalue = 0;
         		for(int f=0;f<files.length;f++){
         		 selected=path+files[f];
         		 //System.out.println("selected: "+selected);
        		 try {
        			 InputStream inp = new FileInputStream(selected);
        			    HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(inp));
        			    Sheet sheet1 = wb.getSheetAt(0);
        			    for (Row row : sheet1) {
        			    	 if(row.getRowNum()!=0){
        			    		 count++;
        			    	 }
        			    	for (Cell cell : row) {
        			    		
        			    		 //CellReference cellRef = new CellReference(row.getRowNum(), cell.getColumnIndex());
        			             //System.out.println(row.getRowNum()+" "+cell.getColumnIndex());
        			           
        			             if(cell.getCellType()== Cell.CELL_TYPE_STRING){
        			            	 //System.out.println(cell.getRichStringCellValue().getString().trim());
        			            	 switch(cell.getColumnIndex()){
        			            		 case 0:
        			            			 mirna=cell.getRichStringCellValue().getString().trim();
        			            			 //System.out.println(mirna);
        			            			 nm++;
        			            			 break;
        			            		 case 3:
        			            			 if(cell.getRichStringCellValue().getString().trim().length()>0){
        			            				 if(row.getRowNum()!=0){
        			            					 //Score = key
        			            					 //Frequency = Value
        			            					 jvalue = Double.parseDouble(cell.getRichStringCellValue().getString().trim());
        			            					 key = Integer.parseInt(cell.getRichStringCellValue().getString().trim());
        			            					 totaleHit += key;
        			            					 dataset.addValue(jvalue, "", mirna);
        			            					 if(media_ponderata.get(key) == null){
        			            						 media_ponderata.put(key, 1);
        			            					 }
        			            					 else{
        			            						 temp_media = media_ponderata.get(key) + 1; 
            			            					 media_ponderata.put(key, temp_media);
            			            					 //System.out.println(key+" "+temp_media); 
        			            					 }
        			            					
        			            				 }
        			            			 }
        			            			 break;
        			            		 case 5:
        			            			 temp=cell.getRichStringCellValue().getString();
        			            			 if(row.getRowNum()==0){
        		  	            				  if(temp.contains("(")){
        		  	            					  temp=temp.substring(temp.indexOf("(")+1,temp.indexOf(")"));
        		  	            					  upstream=Integer.parseInt(temp.substring(1,temp.indexOf(";")).toString());
        		  	            					  downstream=Integer.parseInt(temp.substring(temp.indexOf("+")+1).toString());
        		  	            					  PlotWidth=upstream+downstream;
        		  	            					  switch(type){
        		  	            					  case 1:
        		  	            						  /*
        		  	            						  if(upWindow!=0){
        		  	            							simuData = new short[upWindow * downWindow*2];
        		  	            						  }else{
        		  	            							  simuData = new short[((int)(PlotWidth/resolution)) * DataHeight*2];
        		  	            						  }
        		  	            		 			    for(int y=0;y<simuData.length;y++) simuData[y]=0;
        		  	            						  break;
        		  	            						  */
        		  	            					  case 2:
        		  	            						  if(upWindow!=0){
        		  	            							plotData= new double[(upWindow+downWindow)/resolution];
        		  	            						  }else{
        		  	            							  upWindow=upstream;
        		  	            							  downWindow=downstream;
        		  	            							  plotData= new double[PlotWidth/resolution];
        		  	            						  }
        		  	            						
          		  	            					  for(int y=0;y<plotData.length;y++) plotData[y]=(double)0;
        		  	            						  break;
        		  	            					  }
        		  	            					 
        		  	            				  }
        		  	            				   continue;
        		     			            	 }
        			            			 //temp=cell.getRichStringCellValue().getString();
        			            			 //System.out.println(cell.getRichStringCellValue().getString());
        			            			 if(temp.length()==0) continue;
        			            			 String[] t=temp.split(System.getProperty("line.separator"));
        			            			 String[] t1;
        			            			 c=t.length;
        			            			 int v=0;
        			            			 for(int y=0;y<c;y++){
        			            				 t1=t[y].split(" ");
        			            				 for(int x=0;x<t1.length;x++){
        			            					 if(t1[x].toString().startsWith("<")){
        			            						 v=upstream+Integer.parseInt(t1[x].toString().trim().substring(1,(t1[x].toString().indexOf(">"))));
        			            					 }else{
        			            						 v = Integer.parseInt(t1[x].toString().trim()) + upstream;
        			            						 //System.out.println(t1[x].toString().trim()+" -> "+v);
        			            					 }
//        			            					 if(t1[x].toString().startsWith("-")){
//    			            							 v=upstream-Integer.parseInt(t1[x].toString().trim().substring(1));
//    			            						 }else if(t1[x].toString().startsWith("<")){
//    			            							 v=upstream+Integer.parseInt(t1[x].toString().trim().substring(1,(t1[x].toString().indexOf(">"))));
//    			            						 }else{
//    			            							 v=upstream+Integer.parseInt(t1[x].toString().trim());
//    			            						 }
        			            					 //max=(nm*DataWidth)+(v/10);//*100/65000;
        			            					//System.out.println(nm+" + "+v/10+" -> "+max);
        			            					 /*
        			            					 if(type==1){
    			            							 if(DataHeight==1){
    			            								 simuData[(v/resolution)]++;
    			            								 max=Math.max(simuData[(v/resolution)],max);
    			            							 }else{
    			            								 simuData[(nm*PlotWidth/resolution)+(v/resolution)]++;
    			            								 max=Math.max(simuData[(v/resolution)],max);
    			            							 }
        			            						 
        			            					 }
        			            					 */
        			            					 if(type==2 || type == 1){
        			            						 if(upWindow!=0){
        			            							 if(v>(upstream-upWindow) && v<PlotWidth-(downstream-downWindow)){
        			            								 p=v-(upstream-upWindow);
        			            								 plotData[p/resolution]++;
        			            								 max=Math.max((int)plotData[p/resolution],max);
        			            							 }
        			            						 }else{
        			            							 plotData[v/resolution]++;
        			            							 max=Math.max((int)plotData[v/resolution],max);
        			            						 }
        			            						 
        			            						 
        			            						 //System.out.println("plotData["+v+"]="+(int)plotData[v]);
        			            						 
        			            					 }
        			            					 
        			            				 }
        			            			 }
        			            			 //dati.put(mirna,hit);
        			            			 break;
        			            	 }
        			            	 
        			            	 
        			             }
        			    	}
        			    	
        			    }
        			    inp.close();
        		 }	 
        			 catch (IOException ioException) {
        				 ioException.printStackTrace();
        		  		} 
        		 
         	 }//fine file[]
         	 
         	 
        	
        		 final LightweightSystem lws = new LightweightSystem(shell);
        		 System.out.println("mirna = "+nm);
        		 System.out.println("max hit = "+max+" in:"+count);
        		 System.out.println(PlotWidth+" -> "+upstream+" + "+downstream+" res."+resolution);
        		 
        		  MessageDialog dialog = new MessageDialog(
							new Shell(), 
							"Hit(s) Count", 
							null, 
							"miRNA found: " + nm + System.getProperty("line.separator") +
							"Max hit(s) found: " + max + System.getProperty("line.separator") +
							"Total hits found: " + totaleHit + System.getProperty("line.separator")+
							"PlotWidth: " +upstream+" upstream  and "+downstream+" downstream (res."+resolution+")", 
							MessageDialog.INFORMATION, 
							new String[]{"OK"}, 
							0);	
        		 dialog.open();
        		 XYGraph xyGraph = new XYGraph();
        		 
        		 
        		 System.out.println("Totale Hit delle ccaat: "+totaleHit);
      			//
      			double[] ydata=new double[PlotWidth/resolution];
      			double[] xdata=new double[PlotWidth/resolution];
      			//t=0;
      			double[] yMdata=new double[PlotWidth/resolution]; //grafico per lo start 
      			double[] xMdata=new double[PlotWidth/resolution];
      			
      			for(int y=0;y<(plotData).length;y++){
      				if(plotData[y]>=minValue){
      					xdata[y]=y;
      					ydata[y]=plotData[y];
      					xMdata[y]=y;
      					yMdata[y]=-1;
      					
      				}
      			}
      			/*
      			float val;
      			double hit_percent = 0;
      			int t = 0;
      			int block = 1;
      			for(int y=0;y<(plotData).length;y++){
      				if(t == 1000){
      					val = (float)((hit_percent * 100) / totaleHit);
      					System.out.println("Blocco " + block +" "+ String.format("%.0f%%",val));
      					block++;
      					hit_percent = 0;
      					t=0;
      				}else{
      					t++;
      					hit_percent += plotData[y];
      				}
      			}
      			*/	
      			
      			
      			
      			
        		 
        		 switch(type){
        		 case 1: //percentuale delle hit
        			 int q = Math.round((PlotWidth / num_window));
        			 System.out.println("larghezza bps = "+q+" "+plotData.length);
        			 double[] xpercent = new double[num_window];
        			 double[] ypercent = new double[num_window];
        			 double[] Sxpercent = new double[num_window];
        			 double[] Sypercent = new double[num_window];
        			 int count_q = 0;
        			 int hit_count = 0;
        			 for(int y=1;y<PlotWidth;y++){
        				 hit_count += plotData[y];
           			 	if( y%q == 0 || y==(PlotWidth-1) ){
           			 		Sypercent[count_q] = 0;
           					xpercent[count_q] = count_q;
           					ypercent[count_q] = ((double)((double)hit_count/(double)totaleHit))*100.0f;
           				    System.out.println("valore "+ypercent[count_q] + " zona "+count_q );
           				    hit_count = 0;
           					count_q++;
           				}
           			}
        			 System.arraycopy(xpercent, 0, Sxpercent, 0, num_window);
        			 
        			 
        			 lws.setContents(xyGraph);
        			 xyGraph.primaryXAxis.setShowMajorGrid(false);
        			 xyGraph.primaryXAxis.setTickLableSide(LabelSide.Primary);
        			 xyGraph.primaryXAxis.setRange(0, num_window);
        			 xyGraph.primaryYAxis.setShowMajorGrid(true);
        			 xyGraph.primaryYAxis.setRange(0, 100);
        			 xyGraph.primaryXAxis.setTitle("Window_Range "+q+"bps");
        			 xyGraph.primaryYAxis.setTitle("Hits %");
        			 
        			 CircularBufferDataProvider traceDataProviderhitsperc = new CircularBufferDataProvider(true);
        			 traceDataProviderhitsperc.setBufferSize(q);		
        			 traceDataProviderhitsperc.setCurrentXDataArray(xpercent);
        			 traceDataProviderhitsperc.setCurrentYDataArray(ypercent);
        			 Trace tracePerCent = new Trace("Hits PerCents", 
      						xyGraph.primaryXAxis, xyGraph.primaryYAxis, traceDataProviderhitsperc);		
      				//set trace property
      				 tracePerCent.setLineWidth(10);
        			 tracePerCent.setAntiAliasing(true);
        			 tracePerCent.setTraceType(TraceType.BAR);
        			 tracePerCent.setTraceColor(XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_BLACK) );
      				//trace2.setPointStyle(PointStyle.TRIANGLE);
      				 xyGraph.addTrace(tracePerCent);
      				 
      				 
      				 
      				 CircularBufferDataProvider traceDataProviderstart = new CircularBufferDataProvider(true);
      				traceDataProviderstart.setBufferSize((upWindow+downWindow)/resolution);
      				traceDataProviderstart.setCurrentXDataArray(xdata);
      				traceDataProviderstart.setCurrentYDataArray(ydata);	
        			 Trace tracestart = new Trace("Trace Plot", 
        						xyGraph.primaryXAxis, xyGraph.primaryYAxis, traceDataProviderstart);			
        			 //xyGraph.primaryXAxis.
        				//set trace property
        			//set trace property
        			 //trace.setAntiAliasing(true);
        			 tracestart.setTraceColor(XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_BLACK) );
        			 tracestart.setTraceType(TraceType.BAR);
        			 tracestart.setPointStyle(PointStyle.NONE);
        				//add the trace to xyGraph
        				//xyGraph.addTrace(trace);	
        				
        				//add the trace to xyGraph
        			 xyGraph.addTrace(tracestart);	
      				 
      				 
        			 break;
        			 
        		 case 2: //plot
        			
        			// ToolbarArmedXYGraph toolbarArmedXYGraph = new ToolbarArmedXYGraph(xyGraph);
        			 //lws.setContents(toolbarArmedXYGraph);
        			
        			//System.out.println("Totale Hit delle ccaat: "+totaleHit);
        			//
        		
        			
        			 yMdata[upWindow/resolution]=max+(0.5);
        			
        			
        			 lws.setContents(xyGraph);
        			 xyGraph.primaryXAxis.setShowMajorGrid(false);
        			 xyGraph.primaryXAxis.setTickLableSide(LabelSide.Primary);
        			 xyGraph.primaryXAxis.setRange(0,(upWindow+downWindow)/resolution);
        			 xyGraph.primaryYAxis.setShowMajorGrid(false);
        			 xyGraph.primaryYAxis.setRange(minValue, max+1);
        			 xyGraph.primaryXAxis.setTitle("Position ("+trace2_name+": "+(upWindow/resolution)+" -  Hits minValue: "+minValue+")");
        			 xyGraph.primaryYAxis.setTitle("Score");
        			 
        			 
        			//create a trace data provider, which will provide the data to the trace.
     				CircularBufferDataProvider traceDataProvider2 = new CircularBufferDataProvider(true);
     				traceDataProvider2.setBufferSize((upWindow+downWindow)/resolution);		
     				traceDataProvider2.setCurrentXDataArray(xMdata);
     				traceDataProvider2.setCurrentYDataArray(yMdata);
     				//create the trace
     				Trace trace2 = new Trace(trace2_name, 
     						xyGraph.primaryXAxis, xyGraph.primaryYAxis, traceDataProvider2);		
     				//set trace property
     				//trace2.setPointSize(10);
     				trace2.setAntiAliasing(true);
     				trace2.setTraceType(TraceType.BAR);
     				trace2.setTraceColor(XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_RED) );
     				trace2.setPointStyle(PointStyle.TRIANGLE);
     				xyGraph.addTrace(trace2);	//START MIRNA-TSS
        			 
        			 CircularBufferDataProvider traceDataProvider = new CircularBufferDataProvider(true);
        			 traceDataProvider.setBufferSize((upWindow+downWindow)/resolution);
        			 traceDataProvider.setCurrentXDataArray(xdata);
        			 traceDataProvider.setCurrentYDataArray(ydata);	
        			 Trace trace = new Trace("Trace Plot", 
        						xyGraph.primaryXAxis, xyGraph.primaryYAxis, traceDataProvider);			
        			 //xyGraph.primaryXAxis.
        				//set trace property
        			//set trace property
        			 //trace.setAntiAliasing(true);
        			 trace.setTraceColor(XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_BLUE) );
        			 trace.setTraceType(TraceType.BAR);
        			 trace.setPointStyle(PointStyle.NONE);
        				//add the trace to xyGraph
        				//xyGraph.addTrace(trace);	
        				
        				//add the trace to xyGraph
        			 xyGraph.addTrace(trace);	
        			 
        			 
        			 
        			CircularBufferDataProvider traceDataProvider4 = new CircularBufferDataProvider(true);
        			traceDataProvider4.setBufferSize((upWindow+downWindow)/resolution);
        			 traceDataProvider4.setCurrentXDataArray(xdata);
        			 traceDataProvider4.setCurrentYDataArray(ydata);	
     				
     				//create the trace
     				Trace trace4 = new Trace("Trace1-XY Plot", 
     						xyGraph.primaryXAxis, xyGraph.primaryYAxis, traceDataProvider4);			
     				
     				//set trace property
     				trace4.setAreaAlpha(150);
     				trace4.setTraceType(TraceType.AREA);
     				trace4.setTraceColor(XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_BLUE) );
     				
     				//add the trace to xyGraph
     				//xyGraph.addTrace(trace4);	
        			 
        		
        			 break;
        		 case 3: 
        			 media = 0;
        			 int freq = 0;
        			 double varianza = 0;
        			 for(Integer x: media_ponderata.keySet()){
        				 System.out.println(x+";"+media_ponderata.get(x));
        				 freq += media_ponderata.get(x); 
    					 media += x*media_ponderata.get(x);
        			 }
        			 System.out.print("media="+media+"/frequenza="+freq+"("+media/freq+") = ");
        			 media = media/freq;
        			 System.out.println(media);
        			 for(Integer x: media_ponderata.keySet()){
    					 varianza += x*(Math.pow((media_ponderata.get(x) - media), 2));
        			 }
        			 System.out.print("varianza="+varianza+"/frequenza="+freq+"("+varianza/freq+") = ");
        			 varianza = Math.sqrt( varianza/freq ) ;
        			 System.out.println(varianza);
        			 //media = media/nm;
 			    	 
        			 DefaultCategoryDataset dataset2 = new DefaultCategoryDataset();
        			//System.out.println(dataset.getRowKeys().size()+" + "+dataset.getColumnKeys().size());
        			 List<Comparable> columns = dataset.getColumnKeys();
        			 List<Comparable> rows = dataset.getRowKeys();
        			 for(int i = 0; i<columns.size();i++){
        				 for(int j=0;j<rows.size();j++){
        					 Comparable column = columns.get(i);
        					 Comparable row = rows.get(j);
        					 Number value = dataset.getValue(row, column);
        					 if( ((Double)value).intValue() < 20 && ((Double)value).intValue() > 8){
        						 //System.out.println(i+"-"+j+"-"+column+"-"+row+" = "+value);
        						 System.out.println(column + ";" + value);
        						 dataset2.addValue( value, "", column);
        					 }
        					 
        					 
        				 }
        			 }
        			 
//        			 for(int x = 0;x<dataset.getColumnKeys().size();x++){
//        				 System.out.println( dataset.getValue(x, 0).intValue()+" "+ dataset.getValue(x, 2).toString()  );
//        				 if( ((Double)dataset.getValue(x, 0)).intValue() < 100 && ((Double)dataset.getValue(x, 0)).intValue() > 50){
//        					 System.out.println( dataset.getValue(x, 0).intValue()+" "+ dataset.getValue(x, 2).toString()  );
//        					 dataset2.addValue( dataset.getValue(x, 0).intValue(), "", dataset.getValue(x, 2).toString());
//        				 } 
//        			 }
        			 
        			  // create the chart...
        		        final JFreeChart chart = ChartFactory.createBarChart(
        		        	"",         // chart title
        		            "miRNA",               // domain axis label
        		            "Score",                  // range axis label
        		            dataset2,                  // data
        		            PlotOrientation.HORIZONTAL, // orientation
        		            false,                     // include legend
        		            false,                     // tooltips?
        		            false                     // URLs?
        		        );
        		        
        		        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        				plot.setDomainGridlinesVisible(false);
//        				plot.getDomainAxis().setLabelFont( new Font( "Sans", SWT.NORMAL, 8 ) );
//        				plot.getRangeAxis().setLabelFont( new Font( "Sans", SWT.NORMAL, 6 ) );
        				plot.getDomainAxis().setTickLabelFont( new Font( "Sans", SWT.NORMAL, 7 ) );
        				plot.getRangeAxis().setTickLabelFont( new Font( "Sans", SWT.NORMAL, 9 ) );
        				
        				final ChartComposite frame = new ChartComposite(content, SWT.NONE,
        						chart, true);
        				 frame.setDisplayToolTips(false);
        			        frame.setHorizontalAxisTrace(false);
        			        frame.setVerticalAxisTrace(false);
        			        frame.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1 , 1));

        			 break;
        			 
        			 
        		 case 4:
        			 
        			 break;
        	
        			 
        		 }
        		 
        		 
        		 
        		
        		 
        		
        		 //intensityGraph.setDataArray(simuData);
        		 
        		 shell.open();
        		 //shell.(content);
        		//final short[] data = new short[DataWidth * DataHeight];
        		 
        		 
        		
        		 	}//select !=null
		}//if !esiste
		
		//content.getShell().open();
		GridLayout layout=new GridLayout(1,true);
		content.setLayout(layout);
		shell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1 , 1));
		
		shell.setLayout(layout);
		
		return content;
	}
	
	
}
