import java.util.*;
import java.util.concurrent.Semaphore;

public class BlockingQueue<T> {

	private List<Object> queue = new LinkedList<Object>();
	@SuppressWarnings("unused")
	private int limit;
	private Semaphore openings; // semaphore for empty openings
	private Semaphore objs; // semaphore for filled openings
	private Semaphore mutex; // for the critical section

	public BlockingQueue(int hardLimit) {
		this.limit = hardLimit;
		this.openings = new Semaphore(hardLimit); // essentially the blocking
													// queue
		this.objs = new Semaphore(0);
		this.mutex = new Semaphore(1);
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Welcome to the Semaphore-based BlockingQueue\n");
		BlockingQueue<Integer> queue = new BlockingQueue<>(100);
		Runnable r = () -> { // replace lambda if you don’t have access to Java
								// 8
			for (int i = 0; i < 200; i++) {
				try {
					int n = (int) queue.dequeue();
					System.out.println(n + " removed");
					Thread.sleep(500);
				} catch (Exception e) {
				}
			}
		};
		Thread t = new Thread(r);
		t.start();
		for (int i = 0; i < 200; i++) {
			System.out.println("Adding " + i);
			queue.enqueue(i);
		}
	}

	/*
	 * This method will provide the functionality for enqueuing in a
	 * BlockingQueue. The method acquire() serves a purpose similar to the point
	 * of a BlockingQueue, as it will acquire a permit from a permit from a
	 * semaphore while blocking until one is available or the thread is
	 * interrupted. The release() method operates similarly, releasing a given
	 * number of permits and returning them to the semaphore.
	 */
	private void enqueue(Object o) throws InterruptedException {
		openings.acquire();
		mutex.acquire(); // start of critical section
		queue.add(o);
		mutex.release(); // end of critical section
		objs.release();
	}

	/*
	 * This method will provide the functionality for dequeuing in a
	 * BlockingQueue. The method acquire() serves a purpose similar to the point
	 * of a BlockingQueue, as it will acquire a permit from a permit from a
	 * semaphore while blocking until one is available or the thread is
	 * interrupted. The release() method operates similarly, releasing a given
	 * number of permits and returning them to the semaphore.
	 */
	private Object dequeue() throws InterruptedException {
		objs.acquire();
		mutex.acquire(); // start of critical section
		Object o = queue.remove(0);
		mutex.release(); // end of critical section
		openings.release();
		return o;
	}

}
