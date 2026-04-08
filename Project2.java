
package Project2;

import java.util.ArrayList;
import java.util.Scanner;

class LightRow{
    int grid, row, brokenCol = -1; // grid size, row placement, broken light(if any, else -1)
    char[] lightState;
    
    public LightRow(int G, int R, char[] S){grid = G; row = R; lightState = S;}
    
    public void setBroken(int broken){ brokenCol = broken;}
    
    public void printState(){
        System.out.printf("row %2d ",row); 
        for(int i = 0; i < grid; i++){
            System.out.print("|   ");
            if(i == brokenCol){ // check if the i match the point where the broken light is or not(if have one)
                System.out.print(lightState[i] + "x  ");
            }
            else{ System.out.print(lightState[i] + "   ");}
            
        }
        System.out.println();
    }
}

public class Project2 {

    public static void main(String[] args) {
        try{
            Scanner Scan = new Scanner(System.in);
            int grid = 0, rowCount = 0, colCount;
            String stateGet;
            int brokenRow = 0, brokenCol = 0, testGet;
            boolean pass = false;
            ArrayList<LightRow> AllLights = new ArrayList<>(); // save all lightStates here
            
            System.out.print("Enter number of rows for square grid = ");
            do{
                if(Scan.hasNextInt()){ // check if the input is a number or not
                    grid = Scan.nextInt();
                    if(grid < 1){ // check if the input is positive number or not
                        System.out.println("Input is less than 1, please try again.");
                        System.out.print("Enter number of rows for square grid = ");
                        Scan.nextLine();
                    }
                    else{pass = true; Scan.nextLine();}
                    
                }
                else{
                    System.out.println("Invalid input type, please try again.");
                    System.out.print("Enter number of rows for square grid = ");
                    Scan.nextLine();
                }
            }while(!pass); // finish getting grid input
            
            pass = false;
            do{
                System.out.printf("\nEnter initial light states(" + (grid*grid) + " states, Left to Right, Up to Down) = ");
                stateGet = Scan.nextLine().trim();
                if(stateGet.matches("[01]+")){ // check if the input is all 0 and 1 or not
                    if(stateGet.length() != grid*grid){ // check if the input size is the same as grid size or not
                        System.out.printf("State inputs are not equal to the grid size(" + (grid*grid) +" needed), please try again.");
                    }
                    else{pass = true;}
                }
                else{
                    System.out.print("State inputs can only be either 0 or 1, please try again.");
                }
            }while(!pass); // finish getting state inputs
            System.out.println();
            
            // Normal printing
            
            char[] splitState = stateGet.toCharArray(); // split stateGet into separate characters
            char[] preState = new char[grid];
            int mark = 0;
            for(int i = 0; i < splitState.length; i++){
                preState[mark] = splitState[i];
                if(mark+1 == grid){ // check if the prepare State has enough states for a row
                    AllLights.add(new LightRow(grid, rowCount, preState)); // set up the AllLights arrayList
                    
                    rowCount++; mark = 0;
                    preState = new char[grid];
                }
                else{mark++;}
            }
            
            System.out.printf("This is grid: " + grid + "\n");
            System.out.printf("This is state: " + stateGet + "\n");
            System.out.println();
            
            System.out.print("       "); 
            for(int i = 0; i < grid; i++){
                System.out.printf("| col %-2d",i);
            }
            System.out.println();
            for (LightRow row : AllLights) {
                row.printState();
            }
            System.out.println();
            
            String wantBroken;
            boolean setBroken = false;
            pass = false;
            
            do{
            System.out.printf("Set broken light(Y/N) ? ");
            wantBroken = Scan.nextLine().trim().toUpperCase();
            if("Y".equals(wantBroken)) {setBroken = true; pass = true;}
            else if("N".equals(wantBroken)){pass = true;}
            }
            while(!pass);
            
            if(setBroken){
                pass = false;
                boolean passrow = false;
            do{
                if(!passrow) {System.out.printf("Enter row of broken light(0-" + (grid-1) + ")= ");}
                else {System.out.print("Enter col of broken light(0-" + (grid-1) + ")= ");}
                if(Scan.hasNextInt()){ // check if the input is a number or not
                    testGet = Scan.nextInt();
                    if(testGet < 0 || testGet > grid-1){ // check if the input is positive number or not and whether it is under the grid
                        System.out.printf("Input is outside the grid size(0-" + (grid-1) + "), please try again.\n");
                        Scan.nextLine();
                    }
                    else{
                        if(!passrow){brokenRow = testGet; passrow = true;}
                        else{brokenCol = testGet; pass = true; Scan.nextLine();}
                    }
                    
                }
                else{
                    System.out.println("Invalid input type, please try again.");
                    Scan.nextLine();
                }
            }while(!pass); // finish getting broken inputs
            System.out.println();
            
            AllLights.get(brokenRow).setBroken(brokenCol); // set the column's light state in the chosen row to be broken
            
            System.out.printf("This is broken row: " + brokenRow + "\n");
            System.out.printf("This is broken column: " + brokenCol + "\n");
            //} // can remove this for using the print below later, but now it is used to check if the broken is put correctly or not
            
            System.out.print("       "); 
            for(int i = 0; i < grid; i++){
                System.out.printf("| col %-2d",i);
            }
            System.out.println();
            for (LightRow row : AllLights) {
                row.printState();
            }
            } // remove this if needed, as long as you uncomment the one at line 156
            
        }
        catch(Exception ex){System.out.print("Got problem here.");}
    }
}