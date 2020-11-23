import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class NFAfinalDFA {
	
	static String line = null;
	static String goal = null;
	static String alphabet = null;
	static String initState = null;
	static String transitionList = null;
	static String input = null;
	static String mt = null;

  static String start;

  //lists we need
	static String[] stateList;
	static String[] goalList;
	static String[] alphabetList;
	static String[] transitionList;
	static String[] inputList;
	static ArrayList<Transition> transitionList = new ArrayList<>();


	public static void main(String[] args) throws IOException {
		FileReader fileReader = new FileReader("in1.in");
		BufferedReader br = new BufferedReader(fileReader);


		while((line = br.readLine()) != null){
			//start with an empty transitionList
			transitionList.clear();

			//read in all the parts of the input
			goal = br.readLine(); 
			alphabet = br.readLine();
		  initState = br.readLine(); 
			trans = br.readLine(); 
			input = br.readLine();
			mt = br.readLine(); //Should be empty

			
			if(!checkLines()){
				System.err.print("The line is empty.");
				continue;
			}


			states = line.split(",");
			goals = goal.split(",");


			if(!checkGoal()){
				continue;
			}

      //parses alphabet
			alphabets = alphabet.split(",");
			start = initState;

			if(!inArray(start, stateList)){
				System.err.println("Invalid start state: "+start);
				continue;
			}

      //parses transitionList
			transitions = trans.split("#");
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
				transitionList.add(new Transition(transitionArray[0],transitionArray[1],transitionArray[2]));
			}
			
			if(error) {
				System.err.println("Invalid transition. transitionList should be of size 3");
				continue;
			}
			
			if(error2) {
				continue;
			}



			inputs = input.split("#");
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
			
	
				if(goal == "" || alphabet == "" || initState == "" || trans == "" 
					|| input == "") {
					System.err.println("One of the required lines is empty.");
					continue;
				}
				
				if(mt == "") {
					System.err.println("No newline after input");
					continue;
				}

			System.out.println("Here is the equivalent DFA: ");
			ArrayList<String> initialStateDFA = getAllEpsilonClosure(start, transitionList.finalArray(new Transition[transitionList.size()]));
			ArrayList<Transition> NFATransitions= new ArrayList<>();
			ArrayList<ArrayList<String>> allStates = new ArrayList<>();
			NFATransitions = makeTransitions(initialStateDFA, transitionList.finalArray(new Transition[transitionList.size()]), alphabetList);
			for(int i = 0; i < NFATransitions.size() ; i ++) {
				addToStates(allStates,NFATransitions.get(i).initAL);
				addToStates(allStates,NFATransitions.get(i).finalAL);
				addTransitionsIfNotExists(NFATransitions,makeTransitions(NFATransitions.get(i).finalAL, transitionList.finalArray(new Transition[transitionList.size()]), alphabetList));
			}

			//PRINTING ALL stateList
			String DFAStates = "";
			for(int i = 0 ; i<allStates.size();i++) {
				ArrayList<String> stateInAllStates = allStates.get(i);
				DFAStates += printStates(stateInAllStates);
				if(i<allStates.size()-1) {
					DFAStates += ",";
				}
			}
			System.out.println(DFAStates);

			//PRINTING GOAL stateList
			String DFAGoals = "";
			for(int i = 0 ; i<allStates.size();i++) {
				ArrayList<String> stateInAllStates = allStates.get(i);
				if(hasAcceptState(goals, stateInAllStates)) {
					DFAGoals += printStates(stateInAllStates);
					if(i<allStates.size()-1) {
						DFAGoals += ",";
					}
				}
			}
			System.out.println(DFAGoals);

			//PRINTING ALPHABET
			System.out.println(l2);

			//PRINTING INITIAL STATE
			String DFAInitState = printStates(initialStateDFA);
			System.out.println(DFAInitState);

			//PRINTING ALL transitionList
			String DFATransitions = "";
			for(int i = 0 ; i<NFATransitions.size();i++) {
				DFATransitions+=printStates(NFATransitions.get(i).initAL);
				DFATransitions+=",";
				DFATransitions+=printStates(NFATransitions.get(i).finalAL);
				DFATransitions+=",";
				DFATransitions+=NFATransitions.get(i).alphabet;
				if(i < NFATransitions.size() - 1) {
					DFATransitions+="#";
				}
			}
			System.out.println(DFATransitions);

			//PRINTING INPUT
			System.out.println(l5);

			constructAndSolveDFA(DFAStates, DFAGoals, l2, DFAInitState, DFATransitions, l5);
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
		states = line.split(",");
		goals = accepts.split(",");

		if(!checkGoal()){
			return;
		}

		alphabets = l2.split(",");
		start = l3;

		if(!inArray(start, stateList)){
			System.err.println("Invalid start state "+start);
			return;
		}
		transitions = l4.split("#");
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
			transitionList.add(new Transition(transitionArray[0],transitionArray[1],transitionArray[2]));
		}
		if(error){
			System.err.println("Invalid transition. transitionList should be of size 3");
			return;
		}
		if(error2){
			return;
		}
		inputs = l5.split("#");
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
		String final;
		ArrayList<String> initList;
		ArrayList<String> finalList;
		String alphabet;
	
		//constucfinalr
		public Transition(String init, String final, String alphabet){
			this.init = init;
			this.final = final;
			this.alphabet = alphabet;
		}
	
		//constucfinalr with lists
		public Transition(ArrayList<String> init, ArrayList<String> final, String alphabet){
			this.initList = init;
			this.finalList = final;
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
			Collections.sort(newTransitions.get(i).initAL);
			Collections.sort(newTransitions.get(i).finalAL);
			int j;
			for (j = 0;j < nFATransitions.size(); j++) {
				Collections.sort(nFATransitions.get(j).initAL);
				Collections.sort(nFATransitions.get(j).finalAL);
				if(nFATransitions.get(j).initAL.equals(newTransitions.get(i).initAL) && nFATransitions.get(j).finalAL.equals(newTransitions.get(i).finalAL) && nFATransitions.get(j).alphabet.equals(newTransitions.get(i).alphabet)){
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
			for(int j = 0 ; j < transitionList.size() ; j++){
				if(transitionList.get(j).init.equals(currentState) && transitionList.get(j).alphabet.equals(inputArray[i])){
					currentState = transitionList.get(j).final;
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
			if(!inArray(goal,states)){
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
			if(transitions[i].alphabet.equals("$") && transitionList[i].init.equals(state) && !result.contains(transitions[i].final)) {
				result.add(transitions[i].final);
			}
		}
		return result;
	}

	public static ArrayList<String> getAllEpsilonClosure(String state, Transition[] transitions){
		ArrayList<String> result = getEpsilonClosure(state, transitionList);
		for(int i = 0 ; i < result.size(); i++) {
			ArrayList<String> newOutcome = getEpsilonClosure(result.get(i), transitionList);
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
				if(transitions[j].alphabet.equals(alphabet) && transitionList[j].init.equals(stateOfStates.get(i))&&!result.contains(transitions[j].final)) {
					result.add(transitions[j].final);
					addIfNotContains(result, getAllEpsilonClosure(transitions[j].final, transitionList));
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

	public static ArrayList<Transition> makeTransitions(ArrayList<String> stateOfStates,Transition[]transitions,String[]alphabets) {
		ArrayList<Transition> result= new ArrayList<>();
		for(int i = 0 ; i< alphabetList.length ; i++) {
			ArrayList<String> finalStates = getStatesForGivenInput(stateOfStates, transitionList, alphabetList[i]);
			if(finalStates.size() == 0) {
				finalStates.add("Dead");
			}
			result.add(new Transition(stateOfStates, finalStates, alphabetList[i]));
		}
		return result;
	}
	
	public static String printStates(ArrayList<String>states) {
		String out = "";
		for(int i = 0 ; i < states.size();i++) {
			out +=  states.get(i);
			if(i < stateList.size() - 1) {
				out += "*";
			}
		}
		return out;
	}

	
	private static boolean transitionExists(String state, String alphabet) {
		for(int i = 0 ; i < transitionList.size() ; i++){
			if(transitionList.get(i).init.equals(state) 
			&& transitionList.get(i).alphabet.equals(alphabet)) {
				return true;
			}
		}
		return false;
	}
}
