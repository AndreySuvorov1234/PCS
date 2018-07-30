import java.util.Arrays;

	//import java.util.Scanner;
	class preProcess
	{
		//holds the array of volumes and the array of counts
		//volumes are the calculated values for preprocessing step.
		//count is the amount of data points collected for each 
		//corresponding (by indexes) volume value.
		double[] volumes;
		int[] count;
		public preProcess() {}
		public preProcess(double[] volumes, int[] count)
		{
			this.volumes = volumes;
			this.count = count;
		}
		
		public void setVolumes(double[] volumes)
		{
			this.volumes=volumes;
		}
		
		public void setCount(int[] count)
		{
			this.count = count;
		}
		
		public double[] getVolumes()
		{
			return this.volumes;
		}
		
		public int[] getCount()
		{
			return this.count;
		}
	}
	
	public class PCS
	{
		public static void main(String[] args) 
		{
			double[] x =	{0.0 , 1.0/2.0, 1};
	        double[] y =	{1.0/2.0 , 3.0/2.0, 1};
			//double[] x =	{0.0, 1.0/8, 1.0/4, 3.0/8, 1.0/2, 5.0/8, 3.0/4, 7.0/8};
	        //double[] y =	{1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0};
			//double[] x =	{0.0 , 1.0/4, 1.0/2.0, 3.0/4.0};
	        //double[] y =	{1.0/2.0 , 3.0/2.0, 0, 0};
	        int		 m =	1;
	        double	eps=	Math.pow(2, -15);
	        
	        System.out.print(Arrays.toString(x));
	        System.out.println(Arrays.toString(y));
	        
	        /*
	        Scanner s = new Scanner(System.in); 
	        System.out.println("Please input the maxScale value: ");
	        int m = s.nextInt();
	        s.close();
	        */
	        
	        
	        double[] pcsVal1	= getTopDownPCS(x,y, eps);
	        double[] pcsVal2	= getBottomUpPCS(x,y, 0, eps);
	        double[] pcsVal3	= getBottomUpPCS(x,y, 1, eps);
	        System.out.print("top down PCS:"+Arrays.toString(pcsVal1));
	        System.out.print("\nbottom up PCS m=0:"+Arrays.toString(pcsVal2));
	        System.out.print("\nbottom up PCS m=1:"+Arrays.toString(pcsVal3));
	    }

		
		//__Average Volumes of Intervals__ where interval is defined by maxScale,
		//based on y data recorded at x time stamps
		//all x values are assumed to be sorted and to be within the range: from 0 to 1;
		//BUILDS an array with with the volumes of the highest precision, precision is dictated by the maxScale value 
	    public static preProcess getAVI(double[] x, double[] y, int maxScale) 
	    {
	        double interval = Math.pow(2, maxScale + 1);
	        double[] avgV = new double[(int)interval];
	        int[] count = new int[avgV.length];
	        System.out.println("volume: "+Arrays.toString(avgV));
	        
	        //goes through the volume array of size=interval
	        for (int cutoff =0, i = 0; i < (int)interval; i++) 
	        {
	        	boolean emptySet = false;
	        	int n=0;
	        	System.out.println("i: "+i);
	        	System.out.println("CutOFF: "+cutoff);
	        	System.out.println("fraction: " +  (double)(i+1)*( 1.0/(interval)));
	        	//checks all the data up to current interval.
	        	//starts from a cutoff: where stopped previously.
	        	//continues until it reaches the end of the data file 
	        	//or the last interval(inclusive on the last interval, interval =1)
	            for(int a=cutoff;  
	            		a<x.length	&&	(x[a] < ( (double)(i+1)*( 1.0/(interval))) || ((double)(i+1) == interval));
	            	a++)
	            {
	            	System.out.println("a:"+a);
	            	//finds total volume in each interval
	            	avgV[i]+=y[a];
	            	cutoff++;
	            	n++;
	            	System.out.println(Arrays.toString(avgV));
	            	System.out.println("n value: " +n);
	            }
	            if(n==0)
	            {
	            	avgV[i]=0;
	            	emptySet = true;
	            }
	            else 
	            {
	                //finds average height of the interval
	            	avgV[i] = avgV[i]/(double)n;
	            	//finds average volume of the interval
	            	if (!emptySet)
	            		avgV[i] = avgV[i]/interval;
	            }
	            System.out.println("\t"+emptySet);
	            //stores the amount of
	            count[i]=n;
	        }
	        return new preProcess(avgV,count);
	    }
	    
	    
	    //finds the PCS values from an array of Volumes avgV, checks for floating point precision errors using eps :epsilon
	    public static double[] getBottomUpPCS(double[] x, double[] y, int maxScale, double eps)
	    {    
	    	double[] avgV = getAVI(x,y,maxScale).getVolumes();
	        double[] pcs = new double[(int)Math.pow(2, maxScale+1)-1];
	        System.out.println("Complete volume: "+Arrays.toString(avgV));
	    	
	        //fills PCS values into array
	        //first loop to account for "levels" of the tree or the generalization of data
	        for(int level=0, b=0; b < pcs.length; level++)
	        {
		        int interval= (int)Math.pow(2, level);
		        System.out.println(b);
		        
		        //this loop parses through average volume array by 2*interval at a time (leftV, rightV)=> ++2
		        //takes into account the interval generalization value  based on the maxScale;
		        for(int a=0; a < avgV.length-1; b++, a=a+2*interval)
		        {
		        	System.out.println("incompletePCS:"+Arrays.toString(pcs));
		        	//for clarity purposes the left and right "child node" values are created
		        	double 	leftV=  0.0,
		        			rightV= 0.0;
		        	//left and right children are given a value based on the level of the tree
		        	//interval implies the level via Math.pow(2,level) operation
		        	//must use interval value concept because the data is all in a single dimensional array
		        	//also because recursive calls are heavier than iterative approach.
		        	for(int i=a; i<a+interval; i++)
		        	{
		        		System.out.println("left node index(es): "+i);
		        		leftV	+=	avgV[i];
		        		System.out.println("right node index(es): "+(i+interval));
		        		rightV	+=	avgV[i+interval];
		        	}
		
		        	System.out.println("left: "+leftV);
		        	System.out.println("right: "+rightV);
		        	
		        	//PCS value calculation is done accounting for the epsilon value
		        	//must account for the floating point error.
		        	if((leftV + rightV) >=eps)
		        	{
		        		pcs[b] = (leftV - rightV)/(leftV + rightV);
		        	}
		        	
		        	else if((leftV + rightV) < eps)
		        	{
		        		pcs[b] = 0;
		        	}
		        }
	        }	
	        return pcs;     	
	        	
	     }
	    
	    //work in progress, most of the code needs to be replaced
	    //finds the PCS values from an array of Volumes avgV, checks for floating point precision errors using eps :epsilon
	    public static double[] getTopDownPCS(double[] x,double[] y, double eps)
	    {    
	    	int maxScale=-1;
	    	preProcess container = getAVI(x,y,maxScale);
	    	for(int a=0; a<2; a++)//while(Decision(container.getCount()))
	        {
	    		maxScale++;
	        	container=getAVI(x,y,maxScale);	
	        }

	         	double[] avgV = container.getVolumes();
		        double[] pcs = new double[(int)Math.pow(2, maxScale+1)-1];
		    	
		        //fills PCS values into array
		        //first loop to account for "levels" of the tree or the generalization of data
		        for(int level=0, b=0; b < pcs.length; level++)
		        {
			        int interval= (int)Math.pow(2, level);
			        System.out.println("b is: "+b);
			        
			        //this loop parses through volume array by 2*interval at a time (leftV, rightV)=> ++2
			        //takes into account the interval generalization value  based on the maxScale;
			        for(int a=0; a < avgV.length-1; b++, a=a+2*interval)
			        {
			        	System.out.println("incompletePCS:"+Arrays.toString(pcs));
			        	//for clarity purposes the left and right "child node" values are created
			        	double 	leftV=  0.0,
			        			rightV= 0.0;
			        	//left and right children are given a value based on the level of the tree
			        	//interval implies the level via Math.pow(2,level) operation
			        	//must use interval value concept because the data is all in a single dimensional array
			        	//also because recursive calls are heavier than iterative approach.
			        	for(int i=a; i<a+interval; i++)
			        	{
			        		System.out.println("left node index(es): "+i);
			        		leftV	+=	avgV[i];
			        		System.out.println("right node index(es): "+(i+interval));
			        		rightV	+=	avgV[i+interval];
			        		//accounting for inconsistency caused by the change of maxscales
				        	if(leftV==0)
				        	{
				        		avgV[i+interval]*=2;
				        	}
				        	if(rightV==0)
				        	{
				        		avgV[i]*=2;
				        	}  
			        	}
			
			        	System.out.println("left: "+leftV);
			        	System.out.println("right: "+rightV);
			        	
			          	      	
			        	System.out.println("left after: "+leftV);
			        	System.out.println("right after: "+rightV);
			        	
			        	//PCS value calculation is done accounting for the epsilon value
			        	//must account for the floating point error.
			        	if((leftV + rightV) >=eps)
			        	{
			        		pcs[b] = (leftV - rightV)/(leftV + rightV);
			        	}
			        	
			        	else if((leftV + rightV) < eps)
			        	{
			        		pcs[b] = 0;
			        	}
			        }
		        }	
		        return pcs;     	
		        	
		     }
		        	
		       
	    //to be implemented in the future
	    public static boolean Decision(int[] count)
	    {
	    	return true;
	    }
	    
	    
	    
	    //forces the total interval upon (0,1) scale, where (0 is exclusive, 1 is inclusive) 
	    //until there is a need to implement the sorting algorithm the default value: sorted = true
	    public static double[] normalize(double[] x, boolean sorted, double max, double min)
	    {
	    
	    	double maxold =0.0, minold=0.0;
	    	sorted=true;
	    	
	    	if(!sorted)
	    	{
	    		//pick sorting algorithm to use
	    	}
	    	maxold=x[x.length-1];
	    	minold=x[0];
	    	//re-scale formula used: (maxnew-minnew)/(maxold-minold)*(x-maxold)+maxnew
	    	for(int a=0; a<x.length; a++)
	    	{
	    		x[a] = (max-min)/(maxold-minold)*(x[a]-maxold)+max;
	    	}
	    	return x;
	    }
	    
	    //process x and y coordinates for variance calculation from the medical app
	    //formula: x^2 + y^2 = z
	    //assumes x and y are equal length
	    public static double[] prepXY(float[] x, float[] y)
	    {
	    	double[] z = new double[x.length];
	    	for(int i=0; i<z.length; i++)
	    	{
	    		z[i] = (x[i]*x[i]) + (y[i]*y[i]); 
	    	}
	    	return z;
	    }
	}


