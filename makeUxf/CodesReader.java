package makeUxf;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class CodesReader {
	private ArrayList<String[]> extendsArrows;//継承クラス関係
	private ArrayList<String[]> implementsArrows;//実装クラス関係
	private ArrayList<String[]> transferArrows;//委譲関係
	private HashMap<String, ClassElements> codes;//<クラス名,クラスの要素を持つClassElementsクラス>

	public CodesReader() {
		extendsArrows = new ArrayList<>();
		implementsArrows = new ArrayList<>();
		transferArrows = new ArrayList<>();
		codes = new HashMap<>();
	}

	public void codesRead(String[] filePaths) {
		for (String path : filePaths) {
			File file = new File(path);
			codeRead(file.getAbsolutePath());
		}
		setArrow();
	}

	public void codeRead(String filePaths) {
		String[] filePath = filePaths.split("/");
		String fileName = filePath[filePath.length - 1];
		if(fileName.indexOf(".java") == -1){
			System.out.println("javaクラスでないファイルが読み込まれました");
		}
		else{
			String className = fileName.split(".java")[0];
			//System.out.println("ClassName is " + className);
			codes.put(className, new ClassElements(className));
			makeCodes(filePaths, className);
		}
	}

	/**
	 * filenameクラスにClassElementsクラスにクラスの要素を追加する
	 * @param filename
	 */
	public void makeCodes(String filePaths, String className) {
		ClassElements ce = codes.get(className);
		Path path = Paths.get(filePaths);
		try(BufferedReader reader = Files.newBufferedReader(path)){
			String line;
			int count = -1;
			while((line = reader.readLine()) != null) {
				String subLine="";
				line = line.replace("\t", "");
				if(line.equals("") || line.contains("@"))
					continue;
				while(!endKey(line)) {
					String at = reader.readLine();
					if(at.contains("@"))
						continue;
					line+="\n"+at.replace("\t", "");
				}
				if(line.replace(" ","").replace("\n", "").equals("}"))
					break;
				count += search(line, '{');
				count -= search(line, '}');
				if(count < 0)
					continue;
				while(count != 0) {
					subLine = reader.readLine();
					count+=search(subLine,'{');
					count-=search(subLine,'}');
				}
				ce.addElemnts(line);
			}
		}catch(IOException e) {
			System.out.println(e);
		}
	}

	/**
	 * 文字列から文字の個数を探索するメソッド
	 * @param str
	 * @param target
	 * @return 文字の個数
	 */
	public int search(String str, char target){
		int count = 0;
		for(char x: str.toCharArray()){
			if(x == target){
				count++;
			}
		}
		return count;
	}

	/**
	 * 文字列にJavaソースコードにおける行の終了コードが含まれているかを返すメソッド
	 * @param line
	 * @return
	 */
	public boolean endKey(String line) {
		boolean b1 = (line.contains(";"));
		boolean b2 = (line.contains("{"));
		boolean b3 = (line.contains("}"));
		return  b1||b2||b3;
	}

	/**
	 * uml図生成時のクラス関係の矢印を追加するメソッド
	 *
	 */
	private void setArrow() {
		for(String className : codes.keySet()) {
			//System.out.println("Arrows:"+className);
			ClassElements ce = codes.get(className);
			String[] ret = new String[2];
			ret[0] = className;
			//継承関係
			if(ce.getExtendsClass() != null && codes.containsKey(ce.getExtendsClass())) {
				ret[1] = ce.getExtendsClass();
				extendsArrows.add(ret);
			}
			//実装クラス関係
			if(ce.getImplementsClass() != null) {
				for (String ic: ce.getImplementsClass()) {
					if(codes.containsKey(ic)) {
						ret = ret.clone();
						ret[1] = ic;
						implementsArrows.add(ret);
					}
				}
			}
			//委譲関係
			ret = ret.clone();
			ret[1] = className;
			for (String rClassName: codes.keySet()) {
				for(String[] fd: ce.getFileds()) {
					if(fd[3].equals(rClassName)) {
						ret = ret.clone();
						ret[0] = rClassName;
						transferArrows.add(ret);
					}
				}
			}
		}
	}
	public void arrowsshow() {
		System.out.println();
		for(String[] st: extendsArrows) {
			System.out.println("継承関係: "+st[0]+" ---▷ "+st[1]);
		}
		System.out.println();
		for(String[] st: implementsArrows) {
			System.out.println("実装関係: "+st[0]+" - -▷ "+st[1]);
		}
		System.out.println();
		for(String[] st: transferArrows) {
			System.out.println("委譲関係: "+st[0]+" ---◆ "+st[1]);
		}
	}

	public void allCodesShow() {

		for(String st : codes.keySet()) {
			System.out.println(st);
			codes.get(st).showClassElements();
			System.out.println();
		}
		arrowsshow();
	}

	public ArrayList<String[]> getExtendsArrows() {
		return extendsArrows;
	}

	public ArrayList<String[]> getImplementsArrows() {
		return implementsArrows;
	}

	public ArrayList<String[]> getTransferArrows() {
		return transferArrows;
	}

	public HashMap<String, ClassElements> getCodes() {
		return codes;
	}
}
