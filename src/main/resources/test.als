module examples/tutorial/Map


	abstract sig Object {}

	sig Key, Value extends Object {}

	sig Map { values: Key -> Value } 


assert unique {

	        all m:Map, k:Key, v, v':Value |

	                k -> v in m.values and k -> v' in m.values implies v = v'

	}

assert wrong {

	        all m:Map, k:Key, v, v':Value |

	                k -> v in m.values and k -> v' in m.values implies v != v'

	}

check unique for 2
