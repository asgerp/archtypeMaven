module adapter

abstract sig Configuration { elements: set Element }
abstract sig Element { references: set Element }

// adapter style
abstract sig Client extends Element { }
abstract sig Target extends Element { }
abstract sig Adapter extends Element { }
abstract sig Adaptee extends Element { }


pred client_adapter_style [c: Configuration] {
	all client: c.elements & Client | all refs: client.references | refs not in Adapter & ref not in Adaptee
}

pred target_adapter_style [c: Configuration] {
	all target: c.elements & Target | all refs: target.references | refs not in Adaptee & refs not in Client
}

pred adapter_adapter_style [c: Configuration] {
	all adapter: c.elements & Adapter | all refs: adapter.references | refs not in Client
}

pred adaptee_adapter_style [c: Configuration] {
	all adaptee: c.elements & Adaptee | all refs: adaptee.references | refs not in Client & refs not in Target
}