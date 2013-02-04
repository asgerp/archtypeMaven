open blackboard

one sig conf extends Configuration {} 	{ 
	elements = BB + BB2 + Ctrl + Ctrl2 + Client1 + Client2 + Client3 + Client4
}

one sig BB extends Blackboard {} {
	references = Ctrl + Client1 + Client2 + Client3
}

one sig BB2 extends Blackboard {} {
	references = Ctrl2 + Client4
}

one sig Ctrl extends Controller {} {
	references = BB
}

one sig Ctrl2 extends Controller {} {
	references = BB2
}

one sig Client1 extends Client {} {
	references = BB
}

one sig Client2 extends Client {} {
	references = BB
}

one sig Client3 extends Client {} {
	references = BB
}

one sig Client4 extends Client {} {
	references = BB2
}
// 2-way refs
assert refC2BB{
	refsCl[conf] 
}
assert refCtrl2BB{
	refsCtrl[conf] 
}
// 1 ctrl to 1 bb only
assert oneCtrlOneBB{
	oneCtrlPerBB[conf] oneBBPerCtrl[conf]
}
// at least 1 client per repo
assert gt1c{
	gt1Client[conf] 
}
// no client <-> controller
assert noClient2Ctrl{
	noC2Ctrl[conf]
}
// no c2c refs
assert c2c{
	noC2C[conf]
}
// no bb2bb refs
assert b2b{
	noB2B[conf]
}

check refC2BB for 8
check refCtrl2BB for 8
check oneCtrlOneBB for 8
check gt1c for 8
check noClient2Ctrl for 8
check c2c for 8 
check b2b for 8
