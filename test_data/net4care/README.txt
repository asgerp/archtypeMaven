Net4Care Ecosystem
==================
This folder contains the source for the Net4Care ecosystem framework

Overview
-------
The module structure is as follows:

net4care/
  n4c_site/:		source for Net4Care website
  n4c_osgi/: 		source for Net4Care OSGi runtime/the framework
  n4c_example/: 	source for several demonstrators

Getting started
---------------
To build the source distribution, open a shell and execute:

$ mvn install

To do a clean, install, and integration test, do

$ mvn clean -P ci install

To generate a local copy of the web site, change to
  the n4c_site folder and do

$ mvn site

For further information, see the Net4Care web page:

    http://www.net4care.org

Contact
-------
dev@net4care.org
