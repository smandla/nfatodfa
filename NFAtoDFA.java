package NFAtoDFA;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class NFAtoDFA {
	
	static String line = null;
	static String goal = null;
	static String alphabet = null;
	static String initState = null;
	static String transition = null;
	static String input = null;
	static String mt = null;

	static String start;

  	//lists we need
	static String[] stateList;
	static String[] goalList;
	static String[] alphabetList;
	static String[] transitionList;
	static String[] inputList;
	static ArrayList<Transition> transitionArrayList = new ArrayList<>();


	public static void main(String[] args) throws IOException {
		FileReader fileReader = new FileReader("in1.in");
		BufferedReader br = new BufferedReader(fileReader);


		while((line = br.readLine()) != null){
			//start with an empty transitionList
			transitionArrayList.clear();

			//read in all the parts of the input
			goal = br.readLine(); 
			alphabet = br.readLine();
		    initState = br.readLine(); 
			transition = br.readLine(); 
			input = br.readLine();
			mt = br.readLine(); // should be empty


			stateList = line.split(",");
			goalList = goal.split(",");


			if(!checkGoal()){
				continue;
			}

			//parses alphabet
			alphabetList = alphabet.split(",");
			start = initState;

			if(!inArray(start, stateList)){
				System.err.println("Invalid start state: "+start);
				continue;
			}

			//parses transitionList
			transitionList = transition.split("#");
			boolean error = false;
			boolean error2 = false;
			for(String t : transitionList){
				error2 = false;
				String [] transitionArray = t.split(",");
				if(transitionArray.length != 3){
					error = true;
					break;
				}
				
				for ( int i = 0 ; i < 2 ; i++) {
					if(!inArray(transitionArray[i], stateList)){
						error2 = true;
						System.err.println("Invalid transition. "+transitionArray[i]+" is not included in the given list of states.");
						break;
					}
				}
				
				if(!inArray(transitionArray[2], alphabetList) && !transitionArray[2].equals("$")){
					error2 = true;
					System.err.println("Invalid transition. "+transitionArray[2]+" is not included in the alphabet.");
				}
				
				if(error2){
					break;
				}
				
				Transition newTran = new Transition(transitionArray[0],transitionArray[1],transitionArray[2]);
				transitionArrayList.add(newTran);
			}
			
			if(error) {
				System.err.println("Invalid transition. transitionList should be of size 3");
				continue;
			}
			
			if(error2) {
				continue;
			}



			inputList = input.split("#");
			boolean invalidInput = false;
			String badInput = "";
			for(String i : inputList){
				String [] inputArray = i.split(",");
				
				for(String inputAlphabet : inputArray) {
					if(!inArray(inputAlphabet, alphabetList)) {
						invalidInput = true;
						badInput = inputAlphabet;
						break;
					}
				}
			}
			
	
				if(goal == "" || alphabet == "" || initState == "" || transition == "" 
					|| input == "") {
					System.err.println("One of the required lines is empty.");
					continue;
				}
				
				if(mt == "") {
					System.err.println("No newline after input");
					continue;
				}

			System.out.println("Here is the equivalent DFA: ");
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
			System.out.println(DFAStates);

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
			System.out.println(DFAGoals);

			//PRINTING ALPHABET
			System.out.println(alphabet);

			//PRINTING INITIAL STATE
			String DFAInitState = printStates(initialStateDFA);
			System.out.println(DFAInitState);

			//PRINTING ALL transitionList
			String DFATransitions = "";
			for(int i = 0 ; i<NFATransitions.size();i++) {
				DFATransitions+=printStates(NFATransitions.get(i).initList);
				DFATransitions+=",";
				DFATransitions+=printStates(NFATransitions.get(i).finishList);
				DFATransitions+=",";
				DFATransitions+=NFATransitions.get(i).alphabet;
				if(i < NFATransitions.size() - 1) {
					DFATransitions+="#";
				}
			}
			System.out.println(DFATransitions);

			//PRINTING INPUT
			System.out.println(input);

			constructAndSolveDFA(DFAStates, DFAGoals, alphabet, DFAInitState, DFATransitions, l5);
		}
		
		br.close();
	}

	public static void constructAndSolveDFA(String DFAstates, String DFAacceptStates, 
					String DFAAlphabet, String DFAinitState, String DFAtransitions, 
					String DFAinput ) {
		String line = DFAstates;
		String accepts = DFAacceptStates;
		String l2 = DFAAlphabet;
		String l3 = DFAinitState;
		String l4 = DFAtransitions;
		String l5 = DFAinput;
		stateList = line.split(",");
		goalList = accepts.split(",");

		if(!checkGoal()){
			return;
		}

		alphabetList = l2.split(",");
		start = l3;

		if(!inArray(start, stateList)){
			System.err.println("Invalid start state "+start);
			return;
		}
		
		transitionList = l4.split("#");
		boolean error = false;
		boolean error2 = false;
		for(String transition : transitionList){
			error2 = false;
			String [] transitionArray = transition.split(",");
			if(transitionArray.length != 3){
				error = true;
				break;
			}
			
			for ( int i = 0 ; i <2 ;i++){
				if(!inArray(transitionArray[i], stateList)){
					error2 = true;
					System.err.println("Invalid transition. "+transitionArray[i]+" is not included in the stateList.");
					break;
				}
			}
			
			if(!inArray(transitionArray[2], alphabetList) &&!transitionArray[2].equals("$")){
				error2 = true;
				System.err.println("Invalid transition. "+transitionArray[2]+" is not included in the alphabet.");
			}
			
			if(error2){
				break;
			}
			transitionArrayList.add(new Transition(transitionArray[0],transitionArray[1],transitionArray[2]));
		}
		if(error){
			System.err.println("Invalid transition. transitionList should be of size 3");
			return;
		}
		if(error2){
			return;
		}
		inputList = l5.split("#");
		boolean error3 = false;
		String badInput = "";
		for(String input : inputList){
			String [] inputArray = input.split(",");
			for(String inputAlphabet : inputArray){
				if(!inArray(inputAlphabet, alphabetList)){
					error3 = true;
					badInput = inputAlphabet;
					break;
				}
			}
		}
		
		if(error3){
			System.err.println("Invalid input string at "+badInput);
			return;
		}
		
		boolean error4 = false;
		
		for(String state : stateList){
			for(String alphabet : alphabetList){
				if(!transitionExists(state,alphabet)){
					error4 = true;
					System.err.println("Missing transition for state " +state+" on input "+ alphabet );
					break;
				}
			}
		}
		
		if(error4){
			return;
		}
		System.out.println("DFA Constructed");
		for(String input : inputList){
			String result = processInput(input);
			if(inArray(result, goalList)){
				System.out.println("Accepted");
			} else {
				System.out.println("Rejected");
			}
		}
		System.out.println("");
	}

	
	//represents a transition
	class Transition {
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
	private static String processInput(String input) {
		String currentState = start;
		String [] inputArray = input.split(",");
		for(int i = 0 ; i< inputArray.length ;i++){
			for(int j = 0 ; j < transitionArrayList.size() ; j++){
				if(transitionArrayList.get(j).init.equals(currentState) && transitionArrayList.get(j).alphabet.equals(inputArray[i])){
					currentState = transitionArrayList.get(j).finish;
					break;
				}
			}
		}
		return currentState;
	}

	private static boolean checkGoal() {
		for(String goal : goalList){
			if(goal.equals("")){
				continue;
			}
			if(!inArray(goal,stateList)){
				System.err.println("Invalid accept state "+goal);
				return false;
			}
		}
		return true;
	}

	private static boolean inArray(String s , String [] array){
		for(int i = 0 ; i < array.length;i++){
			if(array[i].equals(s)){
				return true;
			}
		}
		return false;
	}

	public static ArrayList<String> getEpsilonClosure(String state, Transition[] transitions){
		ArrayList<String> result = new ArrayList<>();
		result.add(state);
		for(int i = 0; i < transitions.length; i++) {
			if(transitions[i].alphabet.equals("$") && transitionList[i].init.equals(state) && !result.contains(transitions[i].finish)) {
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
				finishStates.add("Dead");
			}
			result.add(new Transition(stateOfStates, finishStates, alphabetList[i]));
		}
		return result;
	}
	
	public static String printStates(ArrayList<String>states) {
		String out = "";
		for(int i = 0 ; i < states.size();i++) {
			out +=  states.get(i);
			if(i < stateList.length - 1) {
				out += "*";
			}
		}
		return out;
	}

	
	private static boolean transitionExists(String state, String alphabet) {
		for(int i = 0 ; i < transitionArrayList.size() ; i++){
			if(transitionArrayList.get(i).init.equals(state) 
			&& transitionArrayList.get(i).alphabet.equals(alphabet)) {
				return true;
			}
		}
		return false;
	}
 }



