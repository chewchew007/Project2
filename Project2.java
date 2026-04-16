package Project2;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import org.jgrapht.*;
import org.jgrapht.graph.*;

class LightRow {

    int grid, row, brokenCol = -1; // grid size, row placement, broken light(if any, else -1)
    char[] lightState;

    public LightRow(int G, int R, char[] S) {
        grid = G;
        row = R;
        lightState = S;
    }

    public void setBroken(int broken) {
        brokenCol = broken;
    }

    public void printState() {
        System.out.printf("row %2d ", row);
        for (int i = 0; i < grid; i++) {
            System.out.print("|   ");
            if (i == brokenCol) { // check if the i match the point where the broken light is or not(if have one)
                System.out.print(lightState[i] + "x  ");
            } else {
                System.out.print(lightState[i] + "   ");
            }

        }
        System.out.println();
    }
}

class MoveEdge extends DefaultEdge {

    private int row;
    private int col;

    public MoveEdge(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String toString() {
        return "Move: row " + row + ", col " + col;
    }
}

public class Project2 {

    public void menu() {
        try {
            Scanner Scan = new Scanner(System.in);

            int grid = getGridSize(Scan);
            String stateGet = getInitialState(Scan, grid);
            ArrayList<LightRow> AllLights = createLightRows(stateGet, grid);

            printGrid(grid, AllLights);

            boolean setBroken = askForBrokenLight(Scan);

            int brokenRow = 0;
            int brokenCol = 0;

            if (setBroken) {
                int[] brokenPos = getBrokenPosition(Scan, grid);
                brokenRow = brokenPos[0];
                brokenCol = brokenPos[1];

                AllLights.get(brokenRow).setBroken(brokenCol);

                //printGrid(grid, AllLights);

            }

            Graph<String, MoveEdge> puzzleGraph = new SimpleDirectedGraph<>(MoveEdge.class);
            Queue<String> queue = new LinkedList<>();
            HashSet<String> visited = new HashSet<>();

            puzzleGraph.addVertex(stateGet);
            queue.add(stateGet);
            visited.add(stateGet);

            int nodeCount = 1;

            while (!queue.isEmpty()) {
                String current = queue.poll();

                for (int r = 0; r < grid; r++) {
                    for (int c = 0; c < grid; c++) {

                        String nextState = toggleLight(current, grid, r, c, setBroken, brokenRow, brokenCol);

                        if (!visited.contains(nextState)) {
                            puzzleGraph.addVertex(nextState);
                            visited.add(nextState);
                            queue.add(nextState);
                            nodeCount++;
                        }

                        puzzleGraph.addEdge(current, nextState, new MoveEdge(r, c));
                    }
                }
            }

            String goalState = "0".repeat(grid * grid);

// BFS to find shortest path from stateGet to goalState
            Map<String, String> parentMap = new HashMap<>();   // child -> parent state
            Map<String, MoveEdge> moveMap = new HashMap<>();   // child -> edge used to reach it

            Queue<String> bfsQueue = new LinkedList<>();
            parentMap.put(stateGet, null);
            bfsQueue.add(stateGet);

            boolean found = false;

            if (stateGet.equals(goalState)) {
                found = true;
            }

            while (!bfsQueue.isEmpty() && !found) {
                String curr = bfsQueue.poll();
                for (MoveEdge edge : puzzleGraph.outgoingEdgesOf(curr)) {
                    String next = puzzleGraph.getEdgeTarget(edge);
                    if (!parentMap.containsKey(next)) {
                        parentMap.put(next, curr);
                        moveMap.put(next, edge);
                        if (next.equals(goalState)) {
                            found = true;
                            break;
                        }
                        bfsQueue.add(next);
                    }
                }
            }

            if (!found) {
                System.out.println("\nNo solution !!");
            } else {
                // Reconstruct path
                LinkedList<String> path = new LinkedList<>();
                LinkedList<MoveEdge> moves = new LinkedList<>();
                String cur = goalState;
                while (parentMap.get(cur) != null) {
                    path.addFirst(cur);
                    moves.addFirst(moveMap.get(cur));
                    cur = parentMap.get(cur);
                }
                path.addFirst(stateGet); // add initial state

                System.out.println();
                System.out.println(moves.size() + " moves to turn off all lights");

                for (int step = 0; step < moves.size(); step++) {
                    MoveEdge move = moves.get(step);
                    String nextState = path.get(step + 1);

                    // Determine turn on or turn off (check the toggled cell in nextState)
                    String prevState = path.get(step);
                    int idx = move.getRow() * grid + move.getCol();
                    char cellBefore = prevState.charAt(idx);
                    String action = (cellBefore == '1') ? "turn off" : "turn on";

                    System.out.println("\n>>> Move " + (step + 1) + " : " + action
                            + " row " + move.getRow() + ", col " + move.getCol());
                    System.out.println("States in bits = " + nextState);

                    // Update AllLights display for this step
                    ArrayList<LightRow> stepLights = createLightRows(nextState, grid);
                    if (setBroken) {
                        stepLights.get(brokenRow).setBroken(brokenCol);
                    }
                    printGrid(grid, stepLights);
                }
            }

        } catch (Exception ex) {
            System.out.print("Got problem here.");
        }
    }

    private String toggleLight(String currentState, int grid, int targetRow, int targetCol, boolean hasBroken, int bRow, int bCol) {
        char[] state = currentState.toCharArray();

        toggle(state, grid, targetRow, targetCol);

        if (hasBroken && targetRow == bRow && targetCol == bCol) {
            toggle(state, grid, targetRow - 1, targetCol - 1);
            toggle(state, grid, targetRow - 1, targetCol + 1);
            toggle(state, grid, targetRow + 1, targetCol - 1);
            toggle(state, grid, targetRow + 1, targetCol + 1);
        } else {
            toggle(state, grid, targetRow - 1, targetCol);
            toggle(state, grid, targetRow + 1, targetCol);
            toggle(state, grid, targetRow, targetCol - 1);
            toggle(state, grid, targetRow, targetCol + 1);
        }
        return new String(state);
    }

    private void toggle(char[] state, int grid, int r, int c) {
        if (r >= 0 && r < grid && c >= 0 && c < grid) {
            int index = (r * grid) + c;
            state[index] = (state[index] == '0') ? '1' : '0';
        }
    }

    private int getGridSize(Scanner Scan) {
        int grid = 0;
        boolean pass = false;
        System.out.print("Enter number of rows for square grid = ");
        do {
            if (Scan.hasNextInt()) {
                grid = Scan.nextInt();
                if (grid < 1) {
                    System.out.println("Input is less than 1, please try again.");
                    System.out.print("Enter number of rows for square grid = ");
                    Scan.nextLine();
                } else {
                    pass = true;
                    Scan.nextLine();
                }
            } else {
                System.out.println("Invalid input type, please try again.");
                System.out.print("Enter number of rows for square grid = ");
                Scan.nextLine();
            }
        } while (!pass);
        return grid;
    }

    private String getInitialState(Scanner Scan, int grid) {
        String stateGet = "";
        boolean pass = false;
        do {
            System.out.printf("\nEnter initial light states(" + (grid * grid) + " states, Left to Right, line by line) = ");
            stateGet = Scan.nextLine().trim();
            if (stateGet.matches("[01]+")) {
                if (stateGet.length() != grid * grid) {
                    System.out.printf("State inputs are not equal to the grid size(" + (grid * grid) + " needed), please try again.");
                } else {
                    pass = true;
                }
            } else {
                System.out.print("State inputs can only be either 0 or 1, please try again.");
            }
        } while (!pass);
        System.out.println();
        return stateGet;
    }

    private ArrayList<LightRow> createLightRows(String stateGet, int grid) {
        ArrayList<LightRow> AllLights = new ArrayList<>();
        int rowCount = 0;
        char[] splitState = stateGet.toCharArray();
        char[] preState = new char[grid];
        int mark = 0;
        for (int i = 0; i < splitState.length; i++) {
            preState[mark] = splitState[i];
            if (mark + 1 == grid) {
                AllLights.add(new LightRow(grid, rowCount, preState));
                rowCount++;
                mark = 0;
                preState = new char[grid];
            } else {
                mark++;
            }
        }
        return AllLights;
    }

    private void printGrid(int grid, ArrayList<LightRow> AllLights) {
        System.out.print("       ");
        for (int i = 0; i < grid; i++) {
            System.out.printf("| col %-2d", i);
        }
        System.out.println();
        for (LightRow row : AllLights) {
            row.printState();
        }
        System.out.println();
    }

    private boolean askForBrokenLight(Scanner Scan) {
        String wantBroken;
        boolean setBroken = false;
        boolean pass = false;
        do {
            System.out.printf("Set broken light(Y/N) ? ");
            wantBroken = Scan.nextLine().trim().toUpperCase();
            if ("Y".equals(wantBroken)) {
                setBroken = true;
                pass = true;
            } else if ("N".equals(wantBroken)) {
                pass = true;
            }
        } while (!pass);
        return setBroken;
    }

    private int[] getBrokenPosition(Scanner Scan, int grid) {
        int brokenRow = 0;
        int brokenCol = 0;
        boolean pass = false;
        boolean passrow = false;
        int testGet;

        do {
            if (!passrow) {
                System.out.printf("Enter row of broken light(0-" + (grid - 1) + ")= ");
            } else {
                System.out.print("Enter col of broken light(0-" + (grid - 1) + ")= ");
            }
            if (Scan.hasNextInt()) {
                testGet = Scan.nextInt();
                if (testGet < 0 || testGet > grid - 1) {
                    System.out.printf("Input is outside the grid size(0-" + (grid - 1) + "), please try again.\n");
                    Scan.nextLine();
                } else {
                    if (!passrow) {
                        brokenRow = testGet;
                        passrow = true;
                    } else {
                        brokenCol = testGet;
                        pass = true;
                        Scan.nextLine();
                    }
                }
            } else {
                System.out.println("Invalid input type, please try again.");
                Scan.nextLine();
            }
        } while (!pass);
        System.out.println();

        return new int[]{brokenRow, brokenCol};
    }

    public static void main(String[] args) {
        Project2 mainapp = new Project2();
        mainapp.menu();
    }
}