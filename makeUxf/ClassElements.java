package makeUxf;


import java.util.ArrayList;
/**
 * クラスの構成要素を表すクラス
 * @author tomoe
 *
 */
public class ClassElements {
	private ArrayList<String> classType; //classType&comment
	private String extendsClass;
	private String[] implementsClass;
	private ArrayList<String[]> fileds; //[修飾子,isStatic,isFinal,型,フィールド名,コメント]
	private ArrayList<String[]> methods; //[修飾子,isStatic,isAbstract,型,引数含むメソッド名,コメント]
	private String className;

	public ClassElements(String className) {
		classType = new ArrayList<>();
		fileds = new ArrayList<>();
		methods = new ArrayList<>();
		implementsClass = null;
		this.className = className;
	}

	public void addElemnts(String line) {
		String comment="";
		//コメント削除部
		int a = 0,b,c = 0;
		String tmp;
		while(a !=-1 || c !=-1) {
			a = line.indexOf("//");
			c = line.indexOf("/*");
			if(a!=-1) {
				if(a < (b=line.substring(a).indexOf("\n"))) {
					tmp = line.substring(a,b+1);
					comment +=  tmp;
					line=line.replace(tmp, "");
				}
				else {
					comment += line.substring(a);
					line=line.substring(0,a);
				}
			}
			if(c != -1) {
				tmp = line.substring(c,line.indexOf("*/")+2);
				comment+= tmp;
				line = line.replace(tmp, "");
			}
		}

		//ソースコード分解部
		line = line.replace("\n", "");

		if(line.contains("{")||line.contains("abstract")|| (!line.contains("final") && classType.get(0).equals("interface"))) {
			line = line.replace("{", "").replace("}", "").replace(";", "");
			//メソッド
			//[修飾子,isStatic,isAbstact,型,引数含むメソッド名,コメント]
			if(line.contains("(")) {
				String[] ret= {"","","","","",""};
				if(line.contains("static ")) {
					ret[1] = "true";
					line = line.replace("static ", "");
				}else {
					ret[1] = "false";
				}
				if(line.contains("abstract ")) {
					ret[2] = "true";
					line = line.replace("abstract ", "");
				}else {
					ret[2] = "false";
				}
				StringBuffer str = new StringBuffer(line);
				String alt_line = str.reverse().toString();
				//tmp = line.substring(line.indexOf("("),line.indexOf(")")+1);
				tmp = line.substring(line.indexOf("("),line.length() - alt_line.indexOf(")"));
				line = line.replace(tmp, "");
				String argument="";
				ArrayList<String> l = new ArrayList<>();
				if(tmp.length()!=2) {
					String temp = tmp.substring(1,tmp.length()-1);
					String[] tmp_split_comma = temp.trim().replaceAll(" +", " ").split(",");
					for(String tsc : tmp_split_comma) {
						String[] tsc_split_space = tsc.split(" ");
						String type="";
						for (int i = 0; i < tsc_split_space.length-1; i++) {
							type += tsc_split_space[i];
						}
						String name = tsc_split_space[tsc_split_space.length-1];
						l.add(name+": "+type);
					}
					argument = String.join(", ", l);
				}
				argument = "("+argument+")";


				String access="";
				if(line.contains("public "))
					access = "public";
				else if(line.contains("private "))
					access = "private";
				else if(line.contains("protected"))
					access = "protected";
				line = line.replace(access, "");

				String[] li = line.trim().replaceAll(" +", " ").split(" ");
				ret[0] = access;
				if(li.length == 1) {
					if(classType.get(0).equals("enum") && !line.contains(className)) {
						ret[5] = comment;
						ret[3] = "enum";
						ret[4] = li[0]+tmp;
						addFiled(ret);
						return;
					}
					ret[4] = li[0]+argument;
				}
				else {
					String type = "";
					for (int i = 0; i < li.length-1; i++) {
						type += li[i];
					}
					ret[3] = type;
					ret[4] = li[li.length-1]+argument;
				}
				ret[5] = comment;
				addMethod(ret);
			}
			//クラスType
			else {
				String[] li = line.split(" ");
				addClassType(li[1]);
				addClassType(comment);
				String[] im = line.split("implements");
				String[] ex = im[0].split("extends");
				if(im.length > 1) {
					implementsClass=im[1].replace(" ", "").split(",");
				}
				if(ex.length > 1) {
					extendsClass= ex[1].replace(" ", "");
				}

			}
		}

		//フィールド
		else {
			line = line.replace(";", "");
			//[修飾子,isStatic,isfinal,型,フィールド名,コメント]
			String[] ret= {"","","","","",""};
			if(line.contains("static ")) {
				ret[1] = "true";
				line = line.replace("static", "");
			}else {
				ret[1] = "false";
			}
			if(line.contains("final ")) {
				ret[2] = "true";
				line = line.replace("final", "");
			}else {
				ret[2] = "false";
			}
			String access="";
			if(line.contains("public "))
				access = "public";
			else if(line.contains("private "))
				access = "private";
			else if(line.contains("protected"))
				access = "protected";
			line = line.replace(access, "");
			String[] li = line.trim().replaceAll(" +", " ").split(" ");
			ret[0] = access;

			if(classType.get(0).equals("enum") && (line.contains(",")||line.contains("(") && (!line.contains("Map")))) {
				if(line.contains("("))
					li = li[0].split(",");
				String[] comments = {};
				ret[3] = "enum";
				if(comment.contains("*/")) {
					comments= comment.split("\\*/");
					for(int i= 0; i <comments.length; i++)
						comments[i] = comments[i]+"*/";
				}
				else if(comment.contains("//")) {
					comment = comment.substring(2);
					comments = comment.split("//");
					for(int i= 0; i <comments.length; i++)
						comments[i] = "//"+comments[i];
				}

				for (int i = 0; i < li.length;i++) {
					ret = ret.clone();
					ret[4] = li[i].replace(",","");
					if(comments.length > i)
						ret[5] = comments[i];
					addFiled(ret);
				}
				return;
			}
			else {
				String type = "";
				for (int i = 0; i < li.length-1; i++) {
					type += li[i];
				}
				ret[3] = type;
				ret[4] = li[li.length-1];
			}
			ret[5] = comment;
			addFiled(ret);
		}
	}

	public void addClassType(String str) {
		classType.add(str);
	}
	public void addFiled(String[] strs) {
		fileds.add(strs);
	}
	public void addMethod(String[] strs) {
		methods.add(strs);
	}

	public ArrayList<String[]> getFileds(){
		return fileds;
	}

	public ArrayList<String> getClassType() {
		return classType;
	}

	public String getExtendsClass() {
		return extendsClass;
	}

	public String[] getImplementsClass() {
		return implementsClass;
	}

	public ArrayList<String[]> getMethods() {
		return methods;
	}

	public void showClassElements() {
		System.out.println("classType:");
		for(int i = 0; i < classType.size(); i++)
			System.out.print(i+":"+classType.get(i).replace("\n", "\\n")+", ");
		System.out.println();
		System.out.println("extends");
		System.out.println(extendsClass);
		System.out.println("implements");
		if(implementsClass != null) {
			for(String im:implementsClass)
				System.out.print(im+", ");
		}else {
			System.out.print("null");
		}
		System.out.println();
		System.out.println("fileds");
		for(String[] strs: fileds) {
			for(int i = 0; i < strs.length; i++)
				System.out.print(i+":"+strs[i].replace("\n", "\\n")+", ");
			System.out.println();
		}
		System.out.println("methods");
		for(String[] strs: methods) {
			for(int i = 0; i < strs.length; i++)
				System.out.print(i+":"+strs[i].replace("\n", "\\n")+", ");
			System.out.println();
		}
	}


}
