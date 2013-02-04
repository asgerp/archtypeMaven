module publishsubscribe

// General model
abstract sig Configuration { elements: set Element }
abstract sig Element { references: set Element }

abstract sig Producer extends Element { }
abstract sig SubscriptionManager extends Element { }
abstract sig Consumer extends Element { }
// only communicate with other producers and managers
pred publishsubscribe_producer_style [c:Configuration] {
	all p: c.elements & Producer | all r: p.references | r not in Consumer
}
// only communicate with other consumers and managers
pred publishsubscribe_consumer_style [c:Configuration] {
	all co: c.elements & Consumer | all r: co.references | r not in Producer
}

pred publishsubscribe_manager_style [c:Configuration] {
	one m: c.elements & SubscriptionManager
}
