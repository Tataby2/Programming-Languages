/*
 * Author: Aabaan Samad
 * Date: 11/22/2022
 * Program: Dining Ranking Problem
 * Description: Program to rank diners based on their preferences using a similarity matrix calculated using Spearmans Rank Correlation and a recursive function that finds the optimal permutation of the diners
 * Using a HashMap to keep track of the all possible similarity scores and its corresponding permutation and an ArrayList to keep track of the positions of the diners. 
 * I use a Diner class to add structure to the program and to make keeping track of each diner easier.
 * The algorithm is exhaustive and goes through all possible combinations. I had first thought of using a more comparitive method as can be seen in the commented out method 
 * (AssignPositions)to compare each score in each row of the similarity matrix, however, that method only considered the first best score it came across and disregarded subsequent scores,
 * so although it allowed the first 2 diners to be ranked correctly and to the most accurate degree, it did not allow the last 2 diners to be ranked correctly, and instead just listed them in numerical order.
 * It was dependant on where the first best score occured and did not backtrack to check for future score comparisons.
 * Version: 2.0
 */


import java.util.*;
public class Main {
    
    public static int scores[] = new int[120];
    public static HashMap<ArrayList<Integer>, Double> map = new HashMap<>();
    
    //Diner Class Definition
    static class Diner {
        //Diner Attributes
        private int DinerNum;
        private int[] choice;
        private int position;
    
        //Diner Getters and Setters
        public void setDinerNum(int dinerNum) {
            DinerNum = dinerNum;
        }
    
        public void setChoice(int[] choice) {
            this.choice = choice;
        }
    
        public void setPosition(int position) {
            this.position = position;
        }
    
        public int getDinerNum() {
            return DinerNum;
        }
    
        public int[] getChoice() {
            return choice;
        }
    
        public int getPosition() {
            return position;
        }
    
        //Diner toString method
        public void printDiner() {
            System.out.println("Diner Number: " + DinerNum);
            System.out.println("Diner Choices: " + Arrays.toString(choice));
            System.out.println("Diner Position: " + position);
        }
        
    }
   
    //Creating an array of diners using a for loop
    public static Diner[] CreateDiners() {
        Diner[] diners = new Diner[5];
        for (int i = 0; i < diners.length; i++) {
            diners[i] = new Diner();
            //Set the number of the diner for identification purposes
            diners[i].setDinerNum(i+1);
        }

        return diners;
    }
    
    //Generating a list of unique random numbers
    public static int[] UniqueRandomNumbers () {
        // Create a arraylist of numbers from 1 to 5
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            numbers.add(i);
        }
    
        // Shuffle the list to get a random order
        Collections.shuffle(numbers);

        // Convert the list to an array
        return numbers.stream().mapToInt(Integer::intValue).toArray();
    }
    
    //Setting a random choice for each diner
    public static Diner[] setDinerChoices(Diner[] diners) {
        
        for (int i = 0; i < diners.length; i++) {
            
            int[] choiceNum = UniqueRandomNumbers();
            diners[i].setChoice(choiceNum);
        }

        return diners;
    }

    //Setting a random position for each diner using setP
    public static Diner[] setDinerPositions(Diner[] diners) {
        
        for (int i = 0; i < diners.length; i++) {
            
            diners[i].setPosition(i+1);
        }

        return diners;
    }

    //Calculating the Similarity Matrix using Spearman's Rank Correlation
    public static double[][] CalculateSimilarity(Diner[] diners) {

        double[][] similarityMatrix = new double[diners.length][diners.length];

        for (int i = 0; i < diners.length; i++) {
            for (int j = 0; j < diners.length; j++) {
                //Set similarity to 1 if the choices of different diners are the exact same 
                if (diners[i].getChoice() == diners[j].getChoice()) {
                    similarityMatrix[i][j] = 1;
                }else{
                    int leng = diners[i].getChoice().length;

                    double MeanSquaredError = 0.0;
                    //Calculating the Mean Squared Error using the choices of different diners
                    for (int k = 0; k < leng; k++) {
                        MeanSquaredError += Math.pow(diners[i].getChoice()[k] - diners[j].getChoice()[k], 2);

                    }
                    //Formula for Spearman's Rank Correlation as per Wikipedia
                    double similarity = 1 - (6 * MeanSquaredError)/(leng * (leng * leng - 1));
                    similarityMatrix[i][j] = similarity;

                }
            }
        }

        return similarityMatrix;
    }

    //Assigning a position to each diner based on the similarity matrix
    /**public static ArrayList<Integer> AssignPositions(Diner[] diners, double [][] similarityMatrix) {
        
        ArrayList<Integer> positions = new ArrayList<Integer>();
        int n = 0;
        
        for (int i = 0; i < diners.length; i++) {
            for (int j = 0; j < diners.length; j++) {
                
                double maxThreshold = -1; //-0.1
                double minThreshold = 1;
                
                int maxHold1, maxHold2 = 0;
                int minHold1, minHold2 = 0;
                
                //Ignore ranking of a diner against itself
                if (i == j) {
                    //System.out.println("Ignore");
                    if (j >= diners.length && i < diners.length-1) {
                        n = 0;
                        i++;
                    }
                    continue;
                }
                else if (similarityMatrix[i][j] >= maxThreshold) {
                    
                    maxHold1 = i+1;
                    maxHold2 = j+1;
                    maxThreshold = similarityMatrix[i][j];

                    if (j == diners.length-1) {

                        if(positions.contains(maxHold1) == false) {
                            positions.add(maxHold1);
                        }

                        if(positions.contains(maxHold2) == false) {
                            positions.add(maxHold2);
                        }
                    }
                }
                /*else if (similarityMatrix[i][j] < minThreshold) {
                    
                    if(j == diners.length-1) {
                        if(positions.contains(minHold1) == false) {

                        }    
                    }
                }
            }
        }
        return positions;
    }**/

    //Recursive function to find optimal permutation for ordering the diners based on the similarity matrix
    public static void optimalPermutation(ArrayList<Integer> positions, double [][] similarityMatrix) {
        
        int n = similarityMatrix.length;
        
        //Set to keep track of all possible UNIQUE choices and prevent repeating the same elements. If I had used a ArrayList, it would have allowed us to repeat elements which is not what I want
        Integer [] nums = {1, 2, 3, 4, 5};
        Set<Integer> set = new HashSet<Integer>(Arrays.asList(nums));
        
        //Remove choices that have already been assigned
        for (int i = 0; i < positions.size(); i++) {
            set.remove(positions.get(i));
        }


        if(set.isEmpty()) {
            
            //If there is nothing left to add, we are in the base case
            //Calculate the similarity of all the permutations and return the one with the highest similarity
            double totalSimScore = 0;
            
            for (int i = 0; i < n-1; i++) {
                int xCoord = positions.get(i);
                int yCoord = positions.get(i+1);
                double similarity = similarityMatrix[xCoord-1][yCoord-1];
                totalSimScore += similarity;
            }

            map.put(positions, totalSimScore);
        }
        else {
            //If there is something left to remove from the set and there is a position left to encounter, recurse through the function until each element has been proccessed through the recursive case
            for (int i = 0; i < set.size(); i++) {
                //Copy over updated positions array
                ArrayList<Integer> newPositions = new ArrayList<Integer>(positions);
                newPositions.add((Integer)set.toArray()[i]);
                optimalPermutation(newPositions, similarityMatrix);
            }
        }
    }
    
    //Main Method
    public static void main(String[] args) {
        
        //Program Start
        System.out.println("\nInviting Diners...Diner Choices are listed from left to right from most to least preferred with choices being 1 to 5...\n");
        
        //Initializing the diners
        Diner[] diners = CreateDiners();
        
        //Assigning a random choice ranking to each diner
        diners = setDinerChoices(diners);
            
        //Dining Rules to arrange the diners based on their preferences and choice rankings (left to right is most to least preferred)
        double [][] similarityMatrix = CalculateSimilarity(diners);
        
        //Printing the Similarity Matrix (debugging purposes)
        /*for (int i = 0; i < diners.length; i++) {
            for (int j = 0; j < diners.length; j++) {
                //print the similarity matrix
                System.out.printf("%.2f ", similarityMatrix[i][j]);
            }
            System.out.println();
        }
        */

        System.out.println();

        //Assigning a position to each diner based on the rankings of each of their groupings
        //ArrayList<Integer> positions = AssignPositions(diners, similarityMatrix);

        optimalPermutation(new ArrayList<>(), similarityMatrix);
        //System.out.println(map);
        
        ArrayList <Integer> maxPositions = new ArrayList<Integer>();
        
        double maxSimScore = 0;

        //This goes through the map and finds the position with the highest similarity and returns the score value and the best possible positioning permutation
        for (Map.Entry<ArrayList<Integer>, Double> entry : map.entrySet()) {
            if (entry.getValue() > maxSimScore) {
                maxSimScore = entry.getValue();
                maxPositions = entry.getKey();
            }
        }

        for (int i = 0; i < maxPositions.size(); i++) {
            diners[i].setPosition(maxPositions.get(i));
        }

        //Printing the diners and their information
        for (int i = 0; i < diners.length; i++) {
            diners[i].printDiner();
            System.out.println();
        }

        //Printing the positions of the diners
        System.out.println("Final Diner Positions: " + maxPositions);
        System.out.printf("Highest Similarity Score: %.2f", maxSimScore);
    }
}
