import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Oscar Alcaraz Ismail Abbas
 * 
 * CS 431: Operating Systems Multi-Threading
 *
 * This program will take any given text file with a paragraph of input (or any length of input), 
 * and report to the user the number of upper case letters, lower case letters, and numbers that
 * are present within the iniput file.
 *
 */

public class ThreadTest {

	static String fileName;

	public static void main(String[] args) {

		BlockingQueue<String> fileQueue = new PriorityBlockingQueue();
		BlockingQueue<String> processQueue = new PriorityBlockingQueue();
		BlockingQueue<Integer> occurences = new PriorityBlockingQueue();

		IOThread io = new IOThread(fileQueue, occurences);
		FileReadThread read = new FileReadThread(fileQueue, processQueue);
		ProcessThread proc = new ProcessThread(processQueue, occurences);

		new Thread(io).start();
		new Thread(read).start();
		new Thread(proc).start();

	}

	/*
	 * User I/O thread: this thread is responsible for requesting commands from
	 * the user and acting on those commands.
	 */

	protected static class IOThread implements Runnable {

		private BlockingQueue<String> fileQueue;
		private BlockingQueue<Integer> occurences;
		private Scanner input = new Scanner(System.in);
		private Integer lock = 0;
		private String userInput;
		private String[] split = new String[2];
		private volatile boolean done = false;

		public IOThread(BlockingQueue<String> q, BlockingQueue<Integer> qp) {
			fileQueue = q;
			occurences = qp;
		}

		public void terminate() {
			done = true;
			Thread.currentThread().interrupt();
		}

		@Override
		public void run() {

			System.out.println("\n Multithreaded Program");
			System.out.println("\n There are 3 Commands available:\n");
			System.out.println("\tread filename.txt");
			System.out.println("\tcounts");
			System.out.println("\texit");
			while (!done) {
				System.out.println("\n Enter a Command: \n\n");
				userInput = input.nextLine();
				split = userInput.split(" ");
				split[0].toLowerCase();
				switch (split[0]) {
				case "read":
					try {
						fileQueue.put(split[1]);
						split = null;
					} catch (InterruptedException e) {
						if (done) {
							break;
						}
					}
					break;
				case "counts":
					synchronized (fileQueue) {
						fileQueue.notify();
					}
					System.out.println("\n " + fileName + " printing: ");
					try {
						System.out.println(" The number Upper Case letters found was " + occurences.take());
						System.out.println(" The number of Digits found was " + occurences.take());
						System.out.println(" The number of Lower Case letters found was " + occurences.take() + "\n");

						synchronized (lock) {
							if (!fileQueue.isEmpty()) {
								lock.wait();
							}
						}
					} catch (InterruptedException e) {
						System.out.println("IOthread");
						if (done) {
							break;
						}
					}
					break;
				case "exit":
					System.exit(0);
					break;
				}
			}
		}
	}

	/*
	 * File read thread: given some command from the user, this thread will read
	 * the content of a file and store lines of text from the file in a queue.
	 */
	protected static class FileReadThread implements Runnable {

		private BlockingQueue<String> fileQueue;
		private BlockingQueue<String> processQueue;
		private File file;
		private Integer lock = 0;
		private volatile static boolean done = false;

		public FileReadThread(BlockingQueue<String> q, BlockingQueue<String> pq) {
			fileQueue = q;
			processQueue = pq;
		}

		public static void terminate() {

			Thread.currentThread().interrupt();
		}

		@Override
		public void run() {
			while (!done) {
				synchronized (fileQueue) {
					while (fileQueue.isEmpty()) {
						try {
							fileQueue.wait();
						} catch (InterruptedException e) {
							System.out.println("FileReadthread");
							if (done) {
								System.out.println("FileReadthread catch");
								break;
							}
						}
					}
				}

				try {
					fileName = fileQueue.peek();
					file = new File(fileQueue.take());
					Scanner read = new Scanner(file);
					while (read.hasNextLine()) {
						processQueue.put(read.nextLine());
					}
					synchronized (processQueue) {
						processQueue.notify();
					}
				} catch (InterruptedException e) {
					System.out.println("FileReadthread 2");
					if (done) {
						System.out.println("FileReadthread 2 catch");
						break;
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					System.out.println("File not found!");
					/*
					 * synchronized(lock) { lock.notify(); }
					 */

				}
			}
		}
	}

	/*
	 * Processing thread: this thread will constantly check the queue populated
	 * by the file read thread and remove a line to process it by counting the
	 * number of occurrences of lowercase letters, uppercase letters, and digits
	 * 0-9 storing these in an array.
	 */
	static class ProcessThread implements Runnable {

		private BlockingQueue<String> processQueue;
		private BlockingQueue<Integer> occurences;
		private char[] line;
		private int[] occur = new int[3];
		private Integer lock = 0;
		private volatile static boolean done = false;

		public ProcessThread(BlockingQueue<String> q, BlockingQueue<Integer> qp) {

			processQueue = q;
			occurences = qp;
		}

		protected void count(BlockingQueue<String> q) {

			while (!q.isEmpty()) {
				try {
					line = q.take().toCharArray();
				} catch (InterruptedException e) {
					if (done) {
						break;
					}
				}
				for (int i = 0; i < line.length; i++) {

					if (Character.isUpperCase(line[i])) {
						occur[0]++;
					} else if (Character.isLowerCase(line[i])) {
						occur[1]++;
					} else if (Character.isDigit(line[i])) {
						occur[2]++;
					}
				}
			}
		}

		public static void terminate() {
			done = true;
			Thread.currentThread().interrupt();
		}

		@Override
		public void run() {

			while (!done) {
				if (done) {
					break;
				}
				try {
					synchronized (processQueue) {
						processQueue.wait();
					}
					count(processQueue);
					occurences.put(occur[0]);
					occurences.put(occur[1]);
					occurences.put(occur[2]);
					occur = new int[] { 0, 0, 0 };
					synchronized (lock) {
						lock.notify();
					}
				} catch (InterruptedException e) {
					if (done) {
						System.out.println("Processthread 2 catch");
						break;
					}
				}
			}
		}
	}
}
