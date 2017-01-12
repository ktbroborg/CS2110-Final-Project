# CS2110-Final-Project

GnomenWald!

This project addresses a routing problem with gnomes travelling among villages on toll roads. Each gnome is a separate thread. The assignment specified a fully implemented GUI, topological sort and a minimal spanning tree. The full asssignment is in this repository as HW5description.pdf.

Traveling - happens in a gnome thread.
	* gnomes are put into groups of keyholders (for finding)
		- coordinates of the one to find serve as the check
	* related - should have a makeRoute() method whose efficiency will depend on the urgency
		- start with just the shortest path
	* gnomes start in a village then travel along a road
	* by default they move randomly
		- if they don't have enough money for a certain road, they try to find another or get stuck
	* gnomes get stuck if they run out of cash. 

Map - has a list of the villages and a list of roads
The initial map is built to have 16 villages. Each village added has one road in, and one road out.
	* add - adds a village with a given limit
	* remove removes a village - what happens to the gnomes with routes that go through it?
		- should also clear all gnomes from the village and surrounding area
		- they're locations are set to null, so they're essentially removed from the map
		- government subsidizes the moves, because it's a government choice ?
			(it's rather rude to vaporize a village/road from right around a gnome)
	* needs to have a shortest path given two villages in the map
		- Dijkstra - cost is based on delay instead of toll, should be the fastest path
		- reroutes the gnome
		- route planning does not take into account the tolls along the way (this would be a good idea tho)
		- an extension would be to have more and less efficient routes
	* topological sort
		- indegree is num connections - num nexts 
		- cycles throw an exception
	* spanning tree
		- option 1 - Prim's - disregards direction, so it's very possible to have dead ends
		- weight is based on toll
		- work on Edmond's??? - complicated but actually for directed graphs

Location is a superclass of both Village and Road which makes things like finding the location of a gnome
simpler, because there isn't a need to determine whether they are on a road or village, and then find which
one.

Villages are given names like ID numbers. When a new village is created, it is given an integer name that is
consecutive to whatever the previous highest-numbered(named?) village was. When a village is deleted, the
names do not shift, otherwise, how confusing would that be?
	* Villages are built to have coordinates in them
	* Villages maintain a list of nexts, which is roads that can be taken that start in that village
	* Villages also maintain a list of neighbors which is villages that can be gotten to in 1 step, and also
	villages from which it can be gotten to in 1 step. This will be used in editing villages, to shut down
	areas close to them to make sure the system stays properly in sync

Graphing - the graph displayed has nodes and edges. Ideally, each village node will have the village's name
as well as the current number of occupants.
	* the gnomes on the roads are put there to reflect realtime motion
	* the gnomes in villages are in fixed positions based on number of gnomes currently in the village
