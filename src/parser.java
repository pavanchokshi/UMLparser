package umlparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.*;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderUtil;

public class parser {

	public static void main(String[] args) throws Exception {
		// creates an input stream for the file to be parsed
		String umlparser = "";
		File folder = new File(args[0]);
		File[] listOfFiles = folder.listFiles();
		int nooffiles = listOfFiles.length;

		CompilationUnit[] cu = new CompilationUnit[nooffiles];

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].getName().contains(".java")) {
				FileInputStream in = new FileInputStream(listOfFiles[i]);
				cu[i] = JavaParser.parse(in);
				in.close();
			}
		}
		ClassVisitor c = new ClassVisitor();

		// call to initialize all class names
		for (int i = 0; i < listOfFiles.length; i++) {
			if (cu[i] != null) {
				c.visit(cu[i],1);
			}
		}

		for (int i = 0; i < listOfFiles.length; i++) {
			if (cu[i] != null) {
				c.visit(cu[i], null);
				umlparser += c.getumlparser();			
				
			}
		}


	List classnames=c.names;
		
		
		
		 for (int i = 0; i < classnames.size() ; i++) 
		    {
		    	for (int j = 0; j < classnames.size() ; j++) 
		    	{
		    		if(classnames.get(i).toString().equals(classnames.get(j).toString()))
		    				{
		    			
		    				}
		    		else
		    		{
		    			String compare=classnames.get(i).toString()+"--"+classnames.get(j).toString();
		    			String reverse=classnames.get(j).toString()+"--"+classnames.get(i).toString();
		    			if(umlparser.contains(compare) && umlparser.contains(reverse))
		    			{
		    				umlparser=umlparser.replace(compare, "");
		    			}
		    			String compare1=classnames.get(i).toString()+"--"+classnames.get(j).toString();
		    			String reverse1=classnames.get(j).toString()+" - \"*\""+classnames.get(i).toString();
		    			if(umlparser.contains(compare1) && umlparser.contains(reverse1))
		    			{
		    				umlparser=umlparser.replace(compare1, "");
		    			}
		    			String compare2=classnames.get(i).toString()+" - \"*\""+classnames.get(j).toString();
		    			String reverse2=classnames.get(j).toString()+"--"+classnames.get(i).toString();
		    			if(umlparser.contains(compare2) && umlparser.contains(reverse2))
		    			{
		    				umlparser=umlparser.replace(reverse, "");
		    			}
		    			String compare3=classnames.get(i).toString()+" - \"*\""+classnames.get(j).toString();
		    			String reverse3=classnames.get(j).toString()+" - \"*\""+classnames.get(i).toString();
		    			if(umlparser.contains(compare3) && umlparser.contains(reverse3))
		    			{
		    				umlparser=umlparser.replace(compare3,classnames.get(j).toString()+"\"*\" - \"*\""+classnames.get(i).toString());
		    			}
		    			/*String compare4=classnames.get(i).toString()+" <|.. "+classnames.get(j).toString();
		    			String reverse4=classnames.get(j).toString()+"--"+classnames.get(i).toString();
		    			if(umlparser.contains(compare4) && umlparser.contains(reverse4))
		    			{
		    				umlparser=umlparser.replace(reverse4, "");
		    			}*/
		    			
		    			/*String compare5=classnames.get(i).toString()+"..>"+classnames.get(j).toString()+ " :uses ";
		    			String reverse5=classnames.get(i).toString()+"..>"+classnames.get(j).toString()+ " :uses ";
		    			if(umlparser.contains(compare5))
		    			{
		    				umlparser=umlparser.replace(compare5,"");
		    				umlparser= umlparser+ compare5 +"\n";
		    				
		    			}*/
		    		}
		    	}	
				
			}
		 String source = "@startuml \n skinparam classAttributeIconSize 0 \n" + umlparser + "\n@enduml";
		 
		
		System.out.println(source);
		
		SourceStringReader reader = new SourceStringReader(source);
		// Write the first image to "png"

		OutputStream png = null;
		try {
			png = new FileOutputStream(args[1]);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		;

		try {
			String desc = reader.generateImage(png);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Return a null string if no generation

	}

	// get class
	private static class ClassVisitor extends VoidVisitorAdapter {

		String nameofClass;
		
		String uml;
		List names = new ArrayList<>();
		List types= new ArrayList<>();

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Object arg) {
			// here you can access the attributes of the method.
			// this method will be called for all methods in this
			// CompilationUnit, including inner class methods
			uml = "";
			Boolean currclasstype=n.isInterface();
			// get all class names of the files
			if (arg!=null) 
			{
				String classNames = n.getName();
				names.add(classNames);
				
				if(n.isInterface())
				{
					types.add(true);
				}
				else
				{
					types.add(false);
				}
			
			} 
			else {
				// for interfaces
				if (n.isInterface()) {
					nameofClass = n.getName();
					uml += "\nInterface " + nameofClass + "\n";
					java.util.List<ClassOrInterfaceType> extendingClass = n.getExtends();
					if (extendingClass.isEmpty()) {
						// do nothing
					} else {
						// for interfaces extending multiple interfaces
						for (int i = 0; i < extendingClass.size(); i++) {
							String ext = extendingClass.toString().replace("[", "");
							ext = ext.replace("]", "");
							uml += ext + " <|-- " + nameofClass + "\n";
						}
					}
				} else
				// for classes
				{
					nameofClass = n.getName();

					java.util.List<ClassOrInterfaceType> extendingClass = n.getExtends();
					if (extendingClass.isEmpty()) {
						// do nothing
					} else {
						// append the uml with the extending class
						String ext = extendingClass.toString().replace("[", "");
						ext = ext.replace("]", "");
						uml += ext + " <|-- " + nameofClass + "\n";
					}

					java.util.List<ClassOrInterfaceType> implementingInterfaces = n.getImplements();
					if (implementingInterfaces.isEmpty()) {
						// do nothing
					} else {
						// for classes implementing multiple interfaces
						for (int i = 0; i < implementingInterfaces.size(); i++) {
							String ext = implementingInterfaces.get(i).toString().replace("[", "");
							ext = ext.replace("]", "");
							uml += ext + " <|.. " + nameofClass + "\n";
						}

					}
				}

				// get variables and assign + or -
				methodAccess m = new methodAccess();
				m.setuml(uml, nameofClass,names,types);
				m.currclasstype=currclasstype;
				m.visit(n, null);
				uml = m.getumlmethods();
				
				constructorVisitor cv=new constructorVisitor();
				//System.out.println("UML value passing"+uml);
				cv.setuml(uml, nameofClass,names,types);
				cv.currclasstype=currclasstype;
				cv.visit(n, null);
				uml = cv.getumlmethods();
				
			}
			
		}

		public String getumlparser() {
			return uml;
			
		}

	}

}