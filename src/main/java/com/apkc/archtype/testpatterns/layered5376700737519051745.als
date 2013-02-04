module layered
open util/integer

// General model
abstract sig Configuration { elements: set Element }
abstract sig Element { references: set Element, meta: Int }

// Layered style
some sig Layer extends Element { }


pred layer_bigger[ i:Int, j:Int] {
	int[i].add[1] = int[j] 
}

pred layer_smaller[ i:Int, j:Int] {
	int[i].sub[1] = int[j] 
}

pred layer_equal[ i:Int, j:Int] {
	int[i] = int[j] 
}

pred non_zero_refs [c: Configuration ] {
	all m: c.elements & Layer | #m.references > 0
}

pred layered_style [c: Configuration] {
	all m: c.elements & Layer | all r: m.references | layer_bigger[r.meta,m.meta] or layer_smaller[r.meta,m.meta] or layer_equal[r.meta,m.meta]
}
	
pred layered_layer_style [c: Configuration]{
	layered_style[c] 
	non_zero_refs[c]
}
one sig testLayered extends Configuration { } {
	elements = Layer1 + Layer2 + Layer3
}
one sig Layer1 extends Layer { } {
	references = Layer2
	meta = 1
}
one sig Layer2 extends Layer { } {
	references = Layer1
	meta = 2
}
one sig Layer3 extends Layer { } {
	references = Layer2
	meta = 3
}
assert layer1 {
	layered_layer_style[testLayered]
}
assert layer2 {
	layered_layer_style[testLayered]
}
assert layer3 {
	layered_layer_style[testLayered]
}
check layer1 for 8 but 1 Configuration
check layer2 for 8 but 1 Configuration
check layer3 for 8 but 1 Configuration
