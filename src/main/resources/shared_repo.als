module shared_repo

// General model
abstract sig Configuration { elements: set Element }
abstract sig Element { }

// Shared repo style
sig Shared_repo extends Element { references: set Element }
sig Client extends Element { references: one Element }

// enforce client <-> repo referencing
pred refs [c: Configuration] { 
		all cl: c.elements & Client, s: c.elements & Shared_repo | 
			s in cl.references <=> cl in s.references 
}
// at least one client per repo
pred gt1Client [c: Configuration] {
	all s: c.elements & Shared_repo | #s.references > 0
}

// no client <-> client referencing * throws warning
pred noC2C [c: Configuration] { 
	all cl:c.elements & Client | #(cl.references & Client) = 0 
} 

// no repo <-> repo referencing * throws warning
pred noR2R [c: Configuration] { 
	all s:c.elements & Shared_repo | #(s.references & Shared_repo) = 0
}

// main predicates
pred shared_repo_shared_repo_style [c:Configuration] {
	refs[c] noR2R[c] 
}

pred shared_repo_client_style [c: Configuration] {
	gt1Client[c] noC2C[c]
}
