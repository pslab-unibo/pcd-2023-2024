package pcd.lab01.ex01;

import java.util.Arrays;

public class SorterProcess implements Runnable {
    private final long[] v;
    private final int startIndex;
    private final int endIndex;

    public SorterProcess(final long[] v, final int startIndex, final int endIndex) {
        this.v = v;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public void run() {
        Arrays.sort(this.v, this.startIndex, this.endIndex);
    }
}