open mvc

// MVC instance counterexample
one sig EHR extends Configuration { } {
	elements = EHRModel + EHRView
}
one sig EHRModel extends Model { } {
	references = EHRView
}
one sig EHRView extends View { } {
	references = EHRModel
}

assert conforms { mvc_style[EHR] } check conforms
