module clientserv
abstract sig Configuration { elements: set Element }
abstract sig Element { references: set Element }

// Client Server Style
abstract sig Client extends Element { }
abstract sig Server extends Element { }

// a client contacts a single server (there can be more instances)
pred clientserv_client_style [c : Configuration] {
	all cl: c.elements & Client | one r: cl.references | r in Server
}

// one server (there can be more instances) is in contact with several clients
pred clientserv_server_style [c: Configuration] {
	all s: c.elements & Server | all r: s.references | r in Client
}

pred clientserv_style [c: Configuration] {
	clientserv_client_style[c] clientserv_server_style[c]
}
