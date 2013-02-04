module indirectionlayer
open util/integer
open util/natural

abstract sig Configuration { elements: set Element }
abstract sig Element { references: set Element}

sig Inlayer extends Element {  meta: one Int  }

pred layer_bigger[ i:Int, j:Int] {
	int[i].add[1] = int[j] 
}
pred layer_equal[ i:Int, j:Int] {
	int[i] = int[j] 
}

pred non_zero_refs [c: Configuration ] {
	all m: c.elements & Inlayer | #m.references > 0
}

pred indirectionlayer_style [c: Configuration] {
	all m: c.elements & Inlayer| all r: m.references | layer_bigger[m.meta,r.meta] or layer_equal[m.meta,r.meta]
}
	
pred indirectionlayer_layer_style [c: Configuration]{
	indirectionlayer_style[c] 
	non_zero_refs[c]
}

run indirectionlayer_layer_style for exactly 1 Configuration, 5 Inlayer, 3 Natural
