TestScanner
======

TestScanner is a tool for analyzing JavaScript tests. 

Given a JavaScript application test folder, it extracts the following metrics: Number of tests, asynchronous tests, assertions, distinct function calls, max and average distinct function calls per test case, event trigger calls, and object creations. => `TestCodePropertyAnalyzer.java`

Given a JavaScript test coverage report, it extracts: Uncovered statement in uncovered function ratio, and coverage for regular functions, callback functions, asynchronous callbacks, event-dependent callbacks, and closures. => `ProductionCodeCoverageAnalyzer.java`

In addition, given a URL to a Github repo for a JavaScript application it extracts these metrics from the repository: Number of watches, stars, forks, commits, branches, releases, and contributors. => `GetRepoStat.java`


Subjects and analysis results
======

You can find more information on the study that we carried [here](http://www.ece.ubc.ca/~aminmf/js_test_study.html).
