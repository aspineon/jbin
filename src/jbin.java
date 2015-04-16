import java.io.*;
import java.util.Scanner;

public class jbin
{
	public static void jarToBinary(String jar, String binary) throws IOException
	{
		try {
			File binaryFile = new File(binary);

			// Deletes file if exists.
			binaryFile.delete();

			// Creates blank file.
			binaryFile.createNewFile();
		} catch (FileNotFoundException e) {

		} catch (IOException e) {
			System.out.println("Error reading file '" + binary + "'.");
		}

		// Creates executable binary.
		try {
			byte[] buffer = new byte[(int) (new File(jar)).length()];

			FileInputStream shebangInputStream = new FileInputStream("shebang.txt");
			FileInputStream jarInputStream = new FileInputStream(jar);

			int shebangRead = 0;
			int jarRead = 0;

			while((jarRead = jarInputStream.read(buffer)) != -1 && (shebangRead = shebangInputStream.read(buffer)) != -1) {
				FileOutputStream outputStream = new FileOutputStream(binary);

        outputStream.write(buffer);
        outputStream.close();
			}

			shebangInputStream.close();
			jarInputStream.close();
		} catch (FileNotFoundException e) {
			System.out.println("Unable to open file '" + jar + "'.");
		} catch (IOException e) {
			System.out.println("Error reading file '" + jar + "'.");
		}

		// Allow execution.
		try {
			File binaryFile = new File(binary);

			binaryFile.setExecutable(true);
		} catch (SecurityException e) { // If permissions are not sufficient.
			System.out.println("Unable to set '" + binary + "' to executable.");
			System.out.println("Do this manually with: chmod +x " + binary);
		}
	}

	public static void sourceToJAR(String main, String[] additional, String jar) throws IOException, InterruptedException
	{
		// Create JAR...

		// Create META-INF/MANIFEST.MF if it doesn't exist.
		try {
			File manifest = new File("META-INF/MANIFEST.MF");

			// Ensure META-INF directory exists.
			manifest.getParentFile().mkdirs();

			if (!manifest.exists()) { // If it doesn't exist...
				manifest.createNewFile(); // ...create it.

				OutputStream manifestOutput = new FileOutputStream(manifest);
				String manifestContent = "Main-Class: " + main.substring(0, main.length() - 5);

				manifestOutput.write(manifestContent.getBytes());
				manifestOutput.flush();
				manifestOutput.close();
			}
		} catch (IOException e) {
			System.out.println("Could not create manifest file for JAR.");
			System.out.println(e.getMessage());
		}

		// Compile everything.
		try {
			Process compileSource = Runtime.getRuntime().exec("javac " + main); // TODO: compile additional files.
			compileSource.waitFor();
		} catch (InterruptedException e) {
			System.out.println("Could not compile Java source.");
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		jbin.sourceToJAR(args[0], new String[0], args[1]);
	}
}
