import net.sf.paperclips.DefaultGridLook;
import net.sf.paperclips.GridPrint;
import net.sf.paperclips.LinePrint;
import net.sf.paperclips.PageNumberPrint;
import net.sf.paperclips.PaperClips;
import net.sf.paperclips.PrintJob;
import net.sf.paperclips.TextPrint;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.printing.*;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextPrintOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHexColor;

public class output {

	static dbAuth a=new dbAuth();
	static Object[] auth=a.getAuth();
	static funzioni f=new funzioni(auth);
	
	
	public void print(Boolean toFile,Object toPrint){
		if(toFile) exporter(toPrint);
		else printer(toPrint);
	}
	
	
	
	public void exporter(Object toExport){
		if(toExport instanceof org.eclipse.swt.widgets.TableItem[]){
			try{
				System.out.println(((TableItem[])toExport)[0].getParent().getColumns()[0].getText());
				FileDialog dialog = new FileDialog(new Shell(), SWT.SAVE);
    		    dialog.setFilterNames(new String[] { "*.xls", "All Files (*.*)" });
    		    dialog.setFilterExtensions(new String[] { "*.xls", "*.*" }); 
    		    dialog.setFilterPath(System.getProperty("user.home") + "/Desktop"); // Windows path
	    		  String selected = dialog.open();
	    		  if(selected!=null){
	    			  
	    			  	FileOutputStream fileOut = new FileOutputStream(selected);
	    			  	HSSFWorkbook workbook = new HSSFWorkbook();
	    			  	HSSFSheet worksheet = workbook.createSheet("Worksheet");
				
	    			  	HSSFRow row = worksheet.createRow((short)0);
	    			  	//intestazione
				
	    			  	HSSFFont font = workbook.createFont();
	    			  	font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	    			  	font.setFontName("Courier New");
	    			  	font.setItalic(true);
				
	    			  	HSSFCellStyle style = workbook.createCellStyle();
	    			  	style.setFont(font);
	    			  	int mirnaID=-1;
	    			  	int tss=0;
	    			  	for(int i=0;i<((TableItem[])toExport)[0].getParent().getColumnCount()+2;i++){
	    			  		HSSFCell cell = row.createCell(i);
	    			  		cell.setCellStyle(style);
	    			  		
	    			  		switch(i){
	    			  			case 0: //mirna
	    			  				//mirnaID=f.getMirnaId_fromName(((TableItem[])toExport)[0].getParent().getColumns()[i].getText());
	    			  			case 1: 
	    			  			case 2:
	    			  			case 3:
	    			  			case 4:
	    			  			case 5: 
	    			  				cell.setCellValue(((TableItem[])toExport)[0].getParent().getColumns()[i].getText());
	    			  				break;
	    			  			case 6: //occur from TSS
	    			  				cell.setCellValue("Occurs from TSS");
	    			  				break;
	    			  			case 7:
	    			  				cell.setCellValue("Host Gene");
	    			  				break; 
	    			  		}
	    			  	}
	    			  	
					
				
				
	    			  	int rowNum=worksheet.getLastRowNum()+1;
	    			  	HSSFCellStyle styleVC = workbook.createCellStyle();
	    			  	styleVC = workbook.createCellStyle();
	    			  	styleVC.setWrapText(true);
	    			  	styleVC.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	    			  	HSSFCellStyle styleACVC = workbook.createCellStyle();
	    			  	styleACVC = workbook.createCellStyle();
	    			  	styleACVC.setWrapText(true);
	    			  	styleACVC.setAlignment(CellStyle.ALIGN_CENTER);
		  				styleACVC.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	    			  	for(int i=0;i<((TableItem[])toExport).length;i++){
	    			  		row = worksheet.createRow(rowNum);
	    			  		rowNum++;
	    			  		for(int j=0;j<((TableItem[])toExport)[0].getParent().getColumnCount()+2;j++){
	    			  			HSSFCell cell = row.createCell(j);
	    			  			switch(j){
	    			  			case 0:
	    			  			case 1:
	    			  			case 2:
	    			  			case 4:
	    			  			case 5:
//	    			  				style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//	    			  				style.setWrapText(true);
		    			  			cell.setCellValue((((TableItem[])toExport)[i]).getText(j));
		    			  			cell.setCellStyle(styleVC);
		    			  			break;
	    			  			case 3:
//	    			  				style.setAlignment(CellStyle.ALIGN_CENTER);
//	    			  				style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	    			  				style.setWrapText(true);
		    			  			cell.setCellValue((((TableItem[])toExport)[i]).getText(j));
		    			  			cell.setCellStyle(styleACVC);
		    			  			break;
	    			  			case 6:
//	    			  				style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//	    			  				style.setWrapText(true);
		    			  			if((((TableItem[])toExport)[i]).getData("occurTSS")!=null) cell.setCellValue((((TableItem[])toExport)[i]).getData("occurTSS").toString());
		    			  			else cell.setCellValue("");//System.out.println("->"+(((TableItem[])toExport)[i]).getData("occurTSS").toString());
		    			  			cell.setCellStyle(styleVC);
		    			  			break;
	    			  			case 7:
//	    			  				style.setAlignment(CellStyle.ALIGN_CENTER);
//	    			  				style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//	    			  				style.setWrapText(true);
	    			  				if((((TableItem[])toExport)[i]).getData("host")!=null){
	    			  					cell.setCellValue((((TableItem[])toExport)[i]).getData("host").toString());
	    			  				}else{
	    			  					cell.setCellValue("");
	    			  				}
		    			  			cell.setCellStyle(styleACVC);
		    			  			break;
	    			  				
	    			  			}
	    			  			
	    			  			//preparo il cellstyle
	    			  			/*style.setWrapText(true);
	    			  			cell.setCellValue((((TableItem[])toExport)[i]).getText(j));
	    			  			cell.setCellStyle(style);*/
					}
				}
				
				for(int i=0;i<worksheet.getRow(0).getLastCellNum();i++){
					worksheet.autoSizeColumn(i);
				}
				
				workbook.write(fileOut);
				fileOut.flush();
				fileOut.close();
			}
				
			}catch(Exception e){
				e.printStackTrace();
				 MessageDialog dialog=new MessageDialog(new Shell(),
										"Errore",
										null,
										"\n"+e.getMessage(),
										MessageDialog.ERROR,
										new String[] { "OK" },
										0);
				  dialog.open();
			}
			
		}else if(toExport instanceof org.eclipse.swt.custom.StyledText){
			try{
				FileDialog dialog = new FileDialog(new Shell(), SWT.SAVE);
    		    dialog.setFilterNames(new String[] { "*.doc","*.docx", "All Files (*.*)" });
    		    dialog.setFilterExtensions(new String[] { "*.doc",".docx", "*.*" }); 
    		    dialog.setFilterPath(System.getProperty("user.home") + "/Desktop"); // Windows path
	    		  String selected = dialog.open();
	    		  if(selected!=null){
	    			  		StyleRange[] tutti=((StyledText)toExport).getStyleRanges();
	    			  		//for(int y=0;y<tutti.length;y++) System.out.println(tutti[y]);
	    			  		XWPFDocument document = new XWPFDocument();
	    			  		XWPFParagraph paragrafoUno = document.createParagraph();
				        	paragrafoUno.setAlignment(ParagraphAlignment.LEFT);
				        	
				        	
				        	STHexColor chex;
				        	CTColor ctColor;
				        	if(tutti.length>0){
				        		int l=0;
				        		String hex;
				        		for(int y=0;y<tutti.length;y++){
				        			XWPFRun paragrafoUnoRunUno = paragrafoUno.createRun();
				        			paragrafoUnoRunUno.setText(((org.eclipse.swt.custom.StyledText) toExport).getText().substring(l, tutti[y].start));
						        	paragrafoUnoRunUno.setFontSize(12);
						        	paragrafoUnoRunUno.setColor("000000");
						        	//paragrafoUnoRunUno.addBreak();
						        	
				        			XWPFRun due = paragrafoUno.createRun();
				        			due.setText(((org.eclipse.swt.custom.StyledText) toExport).getText().substring(tutti[y].start, tutti[y].start+tutti[y].length));
				        			due.setFontSize(14);
						        	hex = String.format("%02x%02x%02x", tutti[y].foreground.getRed(), tutti[y].foreground.getGreen(), tutti[y].foreground.getBlue());
						        	due.setColor(hex);
						        	l=tutti[y].start+tutti[y].length;
				        		}
				        		XWPFRun paragrafoUnoRunUno = paragrafoUno.createRun();
				        		paragrafoUnoRunUno.setText(((org.eclipse.swt.custom.StyledText) toExport).getText().substring(l));
					        	paragrafoUnoRunUno.setFontSize(12);
					        	ctColor = CTColor.Factory.newInstance();
					        	chex = STHexColor.Factory.newValue(new byte[]{(byte)0x00, (byte)0x00, (byte)0x00});
					        	ctColor.setVal(chex);
					        	paragrafoUnoRunUno.getCTR().getRPr().setColor(ctColor);
				        	}else{
				        		XWPFRun paragrafoUnoRunUno = paragrafoUno.createRun();
				        		paragrafoUnoRunUno.setText(((org.eclipse.swt.custom.StyledText) toExport).getText());
					        	paragrafoUnoRunUno.setFontSize(12);
					        	ctColor = CTColor.Factory.newInstance();
					        	chex = STHexColor.Factory.newValue(new byte[]{(byte)0x00, (byte)0x00, (byte)0x00});
					        	ctColor.setVal(chex);
					        	paragrafoUnoRunUno.getCTR().getRPr().setColor(ctColor);
					        	paragrafoUnoRunUno.addBreak();
				        	}
				        	
				        	
				        	FileOutputStream outStream = null;
				        	try {
				        	outStream = new FileOutputStream(selected);
				        	document.write(outStream);
				        	outStream.close();
				        	} catch (FileNotFoundException e) {
				        	e.printStackTrace();
				        	} 
	    		  }
				 
			        	
			}catch(Exception e){
				e.printStackTrace();}
		}
	}
	
	
	
	public void printer(Object toPrint){
		//System.out.println("ok. Print "+((TableItem[])toPrint).length);
		if(toPrint instanceof org.eclipse.swt.widgets.TableItem[]){
			 if (toPrint == null)  return;
			 if(((TableItem[])toPrint).length==0){
				 System.out.println("item vuoto");
				 return;
			 }
			 PrintDialog dialog = new PrintDialog(new Shell(), SWT.NONE);
	    	 PrinterData data = dialog.open();
	    	 
			 if (!data.printToFile) {
				 data.fileName = "report.out";
    	    	 GridPrint grid=new GridPrint("d,d:g,d," +
							"d:g,d," +
							"d:g,d," +
							"d:g,d," +
							"d:g,d," +
							"d:g,d",new DefaultGridLook());

    	    	 grid.addHeader(new LinePrint(SWT.HORIZONTAL), GridPrint.REMAINDER);
    	    	 grid.addHeader(SWT.DEFAULT, SWT.FILL, new LinePrint(SWT.VERTICAL));
    	    	 grid.addHeader(SWT.LEFT,SWT.CENTER,new TextPrint("pre-miRNA")); //mirna
    	    	 grid.addHeader(SWT.DEFAULT, SWT.FILL, new LinePrint(SWT.VERTICAL));
    	    	 grid.addHeader(SWT.LEFT,SWT.CENTER,new TextPrint("Location"));//location
    	    	 grid.addHeader(SWT.DEFAULT, SWT.FILL, new LinePrint(SWT.VERTICAL));
    	    	 grid.addHeader(SWT.LEFT,SWT.CENTER,new TextPrint("TSS (distance)")); //tss
    	    	 grid.addHeader(SWT.DEFAULT, SWT.FILL, new LinePrint(SWT.VERTICAL));
    	    	 grid.addHeader(SWT.CENTER,SWT.CENTER,new TextPrint("Hits"));//occorrenze totali
    	    	 grid.addHeader(SWT.DEFAULT, SWT.FILL, new LinePrint(SWT.VERTICAL));
    	    	 grid.addHeader(SWT.CENTER,SWT.CENTER,new TextPrint("Consensus"));//consensus
    	    	 grid.addHeader(SWT.DEFAULT, SWT.FILL, new LinePrint(SWT.VERTICAL));
    	    	 grid.addHeader(SWT.LEFT,SWT.CENTER,new TextPrint("Occurrence (relative to TSS)"));//occorrenze TSS
    	    	 grid.addHeader(SWT.DEFAULT, SWT.FILL, new LinePrint(SWT.VERTICAL));
    	    	 
    	    	 grid.add(new LinePrint(SWT.HORIZONTAL), GridPrint.REMAINDER);
    	    	 
    	    	 TableItem[] selection=(TableItem[])toPrint;
    	    	 for(int i=0;i<selection.length;i++){
		    	    	for(int y=0;y<6;y++){
		    	    		switch(y){
		    	    		case 0:
		    	    			grid.add(SWT.DEFAULT, SWT.FILL, new LinePrint(SWT.VERTICAL));
		    	    			grid.add(SWT.LEFT, SWT.CENTER,
	    	    					//new TextPrint(selection[i].getText(y)));
		    	    					new TextPrint(selection[i].getText(y)));
		    	    			grid.add(SWT.DEFAULT, SWT.FILL, new LinePrint(SWT.VERTICAL));
		    	    			break;
		    	    		case 1:
		    	    		case 2:
		    	    			grid.add(SWT.LEFT, SWT.CENTER,
		    	    					new TextPrint(selection[i].getText(y)));
			    	    		grid.add(SWT.DEFAULT, SWT.FILL, new LinePrint(SWT.VERTICAL));
			    	    		break;
		    	    		case 3:
		    	    			grid.add(SWT.CENTER, SWT.CENTER,
		    	    					new TextPrint(selection[i].getText(y)));
			    	    		grid.add(SWT.DEFAULT, SWT.FILL, new LinePrint(SWT.VERTICAL));
			    	    		break;
		    	    		case 4:
		    	    		case 5:
		    	    			grid.add(SWT.LEFT, SWT.CENTER,
		    	    					new TextPrint(selection[i].getText(y)));
			    	    		grid.add(SWT.DEFAULT, SWT.FILL, new LinePrint(SWT.VERTICAL));
			    	    		break;
		    	    		}//fine switch
		    	    		
		    	    	}//fine for y
	    				grid.add(new LinePrint(SWT.HORIZONTAL), GridPrint.REMAINDER);
		    	    }//fine for i
		    	    grid.add(new LinePrint(SWT.HORIZONTAL), GridPrint.REMAINDER);	
		    	    //grid.add(new PageNumberPrint(pageNumber, SWT.RIGHT));
		    	    	 PrintJob job=new PrintJob("prova",grid);
		    	    	 PaperClips.print(job, data);
			 }
			 
		}else{
			if(toPrint instanceof org.eclipse.swt.custom.StyledText){
				//System.out.println(toPrint.toString());
				if(((StyledText)toPrint).getText().length()>0){
					 PrintDialog dialog = new PrintDialog(new Shell(), SWT.NONE);
			    	 PrinterData data = dialog.open();
			    	    if (data == null){
			    	    	 return;
			    	    }else{
			    	    	Printer printer = new Printer(data);
			    	    	StyledTextPrintOptions options = new StyledTextPrintOptions();
							options.printTextBackground = true;
						    options.printTextFontStyle = true;
						    options.printTextForeground = true;
					    	((StyledText)toPrint).print(printer, options).run();
			    	    }

				}
			}
		}
	}
}
