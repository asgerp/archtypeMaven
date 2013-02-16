module layered
open util/integer

// General model
abstract sig Configuration { elements: set Element }
abstract sig Element { references: set Element, meta: Int }

// Layered style
abstract sig Layer extends Element { }


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
	all m: c.elements & Layer | #m.references = 0 => some_refs_me[m,c]
}

pred some_refs_me [l: Layer, c: Configuration] {
    some m: c.elements & Layer | l in m.references
}

pred same_layer [c: Configuration, layer: Layer] {
    all l: c.elements & Layer | layer_equal[l.meta,layer.meta]
}

pred non_refs [c: Configuration] {
    all l: c.elements | same_layer[c, l]
}
pred layered_style [c: Configuration] {
	all m: c.elements & Layer | all r: m.references | layer_bigger[r.meta,m.meta] or layer_smaller[r.meta,m.meta] or layer_equal[r.meta,m.meta]
}
	
pred layered_layer_style [c: Configuration]{
	layered_style[c]
	non_zero_refs[c] 
        //non_refs[c]

}


