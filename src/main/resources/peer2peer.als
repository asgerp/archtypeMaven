module peer2peer

// General model
abstract sig Configuration { elements: set Element }
abstract sig Element { references: set Element }

abstract sig Peer extends Element { }

//all peers must be connected to at least one peer
pred non_zero_refs [c: Configuration ] {
	all p: c.elements & Peer | #p.references > 0
}
	
