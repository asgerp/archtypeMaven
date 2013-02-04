# Archtype


Masters project on how to annotate source code to make sure that architectural patterns/styles, used to design a system, is upheld in the actual implementation.

## Run it

compile the project and then call:

	javac -processor com.apkc.archtype.processors.ComponentProcessor -cp ~/path/to/archtype-1.0.jar
	path/to/TestController.java path/to/TestView.java path/to/TestModel.java


## Annotations

An annotation looks like this

	@Component(
		patterns = {@Pattern(name="testMvc",kind = "MVC", role="Controller"), @Pattern(...)}
	)

Certain patterns may require meta data, such as layers and presenter-abstraction-control. This can be done by post fixing the role name with the meta data in {}. Like this:

	@Component(
		patterns = {@Pattern(name="testLayer",kind = "Layered", role="layer{1}")}
	)

where patterns is a list of patterns the component participates in. A pattern consists of a name of the pattern, the kind of the pattern, and the role the components plays in the pattern.

### Supported patterns

1. Layers ( **Components**: Layer{layer#}, **Pattern name**: Layered)
1. Indirection Layers ( **Components**: Inlayer{inlayer#}, **Pattern name**: Indirectionlayer)
1. Model-View-Control ( **Components**: Model, View, Controller, **Pattern name**: MVC)
1. Client-Server ( **Components**: Client, Server, **Pattern name**: clientserv)
1. Peer-to-Peer ( **Components**: Peer, **Pattern name**: peer2peer)
1. Publish-Subscribe ( **Components**: Producer, SubscriptionManager, Consumer, **Pattern name**: publishsubscribe)
1. Shared Repository
1. Active Repository
1. Blackboard


## Requirements

all extern jars should be placed in lib/ folder.

* [alloy](http://alloy.mit.edu/alloy/download.html)
* [Apache commons lang 3](http://commons.apache.org/lang/download_lang.cgi)