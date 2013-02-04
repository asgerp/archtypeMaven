module mvc
// General model
abstract sig Configuration { elements: set Element }
abstract sig Element { references: set Element }

// MVC Style
abstract sig Model extends Element { }
abstract sig View extends Element { }
abstract sig Controller extends Element { }

pred mvc_model_style [c: Configuration] {
	all m: c.elements & Model | all r: m.references | r not in View
}
pred mvc_view_style [c: Configuration] {
	all view: c.elements & View | all ref: view.references | ref not in Model
}

pred mvc_controller_style [c: Configuration] {
    all controller: c.elements & Controller | all ref: controller.references | ref in Model or ref in View or ref in Controller
}

pred mvc_style [c: Configuration]{
	 mvc_model_style[c] mvc_view_style[c]
}

