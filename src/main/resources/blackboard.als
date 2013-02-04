module blackboard

// General model
abstract sig Configuration { elements: set Element }
abstract sig Element { }

// Blackboard style
sig Blackboard extends Element { references: set Element }
sig Client extends Element { references: one Element }
sig Controller extends Element { references: one Element }

// enforce client <-> blackboard referencing
pred refsCl [c: Configuration] { 
		all cl: c.elements & Client, s: c.elements & Blackboard | 
			s in cl.references <=> cl in s.references 
}

// enforce controller <-> blackboard referencing
pred refsCtrl [c: Configuration] { 
		all cl: c.elements & Controller, s: c.elements & Blackboard | 
			s in cl.references <=> cl in s.references 
}

// at least one client per blackboard
pred gt1Client [c: Configuration] {
	all s: c.elements & Blackboard | #s.references > 0
}

// blackboard has one controller only
pred oneCtrlPerBB [c: Configuration] {
	all bbs: c.elements & Blackboard | #(bbs.references & Controller) = 1
}

// controller has one blackboard only
pred oneBBPerCtrl [c: Configuration] {
	all ctrl: c.elements & Controller | #(ctrl.references & Blackboard) = 1
}

// no client <-> client referencing * throws warning
pred noC2C [c: Configuration] { all cl:c.elements & Client | cl.references not in Client } 

// no client <-> controller referencing
pred noC2Ctrl [c: Configuration] {
	all cl:c.elements & Client | cl.references not in Controller
	all ctrl:c.elements & Controller | ctrl.references not in Client
}

// no blackboard <-> blackboard referencing * throws warning
pred noB2B [c: Configuration] { 
	//all bbs:c.elements & Blackboard | bbs.references not in Blackboard
	all bbs:c.elements & Blackboard | #(bbs.references & Blackboard) = 0
}

// main predicates
pred blackboard_blackboard_style [c:Configuration] {
	refsCl[c] noB2B[c] oneCtrlPerBB[c] oneBBPerCtrl[c]
}

pred blackboard_client_style [c: Configuration] {
	gt1Client[c] noC2C[c] noC2Ctrl[c]
}

pred main [c: Configuration] {
 blackboard_blackboard_style[c] blackboard_client_style[c] 	
}

run main for 1 Configuration, 1 Blackboard, 1 Controller, 4 Client
