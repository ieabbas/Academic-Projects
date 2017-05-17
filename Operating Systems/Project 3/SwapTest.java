package cs431_project_3;

import java.util.*;

public class SwapTest {

	// All default values will initialize to null, to check for content check
	// for (!null)
	private static Job[] jobList = new Job[100];
	private static Segment start = new Segment(0, 0, 100, null);
	private static int activeProcesses = 0;

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		String[] split = new String[3];

		// Split the command into the action (string[0]) and the process
		// (string[1])
		String action = null;
		int process = 0;
		int processSize = 0;

		System.out.println("Welcome to Operating System Swap Testing\n");
		System.out.println("Type \"quit\" to exit at any time");

		while (input.hasNext()) {
			split = input.nextLine().split(" ");
			action = split[0];
			// If the command is "add", you need to take in two integer
			// arguments
			if (action.equals("add")) {
				process = Integer.parseInt(split[1]);
				processSize = Integer.parseInt(split[2]);
				add(process, processSize);
			} else if (action.equals("jobs")) {
				jobs();
			} else if (action.equals("list")) {
				System.out.println("List was chosen as an action");
				list();
			} else if (action.equals("ff")) {
				process = Integer.parseInt(split[1]);
				// If the ff allocation returns true, perform the alg for real,
				// otherwise tell people you failed at life
				if (findJob(process).allocated == true) {
					System.out.println("Job already allocated, attempt unappreciated.");
				} else if (firstFit(process) == true) {
					System.out.print("You've successfully run the firstFit method.\n");
				} else {
					System.out.println("First fit allocation failed, try again.");
				}
			} else if (action.equals("nf")) {
				process = Integer.parseInt(split[1]);
				// If the ff allocation returns true, perform the alg for real,
				// otherwise tell people you failed at life
				if (findJob(process).allocated == true) {
					System.out.println("Job already allocated, attempt unappreciated.");
				} else if (nextFit(process) == true) {
					System.out.print("You've successfully run the Next Fit method.");
				} else {
					System.out.println("Next fit allocation failed, try again.");
				}
			} else if (action.equals("bf")) {
				process = Integer.parseInt(split[1]);
				// If the bf allocation returns true, perform the alg for real,
				// otherwise tell people you failed at life
				if (bestFit(process)) {
					bestFit(process);
				} else {
					System.out.println("Best fit allocation failed, try again.");
				}
			} else if (action.equals("wf")) {
				process = Integer.parseInt(split[1]);
				// If the wf allocation returns true, perform the alg for real,
				// otherwise tell people you failed at life
				if (worstFit(process)) {
					worstFit(process);
				} else {
					System.out.println("Worst fit allocation failed, try again.");
				}
			} else if (action.equals("find")) {
				process = Integer.parseInt(split[1]);
				findJob(process);
			} else if (action.equals("quit")) {
				System.exit(0);
			} else {
				System.out.println("Invalid command, try again.");
			}
		}
		input.close();

		// Things to do :
		// firstFit(int pid);
		// nextFit(int pid);
		// bestFit(int pid);
		// worstFit(int pid);
		// jobs();
		// list();
		// deallocateSegment(int pid);
	}

	/*
	 * This method will add a job to the jobList array, so long as the current
	 * position is not null
	 */
	public static void add(int pid, int size) {
		for (int i = 0; i < jobList.length; i++) {
			if (jobList[i] == null) {
				// System.out.println("A null value was found 2 spots down.");
				jobList[i] = new Job(pid, size);
				// Necessary so that all the elements of the list aren't
				// overwritten
				break;
			}
		}
	}

	/*
	 * This method will print the list of jobs that are in the list, and may or
	 * may not be loaded into memory
	 */
	public static void jobs() {
		for (int i = 0; i < jobList.length; i++) {
			if (jobList[i] != null) {
				System.out.print(jobList[i].toString() + " ");
				// System.out.print("[" + jobList[i].getPid() + " " +
				// jobList[i].getSize() + "] ");
			} else {
				// System.out.print(" [" + jobList[i].getPid() + " " +
				// jobList[i].getSize() + "} ");
			}

		}
	}

	/*
	 * This method will print the linked list of segments, with appropriate
	 * allocations
	 */
	public static void list() {
		// CURRENTLY BUGGED, SOLVE THIS FIRST TO CONTINUE TO OTHER FIT
		// ALGORITHMS
		Segment currentSegment = start;
		System.out.println("Inside the list method");
		while (true) {
			System.out.println("Inside the list method's loop");
			System.out.println(currentSegment.toString());
			currentSegment = currentSegment.getNext();
			if (activeProcesses == 0) {
				System.out.println("There are no jobs allocated within memory");
				break;
			}
		}
	}

	/*
	 * This method will perform the first fit allocation algorithm for a job
	 * within the linked list of segments.
	 */
	@SuppressWarnings("unused")
	public static boolean firstFit(int pid) {
		Job currentJob = findJob(pid);
		currentJob.allocated = true;
		int jobSize = currentJob.getSize();
		Segment currentSegment = start;
		Segment newSegment;
		// While the segment is a hole and it's length is bigger than the
		// size of currentJob, place the job there
		for (int i = 0; jobList[i] != null; i++) {
			if (currentSegment.getPid() == 0) {
				activeProcesses++;
				// If there isn't a job/segment already allocated after
				if (currentSegment.getNext() == null) {
					// Work around the setLegth() method returning void, a new
					// object can't be instantiated with setLegnth() as a
					// parameter
					newSegment = new Segment(activeProcesses, currentSegment.getStart() + currentJob.getSize(), 0,
							null);
					newSegment.setLength(currentJob.getSize());
					currentSegment.setLength(currentSegment.length - currentJob.getSize());
					System.out.println(newSegment.getLength() + " is the length of the current segment");
					currentSegment.setNext(newSegment);
					start.setLength(start.getLength() - currentJob.getSize());
					System.out.println(currentSegment.getLength() + " is the length of the start segment");
					return true;
				}
				// If there is a segment/hole after, first fit becomes a linked
				// list type of addition problem
				else {
					System.out.println("Else clause of first fit encountered");
				}
			} else {
				// Iterate to the next segment of the linked list
				currentSegment = currentSegment.getNext();
			}
		}
		// Return false if nothing else goes right return false, meaning first
		// fit couldn't find a hole of sufficient size for the job
		return false;
	}

	/*
	 * This method will perform the next fit allocation algorithm for a job
	 * within the linked list of segments.
	 */
	public static boolean nextFit(int pid) {
		Job currentJob = findJob(pid);
		currentJob.allocated = true;
		int jobSize = currentJob.getSize();
		Segment currentSegment = start;
		Segment newSegment;
		// While the segment is a hole and it's length is bigger than the
		// size
		// of currentJob, place the job there
		for (int i = 0; start.getNext() == null; i++) {
			if (currentSegment.getPid() == 0) {
				activeProcesses++;
				// Work around the setLegth() method returning void, a new
				// object can't be instantiated with setLegnth() as a
				// parameter
				newSegment = new Segment(activeProcesses, currentSegment.getStart() + currentJob.getSize(), 0, null);
				newSegment.setLength(currentJob.getSize());
				currentSegment.setLength(currentSegment.length - currentJob.getSize());
				return true;
			}
			// Iterate to the next segment of the linked list
			currentSegment = currentSegment.getNext();
		}
		return false;
	}

	/*
	 * This method will perform the first fit allocation algorithm for a job
	 * within the linked list of segments.
	 */
	public static boolean bestFit(int pid) {

		return false;
	}

	/*
	 * This method will perform the first fit allocation algorithm for a job
	 * within the linked list of segments.
	 */
	public static boolean worstFit(int pid) {

		return false;
	}

	/*
	 * This method will deallocate a segment from the linked list of segments,
	 * given the specific pid of the job. There are 4 cases for removal of a
	 * segment. (A), (B), (C), (D).
	 */
	public static void deallocateSegment(int pid) {

	}

	/*
	 * This auxiliary method will perform the task of finding and returning a
	 * job within the jobList array based off the pid provided
	 */
	public static Job findJob(int pid) {
		for (int i = 0; i < jobList.length; i++) {
			if (jobList[i].getPid() == pid) {
				// System.out.println(jobList[i].toString());
				return jobList[i];
			}
		}
		// This should never happen, needs validation improvement (not here,
		// probably in each fit algorithm)
		return null;
	}

	/*
	 * This class is a variant of the traditional Linked List, provided by the
	 * assignment prompt
	 */
	public static final class Segment {

		private int pid;
		private int start;
		private int length;
		private Segment next;

		public Segment(int pid, int start, int length, Segment next) {
			this.pid = pid;
			this.start = start;
			this.length = length;
			this.next = next;
		}

		public int getPid() {
			return pid;
		}

		public int getStart() {
			return start;
		}

		public int getLength() {
			return length;
		}

		public Segment getNext() {
			return next;
		}

		public void setPid(int pid) {
			this.pid = pid;
		}

		public void setStart(int start) {
			this.start = start;
		}

		public void setLength(int length) {
			this.length = length;
		}

		public void setNext(Segment next) {
			this.next = next;
		}

		@Override
		public String toString() {
			return String.format("(%d %d %d)", pid, start, length);
		}
	}

	/*
	 * This class will represent the job objects
	 */
	public static final class Job {

		private boolean allocated;
		private final int pid;
		private final int size;

		public Job(int pid, int size) {
			allocated = false;
			this.pid = pid;
			this.size = size;
		}

		public int getPid() {
			return pid;
		}

		public int getSize() {
			return size;
		}

		@Override
		public String toString() {
			return String.format("[%d %d]", pid, size);
		}
	}

}
