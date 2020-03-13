package voting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import dataStructures.ArrayList;
import dataStructures.DynamicSet;
import dataStructures.Set;
import dataStructures.StaticSet;

public class Election {
	
	private final static File BALLOTS_FILE = new File("res/ballots.csv");
	private final static File CANDIDATES_FILE = new File("res/candidates.csv");
	
	private Set<Ballot> ballots; // set that contains all valid ballots
	private Set<String[]> candidates; // set that contains candidates and their IDs {[name, id], [name, id], ...}
	private ArrayList<Set<Ballot>> ballotsByCandidate; // array list that contains ballots separated by candidate with rank 1
	private Set<Integer> eliminatedCandidates; // set that contains the candidate IDs of candidates that have been eliminated
	
	private int round;  // keeps track how many candidates have been eliminated
	private int numberOfBallots; // how many ballots were given 
	private int invalidBallots; // how many invalid ballots were given
	private int blankBallots; // how many blank ballots were given
	
	public Election() {
		this.round = 1;
		this.numberOfBallots = 0;
		this.invalidBallots = 0;
		this.blankBallots = 0;
		this.candidates = generateCandidates(CANDIDATES_FILE);
		this.ballots = generateBallots(BALLOTS_FILE);
		this.ballotsByCandidate = generateBallotsByCandidate();
		this.eliminatedCandidates = new StaticSet<Integer>(this.candidates.size() - 1);
	}
	
	public Election(File ballots, File candidates) {
		this.round = 1;
		this.numberOfBallots = 0;
		this.invalidBallots = 0;
		this.blankBallots = 0;
		this.candidates = generateCandidates(candidates);
		this.ballots = generateBallots(ballots);
		this.ballotsByCandidate = generateBallotsByCandidate();
		this.eliminatedCandidates = new StaticSet<Integer>(this.candidates.size() - 1);
	}
	
	public Election(String ballotsFileName, String candidatesFileName) {
		File ballots = new File(ballotsFileName);
		File candidates = new File(candidatesFileName);
		
		this.round = 1;
		this.numberOfBallots = 0;
		this.invalidBallots = 0;
		this.blankBallots = 0;
		this.candidates = generateCandidates(candidates);
		this.ballots = generateBallots(ballots);
		this.ballotsByCandidate = generateBallotsByCandidate();
		this.eliminatedCandidates = new StaticSet<Integer>(this.candidates.size() - 1);
	}
	
	// decides which candidate won and generates result.txt
	public void runElection() {
		try {
			File results = new File("res/results.txt"); 
			if (!results.exists()) { // creates file if it doesn't exists
				results.createNewFile();
			}
			
			FileWriter electionResult = new FileWriter(results);
			electionResult.write("Number of ballots: " + this.numberOfBallots + "\n");
			electionResult.write("Number of blank ballots: " + this.blankBallots + "\n");
			electionResult.write("Number of invalid ballots: " + this.invalidBallots + "\n");
			
			while (this.eliminatedCandidates.size() < this.candidates.size() - 1) {	// writes rounds of eliminated candidates
				int moreThanHalf = this.checkMoreThanHalf();
				if (moreThanHalf == -1) { // checks if a candidate has more than 50% of 1's before proceeding
					electionResult.write(this.eliminateCandidateFromElection(this.getLowestCandidate()) + "\n");
				} else {
					String name = "";
					for (String[] s: this.candidates) { // retrieve candidate name, s = [name, id]
						if (Integer.parseInt(s[1]) == moreThanHalf) {
							name = s[0];
							break;
						}
					}
					electionResult.write("Winner: " + name + " wins with " + this.count1s(moreThanHalf) + " #1's");
					break;
				}
			}
			
			electionResult.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// check if any candidate has more than 50% of 1s
	// returns -1 if no candidate has more than 50%
	private int checkMoreThanHalf() {
		int half  = (int) Math.ceil(this.ballots.size() / 2);
		
		for (int i = 0; i < this.ballotsByCandidate.size(); i++) {
			if (this.ballotsByCandidate.get(i).size() >= half) {
				return i + 1; // i originally refers to ballotsByCandidate index so we have to convert it to candidateId
			}
		}
		
		return -1;
	}
	
	// gets candidate with the lowest amount of 1's, handles ties accordingly
	private int getLowestCandidate() {
		int higher = 0;
		
		// finds highest candidate
		for (int i = 1; i < this.ballotsByCandidate.size(); i++) {
			if (this.ballotsByCandidate.get(i).size() != 0 && this.ballotsByCandidate.get(i).size() < this.ballotsByCandidate.get(higher).size()) {
				higher = i;
			}
		}
		
		// finds if two candidates have the same amount 1's
		for (int j = 0; j < this.ballotsByCandidate.size(); j++) {
			if (j != higher && this.ballotsByCandidate.get(j).size() == this.ballotsByCandidate.get(higher).size()) {
				int rank = 2;
				while (rank <= this.candidates.size()) { // iterates through ranks until tie is broken or it reaches last rank
					int countJ = 0;
					int countH = 0;
					for (Set<Ballot> s: this.ballotsByCandidate) {
						for (Ballot b: s) {
							if (b.getCandidateByRank(rank) == j+1) { // count 2s of candidate j
								countJ++;
							} else if (b.getCandidateByRank(rank) == higher+1) { // count 2s of candidate higher
								countH++;
							}
						}
					}
					if (countJ < countH) { // if j has less candidates
						higher = j; // higher is now candidate with less 1's, I know its confusing
						break;
					} else if (countJ > countH) { // if higher has less candidates tie is broken
						break;
					} else {
						rank++;
					}
				}
				
				if (rank > this.candidates.size()) { // if while loop went through all ranks and couldn't break tie
					if (higher < j) { // if candidate j's id is larger, then we eliminate j
						higher = j; 
					}
				}
			}
		}
		
		return higher + 1; // higher originally refers to ballotsByCandidate index so we have to convert it to candidateId
	}
	
	// eliminates candidate form ballotsByCandidate and their corresponding ballots, then re distributes the ballots
	private String eliminateCandidateFromElection(int candidateId) {
		if (candidateId < 1 || candidateId > this.candidates.size()) {
			throw new IllegalArgumentException("Canidate does not exists");
		}
		
		String result = "Round "  + this.round++ + ": " ;
		this.eliminatedCandidates.add(candidateId); // add candidate to eliminated candidates
		
		for (String[] s: this.candidates) { // retrieve candidate name, s = [name, id]
			if (Integer.parseInt(s[1]) == candidateId) {
				result += s[0];
				break;
			}
		}
		result += " was eliminated with " + this.count1s(candidateId) + " #1's";
		
//		for (Set<Ballot> s: this.ballotsByCandidate) {
//			for (Ballot b: s) {
//				for (int i: this.eliminatedCandidates) {
//					b.eliminate(i);
//				}
//				this.ballotsByCandidate.get(b.getCandidateByRank(1) - 1).add(b);
//			}
//		}
		
		for (Ballot b: this.ballotsByCandidate.get(candidateId - 1)) { // iterates through candidate set
			for (int i: this.eliminatedCandidates) { // iterates through eliminated candidates IDs
				b.eliminate(i); // eliminates all previously eliminated candidates from ballot
			}
			this.ballotsByCandidate.get(b.getCandidateByRank(1) - 1).add(b); // adds ballot according to which candidate is rank 1
		}
		
		Set<Ballot> empty = new StaticSet<Ballot>(1); // empty stack to replace previous ballot stack of the eliminated candidate
		this.ballotsByCandidate.set(candidateId - 1, empty); // sets eliminated candidate set to and empty set
		
		return result; // Round <round>: <candidateName> was eliminated with <count1s> #1’s
	}
	
	// counts the amount of 1s a given candidate has
	private int count1s(int candidateId) {
		// the size of the set of ballots corresponding to that candidate
		return this.ballotsByCandidate.get(candidateId - 1).size();
	}
	
	private Set<Ballot> generateBallots(File ballots) {
		Set<Ballot> result = new DynamicSet<Ballot>(2);
		
		try (Scanner scanner = new Scanner(ballots)) {
			while (scanner.hasNextLine()) {
				this.numberOfBallots++; // counts every ballot regardless if valid or blank
				Ballot b = new Ballot(scanner.nextLine());
				if (validateBallot(b) && !isBallotEmpty(b)) { // checks if ballot is valid before inserting into set
					result.add(b);
				} else if (isBallotEmpty(b)) {
					this.blankBallots++;
				} else {
					this.invalidBallots++;
				}
			}
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public Set<Ballot> getBallots() {
		return this.ballots;
	}
		
	private Set<String[]> generateCandidates(File candidates) {
		Set<String[]> result = new StaticSet<String[]>(5);
		
		try (Scanner scanner = new Scanner(candidates)) {
			while (scanner.hasNextLine()) {
				String[] candidate = scanner.nextLine().split(","); // generates array: [candidateName, candidateID]
				result.add(candidate);
			}
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public Set<String[]> getCandidates() {
		return this.candidates;
	}

	private ArrayList<Set<Ballot>> generateBallotsByCandidate() {
		ArrayList<Set<Ballot>> result = new ArrayList<Set<Ballot>>(this.getCandidates().size());
		
		for (int i = 0; i < this.getCandidates().size(); i++) { // initialize ballot set for each candidate
			Set<Ballot> temp = new DynamicSet<Ballot>(1);
			result.add(temp);
		}
		
		for (Ballot b: ballots) {
			result.get(b.getCandidateByRank(1) - 1).add(b); // gets set corresponding to candidate,
															// adds ballot in which that candidate was rank 1
		}
		
		return result;
	}
	
	// if ballot is valid returns true
	private boolean validateBallot(Ballot b) {
		Set<Integer> ballotCandidates = new StaticSet<Integer>(this.candidates.size());
		Set<Integer> ballotRanks = new StaticSet<Integer>(this.candidates.size());
		Set<Integer> candidateIds = new StaticSet<Integer>(this.candidates.size());
		
		for (String[] s: this.candidates) { // creates set of integers containing the IDs of every candidate
			try { //checks if candidate ID is a number
				candidateIds.add(Integer.parseInt(s[1])); 
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		
		for (int rank = 1; rank <= ballotRanks.size(); rank++) { // EX: if there are 4 votes in total, then they must be [1,2,3,4]
			if (!ballotRanks.isMember(rank)) {                   // therefore if one number in the sequence is missing, then the ballot is invalid
				return false;
			}
		}
		
		int i = 1; 
		for (int[] arr: b.getVotes()) { // arr = [candidate, rank]
			if (!ballotCandidates.add(arr[0]) || !candidateIds.isMember(arr[0])) { // when trying to add candidate that already exists
				return false;													   // or when trying to add candidate that does not exist
			} else {
				ballotCandidates.add(arr[0]);
			}
			
			if (arr[1] < 1 || arr[1] > candidateIds.size()) { // if rank is less than 1 or larger that the largest candidate's ID
				return false;
			} else {
				ballotRanks.add(arr[1]);
			}
			
			if (i != arr[1]) { // if rank skips or repeats a number
				return false;
			}
			i++;
		}
				
		return true;
	}
	
	// if ballot is empty returns true
	private boolean isBallotEmpty(Ballot b) {
		if (b.getVotes().isEmpty()) {
			return true;
		}
		
		return false;
	}
	
	public String ballotsByCandidateToString() {
		String result = "";
		
		for (Set<Ballot> s: this.ballotsByCandidate) {
			result += "\n{";
			for (Ballot b: s) {
				if (b != null) {
					result += b.toString() + "\n";
				}
			}
			result += "}";
		}
		
		return result;
	}
	
	// generates string containing all ballots and all candidates
	@Override
	public String toString() {
		String result = "";
		
		for (Ballot b: this.ballots) { 
			result += b.toString() + "\n";
		}
		
		result += "{";
		for (String[] s: this.candidates) {
			result += "[" + s[0] + ", " + s[1] + "], ";
		}
		result += "}";
		
		return result;
	}

}
