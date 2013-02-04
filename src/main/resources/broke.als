module broker
// General model
abstract sig Configuration { elements: set Element }
abstract sig Element { references: set Element }

abstract sig Requestor extends Element {}
abstract sig Invoker extends Element {}
abstract sig Marshaller extends Element {}
abstract sig RequestHandler extends Element {}

// "talks" to client marshaller and client requesthandler
pred broker_requestor_style [c:Configuration]{

}

// talks to server marshaller and server requesthandler
pred broker_invoker_style [c:Configuration]{

}

// talks to either reqeuestor or invoker
pred broker_marshaller_style [c:Configuration]{

}

// talks to either invoker or requestor and other requesthandlers
pred broker_requesthandler_style [c:Configuration] {

}
