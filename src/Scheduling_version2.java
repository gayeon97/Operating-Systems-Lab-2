import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
public class Scheduling_version2 {
	
	static int randomOS(int u) throws FileNotFoundException {
		List<String> fileStream = null;
		try {
			fileStream = Files.readAllLines(Paths.get(System.getProperty("user.dir") + "/src/random-numbers"),Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int fileSize = fileStream.size();
		System.out.println("This is the number of lines of the file: " + fileSize);
		
		int randIndex = (int) (Math.random()*fileSize);
		System.out.println("This is the random index chosen from the file: " + randIndex);
		
		int randIntegerChosen = Integer.parseInt(fileStream.get(randIndex));
		System.out.println("This is the random integer chosen using the random index of the file: " + randIntegerChosen);		
		
		System.out.println();		
		return 1 + (randIntegerChosen % u);
	}

	public static void main(String[] args) throws FileNotFoundException {		
		
		File inputFile = null;
		
		System.out.println("this is args.length: " + args.length);
		
		String url = System.getProperty("user.dir") + "/src/";		
		System.out.println(url);
		
		if (0 < args.length) {
			if (args.length == 2) {
				System.out.println("This is args[1]: " + args[1]);
				url += args[1];
				System.out.println(url);
				inputFile = new File(url);
			} else if (args.length == 1){
				System.out.println("This is args[0]: " + args[0]);
				url += args[0];
				System.out.println(url);
				inputFile = new File(url);
			}
		} else {
			System.err.println("Invalid number of command line arguments: " + args.length);
			System.exit(0);
		}
		
		ArrayList<ArrayList<Integer>> list = new ArrayList<>();

		Scanner sc = new Scanner(inputFile);
		sc.useDelimiter("[\\s\\(\\),]+");
		while (sc.hasNextInt()) {
			int numProcesses = sc.nextInt();
			System.out.println("This is number of processes: " + numProcesses);
			
			for (int i = 0; i < numProcesses; i ++) {
				ArrayList<Integer> current = new ArrayList<>();
				for (int j = 0; j < 4; j ++) {
					current.add(sc.nextInt());
				}
				list.add(current);		
			}
			
		}	
		sc.close();
		
		System.out.println();
		
		for (ArrayList<Integer> e: list) {
			for (int k = 0; k < e.size(); k ++) {
				System.out.println(e.get(k));
			}
			System.out.println();
		}
		
		System.out.println(randomOS(100));
		
		
	}

}
