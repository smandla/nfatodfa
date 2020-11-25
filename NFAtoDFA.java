/**
 * NFAtoDFA.java
 * Takes a NFA from an input file and outputs the equivalent DFA to a file. 
 * @author Kavya Mandla
 * @author Hera Malik
 * @version November 2020
 */

package NFAtoDFA;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

public class NFAtoDFA {
	
	//strings to store input pieces
	static String line = null;
	static String goal = null;
	static String alphabet = null;
	static String initState = null;
	static String transition = null;
	static String start;

  	//lists we need
	static String[] stateList;
	static String[] goalList;
	static String[] alphabetList;
	static String[] transitionList;
	static ArrayList<Transition> transitionArrayList = new ArrayList<>();


	public static void main(String[] args) throws IOException {
		FileReader fileReader = new FileReader("nfa.txt");
		BufferedReader br = new BufferedReader(fileReader);


		while((line = br.readLine()) != null){
			//start with an empty transitionList
			transitionArrayList.clear();

			//read in all the parts of the input
			String goal = br.readLine(); 
			String alphabet = br.readLine();
		    String initState = br.readLine(); 
			String transition = br.readLine(); 
			
			
			//parses input on the delimiters
			stateList = line.split(",");
			goalList = goal.split(",");
			alphabetList = alphabet.split(",");
			start = initState;
			transitionList = transition.split(";");
			for(String t : transitionList){
				String [] transitionArray = t.split(",");
				
				if(transitionArray.length != 3) {
					break;
				}
				
				Transition newTran = new Transition(transitionArray[0],transitionArray[1],transitionArray[2]);
				transitionArrayList.add(newTran);
			}
			
			System.out.println(" ");
			System.out.println("Here is the equivalent DFA: ");
			System.out.println(" ");

			ArrayList<String> initialStateDFA = getAllEpsilonClosure(start, transitionArrayList.toArray(new Transition[transitionArrayList.size()]));
			ArrayList<Transition> NFATransitions= new ArrayList<>();
			ArrayList<ArrayList<String>> allStates = new ArrayList<>();
			NFATransitions = makeTransitions(initialStateDFA, transitionArrayList.toArray(new Transition[transitionArrayList.size()]), alphabetList);
			
			for(int i = 0; i < NFATransitions.size() ; i ++) {
				
				if(!allStates.contains(NFATransitions.get(i).initList)) {
					allStates.add(NFATransitions.get(i).initList);
				}
				
				if(!allStates.contains(NFATransitions.get(i).finishList)) {
					allStates.add(NFATransitions.get(i).finishList);
				}
				addNewTransitions(NFATransitions, makeTransitions(NFATransitions.get(i).finishList, transitionArrayList.toArray(new Transition[transitionArrayList.size()]), alphabetList));
			}

			//prints the list of all the states 
			String DFAStates = "";
			for(int i = 0; i < allStates.size(); i++) {
				ArrayList<String> stateInAllStates = allStates.get(i);
				DFAStates += printStates(stateInAllStates);
				if(i < allStates.size() - 1) {
					DFAStates += ",";
				}
			}
			System.out.println("DFA States: " + DFAStates);
			String DFAGoals = "";
			for(int i = 0 ; i < allStates.size(); i++) {
				ArrayList<String> stateInAllStates = allStates.get(i);
				if(hasAcceptState(goalList, stateInAllStates)) {
					DFAGoals += printStates(stateInAllStates);
					if(i < allStates.size() - 1) {
						DFAGoals += ",";
					}
				}
			}
			
			System.out.println("Accepted States: " + DFAGoals);
			System.out.println("Epsilon (Alphabet): " + alphabet);
			String DFAInitState = printStates(initialStateDFA);
			System.out.println("Initial State: " + DFAInitState);

			String DFATransitions = "";
			for(int i = 0 ; i<NFATransitions.size();i++) {
				DFATransitions+=printStates(NFATransitions.get(i).initList);
				DFATransitions+=",";
				DFATransitions+=printStates(NFATransitions.get(i).finishList);
				DFATransitions+=",";
				DFATransitions+=NFATransitions.get(i).alphabet;
				if(i < NFATransitions.size() - 1) {
					DFATransitions+="; ";
				}
			}

			//print transitions 
			System.out.println("Transitions: " + DFATransitions);
			constructDFA(DFAStates, DFAGoals, alphabet, DFAInitState, DFATransitions); //input);
			System.out.println(" ");

			//prints to output file
			PrintWriter out = new PrintWriter("dfa.txt");
			out.println("DFA States: " + DFAStates);
			out.println("Epsilon (Alphabet): "+ alphabet);
			out.println("Transitions: " + DFATransitions);
			out.println("Starting State: " + DFAInitState);
			out.println("Accepting State(s): " + DFAGoals);
			out.close();
			
		}
		br.close();
	}


	/** 
	 * Formats the DFA generated by the transformation to be printed
	 * @param dfaStates-- the states of the generated DFA
	 * @param dfaAcceptStates-- the accept states of the generated DFA
	 * @param dfaAlphabet-- the alphabet of the DFA
	 * @param dfaInitState-- the initial state of the DFA
	 * @param dfaTransitions-- the transitions of the DFA
	 * */
	public static void constructDFA(String dfaStates, String dfaAcceptStates, 
					String dfaAlphabet, String dfaInitState, String dfaTransitions) { 

		stateList = dfaStates.split(",");
		goalList = dfaAcceptStates.split(",");
		alphabetList = dfaAlphabet.split(",");
		start = dfaInitState;
		transitionList = dfaTransitions.split("; ");
		
		for(String transition : transitionList) {
			String [] transitionArray = transition.split(",");
			transitionArrayList.add(new Transition(transitionArray[0],transitionArray[1],transitionArray[2]));
		}
	}

	/**
	 * Represents a single transition
	 */
	public static class Transition {
		String init;
		String finish;
		ArrayList<String> initList;
		ArrayList<String> finishList;
		String alphabet;
	
		/**
		 * Constructor for transition. Creates a transition linking the 
		 * origin and ending state with the character(s) of the transition.
		 * @param init-- the starting state of the transition
		 * @param finish-- the ending state of the transition
		 * @param alphabet-- the character(s) on which the transition occurs
		 */
		public Transition(String init, String finish, String alphabet){
			this.init = init;
			this.finish = finish;
			this.alphabet = alphabet;
		}
	
		//constructor with lists (for the purpose of creating new states)
		public Transition(ArrayList<String> init, ArrayList<String> finish, String alphabet){
			this.initList = init;
			this.finishList = finish;
			this.alphabet = alphabet;
		}
	}
	
	
	/**
	 * Adds transitions
	 * @param nfaTransitions-- the transitions from the NFA
	 * @param newTransitions-- the new transitions
	 */
	public static void addNewTransitions(ArrayList<Transition> nfaTransitions, ArrayList<Transition> newTransitions) {
		for(int i = 0 ; i< newTransitions.size() ; i++) {
			Collections.sort(newTransitions.get(i).initList);
			Collections.sort(newTransitions.get(i).finishList);
			int j;
			for (j = 0; j < nfaTransitions.size(); j++) {
				Collections.sort(nfaTransitions.get(j).initList);
				Collections.sort(nfaTransitions.get(j).finishList);
				if(nfaTransitions.get(j).initList.equals(newTransitions.get(i).initList) && nfaTransitions.get(j).finishList.equals(newTransitions.get(i).finishList) && 
						nfaTransitions.get(j).alphabet.equals(newTransitions.get(i).alphabet)){
					break;
				}
			}
			
			if(j == nfaTransitions.size()) {
				nfaTransitions.add(newTransitions.get(i));
			}
		}
	}

	
	public static ArrayList<String> getEpsilonClosure(String state, Transition[] transitions){
		ArrayList<String> result = new ArrayList<>();
		result.add(state);
		for(int i = 0; i < transitions.length; i++) {
			if(transitions[i].alphabet.equals("$") && transitions[i].init.equals(state) && !result.contains(transitions[i].finish)) {
				result.add(transitions[i].finish);
			}
		}
		return result;
	}

	public static ArrayList<String> getAllEpsilonClosure(String state, Transition[] transitions){
		ArrayList<String> result = getEpsilonClosure(state, transitions);
		for(int i = 0 ; i < result.size(); i++) {
			ArrayList<String> newTransitions = getEpsilonClosure(result.get(i), transitions);
			for(int j = 0; j<newTransitions.size(); j++) {
				if (!result.contains(newTransitions.get(j))) {
					result.add(newTransitions.get(j));
				}
			}
		}
		return result;
	}

	/**
	 * Check that there is an accepting state
	 * @param acceptState-- a list of accept states
	 * @param stateOfStates-- a list of general states
	 */
	public static boolean hasAcceptState(String[] acceptStates, ArrayList<String> stateOfStates) {
		for(int i = 0 ; i < stateOfStates.size(); i++) {
			for( int j = 0 ; j < acceptStates.length; j++) {
				if(acceptStates[j].equals(stateOfStates.get(i))) {
					return true;
				}
			}
		}
		return false;
	}

	
	/**
	 * Generates new states if the conditions are met
	 * @param stateOfStates-- a list of states
	 * @param transitions-- a list of transitions 
	 * @param alphabet-- an alphabet
	 */
	public static ArrayList<String> getStatesForGivenInput(ArrayList<String> stateOfStates, Transition[]transitions, String alphabet){
		ArrayList<String> result = new ArrayList<>();
		for(int i = 0 ; i < stateOfStates.size(); i++) {
			for(int j = 0 ; j < transitionList.length; j++) {
				if(transitions[j].alphabet.equals(alphabet) && transitions[j].init.equals(stateOfStates.get(i))&& !result.contains(transitions[j].finish)) {
					result.add(transitions[j].finish);
					addIfNotContains(result, getAllEpsilonClosure(transitions[j].finish, transitions));
				}
			}
		}
		return result;
	}

	/**
	 * Adds a given ArrayList to a running list of results if not already included
	 * @param result-- the running list of results
	 * @param newArray-- the list being added
	 */
	public static void addIfNotContains(ArrayList<String> result, ArrayList<String> newArray) {
		for(int i = 0; i < newArray.size(); i++) {
			if(!result.contains(newArray.get(i))) {
				result.add(newArray.get(i));
			}
		}
	}

	/**
	 * Creates new transitions based on the input
	 * @param stateOfStates
	 * @param transitions
	 * @param alphabets
	 * @return newTransitions-- the list of new transitions generated 
	 */
	public static ArrayList<Transition> makeTransitions(ArrayList<String> stateOfStates, Transition[] transitions,String[] alphabets) {
		ArrayList<Transition> newTransitions = new ArrayList<>();
		for(int i = 0 ; i< alphabetList.length ; i++) {
			ArrayList<String> finishStates = getStatesForGivenInput(stateOfStates, transitions, alphabetList[i]);
			if(finishStates.size() == 0) {
				finishStates.add("qZero");
			}
			newTransitions.add(new Transition(stateOfStates, finishStates, alphabetList[i]));
		}
		return newTransitions;
	}
	
	/**
	 * Prints the states of the DFA
	 * @param states
	 * @return dfaStates-- the string containing the list of the DFA states
	 */
	public static String printStates(ArrayList<String> states) {
		String dfaStates = "";
		for(int i = 0 ; i < states.size();i++) {
			dfaStates +=  "{" + states.get(i) + "}" ;
		}
		return dfaStates;
	}
 }
