
import java.util.*;

public class SwapTest {

	// All default values will initialize to null, to check for content check
	// for (!null)
	private static Job[] jobList = new Job[100];
	private static Segment start = new Segment(0, 0, 100, null);
	@SuppressWarnings("unused")
	private static Segment newStart;
	private static int activeProcesses = 0;
	@SuppressWarnings("unused")
	private static int numOfSegments = 0;

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
			} else if (action.equals("de")) {
				process = Integer.parseInt(split[1]);
				// dealllocate will handle all of the validation
				deallocateSegment(process);
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
		// nextFit(int pid);
		// bestFit(int pid);
		// worstFit(int pid);
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
		// Segment currentSegment = start;
		// System.out.print(currentSegment.toString() + " ");
		// currentSegment = currentSegment.getNext();
		// System.out.print(currentSegment.toString());
		// currentSegment = currentSegment.getNext();
		// System.out.print(currentSegment.toString());
		// currentSegment = currentSegment.getNext();
		// System.out.print(currentSegment.toString());

		while (start.getNext() != null) {
			System.out.print(start.toString() + " ");
			start = start.getNext();
			if (start.getPid() == 0) {
				System.out.print(start.toString());
			}
		}
	}

	/*
	 * This method will perform the first fit allocation algorithm for a job
	 * within the linked list of segments.
	 */
	@SuppressWarnings("unused")
	public static boolean firstFit(int pid) {
		numOfSegments++;
		Job currentJob = findJob(pid);
		currentJob.allocated = true;
		int jobSize = currentJob.getSize();
		Segment currentSegment;
		Segment prevSegment;
		Segment temp;

		// If the hole is 100, then it's the first thing to be allocated
		if (start.getLength() == 100) {
			activeProcesses++;
			Segment newSegment = new Segment(activeProcesses, 0, currentJob.getSize(), start);
			start.setStart((currentJob.size + 1));
			start.setLength(100 - currentJob.size);
			start = newSegment;
			return true;
		} // If the hole isn't 100, have a previous segment keep track of the
			// hole so that it can add to the right
		else {
			temp = start;
			prevSegment = start;
			currentSegment = prevSegment.getNext();
			// If there isn't enough space left then tell the user
			if (prevSegment.getLength() < currentJob.getSize()) {
				System.out.println("Insufficient space for job, attempt unappreciated");
				return false;
			} else {
				// While the prevSegment and the currentSegment's next segment
				// aren't null, iterate until you hit the end of the list
				while (prevSegment.getNext() != null && currentSegment.getNext() != null) {
					prevSegment = prevSegment.getNext();
					currentSegment = prevSegment.getNext();
				}
				// By this point the currentSegment should be the hole, so it's
				// time to insert a new segment (basically next fit)
				if (currentSegment.getPid() == 0) {
					// Check to see if the currentSement has made it to the
					// final segment, or the hole if all goes right
					// System.out.println("Current segment is the final segment,
					// the hole.");
					// System.out.println(currentSegment.toString());
					activeProcesses++;
					// New segment created in isolation with correct size and
					// only the next segment missing
					Segment newSegment = new Segment(activeProcesses, prevSegment.getLength() + 1, currentJob.getSize(),
							null);
					// The start of the hole equals te new segment's starting
					// position + the length
					currentSegment.setStart(newSegment.getStart() + newSegment.getLength() + 1);
					// The length of the hole equals the old length minus the
					// current job's size
					currentSegment.setLength(currentSegment.getLength() - currentJob.getSize());
					// The next segment must be null for the hole
					currentSegment.setNext(null);
					// The pid for the hole needs to be 0
					currentSegment.setPid(0);
					// Switch the pointers
					prevSegment.setNext(newSegment);
					newSegment.setNext(currentSegment);
					return true;
				} else {
					System.out.println("Jesus what happened, the current segment is supposed to be the hole now");
				}
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
	@SuppressWarnings("unused")
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
	public static boolean worstFit(int pid) {
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
	 * This method will deallocate a segment from the linked list of segments,
	 * given the specific pid of the job. There are 4 cases for removal of a
	 * segment. (A), (B), (C), (D).
	 */
	public static void deallocateSegment(int pid) {
		Segment currentSegment = start;
		Boolean existsOrNot = false;
		Boolean emptyList = true;
		int i = 0;
		// Check if any job in the jobList array has the matching pid
		while (jobList[i] != null) {
			if (jobList[i].getPid() == pid) {
				existsOrNot = true;
			}
			i++;
		}

		// If the job doesn't exist at all...our user is interesting
		if (existsOrNot == false) {
			System.out.println("The job doesn't exist");
		}
		// If the pid isn't allocated, tell the user they're stupid
		else if (findJob(pid).allocated == false) {
			System.out.println("Job already doesn't exist in memory, attempt unappreciated");
		}
		// If the only segment is the start segment, then tell the user they're
		// stupid
		else if (start.getPid() == 0) {
			System.out.println("The only segment in memory is the start segment, attempt unappreciated");
		}
		// If the next segment is the hole, then just make start a brand new
		// segment once more
		else if (currentSegment.getNext() != null && currentSegment.getNext().getPid() == 0) {
			start = new Segment(0, 0, 100, null);
		} else {
			System.out.println("The job exists, but I can't deallocate yet");
		}
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
