import trees.*;
import java.io.*;
import java.util.*;

import utils.FastReader;
import utils.BalancedTree;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private interface Solver {
        String getResult();
    }

    private static final class LogItem {
        private final int id;
        private final String expression;
        private final Exception exception;

        private LogItem(int id, String expression, Exception exception) {
            this.id = id;
            this.expression = expression;
            this.exception = exception;
        }

        private void print() {
            System.err.println(expression);
            if (exception != null) {
                exception.printStackTrace();
            }
        }
    }

    private static final class P3374Solver implements Solver {
        private final String result;

        private P3374Solver(InputStream inputStream) {
            FastReader reader = new FastReader(inputStream);
            int size = reader.readInt();
            int total = reader.readInt();
            BITree<Integer> biTree = new BITree<>(size, 0, Integer::sum);
            StringBuilder builder = new StringBuilder();
            IntStream.rangeClosed(1, size).forEach(i -> biTree.update(i, reader.readInt()));
            IntStream.range(0, total).forEach(i -> {
                switch (reader.readInt()) {
                    case 1:
                        biTree.update(reader.readInt(), reader.readInt());
                        break;
                    case 2:
                        int sub = biTree.query(reader.readInt() - 1);
                        int src = biTree.query(reader.readInt());
                        builder.append(src - sub).append('\n');
                        break;
                    default:
                        break;
                }
            });
            result = builder.toString();
        }

        @Override
        public String getResult() {
            return result;
        }
    }

    private static final class P1908Solver implements Solver {
        private long ans;

        private P1908Solver(InputStream inputStream) {
            FastReader reader = new FastReader(inputStream);
            int size = reader.readInt();
            List<Integer> array = IntStream.range(0, size).map(i -> reader.readInt())
                    .boxed().collect(Collectors.toList());
            AtomicInteger index = new AtomicInteger(1);
            Map<Integer, Integer> map = array.stream().distinct().sorted()
                    .collect(Collectors.toMap(i -> i, i -> index.getAndIncrement(), (i, j) -> i));
            BITree<Integer> biTree = new BITree<>(map.size(), 0, Integer::sum);
            IntStream.rangeClosed(1, array.size()).forEach(i -> {
                ans += biTree.query(map.get(array.get(array.size() - i)) - 1);
                biTree.update(map.get(array.get(array.size() - i)), 1);
            });
        }

        @Override
        public String getResult() {
            return "" + ans;
        }
    }

    private static final class P3368Solver implements Solver {
        private final String result;

        private P3368Solver(InputStream inputStream) {
            FastReader reader = new FastReader(inputStream);
            int size = reader.readInt();
            int total = reader.readInt();
            SegmentTree<Integer> segmentTree = new SegmentTree<>(size, 0, Integer::sum, (x, y) -> x * y);
            StringBuilder builder = new StringBuilder();
            IntStream.rangeClosed(1, size).forEach(i -> segmentTree.insert(i, reader.readInt()));
            IntStream.range(0, total).forEach(i -> {
                switch (reader.readInt()) {
                    case 1:
                        segmentTree.update(reader.readInt(), reader.readInt(), reader.readInt());
                        break;
                    case 2:
                        builder.append(segmentTree.query(reader.readInt())).append('\n');
                        break;
                    default:
                        break;
                }
            });
            result = builder.toString();
        }

        @Override
        public String getResult() {
            return result;
        }
    }

    private static final class P3372Solver implements Solver {
        private final String result;

        private P3372Solver(InputStream inputStream) {
            FastReader reader = new FastReader(inputStream);
            int size = reader.readInt();
            int total = reader.readInt();
            SegmentTree<Long> segmentTree = new SegmentTree<>(size, 0L, Long::sum, (x, y) -> x * y);
            StringBuilder builder = new StringBuilder();
            IntStream.rangeClosed(1, size).forEach(i -> segmentTree.insert(i, (long)reader.readInt()));
            IntStream.range(0, total).forEach(i -> {
                switch (reader.readInt()) {
                    case 1:
                        segmentTree.update(reader.readInt(), reader.readInt(), (long)reader.readInt());
                        break;
                    case 2:
                        builder.append(segmentTree.query(reader.readInt(), reader.readInt())).append('\n');
                        break;
                    default:
                        break;
                }
            });
            result = builder.toString();
        }

        @Override
        public String getResult() {
            return result;
        }
    }

    private static final class P3369Solver implements Solver {
        private final String result;

        private P3369Solver(BalancedTree<Integer> balancedTree, InputStream inputStream) {
            FastReader reader = new FastReader(inputStream);
            StringBuilder builder = new StringBuilder();
            IntStream.range(0, reader.readInt()).forEach(i -> {
                switch (reader.readInt()) {
                    case 1:
                        balancedTree.insert(reader.readInt());
                        break;
                    case 2:
                        balancedTree.delete(reader.readInt());
                        break;
                    case 3:
                        builder.append(balancedTree.rank(reader.readInt())).append('\n');
                        break;
                    case 4:
                        builder.append(balancedTree.select(reader.readInt())).append('\n');
                        break;
                    case 5:
                        builder.append(balancedTree.predecessor(reader.readInt())).append('\n');
                        break;
                    case 6:
                        builder.append(balancedTree.successor(reader.readInt())).append('\n');
                        break;
                    default:
                        break;
                }
            });
            result = builder.toString();
        }

        @Override
        public String getResult() {
            return result;
        }
    }

    private static final class P6136Solver implements Solver {
        private int ans;
        private int last;

        private P6136Solver(BalancedTree<Integer> balancedTree, InputStream inputStream) {
            ans = last = 0;
            FastReader reader = new FastReader(inputStream);
            int insert = reader.readInt();
            int total = reader.readInt();
            IntStream.range(0, insert).forEach(i -> balancedTree.insert(reader.readInt()));
            IntStream.range(0, total).forEach(i -> {
                switch (reader.readInt()) {
                    case 1:
                        balancedTree.insert(reader.readInt() ^ last);
                        break;
                    case 2:
                        balancedTree.delete(reader.readInt() ^ last);
                        break;
                    case 3:
                        last = balancedTree.rank(reader.readInt() ^ last);
                        ans ^= last;
                        break;
                    case 4:
                        last = balancedTree.select(reader.readInt() ^ last);
                        ans ^= last;
                        break;
                    case 5:
                        last = balancedTree.predecessor(reader.readInt() ^ last);
                        ans ^= last;
                        break;
                    case 6:
                        last = balancedTree.successor(reader.readInt() ^ last);
                        ans ^= last;
                        break;
                    default:
                        break;
                }
            });
        }

        @Override
        public String getResult() {
            return "" + ans;
        }
    }

    private static void local() {
        ArrayList<BalancedTree<Integer>> treeList = new ArrayList<>();
        Collections.addAll(treeList, new SGTree<>(), new Splay<>(), new RBTree<>()
                , new FHQTreap<>(), new Treap<>(), new SBTree<>(), new AVLTree<>());
        ArrayList<Vector<LogItem>> result = new ArrayList<>();
        treeList.forEach(i -> result.add(new Vector<>()));
        IntStream.range(0, treeList.size()).parallel().forEach(i ->
                IntStream.rangeClosed(1, 10).parallel().forEach(j -> {
                    String expression = "";
                    try {
                        expression += "testcase " + j + ", " + treeList.get(i).getClass() + " : ";
                        @SuppressWarnings("unchecked")
                        Solver solver = new P3369Solver(treeList.get(i).getClass().getConstructor().newInstance()
                                , new FileInputStream(new File("test/test" + j + ".in")));
                        expression += solver.getResult().equals(new FastReader(
                                (new FileInputStream("test/test" + j + ".std"))).asString());
                        result.get(i).add(new LogItem(j, expression, null));
                    }
                    catch (Exception e) {
                        result.get(i).add(new LogItem(j, expression, e));
                    }
                }));
        result.forEach(i -> {
            i.sort(Comparator.comparing(j -> j.id));
            i.forEach(LogItem::print);
            System.err.println();
        });
    }

    private static void P3374() {
        Solver solver = new P3374Solver(System.in);
        System.out.print(solver.getResult());
    }

    private static void P1908() {
        Solver solver = new P1908Solver(System.in);
        System.out.print(solver.getResult());
    }

    private static void P3368() {
        Solver solver = new P3368Solver(System.in);
        System.out.print(solver.getResult());
    }

    private static void P3372() {
        Solver solver = new P3372Solver(System.in);
        System.out.print(solver.getResult());
    }

    private static void P3369() {
        Solver solver = new P3369Solver(new RBTree<>(), System.in);
        System.out.print(solver.getResult());
    }

    private static void P6136() {
        Solver solver = new P6136Solver(new RBTree<>(), System.in);
        System.out.print(solver.getResult());
    }

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("-Local")) {
            local();
        }
        else if (args.length == 1 && args[0].equals("-P3374")) {
            P3374();
        }
        else if (args.length == 1 && args[0].equals("-P1908")) {
            P1908();
        }
        else if (args.length == 1 && args[0].equals("-P3368")) {
            P3368();
        }
        else if (args.length == 1 && args[0].equals("-P3372")) {
            P3372();
        }
        else if (args.length == 1 && args[0].equals("-P3369")) {
            P3369();
        }
        else if (args.length == 1 && args[0].equals("-P6136")) {
            P6136();
        }
    }
}