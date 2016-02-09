# Mocky
Javascript mock library, inspired by Perl's Test::MockModule.

Files
====

mocky.js:
	Just load this before your unit tests

Usage
=====

Normal (sync) mode
----

	new Mocky(o, function(mocky) {
		mocky.mock({foo: bar});
		// ... your tests, where o.foo === bar ...
	});
	// ... your other code, where o.foo !== bar ...

Normally you would build a new Mocky object passing a function which
contains the entire mocked context. If you do this you don't need
to worry about unmocking afterwards.

Async mode
----

	var mocky = new Mocky(o);
	mocky.mock({foo: bar});
	// ... your tests, where o.foo === bar ...
	mocky.unmock();
	// ... your other code, where o.foo !== bar ...

If you have some async code to test, you can build the mocky object
without a function and call unmock() yourself when you're done. You
should be really sure that your code actually will run though!

Using Mocky with classes
=====

As classes in Javascript are just a constructor and a prototype,
you can just use Mocky with the prototype object:

	function MyClass() {
		// Your constructor body...
	}
	MyClass.prototype = {
		// Your prototype...
	};

	new Mocky(MyClass.prototype, function(mocky) {
		mocky.mock({foo: bar});
		// ... your tests, where (MyClass object).foo === bar ...
	});
	// ... your other code, where (MyClass object).foo !== bar ...

Getting to the pre-mocked version of the property
=====

Pre-mocked versions are available in the "mocked" property of the
mocky object, eg:

	// Mocking o, where o.x === 1
	mocky.mock({x: 2});
	// mocky.mocked.x === 1

Use cases for mocking
=====

Mocking is something you do when you want to constrain your tests
to the current unit (the current file or similar) without depending
on the whole stack of functionality. This might be useful when
you're using a framework, but this also applies to the code's
expectations about the web browsing environment. For example, if
the code really expects an input#foo, you might choose to do:

	new Mocky(document, function(mocky) {
		mocky.mock({
			querySelector: function(selector) {
				if(selector == "#foo") {
					return {value: "mytestvalue"}; // new behaviour: return a fake DOM object
				} else {
					return mocky.mocked.querySelector.call(this, selector); // original behaviour
				}
			}
		});
		// Your tests which you know will trigger document.querySelector("#foo")...
	});
