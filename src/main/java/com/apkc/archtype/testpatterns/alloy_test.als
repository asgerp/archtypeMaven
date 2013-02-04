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
one sig testMvc extends Configuration { } {
	elements = TestController + ViewTest + TestModel + TestController3
}
one sig TestController extends Controller { } {
	references = TestController + TestModel

}
one sig ViewTest extends View { } {
	references = TestController

}
one sig TestModel extends Model { } {
	references = ViewTest + TestController3

}
one sig TestController3 extends Controller { } {
	references = TestController + TestModel

}
assert testcontroller {
	mvc_controller_style[testMvc]
}
assert viewtest {
	mvc_view_style[testMvc]
}
assert testmodel {
	mvc_model_style[testMvc]
}
assert testcontroller3 {
	mvc_controller_style[testMvc]
}

check testcontroller for 8 but 1 Configuration
check viewtest for 8 but 1 Configuration
check testmodel for 8 but 1 Configuration
check testcontroller3 for 8 but 1 Configuration