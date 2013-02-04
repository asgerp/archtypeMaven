open mvc
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
check testcontroller for 8
check viewtest for 8
check testmodel for 8
check testcontroller3 for 8
