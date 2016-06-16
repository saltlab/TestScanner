TestScanner
======

TestScanner is a tool for analyzing JavaScript tests. 

Given a JavaScript application test folder, it extracts the following metrics: Number of tests, asynchronous tests, test modules, assertions, distinct function calls, max and average distinct function calls per test case, event trigger calls, and object creations.

Given a JavaScript test coverage report, it extracts: Percentage of missed statements in missed functions, and coverage for regular functions, callback functions, asynchronous callbacks, event-dependent callbacks, and closures.

In addition, given a URL to a Github repo for a JavaScript application it extracts these metrics from the repository: Number of watches, stars, forks, commits, branches, releases, and contributors.
