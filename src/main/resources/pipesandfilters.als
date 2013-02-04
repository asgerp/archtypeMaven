module pipesandfilters
// General model
abstract sig Configuration {
 	elements: set Filter
}
abstract sig Filter  {
	pipes: set Filter
}
sig Start extends Filter{} {}


// acyclic graph
pred ac[c:Configuration] {
	no f :c.elements & Filter | f in f.^pipes
}

// All filters are reachable from start filter
pred reach[c:Configuration] {
	Filter in Start.*pipes
}

pred pipes_and_filters [c: Configuration] {
	ac[c]
}



run pipes_and_filters for 1 Configuration, 10 Filter




