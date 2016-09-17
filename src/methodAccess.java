package umlparser;

import java.io.FileInputStream;
import java.security.Policy.Parameters;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

//getmethods
    public class methodAccess extends VoidVisitorAdapter{
    	 String retuml,retname;
    	 List classnames=new ArrayList<>();
    	 List classtype=new ArrayList<>();
    	 String multipl="<";
    	 
    	 Boolean currclasstype;
    	 
    	 public void setuml(String uml,String name,List names,List types)
    	 {
    		 retname=name;
    		 retuml=uml;
    		 classnames=names;
    		 classtype=types;
    	 }
    //Method to find variables
		@Override
		public void visit(FieldDeclaration n, Object arg) {
		    //get variable names
		    List varname=n.getVariables();
		    //get type of variables
		    Type type=n.getType();
		    //get modifier
		    int mod=n.getModifiers();
		    for(int i=0;i<varname.size();i++)
		    {
		    	//System.out.println("For variable:" +varname.get(i).toString());
		    	//for public variables
		    	if(mod==1)
		    	{
		    		int j;
		    		for(j=0;j<classnames.size();j++)
		    		{
		    			String comp=classnames.get(j).toString();
		    			if(type.toString().equals(comp))
		    		{
		    				//Association
		    			//System.out.println(varname.get(i) +" contains"+ comp);
		    				
		    				retuml+="\n" +retname+ "--" +comp+"\n";
		    			break;
		    			
		    		}
		    			else if(type.toString().equals("Collection<"+comp+">"))
		    			{
		    				//Association with multiplicity
		    				//System.out.println(varname.get(i) +" contains"+ comp);
		    				
		    				retuml+="\n" +retname + " - \"*\"" +comp +"\n";
		    				break;			
		    			}
		    			else if(type.toString().equals("[]"+comp))
		    			{
		    				
		    				retuml+="\n" +retname + " - \"*\"" +comp +"\n";
		    				break;			
		    			}
		    		} 		
		    	if(j==classnames.size())
		    	{
		    		retuml+="\n" +retname +" : +" +varname.get(i)+" : "+type+"\n";
		    	}
		    }
		    	//for private variables
		    	else if(mod==2)
		    	{
		    		System.err.println("Private variable found: " +varname.get(i).toString());
		    		int j;
		    		for(j=0;j<classnames.size();j++)
		    		{
		    			String comp=classnames.get(j).toString();			
		    			if(type.toString().equals(comp))
		    		{
		    				//Association
		    			System.err.println(varname.get(i) +" contains"+ comp);
		    				retuml+="\n" +retname+ "--" +comp+ "\n";
		    				
		    			break;
		    			
		    		}
		    			else if(type.toString().equals("Collection<"+comp+">"))
		    			{
		    				//Association with multiplicity
		    				//System.out.println(varname.get(i) +" contains"+ comp);
		    				retuml+="\n" +retname + " - \"*\"" +comp +"\n";
		    				
		    				break;			
		    			}
		    			else if(type.toString().equals("[]"+comp))
		    			{
		    				retuml+="\n" +retname + " - \"*\"" +comp +"\n";
		    				break;
		    			}
		    		} 		
		    	if(j==classnames.size())
		    	{
		    		retuml+="\n" +retname +" : -" +varname.get(i)+" : "+type+"\n";
		    	}
		    	}
		    	
		    }
		  
		    super.visit(n, arg);
		}
		
       @Override
        public void visit(MethodDeclaration n, Object arg) {
            // here you can access the attributes of the method.
            // this method will be called for all methods in this 
            // CompilationUnit, including inner class methods
    	   //System.out.println("For class:" +classnames);
    	   String methodname=n.getName();
    	   //System.out.println("Method names: " +methodname);
    	   Type methtype=n.getType();
    	   List param=n.getParameters();
    	   int mod=n.getModifiers();
    	   
    	   
    	   //Check for main methods
    	   if(methodname.equals("main") && mod==9)
    	   {
    		   if(n.getBody()!=null)
        	   {
        	   String body=n.getBody().toStringWithoutComments();
        	   
        	   int j;
        	   for(j=0;j<classnames.size();j++)
			   {
    		if(body.contains(classnames.get(j).toString()))
    		{
    			
    			if(currclasstype.toString().equals("false") && classtype.get(j).toString().equals("true") )
    			{	//Dependency of class on I/F
    				//System.err.println("Class found:" +classnames.get(j).toString());
    				retuml+="\n" +retname+"..>"+classnames.get(j).toString() + " :uses " +"\n";
    				retuml+="\n" +retname+ " : +" +methodname+ "( "+ n.getParameters().toString().replace("[", "").replace("]", "")+ " ) :" +methtype+ "\n";
    				break;
    			}
    			
    		}
    		
			   }
        	   if(j==classnames.size())
          		{
          			retuml+="\n" +retname+ " : +" +methodname+ "( "+ n.getParameters().toString().replace("[", "").replace("]", "")+ ") :" +methtype+ "\n";
          		}
        	   }
    		   else
    		   {
    			   retuml+="\n" +retname+ " : +" +methodname+ "( "+ n.getParameters().toString().replace("[", "").replace("]", "")+ ") :" +methtype+ "\n";
    		   }
    		   
    	   }
 	 
    	   
  //for methods with empty parameters
    	   
    	   if(n.getParameters().isEmpty())
    	   {
    		   if(mod==1)
    		   {
    			   if(methodname.toString().contains("get") || methodname.toString().contains("set") )
    			   {
    				   String mname=methodname.toString();
    				   String var=mname.substring(3).toLowerCase();
    				
    				   //System.err.println(var);
    				   //do not add
    				   String mbody=n.getBody().toStringWithoutComments();
    				   if(mbody.contains(var))
    				   {
    					   if(retuml.contains(retname +" : -" +var))
    					   {
    						   System.err.println("Replaced");
    						   retuml=retuml.replace(retname +" : -" +var,retname +" : +" +var);
    					   }
    					   else
    					   {} 
    				   System.err.println("Getter found");
    				   }
    				   else
    				   {
    					   retuml+="\n" +retname+ " : +" +methodname+ "(" + n.getParameters().toString().replace("[", "").replace("]", "")+ ") :" +methtype+ "\n";
    				   }
    			   }
    			   else
    			   {
    			   retuml+="\n" +retname+ " : +" +methodname+ "( "+ n.getParameters().toString().replace("[", "").replace("]", "")+ ") :" +methtype+ "\n";
    			   }
    		   }
    		   else{}
    	   }
   //for methods with parameters
    	   else if(!n.getParameters().isEmpty())
    	   {	
    		   if(mod==1)
    		   {
    			   if(methodname.toString().contains("get") || methodname.toString().contains("set") )
    			   {
    				   String mname=methodname.toString();
    				   String var=mname.substring(3).toLowerCase();
    				
    				   //System.err.println(var);
    				   //do not add
    				   String mbody=n.getBody().toStringWithoutComments();
    				   if(mbody.contains(var))
    				   {
    					   if(retuml.contains(retname +" : -" +var))
    					   {
    						   System.err.println("Replaced");
    						   retuml=retuml.replace(retname +" : -" +var,retname +" : +" +var);
    					   }
    					   else
    					   {} 
    				   System.err.println("Setter found");
    				   }
    				   else
    				   {
    					   retuml+="\n" +retname+ " : +" +methodname+ "(" + n.getParameters().toString().replace("[", "").replace("]", "")+ ") :" +methtype+ "\n";
    				   }
    			   }  
    			   else
    			   {
    		   for(int i=0;i<param.size();i++)
	    		{	int j;
    			   for(j=0;j<classnames.size();j++)
    			   {
	    		if(param.get(i).toString().contains(classnames.get(j).toString()))
	    		{
	    			
	    			if(currclasstype.toString().equals("false") && classtype.get(j).toString().equals("true") )
	    			{	//Dependency of class on I/F
	    				//System.err.println(classnames.get(j) +" " +classtype.get(j) );
	    				retuml+="\n" +retname+"..>"+classnames.get(j).toString()+ " :uses "+ "\n";
	    				retuml+="\n" +retname+ " : +" +methodname+ "( "+ n.getParameters().toString().replace("[", "").replace("]", "")+ ") :" +methtype+ "\n";
	    				break;
	    			}
	    			else if(currclasstype.toString().equals("true") && classtype.get(j).toString().equals("false"))
	    			{
	    				System.out.println("Interface:" +classnames.get(j));
	    				retuml+="\n" +retname+ " : +" +methodname+ "( "+ n.getParameters().toString().replace("[", "").replace("]", "")+ ") :" +methtype+ "\n";
	    			}
	    		}
	    		
    			   }
    			 if(j==classnames.size())
         		   {
         			   retuml+="\n" +retname+ " : +" +methodname+ "( "+ n.getParameters().toString().replace("[", "").replace("]", "")+ ") :" +methtype+ "\n";
         		   }
    			   
	    		}  
    			   }
    		   }
    		   	
    	   }
    	   
        }
       
       public String getumlmethods()
       {
    	   
    	   return retuml;
       }
    }