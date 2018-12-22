package net.saisimon.agtms.core.concurrent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class DoSomethingByPageTask<V> extends RecursiveTask<V> {
	
	private static final long serialVersionUID = 1L;
	private static final int PAGE_SIZE = 1000;
	
	protected long start, end;
	protected int pageNumber, pageSize;
	
	public DoSomethingByPageTask(long start, long end, int pageNumber) {
		this(start, end, pageNumber, PAGE_SIZE);
	}
	
	public DoSomethingByPageTask(long start, long end, int pageNumber, int pageSize) {
		this.start = start;
		this.end = end;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
	}
	
	@Override
	protected V compute() {
		List<V> results = new ArrayList<>();
		if (end - start <= pageSize) {
			V result = doSomething();
			results.add(result);
		} else {
			List<DoSomethingByPageTask<V>> tasks = new ArrayList<>();
			long s = start;
			long e = s;
			int idx = 1;
			while (e < end) {
				e += pageSize;
				DoSomethingByPageTask<V> task = build(s, e, idx, pageSize);
				idx++;
				s = e + 1;
				if (null == task) {
					continue;
				}
				tasks.add(task);
			}
			if (log.isDebugEnabled()) {
				log.debug("Tasks Size : " + tasks.size());
			}
			Iterator<DoSomethingByPageTask<V>> it = invokeAll(tasks).iterator();
			while (it.hasNext()) {
				DoSomethingByPageTask<V> task = it.next();
				if (task.isDone()) {
					V result = task.getRawResult();
					if (result != null) {
						results.add(result);
					}
				}
			}
		}
		return joinResult(results);
	}
	
	protected abstract V doSomething();
	
	protected abstract V joinResult(List<V> results);
	
	/**
	 * Build DoSomethingByPageBaseTask, Return SubClass
	 * 
	 * @param start Count Start Number
	 * @param end Count End Number
	 * @param pageNumber Page Number
	 * @param pageSize Page Size
	 * @return SubClass
	 */
	protected abstract DoSomethingByPageTask<V> build(long start, long end, int pageNumber, int pageSize);
	
	public synchronized static void printLog(ForkJoinPool pool) {
		System.out.printf("**********************\n");
		System.out.printf("Main: Fork/Join Pool log\n");
		System.out.printf("Main: Fork/Join Pool: Parallelism:%d\n", pool.getParallelism());
		System.out.printf("Main: Fork/Join Pool: Pool Size:%d\n", pool.getPoolSize());
		System.out.printf("Main: Fork/Join Pool: Active Thread Count:%d\n", pool.getActiveThreadCount());
		System.out.printf("Main: Fork/Join Pool: Running Thread Count:%d\n", pool.getRunningThreadCount());
		System.out.printf("Main: Fork/Join Pool: Queued Submission:%d\n", pool.getQueuedSubmissionCount());
		System.out.printf("Main: Fork/Join Pool: Queued Tasks:%d\n", pool.getQueuedTaskCount());
		System.out.printf("Main: Fork/Join Pool: Queued Submissions:%s\n", pool.hasQueuedSubmissions());
		System.out.printf("Main: Fork/Join Pool: Steal Count:%d\n", pool.getStealCount());
		System.out.printf("Main: Fork/Join Pool: Terminated :%s\n", pool.isTerminated());
		System.out.printf("**********************\n");
	}
	
}
