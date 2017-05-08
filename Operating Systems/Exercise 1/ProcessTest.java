import java.lang.ProcessBuilder.Redirect;

public class ProcessTest {

	@SuppressWarnings("unused")
	public static void main(String[] args) {

		String out = null;
		try {
			ProcessBuilder pb = new ProcessBuilder("java");
			pb.redirectOutput(Redirect.INHERIT).redirectError(Redirect.INHERIT);
			//pb.redirectError(Redirect.INHERIT);
			Process p = pb.start();
		} catch (Exception e) {
			System.out.println("An Exception occurred my friend, get ready:");
			e.printStackTrace();
		}
		// System.out.println("IS THIS THING WORKING");
	}
}
