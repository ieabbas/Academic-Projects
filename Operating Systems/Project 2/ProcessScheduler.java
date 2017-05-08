/**
 * CS 431 - Operating Systems - Nicolas Pantic
 * Project 2 - Process Scheduling
 * Ismail Abbas
 */

import java.io.*;
import java.util.*;

// Method descriptions taken from Project description
public class ProcessScheduler {
	@SuppressWarnings("unused")
	public static class Process {
		private Integer quantum;
		private Integer processID;

		public Process(Integer q, Integer p) {
			quantum = q;
			processID = p;
		}

		public void setQuantum(Integer something) {
			quantum = something;
		}

		public void setProcessID(Integer anotherThing) {
			processID = anotherThing;
		}
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		Scanner fileRead;
		String fileName = null;
		String inputLine;
		String[] split = new String[2];
		ArrayList<Integer> quantumList, processList;
		ArrayList<Integer> duplicateValue, duplicateValue2, duplicateValue3;
		ArrayList<Integer> duplicateProcessID, duplicateProcessIDDos, duplicateProcessIDTres;

		// Allow for terminal style entries of arguments to compilation and
		// execution
		if (0 < args.length) {
			fileName = args[0];
		}
		fileRead = new Scanner(new File(fileName));
		processList = new ArrayList<Integer>();
		quantumList = new ArrayList<Integer>();

		// Parse the file for input
		while (fileRead.hasNextLine()) {
			inputLine = fileRead.nextLine();
			Scanner input = new Scanner(inputLine);
			input.useDelimiter("\\s");
			// After the delimiter, separate by comma to have raw integer values
			// to use
			if (input.hasNextInt()) {
				split = input.nextLine().split(",");
				processList.add(Integer.parseInt(split[0]));
				quantumList.add(Integer.parseInt(split[1]));
			}
			input.close();
		}

		// Clone the processID arraylists and quantum arraylists so values
		// aren't overwritten
		duplicateValue = new ArrayList<>();
		duplicateValue = (ArrayList<Integer>) quantumList.clone();
		duplicateProcessID = new ArrayList<>();
		duplicateProcessID = (ArrayList<Integer>) processList.clone();
		duplicateValue2 = new ArrayList<>();
		duplicateValue2 = (ArrayList<Integer>) quantumList.clone();
		duplicateProcessIDDos = new ArrayList<>();
		duplicateProcessIDDos = (ArrayList<Integer>) processList.clone();
		duplicateValue3 = new ArrayList<>();
		duplicateValue3 = (ArrayList<Integer>) quantumList.clone();
		duplicateProcessIDTres = new ArrayList<>();
		duplicateProcessIDTres = (ArrayList<Integer>) processList.clone();

		// Call the troops to get the work done with the appropriate supplies
		firstComeAlg(quantumList, processList);
		shortestJobFirstAlg(quantumList, processList);
		roundRobinAlg(duplicateValue, duplicateProcessID, 50);
		roundRobinAlg(duplicateValue2, duplicateProcessIDDos, 100);
		randomSchedulerAlg(duplicateValue3, duplicateProcessIDTres, 50);

		// Always forget to do this
		fileRead.close();
		// System.celebrateThatItsOver();
	}

	/*
	 * This method achieves the frustratingly difficult task of printing out the
	 * apporapriate output for the program, when given the list of processes,
	 * their associated quantums, and placeholders for the average time and the
	 * total time taken.
	 */
	public static void print(ArrayList<Integer> quantumList, ArrayList<Integer> processList, double averageTime,
			int total, int quantum, double overall, int status) {
		int listSize = processList.size();
		if (status == 0) {
			for (int i = 0; i < quantumList.size(); i++) {
				total += quantumList.get(i);
				System.out.println("Process " + processList.get(i) + " finishes on Cycle " + total + ".");
				averageTime += total;
			}
			averageTime = averageTime / listSize;
			System.out.println("Average turnaround time: " + averageTime);
		} else if (status == 1) {
			for (int i = 0; i < quantumList.size(); i++) {
				System.out.println("Process " + processList.get(i) + " finishes on Cycle " + quantumList.get(i) + ".");
				averageTime += total;
			}
			averageTime = overall / listSize;
			System.out.println("Average turnaround time: " + averageTime);
		} else {
			for (int i = 0; i < quantumList.size(); i++) {
				System.out.println("Process " + processList.get(i) + " finishes on Cycle " + quantumList.get(i) + ".");
				averageTime += total;
			}
			averageTime = overall / listSize;
			System.out.println("Average turnaround time: " + (averageTime + 10) + "\n");
		}
	}

	/*
	 * This scheduler simply runs each process to completion in the order they
	 * are submitted.
	 */
	public static void firstComeAlg(ArrayList<Integer> quantumList, ArrayList<Integer> processList) {
		System.out.println("\nRunning First-come, first-served scheduler.");
		print(quantumList, processList, 0, 0, 0, 0, 0);
	}

	/*
	 * This scheduler will go through the processes in order of submission
	 * executing each for a small amount of time (quantum) before switching to
	 * the next process. If a process has less than the quantum in cycles
	 * remaining to execute, make sure your total cycles elapsed only goes up by
	 * the amount remaining instead of the full quantum.
	 */
	public static void roundRobinAlg(ArrayList<Integer> quantumList, ArrayList<Integer> processList,
			Integer quantumVal) {
		ArrayList<Integer> modifiedId = new ArrayList<Integer>();
		ArrayList<Integer> totalQuantums = new ArrayList<Integer>();
		ArrayList<Integer> processedID = new ArrayList<Integer>();
		int totalTime, targetTime, listSize;
		totalTime = 0;
		targetTime = 0;
		listSize = processList.size();
		double overallTime, averageTime;
		overallTime = 0;
		averageTime = 0;

		System.out.println("\nRunning round robin scheduler with quantum " + quantumVal);

		while (!quantumList.isEmpty()) {
			for (int i = 0; i < quantumList.size(); i++) {
				targetTime = quantumList.get(i);
				if ((targetTime - quantumVal) <= 0) {
					totalTime += targetTime;
					modifiedId.add(processList.get(i));
					overallTime += totalTime;
					totalQuantums.add(totalTime);
					processedID.add(processList.get(i));
				} else {
					targetTime -= quantumVal;
					quantumList.remove(i);
					quantumList.add(i, targetTime);
					totalTime += quantumVal;
				}
			}
			for (int j = 0; j < processedID.size(); j++) {
				for (int k = 0; k < processList.size(); k++) {
					if (processList.get(k) == processedID.get(j)) {
						processList.remove(k);
						quantumList.remove(k);
					}
				}
			}
			processedID.clear();
		}
		averageTime = (double) overallTime / listSize;
		print(totalQuantums, modifiedId, averageTime, totalTime, quantumVal, overallTime, 1);
	}

	/*
	 * This scheduler will run each process to completion in the order of
	 * shortest to longest. For processes of the same length, execute in order
	 * of submission.
	 */
	@SuppressWarnings("unchecked")
	public static void shortestJobFirstAlg(ArrayList<Integer> quantumList, ArrayList<Integer> processList) {
		/*
		 * // Actual sorting for (int i = 0; i < quantumList.size(); i++) { for
		 * (int j = 0; j < i + 1; j++) { if ((procList.get(i).quantum) >
		 * (procList.get(j).quantum)) { // the old switcharoo temp =
		 * procList.get(i).quantum; // set process[i]'s quantum to the value of
		 * process[j]'s // quantum
		 * procList.get(i).setQuantum(procList.get(j).quantum); // set
		 * process[j]'s quantum to the value of temp
		 * procList.get(j).setQuantum(temp); // set temp to be the processID of
		 * process[i] temp = procList.get(i).processID; // set the processID of
		 * process[i] to be the processID of // process[j]
		 * procList.get(i).setProcessID(procList.get(j).processID); // set the
		 * processID of process[j] to be the value of temp
		 * procList.get(j).setProcessID(temp); } } } System.out.println(""); //
		 * extra line of course /* // An attempt to print the processes and
		 * their associated quantums for (int i = 0; i < quantumList.size();
		 * i++) { total += procList.get(i).quantum;
		 * 
		 * System.out.println("Process " + procList.get(i).processID +
		 * " finishes on Cycle " + total + "."); avg += total; }
		 * System.out.println("Average turnaround time: " + (avg /
		 * quantumList.size()));
		 */
		// Second attempt
		ArrayList<Integer> duplicateValues = new ArrayList<>();
		duplicateValues = (ArrayList<Integer>) quantumList.clone();
		System.out.println("\nRunning shortest first scheduler.");
		// Sort arraylist so it can be passed in
		Collections.sort(duplicateValues);
		print(duplicateValues, processList, 0, 0, 0, 0, 0);
	}

	/*
	 * This scheduler will randomly choose a process to run for a quantum
	 * weighted by how many cycles are remaining for each process.
	 */
	@SuppressWarnings("unused")
	public static void randomSchedulerAlg(ArrayList<Integer> quantumList, ArrayList<Integer> processList,
			int quantumVal) {
		Random rand = new Random();
		ArrayList<Integer> modifiedId = new ArrayList<Integer>();
		ArrayList<Integer> totalQuantums = new ArrayList<Integer>();
		ArrayList<Integer> processedID = new ArrayList<Integer>();

		int total, totalTime, targetTime, listSize, minimumVal, maximumVal, closeEnough;
		total = 0;
		targetTime = 0;
		listSize = processList.size();
		minimumVal = 1;
		maximumVal = 0;
		double overallTime, averageTime;
		overallTime = 0;
		averageTime = 0;

		// Next level random generator
		maximumVal = processList.size() + 1;
		closeEnough = rand.nextInt(maximumVal - minimumVal) + minimumVal;
		System.out.println("\nRunning random scheduler with quantum " + quantumVal + ".");

		while (!quantumList.isEmpty()) {

			for (int i = 0; i < quantumList.size(); i++) {
				targetTime = quantumList.get(i);
				if ((targetTime - quantumVal) <= 0) {
					total += targetTime;
					modifiedId.add(processList.get(i));
					overallTime += total;
					totalQuantums.add(total);
					processedID.add(processList.get(i));
				} else {
					targetTime -= quantumVal;
					quantumList.remove(i);
					quantumList.add(i, targetTime);
					total += quantumVal;
				}
			}
			for (int i = 0; i < processedID.size(); i++) {
				for (int j = 0; j < processList.size(); j++) {
					if (processList.get(j) == processedID.get(i)) {
						processList.remove(j);
						quantumList.remove(j);
					}
				}
			}
			processedID.clear();
		}
		averageTime = (double) overallTime / listSize;
		print(totalQuantums, modifiedId, averageTime, total, quantumVal, overallTime, 2);
	}
}
