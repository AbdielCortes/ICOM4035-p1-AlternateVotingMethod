## Data Structures Project 1 - Alternate Voting Method

In elections, countries like the United States use what the voting method known as
*Plurality Voting*. The efficiency of this system becomes questionable when we have 
more than two candidates. Let’s say our candidates are John, Sara, Fred, and Jessica; 
*Plurality Voting* is used for which John gets 27% of the votes, Sara 33%, Fred 18%, 
and Jessica 22%. In this scenario, Sara would win, but only 33% of the voters actually
were with her, and 67% where against her.

*Instant-Runoff Voting* fixes situations like these, where the voters assign each candidate
a rank of preference; for example, one voter's ballot may look like this: Jhon-3, Sara-2,
Fred-4, Jessica-1. In order to decide for a winner, we first look if any candidate was 
marked 1 in more of 50% of the ballots, else we eliminate the lowest ranking candidate and
once again check if any candidate has more than 50%. We continue eliminating the lowest 
ranking candidate until one has reached more than 50% or only two candidate are left.

This project aim to fully implement what the backed for an *Instant-Runoff Voting* system 
would look like. Since were focused on what the backend would be, we read input from txt 
files and we output a txt file detailing the rounds and the winner.

For a very visual an easy to understand explanation on voting systems, I suggest you watch 
the YouTube video by TED-Ed, *Which voting system is the best? - Alex Gendler*.

Video Link: https://www.youtube.com/watch?v=PaxVCsnox_4

If you like to know more about *Instant-Runoff Voting* and *Plurality Voting*, the Wikipedia
pages for these have some very helpful visual examples.

Instant-Runoff Voting: https://en.wikipedia.org/wiki/Instant-runoff_voting

Plurality Voting: https://en.wikipedia.org/wiki/Plurality_voting#Examples_of_plurality_voting

