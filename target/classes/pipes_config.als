open pipesandfilters

one sig Instance extends Configuration {} {
	elements = Filter1 + Filter2 + Filter3 + Filter4 + Filter5
}

one sig Filter1 extends Start{ } {
	pipes = Filter2 + Filter3
}

one sig Filter2 extends Filter{ } {
	pipes = Filter4
}

one sig Filter3 extends Filter{ } {
	pipes = Filter4
}

one sig Filter4 extends Filter{ } {
	pipes = Filter5
}

one sig Filter5 extends Filter{} {
	no pipes
}

assert ac {
	ac[Instance]
}

assert reachable {
	reach[Instance]
}

assert pipes {
	ac[Instance] reach[Instance]
}
//check ac for 8
//check reachable for 8

check pipes for 8


assert conforms{
	pipes_and_filters[Instance] 
}
check conforms for 8 Filter

