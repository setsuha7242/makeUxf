package makeUxf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MakeUxf {
	public CodesReader cr;

	public MakeUxf(CodesReader cr) {
		this.cr = cr;
	}

	public void uxf(String name) {
		try {
			make_uxf_dir();
			FileWriter fw = new FileWriter(name+".uxf");
			fw.write(made_xml());
			fw.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void make_uxf_dir(){
		String path = new File(".").getAbsoluteFile().getParent();
        try {
			File dir = new File(path+"/uxf");
			dir.mkdir();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String made_xml() {
		String ret =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
        		"<diagram program=\"umlet\" version=\"14.2\">\n" +
        		"  <zoom_level>10</zoom_level>\n" +
        		addElements()+
			"</diagram>";
		return ret;
	}

	public String addElements() {
		String ret = "";
		int x=120,y=140;
		for (String className: cr.getCodes().keySet()) {
			ClassElements ce = cr.getCodes().get(className);
			ArrayList<String> lines = new ArrayList<>();
			//クラス名
			String set = "";
			switch (ce.getClassType().get(0)) {
				case "enum":
					lines.add("<<enumeration>>");
					break;
				case "abstract":
					set += "/";
					break;
				case "interface":
					lines.add("<<interface>>");
					break;
				default:
					break;
			}
			lines.add(set+className+set);
			lines.add("--");
			//フィールド //[修飾子,isStatic,isFinal,型,フィールド名,コメント]
			for (String[] cf :ce.getFileds()) {
				switch (cf[0]) {
					case "public":
						set = "+";
						break;
					case "private":
						set = "-";
						break;
					case "protected":
						set = "#";
						break;
					default:
						set = "~";
						break;
				}
				if(cf[3] == "enum")
					set="";
				set+=cf[4];
				if(cf[3] != "enum")
					set+=": "+cf[3];
				if(cf[2] == "true")
					set = "_"+set+"_";
				lines.add(set);
			}
			lines.add("--");
			//メソッド //[修飾子,isStatic,isAbstract,型,引数含むメソッド名,コメント]
			for (String[] cm :ce.getMethods()) {
				String s = "",a ="";
				switch (cm[0]) {
				case "public":
					set = "+";
					break;
				case "private":
					set = "-";
					break;
				case "protected":
					set = "#";
					break;
				default:
					set = "~";
					break;
				}
				if(cm[1] == "true")
					s = "_";
				if(cm[2] == "true")
					a = "/";
				set += cm[4];
				if(cm[3] != "")
					set += ": "+cm[3];

				lines.add(s+a+set+a+s);
			}

			String rets="";
			int h=0,w=0;
			h = lines.size()*15+10;
			for (String line:lines) {
				if(line.length()*7+10 > w)
					w = line.length()*7+10;
				rets+=line.replace("<", "&lt;").replace(">", "&gt;")+"\n";
			}
			ret += "<element>\n"+ "<id>UMLClass</id>\n";
			ret += "<coordinates>\n";
			ret += "<x>"+x+"</x>\n"+"<y>"+y+"</y>\n"+"<w>"+w+"</w>\n" +"<h>"+h+"</h>\n";
			ret += "</coordinates>\n";
			ret += "<panel_attributes>";
			ret += rets;
			ret += "</panel_attributes>\n" + "<additional_attributes/>\n" + "</element>\n";
			x+=50;
			y+=50;
		}
		return ret;
	}


}

