package cs431_project_4;

/**
 * CS 431 - Operating Systems - Nicholas Pantic
 * Project 4 - File System Simulation
 * Ismail Abbas
 */

import java.util.*;
import java.lang.*;

public class FileSystem {

	private long bitmap = 0;
	private static final int size = 64;
	private int[] iNodeBlockPointer = new int[size];
	private List<iNode> inodes = new ArrayList<>();

	/*
	 * This subclass represents the iNode entity used in the programp
	 */
	@SuppressWarnings("unused")
	// No need for get() or set() in a one class program
	private class iNode {
		protected String name;
		protected int startIndex;
		protected int totalBlocks;

		public iNode(String n, int fileStart, int totalBlocks) {
			name = n;
			startIndex = fileStart;
			this.totalBlocks = totalBlocks;
		}

		@Override
		public boolean equals(Object that) {
			if (that == this) {
				return true;
			}
			if (!(that instanceof iNode)) {
				return false;
			}
			iNode thatInode = (iNode) that;
			return name.equals(thatInode.name);
		}
	}

	/*
	 * The simplest implentation of a main method anyone has ever seen
	 */
	public static void main(String[] args) {
		System.out.println("Welcome to File Allocation Table Simulation");
		System.out.println("Type \"exit\" to exit at any time");
		new FileSystem();
	}

	/*
	 * The method that encapsulates the FileSystem simulation. This could be
	 * done with if/else if's, but it would be slightly annoying to implement
	 * correctly
	 */
	@SuppressWarnings("resource")
	public FileSystem() {
		String input = "command";
		Scanner kb = new Scanner(System.in);
		while (!input.equalsIgnoreCase("exit")) {
			// FINALLY figured out how to get the ">" to appear in every line
			System.out.print("> ");
			input = kb.nextLine();
			String[] userCommand = input.split(" ");
			String[] secondArgArr = input.split(" ");

			// Using .ignoreCase() would allow for invalid case input,
			// .toLowerCase is more appropriate
			switch (userCommand[0]) {
			case "put":
				// String[] putArgs = userCommand[1].split(",");
				try {
					// put(putArgs[0], Integer.parseInt(putArgs[1]));
					put(secondArgArr[1], Integer.parseInt(secondArgArr[2]));
				} catch (IllegalArgumentException d) {
					System.out.println(d.getMessage());
				}
				break;
			case "del":
				try {
					del(userCommand[1]);
				} catch (IllegalArgumentException d) {
					System.out.println(d.getMessage());
				}
				break;
			case "bitmap":
				bitmap();
				break;
			case "inodes":
				iNodes();
				break;
			case "exit":
				System.out.println("The program is exiting, peace!");
				break;
			default:
				System.out.println("Command not recognized, please attempt again.");
				break;
			}
		}
	}

	/*
	 * This command will attempt to put the file with the given name and size
	 * (in blocks) in to the file system. To do this, you must add an i-node and
	 * fill in the pointers for each block then update the bitmap. If there is
	 * already an i-node with this file name or if there are not enough
	 * available blocks, do not allow the operation. When filling in the blocks,
	 * search sequentially from the start of the file system and fill in as you
	 * find empty blocks.
	 */
	private void put(String fName, int fSize) throws IllegalArgumentException {
		for (int i = 0; i < inodes.size(); i++) {
			if (fName.equals(inodes.get(i).name)) {
				throw new IllegalArgumentException("File already exists.");
			}
		}
		ArrayList<Integer> fBlocks = new ArrayList<>();
		for (int i = 0; fBlocks.size() < fSize && i < size; i++) {
			if (isBlockFree(i)) {
				fBlocks.add(i);
			}
		}
		if (fBlocks.size() == fSize) {
			allocate(fName, fBlocks, fSize);
		} else {
			throw new IllegalArgumentException(fSize + " block(s) not found.  " + fBlocks.size() + "/64 blocks free.");
		}
	}

	/*
	 * This command should delete the file with the given name. To delete the
	 * file, remove the i-node and clear the bitmap for the appropriate blocks.
	 * It is not necessary to clear the FAT because the bitmap will indicate
	 * that the blocks are available and no i-nodes point to those blocks any
	 * more.
	 * 
	 */
	private void del(String fName) throws IllegalArgumentException {
		int nextLink;
		iNode deletedINode;
		int index = inodes.indexOf(new iNode(fName, 0, 0));
		// -1 indicates that you've reached the end of the linked list
		if (index == -1) {
			throw new IllegalArgumentException("File not found.");
		} else {
			deletedINode = inodes.remove(index);
			nextLink = deletedINode.startIndex;
			do {
				setFree(nextLink);
				nextLink = iNodeBlockPointer[nextLink];
			} while (nextLink != -1);
		}
	}

	/*
	 * This command should print the bitmap as an 8×8 square of bits with each
	 * line labeled by the starting block number
	 */
	private void bitmap() {
		String binBitmap = String.format("%64s", Long.toBinaryString(bitmap)).replace(' ', '0');
		for (int i = 0; i < size; i += 8) {
			System.out.printf("%2d ", i);
			for (int j = i; j < i + 8; j++) {
				System.out.print(binBitmap.charAt(size - 1 - j));
			}
			System.out.println();
		}
	}

	/*
	 * This command should print all of the i-nodes and also the linked list of
	 * pointers from the FAT for each one. For example, if our i-node list has
	 * files test1, test2, and test3, the output might look like given the same
	 * allocations in the bitmap above where test1 is using the sequential
	 * blocks in the second row, test2 is using the sequential blocks in the
	 * fifth row, and test3 is allocated in several places across the file
	 * system.
	 */
	private void iNodes() {
		for (iNode d : inodes) {
			int nextLink = d.startIndex;
			System.out.print(d.name + ": ");
			do {
				System.out.print(nextLink);
				nextLink = iNodeBlockPointer[nextLink];
				if (nextLink != -1) {
					System.out.print(" -> ");
				}
			} while (nextLink != -1);
			System.out.println();
		}
		if (inodes.isEmpty()) {
			System.out.println("No files in system.");
		}
	}

	/*
	 * Auxiliary method to set an block to be allocated given a passed in block
	 * index
	 */
	private void setAllocated(int block) {
		bitmap |= 1L << block;
	}

	/*
	 * Auxiliary method to set a block free given a passed in block index
	 */
	private void setFree(int block) {
		bitmap &= ~(1L << block);
	}

	/*
	 * Auxiliary method to allocate free blocks with respect to a file and it's
	 * size
	 */
	private void allocate(String filename, List<Integer> fBlocks, int fSize) {
		inodes.add(new iNode(filename, fBlocks.get(0), fSize));
		for (int i = 0; i < fSize; i++) {
			setAllocated(fBlocks.get(i));
			if (i == fSize - 1) {
				iNodeBlockPointer[fBlocks.get(i)] = -1;
			} else {
				iNodeBlockPointer[fBlocks.get(i)] = fBlocks.get(i + 1);
			}
		}
	}

	/*
	 * Auxiliary method to free a certain block in memory based on what's passed
	 * in
	 */
	private boolean isBlockFree(int block) {
		return ((bitmap >> block) & 1) == 0;
	}
}
