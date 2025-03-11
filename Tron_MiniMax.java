import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    private static final int ROWS = 20, COLS = 30, DEPTH = 6;
    private static final String[] DIRECTIONS_STRING = {"UP", "DOWN", "LEFT", "RIGHT"};
    private static final int[][] DIRECTIONS = {
            {-1, 0},  // UP
            {1, 0},   // DOWN
            {0, -1},  // LEFT
            {0, 1}   // RIGHT

    };
    private static final boolean[][] visited = new boolean[ROWS][COLS];


    // --------------------------------------------------------------------------
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int myR = -1, myC = -1, enemyR = -1, enemyC = -1;

        // game loop
        while (true) {

            // Get locations
            int N = in.nextInt(); // total number of players (2 to 4).
            int P = in.nextInt(); // your player number (0 to 3).
            for (int i = 0; i < N; i++) {
                int X0 = in.nextInt(); // starting X coordinate of lightcycle (or -1)
                int Y0 = in.nextInt(); // starting Y coordinate of lightcycle (or -1)
                int X1 = in.nextInt(); // starting X coordinate of lightcycle (can be the same as X0 if you play before this player)
                int Y1 = in.nextInt(); // starting Y coordinate of lightcycle (can be the same as Y0 if you play before this player)

                visited[Y0][X0] = true; // 8888888888
                // Get my and enemy's location
                if (i == P) {
                    if (X1 != -1 && Y1 != -1) {
                        myC = X1;
                        myR = Y1;
                        visited[myR][myC] = true;
                    }
                } else {
                    if (X1 != -1 && Y1 != -1) {
                        enemyC = X1;
                        enemyR = Y1;
                        visited[enemyR][enemyC] = true;
                    }
                }

            }

            // Decision Field
            int bestScore = Integer.MIN_VALUE;
            int bestOption = 0; // Set default to up
            boolean[][] tempVisited = new boolean[ROWS][COLS];

            // Make Decision
            for (int i = 0; i < 4; i++) {
                int newR = myR + DIRECTIONS[i][0], newC = myC + DIRECTIONS[i][1];
                int score = Integer.MIN_VALUE;

                // see valid directions
                if (isValid(newR, newC)) {
                    tempVisited[newR][newC] = true;
                    score = miniMax(tempVisited, false, newR, newC, enemyR, enemyC,
                            Integer.MIN_VALUE, Integer.MAX_VALUE, DEPTH - 1);
                    tempVisited[newR][newC] = false;
                }

                // choose
                if (score > bestScore) {
                    bestScore = score;
                    bestOption = i;
                } else if (score == bestScore){
                    int prevBonus = head2headDist(myR+DIRECTIONS[bestOption][0],
                            myC+DIRECTIONS[bestOption][1], enemyR, enemyC);
                    int curBonus = head2headDist(myR+DIRECTIONS[i][0],
                            myC+DIRECTIONS[i][1], enemyR, enemyC);
                    if (prevBonus > curBonus && isValid(newR, newC)){
                        bestOption = i;
                    }
                }
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            System.out.println(DIRECTIONS_STRING[bestOption]);
        }
    }

    // MiniMax Algorithms
    private static int miniMax(boolean[][] mmVisited, boolean max, int mr, int mc, int er, int ec, int a, int b, int d) {
        // Base case
        if (d == 0) {
            int myReach = bfsScore(mr, mc, copyOf(mmVisited));
            int enemyReach = bfsScore(er, ec, copyOf(mmVisited));
            int extra = 0;
            if (enemyReach < 5) {
                extra = 100;
            }

            return (myReach - enemyReach) + extra;
        }

        if (max) {
            int maxScore = Integer.MIN_VALUE;
            for (int[] dir : DIRECTIONS) {
                int newR = mr + dir[0], newC = mc + dir[1];
                if (isValid(newR, newC) && !mmVisited[newR][newC]) {
                    mmVisited[newR][newC] = true;
                    int score = miniMax(mmVisited, false, newR, newC, er, ec, a, b, d - 1);
                    mmVisited[newR][newC] = false;
                    maxScore = Math.max(score, maxScore);
                    a = Math.max(a, score);

                    if (a >= b)
                        break;
                }
            }
            return (maxScore == Integer.MIN_VALUE) ? -999999 : maxScore;
        } else {
            int minScore = Integer.MAX_VALUE;
            for (int[] dir : DIRECTIONS) {
                int newR = er + dir[0], newC = ec + dir[1];
                if (isValid(newR, newC) && !mmVisited[newR][newC]) {
                    mmVisited[newR][newC] = true;
                    int score = miniMax(mmVisited, true, mr, mc, newR, newC, a, b, d - 1);
                    mmVisited[newR][newC] = false;
                    minScore = Math.min(score, minScore);
                    b = Math.min(b, score);

                    if (b <= a)
                        break;
                }
            }
            return (minScore == Integer.MAX_VALUE) ? 999999 : minScore;
        }
    }


    // Check if in Bound -> Done
    private static boolean inBound(int r, int c) {
        return r >= 0 && r < ROWS
                && c >= 0 && c < COLS;
    }

    // Check if is valid
    private static boolean isValid(int r, int c) {
        return inBound(r, c) && !visited[r][c];
    }

    private static int bfsScore(int r, int c, boolean[][]mmVisited){
        // Base case
        if (!inBound(r, c) || visited[r][c] || !mmVisited[r][c]) {
            return 0;
        }

        int count = 0;
        Queue<int[]> q = new LinkedList<>();
        q.offer(new int[]{r, c});
        mmVisited[r][c] = true;

        while (!q.isEmpty()){
            int[] point = q.poll();
            count++;
            int bfsR = point[0];
            int bfsC = point[1];

            for (int[] dir: DIRECTIONS){
                int newR = bfsR + dir[0], newC = bfsC + dir[1];
                if (isValid(newR, newC) && !mmVisited[newR][newC]){
                    q.offer(new int[]{newR, newC});
                    mmVisited[newR][newC] = true;
                }
            }
        }
        return count;
    }

    // Copy an array -> Done
    private static boolean[][] copyOf(boolean[][] a) {
        boolean[][] result = new boolean[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            System.arraycopy(a[i], 0, result[i], 0, a[i].length);
        }
        return result;
    }

    private static int head2headDist(int r, int c, int er, int ec){
        return Math.abs((Math.abs(r-er) + Math.abs(c-ec)) - 1);
    }
}