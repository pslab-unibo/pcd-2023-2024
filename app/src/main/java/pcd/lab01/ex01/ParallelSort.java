package pcd.lab01.ex01;

import java.util.*;

import static pcd.lab01.ex01.SequentialSort.*;

public class ParallelSort {
    private static final int numProcessors = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {
        log("Generating array...");
        long[] v = genArray(VECTOR_SIZE);

        log("Array generated.");
        log("Sorting (" + VECTOR_SIZE + " elements)...");

        long t0 = System.nanoTime();

        final List<Thread> workers = new ArrayList<>();

        for (var i = 0; i < numProcessors; i++) {
            final Pair offset = getOffsetRange(i);
            final Thread worker = new Thread(new SorterProcess(v, offset.first, offset.second));
            workers.add(worker);
            worker.start();
        }

        workers.forEach(t -> {
            try {
                t.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        });

        log("Done partition sorting, merging result...");
        sequentialPartitionMerge(v);

        long t1 = System.nanoTime();
        log("Done. Time elapsed: " + ((t1 - t0) / 1000000) + " ms");
    }

    private static Pair getOffsetRange(final int index) {
        final int end = (VECTOR_SIZE * (index + 1)) / numProcessors;
        return new Pair(
                (VECTOR_SIZE * index) / numProcessors,
                Math.min(end, VECTOR_SIZE)
        );
    }

    private static void sequentialPartitionMerge(long[] v) {
        for (int i = 0; i < numProcessors - 1; i++) {
            final int middle = getOffsetRange(i + 1).first;
            final int high = getOffsetRange(i + 1).second - 1;

            merge(v, 0, middle, high);
        }
    }

    /**
     * Merge step taken from the C++ implementation of Merge-Sort found in
     * "The Algorithm Design Manual" by Steven S. Skiena.
     *
     * Note: this version can't pass the test with 200_000_000 elements due to
     * heap size limits (even if -Xmx jvm argument) on my machine.
     */
    private static void merge(long[] v, final int low, final int middle, final int high) {
        int i = 0;
        final Queue<Long> left = new LinkedList<>();
        final Queue<Long> right = new LinkedList<>();

        for (i = low; i < middle; i++) left.offer(v[i]);
        for (i = middle; i <= high; i++) right.offer(v[i]);

        i = low;
        while (!(left.isEmpty() || right.isEmpty())) {
            if (left.peek() <= right.peek()) {
                v[i++] = left.poll();
            } else {
                v[i++] = right.poll();
            }
        }

        while (!left.isEmpty()) {
            v[i++] = left.poll();
        }

        while (!right.isEmpty()) {
            v[i++] = right.poll();
        }
    }

    private record Pair(int first, int second) {}
}
