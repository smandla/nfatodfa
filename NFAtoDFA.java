package NFAtoDFA;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

public class NFAtoDFA {
	
	static String line = null;
	static String goal = null;
	static String alphabet = null;
	static String initState = null;
	static String transition = null;
	static String mt = null;

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
			goal = br.readLine(); 
			alphabet = br.readLine();
		    initState = br.readLine(); 
			transition = br.readLine(); 
			mt = br.readLine(); // should be empty


			stateList = line.split(",");
			goalList = goal.split(",");
	

			//parses alphabet
			alphabetList = alphabet.split(",");
			start = initState;

			//parses transitionList
			transitionList = transition.split(";");
			for(String t : transitionList){
				String [] transitionArray = t.split(",");
				if(transitionArray.length != 3){
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
				addToStates(allStates,NFATransitions.get(i).initList);
				addToStates(allStates,NFATransitions.get(i).finishList);
				addTransitionsIfNotExists(NFATransitions,makeTransitions(NFATransitions.get(i).finishList, transitionArrayList.toArray(new Transition[transitionArrayList.size()]), alphabetList));
			}

			//PRINTING ALL stateList
			String DFAStates = "";
			for(int i = 0 ; i<allStates.size();i++) {
				ArrayList<String> stateInAllStates = allStates.get(i);
				DFAStates += printStates(stateInAllStates);
				if(i < allStates.size() - 1) {
					DFAStates += ",";
				}
			}
			System.out.println("DFA States: " + DFAStates);

			//PRINTING GOAL stateList
			String DFAGoals = "";
			for(int i = 0 ; i<allStates.size();i++) {
				ArrayList<String> stateInAllStates = allStates.get(i);
				if(hasAcceptState(goalList, stateInAllStates)) {
					DFAGoals += printStates(stateInAllStates);
					if(i < allStates.size()-1) {
						DFAGoals += ",";
					}
				}
			}
			System.out.println("Accepted States: " + DFAGoals);

			//PRINTING ALPHABET
			System.out.println("Epsilon (Alphabet): " + alphabet);

			//PRINTING INITIAL STATE
			String DFAInitState = printStates(initialStateDFA);
			System.out.println("Initial State: " + DFAInitState);

			//PRINTING ALL transitionList
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
			constructAndSolveDFA(DFAStates, DFAGoals, alphabet, DFAInitState, DFATransitions); //input);
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


	/** constructandSolveDFA method to convert from NFA to DFA */
	public static void constructAndSolveDFA(String DFAstates, String DFAacceptStates, 
					String DFAAlphabet, String DFAinitState, String DFAtransitions) { 
						
		String states = DFAstates;
		String accepts = DFAacceptStates;
		String alphabet = DFAAlphabet;
		String initialState = DFAinitState;
		String transitions = DFAtransitions;
		stateList = states.split(",");
		goalList = accepts.split(",");

		

		alphabetList = alphabet.split(",");
		start = initialState;
		
		transitionList = transitions.split("; ");
		for(String transition : transitionList){
			String [] transitionArray = transition.split(",");
			transitionArrayList.add(new Transition(transitionArray[0],transitionArray[1],transitionArray[2]));
		}
	}

	
	//represents a transition
	public static class Transition {
		String init;
		String finish;
		ArrayList<String> initList;
		ArrayList<String> finishList;
		String alphabet;
	
		//constructor
		public Transition(String init, String finish, String alphabet){
			this.init = init;
			this.finish = finish;
			this.alphabet = alphabet;
		}
	
		//constructor with lists
		public Transition(ArrayList<String> init, ArrayList<String> finish, String alphabet){
			this.initList = init;
			this.finishList = finish;
			this.alphabet = alphabet;
		}
	}
	

	private static void addToStates(ArrayList<ArrayList<String>> allStates, ArrayList<String> someStates) {
			if(!allStates.contains(someStates)) {
				allStates.add(someStates);
			}
	}
	public static void addTransitionsIfNotExists(ArrayList<Transition> nFATransitions,ArrayList<Transition> newTransitions) {
		for(int i = 0 ; i< newTransitions.size() ; i++) {
			Collections.sort(newTransitions.get(i).initList);
			Collections.sort(newTransitions.get(i).finishList);
			int j;
			for (j = 0;j < nFATransitions.size(); j++) {
				Collections.sort(nFATransitions.get(j).initList);
				Collections.sort(nFATransitions.get(j).finishList);
				if(nFATransitions.get(j).initList.equals(newTransitions.get(i).initList) && nFATransitions.get(j).finishList.equals(newTransitions.get(i).finishList) && nFATransitions.get(j).alphabet.equals(newTransitions.get(i).alphabet)){
					break;
				}
			}
			if(j == nFATransitions.size()) {
				nFATransitions.add(newTransitions.get(i));
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
			ArrayList<String> newOutcome = getEpsilonClosure(result.get(i), transitions);
			for(int j = 0; j<newOutcome.size(); j++) {
				if (!result.contains(newOutcome.get(j))) {
					result.add(newOutcome.get(j));
				}
			}
		}
		return result;
	}

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
	public static void addIfNotContains(ArrayList<String> result, ArrayList<String> arrayToBeAdded) {
		for(int i = 0; i<arrayToBeAdded.size();i++) {
			if(!result.contains(arrayToBeAdded.get(i))) {
				result.add(arrayToBeAdded.get(i));
			}
		}
	}

	public static ArrayList<Transition> makeTransitions(ArrayList<String> stateOfStates, Transition[] transitions,String[]alphabets) {
		ArrayList<Transition> result= new ArrayList<>();
		for(int i = 0 ; i< alphabetList.length ; i++) {
			ArrayList<String> finishStates = getStatesForGivenInput(stateOfStates, transitions, alphabetList[i]);
			if(finishStates.size() == 0) {
				finishStates.add("qZero");
			}
			result.add(new Transition(stateOfStates, finishStates, alphabetList[i]));
		}
		return result;
	}
	
	//prints the new states of the DFA
	public static String printStates(ArrayList<String>states) {
		String dfaStates = "";
		for(int i = 0 ; i < states.size();i++) {
			dfaStates +=  "{" + states.get(i) + "}" ;
		}
		return dfaStates;
	}
 }
