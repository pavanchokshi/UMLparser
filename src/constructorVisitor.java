package umlparser;

import java.io.FileInputStream;
import java.security.Policy.Parameters;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class constructorVisitor extends VoidVisitorAdapter{
	String retuml,retname;
	 List classnames=new ArrayList<>();
	 List classtype=new ArrayList<>();
 
	 Boolean currclasstype;
	 
	 public void setuml(String uml,String name,List names,List types)
	 {
		 retname=name;
		 retuml=uml;
		 classnames=names;
		 classtype=types;
	 }

	
	@Override
    public void visit(ConstructorDeclaration n, Object arg) {
		
        // here you can access the attributes of the method.
        // this method will be called for all methods in this 
        // CompilationUnit, including inner class methods
	   //System.out.println("For class:" +classnames);
		
	   String construcname=n.getName();
	   System.err.println("Constructor: " +construcname);
	   List param=n.getParameters();
	   System.out.println("Constructor params:" +param);
	   System.out.println("Constructor uml    " +retuml);
	   int mod=n.getModifiers();
	   System.out.println("Modifier: " +mod);
	   
	   if(param.isEmpty())
	   {
		   if(mod==1)
		   {
			   retuml+="\n" +retname +" : + " +construcname + " ( " +param.toString().replace("[", "").replace("]", "")+ " ) " +"\n";
		   }
		   else{}
	   }
	   
	   else
	   {
		   for(int i=0;i<param.size();i++)
    		{	int j;
			   for(j=0;j<classnames.size();j++)
			   {
				  String[] splitparam=param.get(i).toString().split(" ");
				  String s1=splitparam[0];
				  String s2=splitparam[1];
				  System.err.println("S1" +s1 +"\n" +"s2" +s2);
    		if(s1.equals(classnames.get(j).toString()))
    		{
    		
    			if(currclasstype.toString().equals("false")&& classtype.get(j).toString().equals("true"))
    			{
    				//System.err.println("Class name:" +classnames.get(j).toString()+"Current class type:" +currclasstype);
    			System.out.println(param.get(i).toString()+" Contains "+ classnames.get(j).toString());
    			retuml+="\n" +retname+"..>" +classnames.get(j) + " :uses " + "\n";
    			retuml+="\n" +retname +" : + " +construcname +" ( "+ s2 +" : "+ s1 + " ) " +"\n";
    			break; 
    			}
    		}
    		else
    		{
    			
    		}
			   }
if(j==classnames.size())
{
	retuml+="\n" +retname +" : + " +construcname + " ( " +param.get(i)+ " ) " +"\n";
	}
    		}  
		
	   }  
    }
   
   public String getumlmethods()
   {
	   
	   return retuml;
   }
}

