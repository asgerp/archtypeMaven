open shared_repo

one sig conf extends Configuration {} 	{ 
	elements = Repo + Client1 + Client2 + Client3
}

one sig Repo extends Shared_repo {} {
	references = Client1 + Client2 + Client3
}

one sig Client1 extends Client {} {
	references = Repo
}

one sig Client2 extends Client {} {
	references = Repo
}

one sig Client3 extends Client {} {
	references = Repo
}
// 2-way refs
assert ref{
	refs[conf] 
}
// at least 1 client per repo
assert gt1c{
	gt1Client[conf] 
}
// no c2c refs
assert c2c{
	noC2C[conf]
}
// no repo2repo refs
assert r2r{
	noR2R[conf]
}

check ref for 8
check gt1c for 8
check c2c for 8 
check r2r for 8
