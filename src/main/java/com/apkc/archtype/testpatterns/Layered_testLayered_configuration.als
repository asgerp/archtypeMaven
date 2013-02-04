open layered
one sig testLayered extends Configuration { } {
	elements = Layer1 + Layer2 + Layer3
}
one sig Layer1 extends Layer { } {
	references = Layer2
	meta = 1
}
one sig Layer2 extends Layer { } {
	references = Layer1
	meta = 2
}
one sig Layer3 extends Layer { } {
	references = Layer2
	meta = 3
}
assert layer1 {
	layered_layer_style[testLayered]
}
assert layer2 {
	layered_layer_style[testLayered]
}
assert layer3 {
	layered_layer_style[testLayered]
}
check layer1 for 8
check layer2 for 8
check layer3 for 8
