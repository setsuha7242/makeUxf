package makeUxf;

import java.io.File;

public class Do{
	public static void main(String[] args) {
		CodesReader codesReader = new CodesReader();
		codesReader.codesRead(args);
		MakeUxf mx = new MakeUxf(codesReader);
		int i = 1;
		for(i = 1; new File(String.format("uxf/class%02d.uxf",i)).exists();i++);
		System.out.println(String.format("Write: class%02d.uxf",i));
		codesReader.arrowsshow();
		//codesReader.allCodesShow();
		System.out.println();
		mx.uxf(String.format("uxf/class%02d",i));
	}
}
