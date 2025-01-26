# ![Wetator Logo](https://www.wetator.org/images/wetator.png)

Version 4.9.0 / January 25, 2025

:heart: [Sponsor](https://github.com/sponsors/rbri)

### Homepage

[wetator.org](https://www.wetator.org/)

### News

**[Developer Blog](https://htmlunit.github.io/htmlunit-blog/)**

[HtmlUnit@mastodon](https://fosstodon.org/@HtmlUnit) | [HtmlUnit@bsky](https://bsky.app/profile/htmlunit.bsky.social) | [HtmlUnit@Twitter](https://twitter.com/HtmlUnit)


## Sponsoring

Constantly updating and maintaining the Wetator/HtmlUnit code base already takes a lot of time.
For doing this I need your [sponsoring](https://github.com/sponsors/rbri).

## Get it!

* [Download from Wetator.org](https://wetator.org/download/)
* [Download from GitHub](https://github.com/Wetator/wetator/releases)
* [Release History](https://wetator.org/download/release-notes/)

## Overview
 Wetator is a tool for automated Graphical User Interface (GUI) tests of web applications.

It provides you with a simple command language for writing your tests, that
 * maps the typical user actions (open-url, click-on, select, set...) and
 *includes commands to check the page content (assert-content, assert-title...).

This human readable language enables you to specify test cases that are easy to write and easy to read.

Wetator tests are executed using HtmlUnit, a Java based framework which is able to simulate the most popular web browsers (Firefox and Chrome/Edge).
The test result is summarized in a well-arranged report containing detailed information about the test run and snapshots for each step.

## Benefits

 * There is no need for technical details in the tests like names or IDs of the GUI components. Wetator is able to find the corresponding GUI components by their labels, alt texts, position etc. Thus your test cases are not directly destroyed by each GUI change; e.g. it is not important if the button "Google Search" is in the upper left or the lower right corner, whether it has the id g:search or j:763223, or if it is changed into a clickable image with the alt attribute "Google Search"
 * Wetator tests are predicted to be used in test driven development resp. test first approach. They can even be created as a part of requirements specification.
 * Wetator can handle the following technologies: HTML, CSS, JavaScript (incl. highly sophisticated AJAX support) as well as MS Word, MS Excel, PDF and ZIP for assertion issues (e.g. for testing exports)
 * The Wetator test format actually is XML (just hover the example test case at the top of the page). So you can use any editor you want to edit Wetator test cases or you may use the Wetator Test Editor for convenient, Excel-like editing in your Eclipse IDE. Since they are XML, Wetator tests can easily be managed by version control systems and of course validated against their XML schemas.
 * Wetator supports modular test case design and makes it possible to resuse identical parts of your test suite (including parameter support).
 * Currently Wetator can emulate the following browsers for testing: Firefox, Firefox ESR, Chrome and EDGE. So you can run your tests in different browser environments without effort.
 * Wetator and its language are extensible; e.g. you could add special commands for special web site features or introduce new controls you implemented, define your test cases in XML or Excel, or just write your own scripter for another test case format; for maximum extension it is possible to call Java classes from a Wetator test, for instance to set up some test data in your database before a test can run.

## Target Audience
 * Wetator tests are written by developers and testers who want to have automated GUI tests for their web applications that are easy to maintain and easy to read.
 * The Wetator language is understandable for project leads and customers – the people with whom you have to discuss if your tests are testing the requirements correctly and sufficiently.
 * Manual testers get to know what they do not have to focus on (since it is already tested automatedly), for example they could focus on testing design, usability, and of course things that cannot or only hardly be tested automatedly.


## How To
Please have a look at the web site for further [Documentation](https://www.wetator.org/documentation/) and a [Getting Started](https://www.wetator.org/documentation/getting-started/)

## Contributing
Pull Requests and all other Community Contributions are essential for open source software.
Every contribution - from bug reports to feature requests, typos to full new features - are greatly appreciated.

Please try to keep your pull requests small (don't bundle unrelated changes) and try to include test cases.

## License

This project is licensed under the Apache 2.0 License