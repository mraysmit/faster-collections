package dev.mars;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AsynchronousExecutor<T, E> {

    private static final Integer MINUTES_WAITING_THREADS = 1;
    private Integer numThreads;
    private ExecutorService executor;
    private List<E> outputList;

    public AsynchronousExecutor() {
        this.numThreads = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(this.numThreads);
        this.outputList = new ArrayList<>();
    }

    public AsynchronousExecutor(int threads) {
        this.numThreads = threads;
        this.executor = Executors.newFixedThreadPool(this.numThreads);
        this.outputList = new ArrayList<>();
    }

    public void processStream(List<T> inputList, ElementTransformer<T, E> transformer) {
        this.outputList = inputList.stream()
                .map(e -> transformer.apply(e))
                .collect(Collectors.toList());
    }

    public void processParallelStream(List<T> inputList, ElementTransformer<T, E> transformer) {
        this.outputList = inputList.parallelStream()
                .map(e -> transformer.apply(e))
                .collect(Collectors.toList());
    }

    public void processSublistPartition(List<T> inputList, ElementTransformer<List<T>, List<E>> transformer) {
        var partitioner = new ListPartitioner<T>(inputList, numThreads);

        IntStream.range(0, numThreads).forEach(t -> this.executor.execute(() -> {
            var thOutput = transformer.apply(partitioner.get(t));

            if (Objects.nonNull(thOutput) && !thOutput.isEmpty()) {
                synchronized (this.outputList) {
                    this.outputList.addAll(thOutput);
                }
            }
        }));
    }

    @SuppressWarnings("unchecked")
    public void processShallowPartitionArray(List<T> inputList, ElementTransformer<T, E> transformer) {
        var chunkSize = (inputList.size() % this.numThreads == 0) ? (inputList.size() / this.numThreads) : (inputList.size() / this.numThreads) + 1;
        Object[] outputArr = new Object[inputList.size()];

        IntStream.range(0, numThreads).forEach(t -> this.executor.execute(() -> {
            var fromIndex = t * chunkSize;
            var toIndex = Math.min(fromIndex + chunkSize, inputList.size());

            if (fromIndex > toIndex) {
                fromIndex = toIndex;
            }

            IntStream.range(fromIndex, toIndex)
                    .forEach(i -> outputArr[i] = transformer.apply(inputList.get(i)));
        }));

        this.shutdown();
        this.outputList = (List<E>) Arrays.asList(outputArr);
    }

    public void processShallowPartitionList(List<T> inputList, ElementTransformer<T, E> transformer) {
        var chunkSize = (inputList.size() % this.numThreads == 0) ? (inputList.size() / this.numThreads) : (inputList.size() / this.numThreads) + 1;
        this.outputList = new ArrayList<>(Collections.nCopies(inputList.size(), null));

        IntStream.range(0, numThreads).forEach(t -> this.executor.execute(() -> {
            var fromIndex = t * chunkSize;
            var toIndex = Math.min(fromIndex + chunkSize, inputList.size());

            if (fromIndex > toIndex) {
                fromIndex = toIndex;
            }

            IntStream.range(fromIndex, toIndex).forEach(i -> this.outputList.set(i, transformer.apply(inputList.get(i))));
        }));
    }

    public void shutdown() {
        this.executor.shutdown();

        try {
            this.executor.awaitTermination(MINUTES_WAITING_THREADS, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public Integer getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(Integer numThreads) {
        this.numThreads = numThreads;
    }

    public List<E> getOutput() {
        return this.outputList;
    }
}