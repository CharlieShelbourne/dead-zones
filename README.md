# deadZones

## Motivation
Upon starting my graduate scheme I was tasked to build a solution to detect geographical locations where ships can move without being detected by satellite 

## Why 
The team in question wanted to understand if ship tracking data had dead zones where ships could potentially break international sanctions 

## Problem solved 
This solution increased the understanding of the underlying data to a high level answered the question do dead zones exist? 

## Lessons learnt 
Given a prolonged period of time in the order of a couple of months, the solution showed no dead zones in major shipping routes. Only dead zone appeared to the north and south poles. 

Dead zones begin to appear when we reduced the time of data collection. Therefore, we ruled that there are no areas in which satellites pass over that ships cannot be detected given sufficient time for ships to travel. However, this does not account for ships missed in high density zones. 

## What makes this solution standout 
This is the first time I used java as a language. Java proved a fast compute time, although time complexity of the code can be improved. The solution leverages several algorithms such as breadth first search, centroid and convex hull. I wrote most of the geo-related code and algorithms. In python you could probably find a library like geo-pandas to do a lot of this. However, many older systems are written in java and I find it's a robust language. 

## Notes
The code could do with refactoring and addition of comments and unit tests. I was fairly time constrained on this project and able to prove the detection system was not needed further than answering the above question.

 
