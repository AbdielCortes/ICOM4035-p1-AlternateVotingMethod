package voting;

import dataStructures.LinkedList;

public class Ballot {
	
	private int ballotNum; // ID for the ballot
	private LinkedList<int[]> votes = new LinkedList<int[]>(); // [candidateId1, rank]-> [candidateId2, rank]-> [CandidateIdn, rank]

	public Ballot(String input) {
		String[] info = input.split(","); // generates array: ["1234", "5:1", "2:2", "4:3", "1:4", "3:5"]
		
		try { // converts strings to integers, inside try-catch, in case it attempts to convert invalid char to int
			this.ballotNum = Integer.parseInt(info[0]);
			
			for (int i = 1; i < info.length; i++) {
				String[] vote = info[i].split(":"); // generates array: ["candidateId", "rank"]
				int[] idRank = new int[2];
				idRank[0] = Integer.parseInt(vote[0]); 
				idRank[1] = Integer.parseInt(vote[1]);
				this.votes.add(idRank);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Invalid ballot file");
		}
	}
	
	public int getBallotNum() { // returns the ballot number
		return this.ballotNum;
	}
	
	public LinkedList<int[]> getVotes() {
		return this.votes;
	}
	
	// gets rank for parameter candidate
	public int getRankByCandidate(int candidateId) { 
		for (int[] arr: this.votes) { 
			if (arr[0] == candidateId) {
				return arr[1];
			}
		}
		
		return -1; // returns -1 if candidate was not found
	}
	
	// gets candidate with that rank
	public int getCandidateByRank(int rank) { 
		for (int[] arr: this.votes) { 
			if (arr[1] == rank) {
				return arr[0];
			}
		}
		
		return -1; // returns -1 if candidate was not found
	}
	
	// eliminates a candidate from votes
	public boolean eliminate(int candidateId) {
		boolean eliminated = false;
		
		int position = 0;
		for (int[] arr: this.votes) { 
			if (arr[0] == candidateId) { // finds candidate
				position = this.votes.firstIndex(arr);
				eliminated = true;
			}
		}
		
		if (eliminated) { // if candidate was found
			for (int i = position; i < this.votes.size(); i++) {
				this.votes.get(i)[1]--; // decrements candidate rank for every after the deleted one
			}
			this.votes.remove(this.votes.get(position)); // deletes array corresponding to candidate
		}
		
		return eliminated;
	}
	
	@Override 
	public boolean equals(Object obj) { // two ballots are the same if the have the same ballotNum
		if (obj != null) {
			if (this.ballotNum == ((Ballot) obj).getBallotNum()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		String result = this.ballotNum + ": ";
		
		for (int[] e: this.votes) {
			result += "[" + e[0] + ", " + e[1] + "]-> ";
		}
		result += "null";
		
		return result;
	}

}
