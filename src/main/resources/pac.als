module pac
// General model
abstract sig Configuration { elements: set Element }
abstract sig Element { references: set Element }

// PAC style
abstract sig Presentation extends Element { }
abstract sig Abstraction extends Element { }
abstract sig Control extends Element { }

// presentation components can only "talk" to other presentation components
// in the same agent and to the agents control component
pred pac_presentation_style [c: Configuration] {
	all p: c.elements & Presentation | all r: p.references | r not in Abstraction
}

// abstraction components can only "talk" to other abstraction components
// in the same agent and to the agents control component
pred pac_abstraction_style [c: Configuration] {
	all a: c.elements & Abstraction | all r: a.references | r not in Presentation
}



