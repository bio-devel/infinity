import java.util.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;


public class sortTable{

	
	Object[][] mergeSort(Object[][] data,int index, int dir){
		
		Object[][] ordinato=new Object[data.length][data[0].length];
		
		if(data.length==0){
			return ordinato;
		}
		if(data.length==1){
			ordinato=data.clone();
			return ordinato;
		}
		
		Object[][] primo=new Object[data.length/2][data[0].length];
		Object[][] secondo=new Object[(data.length+1)/2][data[0].length];
		
		System.arraycopy(data, 0, primo, 0, data.length/2);
		System.arraycopy(data, data.length/2, secondo, 0, (data.length+1)/2);
		
		 Object[][] primo_ord=mergeSort(primo,index,dir);
		 Object[][] secondo_ord=mergeSort(secondo,index,dir);
		
		return merge(primo_ord,secondo_ord,index,dir);
		}
	
	
	public Object[][] merge(Object[][] primo,Object[][] secondo,int index,int dir){
		Object[][] nuovo=new Object[primo.length+secondo.length][primo[0].length];
		//System.out.println("colum: "+primo[0].getParent().getColumnCount());
		int i,j,z,uno,due;
		i=0;
		j=0;
		for(z=0;z<(primo.length+secondo.length);z++){
			if(j>=secondo.length){
				 System.arraycopy(primo, i, nuovo, z, 1);
				//nuovo[z]=primo[i];
				i++;
			}else if(i>=primo.length){
				System.arraycopy(secondo, j, nuovo, z, 1);
				//nuovo[z]=secondo[j];
				j++;
			}else {
				//if(primo[i][index].toString().length()==0) return;
				uno=Integer.parseInt(primo[i][index].toString());
				due=Integer.parseInt(secondo[j][index].toString());
				if(dir==SWT.UP){
					if(uno>due){
						System.arraycopy(primo, i, nuovo, z, 1);
						//nuovo[z]=primo[i];	
						i++;
					}else{
						System.arraycopy(secondo, j, nuovo, z, 1);
						//nuovo[z]=secondo[j];
						j++;
					}
					//System.out.println("z: "+z+" "+uno+" "+due+" -> "+nuovo[z].getText(3));
				}else{
					if(uno<due){
							System.arraycopy(primo, i, nuovo, z, 1);
							i++;
					}else{
						System.arraycopy(secondo, j, nuovo, z, 1);
						j++;
					}
				}
		}
		}//fine for
		return nuovo;
	}


}